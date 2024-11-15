package me.Azz_9.better_hud.client;

import me.Azz_9.better_hud.ConfigScreen.OptionsScreen;
import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.client.Overlay.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

import static me.Azz_9.better_hud.client.DurabilityPing.isDurabilityUnderThreshold;
import static me.Azz_9.better_hud.client.DurabilityPing.pingPlayer;

public class Better_hudClient implements ClientModInitializer {
    //biome colors for coordinates overlay
    public static Map<RegistryKey<Biome>, Integer> biomeColors = new HashMap<>();

    @Override
    public void onInitializeClient() {
        // initialize modules
        TimeChanger.init();

        //initialize variable
        initializeBiomeColors();

        // HUD elements
        HudRenderCallback.EVENT.register(new CoordinatesOverlay());
        HudRenderCallback.EVENT.register(new FPSOverlay());
        HudRenderCallback.EVENT.register(new ClockOverlay());
        HudRenderCallback.EVENT.register(new ArmorStatusOverlay());
        HudRenderCallback.EVENT.register(new DirectionOverlay());
        HudRenderCallback.EVENT.register(new DayCounterOverlay());
        HudRenderCallback.EVENT.register(new PingOverlay());
        HudRenderCallback.EVENT.register(new ServerAddressOverlay());
        HudRenderCallback.EVENT.register(new MemoryUsageOverlay());
        HudRenderCallback.EVENT.register(new CPSOverlay());
        HudRenderCallback.EVENT.register(new SpeedometerOverlay());
        ModConfig.getInstance();

        // durability ping
        AttackBlockCallback.EVENT.register((player, world, hand, pos, face) -> {
            if (ModConfig.getInstance().isEnabled && ModConfig.getInstance().enableDurabilityPing && !player.isCreative() && !player.isSpectator() && isDurabilityUnderThreshold(player.getStackInHand(hand), player)) {
                pingPlayer(player, player.getStackInHand(hand));
            }

            return ActionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, pos, face) -> {
            if (ModConfig.getInstance().isEnabled && ModConfig.getInstance().enableDurabilityPing && !player.isCreative() && !player.isSpectator() && isDurabilityUnderThreshold(player.getStackInHand(hand), player)) {
                pingPlayer(player, player.getStackInHand(hand));
            }

            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (ModConfig.getInstance().isEnabled && ModConfig.getInstance().enableDurabilityPing && !player.isCreative() && !player.isSpectator() && isDurabilityUnderThreshold(player.getStackInHand(hand), player)) {
                pingPlayer(player, player.getStackInHand(hand));
            }

            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (ModConfig.getInstance().isEnabled && ModConfig.getInstance().enableDurabilityPing && !player.isCreative() && !player.isSpectator() && isDurabilityUnderThreshold(player.getStackInHand(hand), player)) {
                pingPlayer(player, player.getStackInHand(hand));
            }

            return ActionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null
                    || !ModConfig.getInstance().isEnabled || !ModConfig.getInstance().enableDurabilityPing
                    || (!ModConfig.getInstance().checkArmorPieces && !ModConfig.getInstance().checkElytraOnly)) {
                return;
            }

            if (ModConfig.getInstance().checkElytraOnly) {
                ItemStack chestplateSlotItem = client.player.getInventory().getArmorStack(2);
                if (chestplateSlotItem.getItem() == Items.ELYTRA && isDurabilityUnderThreshold(chestplateSlotItem, client.player)) {
                    pingPlayer(client.player, chestplateSlotItem);
                }
            } else if (ModConfig.getInstance().checkArmorPieces) {
                for (ItemStack armorPiece : client.player.getInventory().player.getAllArmorItems()) {
                    if (isDurabilityUnderThreshold(armorPiece, client.player)) {
                        pingPlayer(client.player, armorPiece);
                    }
                }
            }
        });

        //speedometer
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ModConfig.getInstance().isEnabled || !ModConfig.getInstance().showSpeedometer || client.player == null) {
                return;
            }
            SpeedometerOverlay.calculateSpeed(client.player);
        });


        //keybinds
        KeyBinding rightShift = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fabric-key-binding-api-v1-testmod.test_keybinding_1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.category.first.test"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (rightShift.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new OptionsScreen(Text.empty())); // Display the config screen when right shift is pressed
            }

        });

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

}