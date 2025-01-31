package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.DisplayMode;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods.ArmorStatus;
import me.Azz_9.better_hud.client.Better_hudClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArmorStatusOverlay extends HudElement {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);

        ModConfig INSTANCE = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!INSTANCE.isEnabled || !INSTANCE.showArmorStatus || client == null || client.options.hudHidden || client.player == null) {
            return;
        }

        this.x = INSTANCE.armorStatusHudX;
        this.y = INSTANCE.armorStatusHudY;

        PlayerEntity p = client.player;

        MatrixStack matrices = drawContext.getMatrices();

        int hudX = this.x;
        int hudY = this.y;
        if (INSTANCE.displayModeArmorStatus == DisplayMode.Vertical) {
            hudY -= 16;
        }

        ItemStack helmet = p.getInventory().getArmorStack(3);
        ItemStack chestplate = p.getInventory().getArmorStack(2);
        ItemStack leggings = p.getInventory().getArmorStack(1);
        ItemStack boots = p.getInventory().getArmorStack(0);
        ItemStack heldItem = p.getInventory().getMainHandStack();

        if (Better_hudClient.isEditing) {
            helmet = new ItemStack(Items.DIAMOND_HELMET);
            chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
            leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
            boots = new ItemStack(Items.DIAMOND_BOOTS);
            heldItem = new ItemStack(Items.DIAMOND_SWORD);

            this.height = 0;
            this.width = 41;
            if (INSTANCE.showDurability == ArmorStatus.DurabilityType.No) {
                this.width = 16;
            }
            if (INSTANCE.displayModeArmorStatus == DisplayMode.Horizontal) {
                this.height = 16;
            }
        }


        if (INSTANCE.showHelmet) {
            if (INSTANCE.displayModeArmorStatus == DisplayMode.Vertical) {
                hudY += 16;
                this.height += 16;
            }
            renderItemStack(helmet, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
            if (INSTANCE.displayModeArmorStatus == DisplayMode.Horizontal) {
                if (INSTANCE.showDurability == ArmorStatus.DurabilityType.Percentage) {
                    hudX += client.textRenderer.getWidth((int) (((float) (helmet.getMaxDamage() - helmet.getDamage()) / helmet.getMaxDamage()) * 100) + "%");
                } else if (INSTANCE.showDurability == ArmorStatus.DurabilityType.Value) {
                    hudX += client.textRenderer.getWidth(String.valueOf(helmet.getMaxDamage() - helmet.getDamage()));
                } else {
                    renderItemStack(helmet, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                }
            }
        }

        if (INSTANCE.showChestplate) {
            if (INSTANCE.displayModeArmorStatus == DisplayMode.Vertical) {
                this.height += 16;
                hudY += 16;
                renderItemStack(chestplate, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
            } else {
                hudX += 18;
                if (INSTANCE.showDurability == ArmorStatus.DurabilityType.Percentage) {
                    renderItemStack(chestplate, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                    hudX += client.textRenderer.getWidth((int) (((float) (chestplate.getMaxDamage() - chestplate.getDamage()) / chestplate.getMaxDamage()) * 100) + "%");
                } else if (INSTANCE.showDurability == ArmorStatus.DurabilityType.Value) {
                    renderItemStack(chestplate, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                    hudX += client.textRenderer.getWidth(String.valueOf(chestplate.getMaxDamage() - chestplate.getDamage()));
                } else {
                    renderItemStack(chestplate, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                }
                this.width = hudX - this.x + 17;
            }


        }
        if (INSTANCE.showLeggings) {
            if (INSTANCE.displayModeArmorStatus == DisplayMode.Vertical) {
                this.height += 16;
                hudY += 16;
                renderItemStack(leggings, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
            } else {
                hudX += 18;
                if (INSTANCE.showDurability == ArmorStatus.DurabilityType.Percentage) {
                    renderItemStack(leggings, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                    hudX += client.textRenderer.getWidth((int) (((float) (leggings.getMaxDamage() - leggings.getDamage()) / leggings.getMaxDamage()) * 100) + "%");
                } else if (INSTANCE.showDurability == ArmorStatus.DurabilityType.Value) {
                    renderItemStack(leggings, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                    hudX += client.textRenderer.getWidth(String.valueOf(leggings.getMaxDamage() - leggings.getDamage()));
                } else {
                    renderItemStack(leggings, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                }
                this.width = hudX - this.x + 17;
            }

        }
        if (INSTANCE.showBoots) {
            if (INSTANCE.displayModeArmorStatus == DisplayMode.Vertical) {
                this.height += 16;
                hudY += 16;
                renderItemStack(boots, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
            } else {
                hudX += 18;
                if (INSTANCE.showDurability == ArmorStatus.DurabilityType.Percentage) {
                    renderItemStack(boots, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                    hudX += client.textRenderer.getWidth((int) (((float) (boots.getMaxDamage() - boots.getDamage()) / boots.getMaxDamage()) * 100) + "%");
                } else if (INSTANCE.showDurability == ArmorStatus.DurabilityType.Value) {
                    renderItemStack(boots, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                    hudX += client.textRenderer.getWidth(String.valueOf(boots.getMaxDamage() - boots.getDamage()));
                } else {
                    renderItemStack(boots, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                }
                this.width = hudX - this.x + 17;
            }

        }
        if (INSTANCE.showHeldItem) {
            if (INSTANCE.displayModeArmorStatus == DisplayMode.Vertical) {
                this.height += 16;
                hudY += 16;
                renderItemStack(heldItem, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
            } else {
                hudX += 18;
                if (INSTANCE.showDurability == ArmorStatus.DurabilityType.Percentage) {
                    renderItemStack(heldItem, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                    hudX += client.textRenderer.getWidth((int) (((float) (heldItem.getMaxDamage() - heldItem.getDamage()) / heldItem.getMaxDamage()) * 100) + "%");
                } else if (INSTANCE.showDurability == ArmorStatus.DurabilityType.Value) {
                    renderItemStack(heldItem, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                    hudX += client.textRenderer.getWidth(String.valueOf(heldItem.getMaxDamage() - heldItem.getDamage()));
                } else {
                    renderItemStack(heldItem, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                }
                this.width = hudX - this.x + 17;
            }

        }
        if (INSTANCE.showArrowsWhenBowInHand && (heldItem.getItem() == Items.BOW || heldItem.getItem() == Items.CROSSBOW)) {
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

            if (INSTANCE.displayModeArmorStatus == DisplayMode.Vertical) {
                hudY += 16;

                ItemStack arrowStack = Items.ARROW.getDefaultStack();
                renderItemStack(arrowStack, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                if (arrowCount == 0) {
                    arrowStack.setCount(1);
                    renderItemStack(arrowStack, hudX, hudY, ArmorStatus.DurabilityType.No, matrices, drawContext, client, false);
                    drawContext.drawText(client.textRenderer, "0", hudX + 17, hudY + 4, INSTANCE.armorStatusTextColor, INSTANCE.armorStatusTextShadow);
                }
                if (spectralArrowCount != 0) {
                    hudY += 16;
                    ItemStack spectralArrowStack = Items.SPECTRAL_ARROW.getDefaultStack();
                    renderItemStack(spectralArrowStack, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                }
                if (tippedArrowCount != 0) {
                    hudY += 16;
                    ItemStack tippedArrowStack = Items.TIPPED_ARROW.getDefaultStack();
                    renderItemStack(tippedArrowStack, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                }
            } else {
                hudX += 18;
                ItemStack arrowStack = Items.ARROW.getDefaultStack();
                renderItemStack(arrowStack, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                if (arrowCount == 0) {
                    arrowStack.setCount(1);
                    renderItemStack(arrowStack, hudX, hudY, ArmorStatus.DurabilityType.No, matrices, drawContext, client, false);
                    drawContext.drawText(client.textRenderer, "0", hudX + 17, hudY + 4, INSTANCE.armorStatusTextColor, INSTANCE.armorStatusTextShadow);
                }
                hudX += client.textRenderer.getWidth(String.valueOf(arrowCount));
                if (spectralArrowCount != 0) {
                    hudX += 18;
                    ItemStack spectralArrowStack = Items.SPECTRAL_ARROW.getDefaultStack();
                    renderItemStack(spectralArrowStack, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                    hudX += client.textRenderer.getWidth(String.valueOf(spectralArrowCount));
                }
                if (tippedArrowCount != 0) {
                    hudX += 18;
                    ItemStack tippedArrowStack = Items.TIPPED_ARROW.getDefaultStack();
                    renderItemStack(tippedArrowStack, hudX, hudY, INSTANCE.showDurability, matrices, drawContext, client, true);
                }
            }
        }

    }

    private void renderItemStack(ItemStack item, int x, int y, ArmorStatus.DurabilityType displayType, MatrixStack matrices, DrawContext drawContext, MinecraftClient client, boolean displayCount) {
        if (item.isEmpty()) {
            return;
        }

        matrices.push();
        matrices.translate(x, y, 0.0);

        drawContext.drawItem(client.player, item, 0, 0, 1);

        matrices.pop();
        if (displayCount) {
            displayDurabilityOrCount(item, displayType, x, y, drawContext, client);
        }
    }

    private void displayDurabilityOrCount(ItemStack item, ArmorStatus.DurabilityType displayType, int x, int y, DrawContext drawContext, MinecraftClient client) {
        ModConfig INSTANCE = ModConfig.getInstance();

        x += 17; // place the text beside the item in the hud
        y += 4; // lower the text to make it align with the item
        if (!item.isDamageable()){

            String itemCount = String.valueOf(getStackCount(item, client.player));
            drawContext.drawText(client.textRenderer, itemCount, x, y, INSTANCE.armorStatusTextColor, INSTANCE.armorStatusTextShadow);

        } else {
            if (displayType == ArmorStatus.DurabilityType.Percentage) {
                //display durability in percentage
                float percentage = (float) (item.getMaxDamage() - item.getDamage()) / item.getMaxDamage();
                String strPercentage = String.valueOf((int) (percentage * 100));

                drawContext.drawText(client.textRenderer, strPercentage, x, y, item.getItemBarColor(), INSTANCE.armorStatusTextShadow);
                x += client.textRenderer.getWidth(strPercentage);
                drawContext.drawText(client.textRenderer, "%", x, y, INSTANCE.armorStatusTextColor, INSTANCE.armorStatusTextShadow);

            } else if (displayType == ArmorStatus.DurabilityType.Value) {
                //display durability value
                String durabilityValue = String.valueOf(item.getMaxDamage() - item.getDamage());

                drawContext.drawText(client.textRenderer, durabilityValue, x, y, item.getItemBarColor(), INSTANCE.armorStatusTextShadow);
            }

        }

    }

    private int getStackCount(ItemStack stack, PlayerEntity player) {
        int itemCount = 0;

        for(int i = 0; i < player.getInventory().size(); ++i) {
            ItemStack itemStack = player.getInventory().getStack(i);
            if (itemStack.getItem().equals(stack.getItem())) {
                itemCount += itemStack.getCount();
            }
        }

        return itemCount;
    }

    @Override
    public void setPos(int x, int y) {
        ModConfig INSTANCE = ModConfig.getInstance();
        INSTANCE.armorStatusHudX = x;
        INSTANCE.armorStatusHudY = y;
    }

    @Override
    public boolean isEnabled() {
        ModConfig INSTANCE = ModConfig.getInstance();
        return INSTANCE.showArmorStatus && (INSTANCE.showHelmet || INSTANCE.showChestplate || INSTANCE.showLeggings || INSTANCE.showBoots || INSTANCE.showHeldItem || INSTANCE.showArrowsWhenBowInHand);
    }
}
//TODO faire une liste les flèches tipped pour les afficher avec le bon effet
//TODO enhance this shity code