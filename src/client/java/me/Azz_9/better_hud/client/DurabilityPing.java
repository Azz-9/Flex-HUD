package me.Azz_9.better_hud.client;

import me.Azz_9.better_hud.ModMenu.Enum.DurabilityPingTypeEnum;
import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class DurabilityPing {

    public static Map<String, Long> lastPingTime = new HashMap<>();

    public static boolean isDurabilityUnderThreshold(ItemStack item, PlayerEntity player) {


        if (player == null || item == null || item.getMaxDamage() == 0 || !item.isDamageable()) {
            return false;
        }

        double durabilityLeft = item.getMaxDamage() - item.getDamage();
        double percentageLeft = (durabilityLeft / item.getMaxDamage()) * 100.0f;

        return percentageLeft < ModConfig.getInstance().durabilityPingThreshold;
    }

    //return true if player has been ping
    public static boolean pingPlayer(PlayerEntity player, ItemStack item) {
        long currentTime = System.currentTimeMillis();

        // 1 minute has passed since the last ping
        if (!lastPingTime.containsKey(item.getItem().getTranslationKey()) || currentTime - lastPingTime.get(item.getItem().getTranslationKey()) > 60000) {

            lastPingTime.put(item.getItem().getTranslationKey(), currentTime);

            // play sound, display message or both based on the selected option in the config menu
            if (ModConfig.getInstance().durabilityPingType != DurabilityPingTypeEnum.Sound) {
                Text message = Text.literal(item.getItemName().getString().toLowerCase() + " durability low!").formatted(Formatting.RED);
                player.sendMessage(message, true);
            }
            if (ModConfig.getInstance().durabilityPingType != DurabilityPingTypeEnum.Message) {
                player.playSoundToPlayer(SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.AMBIENT, 1.0f, 2.0f);
            }
            return true;
        }
        return false;
    }

}

//TODO faire un event custom : détéction quand un item perd 1 de dura