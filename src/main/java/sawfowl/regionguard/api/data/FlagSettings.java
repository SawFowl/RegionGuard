package sawfowl.regionguard.api.data;

public interface FlagSettings {

	boolean isAllowArgs();

	boolean isAllowSource(String source);

	boolean isAllowTarget(String target);

	boolean isAllowSourceEntity();

	boolean isAllowSourceDamageType();

	boolean isAllowTargetEntity();

	boolean isAllowTargetItem();

	boolean isAllowTargetBlock();

}
