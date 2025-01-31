package me.Azz_9.better_hud.client;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.Screens.OptionsScreen;
import me.Azz_9.better_hud.client.Interface.ItemDurabilityLostCallback;
import me.Azz_9.better_hud.client.Overlay.*;
import me.Azz_9.better_hud.client.utils.CalculateSpeed;
import me.Azz_9.better_hud.client.utils.DurabilityPing;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.Azz_9.better_hud.client.Overlay.ComboCounterOverlay.calculteCombo;
import static me.Azz_9.better_hud.client.Overlay.ComboCounterOverlay.resetCombo;
import static me.Azz_9.better_hud.client.Overlay.ReachDisplayOverlay.calculateReach;

public class Better_hudClient implements ClientModInitializer {

    public static final String MOD_ID = "better_hud";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final ModConfig INSTANCE = ModConfig.getInstance();

    public static List<HudRenderCallback> hudElements;
    public static boolean isEditing = false;

    //biome colors for coordinates overlay
    public static Map<RegistryKey<Biome>, Integer> biomeColors = new HashMap<>();
    //launch time
    private static long launchTime;
    //is mod Xaero's Minimap loaded
    public static boolean isXaerosMinimapLoaded = FabricLoader.getInstance().isModLoaded("xaerominimap");


    @Override
    public void onInitializeClient() {

        launchTime = System.currentTimeMillis();

        LOGGER.info("Better HUD has started up.");

        //initialize variable
        initializeBiomeColors();

        // HUD elements
        initializeHudElements();
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
            if (!INSTANCE.isEnabled || !INSTANCE.showSpeedometer || client.player == null) {
                return;
            }
            CalculateSpeed.calculateSpeed(client.player);
        });

        //reach display
        AttackEntityCallback.EVENT.register((player, world, hand, pos, face) -> {
            if (INSTANCE.isEnabled && INSTANCE.showReach) {
                calculateReach(player, pos);
            }
            return ActionResult.PASS;
        });

        //combo counter
        AttackEntityCallback.EVENT.register((player, world, hand, pos, face) -> {
            if (INSTANCE.isEnabled && INSTANCE.showComboCounter) {
                calculteCombo(player, pos);
            }
            return ActionResult.PASS;
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, damageSource, baseDamageTaken, damageTaken, blocked) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (INSTANCE.isEnabled && INSTANCE.showComboCounter && entity == client.player && !blocked) {
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

    private void initializeHudElements() {
        hudElements = List.of(
                new CoordinatesOverlay(),
                new FPSOverlay(),
                new ClockOverlay(),
                new ArmorStatusOverlay(),
                new DirectionOverlay(),
                new DayCounterOverlay(),
                new PingOverlay(),
                new ServerAddressOverlay(),
                new MemoryUsageOverlay(),
                new CPSOverlay(),
                new SpeedometerOverlay(),
                new ReachDisplayOverlay(),
                new ComboCounterOverlay(),
                new PlaytimeOverlay(),
                new ShriekerWarningLevelOverlay()
        );
    }

    private void initializeBiomeColors() {
        biomeColors.put(BiomeKeys.THE_VOID, 0xFFFFFF);
        biomeColors.put(BiomeKeys.PLAINS, 0x5e9d34);
        biomeColors.put(BiomeKeys.SUNFLOWER_PLAINS, 0xfcd500);
        biomeColors.put(BiomeKeys.SNOWY_PLAINS, 0x9dbcf0);
        biomeColors.put(BiomeKeys.ICE_SPIKES, 0x9dbcf0);
        biomeColors.put(BiomeKeys.DESERT, 0xe5d9af);
        biomeColors.put(BiomeKeys.SWAMP, 0x436024);
        biomeColors.put(BiomeKeys.MANGROVE_SWAMP, 0x436024);
        biomeColors.put(BiomeKeys.FOREST, 0x4d8a25);
        biomeColors.put(BiomeKeys.FLOWER_FOREST, 0xfc7dea);
        biomeColors.put(BiomeKeys.BIRCH_FOREST, 0xcccccc);
        biomeColors.put(BiomeKeys.DARK_FOREST, 0x366821);
        biomeColors.put(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, 0xcccccc);
        biomeColors.put(BiomeKeys.OLD_GROWTH_PINE_TAIGA, 0x4d6a4c);
        biomeColors.put(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, 0x4d6a4c);
        biomeColors.put(BiomeKeys.TAIGA, 0x4d6a4c);
        biomeColors.put(BiomeKeys.SNOWY_TAIGA, 0x9dbcf0);
        biomeColors.put(BiomeKeys.SAVANNA, 0x807b39);
        biomeColors.put(BiomeKeys.SAVANNA_PLATEAU, 0x807b39);
        biomeColors.put(BiomeKeys.WINDSWEPT_HILLS, 0x4d8a25);
        biomeColors.put(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, 0x4d8a25);
        biomeColors.put(BiomeKeys.WINDSWEPT_FOREST, 0x4d8a25);
        biomeColors.put(BiomeKeys.WINDSWEPT_SAVANNA, 0x807b39);
        biomeColors.put(BiomeKeys.JUNGLE, 0x1c6e06);
        biomeColors.put(BiomeKeys.SPARSE_JUNGLE, 0x1c6e06);
        biomeColors.put(BiomeKeys.BAMBOO_JUNGLE, 0x678c39);
        biomeColors.put(BiomeKeys.BADLANDS, 0xb15b25);
        biomeColors.put(BiomeKeys.ERODED_BADLANDS, 0xb15b25);
        biomeColors.put(BiomeKeys.WOODED_BADLANDS, 0xb15b25);
        biomeColors.put(BiomeKeys.MEADOW, 0x8d8d8d);
        biomeColors.put(BiomeKeys.CHERRY_GROVE, 0xbf789b);
        biomeColors.put(BiomeKeys.GROVE, 0x4d8a25);
        biomeColors.put(BiomeKeys.SNOWY_SLOPES, 0x9dbcf0);
        biomeColors.put(BiomeKeys.FROZEN_PEAKS, 0x8d8d8d);
        biomeColors.put(BiomeKeys.JAGGED_PEAKS, 0x8d8d8d);
        biomeColors.put(BiomeKeys.STONY_PEAKS, 0x8d8d8d);
        biomeColors.put(BiomeKeys.RIVER, 0x005eec);
        biomeColors.put(BiomeKeys.FROZEN_RIVER, 0x9dbcf0);
        biomeColors.put(BiomeKeys.BEACH, 0xe5d9af);
        biomeColors.put(BiomeKeys.SNOWY_BEACH, 0x9dbcf0);
        biomeColors.put(BiomeKeys.STONY_SHORE, 0x8d8d8d);
        biomeColors.put(BiomeKeys.WARM_OCEAN, 0x00fccf);
        biomeColors.put(BiomeKeys.LUKEWARM_OCEAN, 0x00fccf);
        biomeColors.put(BiomeKeys.DEEP_LUKEWARM_OCEAN, 0x00fccf);
        biomeColors.put(BiomeKeys.OCEAN, 0x005eec);
        biomeColors.put(BiomeKeys.DEEP_OCEAN, 0x005eec);
        biomeColors.put(BiomeKeys.COLD_OCEAN, 0x9dbcf0);
        biomeColors.put(BiomeKeys.DEEP_COLD_OCEAN, 0x9dbcf0);
        biomeColors.put(BiomeKeys.FROZEN_OCEAN, 0x9dbcf0);
        biomeColors.put(BiomeKeys.DEEP_FROZEN_OCEAN, 0x9dbcf0);
        biomeColors.put(BiomeKeys.MUSHROOM_FIELDS, 0x746f82);
        biomeColors.put(BiomeKeys.DRIPSTONE_CAVES, 0x816759);
        biomeColors.put(BiomeKeys.LUSH_CAVES, 0x576b2c);
        biomeColors.put(BiomeKeys.DEEP_DARK, 0x0d1f25);
        biomeColors.put(BiomeKeys.NETHER_WASTES, 0x880f0f);
        biomeColors.put(BiomeKeys.WARPED_FOREST, 0x0f8976);
        biomeColors.put(BiomeKeys.CRIMSON_FOREST, 0x880f0f);
        biomeColors.put(BiomeKeys.SOUL_SAND_VALLEY, 0x62493b);
        biomeColors.put(BiomeKeys.BASALT_DELTAS, 0x55576a);
        biomeColors.put(BiomeKeys.THE_END, 0x6d00a3);
        biomeColors.put(BiomeKeys.END_HIGHLANDS, 0x6d00a3);
        biomeColors.put(BiomeKeys.END_MIDLANDS, 0x6d00a3);
        biomeColors.put(BiomeKeys.SMALL_END_ISLANDS, 0x6d00a3);
        biomeColors.put(BiomeKeys.END_BARRENS, 0x6d00a3);
    }

    public static long getLaunchTime() {
        return launchTime;
    }

}

//TODO faire les fichiers langues en_us, fr_fr
//TODO faire des tooltip custom