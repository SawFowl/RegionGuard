package sawfowl.regionguard.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Tristate;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.LocalesPaths;

public class DeathListener {

	private final RegionGuard plugin;
	//private Cause cause;
	Map<UUID, Map<Integer, ItemStack>> inventories = new HashMap<UUID, Map<Integer, ItemStack>>();
	Map<UUID, Integer> exps = new HashMap<UUID, Integer>();
	public DeathListener(RegionGuard plugin) {
		this.plugin = plugin;
		//cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
	}

	@Listener(order = Order.LAST)
	public void onDeath(DestructEntityEvent.Death event) {
		if(event.keepInventory() || !(event.entity() instanceof ServerPlayer)) return;
		ServerPlayer player = (ServerPlayer) event.entity();
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		boolean keepInventory = isKeepInventory(region);
		boolean keepExp = isKeepExp(region);
		if(keepInventory && !event.keepInventory()) {
			Map<Integer, ItemStack> map = new HashMap<Integer, ItemStack>();
			player.inventory().slots().forEach(slot -> {
				if(slot.totalQuantity() > 0 && slot.get(Keys.SLOT_INDEX).isPresent()) {
					map.put(slot.get(Keys.SLOT_INDEX).get(), slot.peek());
				}
				slot.clear();
			});
			if(!map.isEmpty()) {
				inventories.put(player.uniqueId(), map);
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.KEEP_INVENTORY));
			}
		}
		if(keepExp && !event.keepInventory() && player.get(Keys.EXPERIENCE).isPresent() && player.get(Keys.EXPERIENCE).get() > 0) {
			exps.put(player.uniqueId(), player.get(Keys.EXPERIENCE).get());
			player.offer(Keys.EXPERIENCE, 0);
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.KEEP_EXP));
		}
	}

	@Listener
	public void onRespawn(RespawnPlayerEvent.Post event) {
		ServerPlayer player = event.entity();
		if(inventories.containsKey(player.uniqueId()) && !inventories.get(player.uniqueId()).isEmpty())  {
			Map<Integer, ItemStack> items = inventories.get(player.uniqueId()); 
			for(Entry<Integer, ItemStack> entry : items.entrySet()) player.inventory().offer(entry.getKey(), entry.getValue());
			inventories.remove(player.uniqueId());
		}
		if(exps.containsKey(player.uniqueId())) {
			player.offer(Keys.EXPERIENCE, exps.get(player.uniqueId()));
			exps.remove(player.uniqueId());
		}
	}

	private boolean isKeepInventory(Region region) {
		Tristate flagResult = region.getFlagResult(Flags.KEEP_INVENTORY, null, null);
		if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		return region.isGlobal() ? true : isKeepInventory(plugin.getAPI().getGlobalRegion(region.getWorldKey()));
	}

	private boolean isKeepExp(Region region) {
		Tristate flagResult = region.getFlagResult(Flags.KEEP_EXP, null, null);
		if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		return region.isGlobal() ? true : isKeepInventory(plugin.getAPI().getGlobalRegion(region.getWorldKey()));
	}

}
