package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.tickables.SpeedTickable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.boat.Boat;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

public class Speedometer extends AbstractTextModule implements TickableModule {
	public ConfigInteger digits = new ConfigInteger(1, "flex_hud.speedometer.config.number_of_digits", 0, 16);
	public ConfigEnum<SpeedometerUnits> units = new ConfigEnum<>(SpeedometerUnits.class, SpeedometerUnits.MPS, "flex_hud.speedometer.config.selected_unit");
	public ConfigBoolean useKnotInBoat = new ConfigBoolean(false, "flex_hud.speedometer.config.use_knot_when_in_boat");

	private String formattedSpeed = "";

	public Speedometer(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.speedometer.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);

		ConfigRegistry.register(getID(), "digits", digits);
		ConfigRegistry.register(getID(), "units", units);
		ConfigRegistry.register(getID(), "useKnotInBoat", useKnotInBoat);
	}

	@Override
	public void init() {
		setHeight(Minecraft.getInstance().font.lineHeight);
	}

	@Override
	public String getID() {
		return "speedometer";
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.speedometer");
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();

		if (shouldNotRender()) {
			return;
		}

		setWidth(formattedSpeed);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		graphics.drawString(minecraft.font, formattedSpeed, 0, 0, getColor(), this.shadow.getValue());

		matrices.popMatrix();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (Minecraft.getInstance().getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 250;
				} else {
					buttonWidth = 170;
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
						new IntFieldEntry.Builder()
								.setIntFieldWidth(20)
								.setVariable(digits)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(useKnotInBoat)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new CyclingButtonEntry.Builder<SpeedometerUnits>()
								.setCyclingButtonWidth(80)
								.setVariable(units)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.setGetTooltip(
										value -> switch (value) {
											case KPH ->
													Tooltip.create(Component.translatable("flex_hud.speedometer.config.tooltip.kph"));
											case MPH ->
													Tooltip.create(Component.translatable("flex_hud.speedometer.config.tooltip.mph"));
											case MPS ->
													Tooltip.create(Component.translatable("flex_hud.speedometer.config.tooltip.mps"));
											default -> null;
										}
								)
								.build()
				);
			}
		};
	}

	@Override
	public void tick() {
		LocalPlayer player = Minecraft.getInstance().player;

		String format = "%." + this.digits.getValue() + "f";
		String speed = String.format(format, Flex_hudClient.isInMoveElementScreen ? 0 : SpeedTickable.getSpeed());

		if (this.units.getValue() == Speedometer.SpeedometerUnits.KNOT || (this.useKnotInBoat.getValue() && player != null && player.getVehicle() instanceof Boat)) {
			formattedSpeed = speed + " " + Component.translatable(Speedometer.SpeedometerUnits.KNOT.getTranslationKey()).getString();
		} else {
			formattedSpeed = speed + " " + Component.translatable(this.units.getValue().getTranslationKey()).getString();
		}
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
