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

			Component getLimitRegions(long size, long limit);

			Component getLimitSubdivisions(long size, long limit);

			Component getLimitBlocks(long size, long limit);

			Component getSmallVolume(int size);

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

	interface Block extends LocaleReference {

		interface Interact extends LocaleReference {

			Component getPrimary();

			Component getSecondary();

		}

		Interact getInteract();

		Component getGrowth();

		Component getPlace();

		Component getBreak();

		Component getImpact();

	}

	interface Fly extends LocaleReference {

		Component getDisable();

		Component getDisableOnJoin();

	}

	interface Teleport extends LocaleReference {

		interface FromRegion extends LocaleReference {

			Component getOther();

			Component getSelf();

			Component getEnderpearl();

		}

		interface ToRegion extends LocaleReference {

			Component getOther();

			Component getSelf();

			Component getEnderpearl();

		}

		FromRegion getFromRegion();

		ToRegion getToRegion();

		Component getPortalUse();

	}

	interface Entity extends LocaleReference {

		interface Interact extends LocaleReference {

			Component getPrimary();

			Component getSecondary();

		}

		Interact getInteract();

		Component getPvP();

		Component getDamage();

		Component getImpact();

		Component getSpawn();

		Component getRiding();

	}

	interface Keep extends LocaleReference {

		Component getExp();

		Component getInventory();

	}

	interface Piston extends LocaleReference {

		Component getUse();

		Component getGrief();

	}

	interface Command extends LocaleReference {

		Component getExecute();

		Component getExecutePvP();

	}

	interface Item extends LocaleReference {

		Component getDrop();

		Component getPickup();

		Component getInteract();

		Component getUse();

	}

	interface Move extends LocaleReference {

		Component getJoin();

		Component getExit();

	}

	Fly getFly();

	Teleport getTeleport();

	RegionEvents getRegion();

	Block getBlock();

	Entity getEntity();

	Keep getKeep();

	Piston getPiston();

	Command getCommand();

	Item getItem();

	Move getMove();

}
