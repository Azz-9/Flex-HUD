package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.utils.ChromaColorUtils;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTextElement extends AbstractBackgroundElement {

	public ConfigBoolean shadow = new ConfigBoolean(true, "flex_hud.global.config.text_shadow");
	public ConfigBoolean chromaColor = new ConfigBoolean(false, "flex_hud.global.config.chroma_text_color");
	public ConfigInteger color = new ConfigInteger(0xffffff, "flex_hud.global.config.text_color");

	public AbstractTextElement(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
	}

	protected void updateWidth(String text) {
		int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(text);
		if (textWidth > this.width) {
			this.width = textWidth;
		}
	}

	protected void updateWidth(String text, int startX) {
		int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(text);
		if (startX + textWidth > this.width) {
			this.width = startX + textWidth;
		}
	}

	protected void setWidth(String text) {
		this.width = MinecraftClient.getInstance().textRenderer.getWidth(text);
	}

	protected int getColor() {
		if (chromaColor.getValue()) {
			return ChromaColorUtils.getColor();
		}
		return color.getValue() | 0xff000000;
	}
}
