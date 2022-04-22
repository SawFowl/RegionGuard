package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class UntrustCommand implements Command.Raw {

	private final RegionGuard plugin;
	private final List<CommandCompletion> empty = new ArrayList<>();
	public UntrustCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		if(region.isGlobal()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND));
		String plainArgs = arguments.input();
		while(plainArgs.contains("  ")) plainArgs = plainArgs.replace("  ", " ");
		List<String> args = Stream.of(plainArgs.split(" ")).filter(string -> (!string.equals(""))).collect(Collectors.toList());
		args.remove(0);
		if(!args.isEmpty() && !Sponge.server().gameProfileManager().cache().findByName(args.get(0)).isPresent()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_PRESENT));
		GameProfile profile = Sponge.server().gameProfileManager().cache().findByName(args.get(0)).get();
		if(player.hasPermission(Permissions.STAFF_TRUST)) {
			if(!region.isCurrentTrustType(profile.uniqueId(), TrustTypes.OWNER)) return untrust(region, player, profile);
			throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_UNTRUST_EXCEPTION_PLAYER_IS_OWNER));
		}
		if(!region.getOwnerUUID().equals(player.uniqueId()) && !region.isCurrentTrustType(player, TrustTypes.MANAGER)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_UNTRUST_EXCEPTION_NEED_TRUST_TYPE));
		if(player.uniqueId().equals(profile.uniqueId())) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_UNTRUST_EXCEPTION_TARGET_SELF));
		if(region.isCurrentTrustType(player, TrustTypes.MANAGER) && (region.isCurrentTrustType(profile.uniqueId(), TrustTypes.OWNER) || region.isCurrentTrustType(profile.uniqueId(), TrustTypes.MANAGER))) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_UNTRUST_EXCEPTION_TARGET_MANAGER));
		return untrust(region, player, profile);
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
		return Optional.ofNullable(Component.text("Excluding a player from a region."));
	}

	@Override
	public Optional<Component> extendedDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Excluding a player from a region."));
	}

	@Override
	public Component usage(CommandCause cause) {
		return Component.text("/rg untrust");
	}

	private CommandResult untrust(Region region, ServerPlayer player, GameProfile untrustedPlayer) {
		region.untrust(untrustedPlayer.uniqueId());
		plugin.getAPI().saveRegion(region);
		player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.PLAYER), Arrays.asList(untrustedPlayer.name())), LocalesPaths.COMMAND_UNTRUST_SUCCESS_PLAYER));
		if(Sponge.server().player(untrustedPlayer.uuid()).isPresent()) Sponge.server().player(untrustedPlayer.uuid()).get().sendMessage(plugin.getLocales().getTextWithReplaced(Sponge.server().player(untrustedPlayer.uuid()).get().locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.PLAYER, ReplaceUtil.Keys.WORLD, ReplaceUtil.Keys.MIN, ReplaceUtil.Keys.MAX), Arrays.asList(player.name(), region.getServerWorldKey().toString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString())), LocalesPaths.COMMAND_UNTRUST_SUCCESS_TARGET));
		return CommandResult.success();
	}

}
