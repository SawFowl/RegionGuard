package sawfowl.regionguard.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionCommandTeleportEvent;
import sawfowl.regionguard.api.events.RegionDeleteEvent;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class ListCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	private List<CommandCompletion> empty = new ArrayList<>();
	private Cause cause;
	private final SimpleDateFormat format;
	public ListCommand(RegionGuard plugin) {
		this.plugin = plugin;
		cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
		format = new SimpleDateFormat("d.MM.yyyy HH:mm:s");
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		boolean otherPlayer = !args.isEmpty() && !args.get(0).equals(player.name()) && player.hasPermission(Permissions.STAFF_LIST) && Sponge.server().player(args.get(0)).isPresent();
		List<Region> regions = otherPlayer ? plugin.getAPI().getPlayerRegions(Sponge.server().player(args.get(0)).get()) : plugin.getAPI().getPlayerRegions(player);
		if(regions.size() == 0) throw new CommandException(Component.text(otherPlayer ? "У игрока нет регионов" : "У вас нет регионов"));
		List<Component> list = new ArrayList<>();
		for(Region region : regions) {
			Component tp = region.getServerWorld().isPresent() && (player.hasPermission(Permissions.TELEPORT) || player.hasPermission(Permissions.STAFF_LIST)) ? Component.text("§7[§bTP§7]").clickEvent(SpongeComponents.executeCallback(callback -> {
				ServerWorld world = region.getServerWorld().get();
				Vector3i vector3i = world.highestPositionAt(region.getCuboid().getCenter().toInt());
				boolean safePos = player.gameMode().get() == GameModes.CREATIVE.get() || player.gameMode().get() == GameModes.SPECTATOR.get() || (world.block(vector3i.add(0, 1, 0)).type() == BlockTypes.AIR.get() && world.block(vector3i.sub(0, 1, 0)).type() != BlockTypes.AIR.get() && world.block(vector3i.sub(0, 1, 0)).type() != BlockTypes.LAVA.get());
				if(safePos) {
					teleport(player, region, world, Vector3d.from(vector3i.x(), vector3i.y(), vector3i.z()));
				} else {
					player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_LIST_EXCEPTION_NOTSAFE).clickEvent(SpongeComponents.executeCallback(callback2 -> {
						teleport(player, region, world, Vector3d.from(vector3i.x(), vector3i.y(), vector3i.z()));
					})));
				}
			})) : Component.empty();
			Component positions = Component.text("§6" + region.getCuboid().getMin() + " ➢ " + region.getCuboid().getMax());
			Component uuidOrName = (region.getName(player.locale()).isPresent() ? region.asComponent(player.locale()) : Component.text("§2<" + region.getUniqueId() + ">").clickEvent(SpongeComponents.executeCallback(callback -> {
				Calendar calendar = Calendar.getInstance(player.locale());
				calendar.setTimeInMillis(region.getCreationTime());
				generateInfoMessage(player, region, calendar);
			})));
			list.add(Component.text().append(tp).append(Component.text(" ")).append(positions).append(Component.text(" ")).append(uuidOrName).build());
		}
		sendRegionsList(player, player.locale(), list, 10, otherPlayer ? args.get(0) : player.name());
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		if(cause.hasPermission(Permissions.STAFF_LIST)) {
			if(args.isEmpty()) return Sponge.server().onlinePlayers().stream().map(player -> (CommandCompletion.of(player.name()))).collect(Collectors.toList());
		}
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.LIST) || cause.hasPermission(Permissions.STAFF_LIST);
	}

	private void sendRegionsList(Audience audience, Locale locale, List<Component> messages, int lines, String name) {
		PaginationList.builder()
		.contents(messages)
		.title(plugin.getLocales().getTextWithReplaced(locale, ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.PLAYER), Arrays.asList(name)), LocalesPaths.COMMAND_LIST_TITLE))
		.padding(plugin.getLocales().getText(locale, LocalesPaths.PADDING))
		.linesPerPage(lines)
		.sendTo(audience);
	}

	private void teleport(ServerPlayer player, Region region, ServerWorld world, Vector3d location) {
		Vector3d playerPos = player.position();
		class RegionTPEvent extends AbstractEvent implements RegionCommandTeleportEvent {
			boolean cancelled = false;
			Component message;
			Vector3d destination;
			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public ServerPlayer getPlayer() {
				return player;
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public Region getOriginalDestinationRegion() {
				return plugin.getAPI().findRegion(world, location.toInt());
			}

			@Override
			public void setMessage(Component message) {
				this.message = message;
			}

			@Override
			public Optional<Component> getMessage() {
				return Optional.ofNullable(message);
			}

			@Override
			public boolean isCancelled() {
				return cancelled;
			}

			@Override
			public void setCancelled(boolean cancel) {
				cancelled = cancel;
			}

			@Override
			public Region from() {
				return plugin.getAPI().findRegion(world, player.blockPosition());
			}

			@Override
			public Vector3d getOriginalLocation() {
				return playerPos;
			}

			@Override
			public Vector3d getOriginalDestinationLocation() {
				return location;
			}

			@Override
			public Vector3d getDestinationLocation() {
				return destination;
			}

			@Override
			public void setDestination(Vector3d location) {
				if(location != null) destination = location;
			}
		}
		RegionCommandTeleportEvent rgEvent = new RegionTPEvent();
		rgEvent.setDestination(location);
		Sponge.eventManager().post(rgEvent);
		if(!rgEvent.isCancelled()) player.transferToWorld(world, rgEvent.getDestinationLocation());
		if(rgEvent.getMessage().isPresent()) player.sendMessage(rgEvent.getMessage().get());
	}

	private void generateInfoMessage(ServerPlayer player, Region region, Calendar calendar) {
		if(region == null || calendar == null) return;
		List<Component> messages = new ArrayList<Component>();
		Component padding = plugin.getLocales().getText(player.locale(), LocalesPaths.PADDING);
		Component headerPart = addDecor(String.join("", Collections.nCopies(13, removeDecor(padding))), getDecor(padding));
		messages.add(headerPart.append(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_INFO_HEADER).append(headerPart)));
		if(!region.isGlobal() && (player.hasPermission(Permissions.STAFF_DELETE) || region.isCurrentTrustType(player, TrustTypes.OWNER))) {
			Component claim = Component.empty();
			Component arena = Component.empty();
			Component admin = Component.empty();
			Component flags = null;
			boolean regen = plugin.getConfig().getRegenerateTerritory().isAllPlayers() && !region.getParrent().isPresent();
			if(regen) player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_DELETE_REGEN));
			Component delete = plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_INFO_DELETE).clickEvent(SpongeComponents.executeCallback(cause -> {
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_DELETE_CONFIRMATION_REQUEST).clickEvent(SpongeComponents.executeCallback(cause2 -> {
					if(region.getParrent().isPresent()) {
						Region parrent = region.getParrent().get();
						RegionDeleteEvent event = new RegionDeleteEvent() {
							boolean canceled;
							Component send;
							@Override
							public void setCancelled(boolean cancel) {
								canceled = cancel;
							}
							@Override
							public boolean isCancelled() {
								return canceled;
							}
							@Override
							public void setMessage(Component message) {
								send = message;
							}
							@Override
							public Optional<Component> getMessage() {
								return Optional.ofNullable(send);
							}
							@Override
							public Region getRegion() {
								return region;
							}
							@Override
							public ServerPlayer getPlayer() {
								return player;
							}
							@Override
							public Cause cause() {
								return cause2.cause();
							}
							@Override
							public Object getSource() {
								return player;
							}
						};
						event.setMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_DELETE_CHILD_DELETED));
						Sponge.eventManager().post(event);
						if(!event.isCancelled()) {
							parrent.removeChild(region);
							plugin.getAPI().saveRegion(parrent.getPrimaryParent());
							if(event.getMessage().isPresent()) player.sendMessage(event.getMessage().get());
						}
					} else {
						RegionDeleteEvent event = new RegionDeleteEvent() {
							boolean canceled;
							Component send;
							@Override
							public void setCancelled(boolean cancel) {
								canceled = cancel;
							}
							@Override
							public boolean isCancelled() {
								return canceled;
							}
							@Override
							public void setMessage(Component message) {
								send = message;
							}
							@Override
							public Optional<Component> getMessage() {
								return Optional.ofNullable(send);
							}
							@Override
							public Region getRegion() {
								return region;
							}
							@Override
							public ServerPlayer getPlayer() {
								return player;
							}
							@Override
							public Cause cause() {
								return cause2.cause();
							}
							@Override
							public Object getSource() {
								return player;
							}
						};
						event.setMessage(region.containsChilds() ? plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_DELETE_DELETED_MAIN_AND_CHILDS) : plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_DELETE_DELETED));
						Sponge.eventManager().post(event);
						if(!event.isCancelled()) {
							if(region.getType() != RegionTypes.UNSET) {
								region.setRegionType(RegionTypes.UNSET);
								if(regen) region.regen(plugin.getConfig().getRegenerateTerritory().isAsync(), plugin.getConfig().getRegenerateTerritory().getDelay());
								plugin.getAPI().deleteRegion(region);
							}
							if(event.getMessage().isPresent()) player.sendMessage(event.getMessage().get());
						}
					}
					if(plugin.getAPI().getWorldEditCUIAPI() != null) plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(player);
					})));
			})).append(Component.text("  "));
			if(player.hasPermission(Permissions.STAFF_SET_REGION_TYPE) && !region.isSubdivision()) {
				claim = region.isBasicClaim() ? Component.text("§7[§6Claim§7]  ") : Component.text("§7[§eClaim§7]  ").clickEvent(SpongeComponents.executeCallback(cause -> {
					region.setRegionType(RegionTypes.CLAIM);
					plugin.getAPI().saveRegion(region);
					generateInfoMessage(player, region, calendar);
				}));
				arena = region.isArena() ? Component.text("§7[§2Arena§7]  ") : Component.text("§7[§aArena§7]  ").clickEvent(SpongeComponents.executeCallback(cause -> {
					region.setRegionType(RegionTypes.ARENA);
					plugin.getAPI().saveRegion(region);
					generateInfoMessage(player, region, calendar);
				}));
				admin = region.isAdmin() ? Component.text("§7[§4Admin§7]") : Component.text("§7[§cAdmin§7]").clickEvent(SpongeComponents.executeCallback(cause -> {
					region.setRegionType(RegionTypes.ADMIN);
					plugin.getAPI().saveRegion(region);
					generateInfoMessage(player, region, calendar);
				}));
				messages.add(Component.empty().append(delete).append(claim).append(arena).append(admin));
			} else {
				flags = plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_INFO_SEE_FLAGS).clickEvent(ClickEvent.runCommand("/rg flag"));
				messages.add(Component.empty().append(delete).append(flags));
			}
		} else {
			messages.add(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_INFO_SEE_FLAGS).clickEvent(ClickEvent.runCommand("/rg flag")));
		}
		messages.add(Component.text(" "));
		messages.add(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.UUID), Arrays.asList(region.getUniqueId())), LocalesPaths.COMMAND_INFO_REGION_UUID));
		if(region.getName(player.locale()).isPresent()) messages.add(plugin.getLocales().getTextReplaced(player.locale(), ReplaceUtil.replaceMapComponents(Arrays.asList(ReplaceUtil.Keys.NAME), Arrays.asList(region.asComponent(player.locale()))), LocalesPaths.COMMAND_INFO_REGION_NAME));
		messages.add(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.TYPE), Arrays.asList(region.getType())), LocalesPaths.COMMAND_INFO_REGION_TYPE));
		messages.add(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.DATE), Arrays.asList(format.format(calendar.getTime()))), LocalesPaths.COMMAND_INFO_CREATED));
		messages.add(Component.text("  "));
		messages.add(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.OWNER), Arrays.asList(region.getOwnerName())), LocalesPaths.COMMAND_INFO_OWNER));
		messages.add(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.UUID), Arrays.asList(region.getOwnerUUID())), LocalesPaths.COMMAND_INFO_OWNER_UUID));
		if(!region.isGlobal()) {
			messages.add(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE), Arrays.asList(region.getTotalMembers())), LocalesPaths.COMMAND_INFO_MEMBERS));
			messages.add(Component.text("   "));
			messages.add(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.POS), Arrays.asList(region.getCuboid().getMin())), LocalesPaths.COMMAND_INFO_MIN_POS));
			messages.add(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.POS), Arrays.asList(region.getCuboid().getMax())), LocalesPaths.COMMAND_INFO_MAX_POS));
			messages.add(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.TYPE), Arrays.asList(region.getCuboid().getSelectorType())), LocalesPaths.COMMAND_INFO_SELECTION_TYPE));
		}
		String footer = String.join("", Collections.nCopies(headerWithoutDecorLength(messages.get(0)) - 8, removeDecor(padding)));
		messages.add(addDecor(footer, getDecor(padding)));
		sendMessage(player, messages);
	}

	private void sendMessage(ServerPlayer player, List<Component> components) {
		components.forEach(component -> {
			player.sendMessage(component);
		});
	}

	private int headerWithoutDecorLength(Component component) {
		return removeDecor(component).length();
	}

	private Component addDecor(String string, Map<TextDecoration, State> decor) {
		Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(string);
		component.decorations().putAll(decor);
		return component;
	}

	private Map<TextDecoration, State> getDecor(Component component) {
		return component.decorations();
	}

}
