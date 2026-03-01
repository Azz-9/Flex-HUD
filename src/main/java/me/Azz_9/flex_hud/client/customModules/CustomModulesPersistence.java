package me.Azz_9.flex_hud.client.customModules;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class CustomModulesPersistence {
	private final static File CUSTOM_MODULES_FODLER = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).resolve("custom_modules").toFile();

	public static void saveConfig() {

	}
}
