package sawfowl.regionguard.configure.locales.ru;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.abstractlocale.Command;
import sawfowl.regionguard.configure.locales.ru.command.ImplementClaim;
import sawfowl.regionguard.configure.locales.ru.command.ImplementClear;
import sawfowl.regionguard.configure.locales.ru.command.ImplementDelete;
import sawfowl.regionguard.configure.locales.ru.command.ImplementExceptions;
import sawfowl.regionguard.configure.locales.ru.command.ImplementFlag;
import sawfowl.regionguard.configure.locales.ru.command.ImplementInfo;
import sawfowl.regionguard.configure.locales.ru.command.ImplementLeave;
import sawfowl.regionguard.configure.locales.ru.command.ImplementLimits;
import sawfowl.regionguard.configure.locales.ru.command.ImplementList;
import sawfowl.regionguard.configure.locales.ru.command.ImplementMain;
import sawfowl.regionguard.configure.locales.ru.command.ImplementSetCreatingType;
import sawfowl.regionguard.configure.locales.ru.command.ImplementSetMessage;
import sawfowl.regionguard.configure.locales.ru.command.ImplementSetName;
import sawfowl.regionguard.configure.locales.ru.command.ImplementSetOwner;
import sawfowl.regionguard.configure.locales.ru.command.ImplementSetSelectorType;
import sawfowl.regionguard.configure.locales.ru.command.ImplementTrust;
import sawfowl.regionguard.configure.locales.ru.command.ImplementUntrust;
import sawfowl.regionguard.configure.locales.ru.command.ImplementUpdateDefaultFlags;
import sawfowl.regionguard.configure.locales.ru.command.ImplementWand;
import sawfowl.regionguard.configure.locales.ru.command.ImplementWeCUI;

@ConfigSerializable
public class ImplementCommand implements Command {

	public ImplementCommand() {}

	@Setting("Main")
	private ImplementMain main = new ImplementMain();
	@Setting("Claim")
	private ImplementClaim claim = new ImplementClaim();
	@Setting("Clear")
	private ImplementClear clear = new ImplementClear();
	@Setting("Delete")
	private ImplementDelete delete = new ImplementDelete();
	@Setting("Flag")
	private ImplementFlag flag = new ImplementFlag();
	@Setting("Info")
	private ImplementInfo info = new ImplementInfo();
	@Setting("Leave")
	private ImplementLeave leave = new ImplementLeave();
	@Setting("Limits")
	private ImplementLimits limits = new ImplementLimits();
	@Setting("List")
	private ImplementList list = new ImplementList();
	@Setting("SetCreatingType")
	private ImplementSetCreatingType setCreatingType = new ImplementSetCreatingType();
	@Setting("SetMessage")
	private ImplementSetMessage setMessage = new ImplementSetMessage();
	@Setting("SetName")
	private ImplementSetName setName = new ImplementSetName();
	@Setting("SetOwner")
	private ImplementSetOwner setOwner = new ImplementSetOwner();
	@Setting("SetSelectorType")
	private ImplementSetSelectorType setSelectorType = new ImplementSetSelectorType();
	@Setting("Trust")
	private ImplementTrust trust = new ImplementTrust();
	@Setting("Untrust")
	private ImplementUntrust untrust = new ImplementUntrust();
	@Setting("UpdateDefaultFlags")
	private ImplementUpdateDefaultFlags updateDefaultFlags = new ImplementUpdateDefaultFlags();
	@Setting("Wand")
	private ImplementWand wand = new ImplementWand();
	@Setting("WeCUI")
	private ImplementWeCUI weCUI = new ImplementWeCUI();
	@Setting("Exceptions")
	private ImplementExceptions exceptions = new ImplementExceptions();

	@Override
	public Main getMain() {
		return main;
	}

	@Override
	public Claim getClaim() {
		return claim;
	}

	@Override
	public Clear getClear() {
		return clear;
	}

	@Override
	public Delete getDelete() {
		return delete;
	}

	@Override
	public Flag getFlag() {
		return flag;
	}

	@Override
	public Info getInfo() {
		return info;
	}

	@Override
	public Leave getLeave() {
		return leave;
	}

	@Override
	public Limits getLimits() {
		return limits;
	}

	@Override
	public List getList() {
		return list;
	}

	@Override
	public SetCreatingType getCreatingType() {
		return setCreatingType;
	}

	@Override
	public SetMessage getSetMessage() {
		return setMessage;
	}

	@Override
	public SetName getSetName() {
		return setName;
	}

	@Override
	public SetOwner getSetOwner() {
		return setOwner;
	}

	@Override
	public SetSelectorType getSetSelectorType() {
		return setSelectorType;
	}

	@Override
	public Trust getTrust() {
		return trust;
	}

	@Override
	public Untrust getUntrust() {
		return untrust;
	}

	@Override
	public UpdateDefaultFlags getUpdateDefaultFlags() {
		return updateDefaultFlags;
	}

	@Override
	public Wand getWand() {
		return wand;
	}

	@Override
	public WeCUI getWeCUI() {
		return weCUI;
	}

	@Override
	public Exceptions getExceptions() {
		return exceptions;
	}

}
