package me.Azz_9.better_hud.screens.modsConfigScreen;

import me.Azz_9.better_hud.screens.widgets.buttons.ResetButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextWidget;

import java.util.ArrayList;
import java.util.List;

public class ScrollableConfigList extends ElementListWidget<ScrollableConfigList.ButtonEntry> {

	public static class ButtonEntry extends ElementListWidget.Entry<ButtonEntry> {
		ClickableWidget configButton;
		ResetButtonWidget resetButtonWidget;
		TextWidget textWidget;

		private int buttonWidth, buttonHeight;
		private int textMarginLeft = 6;
		private int gap = 10; // gap between the button and the reset button

		public ButtonEntry(ClickableWidget configButton, ResetButtonWidget resetButtonWidget, TextWidget textWidget, int buttonWidth, int buttonHeight) {
			this.configButton = configButton;
			this.resetButtonWidget = resetButtonWidget;
			this.textWidget = textWidget;

			this.buttonWidth = buttonWidth;
			this.buttonHeight = buttonHeight;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.configButton.setPosition(x, y);
			this.resetButtonWidget.setPosition(x + entryWidth - 20, y);
			this.textWidget.setPosition(x + textMarginLeft, (int) (y + buttonHeight / 2.0 - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0));


			this.configButton.render(context, mouseX, mouseY, tickDelta);
			this.resetButtonWidget.render(context, mouseX, mouseY, tickDelta);
			this.textWidget.render(context, mouseX, mouseY, tickDelta);
		}

		@Override
		public List<ClickableWidget> selectableChildren() {
			return this.children();
		}

		@Override
		public List<ClickableWidget> children() {
			return List.of(this.configButton, this.resetButtonWidget);
		}
	}

	private final List<ButtonEntry> buttonsList = new ArrayList<>();
	private final int ITEM_WIDTH;

	public ScrollableConfigList(MinecraftClient client, int width, int height, int y, int x, int itemHeight, int itemWidth) {
		super(client, width, height, y, itemHeight);
		setX(x);
		this.ITEM_WIDTH = itemWidth;
	}

	public void addButton(ButtonEntry buttonEntry) {
		this.buttonsList.add(buttonEntry);
		this.addEntry(buttonEntry);
	}

	@Override
	public int getRowWidth() {
		return ITEM_WIDTH;
	}
}