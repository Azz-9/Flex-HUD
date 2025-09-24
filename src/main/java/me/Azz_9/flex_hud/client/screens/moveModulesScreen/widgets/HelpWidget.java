package me.Azz_9.flex_hud.client.screens.moveModulesScreen.widgets;

import me.Azz_9.flex_hud.client.utils.Cursors;
import me.Azz_9.flex_hud.client.utils.EaseUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class HelpWidget extends ClickableWidget {
	private final Identifier texture = Identifier.of(MOD_ID, "widgets/buttons/help/help.png");
	private boolean displayHelp = false;

	private final int BACKGROUND_COLOR = 0x000000;
	private final int TEXT_COLOR = 0xFFFFFF;
	private int alpha = 0;
	private final int TRANSITION_DURATION = 300; //ms
	private long timestamp;
	private boolean isFadingOut = false;

	private final Text[] helpLines;

	public HelpWidget(int x, int y, int width, int height, Text[] helpLines) {
		super(x, y, width, height, Text.translatable("flex_hud.help_widget"));
		this.helpLines = helpLines;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.isHovered() && this.isInteractable()) {
			context.setCursor(Cursors.POINTING_HAND);
		}

		context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, 0, getWidth(), getHeight(), 20, 20);

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

			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

			int padding = 4;
			int marginBottom = 6;

			int popupX = getX();
			int popupY;
			int popupHeight = padding;
			int popupWidth = 200;
			int textWidth = popupWidth - padding * 2;
			int lineSpacing = 3;
			for (Text line : helpLines) {
				popupHeight += textRenderer.getWrappedLinesHeight(line, textWidth);
				popupHeight += lineSpacing;
			}

			popupY = getY() - marginBottom - popupHeight;

			context.fill(popupX, popupY, popupX + popupWidth, popupY + popupHeight, (alpha / 2 << 24) | BACKGROUND_COLOR);

			renderArrow(context, marginBottom);

			int textY = popupY + padding;
			for (Text helpLine : helpLines) {
				context.drawWrappedText(textRenderer, helpLine, popupX + padding, textY, textWidth, (alpha << 24) | TEXT_COLOR, false);
				textY += textRenderer.getWrappedLinesHeight(helpLine, textWidth) + lineSpacing;
			}
		}
	}

	private void renderArrow(DrawContext context, int marginBottom) {
		int arrowSize = 6;

		context.enableScissor(getX(), getY() - marginBottom, getRight(), getY() - marginBottom + arrowSize);

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate((float) (getX() + getWidth() / 2.0), (float) (getY() - marginBottom - Math.sqrt(Math.pow(arrowSize, 2) * 2) / 2));
		matrices.rotate((float) Math.toRadians(45));

		context.fill(0, 0, arrowSize, arrowSize, (alpha / 2 << 24) | BACKGROUND_COLOR);

		matrices.popMatrix();

		context.disableScissor();
	}

	@Override
	public void onClick(Click click, boolean bl) {
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
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}
}
