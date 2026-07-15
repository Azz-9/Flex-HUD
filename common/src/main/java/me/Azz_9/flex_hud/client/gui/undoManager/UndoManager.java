package me.Azz_9.flex_hud.client.gui.undoManager;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.input.KeyEvent;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoManager {
	private final Deque<Action> undoStack = new ArrayDeque<>();
	private final Deque<Action> redoStack = new ArrayDeque<>();

	public void addAction(Action action) {
		undoStack.push(action);
		redoStack.clear();
	}

	public void undo() {
		if (!undoStack.isEmpty()) {
			Action action = undoStack.pop();
			action.undo();
			redoStack.push(action);
		}
	}

	public void redo() {
		if (!redoStack.isEmpty()) {
			Action action = redoStack.pop();
			action.redo();
			undoStack.push(action);
		}
	}

	public boolean handleKeyPressed(KeyEvent input) {
		String keyName = GLFW.glfwGetKeyName(input.key(), input.scancode());
		if (keyName != null && keyName.equalsIgnoreCase("z") && input.modifiers() == InputConstants.MOD_CONTROL) { // CTRL + Z
			undo();
			return true;
		} else if (
				(keyName != null && keyName.equalsIgnoreCase("y") && input.modifiers() == InputConstants.MOD_CONTROL) || // CTRL + Y
						(keyName != null && keyName.equalsIgnoreCase("z") && input.modifiers() == (InputConstants.MOD_SHIFT + InputConstants.MOD_CONTROL)) // CTRL + SHIFT + Z
		) {
			redo();
			return true;
		}

		return false;
	}
}
