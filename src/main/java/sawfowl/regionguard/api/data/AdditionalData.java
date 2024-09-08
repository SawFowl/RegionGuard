package sawfowl.regionguard.api.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import com.google.gson.JsonObject;

@ConfigSerializable
public interface AdditionalData {

	/**
	 * Convert class objects to json data array.<br>
	 * It is recommended not to do `return null`.<br>
	 * If you use this method, add all class fields labeled with the `{@link Setting} annotation to the Json object.<br>
	 * The object access key must match the value of the annotation.<br>
	 * If the method returns `null`, an attempt to serialize the class will be made.
	 */
	JsonObject toJsonObject();

}
