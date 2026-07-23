package net.minecraft.client.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;

/**
 * Version-specific bridge for the package-private RenderType factory in 1.21.5.
 */
public final class FlexHudRenderTypes {
	private FlexHudRenderTypes() {
	}

	public static RenderType create(String name, RenderPipeline pipeline) {
		return RenderType.create(
				name,
				RenderType.SMALL_BUFFER_SIZE,
				pipeline,
				RenderType.CompositeState.builder().createCompositeState(false)
		);
	}
}
