package me.Azz_9.flex_hud.client.utils.cps;

import me.Azz_9.flex_hud.client.configurableModules.JsonConfigHelper;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
	private static boolean isAttackKeyPressed = false;
	private static boolean isUseKeyPressed = false;

	public static void onKey(int button, int action) {

		if (!JsonConfigHelper.getInstance().isEnabled || !JsonConfigHelper.getInstance().cps.isEnabled()) {
			isAttackKeyPressed = false;
			isUseKeyPressed = false;
			return;
		}

		int attackKeyCode = KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.attackKey).getCode();
		int useKeyCode = KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.useKey).getCode();

		if ((button != attackKeyCode && button != useKeyCode) || (button == attackKeyCode && !JsonConfigHelper.getInstance().cps.showLeftClick.getValue()) || (button == useKeyCode && !JsonConfigHelper.getInstance().cps.showRightClick.getValue())) {
			isAttackKeyPressed = false;
			isUseKeyPressed = false;
			return;
		}
		if (action == GLFW.GLFW_PRESS) {
			if (!isAttackKeyPressed && button == attackKeyCode) {
				isAttackKeyPressed = true;
				CpsUtils.onAttackKeyPress();
				return;

			} else if (!isUseKeyPressed && button == useKeyCode) {
				isUseKeyPressed = true;
				CpsUtils.onUseKeyPress();
				return;
			}
		}
		isAttackKeyPressed = false;
		isUseKeyPressed = false;
	}
}
