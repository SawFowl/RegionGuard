package sawfowl.regionguard.commands;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
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
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class SellMembersCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	private List<CommandCompletion> empty = new ArrayList<>();
	public SellMembersCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		if(args.isEmpty()) usage();
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		if(args.size() == 0) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SELLMEMBERS_EXCEPTION_NOT_PRESENT));
		if(!NumberUtils.isParsable(args.get(0))) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SELLMEMBERS_EXCEPTION_WRONG_ARGUMENT));
		long toSell = Long.valueOf(args.get(0));
		if(toSell <= 0) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SELLMEMBERS_EXCEPTION_ENTERED_ZERO));
		if(!plugin.getAPI().getPlayerData(player).isPresent()) plugin.getAPI().setPlayerData(player, PlayerData.of(PlayerLimits.zero(), ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player))));
		if(plugin.getAPI().getPlayerData(player).get().getLimits().getMembersPerRegion() < toSell) throw new CommandException(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.MAX), Arrays.asList(plugin.getAPI().getPlayerData(player).get().getLimits().getMembersPerRegion())), LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_TO_MUCH_VOLUME));
		if(!plugin.getEconomy().addToPlayerBalance(player, plugin.getEconomy().checkCurrency(plugin.getAPI().getCurrency(player)), BigDecimal.valueOf(toSell * plugin.getAPI().getSellSubdivisionPrice(player)))) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SELLMEMBERS_EXCEPTION_ECONOMY_EXCEPTION));
		plugin.getAPI().setLimitMembers(player, plugin.getAPI().getPlayerData(player).get().getLimits().getMembersPerRegion() - toSell);
		player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE, ReplaceUtil.Keys.VOLUME), Arrays.asList(toSell, plugin.getAPI().getLimitMembers(player))), LocalesPaths.COMMAND_SELLMEMBERS_SUCCESS));
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.SELL_BLOCKS);
	}

	@Override
	public CommandException usage() throws CommandException {
		throw new CommandException(text("Usage: /rg sellimit members [Volume]"));
	}

}
