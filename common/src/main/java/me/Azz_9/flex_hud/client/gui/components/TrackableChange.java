package me.Azz_9.flex_hud.client.gui.components;

public interface TrackableChange {
	boolean hasChanged();

	void revertChanges();

	default boolean isValid() {
		return true;
	}
}
