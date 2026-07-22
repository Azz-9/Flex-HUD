package me.Azz_9.flex_hud.client.modules.customModules;

import static java.util.Objects.requireNonNull;
import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.client.modules.customModules.Variables.UpdateFrequency.*;

import net.minecraft.SharedConstants;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;

import org.jetbrains.annotations.Nullable;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.Supplier;

import me.Azz_9.flex_hud.Constants;
import me.Azz_9.flex_hud.client.debug.PerfTester;
import me.Azz_9.flex_hud.client.modules.hud.custom.Speedometer;
import me.Azz_9.flex_hud.client.tickables.MemoryUsageTickable;
import me.Azz_9.flex_hud.client.tickables.SpeedTickable;
import me.Azz_9.flex_hud.utils.CpsUtils;
import me.Azz_9.flex_hud.utils.PingUtils;

public class Variables {

	private static final String VARIABLE_NAME_BASE_KEY = "flex_hud.custom_modules.variable.name";
	private static final String VARIABLE_DESCRIPTION_BASE_KEY = "flex_hud.custom_modules.variable.description";
	private static final Map<String, Variable<?>> VARIABLES = new LinkedHashMap<>();
	private static final Map<UpdateFrequency, List<Variable<?>>> UPDATE_VARIABLES = new EnumMap<>(UpdateFrequency.class);

	enum UpdateFrequency {
		FRAME,
		TICK,
		ON_JOIN_WORLD
	}

	public static void init() {
		resetRegistry();

		registerPlayerVariables();
		registerWorldVariables();
		registerServerVariables();
		registerClientVariables();
		registerCpsVariables();
		registerPcVariables();
		registerClockVariables();
	}

	private static void resetRegistry() {
		VARIABLES.clear();
		UPDATE_VARIABLES.clear();
		for (UpdateFrequency frequency : UpdateFrequency.values()) {
			UPDATE_VARIABLES.put(frequency, new ArrayList<>());
		}
	}

	private static void registerPlayerVariables() {
		register("player.gamemode", SafeSupplier.create(() -> requireNonNull(requireNonNull(MINECRAFT.player).gameMode()).getShortDisplayName().getString(), Component.translatable("selectWorld.gameMode.survival").getString()), TICK);
		register("player.name", () -> MINECRAFT.getUser().getName(), ON_JOIN_WORLD);
		register("player.yaw", SafeSupplier.create(() -> Mth.wrapDegrees(requireNonNull(MINECRAFT.player).getYRot()), 180f), FRAME);
		register("player.pitch", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getXRot(), 0.0f), FRAME);
		register("player.x", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getX(), 0.0), TICK);
		register("player.y", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getY(), 0.0), TICK);
		register("player.z", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getZ(), 0.0), TICK);
		register("nether.player.x", SafeSupplier.create(() -> scaledNetherCoordinate(requireNonNull(MINECRAFT.player).getX()), 0), TICK);
		register("nether.player.z", SafeSupplier.create(() -> scaledNetherCoordinate(requireNonNull(MINECRAFT.player).getZ()), 0), TICK);
		register("player.chunk.x", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).chunkPosition().x, 0), TICK);
		register("player.chunk.z", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).chunkPosition().z, 0), TICK);
		register("player.direction", () -> getDirection(MINECRAFT.player).name(), FRAME);
		register("player.direction_abbr", () -> getDirection(MINECRAFT.player).abbreviation(), FRAME);
		register("player.direction.x", () -> getDirection(MINECRAFT.player).xSign(), FRAME);
		register("player.direction.z", () -> getDirection(MINECRAFT.player).zSign(), FRAME);
		register("player.speed", SafeSupplier.create(() -> Speedometer.SpeedometerUnits.MPS.convert(SpeedTickable.getSpeedMeterPerTicks()), 0), TICK);
		register("player.horizontal_speed", SafeSupplier.create(() -> Speedometer.SpeedometerUnits.MPS.convert(SpeedTickable.getHorizontalSpeedMeterPerTicks()), 0), TICK);
		register("player.health", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getHealth(), 20), TICK);
		register("player.health_max", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getMaxHealth(), 20), TICK);
		register("player.health_percent", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getMaxHealth() == 0 ? 0 : MINECRAFT.player.getHealth() / MINECRAFT.player.getMaxHealth(), 100), TICK);
		register("player.absorption", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getAbsorptionAmount(), 0), TICK);
		register("player.absorption_max", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getMaxAbsorption(), 0), TICK);
		register("player.absorption_percent", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getMaxAbsorption() == 0 ? 0 : MINECRAFT.player.getAbsorptionAmount() / MINECRAFT.player.getMaxAbsorption(), 100), TICK);
		register("player.food", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getFoodData().getFoodLevel(), 20), TICK);
		register("player.saturation", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getFoodData().getSaturationLevel(), 20), TICK);
		register("player.armor", SafeSupplier.create(() -> requireNonNull(MINECRAFT.player).getArmorValue(), 20), TICK);
	}

	private static void registerWorldVariables() {
		register("world.name", SafeSupplier.create(() -> requireNonNull(MINECRAFT.getSingleplayerServer()).getWorldData().getLevelName(), "", "World name"), ON_JOIN_WORLD);
		register("world.biome", SafeSupplier.create(() -> requireNonNull(MINECRAFT.level).getBiome(requireNonNull(MINECRAFT.player).blockPosition()).unwrap().map(key -> key.location().getPath(), value -> "[unregistered " + value + "]"), "", Biomes.PLAINS.location().getPath()), TICK);
		register("world.dimension", SafeSupplier.create(() -> requireNonNull(MINECRAFT.level).dimension().location().getPath(), "", Level.OVERWORLD.location().getPath()), TICK);
		register("world.time", SafeSupplier.create(() -> requireNonNull(MINECRAFT.level).getDayTime() % 24000, 12000L), TICK);
		register("world.time.hour_24", SafeSupplier.create(() -> ((requireNonNull(MINECRAFT.level).getDayTime() % 24000) / 1000 + 6) % 24, 18L), TICK);
		register("world.time.hour_12", SafeSupplier.create(() -> {
			long h = ((requireNonNull(MINECRAFT.level).getDayTime() % 24000) / 1000 + 6) % 12;
			return h == 0 ? 12 : h;
		}, 18L), TICK);
		register("world.time.minute", SafeSupplier.create(() -> (requireNonNull(MINECRAFT.level).getDayTime() % 1000) * 60 / 1000, 0L), TICK);
		register("world.time.second", SafeSupplier.create(() -> (requireNonNull(MINECRAFT.level).getDayTime() % 1000) * 60 % 1000 * 60 / 1000, 0L), TICK);
		register("world.time.ampm", SafeSupplier.create(() -> (requireNonNull(MINECRAFT.level).getDayTime() / 1000 + 6) % 24 < 12 ? "AM" : "PM", "AM"), TICK);
		register("world.day", SafeSupplier.create(() -> requireNonNull(MINECRAFT.level).getDayTime() / 24000, 5), TICK);
	}

	private static void registerServerVariables() {
		register("server.ip", SafeSupplier.create(() -> requireNonNull(MINECRAFT.getCurrentServer()).ip, "", "play.hypixel.net"), ON_JOIN_WORLD);
		register("server.name", SafeSupplier.create(() -> requireNonNull(MINECRAFT.getCurrentServer()).name, "", "Hypixel"), ON_JOIN_WORLD);
		register("server.ping", PingUtils::getPing, TICK);
	}

	private static void registerClientVariables() {
		register("client.fps", MINECRAFT::getFps, TICK);
		register("client.version", () -> SharedConstants.getCurrentVersion().id(), ON_JOIN_WORLD);
		register("client.render_distance", () -> MINECRAFT.options.renderDistance().get(), TICK);
	}

	private static void registerCpsVariables() {
		register("cps.left", CpsUtils::getLeftCps, FRAME);
		register("cps.right", CpsUtils::getRightCps, FRAME);
	}

	private static void registerPcVariables() {
		register("pc.memory_usage", () -> MemoryUsageTickable.getUsedMemory() / 1024.0 / 1024.0, TICK);
		register("pc.max_memory", () -> MemoryUsageTickable.getMaxMemory() / 1024.0 / 1024.0, TICK);
		register("pc.memory_usage_percentage", MemoryUsageTickable::getUsedMemoryPercentage, TICK);
	}

	private static void registerClockVariables() {
		register("time.hour_24", () -> LocalTime.now().getHour(), TICK);
		register("time.hour_12", () -> LocalTime.now().get(ChronoField.CLOCK_HOUR_OF_AMPM), TICK);
		register("time.minute", () -> LocalTime.now().getMinute(), TICK);
		register("time.second", () -> LocalTime.now().getSecond(), TICK);
		register("time.ms", () -> LocalTime.now().getNano() / 1_000_000, FRAME);
		register("time.ampm", () -> LocalTime.now().get(ChronoField.AMPM_OF_DAY) == 0 ? "AM" : "PM", TICK);
	}

	public static Map<String, Variable<?>> getAllVariables() {
		return Collections.unmodifiableMap(VARIABLES);
	}

	public static Variable<?> get(String key) {
		return VARIABLES.get(key);
	}

	private static <T> void register(String key, Supplier<T> supplier, UpdateFrequency updateFrequency) {
		Variable<T> variable = new Variable<>(
				Component.translatable(VARIABLE_NAME_BASE_KEY + "." + key),
				Component.translatable(VARIABLE_DESCRIPTION_BASE_KEY + "." + key),
				key,
				supplier
		);
		VARIABLES.put(key, variable);
		UPDATE_VARIABLES.get(updateFrequency).add(variable);
	}

	public static void tick() {
		if (Constants.DEBUG) {
			PerfTester.testTick("Variables", () -> update(TICK));
		} else {
			update(TICK);
		}
	}

	public static void frame() {
		if (Constants.DEBUG) {
			PerfTester.testFrame("Variables", () -> update(FRAME));
		} else {
			update(FRAME);
		}
	}

	public static void onJoinWorld() {
		update(ON_JOIN_WORLD);
	}

	private static void update(UpdateFrequency frequency) {
		for (Variable<?> variable : UPDATE_VARIABLES.getOrDefault(frequency, List.of())) {
			if (variable.isUsed()) {
				variable.updateValue();
			}
		}
	}

	private static double scaledNetherCoordinate(double coordinate) {
		Level level = requireNonNull(MINECRAFT.player).level();
		if (level.dimension().equals(Level.OVERWORLD)) {
			return coordinate / 8;
		}
		if (level.dimension().equals(Level.NETHER)) {
			return coordinate * 8;
		}
		return coordinate;
	}

	private static PlayerDirection getDirection(@Nullable LocalPlayer player) {
		float yaw = player != null ? (player.getYRot() % 360 + 360) % 360 : 0;

		if (337.5 < yaw || yaw < 22.5) {
			return direction("south", "", "+");
		} else if (22.5 <= yaw && yaw < 67.5) {
			return direction("south_west", "-", "+");
		} else if (67.5 <= yaw && yaw < 112.5) {
			return direction("west", "-", "");
		} else if (112.5 <= yaw && yaw < 157.5) {
			return direction("north_west", "-", "-");
		} else if (157.5 <= yaw && yaw < 202.5) {
			return direction("north", "", "-");
		} else if (202.5 <= yaw && yaw < 247.5) {
			return direction("north_east", "+", "-");
		} else if (247.5 <= yaw && yaw < 292.5) {
			return direction("east", "+", "");
		} else {
			return direction("south_east", "+", "+");
		}
	}

	private static PlayerDirection direction(String key, String xSign, String zSign) {
		return new PlayerDirection(
				Component.translatable("flex_hud.coordinates.hud.direction." + key).getString(),
				Component.translatable("flex_hud.coordinates.hud.direction_abbr." + key).getString(),
				xSign,
				zSign
		);
	}

	private record PlayerDirection(String name, String abbreviation, String xSign, String zSign) {
	}
}
