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
import sawfowl.regionguard.utils.ReplaceUtil;

public class Members extends AbstractPlayerCommand {

	public Members(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, String[] args, Mutable arguments) throws CommandException {
		long toSell = getLong(args, 0).get();
		if(toSell <= 0) throw new CommandException(plugin.getLocales().getText(locale, LocalesPaths.COMMAND_SELLMEMBERS_EXCEPTION_ENTERED_ZERO));
		if(!plugin.getAPI().getPlayerData(src).isPresent()) plugin.getAPI().setPlayerData(src, PlayerData.of(PlayerLimits.zero(), ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(src), plugin.getAPI().getClaimedRegions(src))));
		if(plugin.getAPI().getPlayerData(src).get().getLimits().getMembersPerRegion() < toSell) throw new CommandException(plugin.getLocales().getTextWithReplaced(src.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.MAX), Arrays.asList(plugin.getAPI().getPlayerData(src).get().getLimits().getMembersPerRegion())), LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_TO_MUCH_VOLUME));
		if(!plugin.getEconomy().addToPlayerBalance(src, plugin.getEconomy().checkCurrency(plugin.getAPI().getCurrency(src)), BigDecimal.valueOf(toSell * plugin.getAPI().getSellSubdivisionPrice(src)))) throw new CommandException(plugin.getLocales().getText(src.locale(), LocalesPaths.COMMAND_SELLMEMBERS_EXCEPTION_ECONOMY_EXCEPTION));
		plugin.getAPI().setLimitMembers(src, plugin.getAPI().getPlayerData(src).get().getLimits().getMembersPerRegion() - toSell);
		src.sendMessage(plugin.getLocales().getTextWithReplaced(src.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE, ReplaceUtil.Keys.VOLUME), Arrays.asList(toSell, plugin.getAPI().getLimitMembers(src))), LocalesPaths.COMMAND_SELLMEMBERS_SUCCESS));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_SELLBLOCKS);
	}

	@Override
	public String permission() {
		return Permissions.SELL_BLOCKS;
	}

	@Override
	public String command() {
		return "members";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg selllimit members &7[Volume]&f - ").clickEvent(ClickEvent.suggestCommand("/rg selllimit members ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return Arrays.asList(
				RawArguments.createLongArgument(new ArrayList<Long>(), false, false, 0, null, LocalesPaths.COMMAND_SELLMEMBERS_EXCEPTION_NOT_PRESENT)
		);
	}

}
