package me.Azz_9.flex_hud.compat.waypointsCollectors;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.common.waypoint.Waypoint;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.Compass;
import me.Azz_9.flex_hud.client.tickables.TickRegistry;
import me.Azz_9.flex_hud.client.tickables.Tickable;
import me.Azz_9.flex_hud.client.utils.compass.DimensionTracker;
import me.Azz_9.flex_hud.compat.CompatManager;

public class JourneyMapWaypointCollector extends Collector<Compass.JourneyMapWaypoint> implements Tickable {

	private final List<Compass.JourneyMapWaypoint> waypoints = new ArrayList<>();
	public boolean available = false;

	public JourneyMapWaypointCollector() {
		TickRegistry.register(this);
	}

	public void init() {
		try {
			available = JourneyMapIntegration.getAPI() != null;
		} catch (Throwable t) {
			available = false;
		}
	}

	@Override
	public void initCompassList() {
		ModulesHelper.getInstance().compass.setJourneyMapWaypoints(getWaypoints());
	}

	@Override
	public void updateWaypoints() {
		if (!available) return;

		waypoints.clear();

		if (MINECRAFT.level == null) return;

		try {
			IClientAPI api = JourneyMapIntegration.getAPI();
			if (api == null) return;

			List<? extends Waypoint> all = api.getAllWaypoints();
			for (Waypoint wp : all) {
				if (!wp.isEnabled()) continue;

				BlockPos pos = wp.getBlockPos();
				int color = wp.getColor();

				waypoints.add(new Compass.JourneyMapWaypoint(
						pos.getX(),
						pos.getZ(),
						color,
						!wp.isEnabled(),
						wp.getIconIdentifier(),
						wp.getIconTextureWidth(),
						wp.getIconTextureHeight(),
						wp.getDimensions().contains(MINECRAFT.level.dimension().identifier().toString())
				));
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

	public List<Compass.JourneyMapWaypoint> getWaypoints() {
		return waypoints;
	}

	@Override
	public boolean shouldTick() {
		return CompatManager.isJourneyMapLoaded() &&
				ModulesHelper.getInstance().compass.isEnabled() &&
				ModulesHelper.getInstance().compass.showJourneyMapWaypoints.getValue();
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
