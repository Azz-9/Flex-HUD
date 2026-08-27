package me.Azz_9.flex_hud.utils;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.mojang.blaze3d.platform.InputConstants;

import me.Azz_9.flex_hud.client.modules.Modules;

public class KeyHandler {
	private static boolean isAttackKeyPressed = false;
	private static boolean isUseKeyPressed = false;

	public static void onKey(int button, int action) {

		if (!Modules.getInstance().isEnabled.getValue()) {
			isAttackKeyPressed = false;
			isUseKeyPressed = false;
			return;
		}

		int keyAttackValue = MINECRAFT.options.keyAttack.key.getValue();
		int keyUseValue = MINECRAFT.options.keyUse.key.getValue();

		if (action == InputConstants.PRESS) {
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
