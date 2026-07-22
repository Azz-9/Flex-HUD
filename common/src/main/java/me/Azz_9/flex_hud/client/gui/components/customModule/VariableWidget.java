package me.Azz_9.flex_hud.client.gui.components.customModule;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import me.Azz_9.flex_hud.client.modules.customModules.Variable;
import me.Azz_9.flex_hud.mixin.GuiGraphicsAccessor;
import me.Azz_9.flex_hud.platform.Services;
import me.Azz_9.flex_hud.utils.DrawingUtils;

public class VariableWidget implements Renderable, LayoutElement {

	public static final int PADDING_HORIZONTAL = 3;
	private static final int PADDING_VERTICAL = 2;
	private static final int BG_COLOR = 0xff2b2d31;
	private static final int BORDER_COLOR = 0xff3c3f41;
	private static final int TEXT_COLOR = 0xffffffff;

	public static final int HEIGHT = MINECRAFT.font.lineHeight + PADDING_VERTICAL * 2;

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

		this.textWidth = MINECRAFT.font.width(variable.getName());
		this.width = textWidth + PADDING_HORIZONTAL * 2;
		this.height = HEIGHT;
	}

	@Override
	public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		boolean wasHovered = hovered;
		hovered = getX() <= mouseX && mouseX <= getRight() && getY() <= mouseY && mouseY <= getBottom();

		if (!wasHovered && hovered) {
			startHoverTime = System.currentTimeMillis();
		}

		if (hovered && System.currentTimeMillis() - startHoverTime > DESCRIPTION_DELAY) {
			ScreenRectangle rect = Services.PLATFORM.scissorStackPeek(graphics);
			graphics.disableScissor();

			((GuiGraphicsAccessor) graphics).setDeferredTooltip(
					() -> renderDescription(graphics, mouseX, mouseY, deltaTicks)
			);

			if (rect != null) {
				graphics.enableScissor(rect.left(), rect.top(), rect.right(), rect.bottom());
			}
		}

		graphics.fill(getX(), getY(), getRight(), getBottom(), BG_COLOR);
		DrawingUtils.drawBorder(graphics, getX(), getY(), getWidth(), getHeight(), BORDER_COLOR);

		graphics.drawString(
				MINECRAFT.font,
				variable.getName(),
				getX() + (getWidth() - textWidth) / 2,
				getY() + PADDING_VERTICAL,
				TEXT_COLOR,
				false
		);
	}

	private void renderDescription(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		int x = getRight() + DESCRIPTION_GAP;
		int y = getY();
		int innerWidth = Math.min(DESCRIPTION_MAX_INNER_WIDTH, MINECRAFT.font.width(variable.getDescription()));
		int width = innerWidth + DESCRIPTION_PADDING * 2;
		int height = MINECRAFT.font.wordWrapHeight(
				variable.getDescription(),
				width - DESCRIPTION_PADDING * 2
		) + DESCRIPTION_PADDING * 2;

		Screen screen = MINECRAFT.screen;
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

		graphics.fill(x, y, x + width, y + height, DESCRIPTION_BG_COLOR);
		graphics.drawWordWrap(
				MINECRAFT.font,
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
	public void visitWidgets(@NotNull Consumer<AbstractWidget> widgetVisitor) {
	}
}
