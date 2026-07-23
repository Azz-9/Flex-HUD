package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

	@Invoker("getFov")
	float invokeGetFov(Camera camera, float partialTick, boolean useFovSetting);
}
