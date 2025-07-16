package me.Azz_9.better_hud.client.configurableModules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.MovableModule;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.custom.*;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.vanilla.BossBar;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.vanilla.Crosshair;
import me.Azz_9.better_hud.client.configurableModules.modules.notHud.TimeChanger;
import me.Azz_9.better_hud.client.configurableModules.modules.notHud.WeatherChanger;
import me.Azz_9.better_hud.client.configurableModules.modules.notHud.durabilityPing.DurabilityPing;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	public BossBar bossBar = new BossBar(0, 30, AbstractHudElement.AnchorPosition.CENTER, AbstractHudElement.AnchorPosition.START);
	//others
	public WeatherChanger weatherChanger = new WeatherChanger();
	public TimeChanger timeChanger = new TimeChanger();
	public DurabilityPing durabilityPing = new DurabilityPing();

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

	// Méthode pour charger la configuration depuis le fichier JSON
	private static JsonConfigHelper loadConfig() {
		if (CONFIG_FILE.exists()) {
			try (FileReader reader = new FileReader(CONFIG_FILE)) {
				return GSON.fromJson(reader, JsonConfigHelper.class);
			} catch (IOException e) {
				Better_hudClient.LOGGER.error("Failed to load config {}", e.getMessage());
			}
		}
		return new JsonConfigHelper(); // Si le fichier n'existe pas, retourner la configuration par défaut
	}

	// Méthode pour sauvegarder la configuration dans le fichier JSON
	public static void saveConfig() {
		try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
			GSON.toJson(getInstance(), writer);
		} catch (IOException e) {
			Better_hudClient.LOGGER.error("Failed to save config {}", e.getMessage());
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
				getInstance().reach,
				getInstance().playtime,
				//getInstance().resourcePack,
				getInstance().potionEffect
		);
	}

	public static List<MovableModule> getMovableModules() {
		List<MovableModule> movableModuleList = new ArrayList<>(getHudElements());
		movableModuleList.add(
				getInstance().bossBar
		);
		return movableModuleList;
	}

	public static List<Configurable> getConfigurableModules() {
		List<Configurable> configurables = new ArrayList<>(getMovableModules());
		configurables.addAll(List.of(
				getInstance().weatherChanger,
				getInstance().timeChanger,
				getInstance().durabilityPing,
				getInstance().crosshair
		));
		return configurables;
	}
}
