package me.Azz_9.better_hud.client.configurableModules;

import com.google.gson.*;
import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.MovableModule;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.custom.*;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.vanilla.BossBar;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.vanilla.Crosshair;
import me.Azz_9.better_hud.client.configurableModules.modules.notHud.TimeChanger;
import me.Azz_9.better_hud.client.configurableModules.modules.notHud.TntCountdown;
import me.Azz_9.better_hud.client.configurableModules.modules.notHud.WeatherChanger;
import me.Azz_9.better_hud.client.configurableModules.modules.notHud.durabilityPing.DurabilityPing;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonConfigHelper {
	public boolean isEnabled = true;
	//hud
	public ArmorStatus armorStatus = new ArmorStatus(2, -30, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.CENTER);
	public Cps cps = new Cps(-80, 2, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public Clock clock = new Clock(-204, 2, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public Fps fps = new Fps(2, 2, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public Coordinates coordinates = new Coordinates(2, 15, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public Compass compass = new Compass(0, 0, AbstractHudElement.AnchorPosition.CENTER, AbstractHudElement.AnchorPosition.START);
	public DayCounter dayCounter = new DayCounter(148, 2, AbstractHudElement.AnchorPosition.CENTER, AbstractHudElement.AnchorPosition.START);
	public Ping ping = new Ping(-129, 2, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public ServerAddress serverAddress = new ServerAddress(200, 2, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public MemoryUsage memoryUsage = new MemoryUsage(75, 2, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public Speedometer speedometer = new Speedometer(2, 70, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public Reach reach = new Reach(2, 100, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public Playtime playtime = new Playtime(2, 140, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	//public ResourcePack resourcePack = new ResourcePack(0, 100, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public PotionEffect potionEffect = new PotionEffect(0, 20, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public Crosshair crosshair = new Crosshair();
	public BossBar bossBar = new BossBar(0, 35, AbstractHudElement.AnchorPosition.CENTER, AbstractHudElement.AnchorPosition.START);
	public WeatherDisplay weatherDisplay = new WeatherDisplay(-4, -4, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.END);
	//others
	public WeatherChanger weatherChanger = new WeatherChanger();
	public TimeChanger timeChanger = new TimeChanger();
	public DurabilityPing durabilityPing = new DurabilityPing();
	public TntCountdown tntCountdown = new TntCountdown();

	//number of columns
	public int numberOfColumns = 2;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File("config/better_hud.json");
	private static JsonConfigHelper INSTANCE;

	// Méthode pour obtenir l'instance de la configuration
	public static JsonConfigHelper getInstance() {
		if (INSTANCE == null) {
			INSTANCE = loadConfig();
		}
		return INSTANCE;
	}

	private static JsonConfigHelper loadConfig() {
		// 1) Build defaults from the class constructor values
		JsonConfigHelper defaults = new JsonConfigHelper();

		// 2) If a file exists, deep-merge file values over defaults
		if (CONFIG_FILE.exists()) {
			try (Reader reader = new FileReader(CONFIG_FILE)) {
				// Serialize defaults to a JsonObject tree
				JsonObject defaultTree = GSON.toJsonTree(defaults).getAsJsonObject();

				// Parse existing config as a JsonObject (might be partial)
				JsonElement parsed = JsonParser.parseReader(reader);
				if (parsed != null && parsed.isJsonObject()) {
					// Merge user values into the defaults
					deepMergeInto(defaultTree, parsed.getAsJsonObject());
				}

				// Deserialize merged tree back into the Java class
				INSTANCE = GSON.fromJson(defaultTree, JsonConfigHelper.class);
			} catch (Exception e) {
				// On error, fall back to defaults
				Better_hudClient.LOGGER.error("Failed to load config {}, using defaults", e.getMessage());
				INSTANCE = defaults;
			}
		} else {
			INSTANCE = defaults;
		}

		// 3) Ensure directory exists and write back to disk so new keys appear in file
		ensureParentDirExists(CONFIG_FILE);
		saveConfig();

		return INSTANCE;
	}

	// Méthode pour sauvegarder la configuration dans le fichier JSON
	public static void saveConfig() {
		try {
			if (INSTANCE == null) {
				// Safety: initialize defaults if called very early
				INSTANCE = new JsonConfigHelper();
			}
			ensureParentDirExists(CONFIG_FILE);
			try (Writer writer = new FileWriter(CONFIG_FILE)) {
				GSON.toJson(INSTANCE, writer);
			}
		} catch (IOException e) {
			Better_hudClient.LOGGER.error("Failed to save config {}", e.getMessage());
		}
	}

	/**
	 * Deeply merge 'overrides' into 'target'. Objects are merged; arrays/primitives are replaced.
	 */
	private static void deepMergeInto(JsonObject target, JsonObject overrides) {
		for (Map.Entry<String, JsonElement> e : overrides.entrySet()) {
			String key = e.getKey();
			JsonElement overrideVal = e.getValue();

			// If both sides are objects, merge recursively
			if (target.has(key) && target.get(key).isJsonObject() && overrideVal.isJsonObject()) {
				deepMergeInto(target.getAsJsonObject(key), overrideVal.getAsJsonObject());
			} else {
				// Otherwise, override or add
				target.add(key, overrideVal);
			}
		}
	}

	/**
	 * Ensure the parent directory exists before writing the file.
	 */
	private static void ensureParentDirExists(File file) {
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			//noinspection ResultOfMethodCallIgnored
			parent.mkdirs();
		}
	}

	public static List<AbstractHudElement> getHudElements() {
		return List.of(
				getInstance().armorStatus,
				getInstance().cps,
				getInstance().clock,
				getInstance().fps,
				getInstance().coordinates,
				getInstance().compass,
				getInstance().dayCounter,
				getInstance().ping,
				getInstance().serverAddress,
				getInstance().memoryUsage,
				getInstance().speedometer,
				//getInstance().reach,
				getInstance().playtime,
				//getInstance().resourcePack,
				getInstance().potionEffect,
				getInstance().weatherDisplay,
				getInstance().bossBar
		);
	}

	public static List<MovableModule> getMovableModules() {
		return new ArrayList<>(getHudElements());
	}

	public static List<Configurable> getConfigurableModules() {
		List<Configurable> configurables = new ArrayList<>(getHudElements());
		configurables.addAll(List.of(
				getInstance().weatherChanger,
				getInstance().timeChanger,
				//getInstance().durabilityPing,
				getInstance().crosshair,
				getInstance().tntCountdown
		));
		return configurables;
	}
}
