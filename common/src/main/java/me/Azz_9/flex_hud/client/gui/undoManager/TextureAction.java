package me.Azz_9.flex_hud.client.gui.undoManager;

import me.Azz_9.flex_hud.client.gui.components.config.crosshairEditor.CrosshairEditor;

public class TextureAction implements Action {
	private final CrosshairEditor editor;
	private final int[][] oldTexture, newTexture;

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
