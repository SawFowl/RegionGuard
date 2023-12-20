package sawfowl.regionguard.data;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.api.data.FlagValue;

@ConfigSerializable
public class FlagValueImpl implements FlagValue {

	public FlagValueImpl(){}

	public FlagValue.Builder builder() {
		return new Builder() {
			
			@Override
			public @NotNull FlagValue build() {
				return FlagValueImpl.this;
			}
			
			@Override
			public Builder setValue(boolean value) {
				FlagValueImpl.this.value = value;
				return this;
			}
			
			@Override
			public Builder setSource(String id) {
				source = id;
				return this;
			}
			
			@Override
			public Builder setTarget(String id) {
				target = id;
				return this;
			}
		};
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
		FlagValueImpl other = (FlagValueImpl) obj;
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

	@Override
	public int contentVersion() {
		return 0;
	}

	@Override
	public DataContainer toContainer() {
		return null;
	}

}
