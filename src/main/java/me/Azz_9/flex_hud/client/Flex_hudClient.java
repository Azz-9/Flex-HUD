package me.Azz_9.flex_hud.client;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.HudElement;
import me.Azz_9.flex_hud.client.tickables.TickRegistry;
import me.Azz_9.flex_hud.client.utils.FaviconUtils;
import me.Azz_9.flex_hud.client.utils.FlexHudLogger;
import me.Azz_9.flex_hud.client.utils.SpeedTester;
import me.Azz_9.flex_hud.client.utils.compass.DimensionTracker;
import me.Azz_9.flex_hud.compat.CompatManager;
import me.Azz_9.flex_hud.compat.XaeroWaypointCollector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.List;

public class Flex_hudClient implements ClientModInitializer {

	private static final boolean DEBUG = Boolean.parseBoolean(System.getenv().getOrDefault("FLEXHUD_DEBUG", "false"));

	public static final String MOD_ID = "flex_hud";
	public static KeyBinding openOptionScreenKeyBind;

	public static boolean isInMoveElementScreen;

	private static long launchTime;
	private boolean joinedWorld;
	private boolean layersRegistered = false;

	private XaeroWaypointCollector xaeroCollector;

	@Override
	public void onInitializeClient() {
		launchTime = System.currentTimeMillis();

		FlexHudLogger.info("Flex HUD has started up.");

		if (DEBUG) {
			FlexHudLogger.info("Debug mode enabled.");
		}

		FlexHudLogger.info("Xaeros Minimap {}found !", CompatManager.isXaeroMinimapLoaded() ? "" : "not ");
		if (CompatManager.isXaeroMinimapLoaded()) {
			xaeroCollector = new XaeroWaypointCollector();
			ModulesHelper.getInstance().compass.setXaeroWaypoints(xaeroCollector.getWaypoints());
		}

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			if (layersRegistered) return;
			layersRegistered = true;

			for (AbstractModule module : ModulesHelper.getModules()) {
				module.init();
			}

			for (HudElement hudElement : ModulesHelper.getHudElements()) {
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

				if (ModulesHelper.getInstance().compass.isEnabled() && ModulesHelper.getInstance().compass.showXaerosMapWaypoints.getValue() && CompatManager.isXaeroMinimapLoaded()) {
					if ((joinedWorld && !xaeroCollector.available) || DimensionTracker.shouldInit) {
						xaeroCollector.init();
					} else {
						DimensionTracker.check();
					}
					xaeroCollector.updateWaypoints();
				}
			}
		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.getCurrentServerEntry() != null) { // joined a multiplayer server
				FaviconUtils.registerServerIcon(client.getCurrentServerEntry().getFavicon());
			}

			if (CompatManager.isXaeroMinimapLoaded()) {
				xaeroCollector.init();
				joinedWorld = true;
			}
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			if (CompatManager.isXaeroMinimapLoaded()) {
				joinedWorld = false;
				xaeroCollector.available = false;
			}
		});

		final KeyBinding.Category FLEX_HUD = KeyBinding.Category.create(Identifier.of(MOD_ID, "flex-hud"));

		// see KeyBindingMixin
		openOptionScreenKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("flex_hud.controls.open_menu", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_RIGHT_SHIFT, FLEX_HUD));
	}

	public static long getLaunchTime() {
		return launchTime;
	}

	public static boolean isDebug() {
		return DEBUG;
	}
}
