package me.Azz_9.better_hud.client.screens;

public interface TrackableChange {
	void setToInitialState();

	boolean hasChanged();

	void cancel();

	default boolean isValid() {
		return true;
	}
}
