package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons;

import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Function;

public class ConfigCyclingButtonWidget<T, E extends Enum<E> & Translatable> extends ButtonWidget implements TrackableChange, DataGetter<E>, ResetAware {
	private final E INITIAL_STATE;
	private final List<Observer> observers;
	private final E[] values;
	private final ConfigEnum<E> variable;
	@Nullable
	private final Function<E, Tooltip> getTooltip;

	public ConfigCyclingButtonWidget(int width, int height, ConfigEnum<E> variable, List<Observer> observers, @Nullable Function<E, Tooltip> getTooltip) {
		super(0, 0, width, height, Text.translatable(variable.getValue().getTranslationKey()), (btn) -> {
		}, DEFAULT_NARRATION_SUPPLIER);
		this.INITIAL_STATE = variable.getValue();
		this.observers = observers;
		this.variable = variable;
		this.values = variable.getValue().getDeclaringClass().getEnumConstants();
		this.getTooltip = getTooltip;

		if (getTooltip != null) {
			this.setTooltip(getTooltip.apply(variable.getValue()));
		}
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.renderWidget(context, mouseX, mouseY, deltaTicks);

		if (!this.active) {
			context.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		super.onClick(mouseX, mouseY);

		// shift click to go backward
		int offset = Screen.hasShiftDown() ? -1 : 1;
		int index = (variable.getValue().ordinal() + offset + values.length) % values.length;

		setValue(values[index]);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_SPACE) {
			this.onClick(0, 0);
			this.playDownSound(MinecraftClient.getInstance().getSoundManager());
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void setToDefaultState() {
		setValue(variable.getDefaultValue());
	}

	@Override
	public boolean hasChanged() {
		return !variable.getValue().equals(INITIAL_STATE);
	}

	@Override
	public void cancel() {
		variable.setValue(INITIAL_STATE);
	}

	@Override
	public E getData() {
		return variable.getValue();
	}

	public void setValue(E value) {
		variable.setValue(value);
		setMessage(Text.translatable(value.getTranslationKey()));
		if (getTooltip != null) setTooltip(getTooltip.apply(value));

		for (Observer observer : observers) {
			observer.onChange(this);
		}
	}

	@Override
	public boolean isCurrentValueDefault() {
		return variable.getValue().equals(variable.getDefaultValue());
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}
}
