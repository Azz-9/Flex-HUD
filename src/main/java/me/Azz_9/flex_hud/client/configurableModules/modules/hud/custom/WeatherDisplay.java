package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractBackgroundElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class WeatherDisplay extends AbstractBackgroundElement {

	public WeatherDisplay(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.weather_display.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public void init() {
		this.height = 16;
		this.width = 16;
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
		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender()) {
			return;
		}

		if (client.world != null && client.world.getDimension().hasSkyLight() && !client.world.getDimension().hasCeiling() || Flex_hudClient.isInMoveElementScreen) {

			Matrix3x2fStack matrices = context.getMatrices();
			matrices.pushMatrix();
			matrices.translate(getRoundedX(), getRoundedY());
			matrices.scale(this.scale, this.scale);

			drawBackground(context);

			String path = getWeatherIconPath(client);

			context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(MOD_ID, path), 0, 0, 0, 0, 16, 16, 16, 16);

			matrices.popMatrix();
		}
	}

	private static @NotNull String getWeatherIconPath(MinecraftClient client) {
		String path;
		if (Flex_hudClient.isInMoveElementScreen || client.world == null) {
			path = "weather_icons/day_clear.png";
		} else {
			int timeOfDay = (int) (client.world.getTimeOfDay() % 24000L);
			if (timeOfDay >= 12600 && timeOfDay <= 23400) {
				path = "weather_icons/night_";
			} else {
				path = "weather_icons/day_";
			}
			if (client.world.isThundering()) {
				path += "thunder.png";
			} else if (client.world.isRaining()) {
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
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 160;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
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
								.build()
				);
			}
		};
	}
}
