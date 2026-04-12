package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.utils.cps.CpsUtils;

public class Cps extends AbstractTextModule {
	public final ConfigBoolean showLeftClick = new ConfigBoolean(true, "flex_hud.cps.config.show_left_click");
	public final ConfigBoolean showRightClick = new ConfigBoolean(true, "flex_hud.cps.config.show_right_click");
	public final ConfigBoolean showSuffix = new ConfigBoolean(true, "flex_hud.cps.config.show_suffix");

	public Cps(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.cps.config.enable");

		ConfigRegistry.register(getID(), "showLeftClick", showLeftClick);
		ConfigRegistry.register(getID(), "showRightClick", showRightClick);
		ConfigRegistry.register(getID(), "showSuffix", showSuffix);
	}

	@Override
	public void init() {
		setHeight(CLIENT.textRenderer.fontHeight);
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
		if (shouldNotRender()) {
			return;
		}

		String text = "";
		if (Flex_hudClient.isInMoveElementScreen) {
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

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1.0f);

		drawBackground(context);

		context.drawText(CLIENT.textRenderer, text, 0, 0, getColor(), this.shadow.getValue());

		matrices.pop();
	}

	@Override
	public boolean shouldNotRender() {
		return super.shouldNotRender() || (!(this.showLeftClick.getValue() || this.showRightClick.getValue()) && !Flex_hudClient.isInMoveElementScreen);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (CLIENT.getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 190;
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
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showLeftClick)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showRightClick)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showSuffix)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
