package me.Azz_9.flex_hud.client.gui.components.config.colorPicker;

import static me.Azz_9.flex_hud.Constants.MOD_ID;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record ColorPickerHueRenderState(
		Matrix3x2f pose,
		int x0, int y0,
		int x1, int y1,
		@Nullable ScreenRectangle scissorArea,
		@Nullable ScreenRectangle bounds
) implements GuiElementRenderState {

	public static final RenderPipeline COLOR_PICKER_HUE = RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation(ResourceLocation.fromNamespaceAndPath(MOD_ID, "color_picker_hue"))
			.withVertexShader(ResourceLocation.withDefaultNamespace("core/gui"))
			.withFragmentShader(ResourceLocation.fromNamespaceAndPath(MOD_ID, "core/color_picker_hue"))
			.withCull(false)
			.build();

	public ColorPickerHueRenderState(Matrix3x2f pose, int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea) {
		this(pose, x0, y0, x1, y1, scissorArea, getBounds(x0, y0, x1, y1, pose, scissorArea));
	}

	@Override
	public void buildVertices(VertexConsumer consumer, float z) {
		consumer.addVertexWith2DPose(this.pose, this.x0, this.y0, z)
				.setColor(ARGB.color(255, 0, 255, 255));

		consumer.addVertexWith2DPose(this.pose, this.x0, this.y1, z)
				.setColor(ARGB.color(255, 255, 255, 255));

		consumer.addVertexWith2DPose(this.pose, this.x1, this.y1, z)
				.setColor(ARGB.color(255, 255, 255, 255));

		consumer.addVertexWith2DPose(this.pose, this.x1, this.y0, z)
				.setColor(ARGB.color(255, 0, 255, 255));
	}

	@Override
	public @NotNull RenderPipeline pipeline() {
		return COLOR_PICKER_HUE;
	}

	@Override
	public @NotNull TextureSetup textureSetup() {
		return TextureSetup.noTexture();
	}

	@Override
	public @Nullable ScreenRectangle scissorArea() {
		return this.scissorArea;
	}

	private static @Nullable ScreenRectangle getBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
		ScreenRectangle bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);

		return scissorArea != null
				? scissorArea.intersection(bounds)
				: bounds;
	}
}