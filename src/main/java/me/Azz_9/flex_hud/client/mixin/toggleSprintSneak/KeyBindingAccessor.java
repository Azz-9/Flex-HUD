package me.Azz_9.flex_hud.client.mixin.toggleSprintSneak;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {

	@Accessor("boundKey")
	InputUtil.Key getBoundKey();
}