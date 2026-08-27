package me.Azz_9.flex_hud.client.gui;

import static me.Azz_9.flex_hud.Constants.MOD_ID;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.resources.ResourceLocation;

public class FlexHudShaders {

	public static final ShaderProgram COLOR_PICKER_GRADIENT = new ShaderProgram(
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "core/color_picker_gradient"),
			DefaultVertexFormat.POSITION_COLOR,
			ShaderDefines.EMPTY
	);

	public static final ShaderProgram COLOR_PICKER_HUE = new ShaderProgram(
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "core/color_picker_hue"),
			DefaultVertexFormat.POSITION_COLOR,
			ShaderDefines.EMPTY
	);

	private FlexHudShaders() {
	}
}
