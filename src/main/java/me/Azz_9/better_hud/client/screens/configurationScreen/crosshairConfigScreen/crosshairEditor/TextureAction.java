package me.Azz_9.better_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor;

import me.Azz_9.better_hud.client.screens.moveModulesScreen.actions.Action;

public class TextureAction implements Action {
	private CrosshairEditor editor;
	private int[][] oldTexture, newTexture;

	public TextureAction(CrosshairEditor editor, int[][] oldTexture, int[][] newTexture) {
		this.editor = editor;
		this.oldTexture = oldTexture;
		this.newTexture = newTexture;
	}

	@Override
	public void redo() {
		editor.setTexture(newTexture);

	}

	@Override
	public void undo() {
		editor.setTexture(oldTexture);
	}
}
