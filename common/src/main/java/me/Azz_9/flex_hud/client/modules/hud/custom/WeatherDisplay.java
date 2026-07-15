package me.Azz_9.flex_hud.client.modules.hud.custom;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.Constants.MOD_ID;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.hud.AbstractBackgroundModule;

public class WeatherDisplay extends AbstractBackgroundModule {

	private static final int SPRITE_SIZE = 16;
	private static final Identifier DAY_CLEAR = Identifier.fromNamespaceAndPath(MOD_ID, "hud/weather_icons/day_clear");
	private static final Identifier DAY_RAINY = Identifier.fromNamespaceAndPath(MOD_ID, "hud/weather_icons/day_rainy");
	private static final Identifier DAY_THUNDER = Identifier.fromNamespaceAndPath(MOD_ID, "hud/weather_icons/day_thunder");
	private static final Identifier NIGHT_CLEAR = Identifier.fromNamespaceAndPath(MOD_ID, "hud/weather_icons/night_clear");
	private static final Identifier NIGHT_RAINY = Identifier.fromNamespaceAndPath(MOD_ID, "hud/weather_icons/night_rainy");
	private static final Identifier NIGHT_THUNDER = Identifier.fromNamespaceAndPath(MOD_ID, "hud/weather_icons/night_thunder");
	private static final Identifier DEFAULT = DAY_CLEAR;

	public WeatherDisplay(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("weather_display", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.weather_display.config.enable");
	}

	@Override
	public void init() {
		setHeight(SPRITE_SIZE);
		setWidth(SPRITE_SIZE);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.weather_display");
	}

	@Override
	public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender()) {
			return;
		}

		if (MINECRAFT.level != null && MINECRAFT.level.dimensionType().hasSkyLight() && !MINECRAFT.level.dimensionType().hasCeiling() || CommonClass.isEditingLayout) {

			Matrix3x2fStack matrices = graphics.pose();
			matrices.pushMatrix();
			matrices.translate(getRoundedX(), getRoundedY());
			matrices.scale(getScale());

			drawBackground(graphics);

			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, getWeatherSprite(), 0, 0, SPRITE_SIZE, SPRITE_SIZE);

			matrices.popMatrix();
		}
	}

	@Override
	public @Nullable Tooltip getTooltip() {
		if (Modules.getInstance().weatherChanger.enabled.getValue()) {
			return Tooltip.create(Component.translatable("flex_hud.configuration_screen.module_compatibility_warning", Component.translatable("flex_hud.weather_changer")).withStyle(ChatFormatting.RED));
		} else {
			return null;
		}
	}

	private static @NotNull Identifier getWeatherSprite() {
		if (CommonClass.isEditingLayout || MINECRAFT.level == null) {
			return DEFAULT;
		}

		int timeOfDay = (int) (MINECRAFT.level.getOverworldClockTime() % 24000L);
		if (timeOfDay >= 12600 && timeOfDay <= 23400) {
			if (MINECRAFT.level.isThundering()) {
				return DAY_THUNDER;
			} else if (MINECRAFT.level.isRaining()) {
				return DAY_RAINY;
			} else {
				return DAY_CLEAR;
			}
		} else {
			if (MINECRAFT.level.isThundering()) {
				return NIGHT_THUNDER;
			} else if (MINECRAFT.level.isRaining()) {
				return NIGHT_RAINY;
			} else {
				return NIGHT_CLEAR;
			}
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
								.build()
				);
			}
		};
	}
}
