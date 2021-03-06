package sawfowl.regionguard.api.data;

import java.util.Objects;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class FlagValue {

	public FlagValue() {}

	public FlagValue(String source, String target) {
		if(source != null) this.source = source;
		if(target != null) this.target = target;
	}

	public FlagValue(boolean value, String source, String target) {
		this.value = value;
		if(source != null) this.source = source;
		if(target != null) this.target = target;
	}

	@Setting("Value")
	private boolean value = true;
	@Setting("Source")
	private String source = "all";
	@Setting("Target")
	private String target = "all";

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public boolean getValue() {
		return value;
	}

	public boolean isBasic() {
		return source.equals("all") && target.equals("all");
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		FlagValue other = (FlagValue) obj;
		return Objects.equals(source, other.source) && Objects.equals(target, other.target);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, target);
	}

	@Override
	public String toString() {
		return "FlagValue(Source=" + source + ", Target" + target + ")";
	}

}
