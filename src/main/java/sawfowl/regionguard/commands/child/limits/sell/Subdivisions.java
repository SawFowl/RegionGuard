package sawfowl.regionguard.commands.child.limits.sell;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.Placeholders;

public class Subdivisions extends AbstractPlayerCommand {

	public Subdivisions(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, String[] args, Mutable arguments) throws CommandException {
		long toSell = getLong(args, 0).get();
		if(toSell <= 0) exception(locale, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_ENTERED_ZERO);
		if(!plugin.getAPI().getPlayerData(src).isPresent()) plugin.getAPI().setPlayerData(src, PlayerData.of(PlayerLimits.zero(), ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(src), plugin.getAPI().getClaimedRegions(src))));
		if(plugin.getAPI().getPlayerData(src).get().getLimits().getSubdivisions() < toSell) exception(locale, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_TO_MUCH_VOLUME, new String[] {Placeholders.MAX}, plugin.getAPI().getPlayerData(src).get().getLimits().getSubdivisions());
		if(!plugin.getEconomy().addToPlayerBalance(src, plugin.getAPI().getCurrency(src), BigDecimal.valueOf(toSell * plugin.getAPI().getSellSubdivisionPrice(src)))) exception(locale, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_ECONOMY_EXCEPTION);
		plugin.getAPI().setLimitSubdivisions(src, plugin.getAPI().getPlayerData(src).get().getLimits().getSubdivisions() - toSell);
		src.sendMessage(getText(locale, LocalesPaths.COMMAND_SELLSUBDIVISIONS_SUCCESS).replace(new String[] {Placeholders.SIZE, Placeholders.VOLUME}, toSell, plugin.getAPI().getLimitSubdivisions(src)).get());
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_SELLSUBDIVISIONS);
	}

	@Override
	public String permission() {
		return Permissions.SELL_BLOCKS;
	}

	@Override
	public String command() {
		return "subdivisions";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg limits sell subdivisions &7<Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg limits sell subdivisions ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return Arrays.asList(
				RawArguments.createLongArgument(new ArrayList<Long>(), false, false, 0, null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_NOT_PRESENT)
		);
	}

}
