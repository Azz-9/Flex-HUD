package me.Azz_9.flex_hud.client.utils.cps;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
	private static boolean isAttackKeyPressed = false;
	private static boolean isUseKeyPressed = false;

	public static void onKey(int button, int action) {

		if (!ModulesHelper.getInstance().isEnabled.getValue() || !ModulesHelper.getInstance().cps.isEnabled() && !ModulesHelper.getInstance().keyStrokes.isEnabled()) {
			isAttackKeyPressed = false;
			isUseKeyPressed = false;
			return;
		}

		int keyAttackValue = KeyMappingHelper.getBoundKeyOf(Minecraft.getInstance().options.keyAttack).getValue();
		int keyUseValue = KeyMappingHelper.getBoundKeyOf(Minecraft.getInstance().options.keyUse).getValue();

		if ((button != keyAttackValue && button != keyUseValue) ||
				(button == keyAttackValue && !ModulesHelper.getInstance().cps.showLeftClick.getValue() && !ModulesHelper.getInstance().keyStrokes.isEnabled()) ||
				(button == keyUseValue && !ModulesHelper.getInstance().cps.showRightClick.getValue() && !ModulesHelper.getInstance().keyStrokes.isEnabled())) {
			isAttackKeyPressed = false;
			isUseKeyPressed = false;
			return;
		}
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
