package me.Azz_9.better_hud.client.screens.configurationScreen.crosshairConfigScreen;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigIntGrid;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CrosshairButtonWidget<T> extends ClickableWidget implements TrackableChange, DataGetter<int[][]>, ResetAware {
	private ConfigIntGrid variable;
	private final int[][] INITIAL_STATE;
	private final List<Observer> observers;
	private final T disableWhen;
	private final Consumer<CrosshairButtonWidget<T>> onClickAction;

	public CrosshairButtonWidget(int width, int height, ConfigIntGrid variable, List<Observer> observers, T disableWhen, Consumer<CrosshairButtonWidget<T>> onClickAction) {
		super(0, 0, width, height, Text.empty());
		this.variable = variable;
		this.INITIAL_STATE = new int[variable.getValue().length][];
		for (int i = 0; i < variable.getValue().length; i++) {
			this.INITIAL_STATE[i] = Arrays.copyOf(variable.getValue()[i], variable.getValue()[i].length);
		}
		this.observers = observers;
		this.disableWhen = disableWhen;
		this.onClickAction = onClickAction;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.active) {
			if (this.isHovered()) {
				context.fill(this.getX(), this.getY(), this.getRight() - getHeight(), this.getBottom(), 0x80c5c5c5);
			}
			if (this.isSelected()) {
				context.drawBorder(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
			context.drawBorder(getRight() - getHeight(), getY(), getHeight(), getHeight(), (this.isHovered() ? 0xffd0d0d0 : 0xff404040));
		}
		float startX = getRight() - getHeight() + 1 + (getHeight() - 2 - variable.getValue()[0].length) / 2.0f;
		float startY = getY() + 1 + (getHeight() - 2 - variable.getValue().length) / 2.0f;
		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(startX, startY);

		for (int y = 0; y < variable.getValue().length; y++) {
			for (int x = 0; x < variable.getValue()[y].length; x++) {
				context.fill(x, y, x + 1, y + 1, variable.getValue()[y][x]);
			}
		}

		matrices.popMatrix();

		if (!this.active) {
			context.fill(getRight() - getHeight(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	public void onReceivePixel(int x, int y, int color) {
		if (this.variable.getValue()[y][x] != color) {
			this.variable.getValue()[y][x] = color;

			for (Observer observer : observers) {
				observer.onChange(this);
			}
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		onClickAction.accept(this);
	}

	@Override
	public void setToDefaultState() {
		for (int y = 0; y < variable.getDefaultValue().length; y++) {
			for (int x = 0; x < variable.getDefaultValue()[y].length; x++) {
				onReceivePixel(x, y, variable.getDefaultValue()[y][x]);
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

	public T getDisableWhen() {
		return disableWhen;
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
