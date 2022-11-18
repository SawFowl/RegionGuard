package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class LimitsCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	public LimitsCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		List<Component> messages = new ArrayList<Component>();
		String limitBlocks = player.hasPermission(Permissions.UNLIMIT_BLOCKS) ? "∞" : toString(plugin.getAPI().getLimitBlocks(player) < 0 ? 0 : plugin.getAPI().getLimitBlocks(player));
		String limitClaims = player.hasPermission(Permissions.UNLIMIT_CLAIMS) ? "∞" : toString(plugin.getAPI().getLimitClaims(player) < 0 ? 0 : plugin.getAPI().getLimitClaims(player));
		String limitSubdivisions = player.hasPermission(Permissions.UNLIMIT_SUBDIVISIONS) ? "∞" : toString(plugin.getAPI().getLimitSubdivisions(player) < 0 ? 0 : plugin.getAPI().getLimitSubdivisions(player));
		Locale locale = player.locale();
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
		sendInfoList(player, messages);
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		return new ArrayList<>();
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.HELP);
	}

	private void sendInfoList(ServerPlayer player, List<Component> flagList) {
		PaginationList.builder()
		.contents(flagList)
		.title(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_LIMITS_HEADER))
		.padding(plugin.getLocales().getText(player.locale(), LocalesPaths.PADDING))
		.linesPerPage(10)
		.sendTo(player);
	}

	private String toString(Object object) {
		return object.toString();
	}

	@Override
	public CommandException usage() throws CommandException {
		throw new CommandException(text("Usage: /rg limits"));
	}

}
