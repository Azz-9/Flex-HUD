package me.Azz_9.better_hud.client.Interface;

public interface TrackableChange {
	boolean hasChanged();
	void cancel();
	default boolean isValid() {
		return true;
	};
}
