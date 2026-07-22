package me.Azz_9.flex_hud.client.gui.components.config.buttons;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.Translatable;
import me.Azz_9.flex_hud.client.config.option.ConfigEnum;
import me.Azz_9.flex_hud.client.gui.components.TrackableChange;
import me.Azz_9.flex_hud.client.gui.components.config.DataGetter;
import me.Azz_9.flex_hud.client.gui.components.config.Observer;
import me.Azz_9.flex_hud.client.gui.components.config.ResetAware;

public class ConfigCyclingButtonWidget<T, E extends Enum<E> & Translatable> extends Button implements TrackableChange, DataGetter<E>, ResetAware {
	private final E INITIAL_STATE;
	private final List<Observer> observers;
	private final E[] values;
	private final ConfigEnum<E> variable;
	@Nullable
	private final Function<E, Tooltip> getTooltip;

	public ConfigCyclingButtonWidget(int width, int height, ConfigEnum<E> variable, List<Observer> observers, @Nullable Function<E, Tooltip> getTooltip) {
		super(0, 0, width, height, Component.translatable(variable.getValue().getTranslationKey()), (btn) -> {
		}, DEFAULT_NARRATION);
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
	protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		super.renderWidget(graphics, mouseX, mouseY, deltaTicks);

		if (!this.active) {
			graphics.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		super.onClick(mouseX, mouseY);

		// shift click to go backward
		onPress(Screen.hasShiftDown() ? -1 : 1);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
			onPress(Screen.hasShiftDown() ? -1 : 1);
			this.playDownSound(MINECRAFT.getSoundManager());
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private void onPress(int offset) {
		int index = (variable.getValue().ordinal() + offset + values.length) % values.length;

		setValue(values[index]);
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
	public void revertChanges() {
		variable.setValue(INITIAL_STATE);
	}

	@Override
	public E getData() {
		return variable.getValue();
	}

	public void setValue(E value) {
		variable.setValue(value);
		setMessage(Component.translatable(value.getTranslationKey()));
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
