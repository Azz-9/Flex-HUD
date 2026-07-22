package me.Azz_9.flex_hud.client.gui.components.config.buttons;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Ease;

import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import me.Azz_9.flex_hud.client.config.option.ConfigIntGrid;
import me.Azz_9.flex_hud.client.gui.Cursors;
import me.Azz_9.flex_hud.client.gui.components.TrackableChange;
import me.Azz_9.flex_hud.client.gui.components.config.DataGetter;
import me.Azz_9.flex_hud.client.gui.components.config.Observer;
import me.Azz_9.flex_hud.client.gui.components.config.ResetAware;

public class CrosshairButtonWidget<T> extends AbstractWidget.WithInactiveMessage implements TrackableChange, DataGetter<int[][]>, ResetAware {
	private final ConfigIntGrid variable;
	private final int[][] INITIAL_STATE;
	private final List<Observer> observers;
	private final Consumer<CrosshairButtonWidget<T>> onClickAction;

	private long transitionStartTime = -1;
	private boolean hovering = false;
	private boolean transitioningIn = false;
	private boolean transitioningOut = false;
	private static final int TRANSITION_DURATION = 300;

	public CrosshairButtonWidget(int width, int height, ConfigIntGrid variable, List<Observer> observers, Consumer<CrosshairButtonWidget<T>> onClickAction) {
		super(0, 0, width, height, Component.empty());
		this.variable = variable;
		this.INITIAL_STATE = variable.getValue();
		this.observers = observers;
		this.onClickAction = onClickAction;
	}

	@Override
	protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.active) {
			if (this.isHovered()) graphics.requestCursor(Cursors.POINTING_HAND);

			drawHover(graphics);

			if (this.isHoveredOrFocused()) {
				graphics.renderOutline(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
			graphics.renderOutline(getRight() - getHeight(), getY(), getHeight(), getHeight(), (this.isHovered() ? 0xffd0d0d0 : 0xff404040));
		} else {
			if (this.isHovered()) graphics.requestCursor(Cursors.NOT_ALLOWED);
		}
		float startX = getRight() - getHeight() + 1 + (getHeight() - 2 - variable.getRowLength(0)) / 2.0f;
		float startY = getY() + 1 + (getHeight() - 2 - variable.getLength()) / 2.0f;
		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(startX, startY);

		for (int y = 0; y < variable.getLength(); y++) {
			for (int x = 0; x < variable.getRowLength(y); x++) {
				graphics.fill(x, y, x + 1, y + 1, variable.getIntValue(x, y));
			}
		}

		matrices.popMatrix();

		if (!this.active) {
			graphics.fill(getRight() - getHeight(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	private void drawHover(GuiGraphics graphics) {
		boolean currentlyHovered = this.isHovered();

		// Handle transition triggers
		if (currentlyHovered && !hovering) {
			hovering = true;
			transitioningIn = true;
			transitioningOut = false;
			transitionStartTime = System.currentTimeMillis();
		} else if (!currentlyHovered && hovering) {
			hovering = false;
			transitioningOut = true;
			transitioningIn = false;
			transitionStartTime = System.currentTimeMillis();
		}

		// Calculate alpha
		int alpha = 0;
		if (transitioningIn || transitioningOut) {
			int elapsed = (int) (System.currentTimeMillis() - transitionStartTime);
			if (elapsed <= TRANSITION_DURATION) {
				float progress = (float) elapsed / TRANSITION_DURATION;
				float eased = Ease.outQuad(progress);
				if (transitioningOut) eased = 1 - eased;
				alpha = (int) (0xFF * eased);
			} else {
				alpha = transitioningIn ? 0xFF : 0x00;
				transitioningIn = false;
				transitioningOut = false;
			}
		} else if (hovering) {
			alpha = 0xFF;
		}

		if (alpha > 0) {
			graphics.fill(getX(), getY(), getRight(), getBottom(), ARGB.color(alpha / 3, 0xC5C5C5));
		}
	}

	public void onReceivePixel(int x, int y, int color) {
		if (this.variable.getIntValue(x, y) != color) {
			this.variable.setIntValue(x, y, color);

			for (Observer observer : observers) {
				observer.onChange(this);
			}
		}
	}

	public void setCrosshairTexture(int[][] texture) {
		this.variable.setValue(texture);

		for (Observer observer : observers) {
			observer.onChange(this);
		}
	}

	@Override
	public void onClick(@NonNull MouseButtonEvent click, boolean bl) {
		onClickAction.accept(this);
	}

	@Override
	public void setToDefaultState() {
		for (int y = 0; y < variable.getLength(); y++) {
			for (int x = 0; x < variable.getRowLength(y); x++) {
				onReceivePixel(x, y, variable.getIntDefaultValue(x, y));
			}
		}
	}

	@Override
	public boolean hasChanged() {
		return !Arrays.deepEquals(variable.getValue(), INITIAL_STATE);
	}

	@Override
	public void revertChanges() {
		variable.setValue(INITIAL_STATE);
	}

	@Override
	public int[][] getData() {
		return variable.getValue();
	}

	@Override
	public boolean isCurrentValueDefault() {
		return Arrays.deepEquals(variable.getValue(), variable.getDefaultValue());
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public boolean isHoveredOrFocused() {
		return this.isFocused();
	}

	@Override
	protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
	}
}
