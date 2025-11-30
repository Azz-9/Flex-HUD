package me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigIntGrid;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import me.Azz_9.flex_hud.client.utils.EaseUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class CrosshairButtonWidget<T> extends ClickableWidget implements TrackableChange, DataGetter<int[][]>, ResetAware {
	private ConfigIntGrid variable;
	private final int[][] INITIAL_STATE;
	private final List<Observer> observers;
	private final Consumer<CrosshairButtonWidget<T>> onClickAction;

	private long transitionStartTime = -1;
	private boolean hovering = false;
	private boolean transitioningIn = false;
	private boolean transitioningOut = false;
	private static final int TRANSITION_DURATION = 300;

	public CrosshairButtonWidget(int width, int height, ConfigIntGrid variable, List<Observer> observers, Consumer<CrosshairButtonWidget<T>> onClickAction) {
		super(0, 0, width, height, Text.empty());
		this.variable = variable;
		this.INITIAL_STATE = variable.getValue();
		this.observers = observers;
		this.onClickAction = onClickAction;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.active) {

			drawSelectedTexture(context);

			if (this.isSelected()) {
				context.drawBorder(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
			context.drawBorder(getRight() - getHeight(), getY(), getHeight(), getHeight(), (this.isHovered() ? 0xffd0d0d0 : 0xff404040));
		}
		float startX = getRight() - getHeight() + 1 + (getHeight() - 2 - variable.getRowLength(0)) / 2.0f;
		float startY = getY() + 1 + (getHeight() - 2 - variable.getLength()) / 2.0f;
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(startX, startY, 0);

		for (int y = 0; y < variable.getLength(); y++) {
			for (int x = 0; x < variable.getRowLength(y); x++) {
				context.fill(x, y, x + 1, y + 1, variable.getIntValue(x, y));
			}
		}

		matrices.pop();

		if (!this.active) {
			context.fill(getRight() - getHeight(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	private void drawSelectedTexture(DrawContext context) {
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
				float eased = EaseUtils.getEaseOutQuad(progress);
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
			Identifier selectedTexture = Identifier.of(MOD_ID, "widgets/buttons/selected.png");
			context.drawTexture(RenderLayer::getGuiTextured, selectedTexture, this.getX(), this.getY(), 0, 0, this.width - this.height, this.height, 120, 20, ColorHelper.withAlpha(alpha, 0xFFFFFF));
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
	public void onClick(double mouseX, double mouseY) {
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
	public void cancel() {
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
	public boolean isSelected() {
		return this.isFocused();
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}
}
