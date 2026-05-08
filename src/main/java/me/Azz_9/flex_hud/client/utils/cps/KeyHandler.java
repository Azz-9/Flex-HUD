package me.Azz_9.flex_hud.client.utils.cps;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import org.lwjgl.glfw.GLFW;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;

public class KeyHandler {
	private static boolean isAttackKeyPressed = false;
	private static boolean isUseKeyPressed = false;

	public static void onKey(int button, int action) {

		if (!ModulesHelper.getInstance().isEnabled.getValue()) {
			isAttackKeyPressed = false;
			isUseKeyPressed = false;
			return;
		}

		int attackKeyCode = KeyBindingHelper.getBoundKeyOf(CLIENT.options.attackKey).getCode();
		int useKeyCode = KeyBindingHelper.getBoundKeyOf(CLIENT.options.useKey).getCode();

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
