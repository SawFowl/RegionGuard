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
		return Arrays.asList(
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
			new WeCUI(plugin)
		);
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
		return Settings.builder().setEnable(true).setAliases(new String[] {"region", "rg"}).build();
	}
	
	/**
	 * 
			Locale locale = src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT;
			messages.add(text("&6/rg setlimit &b[Args...]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SETLIMIT)));
			messages.add(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_USED_BY_NON_PLAYER));
			messages.add(text("&6/rg wand&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_WAND)));
			messages.add(text("&6/rg claim&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_CLAIM)));
			messages.add(text("&6/rg delete&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_DELETE)));
			messages.add(text("&6/rg info&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_INFO)));
			messages.add(text("&6/rg limits&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_LIMITS)));
			messages.add(text("&6/rg setname &b<ClearFlag> <OptionalLocale> [Name]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SET_NAME)));
			messages.add(text("&6/rg setmessage &b<CommandFlags> <Locale> [Message]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SET_MESSAGE)));
			messages.add(text("&6/rg flag &b[FlagName] [Value] <Source> <Target>&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_FLAG)));
			messages.add(text("&6/rg leave&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_LEAVE)));
			messages.add(text("&6/rg setowner &b[Player]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SETOWNER)));
			messages.add(text("&6/rg trust &b[Player] [TrustType]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_TRUST)));
			messages.add(text("&6/rg untrust &b[Player]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_UNTRUST)));
			messages.add(text("&6/rg setselector &b[Type]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SET_SELECTOR_TYPE)));
			messages.add(text("&6/rg setcreatingtype &b[Type]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SET_CREATING_TYPE)));
			messages.add(text("&6/rg wecui&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_WECUI)));
			messages.add(text("&6/rg list&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_LIST)));
			if(childExecutors.containsKey("buylimit") && childExecutors.containsKey("selllimit")) {
				messages.add(text("&6/rg buylimit &b[Limit] [Volume]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_BUYLIMIT)));
				messages.add(text("&6/rg selllimit &b[Limit] [Volume]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SELLLIMIT)));
			}
			messages.add(text("&6/rg updatedefaultflags&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_UPDATE_DEFAULT_FLAGS)));
			sendCommandsList((Audience) src, locale, messages, 35);
	 */

}
