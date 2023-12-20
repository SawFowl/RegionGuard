package sawfowl.regionguard.api.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import com.google.gson.JsonObject;

@ConfigSerializable
public interface AdditionalData {

	/**
	 * Convert class objects to json data array.<br>
	 * It is recommended not to do `return null`.
	 */
	JsonObject toJsonObject();

}
