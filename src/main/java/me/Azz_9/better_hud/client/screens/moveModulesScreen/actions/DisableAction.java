package me.Azz_9.better_hud.client.screens.moveModulesScreen.actions;

import me.Azz_9.better_hud.client.screens.moveModulesScreen.widgets.MovableWidget;

public class DisableAction implements Action {
	private final MovableWidget widget;

	public DisableAction(MovableWidget widget) {
		this.widget = widget;
	}

	@Override
	public void redo() {
		widget.setEnabled(false);
	}

	@Override
	public void undo() {
		widget.setEnabled(true);
	}
}
