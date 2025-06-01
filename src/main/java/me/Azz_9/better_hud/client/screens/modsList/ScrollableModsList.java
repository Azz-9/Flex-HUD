package me.Azz_9.better_hud.client.screens.modsList;

import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.render.RenderLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScrollableModsList extends ElementListWidget<ScrollableModsList.Entry> {

	private final List<Entry> allEntries = new ArrayList<>();
	private int buttonWidth;
	private int buttonHeight;
	private int iconWidthHeight;
	private int padding;
	private int columns;

	public ScrollableModsList(MinecraftClient client, int width, int height, int top, int itemHeight, int buttonWidth, int buttonHeight, int iconWidthHeight, int padding, int columns) {
		super(client, width, height, top, itemHeight);
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.iconWidthHeight = iconWidthHeight;
		this.padding = padding;
		this.columns = columns;
	}

	/*
	 * features' size needs to be equal or lower than number of columns
	 * */
	public void addFeature(List<Mod> mods) {
		assert mods.size() <= columns;
		if (mods.size() < columns) {
			for (int i = 0; i < columns - mods.size(); i++) {
				mods.add(null);
			}
		}
		Entry entry = new Entry(mods, this);
		this.allEntries.add(entry);
		this.addEntry(entry);
	}

	public void filterFeatures(String query) {
		query = query.toLowerCase().strip();
		this.clearEntries();

		List<Entry> newEntries = new ArrayList<>();
		List<Mod> featuresInEntry = new ArrayList<>();

		Consumer<Mod> addFeatureToEntry = mod -> {
			featuresInEntry.add(mod);

			if (featuresInEntry.size() == columns) {
				newEntries.add(new Entry(new ArrayList<>(featuresInEntry), this));
				featuresInEntry.clear();
			}
		};

		for (Entry entry : this.allEntries) {
			// Vérification pour les colonnes
			for (Mod mod : entry.rowMods) {
				if (mod != null && (mod.name.toLowerCase().contains(query) || mod.id.replace("_", " ").contains(query.toLowerCase()))) {
					addFeatureToEntry.accept(mod);
				}
			}
		}

		// Ajouter une entrée incomplète si nécessaire
		if (!featuresInEntry.isEmpty()) {
			newEntries.add(new Entry(featuresInEntry, this));
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
		this.columns = columns;
		JsonConfigHelper.getInstance().numberOfColumns = columns;
	}

	public static class Entry extends ElementListWidget.Entry<Entry> {
		private final List<Mod> rowMods;
		private final ScrollableModsList scrollableModsList;

		public Entry(List<Mod> rowMods, ScrollableModsList scrollableModsList) {
			this.rowMods = rowMods;
			this.scrollableModsList = scrollableModsList;
		}

		@Override
		public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int totalButtonWidth = scrollableModsList.buttonWidth * scrollableModsList.columns + scrollableModsList.padding;
			int buttonX = x + (entryWidth - totalButtonWidth) / scrollableModsList.columns;
			int iconX = buttonX + (scrollableModsList.buttonWidth - scrollableModsList.iconWidthHeight) / 2;

			for (int i = 0; i < rowMods.size(); i++) {
				if (rowMods.get(i) == null) {
					break;
				}

				if (i != 0) {
					buttonX = buttonX + scrollableModsList.buttonWidth + scrollableModsList.padding;
					iconX = buttonX + (scrollableModsList.buttonWidth - scrollableModsList.iconWidthHeight) / 2;
				}

				drawContext.drawTexture(RenderLayer::getGuiTextured, this.rowMods.get(i).icon, iconX, y, 0, 0,
						scrollableModsList.iconWidthHeight, scrollableModsList.iconWidthHeight, scrollableModsList.iconWidthHeight, scrollableModsList.iconWidthHeight);
				this.rowMods.get(i).button.setX(buttonX);
				this.rowMods.get(i).button.setY(y + scrollableModsList.iconWidthHeight + scrollableModsList.padding / 2);
				this.rowMods.get(i).button.render(drawContext, mouseX, mouseY, tickDelta);
			}
		}

		@Override
		public List<ClickableWidget> children() {
			List<ClickableWidget> clickableWidgets = new ArrayList<>();
			for (Mod mod : rowMods) {
				if (mod != null) {
					clickableWidgets.add(mod.button);
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
