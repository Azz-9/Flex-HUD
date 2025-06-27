package me.Azz_9.better_hud.client.screens.moveModulesScreen.actions;

import me.Azz_9.better_hud.client.screens.moveModulesScreen.widgets.MovableWidget;

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
		widget.updateScalePosition();
	}

	@Override
	public void undo() {
		widget.moveTo(oldX, oldY);
		widget.updateScalePosition();
	}
}
