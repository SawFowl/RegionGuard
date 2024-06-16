package sawfowl.regionguard.configure.locales;

import java.text.SimpleDateFormat;

import sawfowl.localeapi.api.LocaleReference;
import sawfowl.regionguard.configure.locales.abstractlocale.Command;
import sawfowl.regionguard.configure.locales.abstractlocale.Economy;
import sawfowl.regionguard.configure.locales.abstractlocale.Events;

public interface AbstractLocale extends LocaleReference {

	Command getCommand();

	Economy getEconomy();

	Events getEvents();

	SimpleDateFormat getTimeFormat();

}
