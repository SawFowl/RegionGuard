package sawfowl.regionguard.implementsapi.data;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import org.spongepowered.api.data.persistence.DataContainer;

import sawfowl.regionguard.api.data.FlagValue;

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
				if(id != null) source = id;
				return this;
			}
			
			@Override
			public Builder setTarget(String id) {
				if(id != null) target = id;
				return this;
			}
		};
	}

	private boolean value = true;
	private String source = "all";
	private String target = "all";

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public boolean getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(this == obj) return true;
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
