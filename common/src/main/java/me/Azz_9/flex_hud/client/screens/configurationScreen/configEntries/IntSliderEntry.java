package me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.slider.ConfigIntSliderWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;

public class IntSliderEntry extends ScrollableConfigList.AbstractConfigEntry {
	private final ConfigIntSliderWidget<?> sliderWidget;

	private <T> IntSliderEntry(
			int intSliderWidth,
			int intSliderHeight,
			ConfigInteger variable,
			Integer step,
			int resetButtonSize,
			T disableWhen,
			Function<Integer, Tooltip> getTooltip
	) {
		super(resetButtonSize, Text.translatable(variable.getConfigTextTranslationKey()));
		sliderWidget = new ConfigIntSliderWidget<>(intSliderWidth, intSliderHeight, variable, step, observers, disableWhen, getTooltip);
		setResetButtonPressAction((btn) -> sliderWidget.setToDefaultState());

		sliderWidget.addObserver(this.resetButtonWidget);
		this.resetButtonWidget.onChange(sliderWidget);
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		sliderWidget.setX(x + getWidth() - resetButtonWidget.getWidth() - 10 - sliderWidget.getWidth());
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		sliderWidget.setY(y);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
		super.render(context, mouseX, mouseY, hovered, deltaTicks);

		sliderWidget.render(context, mouseX, mouseY, deltaTicks);
	}

	@Override
	public List<? extends Selectable> selectableChildren() {
		return List.of(sliderWidget, resetButtonWidget);
	}

	@Override
	public List<? extends Element> children() {
		return List.of(sliderWidget, resetButtonWidget, textWidget);
	}

	@Override
	public TrackableChange getTrackableChangeWidget() {
		return this.sliderWidget;
	}

	@Override
	public DataGetter<?> getDataGetter() {
		return this.sliderWidget;
	}

	@Override
	public void onChange(DataGetter<?> dataGetter) {
		boolean active = !sliderWidget.getDisableWhen().equals(dataGetter.getData());
		sliderWidget.active = active;
		setActive(active);
		resetButtonWidget.active = active && !sliderWidget.isCurrentValueDefault();
	}

	// Builder
	public static class Builder extends AbstractBuilder<Integer> {
		private int intSliderWidth;
		private int intSliderHeight = 20;
		private ConfigInteger variable;
		private Integer step = null;
		private ScrollableConfigList.AbstractConfigEntry dependency = null;
		private Object disableWhen;

		public IntSliderEntry.Builder setIntSliderWidth(int width) {
			this.intSliderWidth = width;
			return this;
		}

		public IntSliderEntry.Builder setIntSliderSize(int width, int height) {
			this.intSliderWidth = width;
			this.intSliderHeight = height;
			return this;
		}

		public IntSliderEntry.Builder setVariable(ConfigInteger variable) {
			this.variable = variable;
			return this;
		}

		public IntSliderEntry.Builder setStep(int step) {
			this.step = step;
			return this;
		}

		public <T> IntSliderEntry.Builder setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependency = entry;
			this.disableWhen = disableWhen;
			return this;
		}

		@Override
		public IntSliderEntry build() {
			if (variable == null)
				throw new IllegalArgumentException("IntSliderEntry requires a variable to be set using setVariable()!");

			IntSliderEntry entry = new IntSliderEntry(
					intSliderWidth, intSliderHeight,
					variable,
					step,
					resetButtonSize,
					disableWhen,
					getTooltip
			);
			if (dependency != null) {
				dependency.addObserver(entry);
				entry.onChange(dependency.getDataGetter());
			}
			return entry;
		}
	}
}
