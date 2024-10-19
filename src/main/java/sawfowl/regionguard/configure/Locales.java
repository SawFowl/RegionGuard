package sawfowl.regionguard.configure;

import java.util.Locale;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.ConfigurateException;

import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.api.LocaleService;
import sawfowl.localeapi.api.PluginLocale;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.configure.locales.AbstractLocale;
import sawfowl.regionguard.configure.locales.def.ImplementLocale;
import sawfowl.regionguard.configure.locales.ru.ImplementRuLocale;

public class Locales {

	private final LocaleService localeService;
	private String pluginid = "regionguard";
	public Locales(LocaleService localeService) {
		this.localeService = localeService;
		localeService.localesExist(pluginid);
		localeService.createPluginLocale(pluginid, ConfigTypes.YAML, org.spongepowered.api.util.locale.Locales.DEFAULT);
		localeService.createPluginLocale(pluginid, ConfigTypes.HOCON, org.spongepowered.api.util.locale.Locales.RU_RU);
		localeService.setDefaultReference(RegionGuard.getInstance().getPluginContainer(), ImplementLocale.class);
		generateDefault();
		generateRu();
	}

	public AbstractLocale getLocale(ServerPlayer player) {
		return getLocale(player.locale());
	}

	public AbstractLocale getLocale(Locale locale) {
		return getPluginLocale(locale).asReference(AbstractLocale.class);
	}

	public AbstractLocale getSystemLocale() {
		return getPluginLocale(localeService.getSystemOrDefaultLocale()).asReference(AbstractLocale.class);
	}

	public LocaleService getLocaleService() {
		return localeService;
	}

	private void generateDefault() {
		if(getPluginLocale(org.spongepowered.api.util.locale.Locales.DEFAULT).getLocaleRootNode().empty()) try {
			getPluginLocale(org.spongepowered.api.util.locale.Locales.DEFAULT).setLocaleReference(ImplementLocale.class);
			getPluginLocale(org.spongepowered.api.util.locale.Locales.DEFAULT).saveLocaleNode();
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
	}

	private void generateRu() {
		if(getPluginLocale(org.spongepowered.api.util.locale.Locales.RU_RU).getLocaleRootNode().empty()) try {
			getPluginLocale(org.spongepowered.api.util.locale.Locales.RU_RU).setLocaleReference(ImplementRuLocale.class);
			getPluginLocale(org.spongepowered.api.util.locale.Locales.RU_RU).saveLocaleNode();
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
	}
/*
	private PluginLocale getAbstractLocaleUtil(Locale locale) {
		return localeService.getPluginLocales(pluginid).getOrDefault(locale, localeService.getPluginLocales(pluginid).get(org.spongepowered.api.util.locale.Locales.DEFAULT));
	}

	private Component toText(String string) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
	}

	private boolean check(Locale locale, Component value, String comment, Object... path) {
		return getAbstractLocaleUtil(locale).checkComponent(json, value, comment, path);
	}

	private void save(Locale locale) {
		getAbstractLocaleUtil(locale).saveLocaleNode();
	}
*/
	private PluginLocale getPluginLocale(Locale locale) {
		return localeService.getPluginLocales(pluginid).getOrDefault(locale, localeService.getPluginLocales(pluginid).get(org.spongepowered.api.util.locale.Locales.DEFAULT));
	}

}
