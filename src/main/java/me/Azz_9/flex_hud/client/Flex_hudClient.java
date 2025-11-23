package me.Azz_9.flex_hud.client;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.HudElement;
import me.Azz_9.flex_hud.client.tickables.TickRegistry;
import me.Azz_9.flex_hud.client.utils.FaviconUtils;
import me.Azz_9.flex_hud.client.utils.SpeedTester;
import me.Azz_9.flex_hud.client.utils.compass.DimensionTracker;
import me.Azz_9.flex_hud.compat.XaeroCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Flex_hudClient implements ClientModInitializer {

	private static final boolean DEBUG = Boolean.parseBoolean(System.getenv().getOrDefault("FLEXHUD_DEBUG", "false"));

	public static final String MOD_ID = "flex_hud";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static KeyBinding openOptionScreenKeyBind;

	public static boolean isInMoveElementScreen;

	private static long launchTime;
	private boolean joinedWorld;

	@Override
	public void onInitializeClient() {
		launchTime = System.currentTimeMillis();

		LOGGER.info("Flex HUD has started up.");

		if (DEBUG) {
			LOGGER.info("Debug mode enabled.");
		}

		LOGGER.info("Xaeros Minimap {}found !", XaeroCompat.isXaerosMinimapLoaded() ? "" : "not ");

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			for (AbstractModule module : ModulesHelper.getModules()) {
				module.init();
			}

			List<HudElement> hudElements = ModulesHelper.getHudElements();

			for (HudElement hudElement : hudElements) {
				HudElementRegistry.attachElementBefore(
						hudElement.getLayer(),
						Identifier.of(MOD_ID, hudElement.getID()),
						Flex_hudClient.isDebug() ? hudElement::renderWithSpeedTest : hudElement::render
				);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (ModulesHelper.getInstance().isEnabled.getValue()) {
				SpeedTester.tick();

				TickRegistry.tickAll(client);

				for (TickableModule tickableModule : ModulesHelper.getTickables()) {
					if (tickableModule.isEnabled()) {
						tickableModule.tick();
					}
				}

				if (ModulesHelper.getInstance().compass.showXaerosMapWaypoints.getValue() && XaeroCompat.isXaerosMinimapLoaded()) {
					if ((joinedWorld && !XaeroCompat.available) || DimensionTracker.shouldInit) {
						XaeroCompat.init();
					} else {
						DimensionTracker.check();
					}
					XaeroCompat.updateWaypoints();
				}
			}
		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.getCurrentServerEntry() != null) { // joined a multiplayer server
				FaviconUtils.registerServerIcon(client.getCurrentServerEntry().getFavicon());
			}

			if (XaeroCompat.isXaerosMinimapLoaded()) {
				XaeroCompat.init();
				joinedWorld = true;
			}
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			if (XaeroCompat.isXaerosMinimapLoaded()) {
				joinedWorld = false;
				XaeroCompat.available = false;
			}
		});

		// see KeyBindingMixin
		openOptionScreenKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("flex_hud.controls.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "Flex HUD"));
	}

	public static long getLaunchTime() {
		return launchTime;
	}

	public static boolean isDebug() {
		return DEBUG;
	}
}
