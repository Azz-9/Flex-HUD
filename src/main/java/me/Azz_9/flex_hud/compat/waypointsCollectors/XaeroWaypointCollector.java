package me.Azz_9.flex_hud.compat.waypointsCollectors;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.Compass;
import me.Azz_9.flex_hud.client.tickables.TickRegistry;
import me.Azz_9.flex_hud.client.tickables.Tickable;
import me.Azz_9.flex_hud.client.utils.compass.DimensionTracker;
import me.Azz_9.flex_hud.compat.CompatManager;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class XaeroWaypointCollector extends Collector<Compass.XaeroWaypoint> implements Tickable {
	//private MinimapWorld minimapWorld;
	private final List<Compass.XaeroWaypoint> waypoints = new ArrayList<>();

	public boolean available = false;

	public XaeroWaypointCollector() {
		TickRegistry.register(this);
	}

	public void init() {
		try {
			/*MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
			if (session == null) {
				available = false;
				return;
			}

			minimapWorld = session.getWorldManager().getCurrentWorld();
			available = minimapWorld != null;*/
		} catch (Throwable t) {
			available = false;
		}
	}

	@Override
	public void initCompassList() {
		ModulesHelper.getInstance().compass.setXaeroWaypoints(getWaypoints());
	}

	@Override
	public void updateWaypoints() {
		/*if (!available || minimapWorld == null) return;

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
		}*/
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

	public List<Compass.XaeroWaypoint> getWaypoints() {
		return waypoints;
	}

	@Override
	public boolean shouldTick() {
		return CompatManager.isXaeroMinimapLoaded() &&
				ModulesHelper.getInstance().compass.isEnabled() &&
				ModulesHelper.getInstance().compass.showXaerosMapWaypoints.getValue();
	}

	@Override
	public void tick(Minecraft minecraft) {
		if ((isJoinedWorld() && !available) || DimensionTracker.shouldInit) {
			init();
		} else {
			DimensionTracker.check();
		}

		updateWaypoints();
	}
}