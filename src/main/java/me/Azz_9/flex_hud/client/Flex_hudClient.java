package me.Azz_9.flex_hud.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.HudElement;
import me.Azz_9.flex_hud.client.tickables.TickRegistry;
import me.Azz_9.flex_hud.client.utils.FaviconUtils;
import me.Azz_9.flex_hud.client.utils.FlexHudLogger;
import me.Azz_9.flex_hud.client.utils.SpeedTester;
import me.Azz_9.flex_hud.compat.CompatManager;
import me.Azz_9.flex_hud.compat.waypointsCollectors.Collector;
import me.Azz_9.flex_hud.compat.waypointsCollectors.JourneyMapWaypointCollector;
import me.Azz_9.flex_hud.compat.waypointsCollectors.XaeroWaypointCollector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Flex_hudClient implements ClientModInitializer {

	private static final boolean DEBUG = Boolean.parseBoolean(System.getenv().getOrDefault("FLEXHUD_DEBUG", "false"));

	public static final String MOD_ID = "flex_hud";
	public static KeyMapping openOptionScreenKeyBind;

	public static boolean isInMoveElementScreen;

	private static long launchTime;
	private boolean layersRegistered = false;

	private final List<Collector<?>> waypointCollectors = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		launchTime = System.currentTimeMillis();

		FlexHudLogger.info("Flex HUD has started up.");

		if (DEBUG) {
			FlexHudLogger.info("Debug mode enabled.");
		}

		FlexHudLogger.info("Xaeros Minimap {}found !", CompatManager.isXaeroMinimapLoaded() ? "" : "not ");
		FlexHudLogger.info("JourneyMap {}found !", CompatManager.isJourneyMapLoaded() ? "" : "not ");

		if (CompatManager.isXaeroMinimapLoaded()) {
			waypointCollectors.add(new XaeroWaypointCollector());
		}

		if (CompatManager.isJourneyMapLoaded()) {
			waypointCollectors.add(new JourneyMapWaypointCollector());
		}

		waypointCollectors.forEach(Collector::initCompassList);

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			if (layersRegistered) return;
			layersRegistered = true;

			for (AbstractModule module : ModulesHelper.getModules()) {
				module.init();
			}

			for (HudElement hudElement : ModulesHelper.getHudElements()) {
				HudElementRegistry.attachElementBefore(
						hudElement.getLayer(),
						Identifier.fromNamespaceAndPath(MOD_ID, hudElement.getID()),
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
			}
		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.getCurrentServer() != null) { // joined a multiplayer server
				FaviconUtils.registerServerIcon(client.getCurrentServer().getIconBytes());
			}

			waypointCollectors.forEach(Collector::onJoinWorld);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			waypointCollectors.forEach(Collector::onLeaveWorld);
		});

		final KeyMapping.Category FLEX_HUD = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "flex-hud"));

		// see KeyBindingMixin
		openOptionScreenKeyBind = KeyMappingHelper.registerKeyMapping(new KeyMapping("flex_hud.controls.open_menu", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, FLEX_HUD));
	}

	public static long getLaunchTime() {
		return launchTime;
	}

	public static boolean isDebug() {
		return DEBUG;
	}
}
