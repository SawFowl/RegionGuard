package sawfowl.regionguard.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionDeleteEvent;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class InfoCommand implements Command.Raw {

	private final RegionGuard plugin;
	private final SimpleDateFormat format;
	private List<CommandCompletion> empty = new ArrayList<>();
	public InfoCommand(RegionGuard plugin) {
		this.plugin = plugin;
		format = new SimpleDateFormat("d.MM.yyyy HH:mm:s");
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		Calendar calendar = Calendar.getInstance(player.locale());
		calendar.setTimeInMillis(region.getCreationTime());
		generateMessage(player, region, calendar);
		if(!region.isGlobal()) plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region, player, false, false);
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments) throws CommandException {
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.INFO);
	}

	@Override
	public Optional<Component> shortDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Region info."));
	}

	@Override
	public Optional<Component> extendedDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Region info."));
	}

	@Override
	public Component usage(CommandCause cause) {
		return Component.text("/rg info");
	}

	private void generateMessage(ServerPlayer player, Region region, Calendar calendar) {
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
			boolean regen = plugin.getConfig().regenAll() && !region.getParrent().isPresent();
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
								if(regen) region.regen(plugin.getConfig().asyncRegen(), plugin.getConfig().delayRegen());
								plugin.getAPI().deleteRegion(region);
								Optional<PlayerData> optPlayerData = plugin.getAPI().getPlayerData(player);
								if(optPlayerData.isPresent()) {
									optPlayerData.get().getClaimed().setRegions(plugin.getAPI().getClaimedRegions(player) - 1);
									optPlayerData.get().getClaimed().setBlocks(plugin.getAPI().getClaimedBlocks(player) - region.getCuboid().getSize());
									plugin.getPlayersDataWork().savePlayerData(player, optPlayerData.get());
								} else {
									PlayerData playerData = new PlayerData(
										new PlayerLimits(plugin.getAPI().getLimitBlocks(player), plugin.getAPI().getLimitClaims(player), plugin.getAPI().getLimitSubdivisions(player), plugin.getAPI().getLimitMembers(player)), 
										new ClaimedByPlayer(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player))
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

	private String removeDecor(Component component) {
		Component toClear = Component.empty().append(component);
		toClear.decorations().clear();
		return LegacyComponentSerializer.legacyAmpersand().serialize(toClear);
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
