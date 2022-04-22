package sawfowl.regionguard.api;

public enum RegionTypes {

	CLAIM {
		@Override
		public String toString() {
			return "Claim";
		}
	},
	ARENA {
		@Override
		public String toString() {
			return "Arena";
		}
	},
	SUBDIVISION {
		@Override
		public String toString() {
			return "Subdivision";
		}
	},
	ADMIN {
		@Override
		public String toString() {
			return "Admin";
		}
	},
	GLOBAL {
		@Override
		public String toString() {
			return "Global";
		}
	},
	UNSET;

	/**
	 * Search for a region type by name.
	 */
	public static RegionTypes valueOfName(String string) {
		for(RegionTypes type : RegionTypes.values()) {
			if(type.toString().equalsIgnoreCase(string)) return type;
		}
		return RegionTypes.UNSET;
	}

}
