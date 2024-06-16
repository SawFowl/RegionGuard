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
import sawfowl.commandpack.api.data.command.Settings;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class Wand extends AbstractPlayerCommand {

	public Wand(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		if(src.inventory().contains(plugin.getAPI().getWandItem())) exception(getCommand(locale).getWand().getExist());
		if(src.inventory().freeCapacity() == 0) exception(getCommand(locale).getWand().getFullInventory());
		src.inventory().offer(plugin.getAPI().getWandItem());
		src.sendMessage(getCommand(locale).getWand().getSuccess());
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getWand().getDescription();
	}

	@Override
	public String permission() {
		return Permissions.WAND;
	}

	@Override
	public String command() {
		return "wand";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg wand&f - ").clickEvent(ClickEvent.runCommand("/rg wand")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public Settings getCommandSettings() {
		return Settings.unregisteredBuilder().setEnable(true).build();
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
