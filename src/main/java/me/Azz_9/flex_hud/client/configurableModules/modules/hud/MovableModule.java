package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import me.Azz_9.flex_hud.client.configurableModules.Configurable;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.widgets.MovableWidget;
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
			maxWidthScale = (float) (screenWidth - Math.round(getOffsetX())) / getWidth();
		} else if (getAnchorX() == AbstractHudElement.AnchorPosition.END) {
			maxWidthScale = (float) (screenWidth + Math.round(getOffsetX())) / getWidth();
		} else { // CENTER
			maxWidthScale = (float) ((screenWidth / 2.0 - Math.abs(getOffsetX())) / (getWidth() / 2.0));
		}

		// --- Vertical ---
		if (getAnchorY() == AbstractHudElement.AnchorPosition.START) {
			maxHeightScale = (float) (screenHeight - Math.round(getOffsetY())) / getHeight();
		} else if (getAnchorY() == AbstractHudElement.AnchorPosition.END) {
			maxHeightScale = (float) (screenHeight + Math.round(getOffsetY())) / getHeight();
		} else { // CENTER
			maxHeightScale = (float) ((screenHeight / 2.0 - Math.abs(getOffsetY())) / (getHeight() / 2.0));
		}

		return Math.max(MovableWidget.MIN_SCALE, Math.min(maxWidthScale, maxHeightScale));
	}


	void setScale(float scale);

	void setEnabled(boolean enabled);
}
