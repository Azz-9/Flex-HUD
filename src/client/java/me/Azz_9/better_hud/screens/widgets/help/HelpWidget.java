package me.Azz_9.better_hud.screens.widgets.help;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class HelpWidget extends ClickableWidget {
	private final Identifier texture = Identifier.of(MOD_ID, "widgets/buttons/help/help.png");
	private boolean displayHelp = false;

	private final int BACKGROUND_COLOR = 0x000000;
	private final int TEXT_COLOR = 0xFFFFFF;
	private int alpha = 0;
	private final int TRANSITION_DURATION = 300; //ms
	private long timestamp;
	private boolean isFadingOut = false;

	public HelpWidget(int x, int y, int width, int height) {
		super(x, y, width, height, Text.of("Help button"));
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		context.drawTexture(RenderLayer::getGuiTexturedOverlay, texture, getX(), getY(), 0, 0, getWidth(), getHeight(), 20, 20);

		if (displayHelp || isFadingOut) {

			long elapsedTime = System.currentTimeMillis() - timestamp;
			float animationProgress = Math.min(1.0f, (float) elapsedTime / TRANSITION_DURATION);
			float easedProgress = 0.0f;
			if (displayHelp && !isFadingOut) {
				// Ease-Out
				easedProgress = 1 - (1 - animationProgress) * (1 - animationProgress);
			} else if (isFadingOut) {
				// Ease-Out reversed
				easedProgress = (1 - animationProgress) * (1 - animationProgress);
				if (easedProgress <= 0.0f) {
					isFadingOut = false;
				}
			}

			alpha = (int) (251 * easedProgress) + 4;

			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

			List<String> helpLines = new ArrayList<>();
			helpLines.add("CTRL + Z : undo");
			helpLines.add("CTRL + Y / CTRL + SHIFT + Z : redo");
			helpLines.add("Press SHIFT while moving an");
			helpLines.add("element to prevent snapping");
			helpLines.add("Press SHIFT while scaling an");
			helpLines.add("element to snap");

			int padding = 4;
			int marginBottom = 6;

			int lineHeight = 12;

			int popupX = getX();
			int popupY = getY() - marginBottom - lineHeight * helpLines.size() - padding;
			int popupHeight = padding + lineHeight * helpLines.size();
			int popupWidth = 0;
			for (String line : helpLines) {
				popupWidth = Math.max(popupWidth, textRenderer.getWidth(line) + padding * 2);
			}

			context.fill(popupX, popupY, popupX + popupWidth, popupY + popupHeight,  (alpha / 2 << 24) | BACKGROUND_COLOR);

			renderArrow(context, marginBottom);

			for (int i = 0; i < helpLines.size(); i++) {
				context.drawText(textRenderer, helpLines.get(i), popupX + padding, popupY + padding + lineHeight * i, (alpha << 24) | TEXT_COLOR, false);
			}
		}
	}

	private void renderArrow(DrawContext context, int marginBottom) {
		int arrowSize = 6;

		context.enableScissor(getX(), getY() - marginBottom, getRight(), getY() - marginBottom + arrowSize);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getX() + getWidth() / 2.0, getY() - marginBottom - Math.sqrt(Math.pow(arrowSize, 2) * 2) / 2, 0);
		matrices.multiply(new Quaternionf().rotationZ((float) Math.toRadians(45)));

		context.fill(0, 0, arrowSize, arrowSize, (alpha / 2 << 24) | BACKGROUND_COLOR);

		matrices.pop();

		context.disableScissor();
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		isFadingOut = displayHelp;
		timestamp = System.currentTimeMillis();
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
