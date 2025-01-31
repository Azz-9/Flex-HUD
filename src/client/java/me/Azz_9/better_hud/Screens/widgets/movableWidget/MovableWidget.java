package me.Azz_9.better_hud.Screens.widgets.movableWidget;

import me.Azz_9.better_hud.Screens.MoveElementsScreen.MoveElementsScreen;
import me.Azz_9.better_hud.client.Interface.TrackableChange;
import me.Azz_9.better_hud.client.Overlay.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class MovableWidget extends ClickableWidget implements MoveElementsScreen.SnapLines, TrackableChange {
	private double offsetX;
	private double offsetY;
	private final int initialX;
	private final int initialY;
	private final HudElement hudElement;
	private final MoveElementsScreen screen;

	private boolean shouldDrawXCenteredLine = false;
	private boolean shouldDrawYCenteredLine = false;

	private float scale = 1.0f;
	private final int handleSize = 4;
	private int handleX = getX();
	private int handleY = getY();

	private final Set<Integer> pressedKeys = new HashSet<>();

	private boolean isDraggingScalehandle = false;

	public MovableWidget(int x, int y, int width, int height, HudElement hudElement, MoveElementsScreen screen) {
		super(x, y, width, height, Text.literal(""));
		this.initialX = x;
		this.initialY = y;
		this.hudElement = hudElement;
		this.screen = screen;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (!this.isHovered()) {
			this.hovered = context.scissorContains(mouseX, mouseY) && mouseX >= handleX && mouseY >= handleY && mouseX < handleX + handleSize && mouseY < handleY + handleSize;
		}

		setDimensions(hudElement.getWidth(), hudElement.getHeight());

		context.fill(getX(), getY(), getRight(), getBottom(), 0x4F88888C);
		if (this.isHovered()) {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0x7FF8F8FC);
		} else {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0x7FA8A8AC);
		}

		//renderScalehandle(context);
	}

	private void renderScalehandle(DrawContext context) {
		double screenCenterX = screen.width / 2.0;
		double screenCenterY = screen.height / 2.0;
		double centerX = getX() + getWidth() / 2.0;
		double centerY = getY() + getHeight() / 2.0;

		if (centerX < screenCenterX) {
			handleX = getRight() - handleSize / 2;
		} else {
			handleX = getX() - handleSize / 2;
		}
		if (centerY < screenCenterY) {
			handleY = getBottom() - handleSize / 2;
		} else {
			handleY = getY() - handleSize / 2;
		}

		context.fill(handleX, handleY, handleX + handleSize, handleY + handleSize, 0xffF8F8FC);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.active && this.visible && this.hovered;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		if (ishandleHovered(mouseX, mouseY)) {
			isDraggingScalehandle = true;
		}
		offsetX = mouseX - getX();
		offsetY = mouseY - getY();
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		if (!isDraggingScalehandle) {
			int x = (int) Math.clamp(mouseX - offsetX, 0, screen.width - width);
			int y = (int) Math.clamp(mouseY - offsetY, 0, screen.height - height);
			snapElement(x, y);
		} else {
			setScale((float) (((mouseX - getX()) / this.width + (mouseY - getY()) / this.height) / 2.0));
		}
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		isDraggingScalehandle = false;

		shouldDrawYCenteredLine = false;
		shouldDrawXCenteredLine = false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
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
		return false;
	}

	private void move(int x, int y) {
		if (isNotOverflowing(x, y)) {
			setPosition(x, y);
			hudElement.setPos(x, y);
		}
	}

	private void setScale(float scale) {
		this.scale = scale;
		hudElement.setScale(scale);
	}

	private void snapElement(int x, int y) {
		if (!isShiftKeyPressed()) {
			if (Math.abs((x + (double) width / 2) - (screen.width / 2.0)) < 30) {
				x = (screen.width - width) / 2;
				shouldDrawXCenteredLine = true;
			} else {
				shouldDrawXCenteredLine = false;
			}
			if (Math.abs((y + (double) height / 2) - (screen.height / 2.0)) < 30) {
				y = (screen.height - height) / 2;
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

	private boolean isNotOverflowing(int x, int y) {
		return x >= 0 && x <= screen.width - width && y >= 0 && y <= screen.height - height;
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
		return initialX != getX() || initialY != getY();
	}

	@Override
	public void cancel() {
		hudElement.setPos(initialX, initialY);
	}
}
