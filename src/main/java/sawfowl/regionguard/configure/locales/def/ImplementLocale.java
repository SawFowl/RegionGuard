package sawfowl.regionguard.configure.locales.def;

import java.text.SimpleDateFormat;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.AbstractLocale;
import sawfowl.regionguard.configure.locales.abstractlocale.Command;
import sawfowl.regionguard.configure.locales.abstractlocale.Comments;
import sawfowl.regionguard.configure.locales.abstractlocale.Economy;
import sawfowl.regionguard.configure.locales.abstractlocale.Events;

@ConfigSerializable
public class ImplementLocale implements AbstractLocale {

	public ImplementLocale() {}

	@Setting("Command")
	private ImplementCommand command = new ImplementCommand();
	@Setting("Economy")
	private ImplementEconomy economy = new ImplementEconomy();
	@Setting("Events")
	private ImplementEvents events = new ImplementEvents();
	@Setting("Comments")
	private ImplementComments comments = new ImplementComments();
	@Setting("TimeFormat")
	private String timeFormat = "MM.dd.yyyy HH:mm:ss";
	private SimpleDateFormat format;

	@Override
	public Command getCommand() {
		return command;
	}

	@Override
	public Economy getEconomy() {
		return economy;
	}

	@Override
	public Events getEvents() {
		return events;
	}

	@Override
	public SimpleDateFormat getTimeFormat() {
		return format == null ? format = new SimpleDateFormat(timeFormat) : format;
	}

	@Override
	public Comments getComments() {
		return comments;
	}

}
