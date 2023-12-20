package sawfowl.regionguard.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.service.pagination.PaginationList;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.data.command.Settings;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.configure.LocalesPaths;

public class RegionCommand extends AbstractCommand {

	public RegionCommand(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, Audience audience, Locale locale, boolean isPlayer, String[] args, Mutable arguments) throws CommandException {
		generateHelp(cause, locale);
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
		return Arrays.asList(new Claim(plugin));
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return null;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.HELP);
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

	/*private void generateChild() {
		childExecutors.put("claim", new Claim(plugin));
		childExecutors.put("delete", new DeleteCommand(plugin));
		childExecutors.put("flag", new FlagCommand(plugin));
		childExecutors.put("info", new InfoCommand(plugin));
		childExecutors.put("leave", new LeaveCommand(plugin));
		childExecutors.put("limits", new LimitsCommand(plugin));
		SetCreatingTypeCommand setCreatingTypeCommand = new SetCreatingTypeCommand(plugin);
		childExecutors.put("setcreatingtype", setCreatingTypeCommand);
		childExecutors.put("creatingtype", setCreatingTypeCommand);
		childExecutors.put("type", setCreatingTypeCommand);
		SetMessageCommand messageCommand = new SetMessageCommand(plugin);
		childExecutors.put("setmessage", messageCommand);
		childExecutors.put("message", messageCommand);
		SetNameCommand nameCommand = new SetNameCommand(plugin);
		childExecutors.put("setname", nameCommand);
		childExecutors.put("name", nameCommand);
		childExecutors.put("setowner", new SetOwnerCommand(plugin));
		SetSelectorTypeCommand selectorTypeCommand = new SetSelectorTypeCommand(plugin);
		childExecutors.put("setselector", selectorTypeCommand);
		childExecutors.put("selector", selectorTypeCommand);
		SetCreatingTypeCommand selectorCreatingTypeCommand = new SetCreatingTypeCommand(plugin);
		childExecutors.put("setcreatingtype", selectorCreatingTypeCommand);
		childExecutors.put("creatingtype", selectorCreatingTypeCommand);
		childExecutors.put("type", selectorCreatingTypeCommand);
		childExecutors.put("trust", new TrustCommand(plugin));
		childExecutors.put("untrust", new UntrustCommand(plugin));
		childExecutors.put("wand", new WandCommand(plugin));
		childExecutors.put("wecui", new WeCUICommand(plugin));
		childExecutors.put("list", new ListCommand(plugin));
		childExecutors.put("setlimit", new SetLimitsCommand(plugin));
		UpdateDefaultFlagsCommand updateDefaultFlagsCommand = new UpdateDefaultFlagsCommand(plugin);
		childExecutors.put("updatedefaultflags", updateDefaultFlagsCommand);
		childExecutors.put("udf", updateDefaultFlagsCommand);
		childs.addAll(childExecutors.keySet().stream().map(CommandCompletion::of).collect(Collectors.toList()));
	}*/

	private CommandResult generateHelp(CommandCause cause, Locale locale) {
		sendCommandsList(cause.audience(), locale, getChildExecutors().values().stream().filter(child -> child.canExecute(cause)).map(child -> child.usage(cause)).toList(), 35);
		return CommandResult.success();
	}

	private void sendCommandsList(Audience audience, Locale locale, List<Component> messages, int lines) {
		PaginationList.builder()
		.contents(messages)
		.title(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_TITLE))
		.padding(plugin.getLocales().getText(locale, LocalesPaths.PADDING))
		.linesPerPage(lines)
		.sendTo(audience);
	}

	public void genEconomyCommands() {
		if(plugin.getEconomyService() == null) return;
		//getChildExecutors().put("buylimit", new BuyLimitsCommand(plugin));
		//getChildExecutors().put("selllimit", new SellLimitsCommand(plugin));
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
