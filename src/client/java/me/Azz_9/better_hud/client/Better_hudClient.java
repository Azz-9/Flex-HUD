package me.Azz_9.better_hud.client;

import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.utils.ChromaColorUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Better_hudClient implements ClientModInitializer {

	public static final String MOD_ID = "better_hud";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean isInMoveElementScreen;

	private static long launchTime;
	//is mod Xaero's Minimap loaded
	public static boolean isXaerosMinimapLoaded = FabricLoader.getInstance().isModLoaded("xaerominimap");
	//TODO

	@Override
	public void onInitializeClient() {
		launchTime = System.currentTimeMillis();

		LOGGER.info("Better HUD has started up.");
		LOGGER.info("Xaeros Minimap {}found !", isXaerosMinimapLoaded ? "" : "not ");

		List<AbstractHudElement> hudElements = JsonConfigHelper.getHudElements();
		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
			for (int i = 0; i < hudElements.size(); i++) {
				AbstractHudElement element = hudElements.get(i);
				Identifier id = Identifier.of(MOD_ID, "element-" + i);
				layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, id, element::render);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> ChromaColorUtil.updateColor());
	}

	public static long getLaunchTime() {
		return launchTime;
	}
}
