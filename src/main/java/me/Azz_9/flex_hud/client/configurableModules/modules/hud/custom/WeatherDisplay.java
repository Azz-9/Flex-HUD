package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractBackgroundModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;
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
	public Text getName() {
		return Text.translatable("flex_hud.weather_display");
	}

	@Override
	public String getID() {
		return "weather_display";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender()) {
			return;
		}

		if (CLIENT.world != null && CLIENT.world.getDimension().hasSkyLight() && !CLIENT.world.getDimension().hasCeiling() || Flex_hudClient.isInMoveElementScreen) {

			MatrixStack matrices = context.getMatrices();
			matrices.push();
			matrices.translate(getRoundedX(), getRoundedY(), 0);
			matrices.scale(getScale(), getScale(), 1.0f);

			drawBackground(context);

			String path = getWeatherIconPath();

			context.drawTexture(RenderLayer::getGuiTextured, Identifier.of(MOD_ID, path), 0, 0, 0, 0, 16, 16, 16, 16);

			matrices.pop();
		}
	}

	@Override
	public @Nullable Tooltip getTooltip() {
		if (ModulesHelper.getInstance().weatherChanger.enabled.getValue()) {
			return Tooltip.of(Text.literal("⚠ ").append(Text.translatable("flex_hud.configuration_screen.module_compatibility_warning")).append(Text.translatable("flex_hud.weather_changer")).formatted(Formatting.RED));
		} else {
			return null;
		}
	}

	private static @NotNull String getWeatherIconPath() {
		String path;
		if (Flex_hudClient.isInMoveElementScreen || CLIENT.world == null) {
			path = "weather_icons/day_clear.png";
		} else {
			int timeOfDay = (int) (CLIENT.world.getTimeOfDay() % 24000L);
			if (timeOfDay >= 12600 && timeOfDay <= 23400) {
				path = "weather_icons/night_";
			} else {
				path = "weather_icons/day_";
			}
			if (CLIENT.world.isThundering()) {
				path += "thunder.png";
			} else if (CLIENT.world.isRaining()) {
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
				if (CLIENT.getLanguageManager().getLanguage().equals("fr_fr")) {
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
