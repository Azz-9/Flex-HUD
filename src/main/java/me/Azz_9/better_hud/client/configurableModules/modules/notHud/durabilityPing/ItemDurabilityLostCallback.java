package me.Azz_9.better_hud.client.configurableModules.modules.notHud.durabilityPing;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface ItemDurabilityLostCallback {
	Event<ItemDurabilityLostCallback> EVENT = EventFactory.createArrayBacked(ItemDurabilityLostCallback.class,
			(listeners) -> (stack, amount) -> {
				for (ItemDurabilityLostCallback listener : listeners) {
					listener.onDurabilityLost(stack, amount);
				}
			});

	void onDurabilityLost(ItemStack stack, int amount);
}