package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.resources.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PackSelectionScreen.class)
public interface PackSelectionScreenAccessor {

	@Accessor("DEFAULT_ICON")
	static Identifier getDefaultIcon() {
		throw new AssertionError();
	}
}
