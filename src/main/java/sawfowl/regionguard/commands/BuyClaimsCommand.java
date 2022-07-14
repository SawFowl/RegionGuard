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
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class BuyClaimsCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	private List<CommandCompletion> empty = new ArrayList<>();
	public BuyClaimsCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		if(args.size() == 0) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_NOT_PRESENT));
		if(!NumberUtils.isParsable(args.get(0))) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_WRONG_ARGUMENT));
		long toBuy = Long.valueOf(args.get(0));
		if(toBuy <= 0) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_ENTERED_ZERO));
		if(plugin.getAPI().getLimitMaxClaims(player) < toBuy + plugin.getAPI().getLimitClaims(player)) {
			long max = plugin.getAPI().getLimitMaxClaims(player) - plugin.getAPI().getLimitClaims(player);
			if(max < 0) max = 0;
			throw new CommandException(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.MAX), Arrays.asList(max)), LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_TO_MUCH_VOLUME));
		}
		double needMoney = plugin.getAPI().getBuyClaimPrice(player) * toBuy;
		Currency currency = plugin.getEconomy().checkCurrency(plugin.getAPI().getCurrency(player));
		if(!plugin.getEconomy().checkPlayerBalance(player.uniqueId(), currency, BigDecimal.valueOf(needMoney))) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_NOT_ENOUGH_MONEY));
		if(!plugin.getEconomy().removeFromPlayerBalance(player, currency, BigDecimal.valueOf(needMoney))) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_ECONOMY_EXCEPTION));
		if(!plugin.getAPI().getPlayerData(player).isPresent()) plugin.getAPI().setPlayerData(player, new PlayerData());
		if(plugin.getAPI().getPlayerData(player).get().getLimits() == null) plugin.getAPI().getPlayerData(player).get().setLimits(new PlayerLimits());
		plugin.getAPI().setLimitClaims(player, plugin.getAPI().getLimitClaims(player) + toBuy);
		player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE, ReplaceUtil.Keys.VOLUME), Arrays.asList(toBuy, plugin.getAPI().getLimitClaims(player))), LocalesPaths.COMMAND_BUYCLAIMS_SUCCESS));
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.BUY_CLAIMS);
	}

}
