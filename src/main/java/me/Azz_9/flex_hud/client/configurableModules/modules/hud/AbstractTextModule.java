package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.tickables.ChromaColorTickable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTextModule extends AbstractBackgroundModule {

	public ConfigBoolean shadow = new ConfigBoolean(true, "flex_hud.global.config.text_shadow");
	public ConfigBoolean chromaColor = new ConfigBoolean(false, "flex_hud.global.config.chroma_text_color");
	public ConfigInteger color = new ConfigInteger(0xffffff, "flex_hud.global.config.text_color");

	public AbstractTextModule(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);

		ConfigRegistry.register(getID(), "shadow", shadow);
		ConfigRegistry.register(getID(), "chroma_color", chromaColor);
		ConfigRegistry.register(getID(), "color", color);
	}

	protected void updateWidth(String text) {
		int textWidth = Minecraft.getInstance().font.width(text);
		if (textWidth > getWidth()) {
			setWidth(textWidth);
		}
	}

	protected void updateWidth(String text, int startX) {
		int textWidth = Minecraft.getInstance().font.width(text);
		if (startX + textWidth > getWidth()) {
			setWidth(startX + textWidth);
		}
	}

	protected void setWidth(String text) {
		setWidth(Minecraft.getInstance().font.width(text));
	}

	protected void setWidth(String text, int startX) {
		int textWidth = Minecraft.getInstance().font.width(text);
		setWidth(startX + textWidth);
	}

	protected int getColor() {
		if (chromaColor.getValue()) {
			return ChromaColorTickable.getColor();
		}
		return ARGB.color(255, color.getValue());
	}
}
