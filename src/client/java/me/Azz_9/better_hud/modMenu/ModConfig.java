package me.Azz_9.better_hud.modMenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.Azz_9.better_hud.client.overlay.*;
import me.Azz_9.better_hud.client.utils.DurabilityPing;
import me.Azz_9.better_hud.client.utils.TimeChanger;
import me.Azz_9.better_hud.client.utils.WeatherChangerConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.WeatherChanger;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ModConfig {

    public boolean isEnabled = true;
    public CoordinatesOverlay coordinates = new CoordinatesOverlay(2, 15);
    public FPSOverlay fps = new FPSOverlay(2, 2);
    public ClockOverlay clock = new ClockOverlay(650, 2);
    public ArmorStatusOverlay armorStatus = new ArmorStatusOverlay(2, 200);
    public DirectionOverlay direction = new DirectionOverlay(200, 0);
    public DayCounterOverlay dayCounter = new DayCounterOverlay(575, 2);
    public PingOverlay ping = new PingOverlay(725, 2);
    public ServerAddressOverlay serverAddress = new ServerAddressOverlay(200, 2);
    public MemoryUsageOverlay memoryUsage = new MemoryUsageOverlay(75, 2);
    public CPSOverlay cps = new CPSOverlay(815, 2);
    public SpeedometerOverlay speedometer = new SpeedometerOverlay(2, 70);
    public ReachOverlay reach = new ReachOverlay(2, 100);
    public ComboCounterOverlay comboCounter = new ComboCounterOverlay(2, 120);
    public PlaytimeOverlay playtime = new PlaytimeOverlay(2, 140);
    public ShriekerWarningLevelOverlay shriekerWarningLevel = new ShriekerWarningLevelOverlay(2, 160);

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

    public static List<HudRenderCallback> getHudElements() {
        return List.of(INSTANCE.coordinates, INSTANCE.fps, INSTANCE.clock, INSTANCE.armorStatus, INSTANCE.direction,
                INSTANCE.dayCounter, INSTANCE.ping, INSTANCE.serverAddress, INSTANCE.memoryUsage, INSTANCE.cps,
                INSTANCE.speedometer,INSTANCE.reach, INSTANCE.comboCounter, INSTANCE.playtime,
                INSTANCE.shriekerWarningLevel);
    }
}

// TODO global: traduire tous les commentaires en anglais