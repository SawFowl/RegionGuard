package sawfowl.regionguard.api.events;

import java.util.Optional;

import org.spongepowered.api.event.Event;

import net.kyori.adventure.text.Component;

public interface RegionMessageEvent extends Event {

	/**
	 * Set the message that will be shown to the player if the event is canceled.
	 */
	public void setMessage(Component message);

	/**
	 * Getting a message that will be shown to the player when the message is canceled.
	 */
	public Optional<Component> getMessage();

}
