package me.Azz_9.better_hud.Screens.ModsList;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.render.RenderLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScrollableFeatureList extends ElementListWidget<ScrollableFeatureList.Entry> {
	private final List<Entry> allEntries = new ArrayList<>();
	private static int buttonWidth;
	private static int buttonHeight;
	private static int iconWidthHeight;
	private static int padding;
	private static int columns;

	public ScrollableFeatureList(MinecraftClient client, int width, int height, int top, int itemHeight, int buttonWidth, int buttonHeight, int iconWidthHeight, int padding, int columns) {
		super(client, width, height, top, itemHeight);
		ScrollableFeatureList.buttonWidth = buttonWidth;
		ScrollableFeatureList.buttonHeight = buttonHeight;
		ScrollableFeatureList.iconWidthHeight = iconWidthHeight;
		ScrollableFeatureList.padding = padding;
		ScrollableFeatureList.columns = columns;
	}

	/*
	* features' size needs to be equal or lower than number of columns
	* */
	public void addFeature(List<Feature> features) {
		assert features.size() <= columns;
		if (features.size() < columns) {
			for (int i = 0; i < columns - features.size(); i++) {
				features.add(null);
			}
		}
		Entry entry = new Entry(features);
		this.allEntries.add(entry);
		this.addEntry(entry);
	}

	public void filterFeatures(String query) {
		this.clearEntries();

		List<Entry> newEntries = new ArrayList<>();
		List<Feature> featuresInEntry = new ArrayList<>();

		Consumer<Feature> addFeatureToEntry = feature -> {
			featuresInEntry.add(feature);

			if (featuresInEntry.size() == columns) {
				newEntries.add(new Entry(new ArrayList<>(featuresInEntry)));
				featuresInEntry.clear();
			}
		};

		for (Entry entry : this.allEntries) {
			// Vérification pour les colonnes
			for (Feature feature : entry.rowMods) {
				if (feature != null && feature.name.toLowerCase().contains(query.toLowerCase())) {
					addFeatureToEntry.accept(feature);
				}
			}
		}

		// Ajouter une entrée incomplète si nécessaire
		if (!featuresInEntry.isEmpty()) {
			newEntries.add(new Entry(featuresInEntry));
		}

		for (Entry entry : newEntries) {
			this.addEntry(entry);
		}

		updateScroll();

	}

	public void updateScroll() {
		if (this.getScrollY() > this.getMaxScrollY()) {
			this.setScrollY(this.getMaxScrollY());
		}
	}

	@Override
	public int getRowWidth() {
		return buttonWidth * columns + padding * (columns - 1);
	}

	public int getButtonWidth() {
		return buttonWidth;
	}

	public int getButtonHeight() {
        return buttonHeight;
    }

	public List<Entry> getAllEntries() {
		return this.allEntries;
	}

	public void setColumns(int columns) {
		ScrollableFeatureList.columns = columns;
		ModConfig.getInstance().numberOfColumns = columns;
	}

	public static class Entry extends ElementListWidget.Entry<Entry> {
		private final List<Feature> rowMods;

		public Entry(List<Feature> rowMods) {
			this.rowMods = rowMods;
		}

		@Override
		public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int totalButtonWidth = buttonWidth * columns + padding;
			int buttonX = x + (entryWidth - totalButtonWidth) / columns;
			int iconX = buttonX + (buttonWidth - iconWidthHeight) / 2;

			for (int i = 0; i < rowMods.size(); i++) {
				if (rowMods.get(i) == null) {
					break;
				}

				if (i != 0) {
					buttonX = buttonX + buttonWidth + padding;
					iconX = buttonX + (buttonWidth - iconWidthHeight) / 2;
				}

				drawContext.drawTexture(RenderLayer::getGuiTexturedOverlay, this.rowMods.get(i).icon, iconX, y, 0, 0, iconWidthHeight, iconWidthHeight, iconWidthHeight, iconWidthHeight);
				this.rowMods.get(i).button.setX(buttonX);
				this.rowMods.get(i).button.setY(y + iconWidthHeight + padding / 2);
				this.rowMods.get(i).button.render(drawContext, mouseX, mouseY, tickDelta);
			}
		}

		@Override
		public List<ClickableWidget> children() {
			List<ClickableWidget> clickableWidgets = new ArrayList<>();
			for (Feature feature : rowMods) {
				if (feature != null) {
					clickableWidgets.add(feature.button);
				}
			}
			return clickableWidgets;
		}

		@Override
		public List<ClickableWidget> selectableChildren() {
			return this.children(); // Retourne les enfants sélectionnables (également le bouton ici)
		}
	}
}