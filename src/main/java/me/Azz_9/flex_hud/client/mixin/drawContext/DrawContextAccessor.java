package me.Azz_9.flex_hud.client.mixin.drawContext;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {

	@Invoker("drawItemBar")
	void flex_hud$drawItemBar(ItemStack stack, int x, int y);

	@Invoker("drawCooldownProgress")
	void flex_hud$drawCooldownProgress(ItemStack stack, int x, int y);
}