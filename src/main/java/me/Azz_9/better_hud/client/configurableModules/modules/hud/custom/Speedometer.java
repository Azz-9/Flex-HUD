package me.Azz_9.better_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.better_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.better_hud.client.utils.speedometer.SpeedUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

import java.util.LinkedList;
import java.util.List;

public class Speedometer extends AbstractTextElement {
	public ConfigInteger digits = new ConfigInteger(1, "better_hud.speedometer.config.number_of_digits", 0, 16);
	public ConfigEnum<SpeedometerUnits> units = new ConfigEnum<>(SpeedometerUnits.MPS, "better_hud.speedometer.config.selected_unit");
	public ConfigBoolean useKnotInBoat = new ConfigBoolean(false, "better_hud.speedometer.config.use_knot_when_in_boat");
	public static List<Long> times = new LinkedList<>();

	public Speedometer(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public void init() {
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
		this.enabled.setConfigTextTranslationKey("better_hud.speedometer.config.enable");
	}

	@Override
	public String getID() {
		return "speedometer";
	}

	@Override
	public Text getName() {
		return Text.translatable("better_hud.speedometer");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		long a = System.nanoTime();

		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender()) {
			return;
		}

		String formattedSpeed = SpeedUtils.getFormattedSpeed();

		setWidth(formattedSpeed);

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(this.scale, this.scale);

		drawBackground(context);

		context.drawText(client.textRenderer, formattedSpeed, 0, 0, getColor(), this.shadow.getValue());

		matrices.popMatrix();

		long b = System.nanoTime();
		times.add(b - a);
		if (times.size() > 1000) {
			times.removeFirst();
		}
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 250;
				} else {
					buttonWidth = 170;
				}

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
						new IntFieldEntry.Builder()
								.setIntFieldWidth(20)
								.setVariable(digits)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(useKnotInBoat)
								.build(),
						new CyclingButtonEntry.Builder<SpeedometerUnits>()
								.setCyclingButtonWidth(80)
								.setVariable(units)
								.setGetTooltip(
										value -> switch (value) {
											case KPH ->
													Tooltip.of(Text.translatable("better_hud.speedometer.config.tooltip.kph"));
											case MPH ->
													Tooltip.of(Text.translatable("better_hud.speedometer.config.tooltip.mph"));
											case MPS ->
													Tooltip.of(Text.translatable("better_hud.speedometer.config.tooltip.mps"));
											default -> null;
										}
								)
								.build()
				);
			}
		};
	}

	public enum SpeedometerUnits implements Translatable {
		MPS("better_hud.enum.speedometer.units.mps"),
		KPH("better_hud.enum.speedometer.units.kph"),
		MPH("better_hud.enum.speedometer.units.mph"),
		KNOT("better_hud.enum.speedometer.units.knots");

		private final String translationKey;

		SpeedometerUnits(String translationKey) {
			this.translationKey = translationKey;
		}

		@Override
		public String getTranslationKey() {
			return translationKey;
		}
	}
}
