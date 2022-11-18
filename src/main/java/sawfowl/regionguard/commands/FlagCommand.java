package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.registry.RegistryEntry;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.FlagSettings;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class FlagCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	public FlagCommand(RegionGuard plugin) {
		this.plugin = plugin;
		updateCompletions();
	}

	List<String> boolsOfString = Arrays.asList("true", "false");
	List<String> entityStrings = new ArrayList<String>();
	List<String> damageTypeAndEntityStrings = new ArrayList<String>();
	List<String> blockStrings = new ArrayList<String>();
	List<String> itemStrings = new ArrayList<String>();
	List<CommandCompletion> empty = new ArrayList<CommandCompletion>();
	List<CommandCompletion> onlyAll = Arrays.asList(CommandCompletion.of("all"));
	List<CommandCompletion> flags = new ArrayList<CommandCompletion>();
	List<CommandCompletion> bools = Arrays.asList(CommandCompletion.of("true"), CommandCompletion.of("false"));
	List<CommandCompletion> entities = new ArrayList<CommandCompletion>();
	List<CommandCompletion> damageTypesAndEntities = new ArrayList<CommandCompletion>();
	List<CommandCompletion> blocks = new ArrayList<CommandCompletion>();
	List<CommandCompletion> items = new ArrayList<CommandCompletion>();

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		String plainArgs = arguments.input();
		while(plainArgs.contains("  ")) plainArgs.replace("  ", " ");
		ServerPlayer player = (ServerPlayer) src;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		if(args.isEmpty()) {
			return sendFlagList(player, getFlagList(player, region));
		} else {
			String flag = args.get(0);
			String value = null;
			String source = null;
			String target = null;
			if(!plugin.getAPI().getRegisteredFlags().containsKey(flag) || !player.hasPermission(Permissions.setFlag(flag))) return sendFlagList(player, getFlagList(player, region));
			FlagSettings settings = plugin.getAPI().getRegisteredFlags().get(flag);
			for(int cursor = 0; cursor <= args.size() - 1; cursor++) {
				if(cursor == 1) {
					value = args.get(1);
					if(!value.equals("true") && !value.equals("false")) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_VALUE_NOT_PRESENT));
				} else if(cursor == 2) {
					if(settings.isAllowArgs()) {
						source = settings.isAllowSource(args.get(2)) ? args.get(2) : null;
					}
				} else if(cursor == 3) {
					if(settings.isAllowArgs()) {
						target = settings.isAllowTarget(args.get(3)) ? args.get(3) : null;
					}
				}
			}
			setFlag(region, flag, Boolean.parseBoolean(value), source, target);
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_SUCCESS));
		}
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		String plainArgs = arguments.input();
		if(!plainArgs.contains("flag ")) return empty;
		if(args.size() > 4) return empty;
		List<CommandCompletion> allow = flags.stream().filter(flag -> (cause.hasPermission(Permissions.setFlag(flag.completion())))).collect(Collectors.toList());
		List<String> allowNames = allow.stream().map(CommandCompletion::completion).collect(Collectors.toList());
		if(args.isEmpty()) return allow;
		if(args.size() == 1 && !allowNames.contains(args.get(args.size() - 1))) return allowNames.stream().filter(name -> (name.toLowerCase().startsWith(args.get(args.size() - 1).toLowerCase()))).map(CommandCompletion::of).collect(Collectors.toList());
		if(args.size() == 1 && !plainArgs.contains(args.get(args.size() - 1) + " ")) return empty;
		if(args.size() == 1) return bools;
		if(args.size() == 2 && !boolsOfString.contains(args.get(args.size() - 1))) return boolsOfString.stream().filter(bool -> (bool.startsWith(args.get(args.size() - 1)))).map(CommandCompletion::of).collect(Collectors.toList());
		String flag = args.get(0);
		if(!plugin.getAPI().getRegisteredFlags().containsKey(flag)) return empty;
		FlagSettings settings = plugin.getAPI().getRegisteredFlags().get(flag);
		if(args.size() == 2 && !plainArgs.contains(args.get(args.size() - 1) + " ")) return empty;
		if(args.size() > 1 && settings.isAllowArgs()) {
			if(args.size() == 2 && settings.isAllowSourceDamageType()) return damageTypesAndEntities;
			if(args.size() == 2 && settings.isAllowSourceEntity()) return entities;
			if(args.size() == 3) {
				if(!settings.isAllowSourceEntity() && !settings.isAllowSourceDamageType()) return onlyAll;
				if(settings.isAllowSourceDamageType() && !damageTypeAndEntityStrings.contains(args.get(args.size() - 1))) return damageTypeAndEntityStrings.stream().filter(source -> (source.startsWith(args.get(args.size() - 1)))).map(CommandCompletion::of).collect(Collectors.toList());
				if(settings.isAllowSourceEntity() && !entityStrings.contains(args.get(args.size() - 1))) return entityStrings.stream().filter(entity -> (entity.startsWith(args.get(args.size() - 1)))).map(CommandCompletion::of).collect(Collectors.toList());
				if(settings.isAllowTargetEntity()) return entities;
				if(settings.isAllowTargetBlock()) return blocks;
				if(settings.isAllowTargetItem()) return items;
			}
			if(args.size() == 3 && !plainArgs.contains(args.get(args.size() - 1) + " ")) return empty;
			if(args.size() == 4) {
				if(settings.isAllowTargetEntity() && !entityStrings.contains(args.get(args.size() - 1))) return entityStrings.stream().filter(entity -> (entity.startsWith(args.get(args.size() - 1)))).map(CommandCompletion::of).collect(Collectors.toList());
				if(settings.isAllowTargetBlock() && !blockStrings.contains(args.get(args.size() - 1))) return blockStrings.stream().filter(block -> (block.startsWith(args.get(args.size() - 1)))).map(CommandCompletion::of).collect(Collectors.toList());
				if(settings.isAllowTargetItem() && !itemStrings.contains(args.get(args.size() - 1))) return itemStrings.stream().filter(item -> (item.startsWith(args.get(args.size() - 1)))).map(CommandCompletion::of).collect(Collectors.toList());
			}
		}
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.FLAG_COMMAND);
	}

	private void setFlag(Region region, String flag, boolean value, String source, String target) {
		region.setFlag(flag, value, source, target);
		plugin.getAPI().saveRegion(region.getPrimaryParent());
	}

	private CommandResult sendFlagList(ServerPlayer player, List<Component> flagList) {
		PaginationList.builder()
		.contents(flagList)
		.title(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_LIST))
		.padding(plugin.getLocales().getText(player.locale(), LocalesPaths.PADDING))
		.linesPerPage(15)
		.sendTo(player);
		return CommandResult.success();
	}

	private List<Component> getFlagList(ServerPlayer player, Region region) {
		ArrayList<Component> messages = new ArrayList<Component>();
		int flagNumber = 0;
		List<Integer> addedCustom = new ArrayList<Integer>();
		for(String flagName : plugin.getAPI().getRegisteredFlags().keySet()) {
			List<Component> customFlagsList = getCustomFlagsList(player, region, flagName);
			if(!addedCustom.contains(flagNumber)) {
				addedCustom.add(flagNumber);
				if(!customFlagsList.isEmpty()) messages.addAll(customFlagsList);
				flagNumber++;
			}
			if(region.containsFlag(flagName)) {
				Component remove = Component.text("§7[§cRemove§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flagName))) {
								player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.FLAG), Arrays.asList(flagName.toString())), LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_FLAG));
								return;
							}
							region.removeFlag(flagName);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendFlagList(player, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_HOVER_REMOVE)));
				Component setTrue = Component.text("§7[§eTrue§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flagName))) {
								player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.FLAG), Arrays.asList(flagName.toString())), LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_FLAG));
								return;
							}
							region.setFlag(flagName, true, null, null);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendFlagList(player, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_HOVER_TRUE)));
				Component setFalse = Component.text("§7[§eFalse§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flagName))) {
								player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.FLAG), Arrays.asList(flagName.toString())), LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_FLAG));
								return;
							}
							region.setFlag(flagName, false, null, null);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendFlagList(player, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_HOVER_FALSE)));
				messages.add(Component.text("")
						.append(remove)
						.append(Component.text("   "))
						.append(region.getFlagResultWhithoutParrents(flagName, null, null) == Tristate.TRUE ? Component.text("§7[§aTrue§7]§r") : setTrue)
						.append(Component.text("   "))
						.append(region.getFlagResultWhithoutParrents(flagName, null, null) == Tristate.FALSE ? Component.text("§7[§aFalse§7]§r") : setFalse)
						.append(Component.text(" - "))
						.append(Component.text("§7[§b" + flagName.toString() + "§7]").
								hoverEvent(HoverEvent.showText(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_HOVER_SUGGEST_ARGS)))
								.clickEvent(ClickEvent.suggestCommand("/rg flag " + flagName.toString() + " ")))
				);
			} else {
				Component setTrue = Component.text(region.getFlagResult(flagName, null, null) == Tristate.TRUE ? "§7[§2True§7]§r" : "§7[§6True§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flagName))) {
								player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.FLAG), Arrays.asList(flagName)), LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_FLAG));
								return;
							}
							region.setFlag(flagName, true, null, null);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendFlagList(player, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_HOVER_TRUE)));
				Component setFalse = Component.text(region.getFlagResult(flagName, null, null) == Tristate.FALSE ? "§7[§2False§7]§r" : "§7[§6False§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flagName))) {
								player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.FLAG), Arrays.asList(flagName)), LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_FLAG));
								return;
							}
							region.setFlag(flagName, false, null, null);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendFlagList(player, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_HOVER_FALSE)));
				messages.add(Component.text("")
						.append(Component.text("§7[§eRemove§7]§r"))
						.append(Component.text("   "))
						.append(setTrue)
						.append(Component.text("   "))
						.append(setFalse)
						.append(Component.text(" - "))
						.append(Component.text("§7[§3" + flagName + "§7]").
								hoverEvent(HoverEvent.showText(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_HOVER_SUGGEST_ARGS)))
								.clickEvent(ClickEvent.suggestCommand("/rg flag " + flagName + " ")))
				);
			
			}
		}
		return messages;
	}

	private ArrayList<Component> getCustomFlagsList(ServerPlayer player, Region region, String flag) {
		ArrayList<Component> messages = new ArrayList<Component>();
		List<FlagValue> customFlags = new ArrayList<>();
		region.getCustomFlags().entrySet().stream().filter(entry -> (entry.getKey().equals(flag))).forEach(entry -> {
			customFlags.addAll(entry.getValue());
		});
		for(FlagValue flagValue : customFlags) {
			if(region.containsFlag(flag.toString())) {
				Component remove = Component.text("§7[§cRemove§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flag))) {
								player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.FLAG), Arrays.asList(flag.toString())), LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_FLAG));
								return;
							}
							region.removeFlag(flag, flagValue);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendFlagList(player, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_HOVER_REMOVE)));
				Component setTrue = Component.text("§7[§eTrue§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flag.toString()))) {
								player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.FLAG), Arrays.asList(flag)), LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_FLAG));
								return;
							}
							region.setFlag(flag, true, flagValue.getSource(), flagValue.getTarget());
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendFlagList(player, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_HOVER_TRUE)));
				Component setFalse = Component.text("§7[§eFalse§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flag))) {
								player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.FLAG), Arrays.asList(flag)), LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_FLAG));
								return;
							}
							region.setFlag(flag, false, flagValue.getSource(), flagValue.getTarget());
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendFlagList(player, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_FLAG_HOVER_FALSE)));
				Component display = Component.text("§d" + flag).hoverEvent(HoverEvent.showText(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SOURCE, ReplaceUtil.Keys.TARGET), Arrays.asList(flagValue.getSource(), flagValue.getTarget())), LocalesPaths.COMMAND_FLAG_HOVER_VALUES)));
				messages.add(Component.text("")
						.append(remove)
						.append(Component.text("   "))
						.append(flagValue.getValue() ? Component.text("§7[§aTrue§7]§r") : setTrue)
						.append(Component.text("   "))
						.append(!flagValue.getValue() ? Component.text("§7[§aFalse§7]§r") : setFalse)
						.append(Component.text(" - "))
						.append(Component.text("§7[").append(display).append(Component.text("§7]"))));
			} else {
				Component display = Component.text("§5" + flag).hoverEvent(HoverEvent.showText(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SOURCE, ReplaceUtil.Keys.TARGET), Arrays.asList(flagValue.getSource(), flagValue.getTarget())), LocalesPaths.COMMAND_FLAG_HOVER_VALUES)));
				messages.add(Component.text("")
						.append(Component.text("§7[§eRemove§7]§r"))
						.append(Component.text("   "))
						.append(Component.text(flagValue.getValue() ? "§7[§2True§7]§r" : "§7[§6True§7]§r"))
						.append(Component.text("   "))
						.append(Component.text(!flagValue.getValue() ? "§7[§2False§7]§r" : "§7[§6False§7]§r"))
						.append(Component.text(" - "))
						.append(Component.text("§7[").append(display).append(Component.text("§7]"))));
			}
		}
		return messages;
	}

	public void updateCompletions() {
		flags = plugin.getAPI().getRegisteredFlags().keySet().stream().map(CommandCompletion::of).collect(Collectors.toList());

		entityStrings = Sponge.game().registry(RegistryTypes.ENTITY_CATEGORY).streamEntries().map(RegistryEntry::key).map(ResourceKey::toString).collect(Collectors.toList());
		entityStrings.addAll(Sponge.game().registry(RegistryTypes.ENTITY_TYPE).streamEntries().map(RegistryEntry::key).map(ResourceKey::toString).collect(Collectors.toList()));
		damageTypeAndEntityStrings = Sponge.game().registry(RegistryTypes.DAMAGE_TYPE).streamEntries().map(RegistryEntry::key).map(ResourceKey::toString).collect(Collectors.toList());
		damageTypeAndEntityStrings.addAll(entityStrings);

		blockStrings = Sponge.game().registry(RegistryTypes.BLOCK_TYPE).streamEntries().map(RegistryEntry::key).map(ResourceKey::toString).collect(Collectors.toList());
		itemStrings = Sponge.game().registry(RegistryTypes.ITEM_TYPE).streamEntries().map(RegistryEntry::key).map(ResourceKey::toString).collect(Collectors.toList());

		entityStrings.add(0, "all");
		damageTypeAndEntityStrings.add(0, "all");
		blockStrings.add(0, "all");
		itemStrings.add(0, "all");

		entities.addAll(entityStrings.stream().map(CommandCompletion::of).collect(Collectors.toList()));
		damageTypesAndEntities.addAll(damageTypeAndEntityStrings.stream().map(CommandCompletion::of).collect(Collectors.toList()));
		blocks.addAll(blockStrings.stream().map(CommandCompletion::of).collect(Collectors.toList()));
		items.addAll(itemStrings.stream().map(CommandCompletion::of).collect(Collectors.toList()));
		
	}

	@Override
	public CommandException usage() throws CommandException {
		throw new CommandException(text("Usage: /rg flag"));
	}

}