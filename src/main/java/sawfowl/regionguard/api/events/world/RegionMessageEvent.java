package sawfowl.regionguard.api.events.world;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Event;

import net.kyori.adventure.text.Component;

public interface RegionMessageEvent extends Event {

	/**
	 * Set the message that will be shown to the player if the event is canceled.
	 */
	void setMessage(Component message);

	/**
	 * Getting a message that will be shown to the player when the message is canceled.
	 */
	Optional<Component> getMessage();

	/**
	 * Getting the player if he is the cause of the event.<br>
	 * Depending on the event, the type of object returned will be {@link ServerPlayer} or {@link Optional}(including {@link ServerPlayer}, or empty).
	 */
	<T extends Object> T getPlayer();

}
