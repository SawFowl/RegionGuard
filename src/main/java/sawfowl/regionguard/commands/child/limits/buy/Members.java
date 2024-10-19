package sawfowl.regionguard.commands.child.limits.buy;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.economy.Currency;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.commands.raw.arguments.RawArguments;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.commandpack.api.commands.raw.arguments.RawBasicArgumentData;
import sawfowl.commandpack.api.commands.raw.arguments.RawOptional;
import sawfowl.commandpack.utils.CommandsUtil;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Transaction.Limit;

public class Members extends AbstractPlayerCommand {

	public Members(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.BUY_MEMBERS);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		long toBuy = args.getLong(0).get();
		if(toBuy <= 0) exception(getExceptions(locale).getEnteredZero());
		if(plugin.getAPI().getLimitMaxMembers(src) < toBuy + plugin.getAPI().getLimitMembers(src)) {
			long max = plugin.getAPI().getLimitMaxMembers(src) - plugin.getAPI().getLimitMembers(src);
			if(max < 0) max = 0;
			exception(getExceptions(locale).getMaxValue(max));
		}
		double needMoney = plugin.getAPI().getBuyMembersPrice(src) * toBuy;
		Currency currency = plugin.getAPI().getCurrency(src);
		if(!plugin.getEconomy().checkPlayerBalance(src.uniqueId(), currency, BigDecimal.valueOf(needMoney))) exception(getExceptions(locale).getNotEnoughMoney());
		if(!plugin.getEconomy().removeFromPlayerBalance(src, currency, BigDecimal.valueOf(needMoney))) exception(getExceptions(locale).getEconomyException());
		if(!plugin.getAPI().getPlayerData(src).isPresent()) plugin.getAPI().setPlayerData(src, PlayerData.zero());
		plugin.getAPI().setLimitMembers(src, toBuy);
		src.sendMessage(getLimit(locale).getSuccess(toBuy, plugin.getAPI().getLimitMembers(src)));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getLimit(locale).getDescription();
	}

	@Override
	public String permission() {
		return Permissions.BUY_MEMBERS;
	}

	@Override
	public String command() {
		return "members";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg limits buy members &7<Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg limits buy members ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return Arrays.asList(
			RawArguments.createLongArgument(CommandsUtil.getEmptyList(), new RawBasicArgumentData<>(null, "Volume", 0, null, null), RawOptional.notOptional(), locale -> getCommand(locale).getExceptions().getVolumeNotPresent())
		);
	}

	private Limit getLimit(Locale locale) {
		return getCommand(locale).getLimits().getBuy().getMembersLimit();
	}

}
