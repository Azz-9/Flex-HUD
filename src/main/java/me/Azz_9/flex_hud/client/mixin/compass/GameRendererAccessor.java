package me.Azz_9.flex_hud.client.mixin.compass;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

	@Invoker("getFov")
	float flex_hud$getFov(Camera camera, float tickProgress, boolean changingFov);
}
