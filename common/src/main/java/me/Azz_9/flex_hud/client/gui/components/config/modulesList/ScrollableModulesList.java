package me.Azz_9.flex_hud.client.gui.components.config.modulesList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.renderer.RenderPipelines;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import me.Azz_9.flex_hud.client.gui.components.AbstractSmoothScrollableList;
import me.Azz_9.flex_hud.client.modules.Modules;

public class ScrollableModulesList extends AbstractSmoothScrollableList<ScrollableModulesList.Entry> {

	private final List<Entry> entries = new ArrayList<>();
	private final int buttonWidth;
	private final int buttonHeight;
	private final int iconWidthHeight;
	private final int padding;
	private int columns;

	public ScrollableModulesList(Minecraft minecraft, int width, int height, int top, int itemHeight, int buttonWidth, int buttonHeight, int iconWidthHeight, int padding, int columns) {
		super(minecraft, width, height, top, itemHeight);
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.iconWidthHeight = iconWidthHeight;
		this.padding = padding;
		this.columns = columns;
	}

	/*
	 * modules' size needs to be equal or lower than number of columns
	 * */
	public void addModule(List<ModuleElement> moduleElements) {
		assert moduleElements.size() <= columns;
		if (moduleElements.size() < columns) {
			for (int i = 0; i < columns - moduleElements.size(); i++) {
				moduleElements.add(null);
			}
		}
		Entry entry = new Entry(moduleElements, this);
		this.entries.add(entry);
		this.addEntry(entry);
	}

	public void filterModules(String query) {
		String finalQuery = query.toLowerCase().strip();
		super.clearEntries();

		List<Entry> newEntries = new ArrayList<>();
		List<ModuleElement> modulesInEntry = new ArrayList<>(); // modules in current entry

		Consumer<ModuleElement> addModuleToEntry = moduleElement -> {
			modulesInEntry.add(moduleElement);

			if (modulesInEntry.size() == columns) {
				newEntries.add(new Entry(new ArrayList<>(modulesInEntry), this));
				modulesInEntry.clear();
			}
		};

		for (Entry entry : this.entries) {
			// Vérification pour les colonnes
			for (ModuleElement moduleElement : entry.rowModules) {
				if (moduleElement != null && moduleElement.keywords.stream().anyMatch(keyword -> keyword.contains(finalQuery))) {
					addModuleToEntry.accept(moduleElement);
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
		if (this.scrollAmount() > this.maxScrollAmount()) {
			this.setScrollAmount(this.maxScrollAmount());
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

	public void setColumns(int columns) {
		this.columns = columns;
		Modules.getInstance().numberOfColumns.setValue(columns);
	}

	public List<Entry> getEntries() {
		return entries;
	}

	@Override
	public void clearEntries() {
		super.clearEntries();
		this.entries.clear();
	}

	public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
		private final List<ModuleElement> rowModules;
		private final ScrollableModulesList scrollableModulesList;

		public Entry(List<ModuleElement> rowModules, ScrollableModulesList scrollableModulesList) {
			this.rowModules = rowModules;
			this.scrollableModulesList = scrollableModulesList;
		}

		@Override
		public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			int totalButtonWidth = scrollableModulesList.buttonWidth * scrollableModulesList.columns + scrollableModulesList.padding;
			int buttonX = getX() + (getWidth() - totalButtonWidth) / scrollableModulesList.columns;
			int iconX = buttonX + (scrollableModulesList.buttonWidth - scrollableModulesList.iconWidthHeight) / 2;

			for (int i = 0; i < rowModules.size(); i++) {
				if (rowModules.get(i) == null) {
					break;
				}

				if (i != 0) {
					buttonX = buttonX + scrollableModulesList.buttonWidth + scrollableModulesList.padding;
					iconX = buttonX + (scrollableModulesList.buttonWidth - scrollableModulesList.iconWidthHeight) / 2;
				}

				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.rowModules.get(i).icon, iconX, getY(),
						scrollableModulesList.iconWidthHeight, scrollableModulesList.iconWidthHeight);
				this.rowModules.get(i).setButtonX(buttonX);
				this.rowModules.get(i).setButtonY(getY() + scrollableModulesList.iconWidthHeight + scrollableModulesList.padding / 2);
				this.rowModules.get(i).renderButton(graphics, mouseX, mouseY, deltaTicks);
			}
		}

		@Override
		public @NonNull List<AbstractWidget.WithInactiveMessage> children() {
			List<AbstractWidget.WithInactiveMessage> clickableWidgets = new ArrayList<>();
			for (ModuleElement moduleElement : rowModules) {
				if (moduleElement != null) {
					clickableWidgets.addAll(moduleElement.buttons());
				}
			}
			return clickableWidgets;
		}

		@Override
		public @NonNull List<AbstractWidget.WithInactiveMessage> narratables() {
			return this.children();
		}
	}
}
