package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

public class LightLevel extends AbstractTextElement {

	private ConfigBoolean colorDependsOnLightLevel = new ConfigBoolean(true, "flex_hud.light_level.color_depends_on_light_level");


	public LightLevel(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
		this.enabled.setConfigTextTranslationKey("flex_hud.light_level.config.enable");
	}

	@Override
	public void init() {
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.light_level");
	}

	@Override
	public String getID() {
		return "light_level";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender()) {
			return;
		}

		int lightLevel;
		if (Flex_hudClient.isInMoveElementScreen) {
			lightLevel = 7;
		} else {
			lightLevel = MinecraftClient.getInstance().world.getLightLevel(LightType.BLOCK, MinecraftClient.getInstance().player.getBlockPos());
		}

		int color;
		if (colorDependsOnLightLevel.getValue()) {
			if (lightLevel <= 0) {
				color = 0xffff0000;
			} else if (lightLevel <= 7) {
				color = 0xffff7f00;
			} else if (lightLevel <= 11) {
				color = 0xffffff00;
			} else {
				color = 0xffffffff;
			}
		} else {
			color = getColor();
		}

		Text text = Text.translatable("flex_hud.light_level").append(": ").append(String.valueOf(lightLevel));

		setWidth(text.getString());

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(context);

		context.drawText(MinecraftClient.getInstance().textRenderer, text, 0, 0, color, shadow.getValue());

		matrices.popMatrix();
	}

	@Override
	protected boolean shouldNotRender() {
		return super.shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && (
				MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().player == null
		);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 260;
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
								.setVariable(colorDependsOnLightLevel)
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
								.build()
				);
			}
		};
	}
}
