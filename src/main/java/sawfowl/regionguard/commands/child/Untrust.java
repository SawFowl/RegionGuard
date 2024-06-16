package sawfowl.regionguard.commands.child;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.command.registrar.tree.CommandTreeNodeTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentData;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.commandpack.api.commands.raw.arguments.RawOptional;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.MemberData;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class Untrust extends AbstractPlayerCommand {

	public Untrust(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(getExceptions(locale).getRegionNotFound());
		GameProfile profile = args.get(GameProfile.class, 0).get();
		if(src.hasPermission(Permissions.STAFF_TRUST)) {
			if(!region.isCurrentTrustType(profile.uniqueId(), TrustTypes.OWNER)) {
				untrust(region, src, profile);
				return;
			}
			exception(getCommand(locale).getUntrust().getPlayerIsOwner());
		}
		if(!region.getOwnerUUID().equals(src.uniqueId()) && !region.isCurrentTrustType(src, TrustTypes.MANAGER)) exception(getCommand(locale).getUntrust().getNotOwner());
		if(src.uniqueId().equals(profile.uniqueId())) exception(getExceptions(locale).getTargetSelf());
		if(region.isCurrentTrustType(src, TrustTypes.MANAGER) && (region.isCurrentTrustType(profile.uniqueId(), TrustTypes.OWNER) || region.isCurrentTrustType(profile.uniqueId(), TrustTypes.MANAGER))) exception(getCommand(locale).getUntrust().getLowTrust());
		untrust(region, src, profile);
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getUntrust().getDescription();
	}

	@Override
	public String permission() {
		return Permissions.TRUST;
	}

	@Override
	public String command() {
		return "untrust";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg untrust &7<Player>&f - ").clickEvent(ClickEvent.suggestCommand("/rg untrust ")).append(extendedDescription(getLocale(cause)));
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
				(cause, args) -> cause.first(ServerPlayer.class).isPresent() ? findRegion(cause.first(ServerPlayer.class).get()).map(region -> region.getMembers().stream().filter(member -> member.getTrustType() != TrustTypes.OWNER).map(MemberData::getName)).orElse(Stream.empty()) : Stream.empty(),
				(cause, args) -> args.length > 0 && cause.first(ServerPlayer.class).isPresent() ? findRegion(cause.first(ServerPlayer.class).get()).map(region -> region.getMembers().stream().filter(member -> member.getTrustType() != TrustTypes.OWNER).filter(member -> member.getName().equals(args[0])).findFirst().orElse(null)).map(member -> Sponge.server().userManager().streamAll().filter(profile -> member.getUniqueId().equals(profile.uniqueId())).findFirst().orElse(null)) : Optional.empty(),
				new RawArgumentData<>("Player", CommandTreeNodeTypes.GAME_PROFILE.get().createNode(), 0, null, null),
				RawOptional.notOptional(),
				locale -> getExceptions(locale).getPlayerNotPresent()
			)
		);
	}

	private void untrust(Region region, ServerPlayer player, GameProfile untrustedPlayer) {
		region.untrust(untrustedPlayer.uniqueId());
		plugin.getAPI().saveRegion(region);
		player.sendMessage(getCommand(player).getUntrust().getSuccess(untrustedPlayer));
		Sponge.server().player(untrustedPlayer.uuid()).ifPresent(target -> {
			target.sendMessage(getCommand(target).getUntrust().getSuccessTarget(player, region));
		});
	}

	private Optional<Region> findRegion(ServerPlayer player) {
		return plugin.getAPI().findRegion(player.world(), player.blockPosition(), rg -> (player.hasPermission(Permissions.STAFF_TRUST) || rg.isCurrentTrustType(player, TrustTypes.OWNER) || rg.isCurrentTrustType(player, TrustTypes.MANAGER)));
	}

}
