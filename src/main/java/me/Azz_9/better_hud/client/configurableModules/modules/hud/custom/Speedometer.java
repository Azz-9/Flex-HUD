package me.Azz_9.better_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.better_hud.client.utils.speedometer.SpeedUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

import java.util.LinkedList;
import java.util.List;

public class Speedometer extends AbstractHudElement {
	private int digits = 1;
	public SpeedometerUnits units = SpeedometerUnits.MPS;
	public boolean useKnotInBoat = false;
	public static List<Long> times = new LinkedList<>();

	public Speedometer(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled = false;
	}

	@Override
	public void init() {
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
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

		if (!JsonConfigHelper.getInstance().isEnabled || !this.enabled || client == null || (this.hideInF3 && client.getDebugHud().shouldShowDebugHud()) || client.player == null) {
			return;
		}

		String formattedSpeed = getString(client.player);

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(Math.round(getX()), Math.round(getY()));
		matrices.scale(this.scale, this.scale);

		// render background using calculated width and height from the previous render
		if (drawBackground) {
			context.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor);
		}

		context.drawText(client.textRenderer, formattedSpeed, 0, 0, getColor(), this.shadow);

		setWidth(formattedSpeed);

		matrices.popMatrix();

		long b = System.nanoTime();
		times.add(b - a);
		if (times.size() > 1000) {
			times.removeFirst();
		}
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(getName(), parent, parentScrollAmount) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 190;
				} else {
					buttonWidth = 170;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(enabled)
								.setDefaultValue(false)
								.setOnToggle((toggled) -> enabled = toggled)
								.setText(Text.translatable("better_hud.speedometer.config.enable"))
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
								.setToggled(hideInF3)
								.setDefaultValue(true)
								.setOnToggle(toggled -> hideInF3 = toggled)
								.setText(Text.translatable("better_hud.global.config.hide_in_f3"))
								.build(),
						new IntFieldEntry.Builder()
								.setIntFieldWidth(20)
								.setValue(digits)
								.setMin(0)
								.setMax(16)
								.setDefaultValue(1)
								.setOnValueChange(value -> digits = value)
								.setText(Text.translatable("better_hud.speedometer.config.number_of_digits"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(useKnotInBoat)
								.setDefaultValue(false)
								.setOnToggle(toggled -> useKnotInBoat = toggled)
								.setText(Text.translatable("better_hud.speedometer.config.use_knot_when_in_boat"))
								.build(),
						new CyclingButtonEntry.Builder<SpeedometerUnits>()
								.setCyclingButtonWidth(80)
								.setValue(units)
								.setDefaultValue(SpeedometerUnits.MPS)
								.setOnValueChange(value -> units = value)
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
								.setText(Text.translatable("better_hud.speedometer.config.selected_unit"))
								.build()
				);
			}
		};
	}

	private String getString(PlayerEntity player) {
		String format = "%." + this.digits + "f";
		String formattedSpeed = String.format(format, SpeedUtils.getSpeed());

		if (this.units == SpeedometerUnits.KNOT || (this.useKnotInBoat && player.getVehicle() instanceof BoatEntity)) {
			formattedSpeed += " " + Text.translatable(SpeedometerUnits.KNOT.getTranslationKey()).getString();
		} else {
			formattedSpeed += " " + Text.translatable(this.units.getTranslationKey()).getString();
		}
		return formattedSpeed;
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
