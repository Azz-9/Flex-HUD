package me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.ConfigColorButtonWidget;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorSelector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class ColorButtonEntry extends ScrollableConfigList.AbstractConfigEntry {
	private ConfigColorButtonWidget<?> colorButtonWidget;

	private <T> ColorButtonEntry(
			int colorButtonWidth,
			int colorButtonHeight,
			ConfigInteger variable,
			int resetButtonSize,
			T disableWhen,
			@Nullable Function<Integer, Tooltip> getTooltip
	) {
		super(resetButtonSize, Text.translatable(variable.getConfigTextTranslationKey()));
		colorButtonWidget = new ConfigColorButtonWidget<>(colorButtonWidth, colorButtonHeight, variable, observers, disableWhen,
				(btn) -> {
					AbstractConfigurationScreen screen = (AbstractConfigurationScreen) MinecraftClient.getInstance().currentScreen;
					if (screen != null) {
						ColorSelector colorSelector = screen.getColorSelector();
						if (colorSelector == null || !colorSelector.isFocused()) {
							screen.openColorSelector(this.colorButtonWidget);
						} else {
							screen.closeColorSelector();
						}
					}
				}, getTooltip);
		setResetButtonPressAction((btn) -> colorButtonWidget.setToDefaultState());

		colorButtonWidget.addObserver(this.resetButtonWidget);
		this.resetButtonWidget.onChange(colorButtonWidget);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
		super.render(context, mouseX, mouseY, hovered, deltaTicks);

		colorButtonWidget.render(context, mouseX, mouseY, deltaTicks);
	}

	/*@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
		colorButtonWidget.setPosition(x, y);

		colorButtonWidget.render(context, mouseX, mouseY, tickProgress);
	}*/

	@Override
	public List<? extends Selectable> selectableChildren() {
		return List.of(colorButtonWidget, resetButtonWidget);
	}

	@Override
	public List<? extends Element> children() {
		return List.of(colorButtonWidget, resetButtonWidget, textWidget);
	}

	@Override
	public TrackableChange getTrackableChangeWidget() {
		return colorButtonWidget;
	}

	@Override
	public DataGetter<?> getDataGetter() {
		return colorButtonWidget;
	}

	@Override
	public void onChange(DataGetter<?> dataGetter) {
		boolean active = !colorButtonWidget.getDisableWhen().equals(dataGetter.getData());
		colorButtonWidget.active = active;
		setActive(active);
		resetButtonWidget.active = active && !colorButtonWidget.isCurrentValueDefault();
		// fermer le color selector si le color button est désacitvé
		AbstractConfigurationScreen screen = (AbstractConfigurationScreen) MinecraftClient.getInstance().currentScreen;
		if (screen != null && !active) {
			screen.closeColorSelector();
		}
	}

	//Builder
	public static class Builder extends AbstractBuilder<Integer> {
		private int colorButtonWidth;
		private int colorButtonHeight = 20;
		private ConfigInteger variable;
		private ScrollableConfigList.AbstractConfigEntry dependency = null;
		private Object disableWhen;

		public ColorButtonEntry.Builder setColorButtonWidth(int width) {
			this.colorButtonWidth = width;
			return this;
		}

		public ColorButtonEntry.Builder setColorButtonSize(int width, int height) {
			this.colorButtonWidth = width;
			this.colorButtonHeight = height;
			return this;
		}

		public ColorButtonEntry.Builder setVariable(ConfigInteger variable) {
			this.variable = variable;
			return this;
		}

		public <T> ColorButtonEntry.Builder setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependency = entry;
			this.disableWhen = disableWhen;
			return this;
		}

		@Override
		public ColorButtonEntry build() {
			if (variable == null)
				throw new IllegalArgumentException("ColorButtonEntry requires a variable to be set using setVariable()!");

			ColorButtonEntry entry = new ColorButtonEntry(
					colorButtonWidth, colorButtonHeight,
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