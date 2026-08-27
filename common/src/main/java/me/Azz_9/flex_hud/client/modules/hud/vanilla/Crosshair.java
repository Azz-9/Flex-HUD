package me.Azz_9.flex_hud.client.modules.hud.vanilla;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.BlendFactor;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.config.option.ConfigFloat;
import me.Azz_9.flex_hud.client.config.option.ConfigIntGrid;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CrosshairEditorEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.AbstractModule;
import me.Azz_9.flex_hud.client.modules.Modules;

public class Crosshair extends AbstractModule {

	private static RenderPipeline crosshairPipeline;

	public int size = 15;
	public final ConfigFloat scale = new ConfigFloat(1.0f);

	public final ConfigIntGrid pixels = new ConfigIntGrid(
			new int[][]{
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			}, "flex_hud.crosshair.config.custom_texture");

	public final ConfigBoolean disableBlending = new ConfigBoolean(false, "flex_hud.crosshair.config.disable_blending");

	public Crosshair() {
		super("crosshair");
		this.enabled.setConfigTextTranslationKey("flex_hud.crosshair.config.enable");

		ConfigRegistry.register(getID(), "scale", scale);
		ConfigRegistry.register(getID(), "pixels", pixels);
		ConfigRegistry.register(getID(), "disableBlending", disableBlending);
	}

	@Override
	public void init() {
		crosshairPipeline = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
				.withLocation("pipeline/crosshair_no_tex")
				.withColorTargetState(new ColorTargetState(new BlendFunction(BlendFactor.ONE_MINUS_DST_COLOR, BlendFactor.ONE_MINUS_SRC_COLOR, BlendFactor.ONE, BlendFactor.ZERO)))
				.build()
		);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.crosshair");
	}

	public void renderReplacement(GuiGraphicsExtractor graphics) {

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(graphics.guiWidth() / 2.0f, graphics.guiHeight() / 2.0f);
		matrices.scale(scale.getValue());
		matrices.translate(-size / 2.0f, -size / 2.0f);

		int[][] pixelValues = pixels.getValue();

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int color = pixelValues[y][x];

				// ignore transparent pixels
				if ((color >>> 24) == 0) {
					continue;
				}

				if (disableBlending.getValue()) {
					graphics.fill(x, y, x + 1, y + 1, color);
				} else {
					graphics.fill(crosshairPipeline, x, y, x + 1, y + 1, color);
				}
			}
		}

		matrices.popMatrix();
	}

	public boolean shouldReplaceVanillaCrosshair() {
		return Modules.getInstance().isEnabled.getValue() && this.isEnabled();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 165;
				} else {
					buttonWidth = 155;
				}

				super.initContent();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build()
				);
				this.addAllEntries(
						new CrosshairEditorEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(pixels)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(disableBlending)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
