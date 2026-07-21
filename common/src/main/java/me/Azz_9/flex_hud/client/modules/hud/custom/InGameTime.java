package me.Azz_9.flex_hud.client.modules.hud.custom;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
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

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.config.option.ConfigString;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.StringFieldEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.TickableModule;
import me.Azz_9.flex_hud.client.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.utils.ClockUtils;

public class InGameTime extends AbstractTextModule implements TickableModule {

	public final ConfigString textFormat = new ConfigString("hh:mm", "flex_hud.clock.config.text_format");
	private final ConfigBoolean isTwentyFourHourFormat;

	private String formattedTime = "";

	public InGameTime(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("in_game_time", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.in_game_time.config.enable");

		// get the time format depending on the locale
		isTwentyFourHourFormat = new ConfigBoolean(ClockUtils.is24HourFormat(Locale.getDefault()), "flex_hud.in_game_time.config.24-hour_format");

		ConfigRegistry.register(getID(), "textFormat", textFormat);
		ConfigRegistry.register(getID(), "isTwentyFourHourFormat", isTwentyFourHourFormat);
	}

	@Override
	public void init() {
		setHeight(MINECRAFT.font.lineHeight);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.in_game_time");
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

		graphics.drawString(MINECRAFT.font, formattedTime, 0, 0, getColor(), shadow.getValue());

		matrices.popMatrix();
	}

	@Override
	public @Nullable Tooltip getTooltip() {
		if (Modules.getInstance().timeChanger.isEnabled()) {
			return Tooltip.create(Component.translatable("flex_hud.configuration_screen.module_compatibility_warning", Component.translatable("flex_hud.time_changer")).withStyle(ChatFormatting.RED));
		} else {
			return null;
		}
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
				super.initContent();

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
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeX)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeX(anchorModeX.getValue()))
								.build(),
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeY)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeY(anchorModeY.getValue()))
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
		if (MINECRAFT.level == null && !CommonClass.isEditingLayout) return;

		int timeOfDay;
		if (CommonClass.isEditingLayout) {
			timeOfDay = 12000;
		} else {
			timeOfDay = (int) (MINECRAFT.level.getDayTime() % 24000 + 6000) % 24000;
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