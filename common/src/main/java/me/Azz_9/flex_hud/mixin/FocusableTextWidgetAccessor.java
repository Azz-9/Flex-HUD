package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.gui.components.FocusableTextWidget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FocusableTextWidget.class)
public interface FocusableTextWidgetAccessor {

	@Accessor("DEFAULT_PADDING")
	static int getDefaultPadding() {
		throw new AssertionError();
	}
}
