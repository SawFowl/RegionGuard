package sawfowl.regionguard.commands.child;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.command.registrar.tree.CommandTreeNodeTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.Tristate;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentData;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.commandpack.api.commands.raw.arguments.RawOptional;
import sawfowl.commandpack.api.commands.raw.arguments.RawRequiredArgs;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.FlagConfig;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class Flag extends AbstractPlayerCommand {

	private List<String> values;
	public Flag(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		Optional<FlagConfig> optFlag = args.get(0);
		if(optFlag.isPresent()) {
			setFlag(region, optFlag.get().getName(), args.getBoolean(1).orElse(false), args.getString(2).orElse("all"), args.getString(3).orElse("all"));
			src.sendMessage(getCommand(locale).getFlag().getSuccess());
		} else sendPaginationList(src, getCommand(locale).getFlag().getTitle(), getCommand(locale).getFlag().getPadding(), 10, getFlagList(src, region));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getFlag().getDescription();
	}

	@Override
	public String permission() {
		return Permissions.FLAG_COMMAND;
	}

	@Override
	public String command() {
		return "flag";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg flag &7[Flag] [Source] [Target]&f - ").clickEvent(ClickEvent.suggestCommand("/rg flag ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		if(values == null) values = Arrays.asList("true", "false");
		return Arrays.asList(
			RawArgument.of(
				FlagConfig.class,
				(cause, args) -> plugin.getAPI().getRegisteredFlags().keySet().stream().filter(flag -> cause.hasPermission(Permissions.setFlag(flag))),
				(cause, args) -> args.length == 0 ? Optional.empty() : plugin.getAPI().getRegisteredFlags().values().stream().filter(flag -> flag.getName().equalsIgnoreCase(args[0]) && cause.hasPermission(Permissions.setFlag(flag.getName()))).findFirst(),
				new RawArgumentData<>("Flag", CommandTreeNodeTypes.STRING.get().createNode(), 0, null, null),
				RawOptional.optional(),
				locale -> getExceptions(locale).getFlagNotPresent()
			),
			RawArgument.of(
				Boolean.class,
				(cause, args) -> values.stream(),
				(cause, args) -> args.length < 2 || !plugin.getAPI().getRegisteredFlags().containsKey(args[0]) ? Optional.empty() : Optional.ofNullable(BooleanUtils.toBooleanObject(args[1])),
				new RawArgumentData<>("Value", CommandTreeNodeTypes.BOOL.get().createNode(), 1, null, new RawRequiredArgs(new Integer[] {0}, null)),
				RawOptional.optional(),
				locale -> getCommand(locale).getFlag().getValueNotPresent()
			),
			RawArgument.of(
				String.class,
				(cause, args) -> plugin.getAPI().getRegisteredFlags().entrySet().stream().filter(entry -> entry.getKey().equals(args[0])).findFirst().map(entry -> entry.getValue().getSettings().getSources()).orElse(Stream.of("all")),
				(cause, args) -> args.length < 3 ? Optional.empty() : plugin.getAPI().getRegisteredFlags().entrySet().stream().filter(entry -> entry.getKey().equals(args[0])).findFirst().filter(entry -> entry.getValue().getSettings().isAllowArgs()).map(entry -> entry.getValue().getSettings().getSources().filter(source -> args[2] != null && source.equals(args[2])).findFirst().orElse("all")),
				new RawArgumentData<>("Source", CommandTreeNodeTypes.RESOURCE_LOCATION.get().createNode(), 2, null, new RawRequiredArgs(new Integer[] {0, 1}, null)),
				RawOptional.optional(),
				locale -> getCommand(locale).getFlag().getInvalidSource()
			),
			RawArgument.of(
				String.class,
				(cause, args) -> plugin.getAPI().getRegisteredFlags().entrySet().stream().filter(entry -> entry.getKey().equals(args[0])).findFirst().map(entry -> entry.getValue().getSettings().getTargets()).orElse(Stream.of("all")),
				(cause, args) -> args.length < 4 ? Optional.empty() : plugin.getAPI().getRegisteredFlags().entrySet().stream().filter(entry -> entry.getKey().equals(args[0])).findFirst().filter(entry -> entry.getValue().getSettings().isAllowArgs()).map(entry -> entry.getValue().getSettings().getTargets().filter(target -> args[3] != null && target.equals(args[3])).findFirst().orElse("all")),
				new RawArgumentData<>("Target", CommandTreeNodeTypes.RESOURCE_LOCATION.get().createNode(), 3, null, new RawRequiredArgs(new Integer[] {0, 1, 2}, null)),
				RawOptional.optional(),
				locale -> getCommand(locale).getFlag().getInvalidTarget()
			)
		);
	}

	private void setFlag(Region region, String flag, boolean value, String source, String target) {
		region.setFlag(flag, value, source, target);
		plugin.getAPI().saveRegion(region.getPrimaryParent());
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
								player.sendMessage(getCommand(player).getFlag().getNotPermittedFlag(flagName));
								return;
							}
							region.removeFlag(flagName);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendPaginationList(player, getCommand(player).getFlag().getTitle(), getCommand(player).getFlag().getPadding(), 10, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getRemove()));
				Component setTrue = Component.text("§7[§eTrue§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flagName))) {
								player.sendMessage(getCommand(player).getFlag().getNotPermittedFlag(flagName));
								return;
							}
							region.setFlag(flagName, true, null, null);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendPaginationList(player, getCommand(player).getFlag().getTitle(), getCommand(player).getFlag().getPadding(), 10, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getTrue()));
				Component setFalse = Component.text("§7[§eFalse§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flagName))) {
								player.sendMessage(getCommand(player).getFlag().getNotPermittedFlag(flagName));
								return;
							}
							region.setFlag(flagName, false, null, null);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendPaginationList(player, getCommand(player).getFlag().getTitle(), getCommand(player).getFlag().getPadding(), 10, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getFalse()));
				messages.add(Component.text("")
						.append(remove)
						.append(Component.text("   "))
						.append(region.getFlagResultWhithoutParrents(flagName, null, null) == Tristate.TRUE ? Component.text("§7[§aTrue§7]§r") : setTrue)
						.append(Component.text("   "))
						.append(region.getFlagResultWhithoutParrents(flagName, null, null) == Tristate.FALSE ? Component.text("§7[§aFalse§7]§r") : setFalse)
						.append(Component.text(" - "))
						.append(Component.text("§7[§b" + flagName.toString() + "§7]").
								hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getSuggestArgs()))
								.clickEvent(ClickEvent.suggestCommand("/rg flag " + flagName.toString() + " ")))
				);
			} else {
				Component setTrue = Component.text(region.getFlagResult(flagName, null, null) == Tristate.TRUE ? "§7[§2True§7]§r" : "§7[§6True§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flagName))) {
								player.sendMessage(getCommand(player).getFlag().getNotPermittedFlag(flagName));
								return;
							}
							region.setFlag(flagName, true, null, null);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendPaginationList(player, getCommand(player).getFlag().getTitle(), getCommand(player).getFlag().getPadding(), 10, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getTrue()));
				Component setFalse = Component.text(region.getFlagResult(flagName, null, null) == Tristate.FALSE ? "§7[§2False§7]§r" : "§7[§6False§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flagName))) {
								player.sendMessage(getCommand(player).getFlag().getNotPermittedFlag(flagName));
								return;
							}
							region.setFlag(flagName, false, null, null);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendPaginationList(player, getCommand(player).getFlag().getTitle(), getCommand(player).getFlag().getPadding(), 10, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getFalse()));
				messages.add(Component.text("")
						.append(Component.text("§7[§eRemove§7]§r"))
						.append(Component.text("   "))
						.append(setTrue)
						.append(Component.text("   "))
						.append(setFalse)
						.append(Component.text(" - "))
						.append(Component.text("§7[§3" + flagName + "§7]").
								hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getSuggestArgs()))
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
								player.sendMessage(getCommand(player).getFlag().getNotPermittedFlag(flag));
								return;
							}
							region.removeFlag(flag, flagValue);
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendPaginationList(player, getCommand(player).getFlag().getTitle(), getCommand(player).getFlag().getPadding(), 10, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getRemove()));
				Component setTrue = Component.text("§7[§eTrue§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flag.toString()))) {
								player.sendMessage(getCommand(player).getFlag().getNotPermittedFlag(flag));
								return;
							}
							region.setFlag(flag, true, flagValue.getSource(), flagValue.getTarget());
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendPaginationList(player, getCommand(player).getFlag().getTitle(), getCommand(player).getFlag().getPadding(), 10, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getTrue()));
				Component setFalse = Component.text("§7[§eFalse§7]§r")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(!player.hasPermission(Permissions.setFlag(flag))) {
								player.sendMessage(getCommand(player).getFlag().getNotPermittedFlag(flag));
								return;
							}
							region.setFlag(flag, false, flagValue.getSource(), flagValue.getTarget());
							plugin.getAPI().saveRegion(region.getPrimaryParent());
							sendPaginationList(player, getCommand(player).getFlag().getTitle(), getCommand(player).getFlag().getPadding(), 10, getFlagList(player, region));
						}))
						.hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getFalse()));
				Component display = Component.text("§d" + flag).hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getHoverValues(flagValue)));
				messages.add(Component.text("")
						.append(remove)
						.append(Component.text("   "))
						.append(flagValue.getValue() ? Component.text("§7[§aTrue§7]§r") : setTrue)
						.append(Component.text("   "))
						.append(!flagValue.getValue() ? Component.text("§7[§aFalse§7]§r") : setFalse)
						.append(Component.text(" - "))
						.append(Component.text("§7[").append(display).append(Component.text("§7]"))));
			} else {
				Component display = Component.text("§5" + flag).hoverEvent(HoverEvent.showText(getCommand(player).getFlag().getHover().getHoverValues(flagValue)));
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

}
