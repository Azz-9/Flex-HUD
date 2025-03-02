package me.Azz_9.better_hud.client;

import me.Azz_9.better_hud.client.interfaces.ItemDurabilityLostCallback;
import me.Azz_9.better_hud.client.utils.CalculateSpeed;
import me.Azz_9.better_hud.client.utils.DurabilityPing;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.OptionsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static me.Azz_9.better_hud.client.overlay.ComboCounterOverlay.calculteCombo;
import static me.Azz_9.better_hud.client.overlay.ComboCounterOverlay.resetCombo;
import static me.Azz_9.better_hud.client.overlay.ReachOverlay.calculateReach;

public class Better_hudClient implements ClientModInitializer {

    public static final String MOD_ID = "better_hud";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private transient final ModConfig INSTANCE = ModConfig.getInstance();

    public static List<HudRenderCallback> hudElements;
    public static boolean isEditing = false;

    //biome colors for coordinates overlay
    public static final Map<RegistryKey<Biome>, Integer> BIOME_COLORS = Map.copyOf(Map.<RegistryKey<Biome>, Integer>ofEntries(
			entry(BiomeKeys.THE_VOID, 0xFFFFFF),
			entry(BiomeKeys.PLAINS, 0x5e9d34),
			entry(BiomeKeys.SUNFLOWER_PLAINS, 0xfcd500),
			entry(BiomeKeys.SNOWY_PLAINS, 0x9dbcf0),
			entry(BiomeKeys.ICE_SPIKES, 0x9dbcf0),
			entry(BiomeKeys.DESERT, 0xe5d9af),
			entry(BiomeKeys.SWAMP, 0x436024),
			entry(BiomeKeys.MANGROVE_SWAMP, 0x436024),
			entry(BiomeKeys.FOREST, 0x4d8a25),
			entry(BiomeKeys.FLOWER_FOREST, 0xfc7dea),
			entry(BiomeKeys.BIRCH_FOREST, 0xcccccc),
			entry(BiomeKeys.DARK_FOREST, 0x366821),
			entry(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, 0xccccc),
			entry(BiomeKeys.OLD_GROWTH_PINE_TAIGA, 0x4d6a4c),
			entry(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, 0x4d6a4),
			entry(BiomeKeys.TAIGA, 0x4d6a4c),
			entry(BiomeKeys.SNOWY_TAIGA, 0x9dbcf0),
			entry(BiomeKeys.SAVANNA, 0x807b39),
			entry(BiomeKeys.SAVANNA_PLATEAU, 0x807b39),
			entry(BiomeKeys.WINDSWEPT_HILLS, 0x4d8a25),
			entry(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, 0x4d8a),
			entry(BiomeKeys.WINDSWEPT_FOREST, 0x4d8a25),
			entry(BiomeKeys.WINDSWEPT_SAVANNA, 0x807b39),
			entry(BiomeKeys.JUNGLE, 0x1c6e06),
			entry(BiomeKeys.SPARSE_JUNGLE, 0x1c6e06),
			entry(BiomeKeys.BAMBOO_JUNGLE, 0x678c39),
			entry(BiomeKeys.BADLANDS, 0xb15b25),
			entry(BiomeKeys.ERODED_BADLANDS, 0xb15b25),
			entry(BiomeKeys.WOODED_BADLANDS, 0xb15b25),
			entry(BiomeKeys.MEADOW, 0x8d8d8d),
			entry(BiomeKeys.CHERRY_GROVE, 0xbf789b),
			entry(BiomeKeys.GROVE, 0x4d8a25),
			entry(BiomeKeys.SNOWY_SLOPES, 0x9dbcf0),
			entry(BiomeKeys.FROZEN_PEAKS, 0x8d8d8d),
			entry(BiomeKeys.JAGGED_PEAKS, 0x8d8d8d),
			entry(BiomeKeys.STONY_PEAKS, 0x8d8d8d),
			entry(BiomeKeys.RIVER, 0x005eec),
			entry(BiomeKeys.FROZEN_RIVER, 0x9dbcf0),
			entry(BiomeKeys.BEACH, 0xe5d9af),
			entry(BiomeKeys.SNOWY_BEACH, 0x9dbcf0),
			entry(BiomeKeys.STONY_SHORE, 0x8d8d8d),
			entry(BiomeKeys.WARM_OCEAN, 0x00fccf),
			entry(BiomeKeys.LUKEWARM_OCEAN, 0x00fccf),
			entry(BiomeKeys.DEEP_LUKEWARM_OCEAN, 0x00fccf),
			entry(BiomeKeys.OCEAN, 0x005eec),
			entry(BiomeKeys.DEEP_OCEAN, 0x005eec),
			entry(BiomeKeys.COLD_OCEAN, 0x9dbcf0),
			entry(BiomeKeys.DEEP_COLD_OCEAN, 0x9dbcf0),
			entry(BiomeKeys.FROZEN_OCEAN, 0x9dbcf0),
			entry(BiomeKeys.DEEP_FROZEN_OCEAN, 0x9dbcf0),
			entry(BiomeKeys.MUSHROOM_FIELDS, 0x746f82),
			entry(BiomeKeys.DRIPSTONE_CAVES, 0x816759),
			entry(BiomeKeys.LUSH_CAVES, 0x576b2c),
			entry(BiomeKeys.DEEP_DARK, 0x0d1f25),
			entry(BiomeKeys.NETHER_WASTES, 0x880f0f),
			entry(BiomeKeys.WARPED_FOREST, 0x0f8976),
			entry(BiomeKeys.CRIMSON_FOREST, 0x880f0f),
			entry(BiomeKeys.SOUL_SAND_VALLEY, 0x62493b),
			entry(BiomeKeys.BASALT_DELTAS, 0x55576a),
			entry(BiomeKeys.THE_END, 0x6d00a3),
			entry(BiomeKeys.END_HIGHLANDS, 0x6d00a3),
			entry(BiomeKeys.END_MIDLANDS, 0x6d00a3),
			entry(BiomeKeys.SMALL_END_ISLANDS, 0x6d00a3),
			entry(BiomeKeys.END_BARRENS, 0x6d00a3)
	));
    //launch time
    private static long launchTime;
    //is mod Xaero's Minimap loaded
    public static boolean isXaerosMinimapLoaded = FabricLoader.getInstance().isModLoaded("xaerominimap");



    @Override
    public void onInitializeClient() {

        hudElements = ModConfig.getHudElements();

        launchTime = System.currentTimeMillis();

        LOGGER.info("Better HUD has started up.");
		if (isXaerosMinimapLoaded) {
			LOGGER.info("Xaeros Minimap found !");
		} else {
			LOGGER.info("Xaeros Minimap not found !");
		}

        // HUD elements
        for (HudRenderCallback element : hudElements) {
            HudRenderCallback.EVENT.register(element);
        }

        // durability ping
        ItemDurabilityLostCallback.EVENT.register((player, stack, amount) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && player.getUuid().equals(client.player.getUuid()) && DurabilityPing.isDurabilityUnderThreshold(stack)) {
                DurabilityPing.pingPlayer(player, stack);
            }
        });

        //speedometer
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!INSTANCE.isEnabled || !INSTANCE.speedometer.enabled || client.player == null) {
                return;
            }
            CalculateSpeed.calculateSpeed(client.player);
        });

        //reach display
        AttackEntityCallback.EVENT.register((player, world, hand, pos, face) -> {
            if (INSTANCE.isEnabled && INSTANCE.reach.enabled) {
                calculateReach(player, pos);
            }
            return ActionResult.PASS;
        });

        //combo counter
        AttackEntityCallback.EVENT.register((player, world, hand, pos, face) -> {
            if (INSTANCE.isEnabled && INSTANCE.comboCounter.enabled) {
                calculteCombo(player, pos);
            }
            return ActionResult.PASS;
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, damageSource, baseDamageTaken, damageTaken, blocked) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (INSTANCE.isEnabled && INSTANCE.comboCounter.enabled && entity == client.player && !blocked) {
                resetCombo();
            }
        });


        //keybinds
        KeyBinding rightShift = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "Better HUD"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (rightShift.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new OptionsScreen(Text.empty())); // Display the config screen when right shift is pressed
            }

        });

    }

    public static long getLaunchTime() {
        return launchTime;
    }

}

//TODO faire les fichiers langues en_us, fr_fr
//TODO faire des tooltip custom