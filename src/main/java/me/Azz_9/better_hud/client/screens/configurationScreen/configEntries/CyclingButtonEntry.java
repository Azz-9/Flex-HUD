package me.Azz_9.better_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons.ConfigCyclingButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class CyclingButtonEntry<E extends Enum<E>> extends ScrollableConfigList.AbstractConfigEntry {
	private final ConfigCyclingButtonWidget<?, E> cyclingButtonWidget;

	public <T> CyclingButtonEntry(
			int cyclingButtonWidth,
			int cyclingButtonHeight,
			E initialValue,
			E defaultValue,
			Consumer<E> onValueChange,
			int resetButtonSize,
			Text text,
			T disableWhen
	) {
		super(resetButtonSize, text);
		cyclingButtonWidget = new ConfigCyclingButtonWidget<>(cyclingButtonWidth, cyclingButtonHeight, initialValue, defaultValue, onValueChange, observers, disableWhen);
		setResetButtonPressAction((btn) -> cyclingButtonWidget.setToInitialState());
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
		cyclingButtonWidget.setPosition(x + entryWidth - resetButtonWidget.getWidth() - 10 - cyclingButtonWidget.getWidth(), y);

		cyclingButtonWidget.render(context, mouseX, mouseY, tickProgress);
	}

	@Override
	public List<? extends Selectable> selectableChildren() {
		return List.of(cyclingButtonWidget, resetButtonWidget);
	}

	@Override
	public List<? extends Element> children() {
		return List.of(cyclingButtonWidget, resetButtonWidget, textWidget);
	}

	@Override
	public TrackableChange getTrackableChangeWidget() {
		return this.cyclingButtonWidget;
	}

	@Override
	public DataGetter<?> getDataGetter() {
		return this.cyclingButtonWidget;
	}

	@Override
	public void onChange(DataGetter<?> dataGetter) {
		boolean active = !cyclingButtonWidget.getDisableWhen().equals(dataGetter.getData());
		cyclingButtonWidget.active = active;
		setActive(active);
	}

	// Builder
	public static class Builder<E extends Enum<E>> extends AbstractBuilder {
		private int cyclingButtonWidth;
		private int cyclingButtonHeight = 20;
		private E value;
		private E defaultValue;
		private Consumer<E> onValueChange = t -> {
		};
		private ScrollableConfigList.AbstractConfigEntry dependency = null;
		private Object disableWhen;

		public CyclingButtonEntry.Builder<E> setCyclingButtonWidth(int width) {
			this.cyclingButtonWidth = width;
			return this;
		}

		public CyclingButtonEntry.Builder<E> setCyclingButtonSize(int width, int height) {
			this.cyclingButtonWidth = width;
			this.cyclingButtonHeight = height;
			return this;
		}

		public CyclingButtonEntry.Builder<E> setValue(E value) {
			this.value = value;
			return this;
		}

		public CyclingButtonEntry.Builder<E> setDefaultValue(E defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public CyclingButtonEntry.Builder<E> setOnValueChange(Consumer<E> onValueChange) {
			this.onValueChange = onValueChange;
			return this;
		}

		public <T> CyclingButtonEntry.Builder<E> setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependency = entry;
			this.disableWhen = disableWhen;
			return this;
		}

		@Override
		public CyclingButtonEntry<E> build() {
			CyclingButtonEntry<E> entry = new CyclingButtonEntry<>(
					cyclingButtonWidth, cyclingButtonHeight,
					value, defaultValue,
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