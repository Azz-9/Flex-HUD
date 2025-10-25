package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.utils.reach.ReachUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class Reach extends AbstractTextElement {
	public ConfigInteger digits = new ConfigInteger(2, "flex_hud.reach.config.number_of_digits", 0, 16);

	public Reach(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public void init() {
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
		this.enabled.setConfigTextTranslationKey("flex_hud.reach.config.enable");
	}

	@Override
	public String getID() {
		return "reach";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.reach");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender()) {
			return;
		}

		String format = "%." + this.digits.getValue() + "f";
		String formattedSpeed = String.format(format, ReachUtils.getReach());
		Text text = Text.literal(formattedSpeed).append(" ").append(Text.translatable("flex_hud.reach.hud.unit"));

		setWidth(text.getString());

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawBackground(context);

		context.drawText(client.textRenderer, Text.of(text), 0, 0, getColor(), this.shadow.getValue());

		matrices.pop();
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
								.build(),
						new IntFieldEntry.Builder()
								.setIntFieldWidth(20)
								.setVariable(digits)
								.build()
				);
			}
		};
	}
}
