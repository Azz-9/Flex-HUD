package me.Azz_9.flex_hud.compat.waypointsCollectors;

import journeymap.common.waypoint.WaypointStore;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.Compass;
import me.Azz_9.flex_hud.client.tickables.TickRegistry;
import me.Azz_9.flex_hud.client.tickables.Tickable;
import me.Azz_9.flex_hud.client.utils.FlexHudLogger;
import me.Azz_9.flex_hud.compat.CompatManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.lang.reflect.Method;

public class JourneyMapWaypointCollector extends Collector<Compass.JourneyMapWaypoint> implements Tickable {
	// Cached methods
	private Method getX, getZ, getColor, isEnabled, isInPlayerDimension;
	private Method getIdentifier, getTextureWidth, getTextureHeight;

	private boolean available;

	public JourneyMapWaypointCollector() {
		try {
			Class<?> wpClass = Class.forName("journeymap.client.waypoint.ClientWaypointImpl");

			getX = wpClass.getMethod("getX");
			getZ = wpClass.getMethod("getZ");
			getColor = wpClass.getMethod("getColor");
			isEnabled = wpClass.getMethod("isEnabled");
			isInPlayerDimension = wpClass.getMethod("isInPlayerDimension");

			getIdentifier = wpClass.getMethod("getIconResourceLocation");
			getTextureWidth = wpClass.getMethod("getIconTextureWidth");
			getTextureHeight = wpClass.getMethod("getIconTextureHeight");

			available = true;
		} catch (Throwable e) {
			FlexHudLogger.error("JourneyMapWaypointCollector could not be initialized : " + e.getMessage());
			available = false;
		}

		TickRegistry.register(this);
	}

	@Override
	public void initCompassList() {
		ModulesHelper.getInstance().compass.setJourneyMapWaypoints(getWaypoints());
	}

	@Override
	public void updateWaypoints() {
		if (!available) return;

		getWaypoints().clear();

		try {
			for (Object waypoint : WaypointStore.getInstance().getAll()) {
				getWaypoints().add(new Compass.JourneyMapWaypoint(
						(int) getX.invoke(waypoint),
						(int) getZ.invoke(waypoint),
						(int) getColor.invoke(waypoint),
						!(boolean) isEnabled.invoke(waypoint),
						Identifier.tryParse(getIdentifier.invoke(waypoint).toString()),
						(int) getTextureWidth.invoke(waypoint),
						(int) getTextureHeight.invoke(waypoint),
						(boolean) isInPlayerDimension.invoke(waypoint)
				));
			}
		} catch (Throwable ignored) {
		}
	}

	@Override
	public boolean shouldTick() {
		return CompatManager.isJourneyMapLoaded() &&
				ModulesHelper.getInstance().compass.isEnabled() &&
				ModulesHelper.getInstance().compass.showJourneyMapWaypoints.getValue() &&
				isJoinedWorld();
	}

	@Override
	public void tick(MinecraftClient client) {
		updateWaypoints();
	}
}
