package sawfowl.regionguard.commands.child;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;

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
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class SetOwner extends AbstractPlayerCommand {

	public SetOwner(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition()).getPrimaryParent();
		if(region.isGlobal()) exception(getExceptions(locale).getRegionNotFound());
		if(region.isAdmin() && src.hasPermission(Permissions.STAFF_TRUST)) exception(getSetOwner(locale).getAdminClaim());
		GameProfile newOwner = args.get(GameProfile.class, 0).get();
		if(src.uniqueId().equals(newOwner.uniqueId()) && region.isCurrentTrustType(src, TrustTypes.OWNER)) exception(getExceptions(locale).getTargetSelf());
		if(src.hasPermission(Permissions.STAFF_TRUST)) {
			if(region.getOwnerUUID().equals(newOwner.uniqueId())) exception(getSetOwner(locale).getAlreadyOwnerStaff(newOwner));
			src.sendMessage(getSetOwner(locale).getConfirmRequest(newOwner).clickEvent(SpongeComponents.executeCallback(messageCause -> {
				setOwner(src, newOwner, region, true);
			})));
			return;
		}
		if(!region.isCurrentTrustType(src, TrustTypes.OWNER)) exception(getSetOwner(locale).getOnlyOwner());
		src.sendMessage(getSetOwner(locale).getConfirmRequest(newOwner).clickEvent(SpongeComponents.executeCallback(messageCause -> {
			setOwner(src, newOwner, region, false);
		})));
	
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getSetOwner(locale).getDescription();
	}

	@Override
	public String permission() {
		return Permissions.TRUST;
	}

	@Override
	public String command() {
		return "setowner";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg setowner &7<Player>&f - ").clickEvent(ClickEvent.suggestCommand("/rg setowner ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return Arrays.asList(
			RawArguments.createGameProfile(RawBasicArgumentData.createGameProfile(0, null, null), RawOptional.notOptional(), locale -> getExceptions(locale).getPlayerNotPresent())
		);
	}

	private void setOwner(ServerPlayer player, GameProfile newOwner, Region region, boolean staff) {
		if(staff) {
			if(!region.getOwnerUUID().equals(new UUID(0, 0)) && region.getOwner().isPresent()) {
				Component message = getSetOwner(region.getOwner().get()).getSuccessFromStaff(region);
				region.setTrustType(region.getOwnerUUID(), TrustTypes.MANAGER);
				for(Region child: region.getAllChilds()) child.setTrustType(region.getOwnerUUID(), TrustTypes.MANAGER);
				region.getOwner().get().sendMessage(message);
				message = null;
			}
		} else {
			for(Region child: region.getAllChilds()) child.setTrustType(player, TrustTypes.MANAGER);
			region.setTrustType(player, TrustTypes.MANAGER);
		}
		region.setOwner(newOwner);
		for(Region child: region.getAllChilds()) child.setOwner(newOwner);
		player.sendMessage(getSetOwner(player).getSuccess(region, newOwner));
		Sponge.server().player(newOwner.uniqueId()).ifPresent(owner -> {
			owner.sendMessage(getSetOwner(owner).getSuccessNewOwner(region, player.profile()));
		});
		plugin.getAPI().saveRegion(region.getPrimaryParent());
	}

	private sawfowl.regionguard.configure.locales.abstractlocale.Command.SetOwner getSetOwner(Locale locale) {
		return getCommand(locale).getSetOwner();
	}

	private sawfowl.regionguard.configure.locales.abstractlocale.Command.SetOwner getSetOwner(ServerPlayer player) {
		return getSetOwner(player.locale());
	}

}
