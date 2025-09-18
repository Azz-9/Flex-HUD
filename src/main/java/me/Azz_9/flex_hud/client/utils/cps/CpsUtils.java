package me.Azz_9.flex_hud.client.utils.cps;

import me.Azz_9.flex_hud.client.configurableModules.JsonConfigHelper;

import java.util.Deque;
import java.util.LinkedList;

public class CpsUtils {
	private static final Deque<Long> leftClickTimestamps = new LinkedList<>();
	private static final Deque<Long> rightClickTimestamps = new LinkedList<>();

	public static void onAttackKeyPress() {
		if (JsonConfigHelper.getInstance().cps.showLeftClick.getValue()) {
			leftClickTimestamps.add(System.currentTimeMillis());
		}
	}

	public static void onUseKeyPress() {
		if (JsonConfigHelper.getInstance().cps.showRightClick.getValue()) {
			rightClickTimestamps.add(System.currentTimeMillis());
		}
	}

	public static void updateLeftCPS() {
		long currentTime = System.currentTimeMillis();
		while (!leftClickTimestamps.isEmpty() && currentTime - leftClickTimestamps.getFirst() > 1000L) {
			leftClickTimestamps.removeFirst();
		}
	}

	public static void updateRightCPS() {
		long currentTime = System.currentTimeMillis();
		while (!rightClickTimestamps.isEmpty() && currentTime - rightClickTimestamps.getFirst() > 1000L) {
			rightClickTimestamps.removeFirst();
		}
	}

	public static int getLeftCps() {
		updateLeftCPS();
		return leftClickTimestamps.size();
	}

	public static int getRightCps() {
		updateRightCPS();
		return rightClickTimestamps.size();
	}
}
