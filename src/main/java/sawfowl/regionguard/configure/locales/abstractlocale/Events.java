package sawfowl.regionguard.configure.locales.abstractlocale;

import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.LocaleReference;
import sawfowl.regionguard.api.data.Region;

public interface Events {

	interface RegionEvents {

		interface Create extends LocaleReference {

			Component getNoAdminPerm();

			Component getPositionLocked();

			Component getLimitRegions(long limit);

			Component getLimitSubdivisions(long limit);

			Component getLimitBlocks(long size, long limit);

			Component getSmallVolume();

			Component getIncorrectCords();

			Component getIntersect();

			Component getCancel();

			Component getWrongSubdivisionPositions();

			Component getSetPos(int pos, Vector3i cords);

			Component getSuccess(Region region);

			Component getSuccessSubdivision(Region region);

		}

		interface Resize extends LocaleReference {

			Component getIncorrectCords();

			Component getSmallVolume(int size);

			Component getLimitBlocks(long size, long limit);

			Component getIntersect();

			Component getChildOut();

			Component getStart();

			Component getFinish();

		}

		interface WandInfo extends LocaleReference {

			Component getType(Region region);

			Component getOwner(Region region);

		}

		Create getCreate();

		Resize getResize();

		WandInfo getWandInfo();

	}

	interface Block {

		interface Interact {

			Component getPrimary();

			Component getSecondary();

		}

		Interact getInteract();

		Component getGrowth();

		Component getPlace();

		Component getBreak();

		Component getImpact();

	}

	interface Fly {

		Component getDisable();

		Component getDisableOnJoin();

	}

	interface Teleport {

		interface FromRegion {

			Component getOther();

			Component getSelf();

			Component getEnderpearl();

		}

		interface ToRegion {

			Component getOther();

			Component getSelf();

			Component getEnderpearl();

		}

		ToRegion getFromRegion();

		ToRegion getToRegion();

	}

	interface Entity {

		interface Interact {

			Component getPrimary();

			Component getSecondary();

		}

		Interact getInteract();

		Component getPvP();

		Component getDamage();

		Component getImpact();

		Component getSpawn();

	}

	interface Keep {

		Component getExp();

		Component getInventory();

	}

	interface Piston {

		Component getUse();

		Component getGrief();

	}

	interface Command {

		Component getExecute();

		Component getExecutePvP();

	}

	interface Item {

		Component getDrop();

		Component getPickup();

		Component getInteract();

		Component getUse();

	}

	interface Other {

		Component getCancelJoin();

		Component getCancelExit();

		Component getRiding();

		Component getPortalUse();

	}

	Fly getFly();

	Teleport getTeleport();

	RegionEvents getRegion();

	Block getBlock();

	Entity getEntity();

	Keep getKeep();

	Command getCommand();

	Item getItem();

	Other getOther();

}
