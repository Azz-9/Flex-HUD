package me.Azz_9.better_hud.client.utils;

import me.Azz_9.better_hud.modMenu.ModConfig;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
	public static final KeyHandler INSTANCE = new KeyHandler();
	private final MinecraftClient client = MinecraftClient.getInstance();
	private boolean isPressed = false;

	public void onKey(int button, int action) {
		if (!ModConfig.getInstance().isEnabled || !ModConfig.getInstance().cps.enabled || !(ModConfig.getInstance().cps.showLeftClick || ModConfig.getInstance().cps.showRightClick)) {
			this.isPressed = false;
			return;
		}

		int attackKeyCode = KeyBindingHelper.getBoundKeyOf(client.options.attackKey).getCode();
		int useKeyCode = KeyBindingHelper.getBoundKeyOf(client.options.useKey).getCode();

		if (button != attackKeyCode && button != useKeyCode || (button == attackKeyCode && !ModConfig.getInstance().cps.showLeftClick) || (button == useKeyCode && !ModConfig.getInstance().cps.showRightClick)) {
			this.isPressed = false;
			return;
		}
		if (action == GLFW.GLFW_PRESS) {
			if (!this.isPressed) {
				this.isPressed = true;
				CalculateCps.getInstance().onKeyPress(button);
				return;
			}
		}
		this.isPressed = false;
	}

	public static KeyHandler getInstance() {
		return INSTANCE;
	}
}
