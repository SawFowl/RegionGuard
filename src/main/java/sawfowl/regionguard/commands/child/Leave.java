package sawfowl.regionguard.commands.child;

import java.util.List;
import java.util.Locale;

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
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class Leave extends AbstractPlayerCommand {

	public Leave(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(getExceptions(locale).getRegionNotFound());
		if(region.isCurrentTrustType(src, TrustTypes.OWNER)) exception(getExceptions(locale).getNotOwner());
		if(!region.isTrusted(src)) exception(getCommand(locale).getLeave().getNotTrusted());
		src.sendMessage(TextUtils.createCallBack(getCommand(locale).getLeave().getConfirmRequest(), messageCause -> {
			if(region.isTrusted(src)) {
				region.untrust(src);
				plugin.getAPI().saveRegion(region);
				src.sendMessage(getCommand(locale).getLeave().getSuccess());
			}
		}));
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.audience() instanceof ServerPlayer;
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getLeave().getDescription();
	}

	@Override
	public String permission() {
		return null;
	}

	@Override
	public String command() {
		return "leave";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg leave &f - ").clickEvent(ClickEvent.runCommand("/rg leave ")).append(extendedDescription(getLocale(cause)));
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
