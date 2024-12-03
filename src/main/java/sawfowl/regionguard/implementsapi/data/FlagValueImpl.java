package sawfowl.regionguard.implementsapi.data;

import java.util.Objects;

import org.spongepowered.api.data.persistence.DataContainer;

import com.google.gson.JsonObject;

import sawfowl.regionguard.api.data.FlagValue;

public class FlagValueImpl implements FlagValue {

	public FlagValueImpl(){}

	public FlagValue.Builder builder() {
		return new Builder() {
			
			@Override
			public FlagValue build() {
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

			@Override
			public FlagValue fromJson(JsonObject jsonObject) {
				if(jsonObject.has("Value")) value = jsonObject.get("Value").getAsBoolean();
				if(jsonObject.has("Source")) source = jsonObject.get("Source").getAsString();
				if(jsonObject.has("Target")) target = jsonObject.get("Target").getAsString();
				return build();
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
		return "FlagValue[value=" + value + ", source=" + source + ", target=" + target + "]";
	}

	@Override
	public int contentVersion() {
		return 0;
	}

	@Override
	public DataContainer toContainer() {
		return null;
	}

	@Override
	public JsonObject asJson() {
		JsonObject json = new JsonObject();
		json.addProperty("Value", value);
		json.addProperty("Source", source);
		json.addProperty("Target", target);
		return json;
	}

}
