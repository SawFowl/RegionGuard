package sawfowl.regionguard;

import sawfowl.regionguard.api.Flags;

public class Permissions {

	// User
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
	public static final String BUY_BLOCKS = "regionguard.user.buy.blocks";
	public static final String BUY_CLAIMS = "regionguard.user.buy.claims";
	public static final String BUY_SUBDIVISIONS = "regionguard.user.buy.subdivisions";
	public static final String BUY_MEMBERS = "regionguard.user.buy.members";
	public static final String SELL_BLOCKS = "regionguard.user.sell.blocks";
	public static final String SELL_CLAIMS = "regionguard.user.sell.claims";
	public static final String SELL_SUBDIVISIONS = "regionguard.user.sell.subdivisions";
	public static final String SELL_MEMBERS = "regionguard.user.sell.members";
	public static final String LIST = "regionguard.user.list";
	public static final String TELEPORT = "regionguard.user.teleport";



	// Flags
	public static final String FLAG = "regionguard.flags";
	public static final String BYPASS_FLAG = "regionguard.flag.bypass";



	// Unlimit
	public static final String UNLIMIT_BLOCKS = "regionguard.unlimit.blocks";
	public static final String UNLIMIT_CLAIMS = "regionguard.unlimit.claims";
	public static final String UNLIMIT_SUBDIVISIONS = "regionguard.unlimit.subdivisions";
	public static final String UNLIMIT_MEMBERS = "regionguard.unlimit.members";



	// Staff perms
	public static final String STAFF_DELETE = "regionguard.staff.delete";
	public static final String STAFF_RESIZE = "regionguard.staff.resize";
	public static final String STAFF_TRUST = "regionguard.staff.trust";
	public static final String STAFF_SET_MESSAGE = "regionguard.staff.setmessage";
	public static final String STAFF_SET_NAME = "regionguard.staff.setname";
	public static final String STAFF_SET_REGION_TYPE = "regionguard.staff.setregiontype";
	public static final String STAFF_FLAG = "regionguard.staff.flag";
	public static final String STAFF_LIST = "regionguard.staff.list";
	public static final String STAFF_SETLIMIT_BLOCKS = "regionguard.staff.setlimit.blocks";
	public static final String STAFF_SETLIMIT_CLAIMS = "regionguard.staff.setlimit.claims";
	public static final String STAFF_SETLIMIT_SUBDIVISIONS = "regionguard.staff.setlimit.subdivisions";
	public static final String STAFF_SETLIMIT_MEMBERS = "regionguard.staff.setlimit.members";
	public static final String STAFF_ADMINCLAIM = "regionguard.staff.adminclaim";



	// Metaperms
	public static final String LIMIT_BLOCKS = "regionguard.limit.blocks";
	public static final String LIMIT_CLAIMS = "regionguard.limit.claims";
	public static final String LIMIT_SUBDIVISIONS = "regionguard.limit.subdivisions";
	public static final String LIMIT_MEMBERS = "regionguard.limit.members";
	public static final String LIMIT_MAX_BLOCKS = "regionguard.limit.max.blocks";
	public static final String LIMIT_MAX_CLAIMS = "regionguard.limit.max.claims";
	public static final String LIMIT_MAX_SUBDIVISIONS = "regionguard.limit.max.subdivisions";
	public static final String LIMIT_MAX_MEMBERS = "regionguard.limit.max.members";
	public static final String BUY_BLOCK_PRICE = "regionguard.buy.blockprice";
	public static final String BUY_REGION_PRICE = "regionguard.buy.regionprice";
	public static final String BUY_SUBDIVISION_PRICE = "regionguard.buy.subdivisionprice";
	public static final String BUY_MEMBERS_PRICE = "regionguard.buy.memberprice";
	public static final String SELL_BLOCK_PRICE = "regionguard.sell.blockprice";
	public static final String SELL_REGION_PRICE = "regionguard.sell.regionprice";
	public static final String SELL_SUBDIVISION_PRICE = "regionguard.sell.subdivisionprice";
	public static final String SELL_MEMBERS_PRICE = "regionguard.sell.memberprice";
	public static final String TRANSACRION_CURRENCY = "regionguard.transaction.currency";
	
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
