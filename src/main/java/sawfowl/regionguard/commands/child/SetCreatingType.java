package sawfowl.regionguard.commands.child;

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
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;

public class SetCreatingType extends AbstractPlayerCommand {

	public SetCreatingType(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, String[] args, Mutable arguments) throws CommandException {
		switch (RegionTypes.valueOfName(getString(args, 0).get())) {
		case ARENA: {
			plugin.getAPI().setCreatingRegionType(src, RegionTypes.ARENA);
			src.sendMessage(plugin.getLocales().getComponent(locale, LocalesPaths.COMMAND_REGION_TYPE_ARENA));
			break;
		}
		case ADMIN: {
			plugin.getAPI().setCreatingRegionType(src, RegionTypes.ADMIN);
			src.sendMessage(plugin.getLocales().getComponent(locale, LocalesPaths.COMMAND_REGION_TYPE_ADMIN));
			break;
		}
		default:
			plugin.getAPI().setCreatingRegionType(src, RegionTypes.CLAIM);
			src.sendMessage(plugin.getLocales().getComponent(locale, LocalesPaths.COMMAND_REGION_TYPE_CLAIM));
		}
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_SET_CREATING_TYPE);
	}

	@Override
	public String permission() {
		return Permissions.STAFF_SET_REGION_TYPE;
	}

	@Override
	public String command() {
		return "setcreatingtype";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg setcreatingtype &7[Type]&f - ").clickEvent(ClickEvent.suggestCommand("/rg setcreatingtype ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return Arrays.asList(RawArguments.createStringArgument(Arrays.asList("admin", "arena", "claim"), false, false, 0, "claim", LocalesPaths.COMMAND_REGION_TYPE_EXCEPTION_NOT_PRESENT));
	}

}
