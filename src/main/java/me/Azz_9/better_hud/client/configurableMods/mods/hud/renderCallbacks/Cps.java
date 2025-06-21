package me.Azz_9.better_hud.client.configurableMods.mods.hud.renderCallbacks;

import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.better_hud.client.utils.cps.CalculateCps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

public class Cps extends AbstractHudElement {
	public boolean showLeftClick = true;
	public boolean showRightClick = true;

	public Cps(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		super.render(context, tickCounter);

		MinecraftClient client = MinecraftClient.getInstance();

		if (!JsonConfigHelper.getInstance().isEnabled || !this.enabled || !(this.showLeftClick || this.showRightClick) || client == null || client.options.hudHidden) {
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

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh));
		matrices.scale(this.scale, this.scale);

		context.drawText(client.textRenderer, text, 0, 0, getColor(), this.shadow);

		setWidth(text);
		this.height = client.textRenderer.fontHeight;

		matrices.popMatrix();
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
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 190;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(enabled)
								.setDefaultValue(true)
								.setOnToggle(toggled -> enabled = toggled)
								.setText(Text.translatable("better_hud.cps.config.enable"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(shadow)
								.setDefaultValue(true)
								.setOnToggle(toggled -> shadow = toggled)
								.setText(Text.translatable("better_hud.global.config.text_shadow"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(chromaColor)
								.setDefaultValue(false)
								.setOnToggle(toggled -> chromaColor = toggled)
								.setText(Text.translatable("better_hud.global.config.chroma_text_color"))
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setColor(color)
								.setDefaultColor(0xffffff)
								.setOnColorChange(newColor -> color = newColor)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.setText(Text.translatable("better_hud.global.config.text_color"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(drawBackground)
								.setDefaultValue(false)
								.setOnToggle(toggled -> drawBackground = toggled)
								.setText(Text.translatable("better_hud.global.config.show_background"))
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setColor(backgroundColor)
								.setDefaultColor(0x313131)
								.setOnColorChange(newColor -> backgroundColor = newColor)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.setText(Text.translatable("better_hud.global.config.background_color"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showLeftClick)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showLeftClick = toggled)
								.setText(Text.translatable("better_hud.cps.config.show_left_click"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showRightClick)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showRightClick = toggled)
								.setText(Text.translatable("better_hud.cps.config.show_right_click"))
								.build()
				);
			}
		};
	}
}
