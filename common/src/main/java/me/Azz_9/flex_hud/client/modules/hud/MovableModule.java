package me.Azz_9.flex_hud.client.modules.hud;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import me.Azz_9.flex_hud.client.gui.components.MovableWidget;

public interface MovableModule {
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

	AbstractMovableModule.AnchorPosition getAnchorX();

	AbstractMovableModule.AnchorPosition getAnchorY();

	default float getX() {
		return calculateClampedPosition(
				getOffsetX(), getAnchorX(), getScaledWidth(), MINECRAFT.getWindow().getGuiScaledWidth()
		);
	}

	default float getY() {
		return calculateClampedPosition(
				getOffsetY(), getAnchorY(), getScaledHeight(), MINECRAFT.getWindow().getGuiScaledHeight()
		);
	}

	default float getXWithScale(float scale) {
		return (float) calculatePosition(
				getOffsetX(), getAnchorX(), getWidth() * scale, MINECRAFT.getWindow().getGuiScaledWidth()
		);
	}

	default float getYWithScale(float scale) {
		return (float) calculatePosition(
				getOffsetY(), getAnchorY(), getHeight() * scale, MINECRAFT.getWindow().getGuiScaledHeight()
		);
	}

	static double calculatePosition(double offset, AbstractMovableModule.AnchorPosition anchor, double scaledSize, int screenSize) {
		return switch (anchor) {
			case START -> offset;
			case CENTER -> (screenSize - scaledSize) / 2.0 + offset;
			case END -> screenSize - scaledSize + offset;
		};
	}

	static float calculateClampedPosition(double offset, AbstractMovableModule.AnchorPosition anchor, double scaledSize, int screenSize) {
		return (float) Math.clamp(
				calculatePosition(offset, anchor, scaledSize, screenSize),
				0,
				Math.max(screenSize - scaledSize, 0)
		);
	}

	default void setX(double x, AbstractMovableModule.AnchorMode modeX) {
		int screenWidth = MINECRAFT.getWindow().getGuiScaledWidth();

		AbstractMovableModule.AnchorPosition anchor = modeX.isFixed()
				? modeX.toAnchorPosition()
				: resolveAutoAnchorX(x);

		double newOffsetX = switch (anchor) {
			case START -> x;
			case CENTER -> x - (screenWidth - getScaledWidth()) / 2.0;
			case END -> x - screenWidth + getScaledWidth();
		};

		setPos(newOffsetX, getOffsetY(), anchor, getAnchorY());
	}

	default void setY(double y, AbstractMovableModule.AnchorMode modeY) {
		int screenHeight = MINECRAFT.getWindow().getGuiScaledHeight();

		AbstractMovableModule.AnchorPosition anchor = modeY.isFixed()
				? modeY.toAnchorPosition()
				: resolveAutoAnchorY(y);

		double newOffsetY = switch (anchor) {
			case START -> y;
			case CENTER -> y - (screenHeight - getScaledHeight()) / 2.0;
			case END -> y - screenHeight + getScaledHeight();
		};

		setPos(getOffsetX(), newOffsetY, getAnchorX(), anchor);
	}

	default int getRoundedX() {
		return Math.round(getX());
	}

	default int getRoundedY() {
		return Math.round(getY());
	}

	void setPos(double offsetX, double offsetY, AbstractMovableModule.AnchorPosition anchorX, AbstractMovableModule.AnchorPosition anchorY);

	float getScale();

	default float computeMaxScale() {
		int screenWidth = MINECRAFT.getWindow().getGuiScaledWidth();
		int screenHeight = MINECRAFT.getWindow().getGuiScaledHeight();

		float maxWidthScale;
		float maxHeightScale;

		// --- Horizontal ---
		if (getAnchorX() == AbstractMovableModule.AnchorPosition.START) {
			maxWidthScale = (float) (screenWidth - Math.round(getOffsetX())) / getWidth();
		} else if (getAnchorX() == AbstractMovableModule.AnchorPosition.END) {
			maxWidthScale = (float) (screenWidth + Math.round(getOffsetX())) / getWidth();
		} else { // CENTER
			maxWidthScale = (float) ((screenWidth / 2.0 - Math.abs(getOffsetX())) / (getWidth() / 2.0));
		}

		// --- Vertical ---
		if (getAnchorY() == AbstractMovableModule.AnchorPosition.START) {
			maxHeightScale = (float) (screenHeight - Math.round(getOffsetY())) / getHeight();
		} else if (getAnchorY() == AbstractMovableModule.AnchorPosition.END) {
			maxHeightScale = (float) (screenHeight + Math.round(getOffsetY())) / getHeight();
		} else { // CENTER
			maxHeightScale = (float) ((screenHeight / 2.0 - Math.abs(getOffsetY())) / (getHeight() / 2.0));
		}

		return Math.max(MovableWidget.MIN_SCALE, Math.min(maxWidthScale, maxHeightScale));
	}


	void setScale(float scale);

	default AbstractMovableModule.AnchorPosition resolveAutoAnchorX(double absoluteX) {
		int screenWidth = MINECRAFT.getWindow().getGuiScaledWidth();
		double centerX = absoluteX + getScaledWidth() / 2.0;
		if (centerX < screenWidth * 0.25) return AbstractMovableModule.AnchorPosition.START;
		else if (centerX > screenWidth * 0.75) return AbstractMovableModule.AnchorPosition.END;
		else return AbstractMovableModule.AnchorPosition.CENTER;
	}

	default AbstractMovableModule.AnchorPosition resolveAutoAnchorY(double absoluteY) {
		int screenHeight = MINECRAFT.getWindow().getGuiScaledHeight();
		double centerY = absoluteY + getScaledHeight() / 2.0;
		if (centerY < screenHeight * 0.25) return AbstractMovableModule.AnchorPosition.START;
		else if (centerY > screenHeight * 0.75) return AbstractMovableModule.AnchorPosition.END;
		else return AbstractMovableModule.AnchorPosition.CENTER;
	}
}
