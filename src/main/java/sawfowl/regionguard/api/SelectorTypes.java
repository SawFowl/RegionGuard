package sawfowl.regionguard.api;

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

	/**
	 * Search for a selector type by name.
	 */
	public static SelectorTypes checkType(String selectorType) {
		return selectorType.equalsIgnoreCase(CUBOID.toString()) ? CUBOID : FLAT;
	}

}
