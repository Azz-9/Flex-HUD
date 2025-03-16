package me.Azz_9.better_hud.screens.widgets.buttons;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class CyclingButtonWidget<E extends Enum<E>> extends ButtonWidget implements TrackableChange {
	private final E INITIAL_VALUE;
	private final Consumer<E> CONSUMER;

	public CyclingButtonWidget(int x, int y, int width, int height, E currentValue, Text message, PressAction onPress, Class<E> enumClass, Consumer<E> consumer) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
		this.INITIAL_VALUE = currentValue;
		this.CONSUMER = consumer;
	}

	@Override
	public boolean hasChanged() {
		return !getMessage().getString().equals(INITIAL_VALUE.name());
	}

	@Override
	public void cancel() {
		CONSUMER.accept(INITIAL_VALUE);
	}
}
