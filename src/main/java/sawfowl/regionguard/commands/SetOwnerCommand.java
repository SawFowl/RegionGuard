package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class SetOwnerCommand implements Command.Raw {

	private final RegionGuard plugin;
	private final List<CommandCompletion> empty = new ArrayList<>();
	public SetOwnerCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition()).getPrimaryParent();
		if(region.isGlobal()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND));
		if(region.isAdmin() && player.hasPermission(Permissions.STAFF_TRUST)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SETOWNER_EXCEPTION_ADMIN));
		String plainArgs = arguments.input();
		while(plainArgs.contains("  ")) plainArgs = plainArgs.replace("  ", " ");
		List<String> args = Stream.of(plainArgs.split(" ")).filter(string -> (!string.equals(""))).collect(Collectors.toList());
		args.remove(0);
		if(args.isEmpty() || !Sponge.server().player(args.get(0)).isPresent()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_PRESENT));
		ServerPlayer newOwner = Sponge.server().player(args.get(0)).get();
		if(player.uniqueId().equals(newOwner.uniqueId()) && region.isCurrentTrustType(player, TrustTypes.OWNER)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SETOWNER_EXCEPTION_OWNER_TARGET_SELF));
		if(player.hasPermission(Permissions.STAFF_TRUST)) {
			if(region.getOwnerUUID().equals(newOwner.uniqueId())) throw new CommandException(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.PLAYER), Arrays.asList(newOwner.name())), LocalesPaths.COMMAND_SETOWNER_EXCEPTION_STAFF_TARGET_OWNER));
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SETOWNER_CONFIRMATION_REQUEST).clickEvent(SpongeComponents.executeCallback(messageCause -> {
				setOwner(player, newOwner, region, true);
			})));
			return CommandResult.success();
		}
		if(!region.isCurrentTrustType(player, TrustTypes.OWNER)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SETOWNER_EXCEPTION_PLAYER_IS_NOT_OWNER));
		player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SETOWNER_CONFIRMATION_REQUEST).clickEvent(SpongeComponents.executeCallback(messageCause -> {
			setOwner(player, newOwner, region, false);
		})));
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments) throws CommandException {
		String plainArgs = arguments.input();
		if(!plainArgs.contains("setowner ")) return empty;
		List<String> args = Stream.of(plainArgs.split(" ")).filter(string -> (!string.equals(""))).collect(Collectors.toList());
		args.remove(0);
		List<CommandCompletion> toSend = Sponge.server().onlinePlayers().stream().map(ServerPlayer::name).filter(name -> (!((ServerPlayer) cause.root()).name().equals(name))).map(CommandCompletion::of).collect(Collectors.toList());
		if(args.isEmpty()) return toSend;
		if(args.size() == 1 && !Sponge.server().player(args.get(0)).isPresent()) return toSend.stream().filter(player -> (player.completion().startsWith(args.get(0)))).collect(Collectors.toList());
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.TRUST);
	}

	@Override
	public Optional<Component> shortDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Change the region owner."));
	}

	@Override
	public Optional<Component> extendedDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Change the region owner."));
	}

	@Override
	public Component usage(CommandCause cause) {
		return Component.text("/rg setowner");
	}

	private void setOwner(ServerPlayer player, ServerPlayer newOwner, Region region, boolean staff) {
		if(staff) {
			region.setTrustType(region.getOwnerUUID(), TrustTypes.MANAGER);
			for(Region child: region.getAllChilds()) child.setTrustType(region.getOwnerUUID(), TrustTypes.MANAGER);
			if(!region.getOwnerUUID().equals(new UUID(0, 0)) && region.getOwner().isPresent()) region.getOwner().get().sendMessage(plugin.getLocales().getTextWithReplaced(region.getOwner().get().locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.WORLD, ReplaceUtil.Keys.MIN, ReplaceUtil.Keys.MAX), Arrays.asList(region.getServerWorldKey().toString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString())), LocalesPaths.COMMAND_SETOWNER_SUCCESS_FROM_STAFF));
		} else {
			for(Region child: region.getAllChilds()) child.setTrustType(player, TrustTypes.MANAGER);
			region.setTrustType(player, TrustTypes.MANAGER);
		}
		region.setOwner(newOwner);
		for(Region child: region.getAllChilds()) child.setOwner(newOwner);
		player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.PLAYER, ReplaceUtil.Keys.WORLD, ReplaceUtil.Keys.MIN, ReplaceUtil.Keys.MAX), Arrays.asList(newOwner.name(), region.getServerWorldKey().toString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString())), LocalesPaths.COMMAND_SETOWNER_SUCCESS_PLAYER));
		newOwner.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.PLAYER, ReplaceUtil.Keys.WORLD, ReplaceUtil.Keys.MIN, ReplaceUtil.Keys.MAX), Arrays.asList(player.name(), region.getServerWorldKey().toString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString())), LocalesPaths.COMMAND_SETOWNER_SUCCESS_TARGET));
		plugin.getAPI().saveRegion(region.getPrimaryParent());
	}

}
