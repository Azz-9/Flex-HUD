package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hud;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.utils.cps.CpsUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

public class Cps extends AbstractTextElement {
	public ConfigBoolean showLeftClick = new ConfigBoolean(true, "flex_hud.cps.config.show_left_click");
	public ConfigBoolean showRightClick = new ConfigBoolean(true, "flex_hud.cps.config.show_right_click");
	public ConfigBoolean showSuffix = new ConfigBoolean(true, "flex_hud.cps.config.show_suffix");

	public Cps(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
	}

	@Override
	public void init() {
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
		this.enabled.setConfigTextTranslationKey("flex_hud.cps.config.enable");
	}

	@Override
	public String getID() {
		return "cps";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.cps");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender()) {
			return;
		}

		String text = "";
		if (Flex_hud.isInMoveElementScreen) {
			text = "0 | 0";
		} else {
			if (this.showLeftClick.getValue()) {
				text = String.valueOf(CpsUtils.getLeftCps());
			}
			if (this.showLeftClick.getValue() && this.showRightClick.getValue()) {
				text += " | ";
			}
			if (this.showRightClick.getValue()) {
				text += String.valueOf(CpsUtils.getRightCps());
			}
		}

		if (showSuffix.getValue()) {
			text += " CPS";
		}

		setWidth(text);

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(this.scale, this.scale);

		drawBackground(context);

		context.drawText(client.textRenderer, text, 0, 0, getColor(), this.shadow.getValue());

		matrices.popMatrix();
	}

	@Override
	protected boolean shouldNotRender() {
		return super.shouldNotRender() || (!(this.showLeftClick.getValue() || this.showRightClick.getValue()) && !Flex_hud.isInMoveElementScreen);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 190;
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
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showLeftClick)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showRightClick)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showSuffix)
								.build()
				);
			}
		};
	}
}
