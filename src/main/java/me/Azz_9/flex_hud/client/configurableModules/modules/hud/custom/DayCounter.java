package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

public class DayCounter extends AbstractTextElement {

	public DayCounter(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.day_counter.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public void init() {
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
	}

	@Override
	public String getID() {
		return "day_counter";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.day_counter");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && client.world == null) {
			return;
		}

		long day;
		if (Flex_hudClient.isInMoveElementScreen) {
			day = 5;
		} else {
			day = client.world.getTimeOfDay() / 24000;
		}
		Text text = Text.translatable("flex_hud.day_counter.hud.prefix").append(" " + (int) day);

		setWidth(text.getString());

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(this.scale, this.scale);

		drawBackground(context);

		context.drawText(client.textRenderer, text, 0, 0, getColor(), this.shadow.getValue());

		matrices.popMatrix();
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
								.build()
				);
			}
		};
	}
}
