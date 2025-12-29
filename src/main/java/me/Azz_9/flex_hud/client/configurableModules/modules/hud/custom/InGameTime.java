package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.StringFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigString;
import me.Azz_9.flex_hud.client.utils.clock.ClockUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class InGameTime extends AbstractTextElement implements TickableModule {

	public final ConfigString textFormat = new ConfigString("hh:mm", "flex_hud.clock.config.text_format");
	private final ConfigBoolean isTwentyFourHourFormat;

	private String formattedTime = "";

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
		setHeight(Minecraft.getInstance().font.lineHeight);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.in_game_time");
	}

	@Override
	public String getID() {
		return "in_game_time";
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender()) {
			return;
		}

		setWidth(formattedTime);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		graphics.drawString(Minecraft.getInstance().font, formattedTime, 0, 0, getColor(), shadow.getValue());

		matrices.popMatrix();
	}

	@Override
	public @Nullable Tooltip getTooltip() {
		if (ModulesHelper.getInstance().timeChanger.isEnabled()) {
			return Tooltip.create(Component.literal("⚠ ").append(Component.translatable("flex_hud.configuration_screen.module_compatibility_warning")).append(Component.translatable("flex_hud.time_changer")).withStyle(ChatFormatting.RED));
		} else {
			return null;
		}
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
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(color)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackground)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(isTwentyFourHourFormat)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new StringFieldEntry.Builder()
								.setStringFieldWidth(80)
								.setVariable(textFormat)
								.addDependency(this.getConfigList().getFirstEntry(), false)
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
								.setGetTooltip((value) -> Tooltip.create(Component.literal(
										"hh: " + Component.translatable("flex_hud.global.hours").getString() +
												"\nmm: " + Component.translatable("flex_hud.global.minutes").getString() +
												"\nss: " + Component.translatable("flex_hud.global.seconds").getString())))
								.setText(Component.translatable("flex_hud.clock.config.text_format"))
								.build()
				);
			}
		};
	}

	@Override
	public void tick() {
		if (Minecraft.getInstance().level == null && !Flex_hudClient.isInMoveElementScreen) return;

		int timeOfDay;
		if (Flex_hudClient.isInMoveElementScreen) {
			timeOfDay = 12000;
		} else {
			timeOfDay = (int) (Minecraft.getInstance().level.getDayTime() % 24000 + 6000) % 24000;
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