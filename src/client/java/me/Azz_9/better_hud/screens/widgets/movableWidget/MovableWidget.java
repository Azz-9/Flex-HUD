package me.Azz_9.better_hud.screens.widgets.movableWidget;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import me.Azz_9.better_hud.client.overlay.HudElement;
import me.Azz_9.better_hud.screens.moveElementsScreen.MoveElementsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MovableWidget extends ClickableWidget implements MoveElementsScreen.SnapLines, TrackableChange {

	private enum HandlePosition {
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}

	private enum Action {
		MOVE,
		SCALE
	}

	private class PreviousAction {
		/* if the action type is SCALE, the X value is used as the scale value */
		private final Action ACTION_TYPE;
		private final int X;
		private final int Y;
		private final float SCALE;

		public PreviousAction(Action actionType, int x, int y) {
			this.ACTION_TYPE = actionType;
			this.SCALE = 1;
			this.X = x;
			this.Y = y;
		}

		public PreviousAction(Action actionType, float scale) {
			this.ACTION_TYPE = actionType;
			this.SCALE = scale;
			this.X = 0;
			this.Y = 0;
		}

		public double getScale() {
			if (ACTION_TYPE == Action.SCALE) {
				return SCALE;
			} else {
				throw new IllegalStateException("Cannot get scale for MOVE action");
			}
		}

		public double getX() {
			if (ACTION_TYPE == Action.MOVE) {
				return X;
			} else {
				throw new IllegalStateException("Cannot get X for SCALE action");
			}
		}

		public double getY() {
			if (ACTION_TYPE == Action.MOVE) {
				return Y;
			} else {
				throw new IllegalStateException("Cannot get Y for SCALE action");
			}
		}

		@Override
		public String toString() {
			if (ACTION_TYPE == Action.MOVE) {
				return "Move action, previous X : " + X + ", previous Y : " + Y;
			} else {
				return "Scale action, previous scale : " + SCALE;
			}
		}
	}

	private double offsetX;
	private double offsetY;
	private final int INITIAL_X;
	private final int INITIAL_Y;
	private final float INITIAL_SCALE;
	private final HudElement HUD_ELEMENT;
	private final MoveElementsScreen SCREEN;

	private boolean shouldDrawXCenteredLine = false;
	private boolean shouldDrawYCenteredLine = false;
	private boolean shouldDrawScaleValue = false;

	private float scale = 1.0f;
	private final float MAX_SCALE = 3.0f;
	private final float MIN_SCALE = 0.5f;
	private final int handleSize = 4;
	private int handleX = getX() - handleSize / 2;
	private int handleY = getY() - handleSize / 2;
	private HandlePosition handlePosition;
	private int onClickRight;
	private int onClickBottom;
	private double onClickX;
	private double onClickY;
	private float onClickScale;

	private final Set<Integer> pressedKeys = new HashSet<>();

	private final List<PreviousAction> prevActions = new LinkedList<>();
	private final LinkedList<PreviousAction> redoActions = new LinkedList<>();

	private boolean isDraggingScalehandle = false;

	public MovableWidget(int x, int y, float scale, int width, int height, HudElement hudElement, MoveElementsScreen screen) {
		super(x, y, width, height, Text.literal(""));
		this.INITIAL_X = x;
		this.INITIAL_Y = y;
		this.INITIAL_SCALE = scale;
		HUD_ELEMENT = hudElement;
		this.SCREEN = screen;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (!this.isHovered()) {
			this.hovered = context.scissorContains(mouseX, mouseY) && mouseX >= handleX && mouseY >= handleY && mouseX < handleX + handleSize && mouseY < handleY + handleSize;
		}

		setDimensions(roundCustom(HUD_ELEMENT.getWidth() * getScale() + HUD_ELEMENT.x - this.getX()), roundCustom(HUD_ELEMENT.getHeight() * getScale() + HUD_ELEMENT.y - this.getY()));

		context.fill(getX(), getY(), getWidth() + getX(), getHeight() + getY(), 0x4F88888C);
		if (this.isHovered() || this.isFocused()) {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0x7FF8F8FC);
		} else {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0x7FA8A8AC);
		}

		if (shouldDrawScaleValue) {
			MatrixStack matrices = context.getMatrices();
			matrices.push();
			matrices.translate(handleX + handleSize + 1, handleY, 0);
			matrices.scale(0.75f, 0.75f, 1.0f);

			context.drawText(MinecraftClient.getInstance().textRenderer, "×" + getScale(), 0, 0, 0xffffff, true);

			matrices.pop();
		}

		renderScalehandle(context);
	}

	private void renderScalehandle(DrawContext context) {
		double screenCenterX = SCREEN.width / 2.0;
		double screenCenterY = SCREEN.height / 2.0;
		double centerX = getX() + getWidth() / 2.0;
		double centerY = getY() + getHeight() / 2.0;

		if (isDraggingScalehandle) {
			switch (handlePosition) {
				case TOP_LEFT -> {
					handleX = getX() - handleSize / 2;
					handleY = getY() - handleSize / 2;
				}
				case TOP_RIGHT -> {
					handleX = getRight() - handleSize / 2;
					handleY = getY() - handleSize / 2;
				}
				case BOTTOM_LEFT -> {
					handleX = getX() - handleSize / 2;
					handleY = getBottom() - handleSize / 2;
				}
				case BOTTOM_RIGHT -> {
					handleX = getRight() - handleSize / 2;
					handleY = getBottom() - handleSize / 2;
				}
			}
		} else {
			if (centerX < screenCenterX) {
				if (centerY < screenCenterY) {
					handlePosition = HandlePosition.BOTTOM_RIGHT;
					handleX = getRight() - handleSize / 2;
					handleY = getBottom() - handleSize / 2;
				} else {
					handlePosition = HandlePosition.TOP_RIGHT;
					handleX = getRight() - handleSize / 2;
					handleY = getY() - handleSize / 2;
				}
			} else {
				if (centerY < screenCenterY) {
					handlePosition = HandlePosition.BOTTOM_LEFT;
					handleX = getX() - handleSize / 2;
					handleY = getBottom() - handleSize / 2;
				} else {
					handlePosition = HandlePosition.TOP_LEFT;
					handleX = getX() - handleSize / 2;
					handleY = getY() - handleSize / 2;
				}
			}
		}

		context.fill(handleX, handleY, handleX + handleSize, handleY + handleSize, 0xffF8F8FC);
	}

	private int roundCustom(double value) {
		int intPart = (int) value;
		double decimalPart = value - intPart;

		if (decimalPart > 0.16666666) {
			return intPart + 1;
		} else {
			return intPart;
		}
	}


	public float getScale() {
		return HUD_ELEMENT.scale;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.active && this.visible && this.hovered;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		if (ishandleHovered(mouseX, mouseY)) {
			isDraggingScalehandle = true;
			onClickRight = (int) (getWidth() / getScale()) + getX();
			onClickBottom = (int) (getHeight() / getScale()) + getY();
			onClickX = HUD_ELEMENT.x;
			onClickY = HUD_ELEMENT.y;
			onClickScale = getScale();
		}
		offsetX = mouseX - getX();
		offsetY = mouseY - getY();

	}


	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		if (!isDraggingScalehandle) {
			double x = Math.clamp(mouseX - offsetX, 0, SCREEN.width - width);
			double y = Math.clamp(mouseY - offsetY, 0, SCREEN.height - height);
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
					oppositeCornerX = getX() + HUD_ELEMENT.getWidth() * getScale();
					oppositeCornerY = getY() + HUD_ELEMENT.getHeight() * getScale();
				}
				case TOP_RIGHT -> {
					dx = onClickRight - onClickX;
					dy = onClickY - onClickBottom;
					oppositeCornerX = onClickX;
					oppositeCornerY = getY() + HUD_ELEMENT.getHeight() * getScale();
				}
				case BOTTOM_LEFT -> {
					dx = onClickX - onClickRight;
					dy = onClickBottom - onClickY;
					oppositeCornerX = getX() + HUD_ELEMENT.getWidth() * getScale();
					oppositeCornerY = onClickY;
				}
				default -> {
					dx = onClickRight - onClickX;
					dy = onClickBottom - onClickY;
					oppositeCornerX = onClickX;
					oppositeCornerY = onClickY;
				}
			}

			// Calcul du paramètre t (projection du point de la souris sur la droite)
			double denom = dx * dx + dy * dy;
			double t = (denom == 0) ? 0 : ((mouseX - oppositeCornerX) * dx + (mouseY - oppositeCornerY) * dy) / denom;

			// Clamping de t pour rester dans les limites du segment
			t = Math.clamp(t, MIN_SCALE, MAX_SCALE);

			// Coordonnées du point projeté sur le segment
			double closestX = oppositeCornerX + t * dx;
			double closestY = oppositeCornerY + t * dy;

			// Calcul de l'échelle en fonction de la distance projetée
			float newScale = (float) (Math.sqrt(Math.pow((closestX - oppositeCornerX), 2) + Math.pow((closestY - oppositeCornerY), 2))
					/ Math.sqrt(dx * dx + dy * dy));

			// Arrondi à 0.25 près si Maj est enfoncé
			if (isShiftKeyPressed()) {
				newScale = Math.round(newScale * 4) / 4.0f;
				shouldDrawScaleValue = true;
			}

			setScale(newScale);
		}
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		if (!isDraggingScalehandle) {
			addPrevAction(new PreviousAction(Action.MOVE, getX(), getY()));
		} else {
			addPrevAction(new PreviousAction(Action.SCALE, getScale()));
		}

		isDraggingScalehandle = false;

		shouldDrawYCenteredLine = false;
		shouldDrawXCenteredLine = false;

		shouldDrawScaleValue = false;
	}


	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (isDraggingScalehandle) {
			return true; // so pressing a key won't do anything
		}
		if (this.isFocused()) {
			boolean result = false;
			pressedKeys.add(keyCode);
			if (pressedKeys.contains(GLFW.GLFW_KEY_UP)) {
				move(getX(), getY() - 1);
				result = true;
			}
			if (pressedKeys.contains(GLFW.GLFW_KEY_DOWN)) {
				move(getX(), getY() + 1);
				result = true;
			}
			if (pressedKeys.contains(GLFW.GLFW_KEY_LEFT)) {
				move(getX() - 1, getY());
				result = true;
			}
			if (pressedKeys.contains(GLFW.GLFW_KEY_RIGHT)) {
				move(getX() + 1, getY());
				result = true;
			}
			return result;
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		pressedKeys.remove(keyCode);

		if (keyCode >= 262 && keyCode <= 265) { // key released is one of the arrow keys
			addPrevAction(new PreviousAction(Action.MOVE, getX(), getY()));
		} else if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT) {
			shouldDrawScaleValue = false;
		}

		return false;
	}

	private void addPrevAction(PreviousAction previousAction) {
		prevActions.add(previousAction);
		if (prevActions.size() > 100) {
			prevActions.removeFirst();
		}
		redoActions.clear();
		SCREEN.addModifiedWidget(this);
	}

	public void undo() {
		if (prevActions.isEmpty()) {
			return;
		}

		PreviousAction lastAction = prevActions.removeLast(); // Récupérer et supprimer la dernière action
		redoActions.addLast(lastAction);

		PreviousAction previousAction = prevActions.isEmpty() ? null : prevActions.getLast();

		if (lastAction.ACTION_TYPE == Action.MOVE) {
			forceMove(previousAction != null ? previousAction.X : INITIAL_X,
					previousAction != null ? previousAction.Y : INITIAL_Y);
		} else {
			setScale(previousAction != null ? previousAction.SCALE : INITIAL_SCALE);
		}
	}

	public void redo() {
		if (redoActions.isEmpty()) {
			return;
		}

		PreviousAction nextAction = redoActions.removeLast();
		prevActions.addLast(nextAction);

		if (nextAction.ACTION_TYPE == Action.MOVE) {
			move(nextAction.X, nextAction.Y);
		} else {
			setScale(nextAction.SCALE);
		}
	}

	private void move(double x, double y) {
		if (isNotOverflowing(x, y)) {
			setPosition((int) x, (int) y);
			HUD_ELEMENT.setPos(x, y);
		}
	}

	/*
	 * Doesn't check if, with the new coordinates, the element will be overflowing
	 */
	private void forceMove(double x, double y) {
		setPosition((int) x, (int) y);
		HUD_ELEMENT.setPos(x, y);
	}

	private void setScale(float scale) {
		scale = Math.clamp(scale, MIN_SCALE, MAX_SCALE);
		switch (handlePosition) {
			case TOP_RIGHT ->
					forceMove(HUD_ELEMENT.x, onClickY - (HUD_ELEMENT.getHeight() * scale) + (HUD_ELEMENT.getHeight() * onClickScale));
			case BOTTOM_LEFT ->
					forceMove(onClickX - (HUD_ELEMENT.getWidth() * scale) + (HUD_ELEMENT.getWidth() * onClickScale), HUD_ELEMENT.y);
			case TOP_LEFT ->
					forceMove(onClickX - (HUD_ELEMENT.getWidth() * scale) + (HUD_ELEMENT.getWidth() * onClickScale),
							onClickY - (HUD_ELEMENT.getHeight() * scale) + (HUD_ELEMENT.getHeight() * onClickScale));
		}
		this.scale = scale;
		HUD_ELEMENT.scale = scale;
	}

	private void snapElement(double x, double y) {
		/*
		 * snap the element if it is near the X centered line or the Y centered line
		 * else move the element to the right location
		 */
		if (!isShiftKeyPressed()) {
			if (Math.abs((x + (double) width / 2) - (SCREEN.width / 2.0)) < (SCREEN.width / 30.0)) {
				x = (SCREEN.width - width) / 2.0F;
				shouldDrawXCenteredLine = true;
			} else {
				shouldDrawXCenteredLine = false;
			}
			if (Math.abs((y + (double) height / 2) - (SCREEN.height / 2.0)) < (SCREEN.width / 30.0)) { // we want the same value after the <
				y = (SCREEN.height - height) / 2.0F;
				shouldDrawYCenteredLine = true;
			} else {
				shouldDrawYCenteredLine = false;
			}
		} else {
			// prevents lines from displaying if the element was snapped when the player pressed shift, and then moves the element away
			shouldDrawXCenteredLine = false;
			shouldDrawYCenteredLine = false;
		}
		move(x, y);
	}

	private boolean isShiftKeyPressed() {
		MinecraftClient client = MinecraftClient.getInstance();
		return InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_LEFT_SHIFT) ||
				InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_RIGHT_SHIFT);
	}

	private boolean isNotOverflowing(double x, double y) {
		return x >= 0 && x <= SCREEN.width - width && y >= 0 && y <= SCREEN.height - height;
	}

	private boolean ishandleHovered(double mouseX, double mouseY) {
		return mouseX >= handleX && mouseX < handleX + handleSize && mouseY >= handleY && mouseY < handleY + handleSize;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {

	}

	@Override
	public boolean XcenteredLine() {
		return shouldDrawXCenteredLine;
	}

	@Override
	public boolean YcenteredLine() {
		return shouldDrawYCenteredLine;
	}

	@Override
	public boolean hasChanged() {
		return INITIAL_X != getX() || INITIAL_Y != getY() || INITIAL_SCALE != getScale();
	}

	@Override
	public void cancel() {
		HUD_ELEMENT.setPos(INITIAL_X, INITIAL_Y);
		HUD_ELEMENT.scale = INITIAL_SCALE;
	}
}
