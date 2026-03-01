package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.mixin.drawContext.DrawContextAccessor;

public class VariableWidget implements Drawable, Widget {

	public static final int PADDING_HORIZONTAL = 3;
	private static final int PADDING_VERTICAL = 2;
	private static final int BG_COLOR = 0xff2b2d31;
	private static final int BORDER_COLOR = 0xff3c3f41;
	private static final int TEXT_COLOR = 0xffffffff;

	public static final int HEIGHT = CLIENT.textRenderer.fontHeight + PADDING_VERTICAL * 2;

	private static final int DESCRIPTION_DELAY = 500;
	private static final int DESCRIPTION_MAX_INNER_WIDTH = 200;
	private static final int DESCRIPTION_GAP = 1;
	private static final int DESCRIPTION_PADDING = 2;
	private static final int DESCRIPTION_BG_COLOR = 0xff1e1f22;

	private int x, y;
	private final int width, height, textWidth;
	private final Variable<?> variable;

	private boolean hovered;
	private long startHoverTime;


	public VariableWidget(int x, int y, Variable<?> variable) {
		this.x = x;
		this.y = y;
		this.variable = variable;

		this.textWidth = CLIENT.textRenderer.getWidth(variable.getName());
		this.width = textWidth + PADDING_HORIZONTAL * 2;
		this.height = HEIGHT;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		boolean wasHovered = hovered;
		hovered = getX() <= mouseX && mouseX <= getRight() && getY() <= mouseY && mouseY <= getBottom();

		if (!wasHovered && hovered) {
			startHoverTime = System.currentTimeMillis();
		}

		if (hovered && System.currentTimeMillis() - startHoverTime > DESCRIPTION_DELAY) {
			ScreenRect rect = context.scissorStack.peekLast();
			context.disableScissor();

			((DrawContextAccessor) context).setTooltipDrawer(
					() -> renderDescription(context, mouseX, mouseY, deltaTicks)
			);

			if (rect != null) {
				context.enableScissor(rect.getLeft(), rect.getTop(), rect.getRight(), rect.getBottom());
			}
		}

		context.fill(getX(), getY(), getRight(), getBottom(), BG_COLOR);
		context.drawStrokedRectangle(getX(), getY(), getWidth(), getHeight(), BORDER_COLOR);

		context.drawText(
				CLIENT.textRenderer,
				variable.getName(),
				getX() + (getWidth() - textWidth) / 2,
				getY() + PADDING_VERTICAL,
				TEXT_COLOR,
				false
		);
	}

	private void renderDescription(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		int x = getRight() + DESCRIPTION_GAP;
		int y = getY();
		int innerWidth = Math.min(DESCRIPTION_MAX_INNER_WIDTH, CLIENT.textRenderer.getWidth(variable.getDescription()));
		int width = innerWidth + DESCRIPTION_PADDING * 2;
		int height = CLIENT.textRenderer.getWrappedLinesHeight(
				variable.getDescription(),
				width - DESCRIPTION_PADDING * 2
		) + DESCRIPTION_PADDING * 2;

		Screen screen = CLIENT.currentScreen;
		if (screen != null) {
			if (getRight() + DESCRIPTION_GAP + width > screen.width) {
				x = getX() - DESCRIPTION_GAP - width;
			}
			if (getY() + height > screen.height) {
				y = Math.min(getBottom() - height, screen.height - height - DESCRIPTION_GAP);
			} else if (getY() < 0) {
				y = DESCRIPTION_GAP;
			}
		}

		context.fill(x, y, x + width, y + height, DESCRIPTION_BG_COLOR);
		context.drawWrappedText(
				CLIENT.textRenderer,
				variable.getDescription(),
				x + DESCRIPTION_PADDING,
				y + DESCRIPTION_PADDING,
				innerWidth,
				TEXT_COLOR,
				false
		);
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public int getRight() {
		return getX() + getWidth();
	}

	public int getBottom() {
		return getY() + getHeight();
	}

	@Override
	public void forEachChild(Consumer<ClickableWidget> consumer) {
	}
}
