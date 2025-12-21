package me.Azz_9.flex_hud.compat;

import journeymap.common.waypoint.WaypointStore;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.Compass;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class JourneyMapWaypointCollector {
	private final List<Compass.JourneyMapWaypoint> waypoints = new ArrayList<>();

	public void updateWaypoints() {
		waypoints.clear();

		try {
			for (Object waypoint : WaypointStore.getInstance().getAll()) {
				Object icon = waypoint.getClass().getMethod("getIcon").invoke(waypoint);

				waypoints.add(new Compass.JourneyMapWaypoint(
						(int) waypoint.getClass().getMethod("getX").invoke(waypoint),
						(int) waypoint.getClass().getMethod("getZ").invoke(waypoint),
						(int) waypoint.getClass().getMethod("getColor").invoke(waypoint),
						!(boolean) waypoint.getClass().getMethod("isEnabled").invoke(waypoint),
						Identifier.tryParse(icon.getClass().getMethod("getIdentifier").invoke(icon).toString()),
						(int) waypoint.getClass().getMethod("getIconTextureWidth").invoke(waypoint),
						(int) waypoint.getClass().getMethod("getIconTextureHeight").invoke(waypoint),
						(boolean) waypoint.getClass().getMethod("isInPlayerDimension").invoke(waypoint)
				));
			}
		} catch (Exception ignored) {
		}
	}

	public List<Compass.JourneyMapWaypoint> getWaypoints() {
		return waypoints;
	}
}
