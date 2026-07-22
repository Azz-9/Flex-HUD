package me.Azz_9.flex_hud.client.gui.components.config.entries;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.config.option.ConfigInteger;
import me.Azz_9.flex_hud.client.gui.components.TrackableChange;
import me.Azz_9.flex_hud.client.gui.components.config.DataGetter;
import me.Azz_9.flex_hud.client.gui.components.config.Observer;
import me.Azz_9.flex_hud.client.gui.components.config.ScrollableConfigList;
import me.Azz_9.flex_hud.client.gui.components.config.buttons.ConfigColorButtonWidget;
import me.Azz_9.flex_hud.client.gui.components.config.colorSelector.ColorSelector;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;

public class ColorButtonEntry extends ScrollableConfigList.AbstractConfigEntry {
	private ConfigColorButtonWidget colorButtonWidget;

	private final List<Dependency<?>> dependencies = new ArrayList<>();

	private ColorButtonEntry(
			int colorButtonWidth,
			int colorButtonHeight,
			ConfigInteger variable,
			int resetButtonSize,
			@Nullable Function<Integer, Tooltip> getTooltip
	) {
		super(resetButtonSize, Component.translatable(Objects.requireNonNull(variable.getConfigTextTranslationKey())));
		colorButtonWidget = new ConfigColorButtonWidget(colorButtonWidth, colorButtonHeight, variable, observers,
				(btn) -> {
					AbstractConfigurationScreen screen = (AbstractConfigurationScreen) MINECRAFT.screen;
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
	public void render(@NotNull GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
		super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);
		colorButtonWidget.setPosition(x, y);

		colorButtonWidget.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public @NotNull List<? extends NarratableEntry> narratables() {
		return List.of(colorButtonWidget, resetButtonWidget);
	}

	@Override
	public @NotNull List<? extends GuiEventListener> children() {
		return List.of(colorButtonWidget, resetButtonWidget);
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
		boolean shouldDisable = false;

		for (Dependency<?> dependency : dependencies) {
			Object value = dependency.entry().getDataGetter().getData();
			if (Objects.equals(value, dependency.disableWhen())) {
				shouldDisable = true;
				break;
			}
		}

		setActive(!shouldDisable);
		// fermer le color selector si le color button est désacitvé
		AbstractConfigurationScreen screen = (AbstractConfigurationScreen) MINECRAFT.screen;
		if (screen != null && shouldDisable) {
			screen.closeColorSelector();
		}
	}

	@Override
	public void setActive(boolean active) {
		colorButtonWidget.active = active;
		super.setActive(active);
		resetButtonWidget.active = active && !colorButtonWidget.isCurrentValueDefault();
	}

	public <T> void addDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
		dependencies.add(new Dependency<>(entry, disableWhen));
	}

	//Builder
	public static class Builder extends AbstractBuilder<Integer> {
		private int colorButtonWidth;
		private int colorButtonHeight = 20;
		private ConfigInteger variable;
		private final List<Dependency<?>> dependencies = new ArrayList<>();

		public Builder setColorButtonWidth(int width) {
			this.colorButtonWidth = width;
			return this;
		}

		public Builder setColorButtonSize(int width, int height) {
			this.colorButtonWidth = width;
			this.colorButtonHeight = height;
			return this;
		}

		public Builder setVariable(ConfigInteger variable) {
			this.variable = variable;
			return this;
		}

		public <T> Builder addDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependencies.add(new Dependency<>(entry, disableWhen));
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
					getTooltip
			);
			for (Observer observer : observers) {
				entry.addObserver(observer);
			}
			for (Dependency<?> dependency : dependencies) {
				entry.addDependency(dependency.entry(), dependency.disableWhen());
				dependency.entry().addObserver(entry);
				entry.onChange(dependency.entry().getDataGetter());
			}
			return entry;
		}
	}
}