package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.utils.speedometer.SpeedUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class Speedometer extends AbstractTextElement {
	public ConfigInteger digits = new ConfigInteger(1, "flex_hud.speedometer.config.number_of_digits", 0, 16);
	public ConfigEnum<SpeedometerUnits> units = new ConfigEnum<>(SpeedometerUnits.MPS, "flex_hud.speedometer.config.selected_unit");
	public ConfigBoolean useKnotInBoat = new ConfigBoolean(false, "flex_hud.speedometer.config.use_knot_when_in_boat");

	public Speedometer(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public void init() {
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
		this.enabled.setConfigTextTranslationKey("flex_hud.speedometer.config.enable");
	}

	@Override
	public String getID() {
		return "speedometer";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.speedometer");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender()) {
			return;
		}

		String formattedSpeed = SpeedUtils.getFormattedSpeed();

		setWidth(formattedSpeed);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawBackground(context);

		context.drawText(client.textRenderer, formattedSpeed, 0, 0, getColor(), this.shadow.getValue());

		matrices.pop();
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
													Tooltip.of(Text.translatable("flex_hud.speedometer.config.tooltip.kph"));
											case MPH ->
													Tooltip.of(Text.translatable("flex_hud.speedometer.config.tooltip.mph"));
											case MPS ->
													Tooltip.of(Text.translatable("flex_hud.speedometer.config.tooltip.mps"));
											default -> null;
										}
								)
								.build()
				);
			}
		};
	}

	public enum SpeedometerUnits implements Translatable {
		MPS("flex_hud.enum.speedometer.units.mps"),
		KPH("flex_hud.enum.speedometer.units.kph"),
		MPH("flex_hud.enum.speedometer.units.mph"),
		KNOT("flex_hud.enum.speedometer.units.knots");

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
