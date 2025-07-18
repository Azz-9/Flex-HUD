package me.Azz_9.better_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.better_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.buttons.ConfigCyclingButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class CyclingButtonEntry<E extends Enum<E> & Translatable> extends ScrollableConfigList.AbstractConfigEntry {
	private final ConfigCyclingButtonWidget<?, E> cyclingButtonWidget;

	private <T> CyclingButtonEntry(
			int cyclingButtonWidth,
			int cyclingButtonHeight,
			ConfigEnum<E> variable,
			int resetButtonSize,
			T disableWhen,
			@Nullable Function<E, Tooltip> getTooltip
	) {
		super(resetButtonSize, Text.translatable(variable.getConfigTextTranslationKey()));
		cyclingButtonWidget = new ConfigCyclingButtonWidget<>(cyclingButtonWidth, cyclingButtonHeight, variable, observers, disableWhen, getTooltip);
		setResetButtonPressAction((btn) -> cyclingButtonWidget.setToDefaultState());

		cyclingButtonWidget.addObserver(this.resetButtonWidget);
		this.resetButtonWidget.onChange(cyclingButtonWidget);
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
		resetButtonWidget.active = active && !cyclingButtonWidget.isCurrentValueDefault();
	}

	// Builder
	public static class Builder<E extends Enum<E> & Translatable> extends AbstractBuilder<E> {
		private int cyclingButtonWidth;
		private int cyclingButtonHeight = 20;
		private ConfigEnum<E> variable;
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

		public CyclingButtonEntry.Builder<E> setVariable(ConfigEnum<E> variable) {
			this.variable = variable;
			return this;
		}

		public <T> CyclingButtonEntry.Builder<E> setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependency = entry;
			this.disableWhen = disableWhen;
			return this;
		}

		@Override
		public CyclingButtonEntry<E> build() {
			if (variable == null)
				throw new IllegalArgumentException("CyclingButtonEntry requires a variable to be set using setVariable()!");

			CyclingButtonEntry<E> entry = new CyclingButtonEntry<>(
					cyclingButtonWidth, cyclingButtonHeight,
					variable,
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