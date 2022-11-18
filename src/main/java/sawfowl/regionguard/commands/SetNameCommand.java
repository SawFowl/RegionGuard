package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.LocalesPaths;

public class SetNameCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	private final Map<String, Locale> locales = new HashMap<String, Locale>();
	private final List<String> flags = Arrays.asList("-c", "-clear");
	private final List<String> allArgs = new ArrayList<String>();
	private final List<CommandCompletion> completionFlags = flags.stream().map(CommandCompletion::of).collect(Collectors.toList());
	private final List<CommandCompletion> all = new ArrayList<CommandCompletion>();
	private final List<CommandCompletion> completionLocales;
	private final List<CommandCompletion> empty = new ArrayList<>();
	public SetNameCommand(RegionGuard plugin) {
		this.plugin = plugin;
		plugin.getLocales().getLocaleService().getLocalesList().forEach(locale -> {
			locales.put(locale.toLanguageTag(), locale);
		});
		allArgs.addAll(flags);
		allArgs.addAll(locales.keySet());
		completionLocales = locales.values().stream().map(Locale::toLanguageTag).map(CommandCompletion::of).collect(Collectors.toList());
		all.addAll(completionFlags);
		all.addAll(completionLocales);
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		if(args.isEmpty()) usage();
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		if(region.isGlobal()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND));
		if(!player.hasPermission(Permissions.STAFF_SET_NAME)) {
			if(!region.isTrusted(player)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_NAME_NOT_TRUSTED));
			if(region.isCurrentTrustType(player, TrustTypes.OWNER) || region.isCurrentTrustType(player, TrustTypes.MANAGER)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_NAME_LOW_TRUST));
		}
		boolean clearFlag = args.size() > 0 && (args.get(0).equals("-c") || args.get(0).equals("-clear"));
		Locale locale = args.size() > 0 && locales.keySet().contains(args.get(0)) ? locales.get(args.get(0)) : player.locale();
		if(locales.keySet().contains(args.get(0))) args.remove(0);
		if(clearFlag) {
			region.setName(null, locale);
			plugin.getAPI().saveRegion(region.getPrimaryParent());
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_NAME_CLEARED));
		} else {
			String newName = "";
			int size = args.size();
			int word = 0;
			for(String string : args) {
				if(word < size) {
					newName = newName + string + " ";
				} else {
					newName = newName + string;
				}
			}
			if(newName.equals("")) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_NAME_NOT_PRESENT));
			if(removeDecor(newName).length() > 20) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_NAME_TOO_LONG));
			region.setName(newName, locale);
			plugin.getAPI().saveRegion(region.getPrimaryParent());
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_NAME_SUCCESS));
		}
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		if(args.isEmpty()) usage();
		String plainArgs = arguments.input();
		if(!plainArgs.contains("setname ") && !plainArgs.contains("name ")) return empty;
		if(plainArgs.contains("setname")) plainArgs = plainArgs.replaceFirst("setname", "");
		if(plainArgs.contains("name")) plainArgs = plainArgs.replaceFirst("name", "");
		if(!args.isEmpty() && args.size() < 3) {
			if(args.size() == 1 && !flags.contains(args.get(args.size() - 1))) return completionFlags.stream().filter(flag -> (flag.completion().startsWith(args.get(args.size() - 1)))).collect(Collectors.toList());
			if(args.size() == 2 && flags.contains(args.get(args.size() - 2)) && !locales.containsKey(args.get(args.size() - 1))) return completionLocales.stream().filter(locale -> (locale.completion().startsWith(args.get(args.size() - 1)))).collect(Collectors.toList());
			if(locales.containsKey(args.get(args.size() - 1)) || !plainArgs.contains(args.get(args.size() - 1) + " ")) return empty;
			if(plainArgs.contains("-c ") && args.size() == 1) return completionLocales;
			List<String> optArgs = allArgs.stream().filter(string -> (!string.equals(args.get(args.size() - 1)) && string.startsWith(args.get(args.size() - 1)))).collect(Collectors.toList());
			return optArgs.stream().map(CommandCompletion::of).collect(Collectors.toList());
		}
		if(args.isEmpty()) return all;
		if(!args.isEmpty() && locales.containsKey(args.get(0))) return empty;
		if(args.size() == 1 && flags.contains(args.get(0))) return completionLocales;
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.SET_NAME);
	}

	@Override
	public CommandException usage() throws CommandException {
		throw new CommandException(text("Usage: /rg setmessage <CommandFlags> <Locale> [Message]"));
	}

}
