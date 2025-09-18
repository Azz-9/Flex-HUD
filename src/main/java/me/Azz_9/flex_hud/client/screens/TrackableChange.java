package me.Azz_9.flex_hud.client.screens;

public interface TrackableChange {
	boolean hasChanged();

	void cancel();

	default boolean isValid() {
		return true;
	}
}
