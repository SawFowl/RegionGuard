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
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class Claim extends AbstractPlayerCommand {

	public Claim(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, String[] args, Mutable arguments) throws CommandException {
		Optional<Region> optRegion = plugin.getAPI().getTempRegion(src.uniqueId());
		if(!optRegion.isPresent()) throw new CommandException(plugin.getLocales().getText(src.locale(), LocalesPaths.COMMAND_CLAIM_REGION_NOT_FOUND));
		Region region = optRegion.get();
		if(!region.getWorld().isPresent()) throw new CommandException(plugin.getLocales().getTextWithReplaced(src.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.WORLD), Arrays.asList(region.getWorldKey().toString())), LocalesPaths.COMMAND_CLAIM_REGION_NOT_FOUND));
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			Region find = plugin.getAPI().findIntersectsRegion(region);
			if(!plugin.getAPI().findIntersectsRegion(region).equals(region)) {
				src.sendMessage(plugin.getLocales().getTextWithReplaced(src.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.MIN, ReplaceUtil.Keys.MAX), Arrays.asList(find.getCuboid().getMin().toString(), find.getCuboid().getMax().toString())), LocalesPaths.COMMAND_CLAIM_CANCEL));
				return;
			}
			if(region.isBasicClaim()) region.setFlags(plugin.getDefaultFlagsConfig().getClaimFlags());
			if(region.isArena()) region.setFlags(plugin.getDefaultFlagsConfig().getArenaFlags());
			if(region.isAdmin()) region.setFlags(plugin.getDefaultFlagsConfig().getAdminFlags());
			src.sendMessage(plugin.getLocales().getText(src.locale(), LocalesPaths.COMMAND_CLAIM_SUCCESS));
			plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region, src, false, false);
			plugin.getAPI().registerRegion(region);
			plugin.getAPI().saveRegion(region);
		});
	
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_CLAIM);
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
