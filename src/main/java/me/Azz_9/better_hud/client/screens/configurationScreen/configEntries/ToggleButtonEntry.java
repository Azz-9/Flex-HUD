package me.Azz_9.better_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons.ConfigToggleButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class ToggleButtonEntry extends ScrollableConfigList.AbstractConfigEntry {
	private final ConfigToggleButtonWidget<?> toggleButtonWidget;

	private <T> ToggleButtonEntry(
			int toggleButtonWidth,
			int toggleButtonHeight,
			boolean toggled,
			boolean defaultValue,
			Consumer<Boolean> onToggle,
			int resetButtonSize,
			Text text,
			T disableWhen
	) {
		super(resetButtonSize, text);
		toggleButtonWidget = new ConfigToggleButtonWidget<>(toggleButtonWidth, toggleButtonHeight, toggled, defaultValue, onToggle, observers, disableWhen);
		setResetButtonPressAction((btn) -> toggleButtonWidget.setToInitialState());
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
		toggleButtonWidget.setPosition(x, y);
		toggleButtonWidget.render(context, mouseX, mouseY, tickProgress);
	}

	@Override
	public List<? extends Selectable> selectableChildren() {
		return List.of(toggleButtonWidget, resetButtonWidget);
	}

	@Override
	public List<? extends Element> children() {
		return List.of(toggleButtonWidget, resetButtonWidget, textWidget);
	}

	@Override
	public TrackableChange getTrackableChangeWidget() {
		return toggleButtonWidget;
	}

	@Override
	public DataGetter<?> getDataGetter() {
		return toggleButtonWidget;
	}

	@Override
	public void onChange(DataGetter<?> dataGetter) {
		boolean active = !toggleButtonWidget.getDisableWhen().equals(dataGetter.getData());
		toggleButtonWidget.active = active;
		setActive(active);
	}

	// Builder
	public static class Builder extends AbstractBuilder {
		private int toggleButtonWidth;
		private int toggleButtonHeight = 20;
		private boolean toggled;
		private boolean defaultValue = false;
		private Consumer<Boolean> onToggle = t -> {
		};
		private ScrollableConfigList.AbstractConfigEntry dependency = null;
		private Object disableWhen;

		public Builder setToggleButtonWidth(int width) {
			this.toggleButtonWidth = width;
			return this;
		}

		public Builder setToggleButtonSize(int width, int height) {
			this.toggleButtonWidth = width;
			this.toggleButtonHeight = height;
			return this;
		}

		public Builder setToggled(boolean toggled) {
			this.toggled = toggled;
			return this;
		}

		public Builder setDefaultValue(boolean defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder setOnToggle(Consumer<Boolean> onToggle) {
			this.onToggle = onToggle;
			return this;
		}

		public <T> Builder setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependency = entry;
			this.disableWhen = disableWhen;
			return this;
		}

		@Override
		public ToggleButtonEntry build() {
			ToggleButtonEntry entry = new ToggleButtonEntry(
					toggleButtonWidth, toggleButtonHeight,
					toggled, defaultValue, onToggle,
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