package sawfowl.regionguard.api.data;

public abstract class FlagSettings {

	public abstract boolean isAllowArgs();

	public abstract boolean isAllowSource(String source);

	public abstract boolean isAllowTarget(String target);

	public abstract boolean isAllowSourceEntity();

	public abstract boolean isAllowSourceDamageType();

	public abstract boolean isAllowTargetEntity();

	public abstract boolean isAllowTargetItem();

	public abstract boolean isAllowTargetBlock();
}
