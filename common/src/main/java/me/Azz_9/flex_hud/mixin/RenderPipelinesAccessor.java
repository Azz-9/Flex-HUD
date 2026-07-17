package me.Azz_9.flex_hud.mixin;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;

import net.minecraft.client.renderer.RenderPipelines;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderPipelines.class)
public interface RenderPipelinesAccessor {

	@Invoker("register")
	static RenderPipeline invokeRegister(RenderPipeline pipeline) {
		throw new AssertionError();
	}

	@Accessor("GUI_SNIPPET")
	static RenderPipeline.Snippet getGuiSnippet() {
		throw new AssertionError();
	}
}
