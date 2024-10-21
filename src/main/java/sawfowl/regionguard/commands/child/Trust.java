package sawfowl.regionguard.commands.child;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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

public class Trust extends AbstractPlayerCommand {

	public Trust(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(getExceptions(locale).getRegionNotFound());
		if(region.isAdmin()) exception(getTrust(locale).getAdminClaim());
		if((!region.isCurrentTrustType(src, TrustTypes.OWNER) && !region.isCurrentTrustType(src, TrustTypes.MANAGER)) && !src.hasPermission(Permissions.STAFF_TRUST)) exception(getTrust(locale).getLowTrust());
		GameProfile trustedPlayer = args.get(GameProfile.class, 0).get();
		if(!src.hasPermission(Permissions.UNLIMIT_MEMBERS) && plugin.getAPI().getLimitMembers(region.getOwnerUUID()) <= region.getTotalMembers() - 1 && !region.getMemberData(trustedPlayer.uniqueId()).isPresent()) exception(getTrust(locale).getLimitReached());
		TrustTypes trustLevel = args.get(TrustTypes.class, 1).get();
		if((src.uniqueId().equals(trustedPlayer.uniqueId()) && !src.hasPermission(Permissions.STAFF_TRUST)) || (src.uniqueId().equals(trustedPlayer.uniqueId()) && region.isCurrentTrustType(src, TrustTypes.OWNER))) exception(getExceptions(locale).getTargetSelf());
		if(!region.isCurrentTrustType(src, TrustTypes.OWNER) && trustLevel == TrustTypes.MANAGER) exception(getExceptions(locale).getNotOwner());
		region.setTrustType(trustedPlayer, trustLevel);
		src.sendMessage(getTrust(locale).getSuccess(trustLevel.toString(), trustedPlayer));
		Sponge.server().player(trustedPlayer.uniqueId()).ifPresent(trusted -> {
			trusted.sendMessage(getTrust(trusted).getSuccessTarget(trustLevel.toString(), src, region));
		});
		plugin.getAPI().saveRegion(region.getPrimaryParent());
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getTrust(locale).getDescription();
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
			RawArguments.createGameProfile(RawBasicArgumentData.createGameProfile(0, null, null), RawOptional.notOptional(), locale -> getExceptions(locale).getPlayerNotPresent()),
			RawArgument.of(TrustTypes.class,
				(cause, args) -> TrustTypes.getValues().stream(),
				(cause, args) -> args.length > 1 ? Stream.of(TrustTypes.values()).filter(t -> t.toString().equals(args[1])).findFirst() : null,
				new RawArgumentData<>("TrustType", CommandTreeNodeTypes.STRING.get().createNode(), 1, null, null),
				RawOptional.notOptional(),
				locale -> getExceptions(locale).getTrustTypeNotPresent())
		);
	}

	private sawfowl.regionguard.configure.locales.abstractlocale.Command.Trust getTrust(Locale locale) {
		return getCommand(locale).getTrust();
	}

	private sawfowl.regionguard.configure.locales.abstractlocale.Command.Trust getTrust(ServerPlayer player) {
		return getTrust(player.locale());
	}

}