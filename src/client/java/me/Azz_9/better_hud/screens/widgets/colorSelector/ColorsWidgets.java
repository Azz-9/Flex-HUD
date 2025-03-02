package me.Azz_9.better_hud.screens.widgets.colorSelector;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class ColorsWidgets extends ClickableWidget {
	private int cursorX;
	private int cursorY;

	public ColorsWidgets(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {

	}
}
