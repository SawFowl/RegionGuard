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
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;

public class Wand extends AbstractPlayerCommand {

	public Wand(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, String[] args, Mutable arguments) throws CommandException {
		if(src.inventory().contains(plugin.getAPI().getWandItem())) exception(locale, LocalesPaths.COMMAND_WAND_EXCEPTION_ITEM_EXIST);
		if(src.inventory().freeCapacity() == 0) exception(locale, LocalesPaths.COMMAND_WAND_EXCEPTION_INVENTORY_IS_FULL);
		src.inventory().offer(plugin.getAPI().getWandItem());
		src.sendMessage(getComponent(locale, LocalesPaths.COMMAND_WAND_SUCCESS));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_WAND);
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
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return null;
	}

}
