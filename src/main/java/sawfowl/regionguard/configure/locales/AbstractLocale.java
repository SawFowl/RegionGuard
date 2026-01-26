package sawfowl.regionguard.configure.locales;

import java.text.SimpleDateFormat;

import sawfowl.localeapi.api.Translation;
import sawfowl.regionguard.configure.locales.abstractlocale.Command;
import sawfowl.regionguard.configure.locales.abstractlocale.Comments;
import sawfowl.regionguard.configure.locales.abstractlocale.Economy;
import sawfowl.regionguard.configure.locales.abstractlocale.Events;

public interface AbstractLocale extends Translation {

	Command getCommand();

	Economy getEconomy();

	Events getEvents();

	Comments getComments();

	SimpleDateFormat getTimeFormat();

}
