package me.Azz_9.flex_hud.client.gui.undoManager;

import me.Azz_9.flex_hud.client.gui.components.MovableWidget;

public class MoveAction implements Action {
	private final MovableWidget widget;
	private final int oldX, oldY;
	private final int newX, newY;

	public MoveAction(MovableWidget widget, int oldX, int oldY, int newX, int newY) {
		this.widget = widget;
		this.oldX = oldX;
		this.oldY = oldY;
		this.newX = newX;
		this.newY = newY;
	}

	@Override
	public void redo() {
		widget.moveTo(newX, newY);
		widget.updateScaleHandle();
	}

	@Override
	public void undo() {
		widget.moveTo(oldX, oldY);
		widget.updateScaleHandle();
	}
}
