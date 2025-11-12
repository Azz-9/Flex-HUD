package me.Azz_9.flex_hud.client.screens.modulesList;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.screens.AbstractSmoothScrollableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.render.RenderLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScrollableModulesList extends AbstractSmoothScrollableList<ScrollableModulesList.Entry> {

	private final List<Entry> entries = new ArrayList<>();
	private int buttonWidth;
	private int buttonHeight;
	private int iconWidthHeight;
	private int padding;
	private int columns;

	public ScrollableModulesList(MinecraftClient client, int width, int height, int top, int itemHeight, int buttonWidth, int buttonHeight, int iconWidthHeight, int padding, int columns) {
		super(client, width, height, top, itemHeight);
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.iconWidthHeight = iconWidthHeight;
		this.padding = padding;
		this.columns = columns;
	}

	/*
	 * modules' size needs to be equal or lower than number of columns
	 * */
	public void addModule(List<Module> modules) {
		assert modules.size() <= columns;
		if (modules.size() < columns) {
			for (int i = 0; i < columns - modules.size(); i++) {
				modules.add(null);
			}
		}
		Entry entry = new Entry(modules, this);
		this.entries.add(entry);
		this.addEntry(entry);
	}

	public void filterModules(String query) {
		query = query.toLowerCase().strip();
		this.clearEntries();

		List<Entry> newEntries = new ArrayList<>();
		List<Module> modulesInEntry = new ArrayList<>();

		Consumer<Module> addModuleToEntry = module -> {
			modulesInEntry.add(module);

			if (modulesInEntry.size() == columns) {
				newEntries.add(new Entry(new ArrayList<>(modulesInEntry), this));
				modulesInEntry.clear();
			}
		};

		for (Entry entry : this.entries) {
			// Vérification pour les colonnes
			for (Module module : entry.rowModules) {
				if (module != null && (module.name.toLowerCase().contains(query) || module.id.replace("_", " ").contains(query.toLowerCase()))) {
					addModuleToEntry.accept(module);
				}
			}
		}

		// Ajouter une entrée incomplète si nécessaire
		if (!modulesInEntry.isEmpty()) {
			newEntries.add(new Entry(modulesInEntry, this));
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

	public List<Entry> getEntries() {
		return this.entries;
	}

	public void setColumns(int columns) {
		this.columns = columns;
		ModulesHelper.getInstance().numberOfColumns.setValue(columns);
	}

	public static class Entry extends ElementListWidget.Entry<Entry> {
		private final List<Module> rowModules;
		private final ScrollableModulesList scrollableModulesList;

		public Entry(List<Module> rowModules, ScrollableModulesList scrollableModulesList) {
			this.rowModules = rowModules;
			this.scrollableModulesList = scrollableModulesList;
		}

		@Override
		public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int totalButtonWidth = scrollableModulesList.buttonWidth * scrollableModulesList.columns + scrollableModulesList.padding;
			int buttonX = x + (entryWidth - totalButtonWidth) / scrollableModulesList.columns;
			int iconX = buttonX + (scrollableModulesList.buttonWidth - scrollableModulesList.iconWidthHeight) / 2;

			for (int i = 0; i < rowModules.size(); i++) {
				if (rowModules.get(i) == null) {
					break;
				}

				if (i != 0) {
					buttonX = buttonX + scrollableModulesList.buttonWidth + scrollableModulesList.padding;
					iconX = buttonX + (scrollableModulesList.buttonWidth - scrollableModulesList.iconWidthHeight) / 2;
				}

				drawContext.drawTexture(RenderLayer::getGuiTextured, this.rowModules.get(i).icon, iconX, y, 0, 0,
						scrollableModulesList.iconWidthHeight, scrollableModulesList.iconWidthHeight, scrollableModulesList.iconWidthHeight, scrollableModulesList.iconWidthHeight);
				this.rowModules.get(i).button.setX(buttonX);
				this.rowModules.get(i).button.setY(y + scrollableModulesList.iconWidthHeight + scrollableModulesList.padding / 2);
				this.rowModules.get(i).button.render(drawContext, mouseX, mouseY, tickDelta);
			}
		}

		@Override
		public List<ClickableWidget> children() {
			List<ClickableWidget> clickableWidgets = new ArrayList<>();
			for (Module module : rowModules) {
				if (module != null) {
					clickableWidgets.add(module.button);
				}
			}
			return clickableWidgets;
		}

		@Override
		public List<ClickableWidget> selectableChildren() {
			return this.children();
		}
	}
}
