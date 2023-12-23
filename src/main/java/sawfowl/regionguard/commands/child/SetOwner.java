package sawfowl.regionguard.commands.child;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class SetOwner extends AbstractPlayerCommand {

	public SetOwner(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, String[] args, Mutable arguments) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition()).getPrimaryParent();
		if(region.isGlobal()) throw new CommandException(plugin.getLocales().getText(locale, LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND));
		if(region.isAdmin() && src.hasPermission(Permissions.STAFF_TRUST)) throw new CommandException(plugin.getLocales().getText(locale, LocalesPaths.COMMAND_SETOWNER_EXCEPTION_ADMIN));
		GameProfile newOwner = getArgument(GameProfile.class, args, 0).get();
		if(src.uniqueId().equals(newOwner.uniqueId()) && region.isCurrentTrustType(src, TrustTypes.OWNER)) throw new CommandException(plugin.getLocales().getText(locale, LocalesPaths.COMMAND_SETOWNER_EXCEPTION_OWNER_TARGET_SELF));
		if(src.hasPermission(Permissions.STAFF_TRUST)) {
			if(region.getOwnerUUID().equals(newOwner.uniqueId())) throw new CommandException(plugin.getLocales().getTextWithReplaced(locale, ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.PLAYER), Arrays.asList(newOwner.name())), LocalesPaths.COMMAND_SETOWNER_EXCEPTION_STAFF_TARGET_OWNER));
			src.sendMessage(plugin.getLocales().getText(locale, LocalesPaths.COMMAND_SETOWNER_CONFIRMATION_REQUEST).clickEvent(SpongeComponents.executeCallback(messageCause -> {
				setOwner(src, newOwner, region, true);
			})));
			return;
		}
		if(!region.isCurrentTrustType(src, TrustTypes.OWNER)) throw new CommandException(plugin.getLocales().getText(locale, LocalesPaths.COMMAND_SETOWNER_EXCEPTION_PLAYER_IS_NOT_OWNER));
		src.sendMessage(plugin.getLocales().getText(locale, LocalesPaths.COMMAND_SETOWNER_CONFIRMATION_REQUEST).clickEvent(SpongeComponents.executeCallback(messageCause -> {
			setOwner(src, newOwner, region, false);
		})));
	
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_SET_NAME);
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
			RawArgument.of(
				GameProfile.class,
				(cause, args) -> Sponge.server().userManager().streamAll().map(profile -> profile.name().orElse(profile.examinableName())),
				(cause, args) -> args.length > 0 ? Sponge.server().userManager().streamAll().filter(profile -> profile.name().orElse(profile.examinableName()).equals(args[0])).findFirst() : Optional.empty(),
				false,
				false,
				0,
				LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_PRESENT
			)
		);
	}

	private void setOwner(ServerPlayer player, GameProfile newOwner, Region region, boolean staff) {
		if(staff) {
			region.setTrustType(region.getOwnerUUID(), TrustTypes.MANAGER);
			for(Region child: region.getAllChilds()) child.setTrustType(region.getOwnerUUID(), TrustTypes.MANAGER);
			if(!region.getOwnerUUID().equals(new UUID(0, 0)) && region.getOwner().isPresent()) region.getOwner().get().sendMessage(plugin.getLocales().getTextWithReplaced(region.getOwner().get().locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.WORLD, ReplaceUtil.Keys.MIN, ReplaceUtil.Keys.MAX), Arrays.asList(region.getWorldKey().toString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString())), LocalesPaths.COMMAND_SETOWNER_SUCCESS_FROM_STAFF));
		} else {
			for(Region child: region.getAllChilds()) child.setTrustType(player, TrustTypes.MANAGER);
			region.setTrustType(player, TrustTypes.MANAGER);
		}
		region.setOwner(newOwner);
		for(Region child: region.getAllChilds()) child.setOwner(newOwner);
		player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.PLAYER, ReplaceUtil.Keys.WORLD, ReplaceUtil.Keys.MIN, ReplaceUtil.Keys.MAX), Arrays.asList(newOwner.name(), region.getWorldKey().toString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString())), LocalesPaths.COMMAND_SETOWNER_SUCCESS_PLAYER));
		Sponge.server().player(newOwner.uniqueId()).ifPresent(owner -> {
			owner.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.PLAYER, ReplaceUtil.Keys.WORLD, ReplaceUtil.Keys.MIN, ReplaceUtil.Keys.MAX), Arrays.asList(player.name(), region.getWorldKey().toString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString())), LocalesPaths.COMMAND_SETOWNER_SUCCESS_TARGET));
		});
		plugin.getAPI().saveRegion(region.getPrimaryParent());
	}

}
