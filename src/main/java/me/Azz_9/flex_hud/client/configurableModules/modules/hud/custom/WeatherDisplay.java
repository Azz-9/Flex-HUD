package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractBackgroundModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class WeatherDisplay extends AbstractBackgroundModule {

	public WeatherDisplay(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.weather_display.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public void init() {
		setHeight(16);
		setWidth(16);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.weather_display");
	}

	@Override
	public String getID() {
		return "weather_display";
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();

		if (shouldNotRender()) {
			return;
		}

		if (minecraft.level != null && minecraft.level.dimensionType().hasSkyLight() && !minecraft.level.dimensionType().hasCeiling() || Flex_hudClient.isInMoveElementScreen) {

			Matrix3x2fStack matrices = graphics.pose();
			matrices.pushMatrix();
			matrices.translate(getRoundedX(), getRoundedY());
			matrices.scale(getScale());

			drawBackground(graphics);

			String path = getWeatherIconPath(minecraft);

			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MOD_ID, path), 0, 0, 0, 0, 16, 16, 16, 16);

			matrices.popMatrix();
		}
	}

	@Override
	public @Nullable Tooltip getTooltip() {
		if (ModulesHelper.getInstance().weatherChanger.enabled.getValue()) {
			return Tooltip.create(Component.literal("⚠ ").append(Component.translatable("flex_hud.configuration_screen.module_compatibility_warning")).append(Component.translatable("flex_hud.weather_changer")).withStyle(ChatFormatting.RED));
		} else {
			return null;
		}
	}

	private static @NotNull String getWeatherIconPath(@NotNull Minecraft client) {
		String path;
		if (Flex_hudClient.isInMoveElementScreen || client.level == null) {
			path = "weather_icons/day_clear.png";
		} else {
			int timeOfDay = (int) (client.level.getDayTime() % 24000L);
			if (timeOfDay >= 12600 && timeOfDay <= 23400) {
				path = "weather_icons/night_";
			} else {
				path = "weather_icons/day_";
			}
			if (client.level.isThundering()) {
				path += "thunder.png";
			} else if (client.level.isRaining()) {
				path += "rainy.png";
			} else {
				path += "clear.png";
			}
		}
		return path;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (Minecraft.getInstance().getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 160;
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
								.build()
				);
			}
		};
	}
}
