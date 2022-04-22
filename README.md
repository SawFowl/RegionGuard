# RegionGuard

#### Features:
- The commands are partially similar to those in the WorldGuard plugin.
- Working with flags is similar to what you may have seen in the GriefPrevention plugin.
- WECui support. No dependence on WorldEdit.
- Players in regions can be assigned different roles.
- Player limits are set via metadata in the permissions plugin.
- Developers can use the plugin API to extend its functionality and use its functions in their plugins.


#### Permissions:
regionguard.user.help - Main command. \
regionguard.user.wand - Get an item for allocating regions. \
regionguard.user.claim - Claimed region. \
regionguard.user.delete - Delete region. \
regionguard.user.info - Region info command. \
regionguard.user.trust - Add a player to the region and assign him permissions. \
regionguard.user.setmessage- Set/clear join/exit message for region. \
regionguard.user.setname - Set name for region. \
regionguard.user.selector - Change the type of area you want to select. \
regionguard.flag.command - View Flags. You cannot change a flag without a permission for him. \
regionguard.user.wecui - Displaying region boundaries with WECui mod, as well as switching the status of working with the mod. \
regionguard.flags - Access flags by their lowercase name. \
regionguard.flag.bypass - Flags bypass. For admins. You can add specific flags in lower-case to the permission. \
regionguard.unlimit.blocks - Unlimited number of blocks to create regions. \
regionguard.unlimit.claims - Unlimited number of regions created. \
regionguard.unlimit.subdivisions = Unlimited subdivisions per region. \
regionguard.staff.delete - Removing the region of any player. \
regionguard.staff.resize - Change the size of any player's region. \
regionguard.staff.trust - Adding players to the region of any player. \
regionguard.staff.setmessage - Set/remove the join/exit message for any region. \
regionguard.staff.setname - Set a name for any region. \
regionguard.staff.setregiontype - Change the type of region and select the type of regions to be created. \
regionguard.staff.flag - Changing flags in any region.\
regionguard.staff.adminclaim - Creation of subdivisions in admin regions.

#### Region member types:
Hunter - Can only attack hostile monsters. \
Sleep - Can only use the bed. \
Container - Can use a bed and containers. \
User - An ordinary member of the region. Can break and place blocks, attack any mobs and so on. \
Builder - Can break, place blocks, use containers. \
Manager - Has all the same rights as a normal region member, but in addition can add or exclude other members. \
Owner - Region Owner.

#### Commands:
/rg wand - Get an item to create regions. \
/rg claim - Claim the allocated region. \
/rg delete - Delete the region in the current position. \
/rg info - Information about the region. \
/rg limits - Information about your limits. \
/rg setname <ClearFlag> <Locale> [Name] - Set the name of the region. \
/rg setmessage <CommandFlags> <Locale> [Message] - Set/remove the join/exit message in the region. \
/rg flag [FlagName] [Value] <Source> <Target> - Set the flag parameters. \
/rg leave - Leave from region. \
/rg setowner [Player] - Set the owner of the region. \
/rg trust [Player] [TrustType] - Add a player to the region and specify his rights in the region. \
/rg untrust [Player] - Remove a player from the region. \
/rg setselector - Select the type of area selection. \
/rg setcreatingtype - Select the type of region to be created. \
/rg wecui - Switch the sending status of WECui packets. 


Example limits:
lp group default meta set regionguard.limit.claims 3
lp group default meta set regionguard.limit.subdivisions 5
lp group default meta set regionguard.limit.blocks 1000

WECui forge version - https://www.curseforge.com/minecraft/mc-mods/worldeditcui-forge-edition-3
