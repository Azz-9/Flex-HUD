package me.Azz_9.better_hud.client;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.time.LocalTime;

public class TimeChanger {

    public static void init() {

        ModConfig modConfigInstance = ModConfig.getInstance();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (modConfigInstance.isEnabled && modConfigInstance.enableTimeChanger) {
                if (client.world != null) {
                    long currentTime = client.world.getTime();
                    long desiredTime;

                    if (modConfigInstance.useRealTime) {
                        desiredTime = convertRealTimeToMinecraftTime(LocalTime.now());
                    } else {
                        desiredTime = modConfigInstance.selectedTime;
                    }

                    // Update time only if it has changed
                    if (currentTime != desiredTime) {
                        client.world.setTime(currentTime, desiredTime, false);
                    }
                }
            }
        });
    }

    private static long convertRealTimeToMinecraftTime(LocalTime realTime) {
        // Dans Minecraft, un jour dure 24000 ticks
        // Minuit est à 18000, midi est à 6000
        int hour = realTime.getHour();
        int minute = realTime.getMinute();

        // Convertir l'heure réelle en ticks Minecraft
        long minecraftTime = ((hour + 6) % 24) * 1000; // +6 pour aligner minuit à 18000
        minecraftTime += (long) (minute / 60.0 * 1000);

        return minecraftTime;
    }
}

//FIXME ça me clc