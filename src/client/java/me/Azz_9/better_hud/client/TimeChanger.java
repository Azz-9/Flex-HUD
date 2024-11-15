package me.Azz_9.better_hud.client;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.time.LocalTime;

public class TimeChanger {

    public static void init() {

        ModConfig modConfigInstance = ModConfig.getInstance();

        if (modConfigInstance.isEnabled && modConfigInstance.enableTimeChanger) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (client.world != null) {
                    if (modConfigInstance.useRealTime) {
                        long minecraftTime = convertRealTimeToMinecraftTime(LocalTime.now());
                        //client.world.setTimeOfDay(minecraftTime);
                        client.world.setTime(client.world.getTime(), minecraftTime, false);
                    } else {
                        //client.world.setTimeOfDay(modConfigInstance.selectedTime);
                        client.world.setTime(client.world.getTime(), modConfigInstance.selectedTime, false);
                        System.out.println("bonjour");
                    }
                }

            });

        }

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
}//FIXME aled réparer ça