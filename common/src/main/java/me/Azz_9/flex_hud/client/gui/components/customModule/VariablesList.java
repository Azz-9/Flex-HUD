package me.Azz_9.flex_hud.client.gui.components.customModule;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import me.Azz_9.flex_hud.client.gui.components.AbstractSmoothScrollableList;
import me.Azz_9.flex_hud.client.modules.customModules.Variable;
import me.Azz_9.flex_hud.client.modules.customModules.Variables;

public class VariablesList extends AbstractSmoothScrollableList<VariablesList.Entry> {

	private static final int ENTRY_GAP = 2;
	private static final int THUMB_WIDTH = 6;
	private static final int THUMB_PADDING = 2;

	public VariablesList(int x, int y, int height) {
		super(MINECRAFT, 100, height, y, VariableWidget.HEIGHT + ENTRY_GAP);
		setX(x);

		int maxWidth = 0;
		for (Variable<?> variable : Variables.getAllVariables().values()) {
			Entry entry = new Entry(variable);
			addEntry(entry);
			maxWidth = Math.max(maxWidth, entry.widget.getWidth() + Entry.PADDING * 2);
		}

		setWidth(maxWidth + THUMB_WIDTH + THUMB_PADDING * 2);
	}

	public void setOnVariableClick(Consumer<Variable<?>> onVariableClick) {
		for (Entry entry : children()) {
			entry.setOnVariableClick(onVariableClick);
		}
	}

	public void search(String query) {
		String finalQuery = query.toLowerCase().strip();
		super.clearEntries();

		for (Variable<?> variable : Variables.getAllVariables().values()) {
			if (variable.getKey().toLowerCase().contains(finalQuery) ||
					variable.getKey().toLowerCase().replace(".", "").contains(finalQuery) ||
					variable.getName().getString().toLowerCase().contains(finalQuery) ||
					variable.getDescription().getString().toLowerCase().contains(finalQuery)) {

				addEntry(new Entry(variable));
			}
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
		return getWidth();
	}

	@Override
	protected int scrollBarX() {
		return getRight() - THUMB_WIDTH - THUMB_PADDING;
	}

	public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

		private static final int PADDING = 1;

		private final ClickableVariableWidget widget;

		public Entry(Variable<?> variable) {
			this.widget = new ClickableVariableWidget(getX() + PADDING, getY() + PADDING, variable);
		}

		public void setOnVariableClick(Consumer<Variable<?>> onVariableClick) {
			widget.setOnClick(onVariableClick);
		}

		@Override
		public void setY(int y) {
			super.setY(y);
			widget.setY(y + PADDING);
		}

		@Override
		public void setX(int x) {
			super.setX(x);
			widget.setX(x + PADDING);
		}

		@Override
		public void renderContent(@NotNull GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			widget.render(graphics, mouseX, mouseY, deltaTicks);
		}

		@Override
		public @NotNull List<AbstractWidget.WithInactiveMessage> narratables() {
			return children();
		}

		@Override
		public @NotNull List<AbstractWidget.WithInactiveMessage> children() {
			return List.of(widget);
		}
	}
}
