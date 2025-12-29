package me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.ConfigColorButtonWidget;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorSelector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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
					AbstractConfigurationScreen screen = (AbstractConfigurationScreen) Minecraft.getInstance().screen;
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
	public void setX(int x) {
		super.setX(x);
		colorButtonWidget.setX(x);
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		colorButtonWidget.setY(y);
	}

	@Override
	public void renderContent(@NonNull GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
		super.renderContent(graphics, mouseX, mouseY, hovered, deltaTicks);

		colorButtonWidget.render(graphics, mouseX, mouseY, deltaTicks);
	}

	@Override
	public @NonNull List<? extends NarratableEntry> narratables() {
		return List.of(colorButtonWidget, resetButtonWidget);
	}

	@Override
	public @NonNull List<? extends GuiEventListener> children() {
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
		AbstractConfigurationScreen screen = (AbstractConfigurationScreen) Minecraft.getInstance().screen;
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
			for (Dependency<?> dependency : dependencies) {
				entry.addDependency(dependency.entry(), dependency.disableWhen());
				dependency.entry().addObserver(entry);
				entry.onChange(dependency.entry().getDataGetter());
			}
			return entry;
		}
	}
}