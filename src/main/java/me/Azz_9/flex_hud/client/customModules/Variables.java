package me.Azz_9.flex_hud.client.customModules;

import static java.util.Objects.requireNonNull;
import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;
import static me.Azz_9.flex_hud.client.customModules.Variables.UpdateFrequency.*;

import net.minecraft.SharedConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import me.Azz_9.flex_hud.client.tickables.MemoryUsageTickable;
import me.Azz_9.flex_hud.client.tickables.SpeedTickable;
import me.Azz_9.flex_hud.client.utils.PingUtils;
import me.Azz_9.flex_hud.client.utils.cps.CpsUtils;

public class Variables {

	private static final String VARIABLE_NAME_BASE_KEY = "flex_hud.custom_modules.variable.name";
	private static final String VARIABLE_DESCRIPTION_BASE_KEY = "flex_hud.custom_modules.variable.description";
	private static final Map<String, Variable<?>> VARIABLES = new LinkedHashMap<>();
	private static final List<Variable<?>> TICK_UPDATE_VARIABLES = new ArrayList<>();
	private static final List<Variable<?>> FRAME_UPDATE_VARIABLES = new ArrayList<>();
	private static final List<Variable<?>> ON_JOIN_WORLD_UPDATE_VARIABLES = new ArrayList<>();

	enum UpdateFrequency {
		FRAME,
		TICK,
		ON_JOIN_WORLD
	}

	public static void init() {
		VARIABLES.clear();
		TICK_UPDATE_VARIABLES.clear();
		FRAME_UPDATE_VARIABLES.clear();
		ON_JOIN_WORLD_UPDATE_VARIABLES.clear();

		// player
		register("player.gamemode", SafeSupplier.create(() -> requireNonNull(requireNonNull(CLIENT.player).getGameMode()).getSimpleTranslatableName().getString(), Text.translatable("selectWorld.gameMode.survival").getString()), TICK);
		register("player.name", () -> CLIENT.getSession().getUsername(), ON_JOIN_WORLD);
		register("player.yaw", SafeSupplier.create(() -> (requireNonNull(CLIENT.player).getYaw() % 360 + 360) % 360 - 180, 180f), FRAME);
		register("player.pitch", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getPitch(), 0.0f), FRAME);
		register("player.x", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getX(), 0.0), TICK);
		register("player.y", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getY(), 0.0), TICK);
		register("player.z", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getZ(), 0.0), TICK);
		register("nether.player.x", SafeSupplier.create(() -> {
			if (requireNonNull(CLIENT.player).getEntityWorld().getRegistryKey().equals(World.OVERWORLD))
				return CLIENT.player.getX() / 8;
			else if (CLIENT.player.getEntityWorld().getRegistryKey().equals(World.NETHER)) {
				return CLIENT.player.getX() * 8;
			} else {
				return CLIENT.player.getX();
			}
		}, 0), TICK);
		register("nether.player.z", SafeSupplier.create(() -> {
			if (requireNonNull(CLIENT.player).getEntityWorld().getRegistryKey().equals(World.OVERWORLD))
				return CLIENT.player.getZ() / 8;
			else if (CLIENT.player.getEntityWorld().getRegistryKey().equals(World.NETHER)) {
				return CLIENT.player.getZ() * 8;
			} else {
				return CLIENT.player.getZ();
			}
		}, 0), TICK);
		register("player.chunk.x", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getChunkPos().x, 0), TICK);
		register("player.chunk.z", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getChunkPos().z, 0), TICK);
		register("player.direction", () -> getDirection(CLIENT.player)[0], FRAME);
		register("player.direction_abbr", () -> getDirection(CLIENT.player)[1], FRAME);
		register("player.direction.x", () -> getDirection(CLIENT.player)[2], FRAME);
		register("player.direction.z", () -> getDirection(CLIENT.player)[3], FRAME);
		register("player.speed", SpeedTickable::getSpeed, TICK);
		register("player.health", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getHealth(), 20), TICK);
		register("player.health_max", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getMaxHealth(), 20), TICK);
		register("player.health_percent", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getMaxHealth() == 0 ? 0 : CLIENT.player.getHealth() / CLIENT.player.getMaxHealth(), 100), TICK);
		register("player.absorption", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getAbsorptionAmount(), 0), TICK);
		register("player.absorption_max", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getMaxAbsorption(), 0), TICK);
		register("player.absorption_percent", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getMaxAbsorption() == 0 ? 0 : CLIENT.player.getAbsorptionAmount() / CLIENT.player.getMaxAbsorption(), 100), TICK);
		register("player.food", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getHungerManager().getFoodLevel(), 20), TICK);
		register("player.saturation", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getHungerManager().getSaturationLevel(), 20), TICK);
		register("player.armor", SafeSupplier.create(() -> requireNonNull(CLIENT.player).getArmor(), 20), TICK);
		// world
		register("world.name", SafeSupplier.create(() -> requireNonNull(CLIENT.getServer()).getSaveProperties().getLevelName(), "", "World name"), ON_JOIN_WORLD);
		register("world.biome", SafeSupplier.create(() -> requireNonNull(CLIENT.world).getBiome(requireNonNull(CLIENT.player).getBlockPos()).getKeyOrValue().map(key -> key.getValue().getPath(), value -> "[unregistered " + value + "]"), "", BiomeKeys.PLAINS.getValue().getPath()), TICK);
		register("world.dimension", SafeSupplier.create(() -> requireNonNull(CLIENT.world).getRegistryKey().getValue().getPath(), "", World.OVERWORLD.getValue().getPath()), TICK);
		register("world.time", SafeSupplier.create(() -> requireNonNull(CLIENT.world).getTimeOfDay() % 24000, 12000L), TICK);
		register("world.time.hour_24", SafeSupplier.create(() -> ((requireNonNull(CLIENT.world).getTimeOfDay() % 24000) / 1000 + 6) % 24, 18L), TICK);
		register("world.time.hour_12", SafeSupplier.create(() -> {
			long h = ((requireNonNull(CLIENT.world).getTimeOfDay() % 24000) / 1000 + 6) % 12;
			return h == 0 ? 12 : h;
		}, 18L), TICK);
		register("world.time.minute", SafeSupplier.create(() -> (requireNonNull(CLIENT.world).getTimeOfDay() % 1000) * 60 / 1000, 0L), TICK);
		register("world.time.second", SafeSupplier.create(() -> (requireNonNull(CLIENT.world).getTimeOfDay() % 1000) * 60 % 1000 * 60 / 1000, 0L), TICK);
		register("world.time.ampm", SafeSupplier.create(() -> (requireNonNull(CLIENT.world).getTimeOfDay() / 1000 + 6) % 24 < 12 ? "AM" : "PM", "AM"), TICK);
		register("world.day", SafeSupplier.create(() -> requireNonNull(CLIENT.world).getTimeOfDay() / 24000, 5), TICK);
		// server
		register("server.ip", SafeSupplier.create(() -> requireNonNull(CLIENT.getCurrentServerEntry()).address, "", "play.hypixel.net"), ON_JOIN_WORLD);
		register("server.name", SafeSupplier.create(() -> requireNonNull(CLIENT.getCurrentServerEntry()).name, "", "Hypixel"), ON_JOIN_WORLD);
		register("server.ping", PingUtils::getPing, TICK);
		// client
		register("client.fps", CLIENT::getCurrentFps, TICK);
		register("client.version", () -> SharedConstants.getGameVersion().id(), ON_JOIN_WORLD);
		register("client.render_distance", () -> CLIENT.options.getViewDistance().getValue(), TICK);
		// cps
		register("cps.left", CpsUtils::getLeftCps, FRAME);
		register("cps.right", CpsUtils::getRightCps, FRAME);
		// pc
		register("pc.memory_usage", () -> MemoryUsageTickable.getUsedMemory() / 1024.0 / 1024.0, TICK);
		register("pc.max_memory", () -> MemoryUsageTickable.getMaxMemory() / 1024.0 / 1024.0, TICK);
		register("pc.memory_usage_percentage", MemoryUsageTickable::getUsedMemoryPercentage, TICK);
		// time
		register("time.hour_24", () -> LocalTime.now().getHour(), TICK);
		register("time.hour_12", () -> LocalTime.now().get(ChronoField.CLOCK_HOUR_OF_AMPM), TICK);
		register("time.minute", () -> LocalTime.now().getMinute(), TICK);
		register("time.second", () -> LocalTime.now().getSecond(), TICK);
		register("time.ms", () -> LocalTime.now().getNano() / 1_000_000, FRAME);
		register("time.ampm", () -> LocalTime.now().get(ChronoField.AMPM_OF_DAY) == 0 ? "AM" : "PM", TICK);
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
			case ON_JOIN_WORLD -> ON_JOIN_WORLD_UPDATE_VARIABLES.add(variable);
		}

		variable.updateValue();
	}

	public static void tick() {
		for (Variable<?> variable : TICK_UPDATE_VARIABLES) {
			variable.updateValue();
		}
	}

	public static void frame() {
		for (Variable<?> variable : FRAME_UPDATE_VARIABLES) {
			variable.updateValue();
		}
	}

	public static void onJoinWorld() {
		for (Variable<?> variable : ON_JOIN_WORLD_UPDATE_VARIABLES) {
			variable.updateValue();
		}
	}

	private static @NonNull String[] getDirection(@Nullable PlayerEntity player) {
		float yaw = player != null ? (player.getYaw() % 360 + 360) % 360 : 0;

		if (337.5 < yaw || yaw < 22.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.south").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.south").getString(), "", "+"};
		} else if (22.5 <= yaw && yaw < 67.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.south_west").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.south_west").getString(), "-", "+"};
		} else if (67.5 <= yaw && yaw < 112.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.west").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.west").getString(), "-", ""};
		} else if (112.5 <= yaw && yaw < 157.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.north_west").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.north_west").getString(), "-", "-"};
		} else if (157.5 <= yaw && yaw < 202.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.north").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.north").getString(), "", "-"};
		} else if (202.5 <= yaw && yaw < 247.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.north_east").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.north_east").getString(), "+", "-"};
		} else if (247.5 <= yaw && yaw < 292.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.east").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.east").getString(), "+", ""};
		} else {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.south_east").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.south_east").getString(), "+", "+"};
		}
	}
}
