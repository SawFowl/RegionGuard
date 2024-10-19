package sawfowl.regionguard.commands.child;

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
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class UpdateDefaultFlags extends AbstractPlayerCommand {

	public UpdateDefaultFlags(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		plugin.getDefaultFlagsConfig().setDefaultFlags(region);
		src.sendMessage(getCommand(locale).getUpdateDefaultFlags().getSuccess(region.getType()));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getUpdateDefaultFlags().getDescription();
	}

	@Override
	public String permission() {
		return Permissions.STAFF_UPDATE_DEFAULT_FLAGS;
	}

	@Override
	public String command() {
		return "updatedefaultflags";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg updatedefaultflags&f - ").clickEvent(ClickEvent.runCommand("/rg updatedefaultflags")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return null;
	}

}
