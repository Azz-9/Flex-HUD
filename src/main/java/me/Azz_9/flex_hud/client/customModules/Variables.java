package me.Azz_9.flex_hud.client.customModules;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Variables {

	public static Variable<Float> yaw;
	public static Variable<Float> pitch;
	public static Variable<Double> playerX;
	public static Variable<Double> playerY;
	public static Variable<Double> playerZ;

	private static final Map<String, Variable<?>> VARIABLES = new LinkedHashMap<>();
	private static final List<Variable<?>> TICK_UPDATE_VARIABLES = new ArrayList<>();
	private static final List<Variable<?>> FRAME_UPDATE_VARIABLES = new ArrayList<>();

	private enum UpdateFrequency {
		FRAME,
		TICK
	}

	public static void init() {
		if (CLIENT.player == null) {
			throw new RuntimeException("Couldn't init variables because player is null");
		}

		yaw = register(Text.of("yaw"), Text.of("description"), "player.yaw", SafeSupplier.create(() -> (CLIENT.player.getYaw() % 360 + 360) % 360, 180f), UpdateFrequency.FRAME);
		pitch = register(Text.of("pitch"), Text.of("description longue oula y'en a des choses à dire, en vrai pas trop mais vzy faut parler quand même hein"), "player.pitch", SafeSupplier.create(() -> CLIENT.player.getPitch(), 0.0f), UpdateFrequency.FRAME);
		playerX = register(Text.of("playerX"), Text.of("description"), "player.x", SafeSupplier.create(() -> CLIENT.player.getX(), 0.0), UpdateFrequency.TICK);
		playerY = register(Text.of("playerY"), Text.of("description"), "player.y", SafeSupplier.create(() -> CLIENT.player.getY(), 0.0), UpdateFrequency.TICK);
		playerZ = register(Text.of("playerZ"), Text.of("description"), "player.z", SafeSupplier.create(() -> CLIENT.player.getZ(), 0.0), UpdateFrequency.TICK);
	}

	public static Map<String, Variable<?>> getAllVariables() {
		return VARIABLES;
	}

	public static Variable<?> get(String key) {
		return VARIABLES.get(key);
	}

	private static <T> Variable<T> register(Text name, Text description, String key, Supplier<T> supplier, UpdateFrequency updateFrequency) {
		Variable<T> variable = new Variable<>(name, description, key, supplier);
		VARIABLES.put(key, variable);
		switch (updateFrequency) {
			case TICK -> TICK_UPDATE_VARIABLES.add(variable);
			case FRAME -> FRAME_UPDATE_VARIABLES.add(variable);
		}

		return variable;
	}

	public static void tick() {
		for (Variable<?> tickUpdateVariable : TICK_UPDATE_VARIABLES) {
			tickUpdateVariable.updateValue();
		}
	}

	public static void frame() {
		for (Variable<?> frameUpdateVariable : FRAME_UPDATE_VARIABLES) {
			frameUpdateVariable.updateValue();
		}
	}
}
