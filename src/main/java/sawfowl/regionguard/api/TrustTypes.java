package sawfowl.regionguard.api;

import java.util.stream.Stream;

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

	public static Stream<String> getValues() {
		return Stream.of(TrustTypes.values()).filter(value -> value != WITHOUT_TRUST).map(TrustTypes::toString);
	}

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
