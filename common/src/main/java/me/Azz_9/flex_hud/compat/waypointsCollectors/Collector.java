package me.Azz_9.flex_hud.compat.waypointsCollectors;

import net.minecraft.client.Minecraft;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.DimensionTracker;
import me.Azz_9.flex_hud.client.modules.hud.custom.Compass;
import me.Azz_9.flex_hud.client.tickables.Tickable;

public abstract class Collector<Waypoint extends Compass.ModdedWaypoint> implements Tickable {
	private final @NotNull List<@NotNull Waypoint> waypoints = new ArrayList<>();

	public boolean available = false;
	private boolean joinedWorld;

	public abstract void init();

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
