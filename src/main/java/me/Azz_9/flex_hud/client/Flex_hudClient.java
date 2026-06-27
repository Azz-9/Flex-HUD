package me.Azz_9.flex_hud.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.configurableModules.ConfigLoader;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.HudElement;
import me.Azz_9.flex_hud.client.customModules.CustomModule;
import me.Azz_9.flex_hud.client.customModules.CustomModulesPersistence;
import me.Azz_9.flex_hud.client.customModules.Variables;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;
import me.Azz_9.flex_hud.client.tickables.TickRegistry;
import me.Azz_9.flex_hud.client.utils.FaviconUtils;
import me.Azz_9.flex_hud.client.utils.FlexHudLogger;
import me.Azz_9.flex_hud.client.utils.PingUtils;
import me.Azz_9.flex_hud.client.utils.SpeedTester;
import me.Azz_9.flex_hud.compat.CompatManager;
import me.Azz_9.flex_hud.compat.waypointsCollectors.Collector;
import me.Azz_9.flex_hud.compat.waypointsCollectors.JourneyMapWaypointCollector;
import me.Azz_9.flex_hud.compat.waypointsCollectors.XaeroWaypointCollector;

public class Flex_hudClient implements ClientModInitializer {

	private static final boolean DEBUG = Boolean.parseBoolean(System.getenv().getOrDefault("FLEXHUD_DEBUG", "false"));

	public static final String MOD_ID = "flex_hud";
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	public static KeyBinding openOptionScreenKeyBind;

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
			Modifiers.init();

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

			HudElementRegistry.attachElementBefore(
					VanillaHudElements.CHAT,
					Identifier.of(MOD_ID, "custom_modules"),
					(context, tickDelta) -> {
						for (CustomModule module : ModulesHelper.getCustomModules()) {
							if (Flex_hudClient.isDebug()) {
								module.renderWithSpeedTest(context, tickDelta);
							} else {
								module.render(context, tickDelta);
							}
						}
					}
			);

			CustomModulesPersistence.loadConfig();
			if (!ModulesHelper.getCustomModules().isEmpty()) {
				ConfigLoader.loadConfig();
				ConfigLoader.saveConfig();
			}
		});

		// init variables when the languages are loaded
		ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(
				Identifier.of(MOD_ID, "variables_init"),
				(store, prepareExecutor, reloadSynchronizer, applyExecutor) ->
						reloadSynchronizer.whenPrepared(null).thenRunAsync(() -> {
							Variables.init();
							ModulesHelper.recompileCustomModules();
						}, applyExecutor)
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (ModulesHelper.getInstance().isEnabled.getValue()) {
				SpeedTester.tick();

				Variables.tick();

				TickRegistry.tickAll(client);

				for (TickableModule tickableModule : ModulesHelper.getTickables()) {
					if (tickableModule.isEnabled()) {
						tickableModule.tick();
					}
				}
			}
		});


		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			Variables.onJoinWorld();

			if (!client.isIntegratedServerRunning()) {
				PingUtils.packetSender = sender;
				PingUtils.startPinging();
			}

			if (client.getCurrentServerEntry() != null) { // joined a multiplayer server
				FaviconUtils.registerServerIcon(client.getCurrentServerEntry().getFavicon());
			}

			waypointCollectors.forEach(Collector::onJoinWorld);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			PingUtils.stopPinging();
			PingUtils.packetSender = null;
			waypointCollectors.forEach(Collector::onLeaveWorld);
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
