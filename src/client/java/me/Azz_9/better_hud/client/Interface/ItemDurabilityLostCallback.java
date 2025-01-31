package me.Azz_9.better_hud.client.Interface;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface ItemDurabilityLostCallback {
	Event<ItemDurabilityLostCallback> EVENT = EventFactory.createArrayBacked(ItemDurabilityLostCallback.class,
			(listeners) -> (entity, stack, amount) -> {
				for (ItemDurabilityLostCallback listener : listeners) {
					listener.onDurabilityLost(entity, stack, amount);
				}
			});

	void onDurabilityLost(PlayerEntity entity, ItemStack stack, int amount);
}