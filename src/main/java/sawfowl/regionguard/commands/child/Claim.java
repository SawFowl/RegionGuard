package sawfowl.regionguard.commands.child;

import java.util.*;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class Claim extends AbstractPlayerCommand {

	public Claim(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Optional<Region> optRegion = plugin.getAPI().getTempRegion(src.uniqueId());
		if(!optRegion.isPresent()) exception(getExceptions(locale).getRegionNotFound());
		Region region = optRegion.get();
		if(!region.getWorld().isPresent()) exception(getCommand(locale).getClaim().getWorldNotFound(region.getWorldKey().asString()));
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			Region find = plugin.getAPI().findIntersectsRegion(region);
			if(!plugin.getAPI().findIntersectsRegion(region).equals(region)) {
				src.sendMessage(getCommand(locale).getClaim().getIntersect(find.getCuboid().getMin(), find.getCuboid().getMax()));
				return;
			}
			if(region.isBasicClaim()) region.setFlags(plugin.getDefaultFlagsConfig().getClaimFlags());
			if(region.isArena()) region.setFlags(plugin.getDefaultFlagsConfig().getArenaFlags());
			if(region.isAdmin()) region.setFlags(plugin.getDefaultFlagsConfig().getAdminFlags());
			src.sendMessage(getCommand(locale).getClaim().getSuccess());
			plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region, src, false, false);
			plugin.getAPI().registerRegion(region);
			plugin.getAPI().saveRegion(region);
		});
	
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getClaim().getDescription();
	}

	@Override
	public String permission() {
		return Permissions.CLAIM;
	}

	@Override
	public String command() {
		return "claim";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg claim&f - ").clickEvent(ClickEvent.runCommand("/rg claim")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return null;
	}

}
