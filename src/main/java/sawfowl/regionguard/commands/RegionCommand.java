package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.configure.LocalesPaths;

public class RegionCommand implements Command.Raw {

	private final RegionGuard plugin;
	private final List<String> childArgsEmpty = new ArrayList<String>();
	private final List<CommandCompletion> empty = new ArrayList<>();
	private List<CommandCompletion> childs = new ArrayList<CommandCompletion>();
	private Map<String, PluginRawCommand> childExecutors = new HashMap<String, PluginRawCommand>();
	public RegionCommand(RegionGuard plugin, RegisterCommandEvent<Command.Raw> event) {
		this.plugin = plugin;
		generateChild();
		event.register(plugin.getPluginContainer(), (Command.Raw) childExecutors.get("wand"), "wand");
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
		List<String> args = Stream.of(arguments.input().split(" ")).map(String::toString).filter(string -> (!string.equals(""))).collect(Collectors.toList());
		if(args.isEmpty()) {
			return generateHelp(cause, cause.root());
		} else {
			List<String> nextCommand = args.subList(1, args.size());
			if(childExecutors.containsKey(args.get(0)) && childExecutors.get(args.get(0)).canExecute(cause)) return childExecutors.get(args.get(0)).process(cause, arguments, nextCommand);
		}
		return generateHelp(cause, cause.root());
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments) throws CommandException {
		List<String> args = Stream.of(arguments.input().split(" ")).filter(string -> (!string.equals(""))).collect(Collectors.toList());
		List<CommandCompletion> send = childs.stream().filter(command -> (childExecutors.containsKey(command.completion()) && childExecutors.get(command.completion()).canExecute(cause))).collect(Collectors.toList());
		if(!send.isEmpty() && args.size() > 0) {
			List<String> nextCommand = args.size() == 1 ? childArgsEmpty : args.subList(1, args.size());
			if(childExecutors.containsKey(args.get(0)) && childExecutors.get(args.get(0)).canExecute(cause)) {
				return childExecutors.get(args.get(0)).complete(cause, arguments, nextCommand);
			} else {
				if(args.size() == 1) {
					return send.stream().filter(c -> (c.completion().startsWith(args.get(0)))).collect(Collectors.toList());
				} else {
					return empty;
				}
			}
		}
		return send;
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

	private void generateChild() {
		childExecutors.put("claim", new ClaimCommand(plugin));
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
	}

	private CommandResult generateHelp(CommandCause cause, Object src) {
		List<Component> messages = new ArrayList<Component>();
		if(src instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) src;
			if(player.hasPermission(Permissions.WAND)) messages.add(text("&6/rg wand&f - ").clickEvent(ClickEvent.runCommand("/rg wand")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_WAND)));
			if(player.hasPermission(Permissions.CLAIM)) messages.add(text("&6/rg claim&f - ").clickEvent(ClickEvent.runCommand("/rg claim")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_CLAIM)));
			if(player.hasPermission(Permissions.DELETE)) messages.add(text("&6/rg delete&f - ").clickEvent(ClickEvent.runCommand("/rg delete")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_DELETE)));
			if(player.hasPermission(Permissions.INFO)) messages.add(text("&6/rg info&f - ").clickEvent(ClickEvent.runCommand("/rg info")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_INFO)));
			if(player.hasPermission(Permissions.HELP)) messages.add(text("&6/rg limits&f - ").clickEvent(ClickEvent.runCommand("/rg limits")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_LIMITS)));
			if(player.hasPermission(Permissions.SET_NAME)) messages.add(text("&6/rg setname &b<ClearFlag> <Locale> [Name]&f - ").clickEvent(ClickEvent.suggestCommand("/rg setname ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SET_NAME)));
			if(player.hasPermission(Permissions.SET_MESSAGE)) messages.add(text("&6/rg setmessage &b<CommandFlags> <Locale> [Message]&f - ").clickEvent(ClickEvent.suggestCommand("/rg setmessage ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SET_MESSAGE)));
			if(player.hasPermission(Permissions.FLAG_COMMAND)) messages.add(text("&6/rg flag &b[FlagName] [Value] <Source> <Target>&f - ").clickEvent(ClickEvent.runCommand("/rg flag ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_FLAG)));
			messages.add(text("&6/rg leave&f - ").clickEvent(ClickEvent.suggestCommand("/rg leave")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_LEAVE)));
			if(player.hasPermission(Permissions.TRUST)) {
				messages.add(text("&6/rg setowner &b[Player]&f - ").clickEvent(ClickEvent.suggestCommand("/rg setowner ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SETOWNER)));
				messages.add(text("&6/rg trust &b[Player] [TrustType]&f - ").clickEvent(ClickEvent.suggestCommand("/rg trust ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_TRUST)));
				messages.add(text("&6/rg untrust &b[Player]&f - ").clickEvent(ClickEvent.suggestCommand("/rg untrust")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_UNTRUST)));
			}
			if(player.hasPermission(Permissions.CHANGE_SELECTOR)) messages.add(text("&6/rg setselector &b[Type]&f - ").clickEvent(ClickEvent.suggestCommand("/rg setselector ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SET_SELECTOR_TYPE)));
			if(player.hasPermission(Permissions.STAFF_SET_REGION_TYPE)) messages.add(text("&6/rg setcreatingtype &b[Type]&f - ").clickEvent(ClickEvent.suggestCommand("/rg setcreatingtype ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SET_CREATING_TYPE)));
			if(player.hasPermission(Permissions.CUI_COMMAND)) messages.add(text("&6/rg wecui&f - ").clickEvent(ClickEvent.runCommand("/rg wecui")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_WECUI)));
			if(player.hasPermission(Permissions.LIST) || player.hasPermission(Permissions.STAFF_LIST)) messages.add(text("&6/rg list&f - ").clickEvent(ClickEvent.runCommand("/rg list")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_LIST)));
			if(childExecutors.containsKey("buylimit") && childExecutors.containsKey("selllimit")) {
				if(childExecutors.get("buylimit").canExecute(cause) && plugin.getAPI().getBuyBlockPrice(player) > 0) messages.add(text("&6/rg buylimit &b[Limit] [Volume]&f - ").clickEvent(ClickEvent.suggestCommand("/rg buylimit ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_BUYLIMIT)));
				if(childExecutors.get("selllimit").canExecute(cause) && plugin.getAPI().getBuyClaimPrice(player) > 0) messages.add(text("&6/rg selllimit &b[Limit] [Volume]&f - ").clickEvent(ClickEvent.suggestCommand("/rg selllimit ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SELLLIMIT)));
			}
			if(childExecutors.get("setlimit").canExecute(cause)) messages.add(text("&6/rg setlimit &b[Args...]&f - ").clickEvent(ClickEvent.runCommand("/rg setlimit")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SETLIMIT)));
			if(player.hasPermission(Permissions.STAFF_UPDATE_DEFAULT_FLAGS)) messages.add(text("&6/rg updatedefaultflags&f - ").clickEvent(ClickEvent.runCommand("/rg updatedefaultflags")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_UPDATE_DEFAULT_FLAGS)));
			sendCommandsList(player, player.locale(), messages, 10);
		} else {
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
		}
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

	private Component text(String string) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
	}

	public FlagCommand getFlagCommand() {
		return (FlagCommand) childExecutors.get("flag");
	}

	public void genEconomyCommands() {
		if(plugin.getEconomyService() == null) return;
		childExecutors.put("buylimit", new BuyLimitsCommand(plugin));
		childExecutors.put("selllimit", new SellLimitsCommand(plugin));
		childs.add(CommandCompletion.of("buylimit"));
		childs.add(CommandCompletion.of("selllimit"));
	}

}
