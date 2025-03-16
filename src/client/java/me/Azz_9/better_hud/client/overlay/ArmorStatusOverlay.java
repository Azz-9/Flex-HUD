package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.DisplayMode;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.ArmorStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArmorStatusOverlay extends HudElement {
	public boolean showHelmet = true;
	public boolean showChestplate = true;
	public boolean showLeggings = true;
	public boolean showBoots = true;
	public boolean showHeldItem = true;
	public boolean showArrowsWhenBowInHand = true;
	public ArmorStatus.DurabilityType showDurability = ArmorStatus.DurabilityType.Percentage;
	public DisplayMode displayMode = DisplayMode.Vertical;

	public ArmorStatusOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.onHudRender(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden || CLIENT.player == null) {
			return;
		}

		PlayerEntity player = CLIENT.player;

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		int hudX = 0;
		int hudY = 0;
		if (this.displayMode == DisplayMode.Vertical) {
			hudY -= 16;
		}

		ItemStack helmet = player.getInventory().getArmorStack(3);
		ItemStack chestplate = player.getInventory().getArmorStack(2);
		ItemStack leggings = player.getInventory().getArmorStack(1);
		ItemStack boots = player.getInventory().getArmorStack(0);
		ItemStack heldItem = player.getInventory().getMainHandStack();

		if (Better_hudClient.isEditing) {
			helmet = new ItemStack(Items.DIAMOND_HELMET);
			chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
			leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
			boots = new ItemStack(Items.DIAMOND_BOOTS);
			heldItem = new ItemStack(Items.DIAMOND_SWORD);

			this.height = 0;
			this.width = 41;
			if (this.showDurability == ArmorStatus.DurabilityType.No) {
				this.width = 16;
			}
			if (this.displayMode == DisplayMode.Horizontal) {
				this.height = 16;
			}
		}


		if (this.showHelmet) {
			if (this.displayMode == DisplayMode.Vertical) {
				hudY += 16;
				this.height += 16;
			}
			renderItemStack(helmet, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
			if (this.displayMode == DisplayMode.Horizontal) {
				if (this.showDurability == ArmorStatus.DurabilityType.Percentage) {
					hudX += CLIENT.textRenderer.getWidth((int) (((float) (helmet.getMaxDamage() - helmet.getDamage()) / helmet.getMaxDamage()) * 100) + "%");
				} else if (this.showDurability == ArmorStatus.DurabilityType.Value) {
					hudX += CLIENT.textRenderer.getWidth(String.valueOf(helmet.getMaxDamage() - helmet.getDamage()));
				} else {
					renderItemStack(helmet, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
				}
			}
		}

		if (this.showChestplate) {
			if (this.displayMode == DisplayMode.Vertical) {
				this.height += 16;
				hudY += 16;
				renderItemStack(chestplate, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
			} else {
				hudX += 18;
				if (this.showDurability == ArmorStatus.DurabilityType.Percentage) {
					renderItemStack(chestplate, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
					hudX += CLIENT.textRenderer.getWidth((int) (((float) (chestplate.getMaxDamage() - chestplate.getDamage()) / chestplate.getMaxDamage()) * 100) + "%");
				} else if (this.showDurability == ArmorStatus.DurabilityType.Value) {
					renderItemStack(chestplate, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
					hudX += CLIENT.textRenderer.getWidth(String.valueOf(chestplate.getMaxDamage() - chestplate.getDamage()));
				} else {
					renderItemStack(chestplate, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
				}
				this.width = hudX - (int) this.x + 17;
			}


		}
		if (this.showLeggings) {
			if (this.displayMode == DisplayMode.Vertical) {
				this.height += 16;
				hudY += 16;
				renderItemStack(leggings, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
			} else {
				hudX += 18;
				if (this.showDurability == ArmorStatus.DurabilityType.Percentage) {
					renderItemStack(leggings, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
					hudX += CLIENT.textRenderer.getWidth((int) (((float) (leggings.getMaxDamage() - leggings.getDamage()) / leggings.getMaxDamage()) * 100) + "%");
				} else if (this.showDurability == ArmorStatus.DurabilityType.Value) {
					renderItemStack(leggings, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
					hudX += CLIENT.textRenderer.getWidth(String.valueOf(leggings.getMaxDamage() - leggings.getDamage()));
				} else {
					renderItemStack(leggings, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
				}
				this.width = hudX - (int) this.x + 17;
			}

		}
		if (this.showBoots) {
			if (this.displayMode == DisplayMode.Vertical) {
				this.height += 16;
				hudY += 16;
				renderItemStack(boots, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
			} else {
				hudX += 18;
				if (this.showDurability == ArmorStatus.DurabilityType.Percentage) {
					renderItemStack(boots, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
					hudX += CLIENT.textRenderer.getWidth((int) (((float) (boots.getMaxDamage() - boots.getDamage()) / boots.getMaxDamage()) * 100) + "%");
				} else if (this.showDurability == ArmorStatus.DurabilityType.Value) {
					renderItemStack(boots, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
					hudX += CLIENT.textRenderer.getWidth(String.valueOf(boots.getMaxDamage() - boots.getDamage()));
				} else {
					renderItemStack(boots, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
				}
				this.width = hudX - (int) this.x + 17;
			}

		}
		if (this.showHeldItem) {
			if (this.displayMode == DisplayMode.Vertical) {
				this.height += 16;
				hudY += 16;
				renderItemStack(heldItem, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
			} else {
				hudX += 18;
				if (this.showDurability == ArmorStatus.DurabilityType.Percentage) {
					renderItemStack(heldItem, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
					hudX += CLIENT.textRenderer.getWidth((int) (((float) (heldItem.getMaxDamage() - heldItem.getDamage()) / heldItem.getMaxDamage()) * 100) + "%");
				} else if (this.showDurability == ArmorStatus.DurabilityType.Value) {
					renderItemStack(heldItem, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
					hudX += CLIENT.textRenderer.getWidth(String.valueOf(heldItem.getMaxDamage() - heldItem.getDamage()));
				} else {
					renderItemStack(heldItem, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
				}
				this.width = hudX - (int) this.x + 17;
			}

		}
		if (this.showArrowsWhenBowInHand && (heldItem.getItem() == Items.BOW || heldItem.getItem() == Items.CROSSBOW)) {
			int arrowCount = 0;
			int spectralArrowCount = 0;
			int tippedArrowCount = 0;

			for (ItemStack stack : player.getInventory().main) {
				if (stack.getItem() == Items.ARROW) {
					arrowCount += stack.getCount();
				} else if (stack.getItem() == Items.SPECTRAL_ARROW) {
					spectralArrowCount += stack.getCount();
				} else if (stack.getItem() == Items.TIPPED_ARROW) {
					tippedArrowCount += stack.getCount();
				}
			}

			if (this.displayMode == DisplayMode.Vertical) {
				hudY += 16;

				ItemStack arrowStack = Items.ARROW.getDefaultStack();
				renderItemStack(arrowStack, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
				if (arrowCount == 0) {
					arrowStack.setCount(1);
					renderItemStack(arrowStack, hudX, hudY, ArmorStatus.DurabilityType.No, matrices, drawContext, CLIENT, false);
					drawContext.drawText(CLIENT.textRenderer, "0", hudX + 17, hudY + 4, this.color, this.shadow);
				}
				if (spectralArrowCount != 0) {
					hudY += 16;
					ItemStack spectralArrowStack = Items.SPECTRAL_ARROW.getDefaultStack();
					renderItemStack(spectralArrowStack, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
				}
				if (tippedArrowCount != 0) {
					hudY += 16;
					ItemStack tippedArrowStack = Items.TIPPED_ARROW.getDefaultStack();
					renderItemStack(tippedArrowStack, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
				}
			} else {
				hudX += 18;
				ItemStack arrowStack = Items.ARROW.getDefaultStack();
				renderItemStack(arrowStack, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
				if (arrowCount == 0) {
					arrowStack.setCount(1);
					renderItemStack(arrowStack, hudX, hudY, ArmorStatus.DurabilityType.No, matrices, drawContext, CLIENT, false);
					drawContext.drawText(CLIENT.textRenderer, "0", hudX + 17, hudY + 4, this.color, this.shadow);
				}
				hudX += CLIENT.textRenderer.getWidth(String.valueOf(arrowCount));
				if (spectralArrowCount != 0) {
					hudX += 18;
					ItemStack spectralArrowStack = Items.SPECTRAL_ARROW.getDefaultStack();
					renderItemStack(spectralArrowStack, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
					hudX += CLIENT.textRenderer.getWidth(String.valueOf(spectralArrowCount));
				}
				if (tippedArrowCount != 0) {
					hudX += 18;
					ItemStack tippedArrowStack = Items.TIPPED_ARROW.getDefaultStack();
					renderItemStack(tippedArrowStack, hudX, hudY, this.showDurability, matrices, drawContext, CLIENT, true);
				}
			}
		}

		matrices.pop();

	}

	private void renderItemStack(ItemStack item, int x, int y, ArmorStatus.DurabilityType displayType, MatrixStack matrices, DrawContext drawContext, MinecraftClient CLIENT, boolean displayCount) {
		if (item.isEmpty()) {
			return;
		}

		matrices.push();
		matrices.translate(x, y, 0.0);

		drawContext.drawItem(CLIENT.player, item, 0, 0, 1);

		matrices.pop();
		if (displayCount) {
			displayDurabilityOrCount(item, displayType, x, y, drawContext, CLIENT);
		}
	}

	private void displayDurabilityOrCount(ItemStack item, ArmorStatus.DurabilityType displayType, int x, int y, DrawContext drawContext, MinecraftClient CLIENT) {
		x += 17; // place the text beside the item in the hud
		y += 4; // lower the text to make it align with the item
		if (!item.isDamageable()) {

			String itemCount = String.valueOf(getStackCount(item, CLIENT.player));
			drawContext.drawText(CLIENT.textRenderer, itemCount, x, y, this.color, this.shadow);

		} else {
			if (displayType == ArmorStatus.DurabilityType.Percentage) {
				//display durability in percentage
				float percentage = (float) (item.getMaxDamage() - item.getDamage()) / item.getMaxDamage();
				String strPercentage = String.valueOf((int) (percentage * 100));

				drawContext.drawText(CLIENT.textRenderer, strPercentage, x, y, item.getItemBarColor(), this.shadow);
				x += CLIENT.textRenderer.getWidth(strPercentage);
				drawContext.drawText(CLIENT.textRenderer, "%", x, y, this.color, this.shadow);

			} else if (displayType == ArmorStatus.DurabilityType.Value) {
				//display durability value
				String durabilityValue = String.valueOf(item.getMaxDamage() - item.getDamage());

				drawContext.drawText(CLIENT.textRenderer, durabilityValue, x, y, item.getItemBarColor(), this.shadow);
			}

		}

	}

	private int getStackCount(ItemStack stack, PlayerEntity player) {
		int itemCount = 0;

		for (int i = 0; i < player.getInventory().size(); ++i) {
			ItemStack itemStack = player.getInventory().getStack(i);
			if (itemStack.getItem().equals(stack.getItem())) {
				itemCount += itemStack.getCount();
			}
		}

		return itemCount;
	}
}
//TODO faire une liste les flèches tipped pour les afficher avec le bon effet
//TODO enhance this shity code