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

import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.MemberData;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.Placeholders;

public class Untrust extends AbstractPlayerCommand {

	public Untrust(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, String[] args, Mutable arguments) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(locale, LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND);
		GameProfile profile = getArgument(GameProfile.class, cause, args, 0).get();
		if(src.hasPermission(Permissions.STAFF_TRUST)) {
			if(!region.isCurrentTrustType(profile.uniqueId(), TrustTypes.OWNER)) {
				untrust(region, src, profile);
				return;
			}
			exception(locale, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_PLAYER_IS_OWNER);
		}
		if(!region.getOwnerUUID().equals(src.uniqueId()) && !region.isCurrentTrustType(src, TrustTypes.MANAGER)) exception(locale, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_NEED_TRUST_TYPE);
		if(src.uniqueId().equals(profile.uniqueId())) exception(locale, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_TARGET_SELF);
		if(region.isCurrentTrustType(src, TrustTypes.MANAGER) && (region.isCurrentTrustType(profile.uniqueId(), TrustTypes.OWNER) || region.isCurrentTrustType(profile.uniqueId(), TrustTypes.MANAGER))) exception(locale, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_TARGET_MANAGER);
		untrust(region, src, profile);
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_UNTRUST);
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
				CommandTreeNodeTypes.GAME_PROFILE.get().createNode(),
				(cause, args) -> cause.first(ServerPlayer.class).isPresent() ? findRegion(cause.first(ServerPlayer.class).get()).map(region -> region.getMembers().stream().filter(member -> member.getTrustType() != TrustTypes.OWNER).map(MemberData::getName)).orElse(Stream.empty()) : Stream.empty(),
				(cause, args) -> args.length > 0 && cause.first(ServerPlayer.class).isPresent() ? findRegion(cause.first(ServerPlayer.class).get()).map(region -> region.getMembers().stream().filter(member -> member.getTrustType() != TrustTypes.OWNER).filter(member -> member.getName().equals(args[0])).findFirst().orElse(null)).map(member -> Sponge.server().userManager().streamAll().filter(profile -> member.getUniqueId().equals(profile.uniqueId())).findFirst().orElse(null)) : Optional.empty(),
				"Player",
				false,
				false,
				0,
				null,
				LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_PRESENT
			)
		);
	}

	private void untrust(Region region, ServerPlayer player, GameProfile untrustedPlayer) {
		region.untrust(untrustedPlayer.uniqueId());
		plugin.getAPI().saveRegion(region);
		player.sendMessage(getText(player.locale(), LocalesPaths.COMMAND_UNTRUST_SUCCESS_PLAYER).replace(Placeholders.PLAYER, untrustedPlayer.name().orElse(untrustedPlayer.examinableName())).get());
		if(Sponge.server().player(untrustedPlayer.uuid()).isPresent()) Sponge.server().player(untrustedPlayer.uuid()).get().sendMessage(getText(Sponge.server().player(untrustedPlayer.uuid()).get().locale(), LocalesPaths.COMMAND_UNTRUST_SUCCESS_TARGET).replace(new String[] {Placeholders.PLAYER, Placeholders.WORLD, Placeholders.MIN, Placeholders.MAX}, player.name(), region.getWorldKey().toString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString()).get());
	}

	private Optional<Region> findRegion(ServerPlayer player) {
		return plugin.getAPI().findRegion(player.world(), player.blockPosition(), rg -> (player.hasPermission(Permissions.STAFF_TRUST) || rg.isCurrentTrustType(player, TrustTypes.OWNER) || rg.isCurrentTrustType(player, TrustTypes.MANAGER)));
	}

}
