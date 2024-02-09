package sawfowl.regionguard.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.data.command.Settings;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.commands.abstractcommands.AbstractCommand;
import sawfowl.regionguard.commands.child.Claim;
import sawfowl.regionguard.commands.child.Clear;
import sawfowl.regionguard.commands.child.Delete;
import sawfowl.regionguard.commands.child.Flag;
import sawfowl.regionguard.commands.child.Info;
import sawfowl.regionguard.commands.child.Leave;
import sawfowl.regionguard.commands.child.Limits;
import sawfowl.regionguard.commands.child.ListRegions;
import sawfowl.regionguard.commands.child.SetCreatingType;
import sawfowl.regionguard.commands.child.SetMessage;
import sawfowl.regionguard.commands.child.SetName;
import sawfowl.regionguard.commands.child.SetOwner;
import sawfowl.regionguard.commands.child.SetSelectorType;
import sawfowl.regionguard.commands.child.Trust;
import sawfowl.regionguard.commands.child.Untrust;
import sawfowl.regionguard.commands.child.UpdateDefaultFlags;
import sawfowl.regionguard.commands.child.Wand;
import sawfowl.regionguard.commands.child.WeCUI;
import sawfowl.regionguard.configure.LocalesPaths;

public class Region extends AbstractCommand {

	private List<RawCommand> childs;
	public Region(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, Audience audience, Locale locale, boolean isPlayer, String[] args, Mutable arguments) throws CommandException {
		sendPaginationList(audience, getComponent(locale, LocalesPaths.COMMANDS_TITLE), getComponent(locale, LocalesPaths.PADDING), 15, getChildExecutors().values().stream().filter(child -> child.canExecute(cause)).map(child -> child.usage(cause)).toList());
	}

	@Override
	public Component shortDescription(Locale locale) {
		return null;
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return null;
	}

	@Override
	public String permission() {
		return Permissions.HELP;
	}

	@Override
	public String command() {
		return "regionguard";
	}

	@Override
	public List<RawCommand> getChilds() {
		return childs != null ? childs : (childs = Arrays.asList(
			new Claim(plugin),
			new Delete(plugin),
			new Limits(plugin),
			new Flag(plugin),
			new Info(plugin),
			new Leave(plugin),
			new ListRegions(plugin),
			new SetCreatingType(plugin),
			new SetMessage(plugin),
			new SetName(plugin),
			new SetOwner(plugin),
			new SetSelectorType(plugin),
			new Trust(plugin),
			new Untrust(plugin),
			new UpdateDefaultFlags(plugin),
			new Wand(plugin),
			new WeCUI(plugin),
			new Clear(plugin)
		));
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return null;
	}

	@Override
	public Optional<Component> shortDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Main command"));
	}

	@Override
	public Optional<Component> extendedDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Main command"));
	}

	@Override
	public Component usage(CommandCause cause) {
		return Component.text("/rg");
	}

	@Override
	public Settings getCommandSettings() {
		return Settings.unregisteredBuilder().setEnable(true).setAliases(new String[] {"region", "rg"}).setAutoComplete(true).build();
	}

}
