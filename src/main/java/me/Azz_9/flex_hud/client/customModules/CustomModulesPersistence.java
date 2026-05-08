package me.Azz_9.flex_hud.client.customModules;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.utils.FlexHudLogger;

public class CustomModulesPersistence {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final static File CUSTOM_MODULES_FOLDER = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).resolve("custom_modules").toFile();

	public static void saveConfig() {
		try {
			Files.createDirectories(CUSTOM_MODULES_FOLDER.toPath());

			Set<String> expectedFiles = new HashSet<>();
			for (CustomModule module : ModulesHelper.getCustomModules()) {
				String fileName = module.getID() + ".json";
				expectedFiles.add(fileName);

				JsonObject root = new JsonObject();
				root.addProperty("name", module.getName().getString());
				root.addProperty("text", module.getText());

				Path file = CUSTOM_MODULES_FOLDER.toPath().resolve(fileName);
				try (Writer writer = Files.newBufferedWriter(file)) {
					GSON.toJson(root, writer);
				}
			}

			try (Stream<Path> files = Files.list(CUSTOM_MODULES_FOLDER.toPath())) {
				files.filter(Files::isRegularFile)
						.filter(path -> path.getFileName().toString().endsWith(".json"))
						.filter(path -> !expectedFiles.contains(path.getFileName().toString()))
						.forEach(path -> {
							try {
								Files.deleteIfExists(path);
							} catch (Exception e) {
								FlexHudLogger.error("Failed to delete custom module file {}: {}", path.getFileName(), e.getMessage());
							}
						});
			}
		} catch (Exception e) {
			FlexHudLogger.error("Failed to save custom modules: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	public static void loadConfig() {
		if (!CUSTOM_MODULES_FOLDER.exists()) {
			return;
		}

		try (Stream<Path> files = Files.list(CUSTOM_MODULES_FOLDER.toPath())) {
			files.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().endsWith(".json"))
					.sorted()
					.forEach(CustomModulesPersistence::loadModule);
		} catch (Exception e) {
			FlexHudLogger.error("Failed to load custom modules: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	private static void loadModule(Path file) {
		try (Reader reader = Files.newBufferedReader(file)) {
			JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
			if (!root.has("name") || !root.has("text")) {
				FlexHudLogger.warn("Skipping invalid custom module file {}", file.getFileName());
				return;
			}

			String name = root.get("name").getAsString();
			String text = root.get("text").getAsString();
			if (name.isBlank()) {
				FlexHudLogger.warn("Skipping custom module with blank name in {}", file.getFileName());
				return;
			}

			CustomModuleRegistry.register(CustomModule.fromText(name, text));
		} catch (IllegalStateException e) {
			FlexHudLogger.warn("Skipping duplicate custom module from {}: {}", file.getFileName(), e.getMessage());
		} catch (Exception e) {
			FlexHudLogger.error("Failed to load custom module {}: {}", file.getFileName(), e.getMessage());
			e.printStackTrace();
		}
	}
}
