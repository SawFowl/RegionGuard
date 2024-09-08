package sawfowl.regionguard.commands.child;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.Placeholders;

public class Trust extends AbstractPlayerCommand {

	public Trust(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, String[] args, Mutable arguments) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(locale, LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND);
		if(region.isAdmin()) exception(locale, LocalesPaths.COMMAND_TRUST_EXCEPTION_ADMINCLAIM);
		if((!region.isCurrentTrustType(src, TrustTypes.OWNER) && !region.isCurrentTrustType(src, TrustTypes.MANAGER)) && !src.hasPermission(Permissions.STAFF_TRUST)) exception(locale, LocalesPaths.COMMAND_TRUST_EXCEPTION_NEED_TRUST_TYPE);
		GameProfile trustedPlayer = getArgument(GameProfile.class, cause, args, 0).get();
		if(!src.hasPermission(Permissions.UNLIMIT_MEMBERS) && plugin.getAPI().getLimitMembers(region.getOwnerUUID()) <= region.getTotalMembers() - 1 && !region.getMemberData(trustedPlayer.uniqueId()).isPresent()) exception(locale, LocalesPaths.COMMAND_TRUST_EXCEPTION_LIMIT_REACHED);
		TrustTypes trustLevel = getArgument(TrustTypes.class, cause, args, 1).get();
		if((src.uniqueId().equals(trustedPlayer.uniqueId()) && !src.hasPermission(Permissions.STAFF_TRUST)) || (src.uniqueId().equals(trustedPlayer.uniqueId()) && region.isCurrentTrustType(src, TrustTypes.OWNER))) exception(locale, LocalesPaths.COMMAND_TRUST_EXCEPTION_TARGET_SELF);
		if(!region.isCurrentTrustType(src, TrustTypes.OWNER) && trustLevel == TrustTypes.MANAGER) exception(locale, LocalesPaths.COMMAND_TRUST_EXCEPTION_PLAYER_IS_NOT_OWNER);
		region.setTrustType(trustedPlayer, trustLevel);
		src.sendMessage(getText(locale, LocalesPaths.COMMAND_TRUST_SUCCESS_PLAYER).replace(new String[] {Placeholders.TRUST_TYPE, Placeholders.PLAYER}, trustLevel.toString(), trustedPlayer.name().orElse(trustedPlayer.examinableName())).get());
		Sponge.server().player(trustedPlayer.uniqueId()).ifPresent(trusted -> {
			trusted.sendMessage(getText(trusted.locale(), LocalesPaths.COMMAND_TRUST_SUCCESS_TARGET).replace(new String[] {Placeholders.TRUST_TYPE, Placeholders.PLAYER, Placeholders.WORLD, Placeholders.MIN, Placeholders.MAX}, trustLevel.toString(), src.name(), region.getWorldKey().toString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString()).get());
		});
		plugin.getAPI().saveRegion(region.getPrimaryParent());
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_TRUST);
	}

	@Override
	public String permission() {
		return Permissions.TRUST;
	}

	@Override
	public String command() {
		return "trust";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg trust &7<Player> <TrustType>&f - ").clickEvent(ClickEvent.suggestCommand("/rg trust ")).append(extendedDescription(getLocale(cause)));
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
				(cause, args) -> Sponge.server().userManager().streamAll().map(profile -> profile.name().orElse(profile.examinableName())),
				(cause, args) -> args.length > 0 ? Sponge.server().userManager().streamAll().filter(profile -> profile.name().orElse(profile.examinableName()).equals(args[0])).findFirst() : Optional.empty(),
				"Player",
				false,
				false,
				0,
				null,
				LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_PRESENT
			),
			RawArgument.of(
				TrustTypes.class,
				CommandTreeNodeTypes.STRING.get().createNode(),
				(cause, args) -> TrustTypes.getValues(),
				(cause, args) -> args.length > 1 ? Optional.ofNullable(TrustTypes.checkType(args[1])) : Optional.empty(),
				"TrustType",
				false,
				false,
				0,
				null,
				LocalesPaths.COMMAND_TRUST_EXCEPTION_TRUST_TYPE_NOT_PRESENT
			)
		);
	}

}
