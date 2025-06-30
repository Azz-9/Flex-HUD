package me.Azz_9.better_hud.client.screens.moveModulesScreen.widgets;

import me.Azz_9.better_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.MovableModule;
import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.moveModulesScreen.MoveModulesScreen;
import me.Azz_9.better_hud.client.screens.moveModulesScreen.actions.DisableAction;
import me.Azz_9.better_hud.client.screens.moveModulesScreen.actions.MoveAction;
import me.Azz_9.better_hud.client.screens.moveModulesScreen.actions.ScaleAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class MovableWidget extends ClickableWidget implements TrackableChange {
	private final MoveModulesScreen PARENT;
	private final MovableModule HUD_ELEMENT;
	private double offsetX, offsetY;
	private boolean isMoving;
	private int onKeyPressX;
	private int onKeyPressY;

	private final Set<Integer> pressedKeys = new HashSet<>();

	private final int INITIAL_X, INITIAL_Y;
	private final float INITIAL_SCALE;
	private final double INITIAL_OFFSET_X, INITIAL_OFFSET_Y;
	private final AbstractHudElement.AnchorPosition INITIAL_ANCHOR_X, INITIAL_ANCHOR_Y;

	// snap
	private final int SNAP_DISTANCE = 5;
	private final int CENTERED_LINES_SNAP_DISTANCE = 10;
	private int snapLineX = 0;
	private int snapLineY = 0;
	private boolean shouldDrawVerticalSnapLine = false;
	private boolean shouldDrawHorizontalSnapLine = false;

	// scale
	private float scale = 1.0f;
	private final float MAX_SCALE = 4.0f;
	private final float MIN_SCALE = 0.5f;
	private final int HANDLE_SIZE = 4;
	private int handleX = getX() - HANDLE_SIZE / 2;
	private int handleY = getY() - HANDLE_SIZE / 2;
	private HandlePosition handlePosition;
	private boolean isDraggingScalehandle = false;
	private int onClickRight;
	private int onClickBottom;
	private int onClickX;
	private int onClickY;
	private AbstractHudElement.AnchorPosition onClickAnchorX;
	private AbstractHudElement.AnchorPosition onClickAnchorY;
	private float onClickScale;
	private boolean shouldDrawScaleValue = false;

	//cogwheel
	private int cogwheelX;
	private int cogwheelY;
	private final int COGWHEEL_SIZE = 6;
	private final Identifier cogwheelFocused = Identifier.of(MOD_ID, "widgets/buttons/cogwheel/focused.png");
	private final Identifier cogwheelUnfocused = Identifier.of(MOD_ID, "widgets/buttons/cogwheel/unfocused.png");
	//red cross
	private int crossX;
	private int crossY;
	private final int CROSS_SIZE = 6;
	private final Identifier crossFocused = Identifier.of(MOD_ID, "widgets/buttons/cross/focused.png");
	private final Identifier crossUnfocused = Identifier.of(MOD_ID, "widgets/buttons/cross/unfocused.png");

	public MovableWidget(MovableModule hudElement, MoveModulesScreen parent) {
		super(
				Math.round(hudElement.getX()),
				Math.round(hudElement.getY()),
				hudElement.getWidth(), hudElement.getHeight(), Text.empty()
		);
		this.PARENT = parent;
		this.HUD_ELEMENT = hudElement;
		this.INITIAL_X = Math.round(hudElement.getX());
		this.INITIAL_Y = Math.round(hudElement.getY());
		this.INITIAL_SCALE = hudElement.getScale();
		this.INITIAL_OFFSET_X = hudElement.getOffsetX();
		this.INITIAL_OFFSET_Y = hudElement.getOffsetY();
		this.INITIAL_ANCHOR_X = hudElement.getAnchorX();
		this.INITIAL_ANCHOR_Y = hudElement.getAnchorY();

		this.scale = hudElement.getScale();

		updateScalePosition();
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (!this.visible) {
			return;
		}

		if (!this.isHovered()) {
			this.hovered = isScaleHandleHovered(mouseX, mouseY) || isCrossHovered(mouseX, mouseY) || isCogwheelHovered(mouseX, mouseY);
		}

		int newWidth = Math.round(HUD_ELEMENT.getWidth() * HUD_ELEMENT.getScale());
		int newHeight = Math.round(HUD_ELEMENT.getHeight() * HUD_ELEMENT.getScale());
		if (this.width != newWidth || this.height != newHeight) {
			setDimensions((int) Math.ceil(HUD_ELEMENT.getWidth() * HUD_ELEMENT.getScale()), (int) Math.ceil(HUD_ELEMENT.getHeight() * HUD_ELEMENT.getScale()));
			updateCrossPosition();
			updateCogwheelPosition();
		}

		context.fill(getX(), getY(), getRight(), getBottom(), 0x4F88888C);
		if (this.isHovered() || this.isFocused()) {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0x7FF8F8FC);
		} else {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0x7FA8A8AC);
		}

		if (shouldDrawHorizontalSnapLine) {
			context.drawHorizontalLine(0, MinecraftClient.getInstance().getWindow().getScaledWidth(), snapLineY, 0x7fff0000);
		}
		if (shouldDrawVerticalSnapLine) {
			context.drawVerticalLine(snapLineX, 0, MinecraftClient.getInstance().getWindow().getScaledHeight(), 0x7fff0000);
		}

		renderScaleHandler(context);

		if (shouldDrawScaleValue) {
			String text = "×" + getScale();
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

		/*if (this.isHovered()) {
			renderCross(context, mouseX, mouseY);
			renderCogwheel(context, mouseX, mouseY);
		}*/
	}

	public void renderScaleHandler(DrawContext context) {
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

		context.fill(handleX, handleY, handleX + HANDLE_SIZE, handleY + HANDLE_SIZE, 0xffF8F8FC);
	}

	private void renderCogwheel(DrawContext context, int mouseX, int mouseY) {
		Identifier texture = isCogwheelHovered(mouseX, mouseY) ? cogwheelFocused : cogwheelUnfocused;
		context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, cogwheelX, cogwheelY, 0, 0, COGWHEEL_SIZE, COGWHEEL_SIZE, COGWHEEL_SIZE, COGWHEEL_SIZE);
	}

	private void renderCross(DrawContext context, int mouseX, int mouseY) {
		Identifier texture = isCrossHovered(mouseX, mouseY) ? crossFocused : crossUnfocused;
		context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, crossX, crossY, 0, 0, CROSS_SIZE, CROSS_SIZE, CROSS_SIZE, CROSS_SIZE);
	}

	public void updateScalePosition() {
		double screenCenterX = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2.0;
		double screenCenterY = MinecraftClient.getInstance().getWindow().getScaledHeight() / 2.0;
		double centerX = getX() + getWidth() / 2.0;
		double centerY = getY() + getHeight() / 2.0;

		if (centerX < screenCenterX) {
			if (centerY < screenCenterY) {
				handlePosition = HandlePosition.BOTTOM_RIGHT;
				handleX = getRight() - HANDLE_SIZE / 2;
				handleY = getBottom() - HANDLE_SIZE / 2;
			} else {
				handlePosition = HandlePosition.TOP_RIGHT;
				handleX = getRight() - HANDLE_SIZE / 2;
				handleY = getY() - HANDLE_SIZE / 2;
			}
		} else {
			if (centerY < screenCenterY) {
				handlePosition = HandlePosition.BOTTOM_LEFT;
				handleX = getX() - HANDLE_SIZE / 2;
				handleY = getBottom() - HANDLE_SIZE / 2;
			} else {
				handlePosition = HandlePosition.TOP_LEFT;
				handleX = getX() - HANDLE_SIZE / 2;
				handleY = getY() - HANDLE_SIZE / 2;
			}
		}
	}

	private void updateCogwheelPosition() {
		return;
		/*double screenCenterY = MinecraftClient.getInstance().getWindow().getScaledHeight() / 2.0;
		double centerY = getY() + getHeight() / 2.0;

		this.cogwheelX = getX() + 1 + (handlePosition == HandlePosition.BOTTOM_LEFT ? HANDLE_SIZE / 2 : 0);
		if (this.height < COGWHEEL_SIZE * 3) {
			if (centerY < screenCenterY) {
				this.cogwheelY = getBottom() + 1;
			} else {
				this.cogwheelY = getY() - COGWHEEL_SIZE - 1;
			}
		} else {
			this.cogwheelY = getBottom() - COGWHEEL_SIZE - 1;
		}*/
	}

	private void updateCrossPosition() {
		return;
		/*double screenCenterY = MinecraftClient.getInstance().getWindow().getScaledHeight() / 2.0;
		double centerY = getY() + getHeight() / 2.0;

		this.crossX = getRight() - CROSS_SIZE - 1 - (handlePosition == HandlePosition.BOTTOM_RIGHT ? HANDLE_SIZE / 2 : 0);
		if (this.height < CROSS_SIZE * 3) {
			if (centerY < screenCenterY) {
				this.crossY = getBottom() + 1;
			} else {
				this.crossY = getY() - CROSS_SIZE - 1;
			}
		} else {
			this.crossY = getBottom() - CROSS_SIZE - 1;
		}*/
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (isDraggingScalehandle) {
			return true; // so pressing a key won't do anything
		}
		if (this.isFocused() && keyCode >= 262 && keyCode <= 265) { // check if the key is one of the arrow keys
			if (pressedKeys.isEmpty()) {
				onKeyPressX = getX();
				onKeyPressY = getY();
			}
			pressedKeys.add(keyCode);
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
			updateScalePosition();
			return true;
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		shouldDrawScaleValue = false;

		if (keyCode >= 262 && keyCode <= 265) { // the key released is one of the arrow keys
			pressedKeys.remove(keyCode);
			if (pressedKeys.isEmpty() && onKeyPressX != getX() && onKeyPressY != getY()) {
				PARENT.undoManager.addAction(new MoveAction(this, onKeyPressX, onKeyPressY, getX(), getY()));
			}
		}

		return false;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.active && this.visible && this.hovered;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		if (isScaleHandleHovered(mouseX, mouseY)) {
			isDraggingScalehandle = true;
			onClickRight = HUD_ELEMENT.getWidth() + getX();
			onClickBottom = HUD_ELEMENT.getHeight() + getY();
			onClickAnchorX = HUD_ELEMENT.getAnchorX();
			onClickAnchorY = HUD_ELEMENT.getAnchorY();
		} else if (isCrossHovered(mouseX, mouseY)) {
			PARENT.undoManager.addAction(new DisableAction(this));
			setEnabled(false);
		} else if (isCogwheelHovered(mouseX, mouseY)) {
			MinecraftClient.getInstance().setScreen(HUD_ELEMENT.getConfigScreen(PARENT));
		} else {
			offsetX = mouseX - getX();
			offsetY = mouseY - getY();
		}

		onClickX = Math.round(HUD_ELEMENT.getX());
		onClickY = Math.round(HUD_ELEMENT.getY());
		onClickScale = getScale();
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		if (isDraggingScalehandle) {
			updateScalePosition();

			PARENT.undoManager.addAction(new ScaleAction(this, onClickX, onClickY, onClickScale, getX(), getY(), getScale()));

			isDraggingScalehandle = false;
		} else if (isMoving && (onClickX != getX() || onClickY != getY())) {
			PARENT.undoManager.addAction(new MoveAction(this, onClickX, onClickY, getX(), getY()));

			isMoving = false;
		}

		shouldDrawHorizontalSnapLine = false;
		shouldDrawVerticalSnapLine = false;

		shouldDrawScaleValue = false;
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		if (!isDraggingScalehandle) {
			isMoving = true;
			double x = mouseX - offsetX;
			double y = mouseY - offsetY;
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
			if (Screen.hasShiftDown()) {
				newScale = Math.round(newScale * 4) / 4.0f;
				shouldDrawScaleValue = true;
			}

			setScaleAndMove(newScale);
		}
	}

	public void moveTo(double x, double y) {
		int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
		int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

		x = Math.clamp(x, 0, Math.max(screenWidth - HUD_ELEMENT.getWidth() * HUD_ELEMENT.getScale(), 0));
		y = Math.clamp(y, 0, Math.max(screenHeight - HUD_ELEMENT.getHeight() * HUD_ELEMENT.getScale(), 0));

		this.setPosition((int) Math.round(x), (int) Math.round(y));
		HUD_ELEMENT.setX(x);
		HUD_ELEMENT.setY(y);
		this.PARENT.onWidgetChange();

		updateCrossPosition();
		updateCogwheelPosition();
	}

	public void snapElement(double x, double y) {
		double centerY = MinecraftClient.getInstance().getWindow().getScaledHeight() / 2.0;
		double centerX = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2.0;

		shouldDrawHorizontalSnapLine = false;
		shouldDrawVerticalSnapLine = false;
		if (!Screen.hasShiftDown()) {
			// centered lines
			if (Math.abs(y + this.getHeight() / 2.0 - centerY) < CENTERED_LINES_SNAP_DISTANCE) {
				y = centerY - this.getHeight() / 2.0;
				snapLineY = (int) (Math.round(centerY));
				shouldDrawHorizontalSnapLine = true;
			}
			if (Math.abs(x + this.getWidth() / 2.0 - centerX) < CENTERED_LINES_SNAP_DISTANCE) {
				x = centerX - this.getWidth() / 2.0;
				snapLineX = (int) Math.round(centerX);
				shouldDrawVerticalSnapLine = true;
			}

			if (!Screen.hasControlDown()) {
				for (MovableWidget movableWidget : PARENT.getMovableWidgets()) {
					if (!movableWidget.HUD_ELEMENT.getID().equals(this.HUD_ELEMENT.getID())) {
						if (Math.abs(y - movableWidget.getY()) < SNAP_DISTANCE) {
							y = movableWidget.getY();
							snapLineY = movableWidget.getY();
							shouldDrawHorizontalSnapLine = true;
						} else if (Math.abs(y + this.getHeight() - movableWidget.getY()) < SNAP_DISTANCE) {
							y = movableWidget.getY() - this.getHeight();
							snapLineY = movableWidget.getY();
							shouldDrawHorizontalSnapLine = true;
						} else if (Math.abs(y - movableWidget.getBottom()) < SNAP_DISTANCE) {
							y = movableWidget.getBottom();
							snapLineY = movableWidget.getBottom();
							shouldDrawHorizontalSnapLine = true;
						} else if (Math.abs(y + this.getHeight() - movableWidget.getBottom()) < SNAP_DISTANCE) {
							y = movableWidget.getBottom() - this.getHeight();
							snapLineY = movableWidget.getBottom();
							shouldDrawHorizontalSnapLine = true;
						}
						if (Math.abs(x - movableWidget.getX()) < SNAP_DISTANCE) {
							x = movableWidget.getX();
							snapLineX = movableWidget.getX();
							shouldDrawVerticalSnapLine = true;
						} else if (Math.abs(x + this.getWidth() - movableWidget.getX()) < SNAP_DISTANCE) {
							x = movableWidget.getX() - this.getWidth();
							snapLineX = movableWidget.getX();
							shouldDrawVerticalSnapLine = true;
						} else if (Math.abs(x - movableWidget.getRight()) < SNAP_DISTANCE) {
							x = movableWidget.getRight();
							snapLineX = movableWidget.getRight();
							shouldDrawVerticalSnapLine = true;
						} else if (Math.abs(x + this.getWidth() - movableWidget.getRight()) < SNAP_DISTANCE) {
							x = movableWidget.getRight() - this.getWidth();
							snapLineX = movableWidget.getRight();
							shouldDrawVerticalSnapLine = true;
						}
					}
				}
			}
		}

		moveTo(x, y);
		updateScalePosition();
	}

	private boolean isScaleHandleHovered(double mouseX, double mouseY) {
		return mouseX >= handleX && mouseX < handleX + HANDLE_SIZE && mouseY >= handleY && mouseY < handleY + HANDLE_SIZE;
	}

	private boolean isCogwheelHovered(double mouseX, double mouseY) {
		return false;
		//return mouseX >= cogwheelX && mouseX < cogwheelX + COGWHEEL_SIZE && mouseY >= cogwheelY && mouseY < cogwheelY + COGWHEEL_SIZE;
	}

	private boolean isCrossHovered(double mouseX, double mouseY) {
		return false;
		//return mouseX >= crossX && mouseX < crossX + CROSS_SIZE && mouseY >= crossY && mouseY < crossY + CROSS_SIZE;
	}

	@Override
	public void setToDefaultState() {
	}

	public void setEnabled(boolean enabled) {
		HUD_ELEMENT.setEnabled(enabled);
		this.active = enabled;
		this.visible = enabled;
		PARENT.onWidgetChange();
	}

	@Override
	public boolean hasChanged() {
		return this.getX() != INITIAL_X || this.getY() != INITIAL_Y || this.scale != INITIAL_SCALE || !HUD_ELEMENT.isEnabled();
	}

	@Override
	public void cancel() {
		HUD_ELEMENT.setPos(INITIAL_OFFSET_X, INITIAL_OFFSET_Y, INITIAL_ANCHOR_X, INITIAL_ANCHOR_Y);
		HUD_ELEMENT.setScale(INITIAL_SCALE);
		HUD_ELEMENT.setEnabled(true);
	}

	private float getScale() {
		return HUD_ELEMENT.getScale();
	}

	public void setScaleAndMove(float scale) {
		scale = Math.clamp(scale, MIN_SCALE, MAX_SCALE);

		switch (handlePosition) {
			case TOP_RIGHT -> {
				double newY;
				if (onClickAnchorY == AbstractHudElement.AnchorPosition.CENTER)
					newY = onClickY - (HUD_ELEMENT.getHeight() * scale / 2.0) + (HUD_ELEMENT.getHeight() * onClickScale / 2.0);
				else
					newY = onClickY - (HUD_ELEMENT.getHeight() * scale) + (HUD_ELEMENT.getHeight() * onClickScale);

				if (newY < 0)
					return;

				moveTo(HUD_ELEMENT.getX(), newY);
			}
			case BOTTOM_LEFT -> {
				double newX;
				if (onClickAnchorX == AbstractHudElement.AnchorPosition.CENTER)
					newX = onClickX - (HUD_ELEMENT.getWidth() * scale / 2.0) + (HUD_ELEMENT.getWidth() * onClickScale / 2.0);
				else
					newX = onClickX - (HUD_ELEMENT.getWidth() * scale) + (HUD_ELEMENT.getWidth() * onClickScale);

				if (newX < 0)
					return;

				moveTo(newX, HUD_ELEMENT.getY());
			}
			case TOP_LEFT -> {
				double newX;
				double newY;
				if (onClickAnchorY == AbstractHudElement.AnchorPosition.CENTER)
					newY = onClickY - (HUD_ELEMENT.getHeight() * scale / 2.0) + (HUD_ELEMENT.getHeight() * onClickScale / 2.0);
				else
					newY = onClickY - (HUD_ELEMENT.getHeight() * scale) + (HUD_ELEMENT.getHeight() * onClickScale);

				if (onClickAnchorX == AbstractHudElement.AnchorPosition.CENTER)
					newX = onClickX - (HUD_ELEMENT.getWidth() * scale / 2.0) + (HUD_ELEMENT.getWidth() * onClickScale / 2.0);
				else
					newX = onClickX - (HUD_ELEMENT.getWidth() * scale) + (HUD_ELEMENT.getWidth() * onClickScale);

				if (newX < 0 || newY < 0)
					return;

				moveTo(newX, newY);
			}
			case BOTTOM_RIGHT -> {
				double newX;
				double newY;
				if (onClickAnchorY == AbstractHudElement.AnchorPosition.CENTER)
					newY = onClickY - (HUD_ELEMENT.getHeight() * scale / 2.0) + (HUD_ELEMENT.getHeight() * onClickScale / 2.0);
				else
					newY = onClickY;

			}
		}
		setScale(scale);
	}

	public void setScale(float scale) {
		this.scale = scale;
		HUD_ELEMENT.setScale(scale);
		setDimensions(Math.round(HUD_ELEMENT.getWidth() * scale), Math.round(HUD_ELEMENT.getHeight() * scale));

		this.PARENT.onWidgetChange();

		updateCrossPosition();
		updateCogwheelPosition();
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
