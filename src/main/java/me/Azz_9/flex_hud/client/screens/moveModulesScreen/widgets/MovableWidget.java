package me.Azz_9.flex_hud.client.screens.moveModulesScreen.widgets;

import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.MovableModule;
import me.Azz_9.flex_hud.client.mixin.CursorAccessor;
import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.MoveModulesScreen;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.actions.MoveAction;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.actions.ScaleAction;
import me.Azz_9.flex_hud.client.utils.Cursors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.Cursor;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

import static me.Azz_9.flex_hud.client.utils.DrawingUtils.drawBorder;

public class MovableWidget extends ClickableWidget implements TrackableChange {
	private final MoveModulesScreen PARENT;
	private final MovableModule HUD_ELEMENT;
	private double offsetX, offsetY;
	private boolean isMoving;
	private int onKeyPressX;
	private int onKeyPressY;

	private final Set<Integer> pressedKeys = new HashSet<>();

	private final float INITIAL_SCALE;
	private final double INITIAL_OFFSET_X, INITIAL_OFFSET_Y;
	private final AbstractHudElement.AnchorPosition INITIAL_ANCHOR_X, INITIAL_ANCHOR_Y;

	// snap
	private final int SNAP_DISTANCE = 3;
	private final int CENTERED_LINES_SNAP_DISTANCE = 10;
	private int snapLineX = 0;
	private int snapLineY = 0;
	private boolean shouldDrawVerticalSnapLine = false;
	private boolean shouldDrawHorizontalSnapLine = false;

	// scale
	public static final float MAX_SCALE = 3.0f;
	public static final float MIN_SCALE = 0.5f;
	private final int HANDLE_SIZE = 4;
	private int handleX = getX() - HANDLE_SIZE / 2;
	private int handleY = getY() - HANDLE_SIZE / 2;
	private HandlePosition handlePosition;
	private boolean isDraggingScalehandle = false;
	private int onClickRight;
	private int onClickBottom;
	private int onClickX;
	private int onClickY;
	private float onClickScale;
	private boolean shouldDrawScaleValue = false;
	private final float STEP = 0.25f;

	public MovableWidget(MovableModule hudElement, MoveModulesScreen parent) {
		super(
				hudElement.getRoundedX(),
				hudElement.getRoundedY(),
				hudElement.getWidth(), hudElement.getHeight(), Text.empty()
		);
		this.PARENT = parent;
		this.HUD_ELEMENT = hudElement;
		this.INITIAL_SCALE = hudElement.getScale();
		this.INITIAL_OFFSET_X = hudElement.getOffsetX();
		this.INITIAL_OFFSET_Y = hudElement.getOffsetY();
		this.INITIAL_ANCHOR_X = hudElement.getAnchorX();
		this.INITIAL_ANCHOR_Y = hudElement.getAnchorY();

		updateScaleHandle();
	}

	// i don't want to use the render method that already exists in ClickableWidget because it sets the value of hovered, and here, i'm setting this in the method mouseMove
	public void draw(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.visible) {
			this.renderWidget(context, mouseX, mouseY, deltaTicks);
		}
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (((CursorAccessor) context).getCursor() == Cursor.DEFAULT) {
			if (this.isScaleHandleHovered(mouseX, mouseY) || isDraggingScalehandle) {
				context.setCursor(
						switch (handlePosition) {
							case BOTTOM_RIGHT, TOP_LEFT -> Cursors.RESIZE_NWSE;
							case BOTTOM_LEFT, TOP_RIGHT -> Cursors.RESIZE_NESW;
						}
				);
			} else if (this.isHovered()) {
				context.setCursor(Cursors.RESIZE_ALL);
			}
		}

		context.fill(getX(), getY(), getRight(), getBottom(), 0x4f88888c);
		int color;
		if (this.isHovered() || this.isFocused()) {
			color = 0x7ff8f8fc;
		} else {
			color = 0x7fa8a8ac;
		}
		// not using context.drawStrokedRectangle() because it makes the border drawn above the scale handle
		drawBorder(context, getX(), getY(), getWidth(), getHeight(), color);

		if (shouldDrawHorizontalSnapLine) {
			context.drawHorizontalLine(0, context.getScaledWindowWidth(), snapLineY, 0x7fff0000);
		}
		if (shouldDrawVerticalSnapLine) {
			context.drawVerticalLine(snapLineX, 0, context.getScaledWindowHeight(), 0x7fff0000);
		}

		renderScaleHandler(context);
	}

	public void renderScaleHandler(DrawContext context) {
		context.fill(handleX, handleY, handleX + HANDLE_SIZE, handleY + HANDLE_SIZE, 0xffF8F8FC);

		if (shouldDrawScaleValue) {
			String text = "×" + HUD_ELEMENT.getScale();
			int valueX;
			if (handlePosition == HandlePosition.TOP_RIGHT || handlePosition == HandlePosition.BOTTOM_RIGHT) {
				// text to the right of the handle
				valueX = handleX + HANDLE_SIZE + HANDLE_SIZE / 2;
			} else {
				// text to the left of the handle
				valueX = handleX + HANDLE_SIZE - MinecraftClient.getInstance().textRenderer.getWidth(text);
			}
			int valueY = handleY;

			Matrix3x2fStack matrices = context.getMatrices();
			matrices.pushMatrix();
			matrices.translate(valueX, valueY);
			matrices.scale(0.75f, 0.75f);

			context.drawText(MinecraftClient.getInstance().textRenderer, text, 0, 0, 0xffffffff, true);

			matrices.popMatrix();
		}
	}

	public void updateScaleHandle() {
		double screenCenterX = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2.0;
		double screenCenterY = MinecraftClient.getInstance().getWindow().getScaledHeight() / 2.0;
		double centerX = getX() + getWidth() / 2.0;
		double centerY = getY() + getHeight() / 2.0;

		if (centerX < screenCenterX) {
			if (centerY < screenCenterY) {
				handlePosition = HandlePosition.BOTTOM_RIGHT;
			} else {
				handlePosition = HandlePosition.TOP_RIGHT;
			}
		} else {
			if (centerY < screenCenterY) {
				handlePosition = HandlePosition.BOTTOM_LEFT;
			} else {
				handlePosition = HandlePosition.TOP_LEFT;
			}
		}

		refreshScaleHandleCoords();
	}

	public void refreshScaleHandleCoords() {
		switch (handlePosition) {
			case TOP_LEFT -> {
				handleX = getX() - HANDLE_SIZE / 2;
				handleY = getY() - HANDLE_SIZE / 2;
			}
			case TOP_RIGHT -> {
				handleX = getRight() - HANDLE_SIZE / 2;
				handleY = getY() - HANDLE_SIZE / 2;
			}
			case BOTTOM_LEFT -> {
				handleX = getX() - HANDLE_SIZE / 2;
				handleY = getBottom() - HANDLE_SIZE / 2;
			}
			case BOTTOM_RIGHT -> {
				handleX = getRight() - HANDLE_SIZE / 2;
				handleY = getBottom() - HANDLE_SIZE / 2;
			}
		}
	}

	public void updateDimensionAndPosition() {
		setDimensionsAndPosition(
				(int) Math.ceil(HUD_ELEMENT.getScaledWidth()), (int) Math.ceil(HUD_ELEMENT.getScaledHeight()),
				HUD_ELEMENT.getRoundedX(), HUD_ELEMENT.getRoundedY()
		);
	}

	@Override
	public void onClick(Click click, boolean bl) {
		if (isScaleHandleHovered(click.x(), click.y())) {
			isDraggingScalehandle = true;
			onClickRight = HUD_ELEMENT.getWidth() + getX();
			onClickBottom = HUD_ELEMENT.getHeight() + getY();
		} else {
			offsetX = click.x() - getX();
			offsetY = click.y() - getY();
		}
		onClickX = HUD_ELEMENT.getRoundedX();
		onClickY = HUD_ELEMENT.getRoundedY();
		onClickScale = HUD_ELEMENT.getScale();
	}

	@Override
	protected void onDrag(Click click, double d, double e) {
		if (!isDraggingScalehandle) {
			isMoving = true;
			double x = click.x() - offsetX;
			double y = click.y() - offsetY;
			snapElement(x, y);
		} else {
			double oppositeCornerX;
			double oppositeCornerY;

			// Vecteur direction de la droite
			double dx;
			double dy;
			switch (handlePosition) {
				case TOP_LEFT -> {
					dx = onClickX - onClickRight;
					dy = onClickY - onClickBottom;
					oppositeCornerX = getX() + HUD_ELEMENT.getScaledWidth();
					oppositeCornerY = getY() + HUD_ELEMENT.getScaledHeight();
				}
				case TOP_RIGHT -> {
					dx = onClickRight - onClickX;
					dy = onClickY - onClickBottom;
					oppositeCornerX = getX();
					oppositeCornerY = getY() + HUD_ELEMENT.getScaledHeight();
				}
				case BOTTOM_LEFT -> {
					dx = onClickX - onClickRight;
					dy = onClickBottom - onClickY;
					oppositeCornerX = getX() + HUD_ELEMENT.getScaledWidth();
					oppositeCornerY = getY();
				}
				default -> {
					dx = onClickRight - onClickX;
					dy = onClickBottom - onClickY;
					oppositeCornerX = getX();
					oppositeCornerY = getY();
				}
			}

			// Calcul du paramètre t (projection du point de la souris sur la droite)
			double denom = dx * dx + dy * dy;
			double t = (denom == 0) ? 0 : ((click.x() - oppositeCornerX) * dx + (click.y() - oppositeCornerY) * dy) / denom;

			// Clamping de t pour rester dans les limites du segment
			t = Math.clamp(t, MIN_SCALE, MAX_SCALE);

			// Coordonnées du point projeté sur le segment
			double closestX = oppositeCornerX + t * dx;
			double closestY = oppositeCornerY + t * dy;

			// Calcul de l'échelle en fonction de la distance projetée
			float newScale = (float) (Math.sqrt(Math.pow((closestX - oppositeCornerX), 2) + Math.pow((closestY - oppositeCornerY), 2))
					/ Math.sqrt(dx * dx + dy * dy));

			// Arrondi à STEP près si Maj est enfoncé
			if (MinecraftClient.getInstance().isShiftPressed()) {
				newScale = Math.round(newScale / STEP) * STEP;
				shouldDrawScaleValue = true;
			}

			setScale(newScale);
		}
	}

	@Override
	public void onRelease(Click click) {
		if (isDraggingScalehandle) {
			updateScaleHandle();

			PARENT.undoManager.addAction(new ScaleAction(this, onClickScale, HUD_ELEMENT.getScale()));

			isDraggingScalehandle = false;
			shouldDrawScaleValue = false;
		} else if (isMoving && (onClickX != getX() || onClickY != getY())) {
			PARENT.undoManager.addAction(new MoveAction(this, onClickX, onClickY, getX(), getY()));

			isMoving = false;
		}

		shouldDrawHorizontalSnapLine = false;
		shouldDrawVerticalSnapLine = false;
	}

	@Override
	public boolean keyPressed(KeyInput input) {
		if (isDraggingScalehandle) {
			return true; // so pressing a key won't do anything
		}
		if (this.isFocused() && input.key() >= 262 && input.key() <= 265) { // check if the key is one of the arrow keys
			if (pressedKeys.isEmpty()) {
				onKeyPressX = getX();
				onKeyPressY = getY();
			}
			pressedKeys.add(input.key());
			if (pressedKeys.contains(GLFW.GLFW_KEY_UP)) {
				moveTo(getX(), getY() - 1);
			}
			if (pressedKeys.contains(GLFW.GLFW_KEY_DOWN)) {
				moveTo(getX(), getY() + 1);
			}
			if (pressedKeys.contains(GLFW.GLFW_KEY_LEFT)) {
				moveTo(getX() - 1, getY());
			}
			if (pressedKeys.contains(GLFW.GLFW_KEY_RIGHT)) {
				moveTo(getX() + 1, getY());
			}
			updateScaleHandle();
			return true;
		}
		return false;
	}

	@Override
	public boolean keyReleased(KeyInput input) {
		shouldDrawScaleValue = false;

		if (input.key() >= 262 && input.key() <= 265) { // the key released is one of the arrow keys
			pressedKeys.remove(input.key());
			if (pressedKeys.isEmpty() && (onKeyPressX != getX() || onKeyPressY != getY())) {
				PARENT.undoManager.addAction(new MoveAction(this, onKeyPressX, onKeyPressY, getX(), getY()));
			}
		}

		return false;
	}

	private void snapElement(double x, double y) {
		x = Math.clamp(x, 0, MinecraftClient.getInstance().getWindow().getScaledWidth() - this.getWidth());
		y = Math.clamp(y, 0, MinecraftClient.getInstance().getWindow().getScaledHeight() - this.getHeight());

		double centerY = MinecraftClient.getInstance().getWindow().getScaledHeight() / 2.0;
		double centerX = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2.0;

		shouldDrawHorizontalSnapLine = false;
		shouldDrawVerticalSnapLine = false;

		double snappedX = x;
		double snappedY = y;

		double minXDistance = SNAP_DISTANCE;
		double minYDistance = SNAP_DISTANCE;

		// These are the GUIDELINE coordinates (aligned edge), not the element's top-left
		Integer guideX = null;
		Integer guideY = null;

		// Use scaled sizes to match getX()/getY() coordinate space
		double thisW = this.HUD_ELEMENT.getScaledWidth();
		double thisH = this.HUD_ELEMENT.getScaledHeight();

		if (!MinecraftClient.getInstance().isShiftPressed()) {
			// ---- Center snapping (screen center) ----
			double dxCenter = Math.abs((x + thisW / 2.0) - centerX);
			if (dxCenter < CENTERED_LINES_SNAP_DISTANCE) {
				snappedX = centerX - thisW / 2.0;   // position (left of current) to center it
				guideX = (int) Math.round(centerX); // guideline at the center line
				shouldDrawVerticalSnapLine = true;
				minXDistance = dxCenter;
			}

			double dyCenter = Math.abs((y + thisH / 2.0) - centerY);
			if (dyCenter < CENTERED_LINES_SNAP_DISTANCE) {
				snappedY = centerY - thisH / 2.0;
				guideY = (int) Math.round(centerY);
				shouldDrawHorizontalSnapLine = true;
				minYDistance = dyCenter;
			}

			if (!MinecraftClient.getInstance().isCtrlPressed()) {
				for (MovableWidget widget : PARENT.getMovableWidgets()) {
					if (!widget.HUD_ELEMENT.getID().equals(this.HUD_ELEMENT.getID())) {
						double otherX = widget.getX();
						double otherY = widget.getY();
						double otherRight = widget.getRight();
						double otherBottom = widget.getBottom();

						// ---------- Horizontal snapping (align tops/bottoms) ----------
						// Build candidate pairs: (posYToApply, guideLineYToDraw)
						double[][] yPairs = new double[][]{
								{otherY, otherY},          // top-to-top
								{otherBottom, otherBottom},      // top-to-bottom
								{otherY - thisH, otherY},           // bottom(current)-to-top(other)
								{otherBottom - thisH, otherBottom}       // bottom-to-bottom
						};
						for (double[] pair : yPairs) {
							double posY = pair[0];
							double lineY = pair[1];
							double dy = Math.abs(y - posY); // distance in top-left space
							if (dy < minYDistance && dy < SNAP_DISTANCE) {
								minYDistance = dy;
								snappedY = posY;                 // apply position
								guideY = (int) Math.round(lineY); // draw line at aligned edge
								shouldDrawHorizontalSnapLine = true;
							}
						}

						// ---------- Vertical snapping (align lefts/rights) ----------
						// Build candidate pairs: (posXToApply, guideLineXToDraw)
						double[][] xPairs = new double[][]{
								{otherX, otherX},                 // left-to-left
								{otherRight, otherRight},            // left-to-right
								{otherX - thisW, otherX},                // right(current)-to-left(other)
								{otherRight - thisW, otherRight}             // right-to-right
						};
						for (double[] pair : xPairs) {
							double posX = pair[0];
							double lineX = pair[1];
							double dx = Math.abs(x - posX);
							if (dx < minXDistance && dx < SNAP_DISTANCE) {
								minXDistance = dx;
								snappedX = posX;                 // apply position
								guideX = (int) Math.round(lineX); // draw line at aligned edge
								shouldDrawVerticalSnapLine = true;
							}
						}
					}
				}
			}
		}

		if (guideX != null) snapLineX = guideX;
		if (guideY != null) snapLineY = guideY;

		moveTo(snappedX, snappedY);
	}


	public void moveTo(double x, double y) {
		x = Math.clamp(x, 0, MinecraftClient.getInstance().getWindow().getScaledWidth() - this.getWidth());
		y = Math.clamp(y, 0, MinecraftClient.getInstance().getWindow().getScaledHeight() - this.getHeight());

		HUD_ELEMENT.setX(x);
		HUD_ELEMENT.setY(y);
		this.setPosition(HUD_ELEMENT.getRoundedX(), HUD_ELEMENT.getRoundedY());
		updateScaleHandle();
		PARENT.onWidgetChange();
	}

	public void setScale(float scale) {
		scale = Math.clamp(scale, MIN_SCALE, MAX_SCALE);

		if (scale != HUD_ELEMENT.getScale()) {

			float maxScale = HUD_ELEMENT.computeMaxScale();

			if (scale > maxScale) {
				if (shouldDrawScaleValue) { // is snapping
					scale = (float) (Math.floor(maxScale / STEP) * STEP);
				} else {
					scale = maxScale;
				}
			}

			HUD_ELEMENT.setScale(scale);
			updateDimensionAndPosition();
			refreshScaleHandleCoords();
			PARENT.onWidgetChange();
		}
	}

	private boolean isScaleHandleHovered(double mouseX, double mouseY) {
		return mouseX >= handleX && mouseX < handleX + HANDLE_SIZE && mouseY >= handleY && mouseY < handleY + HANDLE_SIZE;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.active && this.visible && this.hovered;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		boolean isScaleHandleHovered = isScaleHandleHovered(mouseX, mouseY);
		this.hovered = (mouseX >= getX() && mouseY >= getY() && mouseX <= getRight() && mouseY <= getBottom()) || isScaleHandleHovered;
	}

	@Override
	public boolean hasChanged() {
		return INITIAL_OFFSET_X != HUD_ELEMENT.getOffsetX() || INITIAL_OFFSET_Y != HUD_ELEMENT.getOffsetY() ||
				INITIAL_ANCHOR_X != HUD_ELEMENT.getAnchorX() || INITIAL_ANCHOR_Y != HUD_ELEMENT.getAnchorY() ||
				INITIAL_SCALE != HUD_ELEMENT.getScale();
	}

	@Override
	public void cancel() {
		HUD_ELEMENT.setPos(INITIAL_OFFSET_X, INITIAL_OFFSET_Y, INITIAL_ANCHOR_X, INITIAL_ANCHOR_Y);
		HUD_ELEMENT.setScale(INITIAL_SCALE);
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	private enum HandlePosition {
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}
}
