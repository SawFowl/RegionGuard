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
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class Clear extends AbstractPlayerCommand {

	public Clear(RegionGuard regionGuard) {
		super(regionGuard);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer player, Locale locale, Mutable mutable, RawArgumentsMap args) throws CommandException {
		plugin.removePlayerPositions(player);
		plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(player);
		plugin.getAPI().getTempRegion(player.uniqueId()).ifPresent(temp -> {
			plugin.getAPI().getWorldEditCUIAPI().revertVisuals(player, temp.getUniqueId());
			plugin.getAPI().removeTempRegion(temp);
		});
		player.sendMessage(getCommand(locale).getClear().getSuccess());
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getClear().getDescription();
	}

	@Override
	public String command() {
		return "clear";
	}

	@Override
	public String permission() {
		return Permissions.CLEAR;
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg clear&f - ").clickEvent(ClickEvent.runCommand("/rg clear")).append(extendedDescription(getLocale(cause)));
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
