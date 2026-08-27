package me.Azz_9.flex_hud.client.gui;

import static me.Azz_9.flex_hud.Constants.MOD_ID;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class FlexHudRenderTypes {

	public static final RenderType COLOR_PICKER_GRADIENT =
			RenderType.create(
					MOD_ID + "_color_picker_gradient",
					DefaultVertexFormat.POSITION_COLOR,
					VertexFormat.Mode.QUADS,
					256,
					RenderType.CompositeState.builder()
							.setShaderState(new RenderStateShard.ShaderStateShard(FlexHudShaders.COLOR_PICKER_GRADIENT))
							.setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
							.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
							.setCullState(RenderStateShard.NO_CULL)
							.setWriteMaskState(RenderStateShard.COLOR_WRITE)
							.createCompositeState(false)
			);

	public static final RenderType COLOR_PICKER_HUE = RenderType.create(
			MOD_ID + "_color_picker_hue",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			256,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(FlexHudShaders.COLOR_PICKER_HUE))
					.setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
					.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
					.setCullState(RenderStateShard.NO_CULL)
					.setWriteMaskState(RenderStateShard.COLOR_WRITE)
					.createCompositeState(false)
	);


	private FlexHudRenderTypes() {
	}
}
