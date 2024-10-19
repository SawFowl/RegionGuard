package sawfowl.regionguard.api;

import java.util.List;
import java.util.stream.Stream;

public enum SelectorTypes {

	FLAT {
		@Override
		public String toString() {
			return "Flat";
		}
	},
	CUBOID {
		@Override
		public String toString() {
			return "Cuboid";
		}
	};

	private static final List<String> VALUES = Stream.of(SelectorTypes.values()).map(type -> type.toString()).toList();

	/**
	 * Search for a selector type by name.
	 */
	public static SelectorTypes checkType(String selectorType) {
		return selectorType.equalsIgnoreCase(CUBOID.toString()) ? CUBOID : FLAT;
	}

	public static List<String> getValues() {
		return VALUES;
	}

}
