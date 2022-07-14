# RegionGuard

### For server admins:
#### Features:
- The commands are partially similar to those in the WorldGuard plugin.
- Working with flags is similar to what you may have seen in the GriefPrevention plugin.
- WECui support. No dependence on WorldEdit.
- Players in regions can be assigned different roles.
- Player limits are set via metadata in the permissions plugin.
- Developers can use the plugin API to extend its functionality and use its functions in their plugins.
- Optional Sponge economy support.
- MySQL support.
- Ability to regenerate territory when a region is removed.

#### Commands:
```yaml
/rg wand - Get an item to create regions.
/rg claim - Claim the allocated region.
/rg delete - Delete the region.
/rg info - Information about the region.
/rg limits - Information about your limits.
/rg setname <ClearFlag> <OptionalLocale> [Name] - Set the name of the region.
/rg setmessage <CommandFlags> <Locale> [Message] - Set/remove the join/exit message in the region.
/rg flag [FlagName] [Value] <Source> <Target> - Set the flag parameters.
/rg leave - Leave from region.
/rg setowner [Player] - Set the owner of the region.
/rg trust [Player] [TrustType] - Add a player to the region and specify his rights in the region.
/rg untrust [Player] - Remove a player from the region.
/rg setselector [Type] - Select the type of area selection.
/rg setcreatingtype [Type] - Select the type of region to be created.
/rg wecui - Switch the sending status of WECui packets.
/rg list - Show list of regions.
/rg buyblocks [Volume] - Payment in game currency to increase the limit of blocks.
/rg buyclaims [Volume] - Payment in game currency to increase the limit of claims.
/rg buysubdivisions [Volume] - Payment in game currency to increase the limit of subdivisions.
/rg sellblocks [Volume] - Selling the limit of blocks for game currency.
/rg sellclaims [Volume] - Selling the limit of claims for game currency.
/rg sellsubdivisions [Volume] - Selling the limit of subdivisions for game currency.
/rg setlimit blocks [Player] [Size] - Selling the limit of blocks for game currency.
/rg setlimit claims [Player] [Size] - Selling the limit of claims for game currency.
/rg setlimit subdivisions [Player] [Size] - Selling the limit of subdivisions for game currency.
/rg setlimit members [Player] [Size] - Changing the limit of members in the player's regions.
```

#### Region member types:
```yaml
Hunter - Can only attack hostile monsters.
Sleep - Can only use the bed.
Container - Can use a bed and containers.
User - An ordinary member of the region. Can break and place blocks, attack any mobs and so on.
Builder - Can break, place blocks, use containers.
Manager - Has all the same rights as a normal region member, but in addition can add or exclude other members.
Owner - Region Owner.
```

#### Permissions:
```yaml
regionguard.user.help - Main command.
regionguard.user.wand - Get an item for allocating regions.
regionguard.user.claim - Claimed region.
regionguard.user.delete - Delete region.
regionguard.user.info - Region info command.
regionguard.user.trust - Add a player to the region and assign him permissions.
regionguard.user.setmessage - Set/clear join/exit message for region.
regionguard.user.setname - Set name for region.
regionguard.user.selector - Change the type of area you want to select.
regionguard.user.flag - View Flags. You cannot change a flag without a permission for him.
regionguard.user.wecui - Displaying region boundaries with WECui mod, as well as switching the status of working with the mod.
regionguard.user.buy.blocks - Payment in game currency to increase the limit of blocks.
regionguard.user.buy.claims - Payment in game currency to increase the limit of claims.
regionguard.user.buy.subdivisions - Payment in game currency to increase the limit of subdivisions.
regionguard.user.buy.members - Payment in game currency to increase the limit of region members.
regionguard.user.sell.blocks - Selling the limit of blocks for game currency.
regionguard.user.sell.claims - Selling the limit of claims for game currency.
regionguard.user.sell.subdivisions - Selling the limit of subdivisions for game currency.
regionguard.user.sell.members - Selling the limit of region members for game currency.
regionguard.user.list - Show list of regions.
regionguard.user.teleport - Teleportation to the region.
regionguard.flags - Access flags by their lowercase name.
regionguard.flag.bypass - Flags bypass. For admins. You can add specific flags in lower-case to the permission.
regionguard.unlimit.blocks - Unlimited number of blocks that can be claimed.
regionguard.unlimit.claims - Unlimited number of regions created.
regionguard.unlimit.subdivisions - Unlimited subdivisions per region.
regionguard.unlimit.members - Unlimited number of players you can add to the region.
regionguard.staff.delete - Removing the region of any player.
regionguard.staff.resize - Change the size of any player’s region.
regionguard.staff.trust - Adding players to the region of any player.
regionguard.staff.setmessage - Set/remove the join/exit message for any region.
regionguard.staff.setname - Set a name for any region.
regionguard.staff.setregiontype - Change the type of region and select the type of regions to be created.
regionguard.staff.flag - Changing flags in any region.
regionguard.staff.list - Getting a list of regions of any player. The ability to teleport is available by default.
regionguard.staff.setlimit.blocks - Change the blocks limit of the player.
regionguard.staff.setlimit.claims - Change the claims limit of the player.
regionguard.staff.setlimit.subdivisions - Change the subdivisions limit of the player.
regionguard.staff.setlimit.subdivisions - Change the subdivisions limit of the player.
regionguard.staff.setlimit.members - Changing the limit of members in the player regions.
regionguard.staff.adminclaim - Creation of subdivisions in admin regions.
```
#### Metaperms:
```yaml
regionguard.limit.blocks - The limit of blocks a player can claim.
regionguard.limit.claims - Player claims limit.
regionguard.limit.subdivisions - Player subdivisions limit per region.
regionguard.limit.members - Members limit for each player region.
regionguard.limit.max.blocks - The maximum number of blocks a player can claim when using the economy.
regionguard.limit.max.claims - The maximum number of regions a player can claim when using the economy.
regionguard.limit.max.subdivisions - The maximum number of subdivisions a player can claim when using the economy.
regionguard.limit.max.members - The maximum number of region members a player can add using the economy.
regionguard.buy.blockprice - Purchase price of 1 block.
regionguard.buy.regionprice - Purchase price of 1 region.
regionguard.buy.subdivisionprice - Purchase price of 1 subdivision.
regionguard.buy.memberprice - The price for increasing the limit of participants by 1.
regionguard.sell.blockprice - Selling price of 1 block.
regionguard.sell.regionprice - Selling price of 1 region.
regionguard.sell.subdivisionprice - Selling price of 1 subdivision.
regionguard.sell.memberprice - The price of reducing the limit of participants by 1.
regionguard.transaction.currency - Currency in which the player will be buying and selling limits.
```

#### Example set metaperms:
```
lp group default meta set regionguard.limit.claims 3
lp group default meta set regionguard.limit.subdivisions 5
lp group default meta set regionguard.limit.blocks 1000
lp group default meta set regionguard.limit.members 5
```

WECui forge version -> https://www.curseforge.com/minecraft/mc-mods/worldeditcui-forge-edition-3

### For developers:
javadoc -> https://sawfowl.github.io/RegionGuard/
##### Get API:
```JAVA
@Plugin("pluginid")
public class Main {
	private Main instance;
	private Logger logger;
	private RegionAPI regionAPI;

	// Get API. This happens in event `ConstructPluginEvent`.
	@Listener
	public void onRegionAPIPostEvent(RegionAPIPostEvent.PostAPI event) {
		instance = this;
		logger = LogManager.getLogger("PluginName");
		regionAPI = event.getAPI();
	}

	// At this stage, you can access the regions.
	@Listener
	public void onCompleteLoadRegionsEvent(RegionAPIPostEvent.CompleteLoadRegions event) {
		logger.info(event.getTotalLoaded());
		//TODO
	}

}
```
##### Gradle:
```gradle
repositories {
	...
	maven { 
		name = "JitPack"
		url 'https://jitpack.io' 
	}
}
dependencies {
	...
	implementation 'com.github.SawFowl:RegionGuard:1.2.1'
}
```
