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
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.commandpack.api.commands.raw.arguments.RawBasicArgumentData;
import sawfowl.commandpack.api.commands.raw.arguments.RawOptional;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class SetCreatingType extends AbstractPlayerCommand {

	private List<String> types;
	public SetCreatingType(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		RegionTypes type = RegionTypes.valueOfName(args.getString(0).get());
		if(type != RegionTypes.CLAIM && type != RegionTypes.ADMIN && type != RegionTypes.ARENA) return;
		plugin.getAPI().setCreatingRegionType(src, type);
		src.sendMessage(getCommand(locale).getCreatingType().get(type));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getCreatingType().getDescription();
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
		return TextUtils.deserializeLegacy("&6/rg setcreatingtype &7<Type>&f - ").clickEvent(ClickEvent.suggestCommand("/rg setcreatingtype ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		types = Arrays.asList("admin", "arena", "claim");
		return Arrays.asList(RawArguments.createStringArgument(types, new RawBasicArgumentData<String>("claim", "Type", 0, null, null), RawOptional.notOptional(), locale -> getExceptions(locale).getRegionTypeNotPresent()));
	}

}
