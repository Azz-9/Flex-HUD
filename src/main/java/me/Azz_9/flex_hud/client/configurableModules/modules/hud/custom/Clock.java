package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.StringFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigString;
import me.Azz_9.flex_hud.client.utils.clock.ClockUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Clock extends AbstractTextElement implements TickableModule {
	public final ConfigString textFormat = new ConfigString("hh:mm:ss", "flex_hud.clock.config.text_format");
	public final ConfigBoolean isTwentyFourHourFormat;

	@NotNull
	private static String formattedTime = "";

	public Clock(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.clock.config.enable");

		// get the time format depending on the locale
		isTwentyFourHourFormat = new ConfigBoolean(ClockUtils.is24HourFormat(Locale.getDefault()), "flex_hud.clock.config.24-hour_format");

		ConfigRegistry.register(getID(), "textFormat", textFormat);
		ConfigRegistry.register(getID(), "isTwentyFourHourFormat", isTwentyFourHourFormat);
	}

	@Override
	public void init() {
		setHeight(Minecraft.getInstance().font.lineHeight);
	}

	@Override
	public String getID() {
		return "clock";
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.clock");
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();

		if (shouldNotRender()) {
			return;
		}

		setWidth(formattedTime);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		graphics.drawString(minecraft.font, formattedTime, 0, 0, getColor(), this.shadow.getValue());

		matrices.popMatrix();
	}


	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (Minecraft.getInstance().getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 180;
				}

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
								.setGetTooltip((value) -> Tooltip.create(Component.literal("hh: " +
										Component.translatable("flex_hud.global.hours").getString() + "\nmm: " +
										Component.translatable("flex_hud.global.minutes").getString() + "\nss: " +
										Component.translatable("flex_hud.global.seconds").getString())))
								.setText(Component.translatable("flex_hud.clock.config.text_format"))
								.build()
				);
			}
		};
	}

	@Override
	public void tick() {
		String textFormat = this.textFormat.getValue().toLowerCase();
		if (this.isTwentyFourHourFormat.getValue()) {
			textFormat = textFormat.replace("hh", "HH").replace("h", "HH");
		} else {
			textFormat += " a";
		}
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(textFormat);
			formattedTime = LocalTime.now().format(formatter);
		} catch (Exception e) {
			// if the text format is not valid, reset to default
			this.textFormat.setToDefault();
		}
	}
}