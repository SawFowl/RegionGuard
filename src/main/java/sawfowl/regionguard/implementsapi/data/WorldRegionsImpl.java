package sawfowl.regionguard.implementsapi.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.api.data.ChunkNumber;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.data.WorldRegions;
public class WorldRegionsImpl implements WorldRegions {
	private final ResourceKey worldKey;
	private Region global;
	private final Map<ChunkNumber, Set<Region>> regionsMap;
	private final Set<Region> regions;
	private final Supplier<Region> supplierDefaultGlobal;
	public WorldRegionsImpl(ResourceKey worldKey, Supplier<Region> supplierDefaultGlobal) {
		this.worldKey = worldKey;
		this.supplierDefaultGlobal = supplierDefaultGlobal;
		regionsMap = new HashMap<>();
		regions = new HashSet<>();
	}

	@Override
	public ResourceKey getWorldKey() {
		return worldKey;
	}

	@Override
	public Region getGlobal() {
		return global == null ? supplierDefaultGlobal.get() : global;
	}

	@Override
	public Collection<Region> getRegions() {
		return Collections.unmodifiableSet(regions);
	}

	@Override
	public Region findRegion(Vector3i position) {
		// Region search time test
		/*
		int time = Instant.now().getNano();
		regionsMap
		.entrySet()
		.stream()
		.filter(
			entry -> entry.getKey().equalsTo(position)
		)
		.flatMap(
			entry -> entry
			.getValue()
			.stream()
			.filter(
				region -> region.getCuboid().containsIntersectsPosition(position)
			)
		)
		.findFirst()
		.orElse(getGlobal());
		System.out.println(Instant.now().getNano() - time);
		*/
		return regionsMap
			.entrySet()
			.stream()
			.filter(
				entry -> entry.getKey().equalsTo(position)
			)
			.flatMap(
				entry -> entry
				.getValue()
				.stream()
				.filter(
					region -> region.getCuboid().containsIntersectsPosition(position)
				)
			)
			.findFirst()
			.orElse(getGlobal())
		;
	}

	@Override
	public Optional<Region> findRegion(Vector3i position, Predicate<Region> filter) {
		return regionsMap
				.entrySet()
				.stream()
				.filter(
					entry -> entry.getKey().equalsTo(position)
				)
				.flatMap(
					entry -> entry
					.getValue()
					.stream()
					.filter(
						region -> region.getCuboid().containsIntersectsPosition(position) && filter.test(region)
					)
				)
				.findFirst()
			;
	}

	@Override
	public Region findIntersectsRegion(Region region) {
		return regions.stream().filter(other -> other.getCuboid().getAABB().intersects(region.getCuboid().getAABB())).findFirst().orElse(region);
	}

	@Override
	public int total() {
		return regions.size();
	}

	public Map<ChunkNumber, Set<Region>> getRegionsMap() {
		return Collections.unmodifiableMap(regionsMap);
	}

	public void add(Region region) {
		if(!region.isGlobal()) {
			regions.add(region);
			region.getChunkNumbers().forEach(cn -> regionsMap.computeIfAbsent(cn, c -> new HashSet<>()).add(region));
		} else setGlobal(region);
	}

	public void remove(Region region) {
		regions.remove(region);
		Set<ChunkNumber> chunks = new HashSet<>(regionsMap.keySet());
		chunks.forEach(chunk -> {
			if(regionsMap.containsKey(chunk) && regionsMap.get(chunk).contains(region)) {
				regionsMap.get(chunk).remove(region);
				if(regionsMap.get(chunk).isEmpty()) regionsMap.remove(chunk);
			}
		});
	}

	public void setGlobal(Region region) {
		global = region;
	}

	public void clear() {
		global = null;
		regionsMap.clear();
		regions.clear();
	}

}
