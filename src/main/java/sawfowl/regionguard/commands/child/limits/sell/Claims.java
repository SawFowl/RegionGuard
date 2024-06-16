package sawfowl.regionguard.commands.child.limits.sell;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

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
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Transaction.Limit;

public class Claims extends AbstractPlayerCommand {

	public Claims(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		long toSell = args.getLong(0).get();
		if(toSell <= 0) exception(getExceptions(locale).getEnteredZero());
		if(!plugin.getAPI().getPlayerData(src).isPresent()) plugin.getAPI().setPlayerData(src, PlayerData.of(PlayerLimits.zero(), ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(src), plugin.getAPI().getClaimedRegions(src))));
		if(plugin.getAPI().getPlayerData(src).get().getLimits().getRegions() < toSell) exception(getExceptions(locale).getMaxValue(plugin.getAPI().getPlayerData(src).get().getLimits().getRegions()));
		if(!plugin.getEconomy().addToPlayerBalance(src, plugin.getAPI().getCurrency(src), BigDecimal.valueOf(toSell * plugin.getAPI().getSellClaimPrice(src)))) exception(getExceptions(locale).getEconomyException());
		plugin.getAPI().setLimitClaims(src, plugin.getAPI().getPlayerData(src).get().getLimits().getRegions() - toSell);
		src.sendMessage(getLimit(locale).getSuccess(toSell, plugin.getAPI().getLimitClaims(src)));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getLimit(locale).getDescription();
	}

	@Override
	public String permission() {
		return Permissions.SELL_CLAIMS;
	}

	@Override
	public String command() {
		return "claims";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg limits sell claims &7<Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg limits sell claims ")).append(extendedDescription(getLocale(cause)));
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
		return getCommand(locale).getLimits().getSell().getClaimsLimit();
	}

}
