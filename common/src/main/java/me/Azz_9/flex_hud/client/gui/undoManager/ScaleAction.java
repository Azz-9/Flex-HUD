package me.Azz_9.flex_hud.client.gui.undoManager;

import me.Azz_9.flex_hud.client.gui.components.MovableWidget;

public class ScaleAction implements Action {
	private final MovableWidget widget;
	private final float oldScale;
	private final float newScale;

	public ScaleAction(MovableWidget widget, float oldScale, float newScale) {
		this.widget = widget;
		this.oldScale = oldScale;
		this.newScale = newScale;
	}

	@Override
	public void redo() {
		widget.setScale(newScale);
		widget.updateScaleHandle();
	}

	@Override
	public void undo() {
		widget.setScale(oldScale);
		widget.updateScaleHandle();
	}
}
