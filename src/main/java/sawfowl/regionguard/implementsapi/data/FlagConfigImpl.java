package sawfowl.regionguard.implementsapi.data;

import org.spongepowered.api.data.persistence.DataContainer;

import sawfowl.regionguard.api.data.FlagConfig;
import sawfowl.regionguard.api.data.FlagSettings;

public class FlagConfigImpl implements FlagConfig {

	public FlagConfigImpl(){}

	public FlagConfig.Builder builder() {
		return new Builder() {

			@Override
			public FlagConfig build() {
				return FlagConfigImpl.this;
			}

			@Override
			public Builder setName(String name) {
				FlagConfigImpl.this.name = name;
				return this;
			}

			@Override
			public Builder setSettings(FlagSettings flagSettings) {
				FlagConfigImpl.this.flagSettings = flagSettings;
				return this;
			}
		};
	}

	private String name;
	private FlagSettings flagSettings;

	@Override
	public int contentVersion() {
		return 0;
	}

	@Override
	public DataContainer toContainer() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public FlagSettings getSettings() {
		return flagSettings;
	}

}
