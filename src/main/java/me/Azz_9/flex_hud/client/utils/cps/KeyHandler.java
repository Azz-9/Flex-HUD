package me.Azz_9.flex_hud.client.utils.cps;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
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

		int attackKeyCode = KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.attackKey).getCode();
		int useKeyCode = KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.useKey).getCode();

		if ((button != attackKeyCode && button != useKeyCode) ||
				(button == attackKeyCode && !ModulesHelper.getInstance().cps.showLeftClick.getValue() && !ModulesHelper.getInstance().keyStrokes.isEnabled()) ||
				(button == useKeyCode && !ModulesHelper.getInstance().cps.showRightClick.getValue() && !ModulesHelper.getInstance().keyStrokes.isEnabled())) {
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
