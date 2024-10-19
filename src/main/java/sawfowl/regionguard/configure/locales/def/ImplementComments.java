package sawfowl.regionguard.configure.locales.def;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.abstractlocale.Comments;
import sawfowl.regionguard.configure.locales.def.comments.ImplementMainConfig;

@ConfigSerializable
public class ImplementComments implements Comments {

	public ImplementComments() {}

	@Setting("MainConfig")
	private ImplementMainConfig mainConfig = new ImplementMainConfig();

	@Override
	public MainConfig getMainConfig() {
		return mainConfig;
	}

}
