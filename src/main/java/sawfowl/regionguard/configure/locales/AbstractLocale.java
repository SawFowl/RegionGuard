package sawfowl.regionguard.configure.locales;

import sawfowl.localeapi.api.LocaleReference;
import sawfowl.regionguard.configure.locales.abstractlocale.Command;
import sawfowl.regionguard.configure.locales.abstractlocale.CommandExceptions;
import sawfowl.regionguard.configure.locales.abstractlocale.Events;

public interface AbstractLocale extends LocaleReference {

	Command getCommand();

	CommandExceptions getExceptions();

	Events getEvents();

	String getEconomyNotFound();

}
