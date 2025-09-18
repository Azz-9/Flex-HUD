package me.Azz_9.flex_hud.client.screens.moveModulesScreen.actions;

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
}
