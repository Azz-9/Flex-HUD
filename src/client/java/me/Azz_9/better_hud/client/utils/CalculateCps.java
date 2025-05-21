package me.Azz_9.better_hud.client.utils;

import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;

import java.util.Deque;
import java.util.LinkedList;

public class CalculateCps {
	private static final Deque<Long> leftClickTimestamps = new LinkedList<>();
	private static final Deque<Long> rightClickTimestamps = new LinkedList<>();

	private static final int attackKeyCode = KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.attackKey).getCode();
	private static final int useKeyCode = KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.useKey).getCode();

	public static void onKeyPress(int button) {
		if (button == attackKeyCode && JsonConfigHelper.getInstance().cps.showLeftClick) {
			leftClickTimestamps.add(System.currentTimeMillis());
		} else if (button == useKeyCode && JsonConfigHelper.getInstance().cps.showRightClick) {
			rightClickTimestamps.add(System.currentTimeMillis());
		}
	}

	private static void updateLeftCPS() {
		long currentTime = System.currentTimeMillis();
		while (!leftClickTimestamps.isEmpty() && currentTime - leftClickTimestamps.getFirst() > 1000L) {
			leftClickTimestamps.removeFirst();
		}
		//leftClickTimestamps.removeIf(click -> (currentTime - click > 1000L));
	}

	private static void updateRightCPS() {
		long currentTime = System.currentTimeMillis();
		while (!rightClickTimestamps.isEmpty() && currentTime - rightClickTimestamps.getFirst() > 1000L) {
			rightClickTimestamps.removeFirst();
		}
		//rightClickTimestamps.removeIf(click -> (currentTime - click > 1000L));
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
