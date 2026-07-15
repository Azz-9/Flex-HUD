package me.Azz_9.flex_hud.compat.waypointsCollectors;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.modules.hud.custom.Compass;

public abstract class Collector<Waypoint extends Compass.ModdedWaypoint> {
	private final @NotNull List<@NotNull Waypoint> waypoints = new ArrayList<>();

	private boolean joinedWorld;

	public abstract void initCompassList();

	public abstract void updateWaypoints();

	public void onJoinWorld() {
		joinedWorld = true;
	}

	public void onLeaveWorld() {
		joinedWorld = false;
	}

	public boolean isJoinedWorld() {
		return joinedWorld;
	}

	public @NotNull List<@NotNull Waypoint> getWaypoints() {
		return waypoints;
	}
}
