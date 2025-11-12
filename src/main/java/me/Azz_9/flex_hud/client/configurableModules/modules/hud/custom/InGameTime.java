package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.Tickable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.StringFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigString;
import me.Azz_9.flex_hud.client.utils.clock.ClockUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class InGameTime extends AbstractTextElement implements Tickable {

	public ConfigString textFormat = new ConfigString("hh:mm", "flex_hud.clock.config.text_format");
	private ConfigBoolean isTwentyFourHourFormat;

	private String formattedTime;

	public InGameTime(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
		this.enabled.setConfigTextTranslationKey("flex_hud.in_game_time.config.enable");

		// get the time format depending on the locale
		isTwentyFourHourFormat = new ConfigBoolean(ClockUtils.is24HourFormat(Locale.getDefault()), "flex_hud.in_game_time.config.24-hour_format");

		ConfigRegistry.register(getID(), "textFormat", textFormat);
		ConfigRegistry.register(getID(), "isTwentyFourHourFormat", isTwentyFourHourFormat);
	}

	@Override
	public void init() {
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.in_game_time");
	}

	@Override
	public String getID() {
		return "in_game_time";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender()) {
			return;
		}

		setWidth(formattedTime);

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(context);

		context.drawText(MinecraftClient.getInstance().textRenderer, formattedTime, 0, 0, getColor(), shadow.getValue());

		matrices.popMatrix();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColor)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(color)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackground)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColor)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(isTwentyFourHourFormat)
								.build(),
						new StringFieldEntry.Builder()
								.setStringFieldWidth(80)
								.setVariable(textFormat)
								.setIsValid(textFormat -> {
									if (textFormat.isBlank()) return false;
									try {
										textFormat = textFormat.toLowerCase();
										if (isTwentyFourHourFormat.getValue()) {
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
								.setGetTooltip((value) -> Tooltip.of(Text.of("hh: " + Text.translatable("flex_hud.global.hours").getString() + "\nmm: " + Text.translatable("flex_hud.global.minutes").getString() + "\nss: " + Text.translatable("flex_hud.global.seconds").getString())))
								.setText(Text.translatable("flex_hud.clock.config.text_format"))
								.build()
				);
			}
		};
	}

	@Override
	public void tick() {
		if (MinecraftClient.getInstance().world == null && !Flex_hudClient.isInMoveElementScreen) return;

		int timeOfDay;
		if (Flex_hudClient.isInMoveElementScreen) {
			timeOfDay = 12000;
		} else {
			timeOfDay = (int) (MinecraftClient.getInstance().world.getTimeOfDay() % 24000 + 6000) % 24000;
		}

		int totalSeconds = (int) Math.round(timeOfDay * 3.6);
		int hours = (totalSeconds / 3600) % 24;
		int minutes = (totalSeconds / 60) % 60;
		int seconds = totalSeconds % 60;

		String textFormat = this.textFormat.getValue().toLowerCase();
		if (this.isTwentyFourHourFormat.getValue()) {
			textFormat = textFormat.replace("hh", "HH").replace("h", "HH");
		} else {
			textFormat += " a";
		}
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(textFormat);
			formattedTime = LocalTime.of(hours, minutes, seconds).format(formatter);
		} catch (Exception e) {
			// if the text format is not valid, reset to default
			this.textFormat.setToDefault();
		}
	}
}