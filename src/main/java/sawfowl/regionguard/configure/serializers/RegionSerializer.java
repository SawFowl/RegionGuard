package sawfowl.regionguard.configure.serializers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.data.AdditionalData;
import sawfowl.regionguard.api.data.AdditionalDataMap;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.MemberData;
import sawfowl.regionguard.api.data.Region;

public class RegionSerializer implements TypeSerializer<Region> {

	private final TypeToken<Map<String, Component>> mapComponentsToken = new TypeToken<Map<String, Component>>(){};
	private final TypeToken<Map<String, Set<FlagValue>>> flagsToken = new TypeToken<Map<String, Set<FlagValue>>>(){};
	private final TypeToken<AdditionalDataMap<? extends AdditionalData>> dataMapToken = new TypeToken<AdditionalDataMap<?>>(){};

	@Override
	public Region deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return node.virtual() || node.empty() ? null : Region.builder()
				.setUniqueId(node.node("UUID").get(UUID.class))
				.addNames(node.node("RegionName").get(mapComponentsToken, new HashMap<>()))
				.setCuboid(node.node("Cuboid").virtual() ? null : node.node("Cuboid").get(Cuboid.class))
				.addChilds(node.node("Childs").getList(Region.class, new ArrayList<>()))
				.setFlags(node.node("Flags").get(flagsToken, new HashMap<>()))
				.addMembers(node.node("Members").getList(MemberData.class, new ArrayList<>()))
				.setType(node.node("Type").virtual() ? RegionTypes.CLAIM : RegionTypes.valueOfName(node.node("Type").getString()))
				.setCreationTime(node.node("Created").getLong(0))
				.addJoinMessages(node.node("JoinMessage").get(mapComponentsToken, new HashMap<>()))
				.addExitMessages(node.node("ExitMessage").get(mapComponentsToken, new HashMap<>()))
				.addAdditionalData(node.node("AdditionalData").virtual() ? null : node.node("AdditionalData").get(dataMapToken))
				.build();
	}

	@Override
	public void serialize(Type type, @Nullable Region region, ConfigurationNode node) throws SerializationException {
		if(region == null) return;
		if(!region.getNames().isEmpty()) node.node("RegionName").set(mapComponentsToken, region.getNames());
		node.node("UUID").set(region.getUniqueId());
		node.node("World").set(region.getWorldKey().asString());
		if(region.getCuboid() != null) node.node("Cuboid").set(region.getCuboid());
		if(region.getChilds() != null && !region.getChilds().isEmpty()) node.node("Childs").setList(Region.class, region.getChilds());
		if(region.getFlags() != null && !region.getFlags().isEmpty()) node.node("Flags").set(flagsToken, region.getFlags());
		if(region.getMembers() != null && !region.getMembers().isEmpty()) node.node("Members").setList(MemberData.class, region.getMembers());
		node.node("Type").set(region.getType().toString());
		node.node("Created").set(region.getCreationTime());
		if(region.getJoinMessages() != null && !region.getJoinMessages().isEmpty()) node.node("JoinMessage").set(mapComponentsToken, region.getJoinMessages());
		if(region.getExitMessages() != null && !region.getExitMessages().isEmpty()) node.node("ExitMessage").set(mapComponentsToken, region.getExitMessages());
		if(region.getAllAdditionalData() != null && !region.getAllAdditionalData().isEmpty()) node.node("AdditionalData").set(dataMapToken, region.getAllAdditionalData());
	}

}
