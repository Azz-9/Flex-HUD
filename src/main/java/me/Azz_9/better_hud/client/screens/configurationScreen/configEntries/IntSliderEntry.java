package me.Azz_9.better_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.slider.ConfigIntSliderWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class IntSliderEntry extends ScrollableConfigList.AbstractConfigEntry {
	private final ConfigIntSliderWidget<?> sliderWidget;

	public <T> IntSliderEntry(
			int intSliderWidth,
			int intSliderHeight,
			int value,
			int defaultValue,
			int min,
			int max,
			Integer step,
			Consumer<Integer> onChange,
			int resetButtonSize,
			Text text,
			T disableWhen
	) {
		super(resetButtonSize, text);
		sliderWidget = new ConfigIntSliderWidget<>(intSliderWidth, intSliderHeight, value, defaultValue, step, min, max, onChange, observers, disableWhen);
		setResetButtonPressAction((btn) -> sliderWidget.setToInitialState());

		sliderWidget.addObserver((Observer) this.resetButtonWidget);
		((Observer) this.resetButtonWidget).onChange(sliderWidget);
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
		sliderWidget.setPosition(x + entryWidth - resetButtonWidget.getWidth() - 10 - sliderWidget.getWidth(), y);

		sliderWidget.renderWidget(context, mouseX, mouseY, tickProgress);
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
	public static class Builder extends AbstractBuilder {
		private int intSliderWidth;
		private int intSliderHeight = 20;
		private int value;
		private int defaultValue;
		private int min;
		private int max;
		private Integer step = null;
		private Consumer<Integer> onValueChange = t -> {
		};
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

		public IntSliderEntry.Builder setValue(int value) {
			this.value = value;
			return this;
		}

		public IntSliderEntry.Builder setDefaultValue(int defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public IntSliderEntry.Builder setMin(int min) {
			this.min = min;
			return this;
		}

		public IntSliderEntry.Builder setMax(int max) {
			this.max = max;
			return this;
		}

		public IntSliderEntry.Builder setStep(int step) {
			this.step = step;
			return this;
		}

		public IntSliderEntry.Builder setOnValueChange(Consumer<Integer> onValueChange) {
			this.onValueChange = onValueChange;
			return this;
		}

		public <T> IntSliderEntry.Builder setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependency = entry;
			this.disableWhen = disableWhen;
			return this;
		}

		@Override
		public IntSliderEntry build() {
			IntSliderEntry entry = new IntSliderEntry(
					intSliderWidth, intSliderHeight,
					value, defaultValue, min, max,
					step,
					onValueChange,
					resetButtonSize,
					text,
					disableWhen
			);
			if (dependency != null) {
				dependency.addObserver(entry);
				entry.onChange(dependency.getDataGetter());
			}
			return entry;
		}
	}
}
