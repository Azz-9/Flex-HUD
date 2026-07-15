package me.Azz_9.flex_hud.utils;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import org.lwjgl.glfw.GLFW;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.mixin.KeyMappingAccessor;

public class KeyHandler {
	private static boolean isAttackKeyPressed = false;
	private static boolean isUseKeyPressed = false;

	public static void onKey(int button, int action) {

		if (!Modules.getInstance().isEnabled.getValue()) {
			isAttackKeyPressed = false;
			isUseKeyPressed = false;
			return;
		}

		int keyAttackValue = ((KeyMappingAccessor) MINECRAFT.options.keyAttack).getBoundKey().getValue();
		int keyUseValue = ((KeyMappingAccessor) MINECRAFT.options.keyUse).getBoundKey().getValue();

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
