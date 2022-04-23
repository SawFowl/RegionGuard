package sawfowl.regionguard.configure;

public class LocalesPaths {

	private static final String PATH_COMMANDS = "Commands";

	private static final String PATH_EVENTS = "Events";

	private static final String PATH_HOVER = "HoverText";

	private static final String PATH_COMMAND_CLAIM = "Claim";

	private static final String PATH_COMMAND_DELETE = "Delete";

	private static final String PATH_COMMAND_INFO = "Info";

	private static final String PATH_COMMAND_LIMITS = "Limits";

	private static final String PATH_COMMAND_SET_MESSAGE = "SetMessage";

	private static final String PATH_COMMAND_SET_NAME = "SetName";

	private static final String PATH_COMMAND_FLAG = "FlagValue";

	private static final String PATH_COMMAND_LEAVE = "Leave";

	private static final String PATH_COMMAND_SETOWNER = "SetOwner";

	private static final String PATH_COMMAND_TRUST = "Trust";

	private static final String PATH_COMMAND_UNTRUST = "Untrust";

	private static final String PATH_COMMAND_WAND = "Wand";

	private static final String PATH_COMMAND_WECUI = "WeCUI";

	private static final String PATH_COMMAND_SELECTOR = "Selector";

	private static final String PATH_COMMAND_REGION_TYPE = "RegionType";

	private static final String PATH_COMMAND_MAIN = "Help";

	private static final String PATH_EXCEPTIONS = "Exceptions";

	private static final String PATH_REGION = "Region";

	private static final String PATH_REGION_CREATE = "Create";

	private static final String PATH_REGION_RESIZE = "Resize";

	private static final String PATH_REGION_WAND_INFO = "RegionWandInfo";

	private static final String PATH_FLAG_RESULT_FALSE = "FlagResultFalse";

	private static final String PATH_FLAG_RESULT_TRUE = "FlagResultTrue";

	public static final Object NOT_PERMITTED = "NotPermitted";
	public static final Object PADDING = "Padding";

	public static final Object[] COMMANDS_ONLY_PLAYER = {PATH_COMMANDS, PATH_EXCEPTIONS, "OnlyPlayer"};
	public static final Object[] COMMANDS_EXCEPTION_REGION_NOT_FOUND = {PATH_COMMANDS, PATH_EXCEPTIONS, "RegionNotFound"};
	public static final Object[] COMMANDS_EXCEPTION_PLAYER_NOT_OWNER = {PATH_COMMANDS, PATH_EXCEPTIONS, "PlayerNotOwner"};
	public static final Object[] COMMANDS_EXCEPTION_PLAYER_NOT_PRESENT= {PATH_COMMANDS, PATH_EXCEPTIONS, "PlayerNotPresent"};

	public static final Object[] COMMANDS_TITLE = {PATH_COMMANDS, PATH_COMMAND_MAIN, "Title"};
	public static final Object[] COMMANDS_USED_BY_NON_PLAYER = {PATH_COMMANDS, PATH_COMMAND_MAIN, "UsedByNonPlayer"};
	public static final Object[] COMMANDS_WAND = {PATH_COMMANDS, PATH_COMMAND_MAIN, "Wand"};
	public static final Object[] COMMANDS_CLAIM = {PATH_COMMANDS, PATH_COMMAND_MAIN, "Claim"};
	public static final Object[] COMMANDS_DELETE = {PATH_COMMANDS, PATH_COMMAND_MAIN, "Delete"};
	public static final Object[] COMMANDS_INFO = {PATH_COMMANDS, PATH_COMMAND_MAIN, "Info"};
	public static final Object[] COMMANDS_LIMITS = {PATH_COMMANDS, PATH_COMMAND_MAIN, "Limits"};
	public static final Object[] COMMANDS_SET_NAME = {PATH_COMMANDS, PATH_COMMAND_MAIN, "SetName"};
	public static final Object[] COMMANDS_SET_MESSAGE = {PATH_COMMANDS, PATH_COMMAND_MAIN, "SetMessage"};
	public static final Object[] COMMANDS_FLAG = {PATH_COMMANDS, PATH_COMMAND_MAIN, "FlagValue"};
	public static final Object[] COMMANDS_LEAVE = {PATH_COMMANDS, PATH_COMMAND_MAIN, "Leave"};
	public static final Object[] COMMANDS_SETOWNER = {PATH_COMMANDS, PATH_COMMAND_MAIN, "SetOwner"};
	public static final Object[] COMMANDS_TRUST = {PATH_COMMANDS, PATH_COMMAND_MAIN, "Trust"};
	public static final Object[] COMMANDS_UNTRUST = {PATH_COMMANDS, PATH_COMMAND_MAIN, "Untrust"};
	public static final Object[] COMMANDS_SET_SELECTOR_TYPE = {PATH_COMMANDS, PATH_COMMAND_MAIN, "SelectorType"};
	public static final Object[] COMMANDS_SET_CREATING_TYPE = {PATH_COMMANDS, PATH_COMMAND_MAIN, "CreatingType"};
	public static final Object[] COMMANDS_WECUI = {PATH_COMMANDS, PATH_COMMAND_MAIN, "WeCUI"};

	public static final Object[] COMMAND_CLAIM_WORLD_NOT_FOUND = {PATH_COMMANDS, PATH_COMMAND_CLAIM,  PATH_EXCEPTIONS, "WorldNotFound"};
	public static final Object[] COMMAND_CLAIM_REGION_NOT_FOUND = {PATH_COMMANDS, PATH_COMMAND_CLAIM, PATH_EXCEPTIONS, "RegionNotFound"};
	public static final Object[] COMMAND_CLAIM_CANCEL = {PATH_COMMANDS, PATH_COMMAND_CLAIM, "CancelCreating"};
	public static final Object[] COMMAND_CLAIM_SUCCESS = {PATH_COMMANDS, PATH_COMMAND_CLAIM, "Success"};

	public static final Object[] COMMAND_DELETE_CONFIRMATION_REQUEST = {PATH_COMMANDS, PATH_COMMAND_DELETE, "ConfirmRequest"};
	public static final Object[] COMMAND_DELETE_CHILD_DELETED = {PATH_COMMANDS, PATH_COMMAND_DELETE, "ChildDeleted"};
	public static final Object[] COMMAND_DELETE_DELETED = {PATH_COMMANDS, PATH_COMMAND_DELETE, "Deleted"};
	public static final Object[] COMMAND_DELETE_DELETED_MAIN_AND_CHILDS = {PATH_COMMANDS, PATH_COMMAND_DELETE, "DeletedMainAndChilds"};


	public static final Object[] COMMAND_INFO_HEADER = {PATH_COMMANDS, PATH_COMMAND_INFO, "Header"};
	public static final Object[] COMMAND_INFO_DELETE = {PATH_COMMANDS, PATH_COMMAND_INFO, "Delete"};
	public static final Object[] COMMAND_INFO_SEE_FLAGS = {PATH_COMMANDS, PATH_COMMAND_INFO, "SeeFlags"};
	public static final Object[] COMMAND_INFO_REGION_UUID = {PATH_COMMANDS, PATH_COMMAND_INFO, "RegionUUID"};
	public static final Object[] COMMAND_INFO_REGION_NAME = {PATH_COMMANDS, PATH_COMMAND_INFO, "RegionName"};
	public static final Object[] COMMAND_INFO_REGION_TYPE = {PATH_COMMANDS, PATH_COMMAND_INFO, "RegionType"};
	public static final Object[] COMMAND_INFO_CREATED = {PATH_COMMANDS, PATH_COMMAND_INFO, "Created"};
	public static final Object[] COMMAND_INFO_OWNER = {PATH_COMMANDS, PATH_COMMAND_INFO, "Owner"};
	public static final Object[] COMMAND_INFO_OWNER_UUID = {PATH_COMMANDS, PATH_COMMAND_INFO, "OwnerUUID"};
	public static final Object[] COMMAND_INFO_MEMBERS = {PATH_COMMANDS, PATH_COMMAND_INFO, "Members"};
	public static final Object[] COMMAND_INFO_MIN_POS = {PATH_COMMANDS, PATH_COMMAND_INFO, "MinPos"};
	public static final Object[] COMMAND_INFO_MAX_POS = {PATH_COMMANDS, PATH_COMMAND_INFO, "MaxPos"};
	public static final Object[] COMMAND_INFO_SELECTION_TYPE = {PATH_COMMANDS, PATH_COMMAND_INFO, "SelectionType"};

	public static final Object[] COMMAND_LIMITS_HEADER = {PATH_COMMANDS, PATH_COMMAND_LIMITS, "Header"};
	public static final Object[] COMMAND_LIMITS_CLAIMS = {PATH_COMMANDS, PATH_COMMAND_LIMITS, "Claims"};
	public static final Object[] COMMAND_LIMITS_SUBDIVISIONS = {PATH_COMMANDS, PATH_COMMAND_LIMITS, "Subdivisions"};
	public static final Object[] COMMAND_LIMITS_BLOCKS = {PATH_COMMANDS, PATH_COMMAND_LIMITS, "Blocks"};

	public static final Object[] COMMAND_SET_MESSAGE_NOT_TRUSTED = {PATH_COMMANDS, PATH_COMMAND_SET_MESSAGE, PATH_EXCEPTIONS, "NotTrusted"};
	public static final Object[] COMMAND_SET_MESSAGE_LOW_TRUST = {PATH_COMMANDS, PATH_COMMAND_SET_MESSAGE, PATH_EXCEPTIONS, "LowTrust"};
	public static final Object[] COMMAND_SET_MESSAGE_TYPE_NOT_PRESENT = {PATH_COMMANDS, PATH_COMMAND_SET_MESSAGE, PATH_EXCEPTIONS, "TypeNotPresent"};
	public static final Object[] COMMAND_SET_MESSAGE_NOT_PRESENT = {PATH_COMMANDS, PATH_COMMAND_SET_MESSAGE, PATH_EXCEPTIONS, "MessageNotPresent"};
	public static final Object[] COMMAND_SET_MESSAGE_TOO_LONG = {PATH_COMMANDS, PATH_COMMAND_SET_MESSAGE, PATH_EXCEPTIONS, "TooLong"};
	public static final Object[] COMMAND_SET_MESSAGE_SUCCESS_JOIN = {PATH_COMMANDS, PATH_COMMAND_SET_MESSAGE, "SuccessSetJoin"};
	public static final Object[] COMMAND_SET_MESSAGE_SUCCESS_EXIT = {PATH_COMMANDS, PATH_COMMAND_SET_MESSAGE, "SuccessSetExit"};
	public static final Object[] COMMAND_SET_MESSAGE_SUCCESS_CLEAR_JOIN = {PATH_COMMANDS, PATH_COMMAND_SET_MESSAGE, "SuccessClearJoin"};
	public static final Object[] COMMAND_SET_MESSAGE_SUCCESS_CLEAR_EXIT = {PATH_COMMANDS, PATH_COMMAND_SET_MESSAGE, "SuccessClearExit"};

	public static final Object[] COMMAND_SET_NAME_NOT_TRUSTED = {PATH_COMMANDS, PATH_COMMAND_SET_NAME, PATH_EXCEPTIONS, "NotTrusted"};
	public static final Object[] COMMAND_SET_NAME_LOW_TRUST = {PATH_COMMANDS, PATH_COMMAND_SET_NAME, PATH_EXCEPTIONS, "LowTrust"};
	public static final Object[] COMMAND_SET_NAME_NOT_PRESENT = {PATH_COMMANDS, PATH_COMMAND_SET_NAME, PATH_EXCEPTIONS, "NewNameNotPresent"};
	public static final Object[] COMMAND_SET_NAME_TOO_LONG = {PATH_COMMANDS, PATH_COMMAND_SET_NAME, PATH_EXCEPTIONS, "TooLong"};
	public static final Object[] COMMAND_SET_NAME_SUCCESS = {PATH_COMMANDS, PATH_COMMAND_SET_NAME, "Success"};
	public static final Object[] COMMAND_SET_NAME_CLEARED = {PATH_COMMANDS, PATH_COMMAND_SET_NAME, "Cleared"};

	public static final Object[] COMMAND_FLAG_NOT_PERMITTED_REGION = {PATH_COMMANDS, PATH_COMMAND_FLAG, NOT_PERMITTED, PATH_EXCEPTIONS, "Region"};
	public static final Object[] COMMAND_FLAG_NOT_PERMITTED_FLAG = {PATH_COMMANDS, PATH_COMMAND_FLAG, NOT_PERMITTED, PATH_EXCEPTIONS, "FlagValue"};
	public static final Object[] COMMAND_FLAG_VALUE_NOT_PRESENT = {PATH_COMMANDS, PATH_COMMAND_FLAG, PATH_EXCEPTIONS, "ValueNotPresent"};
	public static final Object[] COMMAND_FLAG_WRONG_SOURCE = {PATH_COMMANDS, PATH_COMMAND_FLAG, PATH_EXCEPTIONS, "WrongSource"};
	public static final Object[] COMMAND_FLAG_WRONG_TARGET = {PATH_COMMANDS, PATH_COMMAND_FLAG, PATH_EXCEPTIONS, "WrongTarget"};
	public static final Object[] COMMAND_FLAG_SUCCESS = {PATH_COMMANDS, PATH_COMMAND_FLAG, "Success"};
	public static final Object[] COMMAND_FLAG_LIST = {PATH_COMMANDS, PATH_COMMAND_FLAG, "FlagList"};
	public static final Object[] COMMAND_FLAG_HOVER_VALUES = {PATH_COMMANDS, PATH_COMMAND_FLAG, PATH_HOVER, "Values"};
	public static final Object[] COMMAND_FLAG_HOVER_REMOVE = {PATH_COMMANDS, PATH_COMMAND_FLAG, PATH_HOVER, "Remove"};
	public static final Object[] COMMAND_FLAG_HOVER_TRUE = {PATH_COMMANDS, PATH_COMMAND_FLAG, PATH_HOVER, "SetTrue"};
	public static final Object[] COMMAND_FLAG_HOVER_FALSE = {PATH_COMMANDS, PATH_COMMAND_FLAG, PATH_HOVER, "SetFalse"};
	public static final Object[] COMMAND_FLAG_HOVER_SUGGEST_ARGS = {PATH_COMMANDS, PATH_COMMAND_FLAG, PATH_HOVER, "SuggestArgs"};

	public static final Object[] COMMAND_LEAVE_CONFIRMATION_REQUEST = {PATH_COMMANDS, PATH_COMMAND_LEAVE, "ConfirmRequest"};
	public static final Object[] COMMAND_LEAVE_PLAYER_IS_OWNER = {PATH_COMMANDS, PATH_COMMAND_LEAVE, "PlayerIsOwner"};
	public static final Object[] COMMAND_LEAVE_PLAYER_NOT_TRUSTED = {PATH_COMMANDS, PATH_COMMAND_LEAVE, "PlayerNotTrusted"};
	public static final Object[] COMMAND_LEAVE_SUCCESS = {PATH_COMMANDS, PATH_COMMAND_LEAVE, "Success"};

	public static final Object[] COMMAND_SETOWNER_EXCEPTION_ADMIN = {PATH_COMMANDS, PATH_COMMAND_SETOWNER, PATH_EXCEPTIONS, "AdminClaim"};
	public static final Object[] COMMAND_SETOWNER_EXCEPTION_OWNER_TARGET_SELF = {PATH_COMMANDS, PATH_COMMAND_SETOWNER, PATH_EXCEPTIONS, "OwnerTargetSelf"};
	public static final Object[] COMMAND_SETOWNER_EXCEPTION_STAFF_TARGET_OWNER = {PATH_COMMANDS, PATH_COMMAND_SETOWNER, PATH_EXCEPTIONS, "StaffTargetOwner"};
	public static final Object[] COMMAND_SETOWNER_EXCEPTION_PLAYER_IS_NOT_OWNER = {PATH_COMMANDS, PATH_COMMAND_SETOWNER, PATH_EXCEPTIONS, "PlayerIsNotOwner"};
	public static final Object[] COMMAND_SETOWNER_CONFIRMATION_REQUEST = {PATH_COMMANDS, PATH_COMMAND_SETOWNER, "ConfirmRequest"};
	public static final Object[] COMMAND_SETOWNER_SUCCESS_FROM_STAFF = {PATH_COMMANDS, PATH_COMMAND_SETOWNER, "SuccessFromStaff"};
	public static final Object[] COMMAND_SETOWNER_SUCCESS_PLAYER = {PATH_COMMANDS, PATH_COMMAND_SETOWNER, "SuccessPlayer"};
	public static final Object[] COMMAND_SETOWNER_SUCCESS_TARGET = {PATH_COMMANDS, PATH_COMMAND_SETOWNER, "SuccessTarget"};

	public static final Object[] COMMAND_TRUST_EXCEPTION_ADMINCLAIM = {PATH_COMMANDS, PATH_COMMAND_TRUST, PATH_EXCEPTIONS, "AdminClaim"};
	public static final Object[] COMMAND_TRUST_EXCEPTION_NEED_TRUST_TYPE = {PATH_COMMANDS, PATH_COMMAND_TRUST, PATH_EXCEPTIONS, "NeedTrustType"};
	public static final Object[] COMMAND_TRUST_EXCEPTION_PLAYER_IS_NOT_OWNER = {PATH_COMMANDS, PATH_COMMAND_TRUST, PATH_EXCEPTIONS, "PlayerIsNotOwner"};
	public static final Object[] COMMAND_TRUST_EXCEPTION_TRUST_TYPE_NOT_PRESENT = {PATH_COMMANDS, PATH_COMMAND_TRUST, PATH_EXCEPTIONS, "TypeNotPresent"};
	public static final Object[] COMMAND_TRUST_EXCEPTION_TARGET_SELF = {PATH_COMMANDS, PATH_COMMAND_TRUST, PATH_EXCEPTIONS, "TargetSelf"};
	public static final Object[] COMMAND_TRUST_SUCCESS_PLAYER = {PATH_COMMANDS, PATH_COMMAND_TRUST, "SuccessPlayer"};
	public static final Object[] COMMAND_TRUST_EXCEPTION_SUCCESS_TARGET = {PATH_COMMANDS, PATH_COMMAND_TRUST, "SuccessTarget"};

	public static final Object[] COMMAND_UNTRUST_EXCEPTION_PLAYER_IS_OWNER = {PATH_COMMANDS, PATH_COMMAND_UNTRUST, PATH_EXCEPTIONS, "PlayerIsOwner"};
	public static final Object[] COMMAND_UNTRUST_EXCEPTION_NEED_TRUST_TYPE = {PATH_COMMANDS, PATH_COMMAND_UNTRUST, PATH_EXCEPTIONS, "NeedTrustType"};
	public static final Object[] COMMAND_UNTRUST_EXCEPTION_TARGET_SELF = {PATH_COMMANDS, PATH_COMMAND_UNTRUST, PATH_EXCEPTIONS, "TargetSelf"};
	public static final Object[] COMMAND_UNTRUST_EXCEPTION_TARGET_MANAGER = {PATH_COMMANDS, PATH_COMMAND_UNTRUST, PATH_EXCEPTIONS, "TargetManager"};
	public static final Object[] COMMAND_UNTRUST_SUCCESS_PLAYER = {PATH_COMMANDS, PATH_COMMAND_UNTRUST, "SuccessPlayer"};
	public static final Object[] COMMAND_UNTRUST_SUCCESS_TARGET = {PATH_COMMANDS, PATH_COMMAND_UNTRUST, "SuccessTarget"};

	public static final Object[] COMMAND_WAND_EXCEPTION_ITEM_EXIST = {PATH_COMMANDS, PATH_COMMAND_WAND, PATH_EXCEPTIONS, "ItemExist"};
	public static final Object[] COMMAND_WAND_EXCEPTION_INVENTORY_IS_FULL = {PATH_COMMANDS, PATH_COMMAND_WAND, PATH_EXCEPTIONS, "InventoryIsFull"};
	public static final Object[] COMMAND_WAND_SUCCESS = {PATH_COMMANDS, PATH_COMMAND_WAND, "Success"};

	public static final Object[] COMMAND_WECUI_ENABLE = {PATH_COMMANDS, PATH_COMMAND_WECUI, PATH_EXCEPTIONS, "Enable"};
	public static final Object[] COMMAND_WECUI_DISABLE = {PATH_COMMANDS, PATH_COMMAND_WECUI, PATH_EXCEPTIONS, "Disable"};

	public static final Object[] COMMAND_SELECTOR_EXCEPTION_NOT_PRESENT = {PATH_COMMANDS, PATH_COMMAND_SELECTOR, PATH_EXCEPTIONS, "NotPresent"};
	public static final Object[] COMMAND_SELECTOR_CUBOID = {PATH_COMMANDS, PATH_COMMAND_SELECTOR, "Cuboid"};
	public static final Object[] COMMAND_SELECTOR_FLAT = {PATH_COMMANDS, PATH_COMMAND_SELECTOR, "Flat"};

	public static final Object[] COMMAND_REGION_TYPE_EXCEPTION_NOT_PRESENT = {PATH_COMMANDS, PATH_COMMAND_REGION_TYPE, PATH_EXCEPTIONS, "NotPresent"};
	public static final Object[] COMMAND_REGION_TYPE_CLAIM = {PATH_COMMANDS, PATH_COMMAND_REGION_TYPE, "Claim"};
	public static final Object[] COMMAND_REGION_TYPE_ARENA = {PATH_COMMANDS, PATH_COMMAND_REGION_TYPE, "Arena"};
	public static final Object[] COMMAND_REGION_TYPE_ADMIN = {PATH_COMMANDS, PATH_COMMAND_REGION_TYPE, "Admin"};
	
	
	


	public static final Object[] REGION_CREATE_EXCEPTION_ADMIN_CLAIM = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, PATH_EXCEPTIONS, "AdminClaim"};
	public static final Object[] REGION_CREATE_EXCEPTION_POSITION_LOCKED = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, PATH_EXCEPTIONS, "PositionLocked"};
	public static final Object[] REGION_CREATE_EXCEPTION_LARGE_VOLUME_REGIONS = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, PATH_EXCEPTIONS, "LargeVolumeRegions"};
	public static final Object[] REGION_CREATE_EXCEPTION_LARGE_VOLUME_SUBDIVISIONS = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, PATH_EXCEPTIONS, "LargeVolumeSubdivisions"};
	public static final Object[] REGION_CREATE_EXCEPTION_LARGE_VOLUME_BLOCKS = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, PATH_EXCEPTIONS, "LargeVolumeBlocks"};
	public static final Object[] REGION_CREATE_EXCEPTION_INCORRECT_COORDS = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, PATH_EXCEPTIONS, "IncorrectСoords"};
	public static final Object[] REGION_CREATE_EXCEPTION_REGIONS_INTERSECT = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, PATH_EXCEPTIONS, "RegionsIntersect"};
	public static final Object[] REGION_CREATE_EXCEPTION_CENCELLED_EVENT = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, PATH_EXCEPTIONS, "CancelledEvent"};
	public static final Object[] REGION_CREATE_EXCEPTION_WRONG_SUBDIVISION_POSITIONS = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, PATH_EXCEPTIONS, "WrongSubdivisionPositions"};
	public static final Object[] REGION_CREATE_SETPOS = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, "SetPos"};
	public static final Object[] REGION_CREATE_CREATE_BASIC = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, "CreateBasic"};
	public static final Object[] REGION_CREATE_SUBDIVISION = {PATH_EVENTS, PATH_REGION, PATH_REGION_CREATE, "CreateSubdivision"};

	public static final Object[] REGION_WAND_TYPE = {PATH_EVENTS, PATH_REGION_WAND_INFO, "Type"};
	public static final Object[] REGION_WAND_OWNER = {PATH_EVENTS, PATH_REGION_WAND_INFO, "Owner"};

	public static final Object[] REGION_RESIZE_EXCEPTION_INCORRECT_COORDS = {PATH_EVENTS, PATH_REGION, PATH_REGION_RESIZE, PATH_EXCEPTIONS, "IncorrectСoords"};
	public static final Object[] REGION_RESIZE_EXCEPTION_SMALL_VOLUME = {PATH_EVENTS, PATH_REGION, PATH_REGION_RESIZE, PATH_EXCEPTIONS, "SmallVolume"};
	public static final Object[] REGION_RESIZE_EXCEPTION_LARGE_VOLUME = {PATH_EVENTS, PATH_REGION, PATH_REGION_RESIZE, PATH_EXCEPTIONS, "LargeVolume"};
	public static final Object[] REGION_RESIZE_START = {PATH_EVENTS, PATH_REGION, PATH_REGION_RESIZE, "Start"};
	public static final Object[] REGION_RESIZE_FINISH = {PATH_EVENTS, PATH_REGION, PATH_REGION_RESIZE, "Finish"};

	public static final Object[] INTERACT_BLOCK_CANCEL_PRIMARY = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "InteractBlockPrimary"};
	public static final Object[] INTERACT_BLOCK_CANCEL_SECONDARY = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "InteractBlockSecondary"};
	public static final Object[] CANCEL_GROWTH = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "Growth"};
	public static final Object[] CANCEL_PLACE = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "Place"};
	public static final Object[] CANCEL_BREAK = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "Break"};
	public static final Object[] DISABLE_FLY = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "Fly"};
	public static final Object[] DISABLE_FLY_ON_JOIN = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "FlyOnJoin"};
	public static final Object[] TELEPORT_OTHER_TO_REGION = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "TeleportOtherToRegion"};
	public static final Object[] TELEPORT_OTHER_FROM_REGION = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "TeleportOtherFromRegion"};
	public static final Object[] TELEPORT_ENDERPEARL_TO_REGION = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "TeleportEnderpearlToRegion"};
	public static final Object[] TELEPORT_ENDERPEARL_FROM_REGION = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "TeleportEnderpearlFromRegion"};
	public static final Object[] CANCEL_JOIN = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "CancelJoin"};
	public static final Object[] CANCEL_EXIT = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "CancelExit"};
	public static final Object[] TELEPORT_TO_REGION = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "TeleportToRegion"};
	public static final Object[] TELEPORT_FROM_REGION = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "TeleportFromRegion"};
	public static final Object[] INTERACT_ENTITY_CANCEL_PRIMARY = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "InteractEntityPrimary"};
	public static final Object[] INTERACT_ENTITY_CANCEL_SECONDARY = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "InteractEntitySecondary"};

	public static final Object[] PVP = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "PvP"};
	public static final Object[] ENTITY_DAMAGE = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "EntityDamage"};

	public static final Object[] KEEP_EXP = {PATH_EVENTS, PATH_FLAG_RESULT_TRUE, "KeepExp"};
	public static final Object[] KEEP_INVENTORY = {PATH_EVENTS, PATH_FLAG_RESULT_TRUE, "KeepInventory"};

	public static final Object[] IMPACT_BLOCK = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "ImpactBlock"};
	public static final Object[] IMPACT_ENTITY = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "ImpactEntity"};

	public static final Object[] COMMAND_EXECUTE = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "CommandExecute"};
	public static final Object[] COMMAND_EXECUTE_PVP = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "CommandExecutePvP"};

	public static final Object[] ITEM_DROP = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "ItemDrop"};
	public static final Object[] ITEM_PICKUP = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "ItemPickup"};

	public static final Object[] RIDING = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "Riding"};

	public static final Object[] SPAWN = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "Spawn"};

	public static final Object[] INTERACT_ITEM = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "InteractItem"};

	public static final Object[] ITEM_USE = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "InteractItem"};

	public static final Object[] PORTAL_USE = {PATH_EVENTS, PATH_FLAG_RESULT_FALSE, "PortalUse"};
	

}