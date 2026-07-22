package me.Azz_9.flex_hud.client.gui.components.config.entries;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.Azz_9.flex_hud.client.config.option.ConfigIntGrid;
import me.Azz_9.flex_hud.client.gui.components.TrackableChange;
import me.Azz_9.flex_hud.client.gui.components.config.DataGetter;
import me.Azz_9.flex_hud.client.gui.components.config.ScrollableConfigList;
import me.Azz_9.flex_hud.client.gui.components.config.buttons.CrosshairButtonWidget;
import me.Azz_9.flex_hud.client.gui.components.config.crosshairEditor.CrosshairEditor;
import me.Azz_9.flex_hud.client.gui.screens.AbstractPopupScreen;

public class CrosshairEditorEntry extends ScrollableConfigList.AbstractConfigEntry {
	private @NotNull CrosshairButtonWidget<?> crosshairButtonWidget;
	private @Nullable CrosshairEditor crosshairEditor;

	private final List<Dependency<?>> dependencies = new ArrayList<>();

	public CrosshairEditorEntry(
			int crosshairButtonWidth,
			int crosshairButtonHeight,
			ConfigIntGrid variable,
			int resetButtonSize
	) {
		super(resetButtonSize, Component.translatable(Objects.requireNonNull(variable.getConfigTextTranslationKey())));
		crosshairButtonWidget = new CrosshairButtonWidget<>(
				crosshairButtonWidth, crosshairButtonHeight,
				variable,
				observers,
				(btn) -> {
					if (MINECRAFT.screen instanceof AbstractPopupScreen popupScreen) {
						if (crosshairEditor == null) {
							crosshairEditor = new CrosshairEditor(crosshairButtonWidget);
						}

						if (!crosshairEditor.isFocused()) {
							openEditor(popupScreen);
						} else {
							closeEditor(popupScreen);
						}
					}
				}
		);
		setResetButtonPressAction((btn) -> crosshairButtonWidget.setToDefaultState());

		crosshairButtonWidget.addObserver(this.resetButtonWidget);
		this.resetButtonWidget.onChange(crosshairButtonWidget);
	}

	@Override
	public void render(@NotNull GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
		super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);
		crosshairButtonWidget.setPosition(x, y);

		crosshairButtonWidget.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public TrackableChange getTrackableChangeWidget() {
		return crosshairButtonWidget;
	}

	@Override
	public DataGetter<?> getDataGetter() {
		return crosshairButtonWidget;
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
		// fermer l'éditeur si le button est désacitvé
		AbstractPopupScreen screen = (AbstractPopupScreen) MINECRAFT.screen;
		if (screen != null && shouldDisable) {
			closeEditor(screen);
		}
	}

	@Override
	public void setActive(boolean active) {
		crosshairButtonWidget.active = active;
		super.setActive(active);
		resetButtonWidget.active = active && !crosshairButtonWidget.isCurrentValueDefault();
	}

	public <T> void addDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
		dependencies.add(new Dependency<>(entry, disableWhen));
	}

	@Override
	public @NotNull List<? extends NarratableEntry> narratables() {
		return List.of(crosshairButtonWidget, resetButtonWidget);
	}

	@Override
	public @NotNull List<? extends GuiEventListener> children() {
		return List.of(crosshairButtonWidget, resetButtonWidget);
	}

	private void closeEditor(AbstractPopupScreen screen) {
		screen.closePopup();
		if (crosshairEditor != null) crosshairEditor.setFocused(false);
	}

	private void openEditor(AbstractPopupScreen screen) {
		screen.setPopupWidget(crosshairEditor);
		if (crosshairEditor != null) {
			crosshairEditor.setFocused(true);
			crosshairEditor.updateTexture(crosshairButtonWidget.getData());
		}
	}

	// Builder
	public static class Builder extends AbstractBuilder<int[][]> {
		private int crosshairButtonWidth;
		private int crosshairButtonHeight = 20;
		private ConfigIntGrid variable;
		private final List<Dependency<?>> dependencies = new ArrayList<>();

		public Builder setColorButtonWidth(int width) {
			this.crosshairButtonWidth = width;
			return this;
		}

		public Builder setColorButtonSize(int width, int height) {
			this.crosshairButtonWidth = width;
			this.crosshairButtonHeight = height;
			return this;
		}

		public Builder setVariable(ConfigIntGrid variable) {
			this.variable = variable;
			return this;
		}

		public <T> Builder addDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependencies.add(new Dependency<>(entry, disableWhen));
			return this;
		}

		@Override
		public CrosshairEditorEntry build() {
			if (variable == null)
				throw new IllegalArgumentException("CrosshairEditorEntry requires a variable to be set using setVariable()!");

			CrosshairEditorEntry entry = new CrosshairEditorEntry(
					crosshairButtonWidth, crosshairButtonHeight,
					variable,
					resetButtonSize
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
