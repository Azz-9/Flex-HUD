package me.Azz_9.flex_hud.client.customModules;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Variables {

	public static Variable<Float> yaw;
	public static Variable<Float> pitch;
	public static Variable<Double> playerX;
	public static Variable<Double> playerY;
	public static Variable<Double> playerZ;

	private static final Map<String, Variable<?>> VARIABLES = new LinkedHashMap<>();

	public static void init() {
		if (CLIENT.player == null) {
			throw new RuntimeException("Couldn't init variables because player is null");
		}

		yaw = register(Text.of("yaw"), Text.of("description"), "player.yaw", SafeSupplier.create(() -> (CLIENT.player.getYaw() % 360 + 360) % 360, 180f));
		pitch = register(Text.of("pitch"), Text.of("description longue oula y'en a des choses à dire, en vrai pas trop mais vzy faut parler quand même hein"), "player.pitch", SafeSupplier.create(() -> CLIENT.player.getPitch(), 0.0f));
		playerX = register(Text.of("playerX"), Text.of("description"), "player.x", SafeSupplier.create(() -> CLIENT.player.getX(), 0.0));
		playerY = register(Text.of("playerY"), Text.of("description"), "player.y", SafeSupplier.create(() -> CLIENT.player.getY(), 0.0));
		playerZ = register(Text.of("playerZ"), Text.of("description"), "player.z", SafeSupplier.create(() -> CLIENT.player.getZ(), 0.0));
	}

	public static Map<String, Variable<?>> getAllVariables() {
		return VARIABLES;
	}

	public static Variable<?> get(String key) {
		return VARIABLES.get(key);
	}

	private static <T> Variable<T> register(Text name, Text description, String key, Supplier<T> supplier) {
		Variable<T> variable = new Variable<>(name, description, key, supplier);
		VARIABLES.put(key, variable);
		return variable;
	}

	public static String resolveVariable(String key) {
		Variable<?> var = VARIABLES.get(key);
		return var != null ? var.resolve() : "";
	}
}
