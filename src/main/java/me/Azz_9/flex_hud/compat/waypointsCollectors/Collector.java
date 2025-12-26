package me.Azz_9.flex_hud.compat.waypointsCollectors;

import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.Compass;

import java.util.ArrayList;
import java.util.List;

public abstract class Collector<Waypoint extends Compass.ModdedWaypoint> {
	private final List<Waypoint> waypoints = new ArrayList<>();

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

	public List<Waypoint> getWaypoints() {
		return waypoints;
	}
}
