package me.Azz_9.flex_hud.compat.waypointsCollectors;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.hud.custom.Compass;
import me.Azz_9.flex_hud.client.tickables.TickRegistry;
import me.Azz_9.flex_hud.client.tickables.Tickable;
import me.Azz_9.flex_hud.compat.CompatManager;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;

public class XaeroWaypointCollector extends Collector<Compass.XaeroWaypoint> implements Tickable {
	private MinimapWorld minimapWorld;
	private final List<Compass.XaeroWaypoint> waypoints = new ArrayList<>();

	public XaeroWaypointCollector() {
		TickRegistry.register(this);
	}

	@Override
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

	@Override
	public void initCompassList() {
		Modules.getInstance().compass.setXaeroWaypoints(getWaypoints());
	}

	@Override
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

	@Override
	public void onJoinWorld() {
		super.onJoinWorld();
		init();
	}

	@Override
	public void onLeaveWorld() {
		super.onLeaveWorld();
		available = false;
	}

	public @NonNull List<Compass.XaeroWaypoint> getWaypoints() {
		return waypoints;
	}

	@Override
	public boolean shouldTick() {
		return CompatManager.isXaeroMinimapLoaded() &&
				Modules.getInstance().compass.isEnabled() &&
				Modules.getInstance().compass.showXaerosMapWaypoints.getValue();
	}
}