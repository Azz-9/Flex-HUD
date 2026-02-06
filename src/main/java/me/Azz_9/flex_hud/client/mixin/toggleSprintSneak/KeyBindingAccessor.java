package me.Azz_9.flex_hud.client.mixin.toggleSprintSneak;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyBindingAccessor {

	@Accessor("boundKey")
	InputConstants.Key getBoundKey();
}