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
import net.minecraft.item.Items;

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
            if (modConfigInstance.displayModeArmorStatus == DisplayModeEnum.Horizontal) {
                if (modConfigInstance.showDurability == DurabilityTypeEnum.Percentage) {
                    x += client.textRenderer.getWidth((int) (((float) (helmet.getMaxDamage() - helmet.getDamage()) / helmet.getMaxDamage()) * 100) + "%");
                } else {
                    x += client.textRenderer.getWidth(String.valueOf(helmet.getMaxDamage() - helmet.getDamage()));
                }
            }
        }

        if (modConfigInstance.showChestplate) {
            if (modConfigInstance.displayModeArmorStatus == DisplayModeEnum.Vertical) {
                y += 16;
                renderItemStack(chestplate, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
            } else {
                x += 18;
                if (modConfigInstance.showDurability == DurabilityTypeEnum.Percentage) {
                    renderItemStack(chestplate, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                    x += client.textRenderer.getWidth((int) (((float) (chestplate.getMaxDamage() - chestplate.getDamage()) / chestplate.getMaxDamage()) * 100) + "%");
                } else {
                    renderItemStack(chestplate, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                    x += client.textRenderer.getWidth(String.valueOf(chestplate.getMaxDamage() - chestplate.getDamage()));
                }
            }


        }
        if (modConfigInstance.showLeggings) {
            if (modConfigInstance.displayModeArmorStatus == DisplayModeEnum.Vertical) {
                y += 16;
                renderItemStack(leggings, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
            } else {
                x += 18;
                if (modConfigInstance.showDurability == DurabilityTypeEnum.Percentage) {
                    renderItemStack(leggings, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                    x += client.textRenderer.getWidth((int) (((float) (leggings.getMaxDamage() - leggings.getDamage()) / leggings.getMaxDamage()) * 100) + "%");
                } else {
                    renderItemStack(leggings, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                    x += client.textRenderer.getWidth(String.valueOf(leggings.getMaxDamage() - leggings.getDamage()));
                }
            }

        }
        if (modConfigInstance.showBoots) {
            if (modConfigInstance.displayModeArmorStatus == DisplayModeEnum.Vertical) {
                y += 16;
                renderItemStack(boots, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
            } else {
                x += 18;
                if (modConfigInstance.showDurability == DurabilityTypeEnum.Percentage) {
                    renderItemStack(boots, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                    x += client.textRenderer.getWidth((int) (((float) (boots.getMaxDamage() - boots.getDamage()) / boots.getMaxDamage()) * 100) + "%");
                } else {
                    renderItemStack(boots, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                    x += client.textRenderer.getWidth(String.valueOf(boots.getMaxDamage() - boots.getDamage()));
                }
            }

        }
        if (modConfigInstance.showHeldItem) {
            if (modConfigInstance.displayModeArmorStatus == DisplayModeEnum.Vertical) {
                y += 16;
                renderItemStack(heldItem, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
            } else {
                x += 18;
                if (modConfigInstance.showDurability == DurabilityTypeEnum.Percentage) {
                    renderItemStack(heldItem, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                    x += client.textRenderer.getWidth((int) (((float) (heldItem.getMaxDamage() - heldItem.getDamage()) / heldItem.getMaxDamage()) * 100) + "%");
                } else {
                    renderItemStack(heldItem, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                    x += client.textRenderer.getWidth(String.valueOf(heldItem.getMaxDamage() - heldItem.getDamage()));
                }
            }

        }
        if (modConfigInstance.showArrowsWhenBowInHand && (heldItem.getItem() == Items.BOW || heldItem.getItem() == Items.CROSSBOW)) {
            int arrowCount = 0;
            int spectralArrowCount = 0;
            int tippedArrowCount = 0;

            for (ItemStack stack : p.getInventory().main) {
                if (stack.getItem() == Items.ARROW) {
                    arrowCount += stack.getCount();
                } else if (stack.getItem() == Items.SPECTRAL_ARROW) {
                    spectralArrowCount += stack.getCount();
                } else if (stack.getItem() == Items.TIPPED_ARROW) {
                    tippedArrowCount += stack.getCount();
                }
            }

            if (modConfigInstance.displayModeArmorStatus == DisplayModeEnum.Vertical) {
                y += 16;

                ItemStack arrowStack = Items.ARROW.getDefaultStack();
                arrowStack.setCount(arrowCount);
                renderItemStack(arrowStack, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                if (arrowCount == 0) {
                    arrowStack.setCount(1);
                    renderItemStack(arrowStack, x, y, DurabilityTypeEnum.No, matrices, drawContext, client);
                    drawContext.drawText(client.textRenderer, "0", x + 17, y + 4, ModConfig.getInstance().armorStatusTextColor, ModConfig.getInstance().armorStatusTextShadow);
                }
                if (spectralArrowCount != 0) {
                    y += 16;
                    ItemStack spectralArrowStack = Items.SPECTRAL_ARROW.getDefaultStack();
                    spectralArrowStack.setCount(spectralArrowCount);
                    renderItemStack(spectralArrowStack, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                }
                if (tippedArrowCount != 0) {
                    y += 16;
                    ItemStack tippedArrowStack = Items.TIPPED_ARROW.getDefaultStack();
                    tippedArrowStack.setCount(tippedArrowCount);
                    renderItemStack(tippedArrowStack, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                }
            } else {
                x += 18;
                ItemStack arrowStack = Items.ARROW.getDefaultStack();
                arrowStack.setCount(arrowCount);
                renderItemStack(arrowStack, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                if (arrowCount == 0) {
                    arrowStack.setCount(1);
                    renderItemStack(arrowStack, x, y, DurabilityTypeEnum.No, matrices, drawContext, client);
                    drawContext.drawText(client.textRenderer, "0", x + 17, y + 4, ModConfig.getInstance().armorStatusTextColor, ModConfig.getInstance().armorStatusTextShadow);
                }
                x += client.textRenderer.getWidth(String.valueOf(arrowCount));
                if (spectralArrowCount != 0) {
                    x += 18;
                    ItemStack spectralArrowStack = Items.SPECTRAL_ARROW.getDefaultStack();
                    spectralArrowStack.setCount(spectralArrowCount);
                    renderItemStack(spectralArrowStack, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
                    x += client.textRenderer.getWidth(String.valueOf(spectralArrowCount));
                }
                if (tippedArrowCount != 0) {
                    x += 18;
                    ItemStack tippedArrowStack = Items.TIPPED_ARROW.getDefaultStack();
                    tippedArrowStack.setCount(tippedArrowCount);
                    renderItemStack(tippedArrowStack, x, y, modConfigInstance.showDurability, matrices, drawContext, client);
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
                x += client.textRenderer.getWidth(strPercentage);
                drawContext.drawText(client.textRenderer, "%", x, y, ModConfig.getInstance().armorStatusTextColor, ModConfig.getInstance().armorStatusTextShadow);

            } else if (displayType == DurabilityTypeEnum.Value) {
                //display durability value
                String durabilityValue = String.valueOf(item.getMaxDamage() - item.getDamage());

                drawContext.drawText(client.textRenderer, durabilityValue, x, y, item.getItemBarColor(), ModConfig.getInstance().armorStatusTextShadow);
            }

        }

    }

}
//TODO faire une liste les flèches tipped pour les afficher avec le bon effet