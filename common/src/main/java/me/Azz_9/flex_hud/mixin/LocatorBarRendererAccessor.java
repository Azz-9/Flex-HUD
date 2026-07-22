package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LocatorBarRenderer.class)
public interface LocatorBarRendererAccessor {

	@Accessor("LOCATOR_BAR_ARROW_UP")
	static ResourceLocation getArrowUpIdentifier() {
		throw new AssertionError();
	}

	@Accessor("LOCATOR_BAR_ARROW_DOWN")
	static ResourceLocation getArrowDownIdentifier() {
		throw new AssertionError();
	}
}
