package me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.ConfigToggleButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class ToggleButtonEntry extends ScrollableConfigList.AbstractConfigEntry {
	private final ConfigToggleButtonWidget<?> toggleButtonWidget;
	private final BooleanSupplier toggleable;

	private <T> ToggleButtonEntry(
			int toggleButtonWidth,
			int toggleButtonHeight,
			ConfigBoolean variable,
			int resetButtonSize,
			T disableWhen,
			Function<Boolean, Tooltip> getTooltip,
			BooleanSupplier toggleable
	) {
		super(resetButtonSize, Text.translatable(variable.getConfigTextTranslationKey()));
		toggleButtonWidget = new ConfigToggleButtonWidget<>(toggleButtonWidth, toggleButtonHeight, variable, observers, disableWhen, getTooltip);
		setResetButtonPressAction((btn) -> toggleButtonWidget.setToDefaultState());

		toggleButtonWidget.addObserver(this.resetButtonWidget);
		this.resetButtonWidget.onChange(toggleButtonWidget);

		this.toggleable = toggleable;
		if (!toggleable.getAsBoolean()) {
			this.setActive(false);
			toggleButtonWidget.setToggled(false);
			resetButtonWidget.active = false;
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
		super.render(context, mouseX, mouseY, hovered, deltaTicks);

		toggleButtonWidget.render(context, mouseX, mouseY, deltaTicks);
	}

	/*@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
		toggleButtonWidget.setPosition(x, y);
		toggleButtonWidget.render(context, mouseX, mouseY, tickProgress);
	}*/

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
		boolean active = toggleable.getAsBoolean() && !toggleButtonWidget.getDisableWhen().equals(dataGetter.getData());
		this.setActive(active);
	}

	@Override
	public void setActive(boolean active) {
		toggleButtonWidget.active = active;
		super.setActive(active);
		resetButtonWidget.active = active && !toggleButtonWidget.isCurrentValueDefault();
	}

	// Builder
	public static class Builder extends AbstractBuilder<Boolean> {
		private int toggleButtonWidth;
		private int toggleButtonHeight = 20;
		private ConfigBoolean variable;
		private ScrollableConfigList.AbstractConfigEntry dependency = null;
		private Object disableWhen;
		private BooleanSupplier toggleable = () -> true;

		public Builder setToggleButtonWidth(int width) {
			this.toggleButtonWidth = width;
			return this;
		}

		public Builder setToggleButtonSize(int width, int height) {
			this.toggleButtonWidth = width;
			this.toggleButtonHeight = height;
			return this;
		}

		public Builder setVariable(ConfigBoolean variable) {
			this.variable = variable;
			return this;
		}

		public Builder setToggleable(BooleanSupplier toggleable) {
			this.toggleable = toggleable;
			return this;
		}

		public <T> Builder setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependency = entry;
			this.disableWhen = disableWhen;
			return this;
		}

		@Override
		public ToggleButtonEntry build() {
			if (variable == null)
				throw new IllegalArgumentException("ToggleButtonEntry requires a variable to be set using setVariable()!");

			ToggleButtonEntry entry = new ToggleButtonEntry(
					toggleButtonWidth, toggleButtonHeight,
					variable,
					resetButtonSize,
					disableWhen,
					getTooltip,
					toggleable
			);
			if (dependency != null) {
				dependency.addObserver(entry);
				entry.onChange(dependency.getDataGetter());
			}
			return entry;
		}
	}
}