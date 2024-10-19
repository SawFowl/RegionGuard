package sawfowl.regionguard.configure.locales.abstractlocale;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.LocaleReference;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.Region;

public interface Command extends LocaleReference {

	interface CommandInfo extends LocaleReference {

		Component getDescription();

	}

	interface Main extends CommandInfo {

		Component getTitle();

		Component getPadding();

	}

	interface Claim extends CommandInfo {

		Component getRegionNotFound();

		Component getWorldNotFound(String world);

		Component getIntersect(Vector3i min, Vector3i max);

		Component getSuccess();

	}

	interface Clear extends CommandInfo {

		Component getSuccess();
		
	}

	interface Delete extends CommandInfo {

		Component getConfirmRequest();

		Component getRegen();

		Component getSuccesWhithChilds();

		Component getSuccesChild();

		Component getSuccess();

	}

	interface Flag extends CommandInfo {

		interface Hover extends LocaleReference {

			Component getRemove();

			Component getTrue();

			Component getFalse();

			Component getSuggestArgs();

			Component getHoverValues(FlagValue flagValue);

		}

		Hover getHover();

		Component getTitle();

		Component getPadding();

		Component getNotPermitted();

		Component getNotPermittedFlag(String flag);

		Component getValueNotPresent();

		Component getInvalidSource();

		Component getInvalidTarget();

		Component getSuccess();

	}

	interface Info extends CommandInfo {

		interface Buttons extends LocaleReference {

			Component getDelete();

			Component getFlags();

		}

		Buttons getButtons();

		Component getHeader();

		Component getPadding();

		Component getUUID(Region region);

		Component getName(Component name);

		Component getType(Region region);

		Component getCreated(String created);

		Component getOwner(Region region);

		Component getOwnerUUID(Region region);

		Component getMembers(Region region);

		Component getMin(Region region);

		Component getMax(Region region);

		Component getSelector(Region region);

	}

	interface Leave extends CommandInfo {

		Component getOwner();

		Component getNotTrusted();

		Component getConfirmRequest();

		Component getSuccess();

	}

	interface Limits extends CommandInfo {

		interface Transaction extends CommandInfo {

			interface Limit extends CommandInfo {

				Component getSuccess(long size, long limit);

			}

			Limit getBlocksLimit();

			Limit getClaimsLimit();

			Limit getSubdivisionsLimit();

			Limit getMembersLimit();

		}

		interface Buy extends Transaction {}

		interface Sell extends Transaction {}

		interface Set extends CommandInfo {

			interface Limit extends CommandInfo {

				Component getSuccess(long limit);

				Component getSuccessStaff(String player, long limit);

			}

			Limit getBlocksLimit();

			Limit getClaimsLimit();

			Limit getSubdivisionsLimit();

			Limit getMembersLimit();

			Component getLessThanZero();

		}

		Buy getBuy();

		Sell getSell();

		Set getSet();

		Component getTitle(String player);

		Component getPadding();

		Component getClaims(Object claimed, Object limit, Object max);

		Component getBlocks(Object claimed, Object limit, Object max);

		Component getMembers(Object claimed, Object limit, Object max);

		Component getSubdivisions(Object claimed, Object limit, Object max);

	}

	interface List extends CommandInfo {

		Component getTitle(String player);

		Component getPadding();

		Component getEmptyOther();

		Component getEmptySelf();

		Component getTeleportNotSafe();

		default Component getEmpty(boolean other) {
			return other ? getEmptyOther() : getEmptySelf();
		}

	}

	interface SetCreatingType extends CommandInfo {

		Component get(RegionTypes type);

	}

	interface SetMessage extends CommandInfo {

		Component getNotTrusted();

		Component getLowTrust();

		Component getTooLong();

		Component getTypeNotPresent();

		Component getSuccessJoin(boolean clear);

		Component getSuccessExit(boolean clear);

	}

	interface SetName extends CommandInfo {

		Component getNotTrusted();

		Component getLowTrust();

		Component getTooLong();

		Component getSuccess(boolean clear);

	}

	interface SetOwner extends CommandInfo {

		Component getOnlyOwner();

		Component getAdminClaim();

		Component getAlreadyOwner();

		Component getAlreadyOwnerStaff(GameProfile profile);

		Component getConfirmRequest(GameProfile profile);

		Component getSuccessFromStaff(Region region);

		Component getSuccess(Region region, GameProfile newOwner);

		Component getSuccessNewOwner(Region region, GameProfile newOwner);

	}

	interface SetSelectorType extends CommandInfo {

		Component get(boolean flat);

	}

	interface Trust extends CommandInfo {

		Component getAdminClaim();

		Component getLowTrust();

		Component getLimitReached();

		Component getNotOwner();

		Component getSuccess(String trustlevel, GameProfile profile);

		Component getSuccessTarget(String trustlevel, ServerPlayer player, Region region);

	}

	interface Untrust extends CommandInfo {

		Component getLowTrust();

		Component getPlayerIsOwner();

		Component getNotOwner();

		Component getSuccess(GameProfile profile);

		Component getSuccessTarget(ServerPlayer player, Region region);

	}

	interface UpdateDefaultFlags extends CommandInfo {

		Component getSuccess(RegionTypes type);

		Component getException();

	}

	interface Wand extends CommandInfo {

		Component getExist();

		Component getFullInventory();

		Component getSuccess();

	}

	interface WeCUI extends CommandInfo {

		Component get(boolean enable);

	}

	interface Exceptions extends LocaleReference {

		Component getOnlyPlayer();

		Component getPlayerNotPresent();

		Component getTargetSelf();

		Component getRegionNotFound();

		Component getFlagNotPresent();

		Component getMessageNotPresent();

		Component getNameNotPresent();

		Component getVolumeNotPresent();

		Component getEnteredZero();

		Component getNotEnoughMoney();

		Component getNotOwner();

		Component getEconomyException();

		Component getMaxValue(long value);

		Component getRegionTypeNotPresent();

		Component getSelectorTypeNotPresent();

		Component getTrustTypeNotPresent();

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

	SetMessage getSetMessage();

	SetName getSetName();

	SetOwner getSetOwner();

	SetSelectorType getSetSelectorType();

	Trust getTrust();

	Untrust getUntrust();

	UpdateDefaultFlags getUpdateDefaultFlags();

	Wand getWand();

	WeCUI getWeCUI();

	Exceptions getExceptions();

}
