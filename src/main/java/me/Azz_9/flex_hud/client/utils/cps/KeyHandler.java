package me.Azz_9.flex_hud.client.utils.cps;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

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

		int keyAttackValue = KeyMappingHelper.getBoundKeyOf(MINECRAFT.options.keyAttack).getValue();
		int keyUseValue = KeyMappingHelper.getBoundKeyOf(MINECRAFT.options.keyUse).getValue();

		if (action == GLFW.GLFW_PRESS) {
			if (!isAttackKeyPressed && button == keyAttackValue) {
				isAttackKeyPressed = true;
				CpsUtils.onAttackKeyPress();
				return;

			} else if (!isUseKeyPressed && button == keyUseValue) {
				isUseKeyPressed = true;
				CpsUtils.onUseKeyPress();
				return;
			}
		}
		isAttackKeyPressed = false;
		isUseKeyPressed = false;
	}
}
