package me.Azz_9.better_hud.client.utils.cps;

import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;

import java.util.Deque;
import java.util.LinkedList;

public class CalculateCps {
	private static final Deque<Long> leftClickTimestamps = new LinkedList<>();
	private static final Deque<Long> rightClickTimestamps = new LinkedList<>();

	/*public static void onKeyPress(int button) {
		int attackKeyCode = KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.attackKey).getCode();
		int useKeyCode = KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.useKey).getCode();

		if (button == attackKeyCode && JsonConfigHelper.getInstance().cps.showLeftClick) {
			leftClickTimestamps.add(System.currentTimeMillis());
		} else if (button == useKeyCode && JsonConfigHelper.getInstance().cps.showRightClick) {
			rightClickTimestamps.add(System.currentTimeMillis());
		}
	}*/

	public static void onAttackKeyPress() {
		if (JsonConfigHelper.getInstance().cps.showLeftClick) {
			leftClickTimestamps.add(System.currentTimeMillis());
		}
	}

	public static void onUseKeyPress() {
		if (JsonConfigHelper.getInstance().cps.showRightClick) {
			rightClickTimestamps.add(System.currentTimeMillis());
		}
	}

	private static void updateLeftCPS() {
		long currentTime = System.currentTimeMillis();
		while (!leftClickTimestamps.isEmpty() && currentTime - leftClickTimestamps.getFirst() > 1000L) {
			leftClickTimestamps.removeFirst();
		}
	}

	private static void updateRightCPS() {
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
