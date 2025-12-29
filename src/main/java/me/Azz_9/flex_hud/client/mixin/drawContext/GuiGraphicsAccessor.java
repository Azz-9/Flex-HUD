package me.Azz_9.flex_hud.client.mixin.drawContext;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {

	@Invoker("renderItemBar")
	void flex_hud$renderItemBar(ItemStack stack, int x, int y);

	@Invoker("renderItemCooldown")
	void flex_hud$renderItemCooldown(ItemStack stack, int x, int y);
}