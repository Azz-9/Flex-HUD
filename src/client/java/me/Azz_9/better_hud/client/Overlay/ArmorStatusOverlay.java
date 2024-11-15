package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.Enum.DisplayModeEnum;
import me.Azz_9.better_hud.ModMenu.Enum.DurabilityTypeEnum;
import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ArmorStatusOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        ModConfig modConfigInstance = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!modConfigInstance.isEnabled || !modConfigInstance.showArmorStatus || client == null || client.options.hudHidden || client.player == null) {
            return;
        }

        PlayerEntity p = client.player;

        MatrixStack matrices = drawContext.getMatrices();

        int x = modConfigInstance.armorStatusHudX;
        int y = modConfigInstance.armorStatusHudY;

        ItemStack helmet = p.getInventory().getArmorStack(3);
        ItemStack chestplate = p.getInventory().getArmorStack(2);
        ItemStack leggings = p.getInventory().getArmorStack(1);
        ItemStack boots = p.getInventory().getArmorStack(0);
        ItemStack heldItem = p.getInventory().getMainHandStack();


        if (modConfigInstance.showHelmet) {
            renderItemStack(helmet, x, y, modConfigInstance.showDurability, matrices, drawContext, client);

        }

        if (modConfigInstance.showChestplate) {
            if (modConfigInstance.displayModeArmorStatus == DisplayModeEnum.Vertical) {
                y += 16;
                renderItemStack(chestplate, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
            } else {
                if (modConfigInstance.showDurability == DurabilityTypeEnum.Percentage) {
                    x = x + 18 + client.textRenderer.getWidth((int) (((float) (helmet.getMaxDamage() - helmet.getDamage()) / helmet.getMaxDamage()) * 100) + "%");
                    renderItemStack(chestplate, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                } else {
                    x = x + 18 + client.textRenderer.getWidth(String.valueOf(helmet.getMaxDamage() - helmet.getDamage()));
                    renderItemStack(chestplate, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                }
            }


        }
        if (modConfigInstance.showLeggings) {
            if (modConfigInstance.displayModeArmorStatus == DisplayModeEnum.Vertical) {
                y += 16;
                renderItemStack(leggings, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
            } else {
                if (modConfigInstance.showDurability == DurabilityTypeEnum.Percentage) {
                    x = x + 18 + client.textRenderer.getWidth((int) (((float) (chestplate.getMaxDamage() - chestplate.getDamage()) / chestplate.getMaxDamage()) * 100) + "%");
                    renderItemStack(leggings, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                } else {
                    x = x + 18 + client.textRenderer.getWidth(String.valueOf(chestplate.getMaxDamage() - chestplate.getDamage()));
                    renderItemStack(leggings, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                }
            }

        }
        if (modConfigInstance.showBoots) {
            if (modConfigInstance.displayModeArmorStatus == DisplayModeEnum.Vertical) {
                y += 16;
                renderItemStack(boots, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
            } else {
                if (modConfigInstance.showDurability == DurabilityTypeEnum.Percentage) {
                    x = x + 18 + client.textRenderer.getWidth((int) (((float) (leggings.getMaxDamage() - leggings.getDamage()) / leggings.getMaxDamage()) * 100) + "%");
                    renderItemStack(boots, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                } else {
                    x = x + 18 + client.textRenderer.getWidth(String.valueOf(leggings.getMaxDamage() - leggings.getDamage()));
                    renderItemStack(boots, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                }
            }

        }
        if (modConfigInstance.showHeldItem) {
            if (modConfigInstance.displayModeArmorStatus == DisplayModeEnum.Vertical) {
                y += 16;
                renderItemStack(heldItem, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
            } else {
                if (modConfigInstance.showDurability == DurabilityTypeEnum.Percentage) {
                    x = x + 18 + client.textRenderer.getWidth((int) (((float) (boots.getMaxDamage() - boots.getDamage()) / boots.getMaxDamage()) * 100) + "%");
                    renderItemStack(heldItem, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                } else {
                    x = x + 18 + client.textRenderer.getWidth(String.valueOf(boots.getMaxDamage() - boots.getDamage()));
                    renderItemStack(heldItem, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                }
            }

        }

    }

    private void renderItemStack(ItemStack item, int x, int y, DurabilityTypeEnum displayType, MatrixStack matrices, DrawContext drawContext, MinecraftClient client) {
        if (item.isEmpty()) {
            return;
        }

        matrices.push();
        matrices.translate(x, y, 0.0);

        drawContext.drawItem(client.player, item, 0, 0, 1);

        matrices.pop();

        if (displayType != DurabilityTypeEnum.No) {
            displayDurabilityOrCount(item, displayType, x, y, drawContext, client);
        }

    }

    private void displayDurabilityOrCount(ItemStack item, DurabilityTypeEnum displayType, int x, int y, DrawContext drawContext, MinecraftClient client) {
        x += 17; // place the text beside the item in the hud
        y += 4; // lower the text to make it align with the item
        if (!item.isDamageable()){

            String itemCount = String.valueOf(item.getCount());
            drawContext.drawText(client.textRenderer, itemCount, x, y, ModConfig.getInstance().armorStatusTextColor, ModConfig.getInstance().armorStatusTextShadow);

        } else {
            if (displayType == DurabilityTypeEnum.Percentage) {
                //display durability in percentage
                float percentage = (float) (item.getMaxDamage() - item.getDamage()) / item.getMaxDamage();
                String strPercentage = String.valueOf((int) (percentage * 100));

                drawContext.drawText(client.textRenderer, strPercentage, x, y, item.getItemBarColor(), ModConfig.getInstance().armorStatusTextShadow);
                x = x + client.textRenderer.getWidth(strPercentage);
                drawContext.drawText(client.textRenderer, "%", x, y, ModConfig.getInstance().armorStatusTextColor, ModConfig.getInstance().armorStatusTextShadow);

            } else if (displayType == DurabilityTypeEnum.Value) {
                //display durability value
                String durabilityValue = String.valueOf(item.getMaxDamage() - item.getDamage());

                drawContext.drawText(client.textRenderer, durabilityValue, x, y, item.getItemBarColor(), ModConfig.getInstance().armorStatusTextShadow);
            }

        }

    }

}