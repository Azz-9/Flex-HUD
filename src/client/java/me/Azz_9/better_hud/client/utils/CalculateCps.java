package me.Azz_9.better_hud.client.utils;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;

import java.util.Deque;
import java.util.LinkedList;

public class CalculateCps {
	private static final CalculateCps INSTANCE = new CalculateCps();
	private final MinecraftClient client = MinecraftClient.getInstance();

	private static final Deque<Long> leftClickTimestamps = new LinkedList<>();
	private static final Deque<Long> rightClickTimestamps = new LinkedList<>();

	private final int attackKeyCode = KeyBindingHelper.getBoundKeyOf(client.options.attackKey).getCode();
	private final int useKeyCode = KeyBindingHelper.getBoundKeyOf(client.options.useKey).getCode();

	public void onKeyPress(int button) {
		if (button == attackKeyCode && ModConfig.getInstance().showLeftClickCPS) {
			leftClickTimestamps.add(System.currentTimeMillis());
		} else if (button == useKeyCode && ModConfig.getInstance().showRightClickCPS){
			rightClickTimestamps.add(System.currentTimeMillis());
		}
	}

	private void updateLeftCPS() {
		long currentTime = System.currentTimeMillis();
		leftClickTimestamps.removeIf(click -> (currentTime - click > 1000L));
	}

	private void updateRightCPS() {
		long currentTime = System.currentTimeMillis();
		rightClickTimestamps.removeIf(click -> (currentTime - click > 1000L));
	}

	public int getLeftCps() {
		updateLeftCPS();
		return leftClickTimestamps.size();
	}

	public int getRightCps() {
		updateRightCPS();
        return rightClickTimestamps.size();
    }

	public static CalculateCps getInstance() {
		return INSTANCE;
	}
}
