package me.Azz_9.flex_hud;

import static me.Azz_9.flex_hud.Constants.DEBUG;
import static me.Azz_9.flex_hud.Constants.MOD_ID;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.config.ConfigLoader;
import me.Azz_9.flex_hud.client.debug.PerfTester;
import me.Azz_9.flex_hud.client.gui.screens.OptionsScreen;
import me.Azz_9.flex_hud.client.modules.AbstractModule;
import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.TickableModule;
import me.Azz_9.flex_hud.client.modules.customModules.CustomModule;
import me.Azz_9.flex_hud.client.modules.customModules.CustomModulePreview;
import me.Azz_9.flex_hud.client.modules.customModules.CustomModulesPersistence;
import me.Azz_9.flex_hud.client.modules.customModules.Variables;
import me.Azz_9.flex_hud.client.modules.customModules.modifiers.Modifiers;
import me.Azz_9.flex_hud.client.modules.hud.HudElement;
import me.Azz_9.flex_hud.client.tickables.LivingEntityHeadClientAudit;
import me.Azz_9.flex_hud.client.tickables.TickRegistry;
import me.Azz_9.flex_hud.compat.CompatManager;
import me.Azz_9.flex_hud.compat.waypointsCollectors.Collector;
import me.Azz_9.flex_hud.compat.waypointsCollectors.JourneyMapWaypointCollector;
import me.Azz_9.flex_hud.compat.waypointsCollectors.XaeroWaypointCollector;
import me.Azz_9.flex_hud.platform.Services;
import me.Azz_9.flex_hud.utils.FaviconUtils;
import me.Azz_9.flex_hud.utils.PingUtils;
import me.Azz_9.flex_hud.utils.TpsUtils;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as NeoForge events
// however it will be compatible with all supported mod loaders.
public class CommonClass {

	public static Minecraft MINECRAFT;

	public static KeyMapping openOptionScreenKeyBind;
	public static @Nullable KeyMapping printPerfTesterTimesKeyBind;

	public static boolean isEditingLayout;

	private static long launchTime;
	private static boolean layersRegistered = false;

	private static final List<Collector<?>> waypointCollectors = new ArrayList<>();

	public static void init() {
		launchTime = System.currentTimeMillis();

		FlexHudLogger.info("Flex HUD has started up.");

		if (DEBUG) {
			FlexHudLogger.info("Debug mode enabled.");
		}

		FlexHudLogger.info("Xaeros Minimap {}found !", CompatManager.isXaeroMinimapLoaded() ? "" : "not ");
		FlexHudLogger.info("JourneyMap {}found !", CompatManager.isJourneyMapLoaded() ? "" : "not ");

		initCollectors();

		initHudElements();

		Services.PLATFORM.registerClientStartEvent(() -> {
			MINECRAFT = Minecraft.getInstance();

			Modifiers.init();

			if (layersRegistered) return;
			layersRegistered = true;

			for (AbstractModule module : Modules.getModules()) {
				module.init();
			}

			CustomModulesPersistence.loadConfig();
			if (!Modules.getCustomModules().isEmpty()) {
				ConfigLoader.loadConfig();
				ConfigLoader.saveConfig();
			}
		});

		// init variables when the languages are loaded
		Services.PLATFORM.registerReloadListener(
				ResourceLocation.fromNamespaceAndPath(MOD_ID, "variables_init"),
				(store, prepareExecutor, reloadSynchronizer, applyExecutor) ->
						reloadSynchronizer.wait(null).thenRunAsync(() -> {
							Variables.init();
							Modules.recompileCustomModules();
							CustomModulePreview.recompile();
						}, applyExecutor));

		Services.PLATFORM.registerEndClientTickEvent(() -> {
			if (Modules.getInstance().isEnabled.getValue()) {
				Variables.tick();

				TickRegistry.tickAll(MINECRAFT);

				for (TickableModule tickableModule : Modules.getTickables()) {
					if (tickableModule.shouldTick()) {
						if (DEBUG) {
							tickableModule.tickWithPerfTest();
						} else {
							tickableModule.tick();
						}
					}
				}
			}
		});

		Services.PLATFORM.registerJoinEvent(() -> {
			Variables.onJoinWorld();
			LivingEntityHeadClientAudit.runIfEnabled(MINECRAFT);

			if (!MINECRAFT.isLocalServer()) {
				PingUtils.connection = MINECRAFT.getConnection();
				PingUtils.startPinging();
			}

			if (MINECRAFT.getCurrentServer() != null) { // joined a multiplayer server
				FaviconUtils.registerServerIcon(MINECRAFT.getCurrentServer().getIconBytes());
			}

			waypointCollectors.forEach(Collector::onJoinWorld);
		});

		Services.PLATFORM.registerDisconnectEvent(() -> {
			PingUtils.stopPinging();
			PingUtils.connection = null;
			TpsUtils.reset();
			waypointCollectors.forEach(Collector::onLeaveWorld);
		});

		final KeyMapping.Category FLEX_HUD = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "flex-hud"));

		// see KeyBindingMixin
		openOptionScreenKeyBind = Services.PLATFORM.registerKeyMapping(new KeyMapping("flex_hud.controls.open_menu", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, FLEX_HUD));
		if (DEBUG) {
			printPerfTesterTimesKeyBind = Services.PLATFORM.registerKeyMapping(new KeyMapping("Perf tester", InputConstants.Type.KEYSYM, InputConstants.KEY_I, FLEX_HUD));
		}
	}

	private static void initCollectors() {
		if (CompatManager.isXaeroMinimapLoaded()) {
			waypointCollectors.add(new XaeroWaypointCollector());
		}

		if (CompatManager.isJourneyMapLoaded()) {
			waypointCollectors.add(new JourneyMapWaypointCollector());
		}

		waypointCollectors.forEach(Collector::initCompassList);
	}

	private static void initHudElements() {
		for (HudElement hudElement : Modules.getHudElements()) {
			Services.PLATFORM.registerHudElement(
					hudElement.getLayer(),
					ResourceLocation.fromNamespaceAndPath(MOD_ID, hudElement.getID()),
					DEBUG ? hudElement::renderWithPerfTest : hudElement::render
			);
		}

		Services.PLATFORM.registerHudElement(
				Services.PLATFORM.getChatLocation(),
				ResourceLocation.fromNamespaceAndPath(MOD_ID, "custom_modules"),
				(graphics, deltaTracker) -> {
					for (CustomModule module : Modules.getCustomModules()) {
						if (DEBUG) {
							module.renderWithPerfTest(graphics, deltaTracker);
						} else {
							module.render(graphics, deltaTracker);
						}
					}
				}
		);
	}

	public static void handleKeybindsHook() {
		while (openOptionScreenKeyBind.consumeClick()) {
			MINECRAFT.setScreen(new OptionsScreen());
		}

		if (printPerfTesterTimesKeyBind != null) {
			while (printPerfTesterTimesKeyBind.consumeClick()) {
				PerfTester.print();
			}
		}
	}

	public static long getLaunchTime() {
		return launchTime;
	}
}
