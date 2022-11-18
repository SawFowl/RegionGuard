package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.configure.LocalesPaths;

public class SellLimitsCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	private final List<String> childArgsEmpty = new ArrayList<String>();
	private final List<CommandCompletion> empty = new ArrayList<>();
	private Map<String, PluginRawCommand> childExecutors = new HashMap<String, PluginRawCommand>();
	private List<CommandCompletion> childs = new ArrayList<CommandCompletion>();
	public SellLimitsCommand(RegionGuard plugin) {
		this.plugin = plugin;
		generateChild();
		
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		if(args.isEmpty()) {
			return generateHelp(cause.root());
		} else {
			List<String> nextCommand = args.subList(1, args.size());
			if(childExecutors.containsKey(args.get(0)) && childExecutors.get(args.get(0)).canExecute(cause)) return childExecutors.get(args.get(0)).process(cause, arguments, nextCommand);
		}
		return generateHelp(cause.root());
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
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
		return cause.hasPermission(Permissions.SELL_BLOCKS) || cause.hasPermission(Permissions.SELL_CLAIMS) || cause.hasPermission(Permissions.SELL_SUBDIVISIONS) || cause.hasPermission(Permissions.SELL_MEMBERS);
	}

	private void generateChild() {
		childExecutors.put("blocks", new SellBlocksCommand(plugin));
		childExecutors.put("claims", new SellClaimsCommand(plugin));
		childExecutors.put("subdivisions", new SellSubdivisionsCommand(plugin));
		childExecutors.put("members", new SellMembersCommand(plugin));
		childs.addAll(childExecutors.keySet().stream().map(CommandCompletion::of).collect(Collectors.toList()));
	}

	private CommandResult generateHelp(Object src) {
		List<Component> messages = new ArrayList<Component>();
		if(src instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) src;
			if(player.hasPermission(Permissions.SELL_BLOCKS)) messages.add(text("&6/rg selllimit blocks &b[Volume]&f - ").clickEvent(ClickEvent.suggestCommand("/rg selllimit blocks 1")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SELLBLOCKS)));
			if(player.hasPermission(Permissions.SELL_CLAIMS)) messages.add(text("&6/rg selllimit claims &b[Volume]&f - ").clickEvent(ClickEvent.suggestCommand("/rg selllimit claims 1")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SELLCLAIMS)));
			if(player.hasPermission(Permissions.SELL_SUBDIVISIONS)) messages.add(text("&6/rg selllimit subdivisions &b[Volume]&f - ").clickEvent(ClickEvent.suggestCommand("/rg selllimit subdivisions 1")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SELLSUBDIVISIONS)));
			if(player.hasPermission(Permissions.SELL_MEMBERS)) messages.add(text("&6/rg selllimit members &b[Volume]&f - ").clickEvent(ClickEvent.suggestCommand("/rg selllimit members 1")).append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_SELLMEMBERS)));
			sendCommandsList(player, player.locale(), messages, 10);
		} else {
			Locale locale = src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT;
			messages.add(text("&6/rg selllimit blocks &b[Volume]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SELLBLOCKS)));
			messages.add(text("&6/rg selllimit claims &b[Volume]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SELLCLAIMS)));
			messages.add(text("&6/rg selllimit subdivisions &b[Volume]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SELLSUBDIVISIONS)));
			messages.add(text("&6/rg selllimit members &b[Volume]&f - ").append(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_SELLMEMBERS)));
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

	@Override
	public CommandException usage() throws CommandException {
		throw new CommandException(text("Usage: /rg sellimit [Limit] [Volume]"));
	}

}
