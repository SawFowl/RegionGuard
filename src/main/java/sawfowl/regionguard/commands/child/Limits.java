package sawfowl.regionguard.commands.child;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;

import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractCommand;
import sawfowl.regionguard.commands.child.limits.Set;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class Limits extends AbstractCommand {

	public Limits(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return (cause.root() instanceof ServerPlayer && cause.hasPermission(Permissions.HELP)) || getChildExecutors().values().stream().filter(child -> child.canExecute(cause)).findFirst().isPresent();
	}

	@Override
	public void process(CommandCause cause, Audience audience, Locale locale, boolean isPlayer, String[] args, Mutable arguments) throws CommandException {
		if(!isPlayer || args.length > 0) {
			sendPaginationList(audience, getComponent(locale, LocalesPaths.COMMANDS_TITLE), getComponent(locale, LocalesPaths.PADDING), 10, getChildExecutors().values().stream().filter(child -> child.canExecute(cause)).map(child -> child.usage(cause)).toList());
			return;
		}
		ServerPlayer player = (ServerPlayer) audience;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		List<Component> messages = new ArrayList<Component>();
		String limitBlocks = player.hasPermission(Permissions.UNLIMIT_BLOCKS) ? "∞" : toString(plugin.getAPI().getLimitBlocks(player) < 0 ? 0 : plugin.getAPI().getLimitBlocks(player));
		String limitClaims = player.hasPermission(Permissions.UNLIMIT_CLAIMS) ? "∞" : toString(plugin.getAPI().getLimitClaims(player) < 0 ? 0 : plugin.getAPI().getLimitClaims(player));
		String limitSubdivisions = player.hasPermission(Permissions.UNLIMIT_SUBDIVISIONS) ? "∞" : toString(plugin.getAPI().getLimitSubdivisions(player) < 0 ? 0 : plugin.getAPI().getLimitSubdivisions(player));
		long claimedBlocks = 0;
		for(Region playerRegion : plugin.getAPI().getPlayerRegions(player)) {
			claimedBlocks += playerRegion.getCuboid().getSize();
		}
		messages.add(plugin.getLocales().getTextWithReplaced(locale, ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE, ReplaceUtil.Keys.MAX), Arrays.asList(claimedBlocks, limitBlocks)), LocalesPaths.COMMAND_LIMITS_BLOCKS));
		messages.add(plugin.getLocales().getTextWithReplaced(locale, ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE, ReplaceUtil.Keys.MAX), Arrays.asList(plugin.getAPI().getPlayerRegions(player).size(), limitClaims)), LocalesPaths.COMMAND_LIMITS_CLAIMS));
		if(region.isCurrentTrustType(player, TrustTypes.OWNER)) {
			if(region.containsChilds()) {
				List<Region> list = new ArrayList<>();
				list.add(region);
				list.addAll(region.getAllChilds());
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegions(list, player, true);
			} else {
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region, player, true, false);
			}
			messages.add(plugin.getLocales().getTextWithReplaced(locale, ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE, ReplaceUtil.Keys.MAX), Arrays.asList(region.getAllChilds().size(), limitSubdivisions)), LocalesPaths.COMMAND_LIMITS_SUBDIVISIONS));	
		}
		sendPaginationList(player, getComponent(locale, LocalesPaths.COMMAND_LIMITS_HEADER), getComponent(locale, LocalesPaths.PADDING), 10, messages);
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_LIMITS);
	}

	@Override
	public String permission() {
		return Permissions.HELP;
	}

	@Override
	public String command() {
		return "limits";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg limits &7[Action]&f - ").clickEvent(ClickEvent.suggestCommand("/rg limits ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return Arrays.asList(new Set(plugin));
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return null;
	}

	private String toString(Object object) {
		return object.toString();
	}

}
