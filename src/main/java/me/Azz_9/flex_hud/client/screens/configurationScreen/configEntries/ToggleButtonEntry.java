package me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.ConfigToggleButtonWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class ToggleButtonEntry extends ScrollableConfigList.AbstractConfigEntry {
	private final ConfigToggleButtonWidget toggleButtonWidget;
	private final BooleanSupplier toggleable;

	private final List<Dependency<?>> dependencies = new ArrayList<>();

	private ToggleButtonEntry(
			int toggleButtonWidth,
			int toggleButtonHeight,
			ConfigBoolean variable,
			int resetButtonSize,
			Function<Boolean, Tooltip> getTooltip,
			BooleanSupplier toggleable
	) {
		super(resetButtonSize, Component.translatable(Objects.requireNonNull(variable.getConfigTextTranslationKey())));
		toggleButtonWidget = new ConfigToggleButtonWidget(toggleButtonWidth, toggleButtonHeight, variable, observers, getTooltip);
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
	public void setX(int x) {
		super.setX(x);
		toggleButtonWidget.setX(x);
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		toggleButtonWidget.setY(y);
	}

	@Override
	public void renderContent(@NonNull GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
		super.renderContent(graphics, mouseX, mouseY, hovered, deltaTicks);

		toggleButtonWidget.render(graphics, mouseX, mouseY, deltaTicks);
	}

	@Override
	public @NonNull List<? extends NarratableEntry> narratables() {
		return List.of(toggleButtonWidget, resetButtonWidget);
	}

	@Override
	public @NonNull List<? extends GuiEventListener> children() {
		return List.of(toggleButtonWidget, resetButtonWidget);
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
		if (!toggleable.getAsBoolean()) {
			setActive(false);
			return;
		}

		boolean shouldDisable = false;

		for (Dependency<?> dependency : dependencies) {
			Object value = dependency.entry().getDataGetter().getData();
			if (Objects.equals(value, dependency.disableWhen())) {
				shouldDisable = true;
				break;
			}
		}

		setActive(!shouldDisable);
	}

	@Override
	public void setActive(boolean active) {
		toggleButtonWidget.active = active;
		super.setActive(active);
		resetButtonWidget.active = active && !toggleButtonWidget.isCurrentValueDefault();
	}

	public <T> void addDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
		dependencies.add(new Dependency<>(entry, disableWhen));
	}

	// Builder
	public static class Builder extends AbstractBuilder<Boolean> {
		private int toggleButtonWidth;
		private int toggleButtonHeight = 20;
		private ConfigBoolean variable;
		private final List<Dependency<?>> dependencies = new ArrayList<>();
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

		public <T> Builder addDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependencies.add(new Dependency<>(entry, disableWhen));
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
					getTooltip,
					toggleable
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