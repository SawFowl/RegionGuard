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
import sawfowl.commandpack.api.commands.raw.arguments.RawArguments;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractCommand;
import sawfowl.regionguard.commands.child.limits.Set;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.Placeholders;

public class Limits extends AbstractCommand {

	public Limits(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return (cause.root() instanceof ServerPlayer && cause.hasPermission(Permissions.HELP)) || getChildExecutors().values().stream().filter(child -> child.canExecute(cause)).findFirst().isPresent();
	}

	@Override
	public void process(CommandCause cause, Audience audience, Locale locale, boolean isPlayer, Mutable arguments, RawArgumentsMap args) throws CommandException {
		ServerPlayer player = args.getPlayer(0).filter(p -> cause.hasPermission(Permissions.STAFF_LIMITS)).orElse(isPlayer ? (ServerPlayer) audience : null);
		if(!isPlayer && player == null) {
			sendPaginationList(audience, getComponent(locale, LocalesPaths.COMMANDS_TITLE), getComponent(locale, LocalesPaths.PADDING), 10, getChildExecutors().values().stream().filter(child -> child.canExecute(cause)).map(child -> child.usage(cause)).toList());
			return;
		}
		List<Component> messages = generateMessages(locale, player);
		if(isPlayer) {
			messages  = visualize((ServerPlayer) audience, player, locale, messages);
		} else {
			messages.add(getText(locale, LocalesPaths.COMMAND_LIMITS_SUBDIVISIONS).replace(new String[] {Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX}, "?", player.hasPermission(Permissions.UNLIMIT_SUBDIVISIONS) ? "∞" : toString(plugin.getAPI().getLimitSubdivisions(player) < 0 ? 0 : plugin.getAPI().getLimitSubdivisions(player)), player.hasPermission(Permissions.UNLIMIT_SUBDIVISIONS) ? "∞" : plugin.getAPI().getLimitMaxSubdivisions(player)).get());
			messages.add(getText(locale, LocalesPaths.COMMAND_LIMITS_MEMBERS).replace(new String[] {Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX}, "?", player.hasPermission(Permissions.UNLIMIT_MEMBERS) ? "∞" : toString(plugin.getAPI().getLimitMembers(player) < 0 ? 0 : plugin.getAPI().getLimitMembers(player)), player.hasPermission(Permissions.UNLIMIT_MEMBERS) ? "∞" : plugin.getAPI().getLimitMaxMembers(player)).get());
		}
		sendPaginationList(audience, getText(locale, LocalesPaths.COMMAND_LIMITS_HEADER).replace(Placeholders.PLAYER, player.name()).get(), getComponent(locale, LocalesPaths.PADDING), 10, messages);
	}

	private List<Component> generateMessages(Locale locale, ServerPlayer player) {
		List<Component> messages = new ArrayList<Component>();
		long claimedBlocks = 0;
		for(Region playerRegion : plugin.getAPI().getPlayerRegions(player)) claimedBlocks += playerRegion.getCuboid().getSize();
		messages.add(getText(locale, LocalesPaths.COMMAND_LIMITS_BLOCKS).replace(new String[] {Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX}, claimedBlocks, player.hasPermission(Permissions.UNLIMIT_BLOCKS) ? "∞" : plugin.getAPI().getLimitBlocks(player) < 0 ? 0 : plugin.getAPI().getLimitBlocks(player), player.hasPermission(Permissions.UNLIMIT_BLOCKS) ? "∞" : plugin.getAPI().getLimitMaxBlocks(player)).get());
		messages.add(getText(locale, LocalesPaths.COMMAND_LIMITS_CLAIMS).replace(new String[] {Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX}, plugin.getAPI().getPlayerRegions(player).size(), player.hasPermission(Permissions.UNLIMIT_CLAIMS) ? "∞" : plugin.getAPI().getLimitClaims(player) < 0 ? 0 : plugin.getAPI().getLimitClaims(player), player.hasPermission(Permissions.UNLIMIT_CLAIMS) ? "∞" : plugin.getAPI().getLimitMaxClaims(player)).get());
		return messages;
	}

	private List<Component> visualize(ServerPlayer player, ServerPlayer target, Locale locale, List<Component> messages) {
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		if(region.isCurrentTrustType(target, TrustTypes.OWNER)) {
			if(region.containsChilds()) {
				List<Region> list = new ArrayList<>(region.getAllChilds());
				list.add(region);
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegions(list, player, true);
			} else {
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region, player, true, false);
			}
			messages.add(getText(locale, LocalesPaths.COMMAND_LIMITS_SUBDIVISIONS).replace(new String[] {Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX}, region.getAllChilds().size(), target.hasPermission(Permissions.UNLIMIT_SUBDIVISIONS) ? "∞" : toString(plugin.getAPI().getLimitSubdivisions(target) < 0 ? 0 : plugin.getAPI().getLimitSubdivisions(target)), target.hasPermission(Permissions.UNLIMIT_SUBDIVISIONS) ? "∞" : plugin.getAPI().getLimitMaxSubdivisions(target)).get());
			messages.add(getText(locale, LocalesPaths.COMMAND_LIMITS_MEMBERS).replace(new String[] {Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX}, region.getTotalMembers(), target.hasPermission(Permissions.UNLIMIT_MEMBERS) ? "∞" : toString(plugin.getAPI().getLimitMembers(target) < 0 ? 0 : plugin.getAPI().getLimitMembers(target)), target.hasPermission(Permissions.UNLIMIT_MEMBERS) ? "∞" : plugin.getAPI().getLimitMaxMembers(target)).get());
		} else {
			messages.add(getText(locale, LocalesPaths.COMMAND_LIMITS_SUBDIVISIONS).replace(new String[] {Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX}, "?", target.hasPermission(Permissions.UNLIMIT_SUBDIVISIONS) ? "∞" : toString(plugin.getAPI().getLimitSubdivisions(target) < 0 ? 0 : plugin.getAPI().getLimitSubdivisions(target)), target.hasPermission(Permissions.UNLIMIT_SUBDIVISIONS) ? "∞" : plugin.getAPI().getLimitMaxSubdivisions(target)).get());
			messages.add(getText(locale, LocalesPaths.COMMAND_LIMITS_MEMBERS).replace(new String[] {Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX}, "?", target.hasPermission(Permissions.UNLIMIT_MEMBERS) ? "∞" : toString(plugin.getAPI().getLimitMembers(target) < 0 ? 0 : plugin.getAPI().getLimitMembers(target)), target.hasPermission(Permissions.UNLIMIT_MEMBERS) ? "∞" : plugin.getAPI().getLimitMaxMembers(target)).get());
		}
		return messages;
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
		return TextUtils.deserializeLegacy(cause.hasPermission(Permissions.STAFF_LIMITS) ? "&6/rg limits &7[Player|Action]&f - " : "&6/rg limits &7[Action]&f - ").clickEvent(ClickEvent.suggestCommand("/rg limits ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return Arrays.asList(new Set(plugin));
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return Arrays.asList(RawArguments.createPlayerArgument(true, true, 0, null, LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_PRESENT));
	}

	private String toString(Object object) {
		return object.toString();
	}

}
