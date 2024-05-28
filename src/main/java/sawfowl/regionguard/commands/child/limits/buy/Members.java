package sawfowl.regionguard.commands.child.limits.buy;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.Placeholders;

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
		if(toBuy <= 0) exception(locale, LocalesPaths.COMMAND_BUYMEMBERS_EXCEPTION_ENTERED_ZERO);
		if(plugin.getAPI().getLimitMaxMembers(src) < toBuy + plugin.getAPI().getLimitMembers(src)) {
			long max = plugin.getAPI().getLimitMaxMembers(src) - plugin.getAPI().getLimitMembers(src);
			if(max < 0) max = 0;
			exception(locale, LocalesPaths.COMMAND_BUYMEMBERS_EXCEPTION_TO_MUCH_VOLUME, new String[] {Placeholders.MAX}, max);
		}
		double needMoney = plugin.getAPI().getBuyMembersPrice(src) * toBuy;
		Currency currency = plugin.getAPI().getCurrency(src);
		if(!plugin.getEconomy().checkPlayerBalance(src.uniqueId(), currency, BigDecimal.valueOf(needMoney))) exception(locale, LocalesPaths.COMMAND_BUYMEMBERS_EXCEPTION_NOT_ENOUGH_MONEY);
		if(!plugin.getEconomy().removeFromPlayerBalance(src, currency, BigDecimal.valueOf(needMoney))) exception(locale, LocalesPaths.COMMAND_BUYMEMBERS_EXCEPTION_ECONOMY_EXCEPTION);
		if(!plugin.getAPI().getPlayerData(src).isPresent()) plugin.getAPI().setPlayerData(src, PlayerData.zero());
		plugin.getAPI().setLimitMembers(src, toBuy);
		src.sendMessage(getText(locale, LocalesPaths.COMMAND_BUYMEMBERS_SUCCESS).replace(new String[] {Placeholders.SIZE, Placeholders.VOLUME}, toBuy, plugin.getAPI().getLimitMembers(src)).get());
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_BUYMEMBERS);
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
				RawArguments.createLongArgument("Value", new ArrayList<Long>(), false, false, 0, null, null, null, null, LocalesPaths.COMMAND_BUYMEMBERS_EXCEPTION_NOT_PRESENT)
		);
	}

}
