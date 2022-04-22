package sawfowl.regionguard;

import sawfowl.regionguard.api.Flags;

public class Permissions {

	public static final String HELP = "regionguard.user.help";
	public static final String WAND = "regionguard.user.wand";
	public static final String CLAIM = "regionguard.user.claim";
	public static final String DELETE = "regionguard.user.delete";
	public static final String INFO = "regionguard.user.info";
	public static final String TRUST = "regionguard.user.trust";
	public static final String SET_MESSAGE = "regionguard.user.setmessage";
	public static final String SET_NAME = "regionguard.user.setname";
	public static final String CHANGE_SELECTOR = "regionguard.user.selector";
	public static final String FLAG_COMMAND = "regionguard.user.flag";
	public static final String CUI_COMMAND = "regionguard.user.wecui";
	public static final String FLAG = "regionguard.flags";
	public static final String BYPASS_FLAG = "regionguard.flag.bypass";
	public static final String LIMIT_BLOCKS = "regionguard.limit.blocks";
	public static final String LIMIT_CLAIMS = "regionguard.limit.claims";
	public static final String LIMIT_SUBDIVISIONS = "regionguard.limit.subdivisions";
	public static final String UNLIMIT_BLOCKS = "regionguard.unlimit.blocks";
	public static final String UNLIMIT_CLAIMS = "regionguard.unlimit.claims";
	public static final String UNLIMIT_SUBDIVISIONS = "regionguard.unlimit.subdivisions";
	public static final String STAFF_DELETE = "regionguard.staff.delete";
	public static final String STAFF_RESIZE = "regionguard.staff.resize";
	public static final String STAFF_TRUST = "regionguard.staff.trust";
	public static final String STAFF_SET_MESSAGE = "regionguard.staff.setmessage";
	public static final String STAFF_SET_NAME = "regionguard.staff.setname";
	public static final String STAFF_SET_REGION_TYPE = "regionguard.staff.setregiontype";
	public static final String STAFF_FLAG = "regionguard.staff.flag";
	public static final String STAFF_ADMINCLAIM = "regionguard.staff.adminclaim";
	
	public static String setFlag(Flags flagName) {
		return setFlag(flagName.toString());
	}
	
	public static String setFlag(String flagName) {
		return FLAG + "." + flagName.toLowerCase();
	}

	public static String bypassFlag(Flags flagName) {
		return BYPASS_FLAG + "." + flagName.toString().toLowerCase();
	}

}
