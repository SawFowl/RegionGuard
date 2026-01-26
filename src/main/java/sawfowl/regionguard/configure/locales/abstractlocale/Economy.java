package sawfowl.regionguard.configure.locales.abstractlocale;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import sawfowl.localeapi.api.Translation;

public interface Economy extends Translation {

	String getEconomyNotFound();

	String getErrorGiveMoney(ServerPlayer player);

	String getErrorTakeMoney(ServerPlayer player);

}
