package me.Azz_9.flex_hud.client.configurableModules;

import com.google.gson.*;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.AbstractConfigObject;
import me.Azz_9.flex_hud.client.utils.FlexHudLogger;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Map;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class ConfigLoader {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final static File CONFIG_FILE = new File("config/" + MOD_ID + ".json");

	public static void loadConfig() {
		if (!CONFIG_FILE.exists()) {
			FlexHudLogger.info("Config file does not exist, loading default config");
			saveConfig(); // create defaults if missing
			return;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_FILE.toPath())) {
			JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

			// detect old format
			if (containsOldFormat(root)) {
				FlexHudLogger.info("Detected old config format, migrating...");
				root = convertOldFormat(root);
				saveConverted(root);
				FlexHudLogger.info("Old format converted!");
			}

			applyConfig(root);

		} catch (Exception e) {
			FlexHudLogger.error("Failed to load config: {}, using default", e.getMessage());
			e.printStackTrace();
		}
	}

	public static void saveConfig() {
		JsonObject root = new JsonObject();

		for (String moduleName : ConfigRegistry.getModuleNames()) {
			JsonObject moduleJson = new JsonObject();

			Map<String, AbstractConfigObject<?>> module = ConfigRegistry.getModule(moduleName);
			if (module == null) continue;

			for (Map.Entry<String, AbstractConfigObject<?>> entry : module.entrySet()) {
				moduleJson.add(entry.getKey(), entry.getValue().toJsonValue());
			}

			root.add(moduleName, moduleJson);
		}

		try (Writer writer = Files.newBufferedWriter(CONFIG_FILE.toPath())) {
			GSON.toJson(root, writer);
		} catch (Exception e) {
			FlexHudLogger.error("Failed to save config: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	private static void applyConfig(JsonObject root) {
		for (String moduleName : ConfigRegistry.getModuleNames()) {
			JsonObject moduleJson = root.getAsJsonObject(moduleName);
			if (moduleJson == null) continue;

			Map<String, AbstractConfigObject<?>> module = ConfigRegistry.getModule(moduleName);
			if (module == null) continue;

			for (Map.Entry<String, JsonElement> entry : moduleJson.entrySet()) {
				AbstractConfigObject<?> configObject = module.get(entry.getKey());
				if (configObject != null) {
					configObject.applyFromJsonElement(entry.getValue());
				}
			}
		}
	}


	// old format :

	/**
	 * Vérifie récursivement si le JSON contient au moins une clé "configTextTranslationKey"
	 */
	private static boolean containsOldFormat(JsonObject obj) {
		for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
			JsonElement element = entry.getValue();
			if (element.isJsonObject()) {
				JsonObject sub = element.getAsJsonObject();
				if (sub.has("configTextTranslationKey")) {
					return true; // found -> old format
				}
				if (containsOldFormat(sub)) {
					return true;
				}
			}
		}
		return false;
	}

	private static JsonObject convertOldFormat(JsonObject oldRoot) {
		JsonObject newRoot = new JsonObject();

		for (Map.Entry<String, JsonElement> moduleEntry : oldRoot.entrySet()) {
			String moduleName = moduleEntry.getKey();
			JsonElement moduleElement = moduleEntry.getValue();

			if (moduleElement.isJsonObject()) {
				JsonObject oldModule = moduleElement.getAsJsonObject();
				JsonObject newModule = new JsonObject();

				for (Map.Entry<String, JsonElement> configEntry : oldModule.entrySet()) {
					JsonElement valueElem = configEntry.getValue();
					if (valueElem.isJsonObject()) {
						JsonObject inner = valueElem.getAsJsonObject();
						if (inner.has("value")) {
							newModule.add(configEntry.getKey(), inner.get("value"));
						}
					} else {
						newModule.add(configEntry.getKey(), valueElem);
					}
				}

				newRoot.add(moduleName.replaceAll("([A-Z])", "_$1").toLowerCase(), newModule);
			} else {
				JsonObject global = newRoot.has("global")
						? newRoot.getAsJsonObject("global")
						: new JsonObject();

				global.add(moduleName, moduleElement);
				newRoot.add("global", global);
			}
		}

		return newRoot;
	}

	private static void saveConverted(JsonObject root) {
		try (Writer writer = Files.newBufferedWriter(CONFIG_FILE.toPath())) {
			GSON.toJson(root, writer);
			FlexHudLogger.info("Migrated config successfully saved!");
		} catch (Exception e) {
			FlexHudLogger.error("Failed to save migrated config: {}", e.getMessage());
		}
	}
}
