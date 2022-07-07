package sawfowl.regionguard.configure;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.api.LocaleService;
import sawfowl.localeapi.utils.AbstractLocaleUtil;

public class Locales {

	private final LocaleService localeService;
	private final boolean json;
	private String[] localesTags;
	public Locales(LocaleService localeService, boolean json) {
		this.localeService = localeService;
		this.json = json;
		localeService.createPluginLocale("regionguard", ConfigTypes.JSON, org.spongepowered.api.util.locale.Locales.DEFAULT);
		localeService.createPluginLocale("regionguard", ConfigTypes.JSON, org.spongepowered.api.util.locale.Locales.RU_RU);
		generateDefault();
		generateRu();
	}

	public Component getText(Locale locale, Object... path) {
		return getAbstractLocaleUtil(locale).getComponent(json, path);
	}

	public Component getTextWithReplaced(Locale locale, Map<String, String> map, Object... path) {
		return replace(getText(locale, path), map);
	}

	public Component getTextReplaced(Locale locale, Map<String, Component> map, Object... path) {
		return replaceComponent(getText(locale, path), map);
	}

	public Component getTextFromDefault(Object... path) {
		return getAbstractLocaleUtil(org.spongepowered.api.util.locale.Locales.DEFAULT).getComponent(json, path);
	}

	public LocaleService getLocaleService() {
		return localeService;
	}

	public String[] getLocalesTags() {
		if(localesTags != null) return localesTags;
		return localesTags = localeService.getLocalesList().stream().map(Locale::toLanguageTag).collect(Collectors.toList()).stream().toArray(String[]::new);
	}

	private void generateDefault() {
		Locale locale = org.spongepowered.api.util.locale.Locales.DEFAULT;
		boolean save = check(locale, toText("&eThere is no economy plugin on the server. Some of the functionality will not be available."), null, LocalesPaths.ECONOMY_NOT_FOUND);
		save = check(locale, toText("&cYou do not have permission for this action."), null, LocalesPaths.NOT_PERMITTED);
		save = check(locale, toText("&3="), null, LocalesPaths.PADDING) || save;
		save = check(locale, toText("&cThe command can only be executed by a player."), null, LocalesPaths.COMMANDS_ONLY_PLAYER) || save;
		save = check(locale, toText("&cThere is no region in your position."), null, LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND) || save;
		save = check(locale, toText("&cYou do not own this region."), null, LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_OWNER) || save;
		save = check(locale, toText("&cYou must specify the player's nickname."), null, LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_PRESENT) || save;

		save = check(locale, toText("&bCommands list."), null, LocalesPaths.COMMANDS_TITLE) || save;
		save = check(locale, toText("&cThe commands are only available to players."), null, LocalesPaths.COMMANDS_USED_BY_NON_PLAYER) || save;
		save = check(locale, toText("&6Get an item to create regions."), null, LocalesPaths.COMMANDS_WAND) || save;
		save = check(locale, toText("&6Claim the allocated region."), null, LocalesPaths.COMMANDS_CLAIM) || save;
		save = check(locale, toText("&6Delete the region."), null, LocalesPaths.COMMANDS_DELETE) || save;
		save = check(locale, toText("&6Information about the region."), null, LocalesPaths.COMMANDS_INFO) || save;
		save = check(locale, toText("&6Information about your limits."), null, LocalesPaths.COMMANDS_LIMITS) || save;
		save = check(locale, toText("&6Set the name of the region."), null, LocalesPaths.COMMANDS_SET_NAME) || save;
		save = check(locale, toText("&6Set/remove the join/exit message in the region."), null, LocalesPaths.COMMANDS_SET_MESSAGE) || save;
		save = check(locale, toText("&6Set the flag parameters."), null, LocalesPaths.COMMANDS_FLAG) || save;
		save = check(locale, toText("&6Leave from region."), null, LocalesPaths.COMMANDS_LEAVE) || save;
		save = check(locale, toText("&6Set the owner of the region."), null, LocalesPaths.COMMANDS_SETOWNER) || save;
		save = check(locale, toText("&6Add a player to the region and specify his rights in the region."), null, LocalesPaths.COMMANDS_TRUST) || save;
		save = check(locale, toText("&6Remove a player from the region."), null, LocalesPaths.COMMANDS_UNTRUST) || save;
		save = check(locale, toText("&6Select the type of area selection."), null, LocalesPaths.COMMANDS_SET_SELECTOR_TYPE) || save;
		save = check(locale, toText("&6Select the type of region to be created."), null, LocalesPaths.COMMANDS_SET_CREATING_TYPE) || save;
		save = check(locale, toText("&6Switch the sending status of WECui packets."), null, LocalesPaths.COMMANDS_WECUI) || save;
		save = check(locale, toText("&6Show list of regions."), null, LocalesPaths.COMMANDS_LIST) || save;
		save = check(locale, toText("&6Payment in game currency to increase the limit of blocks."), null, LocalesPaths.COMMANDS_BUYBLOCKS) || save;
		save = check(locale, toText("&6Payment in game currency to increase the limit of claims."), null, LocalesPaths.COMMANDS_BUYCLAIMS) || save;
		save = check(locale, toText("&6Payment in game currency to increase the limit of subdivisions."), null, LocalesPaths.COMMANDS_BUYSUBDIVISIONS) || save;
		save = check(locale, toText("&6Selling the limit of blocks for game currency."), null, LocalesPaths.COMMANDS_SELLBLOCKS) || save;
		save = check(locale, toText("&6Selling the limit of claims for game currency."), null, LocalesPaths.COMMANDS_SELLCLAIMS) || save;
		save = check(locale, toText("&6Selling the limit of subdivisions for game currency."), null, LocalesPaths.COMMANDS_SELLSUBDIVISIONS) || save;
		save = check(locale, toText("&6Change the blocks limit of the player."), null, LocalesPaths.COMMANDS_SETLIMITBLOCKS) || save;
		save = check(locale, toText("&6Change the claims limit of the player."), null, LocalesPaths.COMMANDS_SETLIMITCLAIMS) || save;
		save = check(locale, toText("&6Change the subdivisions limit of the player."), null, LocalesPaths.COMMANDS_SETLIMITSUBDIVISIONS) || save;

		save = check(locale, toText("&cRegion world not found: &b%world%"), null, LocalesPaths.COMMAND_CLAIM_WORLD_NOT_FOUND) || save;
		save = check(locale, toText("&cNo region available to create a claim."), null, LocalesPaths.COMMAND_CLAIM_REGION_NOT_FOUND) || save;
		save = check(locale, toText("&cThe region touches another already existing region with boundaries from &b%min% &c to &b%max%&c. Highlight the other region."), null, LocalesPaths.COMMAND_CLAIM_CANCEL) || save;
		save = check(locale, toText("&aYou have successfully claimed a region."), null, LocalesPaths.COMMAND_CLAIM_SUCCESS) || save;

		save = check(locale, toText("&eAre you sure you want to delete the region in your location? Click this message to confirm."), null, LocalesPaths.COMMAND_DELETE_CONFIRMATION_REQUEST) || save;
		save = check(locale, toText("&aThe child region has been removed."), null, LocalesPaths.COMMAND_DELETE_CHILD_DELETED) || save;
		save = check(locale, toText("&aRegion has been removed."), null, LocalesPaths.COMMAND_DELETE_DELETED) || save;
		save = check(locale, toText("&aRegion removed. The child regions it contained were also deleted."), null, LocalesPaths.COMMAND_DELETE_DELETED_MAIN_AND_CHILDS) || save;
		save = check(locale, toText("&eATTENTION!!! The area in the region will be restored to its original state!"), null, LocalesPaths.COMMAND_DELETE_REGEN) || save;

		save = check(locale, toText(" &bRegion Info "), null, LocalesPaths.COMMAND_INFO_HEADER) || save;
		save = check(locale, toText("&7[&4Delete&7]"), null, LocalesPaths.COMMAND_INFO_DELETE) || save;
		save = check(locale, toText("&7[&eView flags&7]"), null, LocalesPaths.COMMAND_INFO_SEE_FLAGS) || save;
		save = check(locale, toText("&eRegion UUID&f: &2%uuid%"), null, LocalesPaths.COMMAND_INFO_REGION_UUID) || save;
		save = check(locale, toText("&eRegion name&f: &2%name%"), null, LocalesPaths.COMMAND_INFO_REGION_NAME) || save;
		save = check(locale, toText("&eRegion type&f: &2%type%"), null, LocalesPaths.COMMAND_INFO_REGION_TYPE) || save;
		save = check(locale, toText("&eCreated&f: &2%date%"), null, LocalesPaths.COMMAND_INFO_CREATED) || save;
		save = check(locale, toText("&eOwner&f: &2%owner%"), null, LocalesPaths.COMMAND_INFO_OWNER) || save;
		save = check(locale, toText("&eOwner UUID&f: &2%uuid%"), null, LocalesPaths.COMMAND_INFO_OWNER_UUID) || save;
		save = check(locale, toText("&eMembers&f: &2%size%"), null, LocalesPaths.COMMAND_INFO_MEMBERS) || save;
		save = check(locale, toText("&eMinimal position&f: &2%pos%"), null, LocalesPaths.COMMAND_INFO_MIN_POS) || save;
		save = check(locale, toText("&eMaximal position&f: &2%pos%"), null, LocalesPaths.COMMAND_INFO_MAX_POS) || save;
		save = check(locale, toText("&eSelection type&f: &2%type%"), null, LocalesPaths.COMMAND_INFO_SELECTION_TYPE) || save;

		save = check(locale, toText(" &bYour limits "), null, LocalesPaths.COMMAND_LIMITS_HEADER) || save;
		save = check(locale, toText("&eClaimss&f: &2%size%&f/&2%max%"), null, LocalesPaths.COMMAND_LIMITS_CLAIMS) || save;
		save = check(locale, toText("&eSubdivisions&f: &2%size%&f/&2%max%"), null, LocalesPaths.COMMAND_LIMITS_SUBDIVISIONS) || save;
		save = check(locale, toText("&eBlocks&f: &2%size%&f/&2%max%"), null, LocalesPaths.COMMAND_LIMITS_BLOCKS) || save;

		save = check(locale, toText("&cYou are not a member of the current region and cannot change join/exit messages."), null, LocalesPaths.COMMAND_SET_MESSAGE_NOT_TRUSTED) || save;
		save = check(locale, toText("&cYou have a low trust level in the current region and cannot change join/exit messages."), null, LocalesPaths.COMMAND_SET_MESSAGE_LOW_TRUST) || save;
		save = check(locale, toText("&cYou didn't specify the type of message."), null, LocalesPaths.COMMAND_SET_MESSAGE_TYPE_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou didn't specify a message."), null, LocalesPaths.COMMAND_SET_MESSAGE_NOT_PRESENT) || save;
		save = check(locale, toText("&cThe message is too long. The maximum length is 50 characters."), null, LocalesPaths.COMMAND_SET_MESSAGE_TOO_LONG) || save;
		save = check(locale, toText("&aYou have set up a welcome message."), null, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_JOIN) || save;
		save = check(locale, toText("&aYou have set up a farewell message."), null, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_EXIT) || save;
		save = check(locale, toText("&aYou have cleared the welcome message."), null, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_CLEAR_JOIN) || save;
		save = check(locale, toText("&aYou have cleared the farewell message."), null, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_CLEAR_EXIT) || save;

		save = check(locale, toText("&cYou are not a member of the current region and cannot change its name."), null, LocalesPaths.COMMAND_SET_NAME_NOT_TRUSTED) || save;
		save = check(locale, toText("&cYou have a low level of trust in the current region and cannot change its name."), null, LocalesPaths.COMMAND_SET_NAME_LOW_TRUST) || save;
		save = check(locale, toText("&cYou didn't specify a new name."), null, LocalesPaths.COMMAND_SET_NAME_NOT_PRESENT) || save;
		save = check(locale, toText("&cThe new name is too long. The maximum length is 20 characters."), null, LocalesPaths.COMMAND_SET_NAME_TOO_LONG) || save;
		save = check(locale, toText("&aYou have set a new name for the region."), null, LocalesPaths.COMMAND_SET_NAME_SUCCESS) || save;
		save = check(locale, toText("&aThe name of the region has been cleared."), null, LocalesPaths.COMMAND_SET_NAME_CLEARED) || save;

		save = check(locale, toText("&cYou cannot change the flags in this region."), null, LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_REGION) || save;
		save = check(locale, toText("&cYou cannot change or delete the flag: &b%flag%&c."), null, LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_FLAG) || save;
		save = check(locale, toText("&cYou must specify the value of the flag."), null, LocalesPaths.COMMAND_FLAG_VALUE_NOT_PRESENT) || save;
		save = check(locale, toText("&cAn invalid event source is specified for the flag."), null, LocalesPaths.COMMAND_FLAG_WRONG_SOURCE) || save;
		save = check(locale, toText("&cFor the flag there is an invalid event target specified."), null, LocalesPaths.COMMAND_FLAG_WRONG_TARGET) || save;
		save = check(locale, toText("&aThe meaning of the flag is set."), null, LocalesPaths.COMMAND_FLAG_SUCCESS) || save;
		save = check(locale, toText("&5Event source &b%source%&5.\n&5Event target &b%target%&5."), null, LocalesPaths.COMMAND_FLAG_HOVER_VALUES) || save;
		save = check(locale, toText("&3List of flags"), null, LocalesPaths.COMMAND_FLAG_LIST) || save;
		save = check(locale, toText("&2Delete flag data."), null, LocalesPaths.COMMAND_FLAG_HOVER_REMOVE) || save;
		save = check(locale, toText("&2Set the allowing value."), null, LocalesPaths.COMMAND_FLAG_HOVER_TRUE) || save;
		save = check(locale, toText("&2Set a disallowing value."), null, LocalesPaths.COMMAND_FLAG_HOVER_FALSE) || save;
		save = check(locale, toText("&2Click to specify arguments."), null, LocalesPaths.COMMAND_FLAG_HOVER_SUGGEST_ARGS) || save;

		save = check(locale, toText("&eAre you sure you want to leave from this region? Click on this message to confirm."), null, LocalesPaths.COMMAND_LEAVE_CONFIRMATION_REQUEST) || save;
		save = check(locale, toText("&cYou own a region and cannot leave it. Change the owner or either delete the region."), null, LocalesPaths.COMMAND_LEAVE_PLAYER_IS_OWNER) || save;
		save = check(locale, toText("&cYou are not a member of this region."), null, LocalesPaths.COMMAND_LEAVE_PLAYER_NOT_TRUSTED) || save;
		save = check(locale, toText("&aYou have leaved the region in your current location."), null, LocalesPaths.COMMAND_LEAVE_SUCCESS) || save;

		save = check(locale, toText("&cYou cannot assign an administrative region to a player."), null, LocalesPaths.COMMAND_SETOWNER_EXCEPTION_ADMIN) || save;
		save = check(locale, toText("&cYou already own this region."), null, LocalesPaths.COMMAND_SETOWNER_EXCEPTION_OWNER_TARGET_SELF) || save;
		save = check(locale, toText("&b%player% &cis already the owner of the region."), null, LocalesPaths.COMMAND_SETOWNER_EXCEPTION_STAFF_TARGET_OWNER) || save;
		save = check(locale, toText("&cYou do not owner this region."), null, LocalesPaths.COMMAND_SETOWNER_EXCEPTION_PLAYER_IS_NOT_OWNER) || save;
		save = check(locale, toText("&eDo you really want to change the region owner? Click on this message to confirm."), null, LocalesPaths.COMMAND_SETOWNER_CONFIRMATION_REQUEST) || save;
		save = check(locale, toText("&eThe administration representative demotes you to manager of a region in the &b%world% &as borders from &b%min% &a to &b%max%&a."), null, LocalesPaths.COMMAND_SETOWNER_SUCCESS_FROM_STAFF) || save;
		save = check(locale, toText("&aYou have appointed &b%player% as the new owner of a region in &b%world%& within the boundaries of &b%min% to &b%max%&."), null, LocalesPaths.COMMAND_SETOWNER_SUCCESS_PLAYER) || save;
		save = check(locale, toText("&b%player% &appoints you as the new owner of a region in the &b%world% &as the boundaries from &b%min% to &b%max%&a."), null, LocalesPaths.COMMAND_SETOWNER_SUCCESS_TARGET) || save;

		save = check(locale, toText("&cYou cannot add players to an administrative region."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_ADMINCLAIM) || save;
		save = check(locale, toText("&cOnly managers and region owners can add players to it."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_NEED_TRUST_TYPE) || save;
		save = check(locale, toText("&cYou did not specify a trust type. Valid values: &b%trust-types%&c."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_TRUST_TYPE_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou can't assign the type of trust to yourself."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_TARGET_SELF) || save;
		save = check(locale, toText("&cOnly the owner of the region can appoint managers."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_PLAYER_IS_NOT_OWNER) || save;
		save = check(locale, toText("&cThe limit of members in this region has been reached."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_LIMIT_REACHED) || save;
		save = check(locale, toText("&aYou have assigned the trust type &b%trust-type% to &b%player%&a."), null, LocalesPaths.COMMAND_TRUST_SUCCESS_PLAYER) || save;
		save = check(locale, toText("&b%player% &aassigns you the trust type &b%trust-type% &ain the region in the world &b%world% &aas the boundaries from &b%min% &ato &b%max%&a."), null, LocalesPaths.COMMAND_TRUST_SUCCESS_TARGET) || save;

		save = check(locale, toText("&cYou cannot exclude the owner of a region. You must first specify another player as the owner."), null, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_PLAYER_IS_OWNER) || save;
		save = check(locale, toText("&cYou are not the owner or manager of this region and cannot exclude other players from it."), null, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_NEED_TRUST_TYPE) || save;
		save = check(locale, toText("&cOne cannot exclude oneself from the region."), null, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_TARGET_SELF) || save;
		save = check(locale, toText("&cYou do not owner the region and cannot exclude another manager from it."), null, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_TARGET_MANAGER) || save;
		save = check(locale, toText("&aYou have excluded a player from the region &b%player%&c."), null, LocalesPaths.COMMAND_UNTRUST_SUCCESS_PLAYER) || save;
		save = check(locale, toText("&b%player% &eexcludes you from the region in the world &b%world% &eas the boundaries from &e%min% &ato &e%max%&a."), null, LocalesPaths.COMMAND_UNTRUST_SUCCESS_TARGET) || save;

		save = check(locale, toText("&cYou already have a tool for creating regions and getting information about them."), null, LocalesPaths.COMMAND_WAND_EXCEPTION_ITEM_EXIST) || save;
		save = check(locale, toText("&cYou have no room in your inventory. Free up at least 1 slot."), null, LocalesPaths.COMMAND_WAND_EXCEPTION_INVENTORY_IS_FULL) || save;
		save = check(locale, toText("&dLeft click to select two points in the world to create a region, then enter the command '/rg claim' in order to claim the region.\n&dThe child regions are added automatically.\n&dA right click on the block will display brief information about the region."), null, LocalesPaths.COMMAND_WAND_SUCCESS) || save;

		save = check(locale, toText("&aYour game client will now receive data packets to display region boundaries, which are handled by the WECui mod. If you do not have this mod, it is recommended that you enter this command again to disable sending packets."), null, LocalesPaths.COMMAND_WECUI_ENABLE) || save;
		save = check(locale, toText("&aNow your client will not receive data packets for the WECui mod."), null, LocalesPaths.COMMAND_WECUI_DISABLE) || save;

		save = check(locale, toText("&cYou did not specify the type of selectior."), null, LocalesPaths.COMMAND_SELECTOR_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&dYou have selected the cuboid selector type."), null, LocalesPaths.COMMAND_SELECTOR_CUBOID) || save;
		save = check(locale, toText("&dYou have selected the flat selector type"), null, LocalesPaths.COMMAND_SELECTOR_FLAT) || save;

		save = check(locale, toText("&cYou didn't specify a region type."), null, LocalesPaths.COMMAND_REGION_TYPE_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&dYou will now create basic regions."), null, LocalesPaths.COMMAND_REGION_TYPE_CLAIM) || save;
		save = check(locale, toText("&dYou will now create arenas. They differ from the basic regions only by the WECui highlighting grid. Recommended for minigames and theme modes."), null, LocalesPaths.COMMAND_REGION_TYPE_ARENA) || save;
		save = check(locale, toText("&dYou will now create administrative regions. With this type of regions, the owner is always the server."), null, LocalesPaths.COMMAND_REGION_TYPE_ADMIN) || save;

		save = check(locale, toText("&cYou don't have any regions."), null, LocalesPaths.COMMAND_LIST_EXCEPTION_EMPTY_SELF) || save;
		save = check(locale, toText("&cThe player has no regions."), null, LocalesPaths.COMMAND_LIST_EXCEPTION_EMPTY_OTHER) || save;
		save = check(locale, toText("&cThe position is not safe. Are you sure you want to teleport? Click on this message to confirm."), null, LocalesPaths.COMMAND_LIST_EXCEPTION_NOTSAFE) || save;
		save = check(locale, toText("&3Regions: %player%"), null, LocalesPaths.COMMAND_LIST_TITLE) || save;

		save = check(locale, toText("&cYou need to specify the volume of the purchase."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a number."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cYou need to specify a number greater than 0."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cThe maximum purchase volume you can specify: &b%max%&c."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cYou don't have enough money to buy."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_NOT_ENOUGH_MONEY) || save;
		save = check(locale, toText("&cSomething went wrong while executing the transaction. The details may be in the server console."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aYou have increased your blocks limit by &b%size%&a. Your current blocks limit: &b%volume%&a."), null, LocalesPaths.COMMAND_BUYBLOCKS_SUCCESS) || save;

		save = check(locale, toText("&cYou need to specify the volume of the purchase."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a number."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cYou need to specify a number greater than 0."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cThe maximum purchase volume you can specify: &b%max%&c."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cYou don't have enough money to buy."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_NOT_ENOUGH_MONEY) || save;
		save = check(locale, toText("&cSomething went wrong while executing the transaction. The details may be in the server console."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aYou have increased your regions limit by &b%size%&a. Your current regions limit: &b%volume%&a."), null, LocalesPaths.COMMAND_BUYCLAIMS_SUCCESS) || save;

		save = check(locale, toText("&cYou need to specify the volume of the purchase."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a number."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cYou need to specify a number greater than 0."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cThe maximum purchase volume you can specify: &b%max%&c."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cYou don't have enough money to buy."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_NOT_ENOUGH_MONEY) || save;
		save = check(locale, toText("&cSomething went wrong while executing the transaction. The details may be in the server console."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aYou have increased your subdivisions limit by &b%size%&a. Your current subdivisions limit: &b%volume%&a."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_SUCCESS) || save;

		save = check(locale, toText("&cYou need to specify the sales volume."), null, LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a number."), null, LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cYou need to specify a number greater than 0."), null, LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cThe maximum sales volume you can specify: &b%max%&c."), null, LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cSomething went wrong while executing the transaction. The details may be in the server console."), null, LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aYou have reduced your block limit by &b%size%&a. Your current blocks limit: &b%volume%&a."), null, LocalesPaths.COMMAND_SELLBLOCKS_SUCCESS) || save;

		save = check(locale, toText("&cYou need to specify the sales volume."), null, LocalesPaths.COMMAND_SELLCLAIMS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a number."), null, LocalesPaths.COMMAND_SELLCLAIMS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cYou need to specify a number greater than 0."), null, LocalesPaths.COMMAND_SELLCLAIMS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cThe maximum sales volume you can specify: &b%max%&c."), null, LocalesPaths.COMMAND_SELLCLAIMS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cПри выполнении транзакции что-то пошло не так. Подробности могут быть в консоли сервера."), null, LocalesPaths.COMMAND_SELLCLAIMS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aYou have reduced your regions limit by &b%size%&a. Your current regions limit: &b%volume%&a."), null, LocalesPaths.COMMAND_SELLCLAIMS_SUCCESS) || save;

		save = check(locale, toText("&cYou need to specify the sales volume."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a number."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cYou need to specify a number greater than 0."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cThe maximum sales volume you can specify: &b%max%&c."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cSomething went wrong while executing the transaction. The details may be in the server console."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aYou have reduced your subdivisions limit by &b%size%&a. Your current subdivisions limit: &b%volume%&a."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_SUCCESS) || save;

		save = check(locale, toText("&cYou need to specify a player online."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_EXCEPTION_PLAYER_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a new limit."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_EXCEPTION_VOLUME_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a number."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cYou entered a number less than zero."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_EXCEPTION_LESS_THEN_ZERO) || save;
		save = check(locale, toText("&aYou have changed the blocks limit of the player &b%player%&a. His limit is now &b%size%&a blocks."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_SUCCESS_SOURCE) || save;
		save = check(locale, toText("&aAdministration representative &b%player%&a has changed your blocks limit. It is now &b%size%&a blocks."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_SUCCESS_TARGET) || save;

		save = check(locale, toText("&cYou need to specify a player online."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_EXCEPTION_PLAYER_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a new limit."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_EXCEPTION_VOLUME_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a number."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cYou entered a number less than zero."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_EXCEPTION_LESS_THEN_ZERO) || save;
		save = check(locale, toText("&aYou have changed the claims limit of the player &b%player%&a. His limit is now &b%size%&a claims."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_SUCCESS_SOURCE) || save;
		save = check(locale, toText("&aAdministration representative &b%player%&a has changed your claims limit. It is now &b%size%&a claims."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_SUCCESS_TARGET) || save;

		save = check(locale, toText("&cYou need to specify a player online."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_EXCEPTION_PLAYER_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a new limit."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_EXCEPTION_VOLUME_NOT_PRESENT) || save;
		save = check(locale, toText("&cYou need to specify a number."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cYou entered a number less than zero."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_EXCEPTION_LESS_THEN_ZERO) || save;
		save = check(locale, toText("&aYou have changed the subdivisions limit of the player &b%player%&a. His limit is now &b%size%&a subdivisions."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_SUCCESS_SOURCE) || save;
		save = check(locale, toText("&aAdministration representative &b%player%&a has changed your subdivisions limit. It is now &b%size%&a subdivisions."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_SUCCESS_TARGET) || save;



		save = check(locale, toText("&cYou do not have the permission to create child regions in an administrative region."), null, LocalesPaths.REGION_CREATE_EXCEPTION_ADMIN_CLAIM) || save;
		save = check(locale, toText("&cThis position already belongs to another player."), null, LocalesPaths.REGION_CREATE_EXCEPTION_POSITION_LOCKED) || save;
		save = check(locale, toText("&cYou have reached the limit of regions available to you. Your limit: &b%size%&c."), null, LocalesPaths.REGION_CREATE_EXCEPTION_LARGE_VOLUME_REGIONS) || save;
		save = check(locale, toText("&cYou have reached the limit of subdivisions in current region. Your limit: &b%size%&c."), null, LocalesPaths.REGION_CREATE_EXCEPTION_LARGE_VOLUME_SUBDIVISIONS) || save;
		save = check(locale, toText("&cYou have chosen too large a volume: &b%selected%&c. Select the smaller area to create the region. You can select &b%max% &blocks."), null, LocalesPaths.REGION_CREATE_EXCEPTION_LARGE_VOLUME_BLOCKS) || save;
		save = check(locale, toText("&cThe points have a coincidence of one of the coordinates. For cuboids, an XYZ coincidence is unacceptable. For flat regions, an XZ coincidence is not allowed."), null, LocalesPaths.REGION_CREATE_EXCEPTION_INCORRECT_COORDS) || save;
		save = check(locale, toText("&cThe region being created partially or completely overlaps an existing region. Select other positions."), null, LocalesPaths.REGION_CREATE_EXCEPTION_REGIONS_INTERSECT) || save;
		save = check(locale, toText("&cThe creation of the region was cancelled."), null, LocalesPaths.REGION_CREATE_EXCEPTION_CENCELLED_EVENT) || save;
		save = check(locale, toText("&cA child region cannot be created because it overlaps the boundaries of the parent region. Base regions cannot overlap either."), null, LocalesPaths.REGION_CREATE_EXCEPTION_WRONG_SUBDIVISION_POSITIONS) || save;
		save = check(locale, toText("&dThe position &b%pos% &d is set to the coordinates of &b%target%&d."), null, LocalesPaths.REGION_CREATE_SETPOS) || save;
		save = check(locale, toText("&aRegion created. Volume: &b%volume%&a. Enter the /rg claim command to claim the territory."), null, LocalesPaths.REGION_CREATE_CREATE_BASIC) || save;
		save = check(locale, toText("&aSubdivision region created. Volume: &b%volume%&a."), null, LocalesPaths.REGION_CREATE_SUBDIVISION) || save;

		save = check(locale, toText("&dRegion type: &b%type%"), null, LocalesPaths.REGION_WAND_TYPE) || save;
		save = check(locale, toText("&dOwner: &b%owner%"), null, LocalesPaths.REGION_WAND_OWNER) || save;

		save = check(locale, toText("&cOne of the XYZ(3D)/XZ(2D) coordinates of the new point coincides with the same coordinate of the opposite corner of the region. Select a different position."), null, LocalesPaths.REGION_RESIZE_EXCEPTION_INCORRECT_COORDS) || save;
		save = check(locale, toText("&cThe volume selected is too small. To complete the operation, select an area larger than the current one by &b%volume%&c."), null, LocalesPaths.REGION_RESIZE_EXCEPTION_SMALL_VOLUME) || save;
		save = check(locale, toText("&cThe new size is too large. You have selected: &b%selected%&c blocks.\nBlocks available: &b%volume%&c."), null, LocalesPaths.REGION_RESIZE_EXCEPTION_LARGE_VOLUME) || save;
		save = check(locale, toText("&cIt is not possible to change the size of a region, as this would cause it to intersect with another region."), null, LocalesPaths.REGION_RESIZE_EXCEPTION_REGIONS_INTERSECT) || save;
		save = check(locale, toText("&cIt is not possible to change the size of a region as this would cause the child region to be outside his boundaries."), null, LocalesPaths.REGION_RESIZE_EXCEPTION_CHILD_OUT) || save;
		save = check(locale, toText("&dClick again elsewhere to resize."), null, LocalesPaths.REGION_RESIZE_START) || save;
		save = check(locale, toText("&dThe size of the region has been changed."), null, LocalesPaths.REGION_RESIZE_FINISH) || save;

		save = check(locale, toText("&cYou cannot interact with blocks with your left hand in the current region."), null, LocalesPaths.INTERACT_BLOCK_CANCEL_SECONDARY) || save;
		save = check(locale, toText("&cYou cannot interact with blocks with your right hand in the current region."), null, LocalesPaths.INTERACT_BLOCK_CANCEL_PRIMARY) || save;
		save = check(locale, toText("&cYou do not have a permission to grow in this region."), null, LocalesPaths.CANCEL_GROWTH) || save;
		save = check(locale, toText("&cYou do not have permission to place or modify blocks in this region."), null, LocalesPaths.CANCEL_PLACE) || save;
		save = check(locale, toText("&cYou do not have permission to break or remove blocks in this region."), null, LocalesPaths.CANCEL_BREAK) || save;
		save = check(locale, toText("&cYou cannot fly in this region. Flight is off."), null, LocalesPaths.DISABLE_FLY) || save;
		save = check(locale, toText("&cYou have entered a region in which flying is forbidden. You will be able to fly again after leaving this region."), null, LocalesPaths.DISABLE_FLY_ON_JOIN) || save;
		save = check(locale, toText("&cYou cannot teleport a player to a specified position."), null, LocalesPaths.TELEPORT_OTHER_TO_REGION) || save;
		save = check(locale, toText("&cYou cannot teleport a player from his current position."), null, LocalesPaths.TELEPORT_OTHER_FROM_REGION) || save;
		save = check(locale, toText("&cYou cannot teleport from this region. Pearls returned to your inventory."), null, LocalesPaths.TELEPORT_ENDERPEARL_FROM_REGION) || save;
		save = check(locale, toText("&cYou cannot enter the region."), null, LocalesPaths.CANCEL_JOIN) || save;
		save = check(locale, toText("&cYou cannot exit from region."), null, LocalesPaths.CANCEL_EXIT) || save;
		save = check(locale, toText("&cYou cannot teleport to this region. Pearls returned to your inventory."), null, LocalesPaths.TELEPORT_ENDERPEARL_TO_REGION) || save;
		save = check(locale, toText("&cYou cannot teleport from the current region."), null, LocalesPaths.TELEPORT_FROM_REGION) || save;
		save = check(locale, toText("&cThe teleportation position is in a region that cannot be teleported to."), null, LocalesPaths.TELEPORT_TO_REGION) || save;
		save = check(locale, toText("&cYou cannot interact with this entity with your right hand in the current region."), null, LocalesPaths.INTERACT_ENTITY_CANCEL_PRIMARY) || save;
		save = check(locale, toText("&cYou cannot interact with this entity with your left hand in the current region."), null, LocalesPaths.INTERACT_ENTITY_CANCEL_SECONDARY) || save;
		save = check(locale, toText("&cPvP is not allowed in this region."), null, LocalesPaths.PVP) || save;
		save = check(locale, toText("&cYou cannot deal damage to this entity in the current region."), null, LocalesPaths.ENTITY_DAMAGE) || save;
		save = check(locale, toText("&aThe region has saved your experience and it will be restored after the respawn."), null, LocalesPaths.KEEP_EXP) || save;
		save = check(locale, toText("&aThe region has saved your inventory and it will be restored after the respawn."), null, LocalesPaths.KEEP_INVENTORY) || save;

		save = check(locale, toText("&cThe block is in a region in which you have no rights to shoot blocks."), null, LocalesPaths.IMPACT_BLOCK) || save;
		save = check(locale, toText("&cThe target is in a region in which you have no rights to shoot entities."), null, LocalesPaths.IMPACT_ENTITY) || save;

		save = check(locale, toText("&cYou cannot use this command in the current region."), null, LocalesPaths.COMMAND_EXECUTE) || save;
		save = check(locale, toText("&cYou cannot use this command in the current region while in combat with another player."), null, LocalesPaths.COMMAND_EXECUTE_PVP) || save;

		save = check(locale, toText("&cYou cannot drop items in the current region."), null, LocalesPaths.ITEM_DROP) || save;
		save = check(locale, toText("&cYou cannot pick up items in the current region."), null, LocalesPaths.ITEM_PICKUP) || save;

		save = check(locale, toText("&cYou cannot ride in this region."), null, LocalesPaths.RIDING) || save;

		save = check(locale, toText("&cThe entity, item or experience spawn event has been canceled because the region has a disallowing flag."), null, LocalesPaths.SPAWN) || save;

		save = check(locale, toText("&cYou cannot use this item in the current region."), null, LocalesPaths.INTERACT_ITEM) || save;
		save = check(locale, toText("&cYou cannot use this item in the current region."), null, LocalesPaths.ITEM_USE) || save;

		save = check(locale, toText("&cYou cannot use the portal in your current region."), null, LocalesPaths.PORTAL_USE) || save;
		
		if(save) save(locale);
	}

	private void generateRu() {
		Locale locale = org.spongepowered.api.util.locale.Locales.RU_RU;
		boolean save = check(locale, toText("&eНа сервере отсутствует плагин экономики. Часть функционала будет недоступна."), null, LocalesPaths.ECONOMY_NOT_FOUND);
		save = check(locale, toText("&cУ вас нет разрешения на это действие."), null, LocalesPaths.NOT_PERMITTED);
		save = check(locale, toText("&3="), null, LocalesPaths.PADDING) || save;
		save = check(locale, toText("&cКоманда может быть выполнена только игроком."), null, LocalesPaths.COMMANDS_ONLY_PLAYER) || save;
		save = check(locale, toText("&cВ вашей позиции нет региона."), null, LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND) || save;
		save = check(locale, toText("&cВы не владелец этого региона."), null, LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_OWNER) || save;
		save = check(locale, toText("&cНужно указать ник игрока."), null, LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_PRESENT) || save;

		save = check(locale, toText("&bСписок команд."), null, LocalesPaths.COMMANDS_TITLE) || save;
		save = check(locale, toText("&cКоманды доступны только игрокам."), null, LocalesPaths.COMMANDS_USED_BY_NON_PLAYER) || save;
		save = check(locale, toText("&6Получить предмет для создания регионов."), null, LocalesPaths.COMMANDS_WAND) || save;
		save = check(locale, toText("&6Заприватить выделенный регион."), null, LocalesPaths.COMMANDS_CLAIM) || save;
		save = check(locale, toText("&6Удалить регион."), null, LocalesPaths.COMMANDS_DELETE) || save;
		save = check(locale, toText("&6Информация о регионе."), null, LocalesPaths.COMMANDS_INFO) || save;
		save = check(locale, toText("&6Информация о ваших лимитах."), null, LocalesPaths.COMMANDS_LIMITS) || save;
		save = check(locale, toText("&6Установить имя региону"), null, LocalesPaths.COMMANDS_SET_NAME) || save;
		save = check(locale, toText("&6Установить/удалить сообщение входа/выхода в регионе."), null, LocalesPaths.COMMANDS_SET_MESSAGE) || save;
		save = check(locale, toText("&6Установить параметры флага."), null, LocalesPaths.COMMANDS_FLAG) || save;
		save = check(locale, toText("&6Покинуть регион."), null, LocalesPaths.COMMANDS_LEAVE) || save;
		save = check(locale, toText("&6Назначить владельца региона."), null, LocalesPaths.COMMANDS_SETOWNER) || save;
		save = check(locale, toText("&6Добавить игрока в регион и указать ему права в регионе."), null, LocalesPaths.COMMANDS_TRUST) || save;
		save = check(locale, toText("&6Удалить игрока из региона."), null, LocalesPaths.COMMANDS_UNTRUST) || save;
		save = check(locale, toText("&6Выбрать тип выделения области."), null, LocalesPaths.COMMANDS_SET_SELECTOR_TYPE) || save;
		save = check(locale, toText("&6Выбрать тип создаваемого региона."), null, LocalesPaths.COMMANDS_SET_CREATING_TYPE) || save;
		save = check(locale, toText("&6Переключить статус отправки пакетов WECui."), null, LocalesPaths.COMMANDS_WECUI) || save;
		save = check(locale, toText("&6Показать список регионов."), null, LocalesPaths.COMMANDS_LIST) || save;
		save = check(locale, toText("&6Повышение лимита блоков за игровую валюту."), null, LocalesPaths.COMMANDS_BUYBLOCKS) || save;
		save = check(locale, toText("&6Повышение лимита приватов за игровую валюту."), null, LocalesPaths.COMMANDS_BUYCLAIMS) || save;
		save = check(locale, toText("&6Повышение лимита дочерних регионов за игровую валюту."), null, LocalesPaths.COMMANDS_BUYSUBDIVISIONS) || save;
		save = check(locale, toText("&6Продажа лимита блоков за игровую валюту."), null, LocalesPaths.COMMANDS_SELLBLOCKS) || save;
		save = check(locale, toText("&6Продажа лимита приватов за игровую валюту."), null, LocalesPaths.COMMANDS_SELLCLAIMS) || save;
		save = check(locale, toText("&6Продажа лимита дочерних регионов за игровую валюту."), null, LocalesPaths.COMMANDS_SELLSUBDIVISIONS) || save;
		save = check(locale, toText("&6Изменение лимита блоков у игрока."), null, LocalesPaths.COMMANDS_SETLIMITBLOCKS) || save;
		save = check(locale, toText("&6Изменение лимита приватов у игрока."), null, LocalesPaths.COMMANDS_SETLIMITCLAIMS) || save;
		save = check(locale, toText("&6Изменение лимита дочерних регионов у игрока."), null, LocalesPaths.COMMANDS_SETLIMITSUBDIVISIONS) || save;

		save = check(locale, toText("&cНе найден мир региона: &b%world%"), null, LocalesPaths.COMMAND_CLAIM_WORLD_NOT_FOUND) || save;
		save = check(locale, toText("&cНет доступного региона для создания привата."), null, LocalesPaths.COMMAND_CLAIM_REGION_NOT_FOUND) || save;
		save = check(locale, toText("&cРегион задевает другой уже существующий регион с границами от &b%min% &cдо &b%max%&c. Выделите другую область."), null, LocalesPaths.COMMAND_CLAIM_CANCEL) || save;
		save = check(locale, toText("&aВы успешно заприватили регион."), null, LocalesPaths.COMMAND_CLAIM_SUCCESS) || save;

		save = check(locale, toText("&eВы точно хотите удалить регион в вашем местоположении? Кликните на это сообщение для подтверждения."), null, LocalesPaths.COMMAND_DELETE_CONFIRMATION_REQUEST) || save;
		save = check(locale, toText("&aДочерний регион удален."), null, LocalesPaths.COMMAND_DELETE_CHILD_DELETED) || save;
		save = check(locale, toText("&aРегион удален."), null, LocalesPaths.COMMAND_DELETE_DELETED) || save;
		save = check(locale, toText("&aРегион удален. Содержавшиеся в нем дочерние регионы так же были удалены."), null, LocalesPaths.COMMAND_DELETE_DELETED_MAIN_AND_CHILDS) || save;
		save = check(locale, toText("&eВНИМАНИЕ!!! Область занимаемая регионом будет восстановлена до изначального состояния!"), null, LocalesPaths.COMMAND_DELETE_REGEN) || save;

		save = check(locale, toText(" &bИнформация о регионе "), null, LocalesPaths.COMMAND_INFO_HEADER) || save;
		save = check(locale, toText("&7[&4Удалить&7]"), null, LocalesPaths.COMMAND_INFO_DELETE) || save;
		save = check(locale, toText("&7[&eПросмотреть флаги&7]"), null, LocalesPaths.COMMAND_INFO_SEE_FLAGS) || save;
		save = check(locale, toText("&eUUID региона&f: &2%uuid%"), null, LocalesPaths.COMMAND_INFO_REGION_UUID) || save;
		save = check(locale, toText("&eИмя региона&f: &2%name%"), null, LocalesPaths.COMMAND_INFO_REGION_NAME) || save;
		save = check(locale, toText("&eТип региона&f: &2%type%"), null, LocalesPaths.COMMAND_INFO_REGION_TYPE) || save;
		save = check(locale, toText("&eСоздан&f: &2%date%"), null, LocalesPaths.COMMAND_INFO_CREATED) || save;
		save = check(locale, toText("&eВладелец&f: &2%owner%"), null, LocalesPaths.COMMAND_INFO_OWNER) || save;
		save = check(locale, toText("&eUUID владельца&f: &2%uuid%"), null, LocalesPaths.COMMAND_INFO_OWNER_UUID) || save;
		save = check(locale, toText("&eУчастников&f: &2%size%"), null, LocalesPaths.COMMAND_INFO_MEMBERS) || save;
		save = check(locale, toText("&eМинимальная позиция&f: &2%pos%"), null, LocalesPaths.COMMAND_INFO_MIN_POS) || save;
		save = check(locale, toText("&eМаксимальная позиция&f: &2%pos%"), null, LocalesPaths.COMMAND_INFO_MAX_POS) || save;
		save = check(locale, toText("&eТип выделения&f: &2%type%"), null, LocalesPaths.COMMAND_INFO_SELECTION_TYPE) || save;

		save = check(locale, toText(" &bВаши лимиты "), null, LocalesPaths.COMMAND_LIMITS_HEADER) || save;
		save = check(locale, toText("&eПриваты&f: &2%size%&f/&2%max%"), null, LocalesPaths.COMMAND_LIMITS_CLAIMS) || save;
		save = check(locale, toText("&eДочерние регионы&f: &2%size%&f/&2%max%"), null, LocalesPaths.COMMAND_LIMITS_SUBDIVISIONS) || save;
		save = check(locale, toText("&eБлоки&f: &2%size%&f/&2%max%"), null, LocalesPaths.COMMAND_LIMITS_BLOCKS) || save;

		save = check(locale, toText("&cВы не состоите в текущем регионе и не можете изменять сообщения входа/выхода."), null, LocalesPaths.COMMAND_SET_MESSAGE_NOT_TRUSTED) || save;
		save = check(locale, toText("&cУ вас низкий уровень доверия в текущем регионе и вы не можете изменять сообщения входа/выхода."), null, LocalesPaths.COMMAND_SET_MESSAGE_LOW_TRUST) || save;
		save = check(locale, toText("&cВы не указали тип сообщения."), null, LocalesPaths.COMMAND_SET_MESSAGE_TYPE_NOT_PRESENT) || save;
		save = check(locale, toText("&cВы не указали сообщение."), null, LocalesPaths.COMMAND_SET_MESSAGE_NOT_PRESENT) || save;
		save = check(locale, toText("&cСообщение слишком длинное. Максимальная длина составляет 50 символов."), null, LocalesPaths.COMMAND_SET_MESSAGE_TOO_LONG) || save;
		save = check(locale, toText("&aВы установили приветственное сообщение."), null, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_JOIN) || save;
		save = check(locale, toText("&aВы установили прощальное сообщение."), null, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_EXIT) || save;
		save = check(locale, toText("&aВы очистили приветственное сообщение."), null, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_CLEAR_JOIN) || save;
		save = check(locale, toText("&aВы очистили прощальное сообщение."), null, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_CLEAR_EXIT) || save;

		save = check(locale, toText("&cВы не состоите в текущем регионе и не можете изменять его имя."), null, LocalesPaths.COMMAND_SET_NAME_NOT_TRUSTED) || save;
		save = check(locale, toText("&cУ вас низкий уровень доверия в текущем регионе и вы не можете изменять его имя."), null, LocalesPaths.COMMAND_SET_NAME_LOW_TRUST) || save;
		save = check(locale, toText("&cВы не указали новое имя."), null, LocalesPaths.COMMAND_SET_NAME_NOT_PRESENT) || save;
		save = check(locale, toText("&cНовое имя слишком длинное. Максимальная длина составляет 20 символов."), null, LocalesPaths.COMMAND_SET_NAME_TOO_LONG) || save;
		save = check(locale, toText("&aВы установили новое имя для региона."), null, LocalesPaths.COMMAND_SET_NAME_SUCCESS) || save;
		save = check(locale, toText("&aИмя региона очищенно."), null, LocalesPaths.COMMAND_SET_NAME_CLEARED) || save;

		save = check(locale, toText("&cВы не можете изменять флаги в этом регионе."), null, LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_REGION) || save;
		save = check(locale, toText("&cВы не можете изменить или удалить флаг: &b%flag%&c."), null, LocalesPaths.COMMAND_FLAG_NOT_PERMITTED_FLAG) || save;
		save = check(locale, toText("&cНужно указать значение флага."), null, LocalesPaths.COMMAND_FLAG_VALUE_NOT_PRESENT) || save;
		save = check(locale, toText("&cДля флага указан недействительный источник события."), null, LocalesPaths.COMMAND_FLAG_WRONG_SOURCE) || save;
		save = check(locale, toText("&cДля флага указана не действительная цель события."), null, LocalesPaths.COMMAND_FLAG_WRONG_TARGET) || save;
		save = check(locale, toText("&aЗначение флага установленно."), null, LocalesPaths.COMMAND_FLAG_SUCCESS) || save;
		save = check(locale, toText("&5Источник события &b%source%&5.\n&5Цель события &b%target%&5."), null, LocalesPaths.COMMAND_FLAG_HOVER_VALUES) || save;
		save = check(locale, toText("&3Список флагов"), null, LocalesPaths.COMMAND_FLAG_LIST) || save;
		save = check(locale, toText("&2Удалить данные флага."), null, LocalesPaths.COMMAND_FLAG_HOVER_REMOVE) || save;
		save = check(locale, toText("&2Установить разрешающее значение."), null, LocalesPaths.COMMAND_FLAG_HOVER_TRUE) || save;
		save = check(locale, toText("&2Установить запрещающее значение."), null, LocalesPaths.COMMAND_FLAG_HOVER_FALSE) || save;
		save = check(locale, toText("&2Клик для указания аргументов."), null, LocalesPaths.COMMAND_FLAG_HOVER_SUGGEST_ARGS) || save;

		save = check(locale, toText("&eВы уверены, что хотите покинуть этот регион? Кликните на это сообщение для подтверждения."), null, LocalesPaths.COMMAND_LEAVE_CONFIRMATION_REQUEST) || save;
		save = check(locale, toText("&cВы владелец региона и не можете его покинуть. Смените владельца или либо удалите регион."), null, LocalesPaths.COMMAND_LEAVE_PLAYER_IS_OWNER) || save;
		save = check(locale, toText("&cВы не являетесь участником этого региона."), null, LocalesPaths.COMMAND_LEAVE_PLAYER_NOT_TRUSTED) || save;
		save = check(locale, toText("&aВы покинули регион в вашем текущем местоположении."), null, LocalesPaths.COMMAND_LEAVE_SUCCESS) || save;

		save = check(locale, toText("&cНельзя назначить игроку административный регион."), null, LocalesPaths.COMMAND_SETOWNER_EXCEPTION_ADMIN) || save;
		save = check(locale, toText("&cВы уже владелец этого региона."), null, LocalesPaths.COMMAND_SETOWNER_EXCEPTION_OWNER_TARGET_SELF) || save;
		save = check(locale, toText("&b%player% &cуже является владельцем региона."), null, LocalesPaths.COMMAND_SETOWNER_EXCEPTION_STAFF_TARGET_OWNER) || save;
		save = check(locale, toText("&cВы не владелец региона."), null, LocalesPaths.COMMAND_SETOWNER_EXCEPTION_PLAYER_IS_NOT_OWNER) || save;
		save = check(locale, toText("&eВы действительно хотите сменить владельца региона? Кликните на это сообщение для подтверждения."), null, LocalesPaths.COMMAND_SETOWNER_CONFIRMATION_REQUEST) || save;
		save = check(locale, toText("&eПредставитель администрации понижает вас до менеджера региона в мире &b%world% &aс границами от &b%min% &aдо &b%max%&a."), null, LocalesPaths.COMMAND_SETOWNER_SUCCESS_FROM_STAFF) || save;
		save = check(locale, toText("&aВы назначили &b%player% &aновым владельцем региона в мире &b%world% &aс границами от &b%min% &aдо &b%max%&a."), null, LocalesPaths.COMMAND_SETOWNER_SUCCESS_PLAYER) || save;
		save = check(locale, toText("&b%player% &aназначает вас новым владельцем региона в мире &b%world% &aс границами от &b%min% &aдо &b%max%&a."), null, LocalesPaths.COMMAND_SETOWNER_SUCCESS_TARGET) || save;

		save = check(locale, toText("&cНельзя добавлять игроков в административный регион."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_ADMINCLAIM) || save;
		save = check(locale, toText("&cТолько менеджеры и владельцы регионов могут добавлять в него игроков."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_NEED_TRUST_TYPE) || save;
		save = check(locale, toText("&cВы не указали тип доверия. Допустимые значения: &b%trust-types%&c."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_TRUST_TYPE_NOT_PRESENT) || save;
		save = check(locale, toText("&cВы не можете назначить тип доверия самому себе."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_TARGET_SELF) || save;
		save = check(locale, toText("&cТолько владелец региона может назначать менеджеров."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_PLAYER_IS_NOT_OWNER) || save;
		save = check(locale, toText("&cДостигнут лимит участников в этом регионе."), null, LocalesPaths.COMMAND_TRUST_EXCEPTION_LIMIT_REACHED) || save;
		save = check(locale, toText("&aВы назначили тип доверия &b%trust-type% &aигроку &b%player%&a."), null, LocalesPaths.COMMAND_TRUST_SUCCESS_PLAYER) || save;
		save = check(locale, toText("&b%player% &aназначает вам тип доверия &b%trust-type% &aв регионе в мире &b%world% &aс границами от &b%min% &aдо &b%max%&a."), null, LocalesPaths.COMMAND_TRUST_SUCCESS_TARGET) || save;

		save = check(locale, toText("&cНельзя исключить владельца региона. Сперва нужно указать в качестве владельца другого игрока."), null, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_PLAYER_IS_OWNER) || save;
		save = check(locale, toText("&cВы не являетесь владельцем или менеджером этого региона и не можете исключать из него других игроков."), null, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_NEED_TRUST_TYPE) || save;
		save = check(locale, toText("&cНельзя исключить из региона самого себя."), null, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_TARGET_SELF) || save;
		save = check(locale, toText("&cВы не владелец региона и не можте исключить из него другого менеджера."), null, LocalesPaths.COMMAND_UNTRUST_EXCEPTION_TARGET_MANAGER) || save;
		save = check(locale, toText("&aВЫ исключили из региона игрока &b%player%&c."), null, LocalesPaths.COMMAND_UNTRUST_SUCCESS_PLAYER) || save;
		save = check(locale, toText("&b%player% &eисключает вас из региона в мире &b%world% &eс границами от &e%min% &aдо &e%max%&a."), null, LocalesPaths.COMMAND_UNTRUST_SUCCESS_TARGET) || save;

		save = check(locale, toText("&cУ вас уже есть инструмент для создания регионов и получения о них информации."), null, LocalesPaths.COMMAND_WAND_EXCEPTION_ITEM_EXIST) || save;
		save = check(locale, toText("&cУ вас нет места в инвентаре. Освободите хотя бы 1 слот."), null, LocalesPaths.COMMAND_WAND_EXCEPTION_INVENTORY_IS_FULL) || save;
		save = check(locale, toText("&dЛевым кликом выделите 2 точки в мире для создания региона, затем введите команду /rg claim для того что бы заприватить регион.\n&dДочерние регионы добавляются автоматически.\n&dПравый клик по блоку отобразит краткую информацию о регионе."), null, LocalesPaths.COMMAND_WAND_SUCCESS) || save;

		save = check(locale, toText("&aТеперь ваш клиент игры будет получать пакеты данных для отображения границ регионов, которые обрабатывает мод WECui. Если у вас нет этого мода, то рекомендуется ввести эту команду еще раз для отключения."), null, LocalesPaths.COMMAND_WECUI_ENABLE) || save;
		save = check(locale, toText("&aТеперь ваш клиент не будет получать пакеты данных для мода WECui."), null, LocalesPaths.COMMAND_WECUI_DISABLE) || save;

		save = check(locale, toText("&cВы не указали тип выделения."), null, LocalesPaths.COMMAND_SELECTOR_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&dВы выбрали кубический тип выделения."), null, LocalesPaths.COMMAND_SELECTOR_CUBOID) || save;
		save = check(locale, toText("&dВы выбрали плоский тип выделения."), null, LocalesPaths.COMMAND_SELECTOR_FLAT) || save;

		save = check(locale, toText("&cВы не указали тип региона."), null, LocalesPaths.COMMAND_REGION_TYPE_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&dТеперь вы будете создавать обычные регионы."), null, LocalesPaths.COMMAND_REGION_TYPE_CLAIM) || save;
		save = check(locale, toText("&dТеперь вы будете создавать арены. От базовых регионов они отличаются только сеткой выделения WECui. Рекомендуется для миниигр и тематических режимов."), null, LocalesPaths.COMMAND_REGION_TYPE_ARENA) || save;
		save = check(locale, toText("&dТеперь вы будете создавать административные регионы. У данного типа регионов владельцем всегда является сервер."), null, LocalesPaths.COMMAND_REGION_TYPE_ADMIN) || save;

		save = check(locale, toText("&cУ вас нет ни одного региона."), null, LocalesPaths.COMMAND_LIST_EXCEPTION_EMPTY_SELF) || save;
		save = check(locale, toText("&cУ игрока нет ни одного региона."), null, LocalesPaths.COMMAND_LIST_EXCEPTION_EMPTY_OTHER) || save;
		save = check(locale, toText("&cПозиция не безопасна. Вы точно хотите телепортироваться? Кликните на это сообщение для подтверждения."), null, LocalesPaths.COMMAND_LIST_EXCEPTION_NOTSAFE) || save;
		save = check(locale, toText("&3Регионы: %player%"), null, LocalesPaths.COMMAND_LIST_TITLE) || save;

		save = check(locale, toText("&cНужно указать объем покупки."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно указать число."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cНужно указать число больше 0."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cМаксимальный объем покупки который вы можете указать: &b%max%&c."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cУ вас недостаточно денег для покупки."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_NOT_ENOUGH_MONEY) || save;
		save = check(locale, toText("&cПри выполнении транзакции что-то пошло не так. Подробности могут быть в консоли сервера."), null, LocalesPaths.COMMAND_BUYBLOCKS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aВы повысили свой лимит блоков на &b%size%&a. Ваш текущий лимит блоков: &b%volume%&a."), null, LocalesPaths.COMMAND_BUYBLOCKS_SUCCESS) || save;

		save = check(locale, toText("&cНужно указать объем покупки."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно указать число."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cНужно указать число больше 0."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cМаксимальный объем покупки который вы можете указать: &b%max%&c."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cУ вас недостаточно денег для покупки."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_NOT_ENOUGH_MONEY) || save;
		save = check(locale, toText("&cПри выполнении транзакции что-то пошло не так. Подробности могут быть в консоли сервера."), null, LocalesPaths.COMMAND_BUYCLAIMS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aВы повысили свой лимит регионов на &b%size%&a. Ваш текущий лимит регионов: &b%volume%&a."), null, LocalesPaths.COMMAND_BUYCLAIMS_SUCCESS) || save;

		save = check(locale, toText("&cНужно указать объем покупки."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно указать число."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cНужно указать число больше 0."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cМаксимальный объем покупки который вы можете указать: &b%max%&c."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cУ вас недостаточно денег для покупки."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_NOT_ENOUGH_MONEY) || save;
		save = check(locale, toText("&cПри выполнении транзакции что-то пошло не так. Подробности могут быть в консоли сервера."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aВы повысили свой лимит дочерних регионов на &b%size%&a. Ваш текущий лимит дочерних регионов: &b%volume%&a."), null, LocalesPaths.COMMAND_BUYSUBDIVISIONS_SUCCESS) || save;

		save = check(locale, toText("&cНужно указать объем продажи."), null, LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно указать число."), null, LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cНужно указать число больше 0."), null, LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cМаксимальный объем продажи который вы можете указать: &b%max%&c."), null, LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cПри выполнении транзакции что-то пошло не так. Подробности могут быть в консоли сервера."), null, LocalesPaths.COMMAND_SELLBLOCKS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aВы уменьшили свой лимит блоков на &b%size%&a. Ваш текущий лимит блоков: &b%volume%&a."), null, LocalesPaths.COMMAND_SELLBLOCKS_SUCCESS) || save;

		save = check(locale, toText("&cНужно указать объем продажи."), null, LocalesPaths.COMMAND_SELLCLAIMS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно указать число."), null, LocalesPaths.COMMAND_SELLCLAIMS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cНужно указать число больше 0."), null, LocalesPaths.COMMAND_SELLCLAIMS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cМаксимальный объем продажи который вы можете указать: &b%max%&c."), null, LocalesPaths.COMMAND_SELLCLAIMS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cПри выполнении транзакции что-то пошло не так. Подробности могут быть в консоли сервера."), null, LocalesPaths.COMMAND_SELLCLAIMS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aВы уменьшили свой лимит регионов на &b%size%&a. Ваш текущий лимит регионов: &b%volume%&a."), null, LocalesPaths.COMMAND_SELLCLAIMS_SUCCESS) || save;

		save = check(locale, toText("&cНужно указать объем продажи."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно указать число."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cНужно указать число больше 0."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_ENTERED_ZERO) || save;
		save = check(locale, toText("&cМаксимальный объем продажи который вы можете указать: &b%max%&c."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_TO_MUCH_VOLUME) || save;
		save = check(locale, toText("&cПри выполнении транзакции что-то пошло не так. Подробности могут быть в консоли сервера."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_EXCEPTION_ECONOMY_EXCEPTION) || save;
		save = check(locale, toText("&aВы уменьшили свой лимит дочерних регионов на &b%size%&a. Ваш текущий лимит дочерних регионов: &b%volume%&a."), null, LocalesPaths.COMMAND_SELLSUBDIVISIONS_SUCCESS) || save;

		save = check(locale, toText("&cНужно указать игрока онлайн."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_EXCEPTION_PLAYER_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно указать новый лимит."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_EXCEPTION_VOLUME_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно ввести число."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cВы ввели число меньше нуля."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_EXCEPTION_LESS_THEN_ZERO) || save;
		save = check(locale, toText("&aВы изменили лимит блоков игрока &b%player%&a. Теперь его лимит составляет &b%size%&a блоков."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_SUCCESS_SOURCE) || save;
		save = check(locale, toText("&aПредставитель администрации &b%player%&a изменил ваш лимит блоков. Теперь он составляет &b%size%&a блоков."), null, LocalesPaths.COMMAND_SETLIMITBLOCKS_SUCCESS_TARGET) || save;

		save = check(locale, toText("&cНужно указать игрока онлайн."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_EXCEPTION_PLAYER_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно указать новый лимит."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_EXCEPTION_VOLUME_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно ввести число."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cВы ввели число меньше нуля."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_EXCEPTION_LESS_THEN_ZERO) || save;
		save = check(locale, toText("&aВы изменили лимит приватов игрока &b%player%&a. Теперь его лимит составляет &b%size%&a приватов."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_SUCCESS_SOURCE) || save;
		save = check(locale, toText("&aПредставитель администрации &b%player%&a изменил ваш лимит приватов. Теперь он составляет &b%size%&a приватов."), null, LocalesPaths.COMMAND_SETLIMITCLAIMS_SUCCESS_TARGET) || save;

		save = check(locale, toText("&cНужно указать игрока онлайн."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_EXCEPTION_PLAYER_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно указать новый лимит."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_EXCEPTION_VOLUME_NOT_PRESENT) || save;
		save = check(locale, toText("&cНужно ввести число."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_EXCEPTION_WRONG_ARGUMENT) || save;
		save = check(locale, toText("&cВы ввели число меньше нуля."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_EXCEPTION_LESS_THEN_ZERO) || save;
		save = check(locale, toText("&aВы изменили лимит дочерних регионов игрока &b%player%&a. Теперь его лимит составляет &b%size%&a дочерних регионов."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_SUCCESS_SOURCE) || save;
		save = check(locale, toText("&aПредставитель администрации &b%player%&a изменил ваш лимит дочерних регионов. Теперь он составляет &b%size%&a дочерних регионов."), null, LocalesPaths.COMMAND_SETLIMITSUBDIVISIONS_SUCCESS_TARGET) || save;



		save = check(locale, toText("&cУ вас нет права создавать дочерние регионы в административном регионе."), null, LocalesPaths.REGION_CREATE_EXCEPTION_ADMIN_CLAIM) || save;
		save = check(locale, toText("&cУказанная позиция уже принадлежит другому игроку."), null, LocalesPaths.REGION_CREATE_EXCEPTION_POSITION_LOCKED) || save;
		save = check(locale, toText("&cВы достигли лимита доступных вам регионов. Ваш лимит: &b%size%&c."), null, LocalesPaths.REGION_CREATE_EXCEPTION_LARGE_VOLUME_REGIONS) || save;
		save = check(locale, toText("&cВы достигли лимита доступных вам дочерних регионов в текущем регионе. Ваш лимит: &b%size%&c."), null, LocalesPaths.REGION_CREATE_EXCEPTION_LARGE_VOLUME_SUBDIVISIONS) || save;
		save = check(locale, toText("&cВы выбрали слишком большой объем: &b%selected%&c. Выделите меньшую область для создания региона. Вы можете выделить &b%max% &cблоков."), null, LocalesPaths.REGION_CREATE_EXCEPTION_LARGE_VOLUME_BLOCKS) || save;
		save = check(locale, toText("&cУ точек совпадает одна из координат. Для кубоидов не допустимо совподение по XYZ. Для плоских регионов недопустимо совпадение по XZ."), null, LocalesPaths.REGION_CREATE_EXCEPTION_INCORRECT_COORDS) || save;
		save = check(locale, toText("&cСоздаваемый регион частично или полностью перекрывает уже существующий. Выберите другие позиции."), null, LocalesPaths.REGION_CREATE_EXCEPTION_REGIONS_INTERSECT) || save;
		save = check(locale, toText("&cСоздание региона было отменено."), null, LocalesPaths.REGION_CREATE_EXCEPTION_CENCELLED_EVENT) || save;
		save = check(locale, toText("&cНевозможно создать дочерний регион, так как он выходит за границы родительского региона. Базовые регионы так же не могут пересекаться."), null, LocalesPaths.REGION_CREATE_EXCEPTION_WRONG_SUBDIVISION_POSITIONS) || save;
		save = check(locale, toText("&dПозиция &b%pos% &dустановленна по координатам &b%target%&d."), null, LocalesPaths.REGION_CREATE_SETPOS) || save;
		save = check(locale, toText("&aРегион создан. Объем: &b%volume%&a. Введите команду /rg claim чтобы заприватить территорию."), null, LocalesPaths.REGION_CREATE_CREATE_BASIC) || save;
		save = check(locale, toText("&aДочерний регион создан. Объем: &b%volume%&a."), null, LocalesPaths.REGION_CREATE_SUBDIVISION) || save;

		save = check(locale, toText("&dТип региона: &b%type%"), null, LocalesPaths.REGION_WAND_TYPE) || save;
		save = check(locale, toText("&dВладелец: &b%owner%"), null, LocalesPaths.REGION_WAND_OWNER) || save;

		save = check(locale, toText("&cОдна из координат XYZ(3D)/XZ(2D) новой точки совпадает с такой же координатой противоположного угла региона. Выберите другую позицию."), null, LocalesPaths.REGION_RESIZE_EXCEPTION_INCORRECT_COORDS) || save;
		save = check(locale, toText("&cВыбран слишком маленький объем. Для завершения операции нужно еще &b%volume%&c."), null, LocalesPaths.REGION_RESIZE_EXCEPTION_SMALL_VOLUME) || save;
		save = check(locale, toText("&cНовый размер слишком велик. Вы выбрали: &b%selected%&c. блоков.\nДоступно блоков: &b%volume%&c."), null, LocalesPaths.REGION_RESIZE_EXCEPTION_LARGE_VOLUME) || save;
		save = check(locale, toText("&cНевозможно изменить размер региона так как это приведет к пересечению с другим регионом."), null, LocalesPaths.REGION_RESIZE_EXCEPTION_REGIONS_INTERSECT) || save;
		save = check(locale, toText("&cНевозможно изменить размер региона так как это приведет к выходу дочернего региона за его границы."), null, LocalesPaths.REGION_RESIZE_EXCEPTION_CHILD_OUT) || save;
		save = check(locale, toText("&dКликните еще раз в другом месте для изменения размера."), null, LocalesPaths.REGION_RESIZE_START) || save;
		save = check(locale, toText("&dРазмер региона изменен."), null, LocalesPaths.REGION_RESIZE_FINISH) || save;

		save = check(locale, toText("&cВы не можете взаимодействовать с блоками левой рукой в текущем регионе."), null, LocalesPaths.INTERACT_BLOCK_CANCEL_SECONDARY) || save;
		save = check(locale, toText("&cВы не можете взаимодействовать с блоками правой рукой в текущем регионе."), null, LocalesPaths.INTERACT_BLOCK_CANCEL_PRIMARY) || save;
		save = check(locale, toText("&cУ вас нет разрешения на выращивание в этом регионе."), null, LocalesPaths.CANCEL_GROWTH) || save;
		save = check(locale, toText("&cУ вас нет разрешения на размещение или изменение блоков в этом регионе."), null, LocalesPaths.CANCEL_PLACE) || save;
		save = check(locale, toText("&cУ вас нет разрешения на ломание или удаление блоков в этом регионе."), null, LocalesPaths.CANCEL_BREAK) || save;
		save = check(locale, toText("&cВ этом регионе нельзя летать. Полет выключен."), null, LocalesPaths.DISABLE_FLY) || save;
		save = check(locale, toText("&cВы вошли в регион в котором запрещены полеты. Вы снова сможете включить полет после выхода из этого региона."), null, LocalesPaths.DISABLE_FLY_ON_JOIN) || save;
		save = check(locale, toText("&cВы не можете телепортировать игрока в указанную позицию."), null, LocalesPaths.TELEPORT_OTHER_TO_REGION) || save;
		save = check(locale, toText("&cВы не можете телепортировать игрока из его текущей позиции."), null, LocalesPaths.TELEPORT_OTHER_FROM_REGION) || save;
		save = check(locale, toText("&cВы не можете телепортироваться из этого региона. Жемчуг возвращен в инвентарь."), null, LocalesPaths.TELEPORT_ENDERPEARL_FROM_REGION) || save;
		save = check(locale, toText("&cВы не можете войти в регион."), null, LocalesPaths.CANCEL_JOIN) || save;
		save = check(locale, toText("&cВы не можете выйти из региона."), null, LocalesPaths.CANCEL_EXIT) || save;
		save = check(locale, toText("&cВы не можете телепортироваться в этот регион. Жемчуг возвращен в инвентарь."), null, LocalesPaths.TELEPORT_ENDERPEARL_TO_REGION) || save;
		save = check(locale, toText("&cВы не можете телепортироваться из текущего региона."), null, LocalesPaths.TELEPORT_FROM_REGION) || save;
		save = check(locale, toText("&cПозиция телепортации находится в регионе в который нельзя телепортироваться."), null, LocalesPaths.TELEPORT_TO_REGION) || save;
		save = check(locale, toText("&cВы не можете взаимодействовать с данной сущностью правой рукой в текущем регионе."), null, LocalesPaths.INTERACT_ENTITY_CANCEL_PRIMARY) || save;
		save = check(locale, toText("&cВы не можете взаимодействовать с данной сущностью левой рукой в текущем регионе."), null, LocalesPaths.INTERACT_ENTITY_CANCEL_SECONDARY) || save;
		save = check(locale, toText("&cPvP запрещено в этом регионе."), null, LocalesPaths.PVP) || save;
		save = check(locale, toText("&cВы не можете нанести урон этой сущности в текущем регионе."), null, LocalesPaths.ENTITY_DAMAGE) || save;
		save = check(locale, toText("&aРегион сохранил ваш опыт и он и будет восстановлен после респавна."), null, LocalesPaths.KEEP_EXP) || save;
		save = check(locale, toText("&aРегион сохранил ваш инвентарь и он и будет восстановлен после респавна."), null, LocalesPaths.KEEP_INVENTORY) || save;

		save = check(locale, toText("&cБлок находится в регионе в котором у вас нет прав на стрельбу по блокам."), null, LocalesPaths.IMPACT_BLOCK) || save;
		save = check(locale, toText("&cЦель находится в регионе в котором у вас нет прав на стрельбу по сущностям."), null, LocalesPaths.IMPACT_ENTITY) || save;

		save = check(locale, toText("&cВы не можете использовать эту команду в текущем регионе."), null, LocalesPaths.COMMAND_EXECUTE) || save;
		save = check(locale, toText("&cВы не можете использовать эту команду в текущем регионе пока находитесь в бою с другим игроком."), null, LocalesPaths.COMMAND_EXECUTE_PVP) || save;

		save = check(locale, toText("&cВы не можете выбрасывать предметы в текущем регионе."), null, LocalesPaths.ITEM_DROP) || save;
		save = check(locale, toText("&cВы не можете подбирать предметы в текущем регионе."), null, LocalesPaths.ITEM_PICKUP) || save;

		save = check(locale, toText("&cВы не можете ездить верхом в этом регионе."), null, LocalesPaths.RIDING) || save;

		save = check(locale, toText("&cСобытие спавна сущности, предмета или опыта было отменено, так как в регионе установлен запрещающий флаг."), null, LocalesPaths.SPAWN) || save;

		save = check(locale, toText("&cВы не можете использовать этот предмет в текущем регионе."), null, LocalesPaths.INTERACT_ITEM) || save;
		save = check(locale, toText("&cВы не можете использовать этот предмет в текущем регионе."), null, LocalesPaths.ITEM_USE) || save;

		save = check(locale, toText("&cВы не можете использовать портал в текущем регионе."), null, LocalesPaths.PORTAL_USE) || save;
		
		if(save) save(locale);
	}

	private Component replace(Component component, Map<String, String> map) {
		for(Entry<String, String> entry : map.entrySet()) {
			component = component.replaceText(TextReplacementConfig.builder().match(entry.getKey()).replacement(Component.text(entry.getValue())).build());
		}
		return component;
	}

	private Component replaceComponent(Component component, Map<String, Component> map) {
		for(Entry<String, Component> entry : map.entrySet()) {
			component = component.replaceText(TextReplacementConfig.builder().match(entry.getKey()).replacement(entry.getValue()).build());
		}
		return component;
	}

	private AbstractLocaleUtil getAbstractLocaleUtil(Locale locale) {
		return localeService.getPluginLocales("regionguard").get(locale);
	}

	private Component toText(String string) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
	}

	private boolean check(Locale locale, Component value, String comment, Object... path) {
		return getAbstractLocaleUtil(locale).checkComponent(json, value, comment, path);
	}

	private void save(Locale locale) {
		getAbstractLocaleUtil(locale).saveLocaleNode();
	}

}
