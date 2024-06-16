package sawfowl.regionguard.commands.child.limits.set;

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
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.commandpack.api.commands.raw.arguments.RawBasicArgumentData;
import sawfowl.commandpack.api.commands.raw.arguments.RawOptional;
import sawfowl.commandpack.utils.CommandsUtil;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.commands.abstractcommands.AbstractCommand;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Set.Limit;

public class Blocks extends AbstractCommand {

	public Blocks(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, Audience audience, Locale locale, boolean isPlayer, Mutable arguments, RawArgumentsMap args) throws CommandException {
		ServerPlayer target = args.getPlayer(0).get();
		long toSet = args.getLong(1).get();
		if(toSet < 0) exception(getCommand(locale).getLimits().getSet().getLessThanZero());
		plugin.getAPI().setLimitBlocks(target, toSet);
		audience.sendMessage(getLimit(locale).getSuccessStaff(target.name(), toSet));
		target.sendMessage(getLimit(locale).getSuccess(toSet));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getLimit(locale).getDescription();
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
		return TextUtils.deserializeLegacy("&6/rg limits set blocks &7<Player> <Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg limits set blocks ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return Arrays.asList(
			RawArguments.createPlayerArgument(RawBasicArgumentData.createPlayer(0, null, null), RawOptional.notOptional(), locale -> getCommand(locale).getExceptions().getPlayerNotPresent()),
			RawArguments.createLongArgument(CommandsUtil.getEmptyList(), new RawBasicArgumentData<>(null, "Volume", 1, null, null), RawOptional.notOptional(), locale -> getCommand(locale).getExceptions().getVolumeNotPresent())
		);
	}

	private Limit getLimit(Locale locale) {
		return getCommand(locale).getLimits().getSet().getBlocksLimit();
	}

}
