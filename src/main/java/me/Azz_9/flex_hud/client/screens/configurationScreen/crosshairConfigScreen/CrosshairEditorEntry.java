package me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigIntGrid;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor.CrosshairEditor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CrosshairEditorEntry extends ScrollableConfigList.AbstractConfigEntry {
	private CrosshairButtonWidget<?> crosshairButtonWidget;

	private final List<Dependency<?>> dependencies = new ArrayList<>();

	public CrosshairEditorEntry(
			int crosshairButtonWidth,
			int crosshairButtonHeight,
			ConfigIntGrid variable,
			int resetButtonSize
	) {
		super(resetButtonSize, Text.translatable(variable.getConfigTextTranslationKey()));
		crosshairButtonWidget = new CrosshairButtonWidget<>(
				crosshairButtonWidth, crosshairButtonHeight,
				variable,
				observers,
				(btn) -> {
					if (MinecraftClient.getInstance().currentScreen instanceof AbstractCrosshairConfigScreen crosshairConfigScreen) {
						CrosshairEditor crosshairEditor = crosshairConfigScreen.getCrosshairEditor();
						if (crosshairEditor == null || !crosshairEditor.isFocused()) {
							crosshairConfigScreen.openEditor(this.crosshairButtonWidget);
						} else {
							crosshairConfigScreen.closeEditor();
						}
					}
				}
		);
		setResetButtonPressAction((btn) -> crosshairButtonWidget.setToDefaultState());

		crosshairButtonWidget.addObserver(this.resetButtonWidget);
		this.resetButtonWidget.onChange(crosshairButtonWidget);
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		crosshairButtonWidget.setX(x);
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		crosshairButtonWidget.setY(y);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
		super.render(context, mouseX, mouseY, hovered, deltaTicks);

		crosshairButtonWidget.render(context, mouseX, mouseY, deltaTicks);
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
		AbstractCrosshairConfigScreen screen = (AbstractCrosshairConfigScreen) MinecraftClient.getInstance().currentScreen;
		if (screen != null && shouldDisable) {
			screen.closeEditor();
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
	public List<? extends Selectable> selectableChildren() {
		return List.of(crosshairButtonWidget, resetButtonWidget);
	}

	@Override
	public List<? extends Element> children() {
		return List.of(crosshairButtonWidget, resetButtonWidget);
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
