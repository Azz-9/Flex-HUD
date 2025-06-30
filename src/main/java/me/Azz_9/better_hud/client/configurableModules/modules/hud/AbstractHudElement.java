package me.Azz_9.better_hud.client.configurableModules.modules.hud;

import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.better_hud.client.utils.ChromaColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractHudElement extends AbstractModule implements MovableModule {
	protected transient final int BACKGROUND_PADDING = 2;

	public double offsetX, offsetY;
	@NotNull
	public AnchorPosition anchorX, anchorY;
	public float scale = 1.0f;

	public ConfigBoolean shadow = new ConfigBoolean(true, "better_hud.global.config.text_shadow");
	public ConfigBoolean chromaColor = new ConfigBoolean(false, "better_hud.global.config.chroma_text_color");
	public ConfigInteger color = new ConfigInteger(0xffffff, "better_hud.global.config.text_color");
	public ConfigBoolean drawBackground = new ConfigBoolean(false, "better_hud.global.config.show_background");
	public ConfigInteger backgroundColor = new ConfigInteger(0x313131, "better_hud.global.config.background_color");
	public ConfigBoolean hideInF3 = new ConfigBoolean(true, "better_hud.global.config.hide_in_f3");

	protected transient int height, width;

	public AbstractHudElement(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super();
		this.offsetX = defaultOffsetX;
		this.offsetY = defaultOffsetY;
		this.anchorX = defaultAnchorX;
		this.anchorY = defaultAnchorY;
	}

	public abstract void render(DrawContext context, RenderTickCounter tickCounter);

	protected boolean shouldNotRender() {
		return !JsonConfigHelper.getInstance().isEnabled || !this.enabled.getValue() || (this.hideInF3.getValue() && MinecraftClient.getInstance().getDebugHud().shouldShowDebugHud());
	}

	protected void drawBackground(DrawContext context) {
		if (drawBackground.getValue() && width != 0 && height != 0) {
			context.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor.getValue());
		}
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

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public double getOffsetX() {
		return offsetX;
	}

	@Override
	public double getOffsetY() {
		return offsetY;
	}

	@Override
	public @NotNull AnchorPosition getAnchorX() {
		return anchorX;
	}

	@Override
	public @NotNull AnchorPosition getAnchorY() {
		return anchorY;
	}

	protected void setWidth(String text) {
		this.width = MinecraftClient.getInstance().textRenderer.getWidth(text);
	}

	@Override
	public void setPos(double offsetX, double offsetY, AnchorPosition anchorX, AnchorPosition anchorY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.anchorX = anchorX;
		this.anchorY = anchorY;
	}

	@Override
	public float getScale() {
		return scale;
	}

	@Override
	public void setScale(float scale) {
		this.scale = scale;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled.setValue(enabled);
	}

	protected int getColor() {
		if (chromaColor.getValue()) {
			return ChromaColorUtils.getColor();
		}
		return color.getValue() | 0xff000000;
	}

	public enum AnchorPosition {
		START,
		CENTER,
		END
	}
}
