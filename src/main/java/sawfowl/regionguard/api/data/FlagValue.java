package sawfowl.regionguard.api.data;

import java.util.Objects;

import org.spongepowered.api.util.Tristate;
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

	public Tristate asTristate() {
		return Tristate.fromBoolean(value);
	}

	public boolean equalsTo(String source, String target) {
		return (source == null ? this.source.equals("all") : source.equals(this.source)) && (target == null ? this.target.equals("all") : target.equals(this.target));
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
