package me.Azz_9.flex_hud.client;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.Tickable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.durabilityPing.DurabilityPing;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.durabilityPing.ItemDurabilityLostCallback;
import me.Azz_9.flex_hud.client.utils.ChromaColorUtils;
import me.Azz_9.flex_hud.client.utils.FaviconUtils;
import me.Azz_9.flex_hud.client.utils.SpeedTester;
import me.Azz_9.flex_hud.client.utils.clock.ClockUtils;
import me.Azz_9.flex_hud.client.utils.compass.DimensionTracker;
import me.Azz_9.flex_hud.client.utils.compass.TamedEntityUtils;
import me.Azz_9.flex_hud.client.utils.speedometer.SpeedUtils;
import me.Azz_9.flex_hud.compat.XaeroCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Flex_hudClient implements ClientModInitializer {

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

		LOGGER.info("Xaeros Minimap {}found !", XaeroCompat.isXaerosMinimapLoaded() ? "" : "not ");

		List<AbstractHudElement> hudElements = ModulesHelper.getHudElements();

		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
			for (AbstractModule module : ModulesHelper.getModules()) {
				module.init();
			}

			for (AbstractHudElement element : hudElements) {
				Identifier id = Identifier.of(MOD_ID, element.getID());
				Identifier layer = element.getID().equals(ModulesHelper.getInstance().bossBar.getID()) ? IdentifiedLayer.BOSS_BAR : IdentifiedLayer.CHAT;
				layeredDrawer.attachLayerBefore(layer, id, element::render);
			}

			Identifier id = Identifier.of(MOD_ID, ModulesHelper.getInstance().crosshair.getID());
			layeredDrawer.attachLayerBefore(IdentifiedLayer.CROSSHAIR, id, ModulesHelper.getInstance().crosshair::render);
		});

		ItemDurabilityLostCallback.EVENT.register((stack, damage) -> {
			if (ModulesHelper.getInstance().isEnabled.getValue() && ModulesHelper.getInstance().durabilityPing.enabled.getValue()) {
				DurabilityPing durabilityPing = ModulesHelper.getInstance().durabilityPing;
				if (durabilityPing.isDurabilityUnderThreshold(stack)) {
					if (durabilityPing.checkElytraOnly.getValue()) {
						if (DurabilityPing.isElytra(stack)) {
							durabilityPing.pingPlayer(stack);
						}
					} else if (DurabilityPing.isArmorPiece(stack)) {
						if (durabilityPing.checkArmorPieces.getValue()) {
							durabilityPing.pingPlayer(stack);
						}
					} else {
						durabilityPing.pingPlayer(stack);
					}
				}
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (ModulesHelper.getInstance().isEnabled.getValue()) {
				SpeedTester.tick();

				ChromaColorUtils.updateColor();

				for (Tickable tickable : ModulesHelper.getTickables()) {
					if (tickable.isEnabled()) {
						tickable.tick();
					}
				}

				if (ModulesHelper.getInstance().clock.enabled.getValue()) ClockUtils.updateTime();
				if (ModulesHelper.getInstance().speedometer.enabled.getValue()) SpeedUtils.calculateSpeed();

				if (ModulesHelper.getInstance().compass.showTamedEntitiesPoint.getValue()) TamedEntityUtils.update();
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

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			if (ModulesHelper.getInstance().crosshair.crosshairTexture != null) {
				ModulesHelper.getInstance().crosshair.crosshairTexture.close();
			}
		});

		// see KeyBindingMixin
		openOptionScreenKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("flex_hud.controls.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "Flex HUD"));
	}

	public static long getLaunchTime() {
		return launchTime;
	}
}
