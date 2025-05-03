package me.Azz_9.better_hud.modMenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.Azz_9.better_hud.client.overlay.*;
import me.Azz_9.better_hud.client.utils.DurabilityPing;
import me.Azz_9.better_hud.client.utils.TimeChanger;
import me.Azz_9.better_hud.client.utils.WeatherChangerConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ModConfig {

	public boolean isEnabled = true;
	//hud
	public CoordinatesOverlay coordinates = new CoordinatesOverlay(0.234, 3.104); // 2 15
	public FPSOverlay fps = new FPSOverlay(0.234, 0.443); // 2 2
	public ClockOverlay clock = new ClockOverlay(76.112, 0.443); // 650 2
	public ArmorStatusOverlay armorStatus = new ArmorStatusOverlay(0.234, 41.685); // 2 200
	public DirectionOverlay direction = new DirectionOverlay(37.471, 0.000); // 329 0
	public DayCounterOverlay dayCounter = new DayCounterOverlay(67.330, 0.417); // 575 2
	public PingOverlay ping = new PingOverlay(84.895, 0.443); // 725 2
	public ServerAddressOverlay serverAddress = new ServerAddressOverlay(23.419, 0.443); // 200 2
	public MemoryUsageOverlay memoryUsage = new MemoryUsageOverlay(8.782, 0.443); // 75 2
	public CPSOverlay cps = new CPSOverlay(95.433, 0.443); // 815 2
	public SpeedometerOverlay speedometer = new SpeedometerOverlay(0.234, 14.634); // 2 70
	public ReachOverlay reach = new ReachOverlay(0.234, 20.843); // 2 100
	public ComboCounterOverlay comboCounter = new ComboCounterOverlay(0.234, 25.055); // 2 120
	public PlaytimeOverlay playtime = new PlaytimeOverlay(0.234, 29.268); // 2 140
	public ShriekerWarningLevelOverlay shriekerWarningLevel = new ShriekerWarningLevelOverlay(0.234, 33.259); // 2 160
	//others
	public WeatherChangerConfig weatherChanger = new WeatherChangerConfig();
	public TimeChanger timeChanger = new TimeChanger();
	public DurabilityPing durabilityPing = new DurabilityPing();

	//number of columns
	public int numberOfColumns = 2;


	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static ModConfig INSTANCE;
	private static final File CONFIG_FILE = new File("config/better_hud.json");


	// Méthode pour obtenir l'instance de la configuration
	public static ModConfig getInstance() {
		if (INSTANCE == null) {
			INSTANCE = loadConfig();
		}
		return INSTANCE;
	}

	// Méthode pour charger la configuration depuis le fichier JSON
	private static ModConfig loadConfig() {
		if (CONFIG_FILE.exists()) {
			try (FileReader reader = new FileReader(CONFIG_FILE)) {
				return GSON.fromJson(reader, ModConfig.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ModConfig(); // Si le fichier n'existe pas, retourner la configuration par défaut
	}

	// Méthode pour sauvegarder la configuration dans le fichier JSON
	public static void saveConfig() {
		try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
			GSON.toJson(getInstance(), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<HudElement> getHudElements() {
		return List.of(INSTANCE.coordinates, INSTANCE.fps, INSTANCE.clock, INSTANCE.armorStatus, INSTANCE.direction,
				INSTANCE.dayCounter, INSTANCE.ping, INSTANCE.serverAddress, INSTANCE.memoryUsage, INSTANCE.cps,
				INSTANCE.speedometer, INSTANCE.reach, INSTANCE.comboCounter, INSTANCE.playtime,
				INSTANCE.shriekerWarningLevel);
	}
}

// TODO global: traduire tous les commentaires en anglais