package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons;

import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Function;

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
	protected void renderContents(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		super.renderDefaultSprite(graphics);
		super.renderDefaultLabel(graphics.textRenderer());

		if (!this.active) {
			graphics.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	@Override
	public void onClick(@NonNull MouseButtonEvent click, boolean bl) {
		super.onClick(click, bl);

		// shift click to go backward
		onPress(Minecraft.getInstance().hasShiftDown() ? -1 : 1);
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		if (input.isConfirmation()) {
			onPress(input.hasShiftDown() ? -1 : 1);
			this.playDownSound(Minecraft.getInstance().getSoundManager());
			return true;
		}
		return super.keyPressed(input);
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
	public void cancel() {
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
