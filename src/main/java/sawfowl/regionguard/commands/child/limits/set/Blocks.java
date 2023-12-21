package sawfowl.regionguard.commands.child.limits.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.commands.raw.arguments.RawArguments;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.commands.abstractcommands.AbstractCommand;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class Blocks extends AbstractCommand {

	public Blocks(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, Audience audience, Locale locale, boolean isPlayer, String[] args, Mutable arguments) throws CommandException {
		String sourceName = isPlayer ? ((ServerPlayer) audience).name() : "Server";
		ServerPlayer target = getPlayer(args, 0).get();
		long toSet = getLong(args, 1).get();
		if(toSet < 0) throw new CommandException(plugin.getLocales().getText(locale, LocalesPaths.COMMAND_SETLIMITBLOCKS_EXCEPTION_LESS_THEN_ZERO));
		plugin.getAPI().setLimitBlocks(target, toSet);
		audience.sendMessage(plugin.getLocales().getTextWithReplaced(locale, ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE, ReplaceUtil.Keys.PLAYER), Arrays.asList(toSet, target.name())), LocalesPaths.COMMAND_SETLIMITBLOCKS_SUCCESS_SOURCE));
		target.sendMessage(plugin.getLocales().getTextWithReplaced(target.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE, ReplaceUtil.Keys.PLAYER), Arrays.asList(toSet, sourceName)), LocalesPaths.COMMAND_SETLIMITBLOCKS_SUCCESS_TARGET));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_SETLIMITBLOCKS);
	}

	@Override
	public String permission() {
		return Permissions.STAFF_SETLIMIT_BLOCKS;
	}

	@Override
	public String command() {
		return "blocks";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg setlimit blocks &7[Player] [Volume]&f - ").clickEvent(ClickEvent.suggestCommand("/rg setlimit blocks ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return Arrays.asList(
			RawArguments.createPlayerArgument(false, false, 0, LocalesPaths.COMMAND_SETLIMITBLOCKS_EXCEPTION_PLAYER_NOT_PRESENT),
			RawArguments.createLongArgument(new ArrayList<Long>(), false, false, 1, null, LocalesPaths.COMMAND_SETLIMITBLOCKS_EXCEPTION_VOLUME_NOT_PRESENT)
		);
	}

}
