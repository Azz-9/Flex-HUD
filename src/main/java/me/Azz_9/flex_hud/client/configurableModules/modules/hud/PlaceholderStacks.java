package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;

public final class PlaceholderStacks {
	private static final Map<Item, ItemStack> CACHE = new IdentityHashMap<>();

	private PlaceholderStacks() {
	}

	public static ItemStack of(Item item) {
		return CACHE.computeIfAbsent(item, ItemStack::new);
	}
}
