package me.Azz_9.better_hud.ModMenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.DisplayMode;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods.ArmorStatus;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods.DurabilityPing;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods.Speedometer;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods.WeatherChanger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {

    //enable mod
    public boolean isEnabled = true;

    //coords
    public boolean showCoordinates = true;
    public int coordinatesHudX = 2;
    public int coordinatesHudY = 15;
    public int coordinatesColor = 0xFFFFFF;
    public boolean coordinatesShadow = true;
    public int coordinatesDigits = 0;
    public boolean showYCoordinates = true;
    public boolean showBiome = true;
    public boolean showCoordinatesDirection = true;
    public boolean coordinatesDirectionAbreviation = true;
    public DisplayMode displayModeCoordinates = DisplayMode.Vertical;
    //fps
    public boolean showFPS = true;
    public int FPSColor = 0xFFFFFF;
    public boolean FPSShadow = true;
    public int FPSHudX = 2;
    public int FPSHudY = 2;
    public float FPSscale = 1.0f;
    //clock
    public boolean showClock = true;
    public int clockColor = 0xFFFFFF;
    public boolean clockShadow = true;
    public String clockTextFormat = "hh:mm:ss";
    public boolean clock24hourformat = true;
    public int clockHudX = 650;
    public int clockHudY = 2;
    //armor status
    public boolean showArmorStatus = true;
    public int armorStatusTextColor = 0xFFFFFF;
    public boolean armorStatusTextShadow = true;
    public boolean showHelmet = true;
    public boolean showChestplate = true;
    public boolean showLeggings = true;
    public boolean showBoots = true;
    public boolean showHeldItem = true;
    public boolean showArrowsWhenBowInHand = true;
    public ArmorStatus.DurabilityType showDurability = ArmorStatus.DurabilityType.Percentage;
    public DisplayMode displayModeArmorStatus = DisplayMode.Vertical;
    public int armorStatusHudX = 2;
    public int armorStatusHudY = 200;
    //direction
    public boolean showDirection = true;
    public int directionColor = 0xFFFFFF;
    public boolean directionShadow = true;
    public boolean showDirectionMarker = true;
    public boolean showIntermediateDirectionPoint = true;
    public boolean showXaerosMapWaypoints = true;
    public int directionHudX = 200;
	public int directionHudY = 0;
    //day counter
    public boolean showDayCounter = true;
    public int dayCounterColor = 0xFFFFFF;
    public boolean dayCounterShadow = true;
    public int dayCounterHudX = 575;
    public int dayCounterHudY = 2;
    //ping
    public boolean showPing = true;
    public int pingColor = 0xFFFFFF;
    public boolean pingShadow = true;
    public boolean hidePingWhenOffline = true;
    public int pingHudX = 725;
    public int pingHudY = 2;
    //server address
    public boolean showServerAddress = true;
    public int serverAddressColor = 0xFFFFFF;
    public boolean serverAddressShadow = true;
    public boolean hideServerAddressWhenOffline = true;
    public int serverAddressHudX = 200;
    public int serverAddressHudY = 2;
    //weather changer
    public boolean enableWeatherChanger = false;
    public WeatherChanger.Weather selectedWeather = WeatherChanger.Weather.Clear;
    //memory usage
    public boolean showMemoryUsage = true;
    public int memoryUsageColor = 0xFFFFFF;
    public boolean memoryUsageShadow = true;
    public int memoryUsageHudX = 75;
    public int memoryUsageHudY = 2;
    //cps
    public boolean showCps = true;
    public int cpsColor = 0xFFFFFF;
    public boolean cpsShadow = true;
    public boolean showLeftClickCPS = true;
    public boolean showRightClickCPS = true;
    public int cpsHudX = 75;
    public int cpsHudY = 2;
    //time changer
    public boolean enableTimeChanger = false;
    public int selectedTime = 6000;
    public boolean useRealTime = false;
    //durability ping
    public boolean enableDurabilityPing = true;
    public int durabilityPingThreshold = 10; // percentage
    public DurabilityPing.DurabilityPingType durabilityPingType = DurabilityPing.DurabilityPingType.Both;
    public boolean checkArmorPieces = true;
    public boolean checkElytraOnly = false;
    //speedometer
    public boolean showSpeedometer = true;
    public int speedometerColor = 0xFFFFFF;
    public boolean speedometerShadow = true;
    public int speedometerDigits = 1;
    public Speedometer.SpeedometerUnits speedometerUnits = Speedometer.SpeedometerUnits.MPS;
    public boolean useKnotInBoat = false;
    public int speedometerHudX = 2;
    public int speedometerHudY = 70;
    //reach
    public boolean showReach = true;
    public int reachColor = 0xFFFFFF;
    public boolean reachShadow = true;
    public int reachDigits = 2;
    public int reachHudX = 2;
    public int reachHudY = 100;
    //combo counter
    public boolean showComboCounter = true;
    public int comboCounterColor = 0xFFFFFF;
    public boolean comboCounterShadow = true;
    public int comboCounterHudX = 2;
    public int comboCounterHudY = 120;
    //playtime
    public boolean showPlaytime = true;
    public int playtimeColor = 0xFFFFFF;
    public boolean playtimeShadow = true;
    public int playtimeHudX = 2;
    public int playtimeHudY = 120;
    //stopwatch
    public boolean showStopwatch = true;
    public int stopwatchColor = 0xFFFFFF;
    public boolean stopwatchShadow = true;
    public int stopwatchHudX = 2;
    public int stopwatchHudY = 120;
    //shrieker warning level
    public boolean showShriekerWarningLevel = true;
    public int shriekerWarningLevelColor = 0xFFFFFF;
    public boolean shriekerWarningLevelShadow = true;
    public boolean showWhenInDeepDark = true;
    public int shriekerWarningLevelHudX = 2;
    public int shriekerWarningLevelHudY = 120;
    //blocks searching
    public boolean enableBlocksSearch = true;
    public List<String> selectedBlocks = new ArrayList<>(); // Liste des identifiants des blocs


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
}

// TODO global: traduire tous les commentaires en anglais