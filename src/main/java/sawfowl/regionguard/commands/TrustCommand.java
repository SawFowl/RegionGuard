package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class TrustCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	private List<CommandCompletion> validTypes = Stream.of(TrustTypes.values()).filter(type -> (type != TrustTypes.WITHOUT_TRUST)).map(TrustTypes::toString).map(CommandCompletion::of).collect(Collectors.toList());
	private final List<CommandCompletion> empty = new ArrayList<>();
	private final String validValues = TrustTypes.CONTAINER.toString() + ", " +
			TrustTypes.SLEEP.toString() + ", " +
			TrustTypes.HUNTER.toString() + ", " +
			TrustTypes.BUILDER.toString() + ", " +
			TrustTypes.USER.toString() + ", " +
			TrustTypes.MANAGER.toString() + ".";
	public TrustCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		if(args.isEmpty()) usage();
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		if(region.isGlobal()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND));
		if(region.isAdmin()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_TRUST_EXCEPTION_ADMINCLAIM));
		if((!region.isCurrentTrustType(player, TrustTypes.OWNER) && !region.isCurrentTrustType(player, TrustTypes.MANAGER)) && !player.hasPermission(Permissions.STAFF_TRUST)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_TRUST_EXCEPTION_NEED_TRUST_TYPE));
		String plainArgs = arguments.input();
		while(plainArgs.contains("  ")) plainArgs = plainArgs.replace("  ", " ");
		if(args.isEmpty() || !Sponge.server().player(args.get(0)).isPresent()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_PRESENT));
		ServerPlayer trustedPlayer = Sponge.server().player(args.get(0)).get();
		if(!player.hasPermission(Permissions.UNLIMIT_MEMBERS) && plugin.getAPI().getLimitMembers(region.getOwnerUUID()) <= region.getTotalMembers() - 1 && !region.getMemberData(trustedPlayer).isPresent()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_TRUST_EXCEPTION_LIMIT_REACHED));
		args.remove(0);
		if(args.isEmpty() || TrustTypes.checkType(args.get(0)) == TrustTypes.WITHOUT_TRUST) throw new CommandException(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.TRUST_TYPES), Arrays.asList(validValues)), LocalesPaths.COMMAND_TRUST_EXCEPTION_TRUST_TYPE_NOT_PRESENT));
		TrustTypes trustLevel = TrustTypes.checkType(args.get(0));
		if((player.uniqueId().equals(trustedPlayer.uniqueId()) && !player.hasPermission(Permissions.STAFF_TRUST)) || (player.uniqueId().equals(trustedPlayer.uniqueId()) && region.isCurrentTrustType(player, TrustTypes.OWNER))) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_TRUST_EXCEPTION_TARGET_SELF));
		if(!region.isCurrentTrustType(player, TrustTypes.OWNER) && trustLevel == TrustTypes.MANAGER) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_TRUST_EXCEPTION_PLAYER_IS_NOT_OWNER));
		region.setTrustType(trustedPlayer, trustLevel);
		player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.TRUST_TYPE, ReplaceUtil.Keys.PLAYER), Arrays.asList(trustLevel.toString(), trustedPlayer.name())), LocalesPaths.COMMAND_TRUST_SUCCESS_PLAYER));
		trustedPlayer.sendMessage(plugin.getLocales().getTextWithReplaced(trustedPlayer.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.TRUST_TYPE, ReplaceUtil.Keys.PLAYER, ReplaceUtil.Keys.WORLD, ReplaceUtil.Keys.MIN, ReplaceUtil.Keys.MAX), Arrays.asList(trustLevel.toString(), player.name(), region.getServerWorldKey().toString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString())), LocalesPaths.COMMAND_TRUST_SUCCESS_TARGET));
		plugin.getAPI().saveRegion(region.getPrimaryParent());
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		String plainArgs = arguments.input();
		List<CommandCompletion> toSendPlayersList = Sponge.server().onlinePlayers().stream().map(ServerPlayer::name).filter(name -> (!((ServerPlayer) cause.root()).name().equals(name))).map(CommandCompletion::of).collect(Collectors.toList());
		if(args.isEmpty()) return toSendPlayersList;
		if(args.size() == 1 && !Sponge.server().player(args.get(0)).isPresent()) return toSendPlayersList.stream().filter(player -> (player.completion().startsWith(args.get(0)))).collect(Collectors.toList());
		if(!Sponge.server().player(args.get(0)).isPresent() || !plainArgs.contains(args.get(0) + " ")) return empty;
		if(args.size() == 1) return validTypes;
		if(args.size() == 2 && TrustTypes.checkType(args.get(1)) == TrustTypes.WITHOUT_TRUST) return validTypes.stream().filter(type ->(type.completion().startsWith(args.get(1)))).collect(Collectors.toList());
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.TRUST);
	}

	@Override
	public CommandException usage() throws CommandException {
		throw new CommandException(text("Usage: /rg trust [Player] [TrustType]"));
	}

}
