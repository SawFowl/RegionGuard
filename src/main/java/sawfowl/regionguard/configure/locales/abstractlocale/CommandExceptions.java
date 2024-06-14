package sawfowl.regionguard.configure.locales.abstractlocale;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.LocaleReference;

public interface CommandExceptions extends LocaleReference {

	Component getOnlyPlayer();

	Component getPlayerNotPresent();

	Component getMessageNotPresent();

	Component getNameNotPresent();

	Component getVolumeNotPresent();

	Component getEnteredZero();

	Component getNotEnoughMoney();

	Component getNotOwner();

	Component getNotEconomyException();

	Component getMaxValue(long value);

	Component getRegionTypeNotPresent();

	Component getSelectorTypeNotPresent();

	Component getTrustTypeNotPresent();

}
