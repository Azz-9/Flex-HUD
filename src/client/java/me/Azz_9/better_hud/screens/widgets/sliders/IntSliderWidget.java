package me.Azz_9.better_hud.screens.widgets.sliders;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class IntSliderWidget extends SliderWidget implements TrackableChange {
	private final Integer step;
	private final int minValue;
	private final int maxValue;
	private final int initialValue;
	private final Consumer<Integer> consumer;

	public IntSliderWidget(int x, int y, int width, int height, double value, Integer step, int minValue, int maxValue, Consumer<Integer> consumer) {
		super(x, y, width, height, Text.of(String.valueOf((int) (value * maxValue))), value);
		this.step = step;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.initialValue = (int) (value * maxValue);
		this.consumer = consumer;
	}

	@Override
	protected void updateMessage() {
		setMessage(Text.of(String.valueOf(getCurrentValue())));
	}

	@Override
	protected void applyValue() {
		if (isShiftPressed() && step != null) {
			// Snap to nearest multiple of step
			int rawValue = getCurrentValue();
			int snappedValue = Math.round((float) rawValue / step) * step;
			this.value = (snappedValue - minValue) / (double) (maxValue - minValue);
		}

		consumer.accept(getCurrentValue());
	}

	private int getCurrentValue() {
		return (int) (value * (maxValue - minValue)) + minValue;
	}

	private boolean isShiftPressed() {
		MinecraftClient client = MinecraftClient.getInstance();
		return InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_LEFT_SHIFT) ||
			   InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_RIGHT_SHIFT);
	}

	public void setValue(int value) {
		this.value = (double) value / maxValue;
		updateMessage();
		consumer.accept(value);
	}

	@Override
	public boolean hasChanged() {
		return initialValue != getCurrentValue();
	}

	@Override
	public void cancel() {
		consumer.accept(initialValue);
	}
}
