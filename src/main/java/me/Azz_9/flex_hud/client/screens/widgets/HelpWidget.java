package me.Azz_9.flex_hud.client.screens.widgets;

import me.Azz_9.flex_hud.client.utils.Cursors;
import me.Azz_9.flex_hud.client.utils.EaseUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class HelpWidget extends AbstractWidget.WithInactiveMessage {
	private final Identifier texture = Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/help/help.png");
	private boolean displayHelp = false;

	private final int BACKGROUND_COLOR = 0x000000;
	private final int TEXT_COLOR = 0xFFFFFF;
	private int alpha = 0;
	private final int TRANSITION_DURATION = 300; //ms
	private long timestamp;
	private boolean isFadingOut = false;

	private final Component[] helpLines;

	public HelpWidget(int x, int y, int width, int height, Component[] helpLines) {
		super(x, y, width, height, Component.translatable("flex_hud.help_widget"));
		this.helpLines = helpLines;
	}

	@Override
	protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (this.isHovered() && this.isActive()) {
			graphics.requestCursor(Cursors.POINTING_HAND);
		}

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, 0, getWidth(), getHeight(), 20, 20);

		if (displayHelp || isFadingOut) {

			long elapsedTime = System.currentTimeMillis() - timestamp;
			float easedProgress = 1.0f;
			if (displayHelp && !isFadingOut && elapsedTime < TRANSITION_DURATION) {
				// Ease-Out
				easedProgress = EaseUtils.getEaseOutQuad(elapsedTime / (float) TRANSITION_DURATION);
			} else if (isFadingOut) {
				// Ease-Out reversed
				easedProgress = -EaseUtils.getEaseOutQuad(Math.min(1.0f, elapsedTime / (float) TRANSITION_DURATION)) + 1;
				if (easedProgress <= 0.0f) {
					isFadingOut = false;
				}
			}

			alpha = (int) (251 * easedProgress) + 4;

			Font font = Minecraft.getInstance().font;

			int padding = 4;
			int marginBottom = 6;

			int popupX = getX();
			int popupY;
			int popupHeight = padding;
			int popupWidth = 200;
			int textWidth = popupWidth - padding * 2;
			int lineSpacing = 3;
			for (Component line : helpLines) {
				popupHeight += font.wordWrapHeight(line, textWidth);
				popupHeight += lineSpacing;
			}

			popupY = getY() - marginBottom - popupHeight;

			graphics.fill(popupX, popupY, popupX + popupWidth, popupY + popupHeight, (alpha / 2 << 24) | BACKGROUND_COLOR);

			renderArrow(graphics, marginBottom);

			int textY = popupY + padding;
			for (Component helpLine : helpLines) {
				graphics.drawWordWrap(font, helpLine, popupX + padding, textY, textWidth, (alpha << 24) | TEXT_COLOR, false);
				textY += font.wordWrapHeight(helpLine, textWidth) + lineSpacing;
			}
		}
	}

	private void renderArrow(GuiGraphics graphics, int marginBottom) {
		int arrowSize = 6;

		graphics.enableScissor(getX(), getY() - marginBottom, getRight(), getY() - marginBottom + arrowSize);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate((float) (getX() + getWidth() / 2.0), (float) (getY() - marginBottom - Math.sqrt(Math.pow(arrowSize, 2) * 2) / 2));
		matrices.rotate((float) Math.toRadians(45));

		graphics.fill(0, 0, arrowSize, arrowSize, (alpha / 2 << 24) | BACKGROUND_COLOR);

		matrices.popMatrix();

		graphics.disableScissor();
	}

	@Override
	public void onClick(@NonNull MouseButtonEvent click, boolean bl) {
		isFadingOut = displayHelp;
		timestamp = System.currentTimeMillis();
		displayHelp = !displayHelp;
	}

	public boolean getDisplayHelp() {
		return displayHelp;
	}

	public void setDisplayHelp(boolean displayHelp) {
		this.displayHelp = displayHelp;
	}

	@Override
	protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
	}
}
