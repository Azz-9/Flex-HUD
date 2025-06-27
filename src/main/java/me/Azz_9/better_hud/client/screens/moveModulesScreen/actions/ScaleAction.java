package me.Azz_9.better_hud.client.screens.moveModulesScreen.actions;

import me.Azz_9.better_hud.client.screens.moveModulesScreen.widgets.MovableWidget;

public class ScaleAction implements Action {
	private final MovableWidget widget;
	private final int oldX, oldY;
	private final float oldScale;
	private final int newX, newY;
	private final float newScale;

	public ScaleAction(MovableWidget widget, int oldX, int oldY, float oldScale, int newX, int newY, float newScale) {
		this.widget = widget;
		this.oldX = oldX;
		this.oldY = oldY;
		this.oldScale = oldScale;
		this.newX = newX;
		this.newY = newY;
		this.newScale = newScale;
	}

	@Override
	public void redo() {
		widget.setScale(newScale);
		widget.moveTo(newX, newY);
		widget.updateScalePosition();
	}

	@Override
	public void undo() {
		widget.setScale(oldScale);
		widget.moveTo(oldX, oldY);
		widget.updateScalePosition();
	}
}
