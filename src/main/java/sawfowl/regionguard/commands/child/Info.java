package sawfowl.regionguard.commands.child;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionDeleteEvent;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.Placeholders;

public class Info extends AbstractPlayerCommand {

	private final SimpleDateFormat format;
	public Info(RegionGuard plugin) {
		super(plugin);
		format = new SimpleDateFormat("d.MM.yyyy HH:mm:s");
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, String[] args, Mutable arguments) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		Calendar calendar = Calendar.getInstance(locale);
		calendar.setTimeInMillis(region.getCreationTime());
		generateMessage(src, region, calendar);
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_INFO);
	}

	@Override
	public String permission() {
		return Permissions.INFO;
	}

	@Override
	public String command() {
		return "info";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg info &f - ").clickEvent(ClickEvent.runCommand("/rg info ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return null;
	}

	private void generateMessage(ServerPlayer player, Region region, Calendar calendar) {
		if(region == null || calendar == null) return;
		if(!region.isGlobal()) plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region, player, false, false);
		List<Component> messages = new ArrayList<Component>();
		Component padding = plugin.getLocales().getComponent(player.locale(), LocalesPaths.PADDING);
		Component headerPart = Component.empty();
		for(Component component : Collections.nCopies(12, padding)) headerPart = headerPart.append(component);
		messages.add(headerPart.append(plugin.getLocales().getComponent(player.locale(), LocalesPaths.COMMAND_INFO_HEADER).append(headerPart)));
		if(!region.isGlobal() && (player.hasPermission(Permissions.STAFF_DELETE) || region.isCurrentTrustType(player, TrustTypes.OWNER))) {
			Component claim = Component.empty();
			Component arena = Component.empty();
			Component admin = Component.empty();
			Component flags = null;
			boolean regen = plugin.getConfig().getRegenerateTerritory().isAllPlayers() && !region.getParrent().isPresent();
			if(regen) player.sendMessage(plugin.getLocales().getComponent(player.locale(), LocalesPaths.COMMAND_DELETE_REGEN));
			Component delete = plugin.getLocales().getComponent(player.locale(), LocalesPaths.COMMAND_INFO_DELETE).clickEvent(SpongeComponents.executeCallback(cause -> {
				player.sendMessage(plugin.getLocales().getComponent(player.locale(), LocalesPaths.COMMAND_DELETE_CONFIRMATION_REQUEST).clickEvent(SpongeComponents.executeCallback(cause2 -> {
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
						event.setMessage(plugin.getLocales().getComponent(player.locale(), LocalesPaths.COMMAND_DELETE_CHILD_DELETED));
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
						event.setMessage(region.containsChilds() ? plugin.getLocales().getComponent(player.locale(), LocalesPaths.COMMAND_DELETE_DELETED_MAIN_AND_CHILDS) : plugin.getLocales().getComponent(player.locale(), LocalesPaths.COMMAND_DELETE_DELETED));
						Sponge.eventManager().post(event);
						if(!event.isCancelled()) {
							if(region.getType() != RegionTypes.UNSET) {
								region.setRegionType(RegionTypes.UNSET);
								if(regen) region.regen(plugin.getConfig().getRegenerateTerritory().isAsync(), plugin.getConfig().getRegenerateTerritory().getDelay());
								plugin.getAPI().deleteRegion(region);
								Optional<PlayerData> optPlayerData = plugin.getAPI().getPlayerData(player);
								if(optPlayerData.isPresent()) {
									optPlayerData.get().getClaimed().setRegions(plugin.getAPI().getClaimedRegions(player) - 1);
									optPlayerData.get().getClaimed().setBlocks(plugin.getAPI().getClaimedBlocks(player) - region.getCuboid().getSize());
									plugin.getPlayersDataWork().savePlayerData(player, optPlayerData.get());
								} else {
									PlayerData playerData = PlayerData.of(
										PlayerLimits.of(plugin.getAPI().getLimitBlocks(player), plugin.getAPI().getLimitClaims(player), plugin.getAPI().getLimitSubdivisions(player), plugin.getAPI().getLimitMembers(player)), 
										ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player))
									);
									plugin.getPlayersDataWork().savePlayerData(player, playerData);
								}
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
					generateMessage(player, region, calendar);
				}));
				arena = region.isArena() ? Component.text("§7[§2Arena§7]  ") : Component.text("§7[§aArena§7]  ").clickEvent(SpongeComponents.executeCallback(cause -> {
					region.setRegionType(RegionTypes.ARENA);
					plugin.getAPI().saveRegion(region);
					generateMessage(player, region, calendar);
				}));
				admin = region.isAdmin() ? Component.text("§7[§4Admin§7]") : Component.text("§7[§cAdmin§7]").clickEvent(SpongeComponents.executeCallback(cause -> {
					region.setRegionType(RegionTypes.ADMIN);
					plugin.getAPI().saveRegion(region);
					generateMessage(player, region, calendar);
				}));
				messages.add(Component.empty().append(delete).append(claim).append(arena).append(admin));
			} else {
				flags = plugin.getLocales().getComponent(player.locale(), LocalesPaths.COMMAND_INFO_SEE_FLAGS).clickEvent(ClickEvent.runCommand("/rg flag"));
				messages.add(Component.empty().append(delete).append(flags));
			}
		} else {
			messages.add(plugin.getLocales().getComponent(player.locale(), LocalesPaths.COMMAND_INFO_SEE_FLAGS).clickEvent(ClickEvent.runCommand("/rg flag")));
		}
		messages.add(Component.text(" "));
		messages.add(getText(player.locale(), LocalesPaths.COMMAND_INFO_REGION_UUID).replace(Placeholders.UUID, region.getUniqueId()).get());
		if(region.getPlainName(player.locale()).isPresent()) messages.add(getText(player.locale(), LocalesPaths.COMMAND_INFO_REGION_NAME).replace(Placeholders.NAME, region.getName(player.locale())).get());
		messages.add(getText(player.locale(), LocalesPaths.COMMAND_INFO_REGION_TYPE).replace(Placeholders.TYPE, region.getType()).get());
		messages.add(getText(player.locale(), LocalesPaths.COMMAND_INFO_CREATED).replace(Placeholders.DATE, format.format(calendar.getTime())).get());
		messages.add(Component.text("  "));
		messages.add(getText(player.locale(), LocalesPaths.COMMAND_INFO_OWNER).replace(Placeholders.OWNER, region.getOwnerName()).get());
		messages.add(getText(player.locale(), LocalesPaths.COMMAND_INFO_OWNER_UUID).replace(Placeholders.UUID, region.getOwnerUUID()).get());
		if(!region.isGlobal()) {
			messages.add(getText(player.locale(), LocalesPaths.COMMAND_INFO_MEMBERS).replace(Placeholders.SIZE, region.getTotalMembers()).get());
			messages.add(Component.text("   "));
			messages.add(getText(player.locale(), LocalesPaths.COMMAND_INFO_MIN_POS).replace(Placeholders.POS, region.getCuboid().getMin()).get());
			messages.add(getText(player.locale(), LocalesPaths.COMMAND_INFO_MAX_POS).replace(Placeholders.POS, region.getCuboid().getMax()).get());
			messages.add(getText(player.locale(), LocalesPaths.COMMAND_INFO_SELECTION_TYPE).replace(Placeholders.TYPE, region.getCuboid().getSelectorType()).get());
		}
		Component footer = Component.empty();
		for(Component component : Collections.nCopies(headerWithoutDecorLength(messages.get(0)) - 2, padding)) footer = footer.append(component);
		messages.add(footer);
		sendMessage(player, messages);
	}

	private void sendMessage(ServerPlayer player, List<Component> components) {
		components.forEach(component -> {
			player.sendMessage(component);
		});
	}

	private int headerWithoutDecorLength(Component component) {
		return TextUtils.clearDecorations(component).length();
	}

}
