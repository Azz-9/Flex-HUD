package me.Azz_9.better_hud.client.configurableMods;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.renderCallbacks.*;
import me.Azz_9.better_hud.client.configurableMods.mods.notHud.TimeChanger;
import me.Azz_9.better_hud.client.configurableMods.mods.notHud.WeatherChanger;
import me.Azz_9.better_hud.client.configurableMods.mods.notHud.durabilityPing.DurabilityPing;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonConfigHelper {
	public boolean isEnabled = true;
	//hud
	public ArmorStatus armorStatus = new ArmorStatus(0.234, 41.685);
	public Cps cps = new Cps(95.433, 0.443);
	public Clock clock = new Clock(76.112, 0.443);
	public Fps fps = new Fps(0.234, 0.443);
	public Coordinates coordinates = new Coordinates(0.234, 3.104);
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
				Better_hudClient.LOGGER.error("Failed to load config");
			}
		}
		return new JsonConfigHelper(); // Si le fichier n'existe pas, retourner la configuration par défaut
	}

	// Méthode pour sauvegarder la configuration dans le fichier JSON
	public static void saveConfig() {
		try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
			GSON.toJson(getInstance(), writer);
		} catch (IOException e) {
			Better_hudClient.LOGGER.error("Failed to save config");
		}
	}

	public static List<AbstractHudElement> getHudElements() {
		return List.of(
				getInstance().armorStatus,
				getInstance().cps,
				getInstance().clock,
				getInstance().fps,
				getInstance().coordinates
		);
	}
}
