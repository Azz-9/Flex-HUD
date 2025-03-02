package me.Azz_9.better_hud.screens.widgets.help;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class HelpWidget extends ClickableWidget {
	private final Identifier texture = Identifier.of(MOD_ID, "widgets/buttons/help/help.png");
	private boolean displayHelp = false;

	public HelpWidget(int x, int y, int width, int height) {
		super(x, y, width, height, Text.of("Help button"));
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		context.drawTexture(RenderLayer::getGuiTexturedOverlay, texture, getX(), getY(), 0, 0, getWidth(), getHeight(), 20, 20);

		if (displayHelp) {
			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

			List<String> helpLines = new ArrayList<>();
			helpLines.add("CTRL + Z : undo");
			helpLines.add("CTRL + Y / CTRL + SHIFT + Z : redo");
			helpLines.add("Press SHIFT while dragging to");
			helpLines.add("prevent snapping");

			int padding = 4;
			int marginBottom = 2;

			int lineHeight = 12;

			int popupX = getX();
			int popupY = getY() - marginBottom - lineHeight * helpLines.size() - padding;
			int popupHeight = padding + lineHeight * helpLines.size();
			int popupWidth = 0;
			for (String line : helpLines) {
				popupWidth = Math.max(popupWidth, textRenderer.getWidth(line)) + padding;
			}

			context.fill(popupX, popupY, popupX + popupWidth, popupY + popupHeight, 0x7F000000);

            for (int i = 0; i < helpLines.size(); i++) {
				context.drawText(textRenderer, helpLines.get(i), popupX + padding, popupY + padding + lineHeight * i, 0xFFFFFFFF, false);
			}
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		displayHelp = !displayHelp;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	public boolean getDisplayHelp() {
		return displayHelp;
	}

	public void setDisplayHelp(boolean displayHelp) {
		this.displayHelp = displayHelp;
	}
}
