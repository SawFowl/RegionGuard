package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.LocalesPaths;

public class SetMessageCommand implements Command.Raw {

	private final RegionGuard plugin;
	private final Map<String, Locale> locales = new HashMap<String, Locale>();
	private final List<String> flags = Arrays.asList("-j", "-join", "-e", "-exit");
	private final List<String> allFlags = Arrays.asList("-c", "-clear", "-j", "-join", "-e", "-exit");
	private final List<String> allArgs = new ArrayList<String>();
	private final List<CommandCompletion> completionAllFlags = allFlags.stream().map(CommandCompletion::of).collect(Collectors.toList());
	private final List<CommandCompletion> completionFlags = flags.stream().map(CommandCompletion::of).collect(Collectors.toList());
	private final List<CommandCompletion> completionLocales;
	private final List<CommandCompletion> empty = new ArrayList<>();
	public SetMessageCommand(RegionGuard plugin) {
		this.plugin = plugin;
		plugin.getLocales().getLocaleService().getLocalesList().forEach(locale -> {
			locales.put(locale.toLanguageTag(), locale);
		});
		completionLocales = locales.values().stream().map(Locale::toLanguageTag).map(CommandCompletion::of).collect(Collectors.toList());
		allArgs.addAll(allFlags);
		allArgs.addAll(locales.keySet());
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition()).getPrimaryParent();
		if(region.isGlobal()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND));
		if(!player.hasPermission(Permissions.STAFF_SET_MESSAGE)) {
			if(!region.isTrusted(player)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_NOT_TRUSTED));
			if(region.isCurrentTrustType(player, TrustTypes.OWNER) || region.isCurrentTrustType(player, TrustTypes.MANAGER)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_LOW_TRUST));
		}
		String plainArgs = arguments.input();
		List<String> args = Stream.of(plainArgs.split(" ")).filter(string -> (!string.equals(""))).collect(Collectors.toList());
		args.remove(0);
		boolean clearFlag = args.contains("-c") || args.contains("-clear") || args.contains("--clear");
		boolean join = args.contains("-j") || args.contains("-join");
		boolean exit = args.contains("-e") || args.contains("-exit");
		if(args.isEmpty()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_NOT_PRESENT));
		Optional<Locale> optLocale = Optional.empty();
		for(String string : args) if(locales.containsKey(string)) {
			optLocale = Optional.ofNullable(locales.get(string));
			break;
		}
		if(optLocale.isPresent()) args.remove(optLocale.get().toLanguageTag());
		if(args.isEmpty()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_NOT_PRESENT));
		Locale locale = optLocale.isPresent() ? optLocale.get() : player.locale();
		if(optLocale.isPresent()) args.remove(optLocale.get().toLanguageTag());
		if(clearFlag) {
			if(join) {
				region.setJoinMessage(null, locale);
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_CLEAR_JOIN));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else if(exit) {
				region.setExitMessage(null, locale);
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_CLEAR_EXIT));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else {
				throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_TYPE_NOT_PRESENT));
			}
		} else {
			args.removeAll(flags);
			String message = "";
			int size = args.size();
			int word = 0;
			for(String string : args) {
				if(word < size) {
					message = message + string + " ";
				} else {
					message = message + string;
				}
			}
			if(message.equals("")) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_NOT_PRESENT));
			if(removeDecor(message).length() > 50) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_TOO_LONG));
			if(join) {
				region.setJoinMessage(message, locale);
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_JOIN));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else if(exit) {
				region.setExitMessage(message, locale);
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_EXIT));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else {
				throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SET_MESSAGE_TYPE_NOT_PRESENT));
			}
		}
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments) throws CommandException {
		String plainArgs = arguments.input();
		if(!plainArgs.contains("setmessage ") && !plainArgs.contains("message ")) return empty;
		List<String> args = Stream.of(plainArgs.split(" ")).filter(string -> (!string.equals(""))).collect(Collectors.toList());
		if(args.get(0).equals("setmessage")) plainArgs = plainArgs.replaceFirst("setmessage", "");
		if(args.get(0).equals("message")) plainArgs = plainArgs.replaceFirst("message", "");
		args.remove(0);
		if(args.isEmpty()) return completionAllFlags;
		if(args.size() < 4) {
			if(args.size() == 2 && flags.contains(args.get(args.size() - 2)) && !locales.containsKey(args.get(args.size() - 1))) return completionLocales.stream().filter(locale -> (locale.completion().startsWith(args.get(args.size() - 1)))).collect(Collectors.toList());
			if(args.size() == 3 && flags.contains(args.get(args.size() - 2)) && !locales.containsKey(args.get(args.size() - 1))) return completionLocales.stream().filter(locale -> (locale.completion().startsWith(args.get(args.size() - 1)))).collect(Collectors.toList());
			if(args.size() == 1 && !allFlags.contains(args.get(0))) return completionAllFlags.stream().filter(flag -> (flag.completion().startsWith(args.get(0)))).collect(Collectors.toList());
			if(locales.containsKey(args.get(args.size() - 1)) || !plainArgs.contains(args.get(args.size() - 1) + " ")) return empty;
			if((plainArgs.contains("-c ") || plainArgs.contains("-clear ")) && args.size() == 1) return completionFlags;
			if(args.size() == 1 && !allFlags.contains(args.get(args.size() - 1))) return completionFlags.stream().filter(flag -> (flag.completion().startsWith(args.get(args.size() - 1)))).collect(Collectors.toList());
			if((plainArgs.contains("-j ") || plainArgs.contains("-join ") || plainArgs.contains("-e ") || plainArgs.contains("-exit ")) && args.size() < 3 && flags.contains(args.get(args.size() - 1))) return completionLocales;
			List<String> optArgs = allArgs.stream().filter(string -> (!string.equals(args.get(args.size() - 1)) && string.startsWith(args.get(args.size() - 1)))).collect(Collectors.toList());
			return optArgs.stream().map(CommandCompletion::of).collect(Collectors.toList());
		}
		if(locales.containsKey(args.get(args.size() - 1))) return empty;
		if(args.size() == 1 && allFlags.contains(args.get(0))) return completionLocales;
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.SET_MESSAGE);
	}

	@Override
	public Optional<Component> shortDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Set join/exit messages."));
	}

	@Override
	public Optional<Component> extendedDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Set join/exit messages."));
	}

	@Override
	public Component usage(CommandCause cause) {
		return Component.text("/rg setmessage <CommandFlag> [Message]");
	}

	private String removeDecor(String string) {
		Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(string);
		component.decorations().clear();
		return LegacyComponentSerializer.legacyAmpersand().serialize(component);
	}

}
