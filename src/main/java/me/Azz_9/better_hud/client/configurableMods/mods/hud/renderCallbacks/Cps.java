package me.Azz_9.better_hud.client.configurableMods.mods.hud.renderCallbacks;

import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.utils.cps.CalculateCps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class Cps extends AbstractHudElement {
	public boolean showLeftClick = true;
	public boolean showRightClick = true;

	public Cps(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!JsonConfigHelper.getInstance().isEnabled || !this.enabled || !(this.showLeftClick || this.showRightClick) || CLIENT == null || CLIENT.options.hudHidden) {
			return;
		}

		String text = "";
		if (this.showLeftClick) {
			text = String.valueOf(CalculateCps.getLeftCps());
		}
		if (this.showLeftClick && this.showRightClick) {
			text += " | ";
		}
		if (this.showRightClick) {
			text += String.valueOf(CalculateCps.getRightCps());
		}

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.drawText(CLIENT.textRenderer, text, 0, 0, getColor(), this.shadow);

		setWidth(text);
		this.height = CLIENT.textRenderer.fontHeight;

		if (drawBackground) {
			drawContext.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor);
		}

		matrices.pop();
	}

	@Override
	public boolean isEnabled() {
		return this.enabled && (this.showLeftClick || this.showRightClick);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(Text.translatable("better_hud.cps"), parent, parentScrollAmount) {
			@Override
			protected void init() {
				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(JsonConfigHelper.getInstance().cps.enabled)
								.setOnToggle(toggled -> JsonConfigHelper.getInstance().cps.enabled = toggled)
								.setText(Text.translatable("better_hud.armor_status.config.enable"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(JsonConfigHelper.getInstance().cps.shadow)
								.setOnToggle(toggled -> JsonConfigHelper.getInstance().cps.shadow = toggled)
								.setText(Text.translatable("better_hud.global.config.text_shadow"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(JsonConfigHelper.getInstance().cps.chromaColor)
								.setOnToggle(toggled -> JsonConfigHelper.getInstance().cps.chromaColor = toggled)
								.setText(Text.translatable("better_hud.global.config.chroma_text_color"))
								.build()
				);
			}
		};
	}
}
