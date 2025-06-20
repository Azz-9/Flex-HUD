package me.Azz_9.better_hud.client.configurableMods.mods.hud.renderCallbacks;

import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.StringFieldEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Clock extends AbstractHudElement {
	public String textFormat = "hh:mm:ss";
	public boolean isTwentyFourHourFormat = true;

	public Clock(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);

		MinecraftClient client = MinecraftClient.getInstance();

		if (!JsonConfigHelper.getInstance().isEnabled || !this.enabled || client == null || client.options.hudHidden) {
			return;
		}

		String currentTime = getCurrentTime();

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.drawText(client.textRenderer, currentTime, 0, 0, getColor(), this.shadow);

		setWidth(currentTime);
		this.height = client.textRenderer.fontHeight;

		if (drawBackground) {
			drawContext.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor);
		}

		matrices.pop();
	}

	public String getCurrentTime() {
		String textFormat = this.textFormat.toLowerCase();
		if (this.isTwentyFourHourFormat) {
			textFormat = textFormat.replace("hh", "HH").replace("h", "HH");
		} else {
			textFormat += " a";
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(textFormat);
		return LocalTime.now().format(formatter);
	}


	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(Text.translatable("better_hud.clock"), parent, parentScrollAmount) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 180;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(enabled)
								.setDefaultValue(true)
								.setOnToggle(toggled -> enabled = toggled)
								.setText(Text.translatable("better_hud.clock.config.enable"))
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
								.setOnColorChange(value -> color = value)
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
								.setOnColorChange(value -> backgroundColor = value)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.setText(Text.translatable("better_hud.global.config.background_color"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(isTwentyFourHourFormat)
								.setDefaultValue(true)
								.setOnToggle(toggled -> isTwentyFourHourFormat = toggled)
								.setText(Text.translatable("better_hud.clock.config.24-hour_format"))
								.build(),
						new StringFieldEntry.Builder()
								.setIntFieldWidth(80)
								.setValue(textFormat)
								.setDefaultValue("hh:mm:ss")
								.setOnValueChange((value) -> textFormat = value)
								.setIsValid(textFormat -> {
									try {
										textFormat = textFormat.toLowerCase();
										if (isTwentyFourHourFormat) {
											textFormat = textFormat.replace("hh", "HH").replace("h", "HH");
										} else {
											textFormat += " a";
										}
										DateTimeFormatter formatter = DateTimeFormatter.ofPattern(textFormat);
										LocalTime.now().format(formatter);
										return true;

									} catch (Exception e) {
										return false;
									}
								})
								.setText(Text.translatable("better_hud.clock.config.text_format"))
								.build()
				);
			}
		};
	}
}