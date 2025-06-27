package me.Azz_9.better_hud.client.configurableModules.modules.hud;

import me.Azz_9.better_hud.client.configurableModules.Configurable;
import net.minecraft.client.MinecraftClient;

public interface MovableModule extends Configurable {
	int getWidth();

	int getHeight();

	double getOffsetX();

	double getOffsetY();

	AbstractHudElement.AnchorPosition getAnchorX();

	AbstractHudElement.AnchorPosition getAnchorY();

	default float getX() {
		int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();

		if (getAnchorX() == AbstractHudElement.AnchorPosition.START) {
			return (float) Math.clamp(getOffsetX(), 0, Math.max(screenWidth - getWidth() * getScale(), 0));
		} else if (getAnchorX() == AbstractHudElement.AnchorPosition.CENTER) {
			return (float) Math.clamp(screenWidth / 2.0 + getOffsetX(), 0, Math.max(screenWidth - getWidth() * getScale(), 0));
		} else {
			return (float) Math.clamp(screenWidth + getOffsetX(), 0, Math.max(screenWidth - getWidth() * getScale(), 0));
		}
	}

	default float getY() {
		int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

		if (getAnchorY() == AbstractHudElement.AnchorPosition.START) {
			return (float) Math.clamp(getOffsetY(), 0, Math.max(screenHeight - getHeight() * getScale(), 0));
		} else if (getAnchorY() == AbstractHudElement.AnchorPosition.CENTER) {
			return (float) Math.clamp(screenHeight / 2.0 + getOffsetY(), 0, Math.max(screenHeight - getHeight() * getScale(), 0));
		} else {
			return (float) Math.clamp(screenHeight + getOffsetY(), 0, Math.max(screenHeight - getHeight() * getScale(), 0));
		}
	}

	void setPos(double offsetX, double offsetY, AbstractHudElement.AnchorPosition anchorX, AbstractHudElement.AnchorPosition anchorY);

	float getScale();

	void setScale(float scale);

	boolean isEnabled();

	void setEnabled(boolean enabled);
}
