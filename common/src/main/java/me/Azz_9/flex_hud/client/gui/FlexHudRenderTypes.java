package me.Azz_9.flex_hud.client.gui;

import static me.Azz_9.flex_hud.Constants.MOD_ID;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class FlexHudRenderTypes {

	public static final RenderType COLOR_PICKER_GRADIENT =
			RenderType.create(
					MOD_ID + "_color_picker_gradient",
					DefaultVertexFormat.POSITION_COLOR.getVertexSize() * 4,
					FlexHudShaders.COLOR_PICKER_GRADIENT,
					RenderType.CompositeState.builder()
							.setTextureState(RenderStateShard.NO_TEXTURE)
							.setLightmapState(RenderStateShard.NO_LIGHTMAP)
							.setOverlayState(RenderStateShard.NO_OVERLAY)
							.setLayeringState(RenderStateShard.NO_LAYERING)
							.setOutputState(RenderStateShard.MAIN_TARGET)
							.setTexturingState(RenderStateShard.DEFAULT_TEXTURING)
							.setLineState(RenderStateShard.DEFAULT_LINE)
							.createCompositeState(false)
			);

	public static final RenderType COLOR_PICKER_HUE = RenderType.create(
			MOD_ID + "_color_picker_hue",
			DefaultVertexFormat.POSITION_COLOR.getVertexSize() * 4,
			FlexHudShaders.COLOR_PICKER_HUE,
			RenderType.CompositeState.builder()
					.setTextureState(RenderStateShard.NO_TEXTURE)
					.setLightmapState(RenderStateShard.NO_LIGHTMAP)
					.setOverlayState(RenderStateShard.NO_OVERLAY)
					.setLayeringState(RenderStateShard.NO_LAYERING)
					.setOutputState(RenderStateShard.MAIN_TARGET)
					.setTexturingState(RenderStateShard.DEFAULT_TEXTURING)
					.setLineState(RenderStateShard.DEFAULT_LINE)
					.createCompositeState(false)
	);


	private FlexHudRenderTypes() {
	}
}
