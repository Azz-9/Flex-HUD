package me.Azz_9.flex_hud.client.mixin.drawContext;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphicsExtractor.class)
public interface GuiGraphicsExtractorAccessor {

	@Invoker("itemBar")
	void flex_hud$renderItemBar(ItemStack stack, int x, int y);

	@Invoker("itemCooldown")
	void flex_hud$renderItemCooldown(ItemStack stack, int x, int y);
}