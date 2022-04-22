package sawfowl.regionguard.configure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplaceUtil {

	public class Keys {

		public static final String WORLD = "%world%";

		public static final String REGION = "%region%";

		public static final String TYPE = "%type%";

		public static final String FLAG = "%flag%";

		public static final String SOURCE = "%source%";

		public static final String TARGET = "%target%";

		public static final String PLAYER = "%player%";

		public static final String OWNER = "%owner%";

		public static final String TRUST_TYPE = "%trust-type%";

		public static final String TRUST_TYPES = "%trust-types%";

		public static final String MIN = "%min%";

		public static final String MAX = "%max%";

		public static final String VOLUME = "%volume%";

		public static final String SIZE = "%size%";

		public static final String POS = "%pos%";

		public static final String SELECTED = "%selected%";
		
	}

	public static Map<String, String>  replaceMap(List<String> keys, List<Object> values) {
		Map<String, String> map = new HashMap<String, String>();
		int i = 0;
		for(String key : keys) {
			if(i >= keys.size() || i >= values.size()) break;
			map.put(key, values.get(i).toString());
			i++;
		}
		return map;
	}

}
