package me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
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

import java.util.List;

public class CrosshairEditorEntry extends ScrollableConfigList.AbstractConfigEntry {
	private CrosshairButtonWidget<?> crosshairButtonWidget;

	public <T> CrosshairEditorEntry(
			int crosshairButtonWidth,
			int crosshairButtonHeight,
			ConfigIntGrid variable,
			int resetButtonSize,
			T disableWhen
	) {
		super(resetButtonSize, Text.translatable(variable.getConfigTextTranslationKey()));
		crosshairButtonWidget = new CrosshairButtonWidget<>(
				crosshairButtonWidth, crosshairButtonHeight,
				variable,
				observers,
				disableWhen,
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
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
		crosshairButtonWidget.setPosition(x, y);

		crosshairButtonWidget.render(context, mouseX, mouseY, tickProgress);
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
		ModulesHelper.getInstance().crosshair.crosshairTexture.updatePixels((int[][]) dataGetter.getData());

		boolean active = !crosshairButtonWidget.getDisableWhen().equals(dataGetter.getData());
		crosshairButtonWidget.active = active;
		setActive(active);
		resetButtonWidget.active = active && !crosshairButtonWidget.isCurrentValueDefault();
		// fermer l'éditeur si le button est désacitvé
		AbstractCrosshairConfigScreen screen = (AbstractCrosshairConfigScreen) MinecraftClient.getInstance().currentScreen;
		if (screen != null && !active) {
			screen.closeEditor();
		}
	}

	@Override
	public List<? extends Selectable> selectableChildren() {
		return List.of(crosshairButtonWidget, resetButtonWidget);
	}

	@Override
	public List<? extends Element> children() {
		return List.of(crosshairButtonWidget, resetButtonWidget, textWidget);
	}

	// Builder
	public static class Builder extends AbstractBuilder<int[][]> {
		private int crosshairButtonWidth;
		private int crosshairButtonHeight = 20;
		private ConfigIntGrid variable;
		private ScrollableConfigList.AbstractConfigEntry dependency = null;
		private Object disableWhen;

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

		public <T> Builder setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependency = entry;
			this.disableWhen = disableWhen;
			return this;
		}

		@Override
		public CrosshairEditorEntry build() {
			CrosshairEditorEntry entry = new CrosshairEditorEntry(
					crosshairButtonWidth, crosshairButtonHeight,
					variable,
					resetButtonSize,
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
