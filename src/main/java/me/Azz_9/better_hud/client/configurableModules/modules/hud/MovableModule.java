package me.Azz_9.better_hud.client.configurableModules.modules.hud;

import me.Azz_9.better_hud.client.configurableModules.Configurable;
import net.minecraft.client.MinecraftClient;

public interface MovableModule extends Configurable {
	int getWidth();

	int getHeight();

	default double getScaledWidth() {
		return getWidth() * getScale();
	}

	default double getScaledHeight() {
		return getHeight() * getScale();
	}

	double getOffsetX();

	double getOffsetY();

	AbstractHudElement.AnchorPosition getAnchorX();

	AbstractHudElement.AnchorPosition getAnchorY();

	default float getX() {
		int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();

		if (getAnchorX() == AbstractHudElement.AnchorPosition.START) {
			return (float) Math.clamp(getOffsetX(), 0, Math.max(screenWidth - getScaledWidth(), 0));
		} else if (getAnchorX() == AbstractHudElement.AnchorPosition.CENTER) {
			return (float) Math.clamp((screenWidth - getScaledWidth()) / 2.0 + getOffsetX(), 0, Math.max(screenWidth - getScaledWidth(), 0));
		} else {
			return (float) Math.clamp(screenWidth - getScaledWidth() + getOffsetX(), 0, Math.max(screenWidth - getScaledWidth(), 0));
		}
	}

	default float getY() {
		int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

		if (getAnchorY() == AbstractHudElement.AnchorPosition.START) {
			return (float) Math.clamp(getOffsetY(), 0, Math.max(screenHeight - getScaledHeight(), 0));
		} else if (getAnchorY() == AbstractHudElement.AnchorPosition.CENTER) {
			return (float) Math.clamp((screenHeight - getScaledHeight()) / 2.0 + getOffsetY(), 0, Math.max(screenHeight - getScaledHeight(), 0));
		} else {
			return (float) Math.clamp(screenHeight - getScaledHeight() + getOffsetY(), 0, Math.max(screenHeight - getScaledHeight(), 0));
		}
	}

	default float getXWithScale(float scale) {
		int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();

		if (getAnchorX() == AbstractHudElement.AnchorPosition.START) {
			return (float) getOffsetX();
		} else if (getAnchorX() == AbstractHudElement.AnchorPosition.CENTER) {
			return (float) ((screenWidth - getWidth() * scale) / 2.0 + getOffsetX());
		} else {
			return (float) (screenWidth - getWidth() * scale + getOffsetX());
		}
	}

	default float getYWithScale(float scale) {
		int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

		if (getAnchorY() == AbstractHudElement.AnchorPosition.START) {
			return (float) getOffsetY();
		} else if (getAnchorY() == AbstractHudElement.AnchorPosition.CENTER) {
			return (float) ((screenHeight - getHeight() * scale) / 2.0 + getOffsetY());
		} else {
			return (float) (screenHeight - getHeight() * scale + getOffsetY());
		}
	}

	default void setX(double x) {
		int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();

		double centerX = x + getScaledWidth() / 2.0;

		if (centerX < screenWidth * 0.25) {
			setPos(x, getOffsetY(), AbstractHudElement.AnchorPosition.START, getAnchorY());
		} else if (centerX > screenWidth * 0.75) {
			setPos(x - screenWidth + getScaledWidth(), getOffsetY(), AbstractHudElement.AnchorPosition.END, getAnchorY());
		} else {
			setPos(x - (screenWidth - getScaledWidth()) / 2.0, getOffsetY(), AbstractHudElement.AnchorPosition.CENTER, getAnchorY());
		}
	}

	default void setY(double y) {
		int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

		double centerY = y + getScaledHeight() / 2.0;

		if (centerY < screenHeight * 0.25) {
			setPos(getOffsetX(), y, getAnchorX(), AbstractHudElement.AnchorPosition.START);
		} else if (centerY > screenHeight * 0.75) {
			setPos(getOffsetX(), y - screenHeight + getScaledHeight(), getAnchorX(), AbstractHudElement.AnchorPosition.END);
		} else {
			setPos(getOffsetX(), y - (screenHeight - getScaledHeight()) / 2.0, getAnchorX(), AbstractHudElement.AnchorPosition.CENTER);
		}
	}

	default int getRoundedX() {
		return Math.round(getX());
	}

	default int getRoundedY() {
		return Math.round(getY());
	}

	void setPos(double offsetX, double offsetY, AbstractHudElement.AnchorPosition anchorX, AbstractHudElement.AnchorPosition anchorY);

	float getScale();

	default float computeMaxScale() {
		int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
		int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

		float maxWidthScale;
		float maxHeightScale;

		// --- Horizontal ---
		if (getAnchorX() == AbstractHudElement.AnchorPosition.START) {
			// Must not overflow to the right
			maxWidthScale = (float) ((screenWidth - getOffsetX()) / getWidth());
		} else if (getAnchorX() == AbstractHudElement.AnchorPosition.CENTER) {
			// Must fit in both directions from the center
			float leftSpace = (float) ((screenWidth / 2.0 + getOffsetX()) / (getWidth() / 2.0));
			float rightSpace = (float) ((screenWidth / 2.0 - getOffsetX()) / (getWidth() / 2.0));
			maxWidthScale = Math.min(leftSpace, rightSpace);
		} else { // END
			// Must not overflow to the left
			maxWidthScale = (float) ((getOffsetX() + getWidth()) / getWidth());
		}

		// --- Vertical ---
		if (getAnchorY() == AbstractHudElement.AnchorPosition.START) {
			// Must not overflow to the bottom
			maxHeightScale = (float) ((screenHeight - getOffsetY()) / getHeight());
		} else if (getAnchorY() == AbstractHudElement.AnchorPosition.CENTER) {
			// Must fit in both directions from the center
			float topSpace = (float) ((screenHeight / 2.0 + getOffsetY()) / (getHeight() / 2.0));
			float bottomSpace = (float) ((screenHeight / 2.0 - getOffsetY()) / (getHeight() / 2.0));
			maxHeightScale = Math.min(topSpace, bottomSpace);
		} else { // END
			// Must not overflow to the top
			maxHeightScale = (float) ((getOffsetY() + getHeight()) / getHeight());
		}

		// Final scale is the minimum of both to ensure full visibility
		return Math.max(0.1f, Math.min(maxWidthScale, maxHeightScale));
	}

	void setScale(float scale);

	boolean isEnabled();

	void setEnabled(boolean enabled);
}
