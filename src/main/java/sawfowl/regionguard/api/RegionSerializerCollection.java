package sawfowl.regionguard.api;

import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import io.leangen.geantyref.TypeToken;

import sawfowl.regionguard.api.data.AdditionalData;
import sawfowl.regionguard.api.data.AdditionalDataMap;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.configure.serializers.AdditionalDataMapSerializer;
import sawfowl.regionguard.configure.serializers.CuboidSerializer;
import sawfowl.regionguard.configure.serializers.FlagValueSerializer;
import sawfowl.regionguard.configure.serializers.MemberDataSerializer;
import sawfowl.regionguard.configure.serializers.RegionSerializer;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.MemberData;
import sawfowl.regionguard.api.data.Region;

public class RegionSerializerCollection {

	public static final TypeSerializerCollection COLLETCTION = TypeSerializerCollection.builder()
			.register(new TypeToken<AdditionalDataMap<? extends AdditionalData>>() {}, new AdditionalDataMapSerializer())
			.register(Cuboid.class, new CuboidSerializer())
			.register(FlagValue.class, new FlagValueSerializer())
			.register(MemberData.class, new MemberDataSerializer())
			.register(Region.class, new RegionSerializer())
			.build();

}
