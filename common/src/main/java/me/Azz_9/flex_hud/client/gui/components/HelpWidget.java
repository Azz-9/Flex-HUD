package me.Azz_9.flex_hud.client.gui.components;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.Constants.MOD_ID;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.Cursors;
import me.Azz_9.flex_hud.utils.Ease;

public class HelpWidget extends AbstractWidget {
	private static final ResourceLocation SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "widget/help");
	private static final int POPUP_WIDTH = 200;
	private static final int POPUP_PADDING = 4;
	private static final int POPUP_MARGIN_BOTTOM = 6;
	private static final int LINE_SPACING = 3;

	private boolean displayHelp = false;

	private static final int BACKGROUND_COLOR = Colors.BLACK;
	private static final int TEXT_COLOR = Colors.WHITE;
	private static final int TRANSITION_DURATION = 300; //ms
	private long timestamp;
	private boolean isFadingOut = false;

	private final Component[] helpLines;

	public HelpWidget(int x, int y, int width, int height, Component[] helpLines) {
		super(x, y, width, height, Component.translatable("flex_hud.help_widget"));
		this.helpLines = helpLines;
	}

	@Override
	protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (this.isHovered() && this.isActive()) {
			graphics.requestCursor(Cursors.POINTING_HAND);
		}

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE, getX(), getY(), getWidth(), getHeight());

		if (displayHelp || isFadingOut) {

			long elapsedTime = System.currentTimeMillis() - timestamp;
			float easedProgress = 1.0f;
			if (displayHelp && !isFadingOut && elapsedTime < TRANSITION_DURATION) {
				// Ease-Out
				easedProgress = Ease.outQuad(elapsedTime / (float) TRANSITION_DURATION);
			} else if (isFadingOut) {
				// Ease-Out reversed
				easedProgress = -Ease.outQuad(Math.min(1.0f, elapsedTime / (float) TRANSITION_DURATION)) + 1;
				if (easedProgress <= 0.0f) {
					isFadingOut = false;
				}
			}

			Font font = MINECRAFT.font;

			int popupX = getX();
			int popupY;
			int popupHeight = POPUP_PADDING;
			int textWidth = POPUP_WIDTH - POPUP_PADDING * 2;
			for (Component line : helpLines)
				popupHeight += font.wordWrapHeight(line, textWidth) + LINE_SPACING;

			popupY = getY() - POPUP_MARGIN_BOTTOM - popupHeight;

			graphics.fill(popupX, popupY, popupX + POPUP_WIDTH, popupY + popupHeight, ARGB.color(easedProgress / 2, BACKGROUND_COLOR));

			renderArrow(graphics, easedProgress);

			int textY = popupY + POPUP_PADDING;
			for (Component helpLine : helpLines) {
				graphics.drawWordWrap(font, helpLine, popupX + POPUP_PADDING, textY, textWidth, ARGB.color(easedProgress, TEXT_COLOR), false);
				textY += font.wordWrapHeight(helpLine, textWidth) + LINE_SPACING;
			}
		}
	}

	private void renderArrow(GuiGraphics graphics, float easedProgress) {
		int arrowSize = 6;

		graphics.enableScissor(getX(), getY() - POPUP_MARGIN_BOTTOM, getRight(), getY() - POPUP_MARGIN_BOTTOM + arrowSize);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate((float) (getX() + getWidth() / 2.0), (float) (getY() - POPUP_MARGIN_BOTTOM - Math.sqrt(Math.pow(arrowSize, 2) * 2) / 2));
		matrices.rotate((float) Math.toRadians(45));

		graphics.fill(0, 0, arrowSize, arrowSize, ARGB.color(easedProgress / 2, BACKGROUND_COLOR));

		matrices.popMatrix();

		graphics.disableScissor();
	}

	public void handleOutsideClick(@NotNull MouseButtonEvent click, boolean doubled) {
		if (getDisplayHelp() && !isMouseOver(click.x(), click.y())) {
			onClick(click, doubled);
		}
	}

	@Override
	public void onClick(@NotNull MouseButtonEvent click, boolean bl) {
		isFadingOut = displayHelp;
		timestamp = System.currentTimeMillis();
		displayHelp = !displayHelp;
	}

	public boolean getDisplayHelp() {
		return displayHelp;
	}

	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
	}
}
