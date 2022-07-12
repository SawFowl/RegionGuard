package sawfowl.regionguard.api;


public enum TrustTypes {

	HUNTER {
		@Override
		public String toString() {
			return "Hunter";
		}
	},
	SLEEP {
		@Override
		public String toString() {
			return "Sleep";
		}
	},
	CONTAINER {
		@Override
		public String toString() {
			return "Container";
		}
	},
	USER {
		@Override
		public String toString() {
			return "User";
		}
	},
	BUILDER {
		@Override
		public String toString() {
			return "Builder";
		}
	},
	MANAGER {
		@Override
		public String toString() {
			return "Manager";
		}
	},
	OWNER {
		@Override
		public String toString() {
			return "Owner";
		}
	},
	WITHOUT_TRUST;

	/**
	 * Search for a trust type by name.
	 */
	public static TrustTypes checkType(String string) {
		for(TrustTypes type : TrustTypes.values()) {
			if(type.toString().equalsIgnoreCase(string)) return type;
		}
		return WITHOUT_TRUST;
	}

}
