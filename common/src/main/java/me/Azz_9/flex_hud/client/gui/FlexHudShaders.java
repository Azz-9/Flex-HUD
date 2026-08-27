package me.Azz_9.flex_hud.client.gui;

import static me.Azz_9.flex_hud.Constants.MOD_ID;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.resources.ResourceLocation;

public class FlexHudShaders {

	private static final RenderPipeline.Snippet COLOR_PICKER_SNIPPET = RenderPipeline.builder()
			.withVertexShader(ResourceLocation.fromNamespaceAndPath(MOD_ID, "core/color_picker"))
			.withCull(false)
			.withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(
					DefaultVertexFormat.POSITION_COLOR,
					VertexFormat.Mode.QUADS
			)
			.buildSnippet();

	public static final RenderPipeline COLOR_PICKER_GRADIENT = RenderPipeline.builder(COLOR_PICKER_SNIPPET)
			.withLocation(ResourceLocation.fromNamespaceAndPath(MOD_ID, "color_picker_gradient"))
			.withFragmentShader(ResourceLocation.fromNamespaceAndPath(MOD_ID, "core/color_picker_gradient"))
			.build();

	public static final RenderPipeline COLOR_PICKER_HUE = RenderPipeline.builder(COLOR_PICKER_SNIPPET)
			.withLocation(ResourceLocation.fromNamespaceAndPath(MOD_ID, "color_picker_hue"))
			.withFragmentShader(ResourceLocation.fromNamespaceAndPath(MOD_ID, "core/color_picker_hue"))
			.build();

	private FlexHudShaders() {
	}
}
