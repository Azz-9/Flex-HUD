package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.util.math.ColorHelper;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.tickables.ChromaColorTickable;

public abstract class AbstractTextModule extends AbstractBackgroundModule {

	public ConfigBoolean shadow = new ConfigBoolean(true, "flex_hud.global.config.text_shadow");
	public ConfigBoolean chromaColor = new ConfigBoolean(false, "flex_hud.global.config.chroma_text_color");
	public ConfigInteger color = new ConfigInteger(0xffffff, "flex_hud.global.config.text_color");

	public AbstractTextModule(@NonNull String id, double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(id, defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);

		ConfigRegistry.register(getID(), "shadow", shadow);
		ConfigRegistry.register(getID(), "chroma_color", chromaColor);
		ConfigRegistry.register(getID(), "color", color);
	}

	protected void updateWidth(String text) {
		int textWidth = CLIENT.textRenderer.getWidth(text);
		if (textWidth > getWidth()) {
			setWidth(textWidth);
		}
	}

	protected void updateWidth(String text, int startX) {
		int textWidth = CLIENT.textRenderer.getWidth(text);
		if (startX + textWidth > getWidth()) {
			setWidth(startX + textWidth);
		}
	}

	protected void setWidth(String text) {
		setWidth(CLIENT.textRenderer.getWidth(text));
	}

	protected void setWidth(String text, int startX) {
		int textWidth = CLIENT.textRenderer.getWidth(text);
		setWidth(startX + textWidth);
	}

	protected int getColor() {
		if (chromaColor.getValue()) {
			return ChromaColorTickable.getColor();
		}
		return ColorHelper.withAlpha(255, color.getValue());
	}
}
