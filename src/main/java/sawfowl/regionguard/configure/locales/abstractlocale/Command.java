package sawfowl.regionguard.configure.locales.abstractlocale;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.data.Region;

public interface Command {

	interface Main extends Command {

		Component getTitle();

		Component getPadding();

	}

	interface Claim extends Command {

		Component getRegionNotFound();

		Component getWorldNotFound(String world);

		Component getFail(Vector3i min, Vector3i max);

		Component getSuccess();

	}

	interface Clear extends Command {

		Component getSuccess();
		
	}

	interface Delete extends Command {

		Component getRegionNotFound();

		Component getNotOwner();

		Component getContainsChild();

		Component getSuccesRegen();

		Component getSuccesWhithChilds();

		Component getSuccesChild();

		Component getSucces();

	}

	interface Flag extends Command {

		Component getTitle();

		Component getPadding();

		Component getSucces();

	}

	interface Info extends Command {

		Component getTitle();

		Component getPadding();

		Component getDeleteButton();

		Component getDeleteConfirmRequest();

		Component getFlags();

		Component getUUID(Region region);

		Component getName(Component name);

		Component getType(Region region);

		Component getCreated(Region region);

		Component getOwner(Region region);

		Component getOwnerUUID(Region region);

		Component getMembers(Region region);

		Component getMin(Region region);

		Component getMax(Region region);

		Component getSelector(Region region);

	}

	interface Leave extends Command {

		Component getRegionNotFound();

		Component getOwner();

		Component getNotTrusted();

		Component getConfirmRequest();

		Component getSucces();

	}

	interface Limits extends Command {

		Component getClaims(long claimed, long limit, long max);

		Component getBlocks(long claimed, long limit, long max);

		Component getMembers(long claimed, long limit, long max);

		Component getSubdivisions(long claimed, long limit, long max);

	}

	interface List extends Command {

		Component getTitle();

		Component getPadding();

		Component getEmptyOther();

		Component getEmptySelf();

	}

	interface SetCreatingType extends Command {

		Component get(RegionTypes type);

	}

	interface SetMessage extends Command {

		Component getRegionNotFound();

		Component getNotTrusted();

		Component getLowTrust();

		Component getTooLong();

		Component getSuccesJoin(boolean clear);

		Component getSuccesExit(boolean clear);

	}

	interface SetName extends Command {

		Component getRegionNotFound();

		Component getNotTrusted();

		Component getLowTrust();

		Component getSucces(boolean clear);

	}

	interface SetOwner extends Command {

		Component getRegionNotFound();

		Component getOnlyOwner();

		Component getAlreadyOwner(GameProfile profile);

		Component getStaffConfirmRequest(GameProfile profile);

		Component getConfirmRequest(GameProfile profile);

		Component getSuccessFromStaff(Region region);

		Component getSuccess(Region region);

		Component getSuccessNewOwner(Region region);

	}

	interface SetSelectorType extends Command {

		Component getCuboid();

		Component getFlat();

	}

	interface Trust extends Command {

		Component getRegionNotFound();

		Component getAdminClaim();

		Component getLowTrust();

		Component getLimitReached();

		Component getNotOwner();

		Component getSuccess(String trustlevel, GameProfile profile);

		Component getSuccessTarget(String trustlevel, ServerPlayer profile, Region region);

	}

	interface Untrust extends Command {

		Component getRegionNotFound();

		Component getLowTrust();

		Component getPlayerIsOwner();

		Component getNotOwner();

		Component getSuccess(GameProfile profile);

		Component getSuccessTarget(GameProfile profile, Region region);

	}

	interface UpdateDefaultFlags extends Command {

		Component getSuccess(RegionTypes type);

	}

	interface Wand extends Command {

		Component getExist();

		Component getFullInventory();

		Component getSuccess();

	}

	interface WeCUI extends Command {

		Component getEnable();

		Component getDisable();

	}

	Main getMain();

	Claim getClaim();

	Clear getClear();

	Delete getDelete();

	Flag getFlag();

	Info getInfo();

	Leave getLeave();

	Limits getLimits();

	List getList();

	SetCreatingType getCreatingType();

	Trust getTrust();

	Untrust getUntrust();

	UpdateDefaultFlags getUpdateDefaultFlags();

	Wand getWand();

	WeCUI getWeCUI();

	Component getDescription();

}
