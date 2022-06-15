package sawfowl.regionguard.commands;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.math.NumberUtils;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class SellSubdivisionsCommand implements Command.Raw {

	private final RegionGuard plugin;
	private List<CommandCompletion> empty = new ArrayList<>();
	public SellSubdivisionsCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		List<String> args = Stream.of(arguments.input().split(" ")).filter(string -> (!string.equals(""))).collect(Collectors.toList());
		args.remove(0);
		if(args.size() == 0) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_NOT_PRESENT));
		if(!NumberUtils.isParsable(args.get(0))) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_WRONG_ARGUMENT));
		long toSell = Long.valueOf(args.get(0));
		if(toSell <= 0) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_ENTERED_ZERO));
		if(plugin.getAPI().getLimitSubdivisions(player) - getDefaultSize(player) < toSell) throw new CommandException(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.MAX), Arrays.asList(plugin.getAPI().getLimitSubdivisions(player) - getDefaultSize(player))), LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_TO_MUCH_VOLUME));
		if(!plugin.getEconomy().addToPlayerBalance(player, plugin.getEconomy().checkCurrency(plugin.getAPI().getCurrency(player)), BigDecimal.valueOf(toSell * plugin.getAPI().getSellSubdivisionPrice(player)))) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_ECONOMY_EXCEPTION));
		plugin.getAPI().setLimitSubdivisions(player, plugin.getAPI().getLimitSubdivisions(player) - toSell);
		player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE, ReplaceUtil.Keys.VOLUME), Arrays.asList(toSell, plugin.getAPI().getLimitSubdivisions(player))), LocalesPaths.COMMAND_SELLSUBDIVISIONS_SUCCESS));
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments) throws CommandException {
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.SELL_BLOCKS);
	}

	@Override
	public Optional<Component> shortDescription(CommandCause cause) {
		return Optional.of(Component.text("Reduce the number of subdivisions that can be claimed."));
	}

	@Override
	public Optional<Component> extendedDescription(CommandCause cause) {
		return Optional.of(Component.text("Reduce the number of subdivisions that can be claimed."));
	}

	@Override
	public Component usage(CommandCause cause) {
		return Component.text("/rg sellsubdivisions");
	}

	private boolean optionIsPresent(ServerPlayer player) {
		return player.user().subjectData().allOptions().values().stream().findFirst().isPresent() && player.user().subjectData().allOptions().values().stream().findFirst().get().containsKey(Permissions.LIMIT_SUBDIVISIONS);
	}

	private long getDefaultSize(ServerPlayer player) {
		return optionIsPresent(player) && NumberUtils.isCreatable(player.user().subjectData().allOptions().values().stream().findFirst().get().get(Permissions.LIMIT_SUBDIVISIONS)) ? Long.valueOf(player.user().subjectData().allOptions().values().stream().findFirst().get().get(Permissions.LIMIT_SUBDIVISIONS)) : 0;
	}

}
