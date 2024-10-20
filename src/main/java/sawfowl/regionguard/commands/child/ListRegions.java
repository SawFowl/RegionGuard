package sawfowl.regionguard.commands.child;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.commands.raw.arguments.RawArguments;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.commandpack.api.commands.raw.arguments.RawBasicArgumentData;
import sawfowl.commandpack.api.commands.raw.arguments.RawOptional;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionCommandTeleportEvent;
import sawfowl.regionguard.api.events.RegionDeleteEvent;
import sawfowl.regionguard.commands.abstractcommands.AbstractCommand;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Delete;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Info;

public class ListRegions extends AbstractCommand {

	private Cause cause;
	public ListRegions(RegionGuard plugin) {
		super(plugin);
		cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
	}

	@Override
	public void process(CommandCause cause, Audience audience, Locale locale, boolean isPlayer, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Optional<GameProfile> optProfile = args.get(GameProfile.class, 0);
		if(!isPlayer && !optProfile.isPresent()) exception(getExceptions(locale).getPlayerNotPresent());
		List<Region> regions = optProfile.isPresent() ? plugin.getAPI().getPlayerRegions(optProfile.get().uniqueId()) : plugin.getAPI().getPlayerRegions((ServerPlayer) audience);
		if(regions.size() == 0) exception(getCommand(locale).getList().getEmpty(optProfile.isPresent()));
		List<Component> list = new ArrayList<>();
		for(Region region : regions) {
			Component tp = region.getWorld().isPresent() && ((isPlayer && cause.hasPermission(Permissions.TELEPORT) && region.isTrusted((ServerPlayer) audience)) || (isPlayer && cause.hasPermission(Permissions.STAFF_LIST))) ? Component.text("§7[§bTP§7]").clickEvent(SpongeComponents.executeCallback(callback -> {
				if(isPlayer) teleport((ServerPlayer) audience, region, true);
			})) : Component.empty();
			Component positions = Component.text((isPlayer ? "§6" : "") + region.getCuboid().getMin() + " ➢ " + region.getCuboid().getMax());
			Component uuidOrName = (region.getPlainName(locale).isPresent() ? region.getName(locale) : Component.text((isPlayer ? "§2" : "") + "<" + region.getUniqueId() + ">").clickEvent(SpongeComponents.executeCallback(callback -> {
				if(!isPlayer) return;
				Calendar calendar = Calendar.getInstance(locale);
				calendar.setTimeInMillis(region.getCreationTime());
				generateInfoMessage((ServerPlayer) audience, region, calendar);
			})));
			list.add(Component.text().append(tp).append(Component.text(" ")).append(positions).append(Component.text(" ")).append(uuidOrName).build());
		}
		sendPaginationList(audience, getCommand(locale).getList().getTitle(optProfile.isPresent() ? optProfile.get().name().orElse(optProfile.get().examinableName()) : ((ServerPlayer) audience).name()), getCommand(locale).getList().getPadding(), 10, list);
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getList().getDescription();
	}

	@Override
	public String permission() {
		return null;
	}

	@Override
	public String command() {
		return "list";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy(cause.first(ServerPlayer.class).isPresent() ? "&6/rg list &7[Player]&f - " : "&6/rg list &7<Player>&f - ").clickEvent(ClickEvent.runCommand("/rg list ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return Arrays.asList(RawArguments.createGameProfile(RawBasicArgumentData.createGameProfile(0, null, null), RawOptional.player(), locale -> getExceptions(locale).getPlayerNotPresent()));
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.LIST) || cause.hasPermission(Permissions.STAFF_LIST);
	}

	private void teleport(ServerPlayer player, Region region, boolean repeat) {
		ServerWorld world = region.getWorld().get();
		Vector3i vector3i = world.highestPositionAt(region.getCuboid().getCenter().toInt());
		boolean safePos = player.gameMode().get() == GameModes.CREATIVE.get() || player.gameMode().get() == GameModes.SPECTATOR.get() || (world.block(vector3i.add(0, 1, 0)).type() == BlockTypes.AIR.get() && world.block(vector3i.sub(0, 1, 0)).type() != BlockTypes.AIR.get() && world.block(vector3i.sub(0, 1, 0)).type() != BlockTypes.LAVA.get());
		if(safePos) {
			teleport(player, region, world, Vector3d.from(vector3i.x(), vector3i.y(), vector3i.z()));
			if(repeat) Sponge.server().scheduler().submit(Task.builder().plugin(plugin.getPluginContainer()).delay(Ticks.single()).execute(() -> {
				teleport(player, region, false);
			}).build());
		} else {
			player.sendMessage(getCommand(player).getList().getTeleportNotSafe().clickEvent(SpongeComponents.executeCallback(callback2 -> {
				teleport(player, region, world, Vector3d.from(vector3i.x(), vector3i.y(), vector3i.z()));
				if(repeat) Sponge.server().scheduler().submit(Task.builder().plugin(plugin.getPluginContainer()).delay(Ticks.single()).execute(() -> {
					teleport(player, region, false);
				}).build());
			})));
		}
	
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

			@SuppressWarnings("unchecked")
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
		Component padding = getInfo(player).getPadding();
		Component headerPart = Component.empty();
		for(Component component : Collections.nCopies(12, padding)) headerPart = headerPart.append(component);
		messages.add(headerPart.append(getInfo(player).getHeader().append(headerPart)));
		if(!region.isGlobal() && (player.hasPermission(Permissions.STAFF_DELETE) || region.isCurrentTrustType(player, TrustTypes.OWNER))) {
			Component claim = Component.empty();
			Component arena = Component.empty();
			Component admin = Component.empty();
			Component flags = null;
			boolean regen = plugin.getConfig().getRegenerateTerritory().isAllPlayers() && !region.getParrent().isPresent();
			if(regen) player.sendMessage(getDelete(player).getRegen());
			Component delete = getCommand(player).getInfo().getButtons().getDelete().clickEvent(SpongeComponents.executeCallback(cause -> {
				player.sendMessage(getDelete(player).getConfirmRequest().clickEvent(SpongeComponents.executeCallback(cause2 -> {
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
							@SuppressWarnings("unchecked")
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
						event.setMessage(getDelete(player).getSuccesChild());
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
							@SuppressWarnings("unchecked")
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
						event.setMessage(region.containsChilds() ? getDelete(player).getSuccesWhithChilds() : getDelete(player).getSuccess());
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
				flags = getInfo(player).getButtons().getFlags().clickEvent(ClickEvent.runCommand("/rg flag"));
				messages.add(Component.empty().append(delete).append(flags));
			}
		} else {
			messages.add(getInfo(player).getButtons().getFlags().clickEvent(ClickEvent.runCommand("/rg flag")));
		}
		messages.add(Component.text(" "));
		messages.add(getInfo(player).getUUID(region));
		if(region.getPlainName(player.locale()).isPresent()) messages.add(getInfo(player).getName(region.getName(player.locale())));
		messages.add(getInfo(player).getType(region));
		messages.add(getInfo(player).getCreated(plugin.getLocales().getLocale(player).getTimeFormat().format(calendar.getTime())));
		messages.add(Component.text("  "));
		messages.add(getInfo(player).getOwner(region));
		messages.add(getInfo(player).getOwnerUUID(region));
		if(!region.isGlobal()) {
			messages.add(getInfo(player).getMembers(region));
			messages.add(Component.text("   "));
			messages.add(getInfo(player).getMin(region));
			messages.add(getInfo(player).getMax(region));
			messages.add(getInfo(player).getSelector(region));
		}
		Component footer = Component.empty();
		for(Component component : Collections.nCopies(TextUtils.clearDecorations(messages.get(0)).length() - 2, padding)) footer = footer.append(component);
		messages.add(footer);
		sendMessage(player, messages);
	}

	private void sendMessage(ServerPlayer player, List<Component> components) {
		components.forEach(component -> {
			player.sendMessage(component);
		});
	}

	private Info getInfo(ServerPlayer player) {
		return getCommand(player).getInfo();
	}

	private Delete getDelete(ServerPlayer player) {
		return getCommand(player).getDelete();
	}

}
