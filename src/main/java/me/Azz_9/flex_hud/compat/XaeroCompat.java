package me.Azz_9.flex_hud.compat;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class XaeroCompat {
	private static Object minimapWorld = null;
	private static Method getIterableWaypointSets = null;
	private static Method addToMethod = null;

	public static boolean available = false;

	private static final List<Object> waypoints = new ArrayList<>();

	public static void init() {
		try {
			Class<?> builtInHudModulesClass = Class.forName("xaero.hud.minimap.BuiltInHudModules");
			Field minimapField = builtInHudModulesClass.getField("MINIMAP");
			Object minimapModule = minimapField.get(null);

			Method getCurrentSession = minimapModule.getClass().getMethod("getCurrentSession");
			Object minimapSession = getCurrentSession.invoke(minimapModule);
			if (minimapSession == null) {
				available = false;
				return;
			}

			Method getWorldManager = minimapSession.getClass().getMethod("getWorldManager");
			Object worldManager = getWorldManager.invoke(minimapSession);

			Method getCurrentWorld = worldManager.getClass().getMethod("getCurrentWorld");
			minimapWorld = getCurrentWorld.invoke(worldManager);

			if (minimapWorld == null) {
				available = false;
				return;
			}

			Class<?> minimapWorldClass = minimapWorld.getClass();
			getIterableWaypointSets = minimapWorldClass.getMethod("getIterableWaypointSets");

			Class<?> waypointSetClass = Class.forName("xaero.hud.minimap.waypoint.set.WaypointSet");
			addToMethod = waypointSetClass.getMethod("addTo", List.class);

			available = true;

		} catch (Exception e) {
			available = false;
		}
	}

	public static void updateWaypoints() {
		if (minimapWorld == null || getIterableWaypointSets == null || addToMethod == null) {
			return;
		}

		try {
			waypoints.clear();
			Iterable<?> sets = (Iterable<?>) getIterableWaypointSets.invoke(minimapWorld);

			for (Object set : sets) {
				addToMethod.invoke(set, waypoints);
			}
		} catch (Exception ignored) {
		}
	}

	public static boolean isXaerosMinimapLoaded() {
		return FabricLoader.getInstance().isModLoaded("xaerominimap");
	}

	public static List<Object> getWaypoints() {
		return waypoints;
	}

	public static class WaypointReflect {
		private static Method getX, getZ, getInitials, getColor, isDisabled;
		private static boolean initialized = false;

		public static void init(Object waypoint) {
			if (initialized) return;
			try {
				Class<?> cls = waypoint.getClass();
				getX = cls.getMethod("getX");
				getZ = cls.getMethod("getZ");
				getInitials = cls.getMethod("getInitials");
				getColor = cls.getMethod("getColor");
				isDisabled = cls.getMethod("isDisabled");
				initialized = true;
			} catch (Exception ignored) {
			}
		}

		public static int getX(Object waypoint) throws Exception {
			return (int) getX.invoke(waypoint);
		}

		public static int getZ(Object waypoint) throws Exception {
			return (int) getZ.invoke(waypoint);
		}

		public static String getInitials(Object waypoint) throws Exception {
			return (String) getInitials.invoke(waypoint);
		}

		public static int getColor(Object waypoint) throws Exception {
			return (int) getColor.invoke(waypoint);
		}

		public static boolean isDisabled(Object waypoint) throws Exception {
			return (boolean) isDisabled.invoke(waypoint);
		}
	}
}
