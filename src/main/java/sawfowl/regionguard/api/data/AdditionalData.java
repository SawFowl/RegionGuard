package sawfowl.regionguard.api.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public abstract class AdditionalData {

	/**
	 * Getting a class to deserialize a custom data.
	 * 
	 * @param clazz - The class is a descendant of CompoundTag.
	 */
	@SuppressWarnings({"unchecked" }) 
	public static Class<AdditionalData> getClass(Class<?> clazz) {
		try {
			return (Class<AdditionalData>) (Object) clazz;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}

}
