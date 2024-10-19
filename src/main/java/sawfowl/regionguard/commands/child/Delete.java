package sawfowl.regionguard.commands.child;

import java.util.Arrays;
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
import sawfowl.commandpack.api.commands.raw.arguments.RawArguments;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.commandpack.api.commands.raw.arguments.RawBasicArgumentData;
import sawfowl.commandpack.api.commands.raw.arguments.RawOptional;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionDeleteEvent;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class Delete extends AbstractPlayerCommand {

	private List<String> regen;
	public Delete(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(getExceptions(locale).getRegionNotFound());
		if(!region.getOwnerUUID().equals(src.uniqueId()) && !src.hasPermission(Permissions.STAFF_DELETE)) exception(getExceptions(locale).getNotOwner());
		boolean regen = !region.getParrent().isPresent() && (src.hasPermission(Permissions.STAFF_DELETE) ? (args.getString(0).isPresent()) && plugin.getConfig().getRegenerateTerritory().isStaff() : plugin.getConfig().getRegenerateTerritory().isAllPlayers());
		if(regen) src.sendMessage(getCommand(locale).getDelete().getRegen());
		src.sendMessage(getCommand(locale).getDelete().getConfirmRequest().clickEvent(SpongeComponents.executeCallback(messageCause -> {
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
						return src;
					}
					@Override
					public Cause cause() {
						return messageCause.cause();
					}
					@Override
					public Object getSource() {
						return src;
					}
				};
				event.setMessage(getCommand(locale).getDelete().getSuccesChild());
				Sponge.eventManager().post(event);
				if(!event.isCancelled()) {
					parrent.removeChild(region);
					plugin.getAPI().saveRegion(parrent.getPrimaryParent());
				}
				if(event.getMessage().isPresent()) src.sendMessage(event.getMessage().get());
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
						return src;
					}
					@Override
					public Cause cause() {
						return messageCause.cause();
					}
					@Override
					public Object getSource() {
						return src;
					}
				};
				event.setMessage(region.containsChilds() ? getCommand(locale).getDelete().getSuccesWhithChilds() : getCommand(locale).getDelete().getSuccess());
				Sponge.eventManager().post(event);
				if(!event.isCancelled()) {
					if(region.getType() != RegionTypes.UNSET) {
						region.setRegionType(RegionTypes.UNSET);
						if(regen) region.regen(plugin.getConfig().getRegenerateTerritory().isAsync(), plugin.getConfig().getRegenerateTerritory().getDelay());
						plugin.getAPI().deleteRegion(region);
					}
					if(event.getMessage().isPresent()) src.sendMessage(event.getMessage().get());
				}
			}
		plugin.getAPI().getWorldEditCUIAPI().revertVisuals(src, region.getUniqueId());
		})));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getDelete().getDescription();
	}

	@Override
	public String permission() {
		return Permissions.DELETE;
	}

	@Override
	public String command() {
		return "delete";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg delete&f - ").clickEvent(ClickEvent.runCommand("/rg delete")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		if(regen == null) regen = Arrays.asList("-regen", "-r");
		return Arrays.asList(RawArguments.createStringArgument(regen, new RawBasicArgumentData<String>(null, "Regen", 0, Permissions.STAFF_DELETE, null), RawOptional.optional(), null));
	}

}
