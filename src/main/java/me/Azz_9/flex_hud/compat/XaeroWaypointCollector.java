package me.Azz_9.flex_hud.compat;

import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.Compass;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;

import java.util.ArrayList;
import java.util.List;

public class XaeroWaypointCollector {
	private MinimapWorld minimapWorld;
	private final List<Compass.XaeroWaypoint> waypoints = new ArrayList<>();

	public boolean available = false;

	public void init() {
		try {
			MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
			if (session == null) {
				available = false;
				return;
			}

			minimapWorld = session.getWorldManager().getCurrentWorld();
			available = minimapWorld != null;
		} catch (Throwable t) {
			available = false;
		}
	}

	public void updateWaypoints() {
		if (!available || minimapWorld == null) return;

		waypoints.clear();

		try {
			Iterable<WaypointSet> sets = minimapWorld.getIterableWaypointSets();
			for (WaypointSet set : sets) {
				for (Waypoint waypoint : set.getWaypoints()) {
					waypoints.add(new Compass.XaeroWaypoint(
							waypoint.getX(),
							waypoint.getZ(),
							waypoint.getWaypointColor().getHex(),
							waypoint.isDisabled(),
							waypoint.getInitials()
					));
				}
			}
		} catch (Throwable ignored) {
		}
	}

	public List<Compass.XaeroWaypoint> getWaypoints() {
		return waypoints;
	}
}
