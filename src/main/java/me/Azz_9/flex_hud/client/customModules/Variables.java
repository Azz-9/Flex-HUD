package me.Azz_9.flex_hud.client.customModules;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.text.Text;
import net.minecraft.world.biome.BiomeKeys;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Variables {

	private static final String VARIABLE_NAME_BASE_KEY = "flex_hud.custom_modules.variable.name";
	private static final String VARIABLE_DESCRIPTION_BASE_KEY = "flex_hud.custom_modules.variable.description";
	private static final Map<String, Variable<?>> VARIABLES = new LinkedHashMap<>();
	private static final List<Variable<?>> TICK_UPDATE_VARIABLES = new ArrayList<>();
	private static final List<Variable<?>> FRAME_UPDATE_VARIABLES = new ArrayList<>();

	private enum UpdateFrequency {
		FRAME,
		TICK
	}

	public static void init() {
		if (CLIENT.player == null || CLIENT.world == null) {
			throw new RuntimeException("Couldn't init variables because player is null");
		}

		register("player.yaw", SafeSupplier.create(() -> (CLIENT.player.getYaw() % 360 + 360) % 360, 180f), UpdateFrequency.FRAME);
		register("player.pitch", SafeSupplier.create(() -> CLIENT.player.getPitch(), 0.0f), UpdateFrequency.FRAME);
		register("player.x", SafeSupplier.create(() -> CLIENT.player.getX(), 0.0), UpdateFrequency.TICK);
		register("player.y", SafeSupplier.create(() -> CLIENT.player.getY(), 0.0), UpdateFrequency.TICK);
		register("player.z", SafeSupplier.create(() -> CLIENT.player.getZ(), 0.0), UpdateFrequency.TICK);
		register("nether.player.x", SafeSupplier.create(() -> CLIENT.player.getX() / 8, 0), UpdateFrequency.TICK);
		register("nether.player.z", SafeSupplier.create(() -> CLIENT.player.getZ() / 8, 0), UpdateFrequency.TICK);
		register("biome", SafeSupplier.create(() -> CLIENT.world.getBiome(CLIENT.player.getBlockPos()).getKeyOrValue().map(key -> key.getValue().getPath(), value -> "[unregistered " + value + "]"), BiomeKeys.PLAINS), UpdateFrequency.TICK);
		register("fps", CLIENT::getCurrentFps, UpdateFrequency.TICK);
		//register("player.gamemode", SafeSupplier.create(() -> Objects.requireNonNull(CLIENT.player.getGameMode()).getSimpleTranslatableName().getString(), Text.translatable("selectWorld.gameMode.survival")), UpdateFrequency.TICK);
	}

	public static Map<String, Variable<?>> getAllVariables() {
		return VARIABLES;
	}

	public static Variable<?> get(String key) {
		return VARIABLES.get(key);
	}

	private static <T> void register(String key, Supplier<T> supplier, UpdateFrequency updateFrequency) {
		Variable<T> variable = new Variable<>(
				Text.translatable(VARIABLE_NAME_BASE_KEY + "." + key),
				Text.translatable(VARIABLE_DESCRIPTION_BASE_KEY + "." + key),
				key,
				supplier
		);
		VARIABLES.put(key, variable);
		switch (updateFrequency) {
			case TICK -> TICK_UPDATE_VARIABLES.add(variable);
			case FRAME -> FRAME_UPDATE_VARIABLES.add(variable);
		}
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
