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
	private List<CommandCompletion> childs = new ArrayList<CommandCompletion>();
	private List<CommandCompletion> empty = new ArrayList<>();
	private Map<String, Command.Raw> childExecutors = new HashMap<String, Command.Raw>();
	public RegionCommand(RegionGuard plugin, RegisterCommandEvent<Command.Raw> event) {
		this.plugin = plugin;
		generateChild();
		event.register(plugin.getPluginContainer(), childExecutors.get("wand"), "wand");
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
		List<String> args = Stream.of(arguments.input().split(" ")).map(String::toString).filter(string -> (!string.equals(""))).collect(Collectors.toList());
		if(args.isEmpty()) {
			return generateHelp(cause.root());
		} else {
			if(childExecutors.containsKey(args.get(0)) && childExecutors.get(args.get(0)).canExecute(cause)) return childExecutors.get(args.get(0)).process(cause, arguments);
		}
		return generateHelp(cause.root());
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments) throws CommandException {
		List<String> args = Stream.of(arguments.input().split(" ")).map(String::toString).filter(string -> (!string.equals(""))).collect(Collectors.toList());
		List<CommandCompletion> send = childs.stream().filter(command -> (childExecutors.containsKey(command.completion()) && childExecutors.get(command.completion()).canExecute(cause))).collect(Collectors.toList());
		if(!send.isEmpty() && args.size() > 0) {
			if(childExecutors.containsKey(args.get(0)) && childExecutors.get(args.get(0)).canExecute(cause)) {
				return childExecutors.get(args.get(0)).complete(cause, arguments);
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
		childExecutors.put("setlimitblocks", new SetBlocksLimitCommand(plugin));
		childExecutors.put("setlimitclaims", new SetClaimsLimitCommand(plugin));
		childExecutors.put("setlimitsubdivisions", new SetSubdivisionsLimitCommand(plugin));
	}

	private CommandResult generateHelp(Object src) {
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
			if(plugin.getEconomyService() != null) {
				if(player.hasPermission(Permissions.BUY_BLOCKS) && plugin.getAPI().getBuyBlockPrice(player) > 0) messages.add(text("&6/rg buyblocks &b<Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg buyblocks 1")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_BUYBLOCKS)));
				if(player.hasPermission(Permissions.BUY_CLAIMS) && plugin.getAPI().getBuyClaimPrice(player) > 0) messages.add(text("&6/rg buyclaims &b<Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg buyclaims 1")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_BUYCLAIMS)));
				if(player.hasPermission(Permissions.BUY_SUBDIVISIONS) && plugin.getAPI().getBuySubdivisionPrice(player) > 0) messages.add(text("&6/rg buysubdivisions &b<Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg buysubdivisions 1")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_BUYSUBDIVISIONS)));
				if(player.hasPermission(Permissions.SELL_BLOCKS) && plugin.getAPI().getBuyBlockPrice(player) > 0) messages.add(text("&6/rg sellblocks &b<Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg sellblocks 1")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SELLBLOCKS)));
				if(player.hasPermission(Permissions.SELL_CLAIMS) && plugin.getAPI().getBuyClaimPrice(player) > 0) messages.add(text("&6/rg sellclaims &b<Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg sellclaims 1")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SELLCLAIMS)));
				if(player.hasPermission(Permissions.SELL_SUBDIVISIONS) && plugin.getAPI().getBuySubdivisionPrice(player) > 0) messages.add(text("&6/rg sellsubdivisions &b<Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg sellsubdivisions 1")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SELLSUBDIVISIONS)));
			}
			if(player.hasPermission(Permissions.STAFF_SETLIMIT_BLOCKS)) messages.add(text("&6/rg setlimitblocks &b<Player> <Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg setlimitblocks ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SETLIMITBLOCKS)));
			if(player.hasPermission(Permissions.STAFF_SETLIMIT_CLAIMS)) messages.add(text("&6/rg setlimitclaims &b<Player> <Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg setlimitclaims ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SETLIMITCLAIMS)));
			if(player.hasPermission(Permissions.STAFF_SETLIMIT_SUBDIVISIONS)) messages.add(text("&6/rg setlimitsubdivisions &b<Player> <Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg setlimitsubdivisions ")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SETLIMITSUBDIVISIONS)));
			sendCommandsList(player, player.locale(), messages, 7);
		} else {
			Locale locale = src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT;
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
			if(plugin.getEconomyService() != null) {
				messages.add(text("&6/rg buyblocks &b<Volume>&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_BUYBLOCKS)));
				messages.add(text("&6/rg buyclaims &b<Volume>&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_BUYCLAIMS)));
				messages.add(text("&6/rg buysubdivisions &b<Volume>&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_BUYSUBDIVISIONS)));
				messages.add(text("&6/rg sellblocks &b<Volume>&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SELLBLOCKS)));
				messages.add(text("&6/rg sellclaims &b<Volume>&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SELLCLAIMS)));
				messages.add(text("&6/rg sellsubdivisions &b<Volume>&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SELLSUBDIVISIONS)));
			}
			messages.add(text("&6/rg setlimitblocks &b<Player> <Volume>&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SETLIMITBLOCKS)));
			messages.add(text("&6/rg setlimitclaims &b<Player> <Volume>&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SETLIMITCLAIMS)));
			messages.add(text("&6/rg setlimitsubdivisions &b<Player> <Volume>&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SETLIMITSUBDIVISIONS)));
			sendCommandsList((Audience) src, locale, messages, 30);
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
		childExecutors.put("buyblocks", new BuyBlocksCommand(plugin));
		childExecutors.put("buyclaims", new BuyClaimsCommand(plugin));
		childExecutors.put("buysubdivisions", new BuySubdivisionsCommand(plugin));
		childExecutors.put("sellblocks", new SellBlocksCommand(plugin));
		childExecutors.put("sellclaims", new SellClaimsCommand(plugin));
		childExecutors.put("sellsubdivisions", new SellSubdivisionsCommand(plugin));
		childs.addAll(childExecutors.keySet().stream().map(CommandCompletion::of).collect(Collectors.toList()));
	}

}
