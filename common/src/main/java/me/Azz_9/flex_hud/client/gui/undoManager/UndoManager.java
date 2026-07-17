package me.Azz_9.flex_hud.client.gui.undoManager;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.input.KeyEvent;

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
		boolean control = (input.modifiers() & InputConstants.MOD_CONTROL) != 0;
		boolean shift = (input.modifiers() & InputConstants.MOD_SHIFT) != 0;

		if (!control) {
			return false;
		}

		if (input.keycode() == InputConstants.KEYCODE_Z && !shift) { // ctrl + z
			undo();
			return true;
		}

		if (input.keycode() == InputConstants.KEYCODE_Y || input.keycode() == InputConstants.KEYCODE_Z) { // ctrl + y or ctrl + shift + z
			redo();
			return true;
		}

		return false;
	}
}
