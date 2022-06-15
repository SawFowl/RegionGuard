package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class ListCommand implements Command.Raw {

	private final RegionGuard plugin;
	private List<CommandCompletion> empty = new ArrayList<>();
	public ListCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		List<String> args = Stream.of(arguments.input().split(" ")).filter(string -> (!string.equals(""))).collect(Collectors.toList());
		args.remove(0);
		boolean otherPlayer = !args.isEmpty() && !args.get(0).equals(player.name()) && player.hasPermission(Permissions.STAFF_LIST) && Sponge.server().player(args.get(0)).isPresent();
		List<Region> regions = otherPlayer ? plugin.getAPI().getPlayerRegions(Sponge.server().player(args.get(0)).get()) : plugin.getAPI().getPlayerRegions(player);
		if(regions.size() == 0) throw new CommandException(Component.text(otherPlayer ? "У игрока нет регионов" : "У вас нет регионов"));
		List<Component> list = new ArrayList<>();
		for(Region region : regions) {
			Component tp = player.hasPermission(Permissions.TELEPORT) || player.hasPermission(Permissions.STAFF_LIST) ? Component.text("§7[§bTP§7]").clickEvent(SpongeComponents.executeCallback(callback -> {
				ServerWorld world = region.getServerWorld().isPresent() ? region.getServerWorld().get() : player.world();
				Vector3i vector3i = world.highestPositionAt(region.getCuboid().getCenter().toInt());
				boolean safePos = player.gameMode().get() == GameModes.CREATIVE.get() || player.gameMode().get() == GameModes.SPECTATOR.get() || (world.block(vector3i.add(0, 1, 0)).type() == BlockTypes.AIR.get() && world.block(vector3i.sub(0, 1, 0)).type() != BlockTypes.AIR.get() && world.block(vector3i.sub(0, 1, 0)).type() != BlockTypes.LAVA.get());
				if(safePos) {
					player.transferToWorld(world, Vector3d.from(vector3i.x(), vector3i.y(), vector3i.z()));
				} else {
					Vector3d finalize = Vector3d.from(vector3i.x(), vector3i.y(), vector3i.z());
					player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_LIST_EXCEPTION_NOTSAFE).clickEvent(SpongeComponents.executeCallback(callback2 -> {
						player.transferToWorld(world, finalize);
					})));
				}
			})) : Component.empty();
			Component positions = Component.text("§6" + region.getCuboid().getMin() + " ➢ " + region.getCuboid().getMax());
			Component uuidOrName = region.getName(player.locale()).isPresent() ? region.asComponent(player.locale()) : Component.text("§2<" + region.getUniqueId() + ">");
			list.add(Component.text().append(tp).append(Component.text(" ")).append(positions).append(Component.text(" ")).append(uuidOrName).build());
		}
		sendRegionsList(player, player.locale(), list, 10, otherPlayer ? args.get(0) : player.name());
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments) throws CommandException {
		if(cause.hasPermission(Permissions.STAFF_LIST)) {
			List<String> args = Stream.of(arguments.input().split(" ")).filter(string -> (!string.equals(""))).collect(Collectors.toList());
			args.remove(0);
			if(args.isEmpty()) return Sponge.server().onlinePlayers().stream().map(player -> (CommandCompletion.of(player.name()))).collect(Collectors.toList());
		}
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.LIST) || cause.hasPermission(Permissions.STAFF_LIST);
	}

	@Override
	public Optional<Component> shortDescription(CommandCause cause) {
		return Optional.of(Component.text("Getting a list of player's regions."));
	}

	@Override
	public Optional<Component> extendedDescription(CommandCause cause) {
		return Optional.of(Component.text("Getting a list of player's regions."));
	}

	@Override
	public Component usage(CommandCause cause) {
		return Component.text("/rg list");
	}

	private void sendRegionsList(Audience audience, Locale locale, List<Component> messages, int lines, String name) {
		PaginationList.builder()
		.contents(messages)
		.title(plugin.getLocales().getTextWithReplaced(locale, ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.PLAYER), Arrays.asList(name)), LocalesPaths.COMMAND_LIST_TITLE))
		.padding(plugin.getLocales().getText(locale, LocalesPaths.PADDING))
		.linesPerPage(lines)
		.sendTo(audience);
	}

}
