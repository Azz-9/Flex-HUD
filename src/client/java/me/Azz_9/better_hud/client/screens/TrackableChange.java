package me.Azz_9.better_hud.client.screens;

public interface TrackableChange {
	boolean hasChanged();

	void cancel();

	boolean isValid();
}
