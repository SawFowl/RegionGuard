package sawfowl.regionguard.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import net.kyori.adventure.text.TextReplacementConfig;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.configure.LocalesPaths;

public class Economy {

	private final RegionGuard plugin;
	public Economy(RegionGuard plugin) {
		this.plugin = plugin;
	}

	public BigDecimal getPlayerBalance(UUID uuid, Currency currency) {
        try {
            Optional<UniqueAccount> uOpt = plugin.getEconomyService().findOrCreateAccount(uuid);
            if (uOpt.isPresent()) {
                return uOpt.get().balance(currency);
            }
        } catch (Exception ignored) {
        }
        return BigDecimal.ZERO;
	}

	public boolean checkPlayerBalance(UUID uuid, Currency currency, BigDecimal money) {
		return getPlayerBalance(uuid, currency).doubleValue() >= money.doubleValue();
	}

	public boolean addToPlayerBalance(ServerPlayer player, Currency currency, BigDecimal money) {
        try {
            Optional<UniqueAccount> uOpt = plugin.getEconomyService().findOrCreateAccount(player.uniqueId());
            if (uOpt.isPresent()) {
                TransactionResult result = uOpt.get().deposit(currency, money);
                if (result.result() == ResultType.SUCCESS) {
                	return true;
                } else if ((result.result() == ResultType.FAILED || result.result() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getConfig().isDebugEconomy()) {
                	plugin.getLogger().error(plugin.getLocales().getText(player.locale(), LocalesPaths.ECONOMY_ERROR_GIVE_MONEY)
                				.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build()));
                } else {
                }
            	}
        	} catch (Exception ignored) {
        		ignored.printStackTrace();
        }
		return false;
	}

	public boolean removeFromPlayerBalance(ServerPlayer player, Currency currency, BigDecimal money) {
        try {
            Optional<UniqueAccount> uOpt = plugin.getEconomyService().findOrCreateAccount(player.uniqueId());
            if (uOpt.isPresent()) {
                TransactionResult result = uOpt.get().withdraw(currency, money);
                if (result.result() == ResultType.SUCCESS) {
                	return true;
                } else if ((result.result() == ResultType.FAILED || result.result() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getConfig().isDebugEconomy()) {
                	plugin.getLogger().error(plugin.getLocales().getText(player.locale(), LocalesPaths.ECONOMY_ERROR_TAKE_MONEY)
            				.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build()));
                } else {
                }
            	}
        	} catch (Exception ignored) {
        		ignored.printStackTrace();
        }
		return false;
	}

	public Currency checkCurrency(String check) {
		if(check == null) return plugin.getEconomyService().defaultCurrency();
		Optional<Currency> optCurrency = getCurrencies().stream().filter(currency -> (TextUtils.clearDecorations(currency.displayName()).equalsIgnoreCase(check) || TextUtils.clearDecorations(currency.symbol()).equalsIgnoreCase(check))).findFirst();
		return optCurrency.isPresent() ? optCurrency.get() : plugin.getEconomyService().defaultCurrency();
	}

	public List<Currency> getCurrencies() {
		List<Currency> currencies = new ArrayList<Currency>();
		Sponge.game().findRegistry(RegistryTypes.CURRENCY).ifPresent(registry -> {
			if(registry.stream().count() > 0) currencies.addAll(registry.stream().collect(Collectors.toList()));
		});
		return !currencies.isEmpty() ? currencies : Arrays.asList(plugin.getEconomyService().defaultCurrency());
	}

}
