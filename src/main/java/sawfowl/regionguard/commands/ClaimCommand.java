package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class ClaimCommand implements Command.Raw {

	private final RegionGuard plugin;
	public ClaimCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		Optional<Region> optRegion = plugin.getAPI().getTempRegion(player.uniqueId());
		if(!optRegion.isPresent()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_CLAIM_REGION_NOT_FOUND));
		Region region = optRegion.get();
		if(!region.getServerWorld().isPresent()) throw new CommandException(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.WORLD), Arrays.asList(region.getServerWorldKey().toString())), LocalesPaths.COMMAND_CLAIM_REGION_NOT_FOUND));
		ServerWorld world = region.getServerWorld().get();
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			for(Vector3i vector3i : region.getCuboid().getAllPositions()) {
				Optional<Region> find = plugin.getAPI().getRegions().parallelStream().filter(rg -> (rg.isIntersectsWith(world, vector3i))).findFirst();
				if(find.isPresent()) {
					player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.MIN, ReplaceUtil.Keys.MAX), Arrays.asList(find.get().getCuboid().getMin().toString(), find.get().getCuboid().getMax().toString())), LocalesPaths.COMMAND_CLAIM_CANCEL));
					return;
				}
			}
			for(Entry<Flags, Boolean> entry : plugin.getConfigs().getDefaultClaimFlags().entrySet()) {
				region.setFlag(entry.getKey(), entry.getValue(), null, null);
			}
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_CLAIM_SUCCESS));
			plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region, player, false, false);
			plugin.getAPI().registerRegion(region);
			plugin.getAPI().saveRegion(region);
		});
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments) throws CommandException {
		return new ArrayList<>();
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.CLAIM);
	}

	@Override
	public Optional<Component> shortDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Claim region."));
	}

	@Override
	public Optional<Component> extendedDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Claim region."));
	}

	@Override
	public Component usage(CommandCause cause) {
		return Component.text("/rg claim");
	}

}