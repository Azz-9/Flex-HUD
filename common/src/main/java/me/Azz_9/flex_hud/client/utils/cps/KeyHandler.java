package me.Azz_9.flex_hud.client.utils.cps;

import me.Azz_9.flex_hud.client.configurableModules.JsonConfigHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
	private static boolean isAttackKeyPressed = false;
	private static boolean isUseKeyPressed = false;

	private static KeyHandlerBridge instance;

	public interface KeyHandlerBridge {
		InputUtil.Key getBoundKeyOf(KeyBinding keyBinding);
	}

	public static void setInstance(KeyHandlerBridge bridge) {
		instance = bridge;
	}

	public static InputUtil.Key getBoundKeyOf(KeyBinding keyBinding) {
		return instance.getBoundKeyOf(keyBinding);
	}

	public static void onKey(int button, int action) {

		if (!JsonConfigHelper.getInstance().isEnabled || !JsonConfigHelper.getInstance().cps.isEnabled()) {
			isAttackKeyPressed = false;
			isUseKeyPressed = false;
			return;
		}

		int attackKeyCode = getBoundKeyOf(MinecraftClient.getInstance().options.attackKey).getCode();
		int useKeyCode = getBoundKeyOf(MinecraftClient.getInstance().options.useKey).getCode();

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
