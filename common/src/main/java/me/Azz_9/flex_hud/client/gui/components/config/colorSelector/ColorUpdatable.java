package me.Azz_9.flex_hud.client.gui.components.config.colorSelector;

abstract class ColorUpdatable {

	private boolean ignoreUpdates = false;

	public void runIgnoringUpdates(Runnable task) {
		ignoreUpdates = true;
		try {
			task.run();
		} finally {
			ignoreUpdates = false;
		}
	}

	public boolean isIgnoringUpdates() {
		return ignoreUpdates;
	}

	abstract void onUpdateColor(ColorSelector.ColorSelectorElement element);
}
