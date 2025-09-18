package me.Azz_9.flex_hud.client;

import me.Azz_9.flex_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.durabilityPing.DurabilityPing;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.durabilityPing.ItemDurabilityLostCallback;
import me.Azz_9.flex_hud.client.utils.ChromaColorUtils;
import me.Azz_9.flex_hud.client.utils.FaviconUtils;
import me.Azz_9.flex_hud.client.utils.clock.ClockUtils;
import me.Azz_9.flex_hud.client.utils.compass.DimensionTracker;
import me.Azz_9.flex_hud.client.utils.compass.TamedEntityUtils;
import me.Azz_9.flex_hud.client.utils.memoryUsage.MemoryUsageUtils;
import me.Azz_9.flex_hud.client.utils.reach.ReachUtils;
import me.Azz_9.flex_hud.client.utils.speedometer.SpeedUtils;
import me.Azz_9.flex_hud.compat.XaeroCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
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
	private boolean hudModulesInitialized = false;

	public static boolean isInMoveElementScreen;

	private static long launchTime;
	private boolean joinedWorld;

	@Override
	public void onInitializeClient() {
		launchTime = System.currentTimeMillis();

		LOGGER.info("Flex HUD has started up.");

		LOGGER.info("Xaeros Minimap {}found !", XaeroCompat.isXaerosMinimapLoaded() ? "" : "not ");

		ItemDurabilityLostCallback.EVENT.register((stack, damage) -> {
			if (JsonConfigHelper.getInstance().isEnabled && JsonConfigHelper.getInstance().durabilityPing.enabled.getValue()) {
				DurabilityPing durabilityPing = JsonConfigHelper.getInstance().durabilityPing;
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
			if (JsonConfigHelper.getInstance().isEnabled) {
				ChromaColorUtils.updateColor();
				if (JsonConfigHelper.getInstance().clock.enabled.getValue()) ClockUtils.updateTime();
				if (JsonConfigHelper.getInstance().memoryUsage.enabled.getValue()) MemoryUsageUtils.updateMemoryUsage();
				if (JsonConfigHelper.getInstance().speedometer.enabled.getValue()) SpeedUtils.calculateSpeed();
				if (JsonConfigHelper.getInstance().tntCountdown.enabled.getValue())
					JsonConfigHelper.getInstance().tntCountdown.renderCountdown();
				if (JsonConfigHelper.getInstance().reach.enabled.getValue()) ReachUtils.tick();

				if (JsonConfigHelper.getInstance().compass.showTamedEntitiesPoint.getValue()) TamedEntityUtils.update();
				if (JsonConfigHelper.getInstance().compass.showXaerosMapWaypoints.getValue() && XaeroCompat.isXaerosMinimapLoaded()) {
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
			if (JsonConfigHelper.getInstance().isEnabled) {
				if (!hudModulesInitialized) {
					List<AbstractHudElement> hudElements = JsonConfigHelper.getHudElements();

					for (int i = 0; i < hudElements.size(); i++) {
						hudElements.get(i).init();

						HudElementRegistry.attachElementBefore(
								VanillaHudElements.CHAT,
								Identifier.of(MOD_ID, "hud_element-" + i),
								hudElements.get(i)::render
						);
					}

					JsonConfigHelper.getInstance().crosshair.init();
					HudElementRegistry.attachElementAfter(
							VanillaHudElements.CROSSHAIR,
							Identifier.of(MOD_ID, "custom_crosshair"),
							JsonConfigHelper.getInstance().crosshair::render
					);

					JsonConfigHelper.getInstance().bossBar.init();
					HudElementRegistry.attachElementAfter(
							VanillaHudElements.BOSS_BAR,
							Identifier.of(MOD_ID, "custom_boss_bar"),
							JsonConfigHelper.getInstance().bossBar::render
					);

					JsonConfigHelper.getInstance().durabilityPing.init();
					JsonConfigHelper.getInstance().tntCountdown.init();
					JsonConfigHelper.getInstance().timeChanger.init();
					JsonConfigHelper.getInstance().weatherChanger.init();

					hudModulesInitialized = true;
				}

				if (client.getCurrentServerEntry() != null) { // joined a multiplayer server
					FaviconUtils.registerServerIcon(client.getCurrentServerEntry().getFavicon());
				}

				if (JsonConfigHelper.getInstance().compass.showXaerosMapWaypoints.getValue() && XaeroCompat.isXaerosMinimapLoaded()) {
					XaeroCompat.init();
					joinedWorld = true;
				}
			}
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			if (JsonConfigHelper.getInstance().isEnabled && JsonConfigHelper.getInstance().compass.showXaerosMapWaypoints.getValue() && XaeroCompat.isXaerosMinimapLoaded()) {
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
}
