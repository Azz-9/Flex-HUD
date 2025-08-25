package me.Azz_9.better_hud.client.configurableModules.modules.hud;

import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractHudElement extends AbstractModule implements MovableModule {

	public double offsetX, offsetY;
	@NotNull
	public AnchorPosition anchorX, anchorY;
	public float scale = 1.0f;

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

	public enum AnchorPosition {
		START,
		CENTER,
		END
	}
}
