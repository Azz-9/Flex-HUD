package me.Azz_9.better_hud.client.configurableMods.mods.hud.renderCallbacks;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableMods.mods.Translatable;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.DisplayMode;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

public class ArmorStatus extends AbstractHudElement {
	private boolean showHelmet = true, showChestplate = true, showLeggings = true, showBoots = true;
	private boolean showHeldItem = true, showOffHandItem = true;
	private boolean showArrowsWhenBowInHand = true;
	private boolean separateArrowTypes = false;
	private DurabilityType durabilityType = DurabilityType.PERCENTAGE;
	private DisplayMode displayMode = DisplayMode.VERTICAL;

	public ArmorStatus(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		super.render(context, tickCounter);

		MinecraftClient client = MinecraftClient.getInstance();
		if (!JsonConfigHelper.getInstance().isEnabled || !this.enabled || client == null || client.options.hudHidden || client.player == null) {
			return;
		}

		PlayerEntity player = client.player;

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh));
		matrices.scale(this.scale, this.scale);

		boolean[] booleans = new boolean[]{
				showHelmet,
				showChestplate,
				showLeggings,
				showBoots,
				showOffHandItem,
				showHeldItem

		};
		ItemStack[] items;
		if (!Better_hudClient.isInMoveElementScreen) {
			items = new ItemStack[]{
					player.getInventory().getStack(39),
					player.getInventory().getStack(38),
					player.getInventory().getStack(37),
					player.getInventory().getStack(36),
					player.getOffHandStack(),
					player.getMainHandStack()
			};
		} else {
			items = new ItemStack[]{
					new ItemStack(Items.DIAMOND_HELMET),
					new ItemStack(Items.DIAMOND_CHESTPLATE),
					new ItemStack(Items.DIAMOND_LEGGINGS),
					new ItemStack(Items.DIAMOND_BOOTS),
					new ItemStack(Items.SHIELD),
					new ItemStack(Items.DIAMOND_SWORD)
			};
		}

		int hudX = 0;
		int hudY = 0;

		int horizontalGap = 4;
		int verticalGap = 1;

		for (int i = 0; i < booleans.length; i++) {
			if (booleans[i]) {
				ItemStack stack = items[i];

				if (!stack.isEmpty()) {
					int drawingWidth = drawItemStack(context, stack, hudX, hudY);

					if (this.displayMode == DisplayMode.VERTICAL) {
						hudY += 16 + verticalGap;
					} else {
						hudX += drawingWidth + horizontalGap;
					}

					if ((i == 4 || i == 5) && this.showArrowsWhenBowInHand && (stack.getItem() == Items.BOW || stack.getItem() == Items.CROSSBOW)) {
						drawArrowsStacks(context, hudX, hudY);
					}
				}
			}
		}

		matrices.popMatrix();
	}

	//return width
	private int drawItemStack(DrawContext context, ItemStack stack, int x, int y) {
		context.drawItem(stack, x, y);
		int drawingWidth = 16;

		if (stack.isDamageable()) {
			switch (this.durabilityType) {
				case PERCENTAGE -> {
					String text = Math.round(getDurabilityPercentage(stack)) + "%";
					context.drawText(MinecraftClient.getInstance().textRenderer, text, x + 17, y + 4, stack.getItemBarColor() | 0xff000000, shadow);
					drawingWidth += MinecraftClient.getInstance().textRenderer.getWidth(text);
				}
				case VALUE -> {
					String text = String.valueOf(getDurabilityValue(stack));
					context.drawText(MinecraftClient.getInstance().textRenderer, text, x + 17, y + 4, stack.getItemBarColor() | 0xff000000, shadow);
					drawingWidth += MinecraftClient.getInstance().textRenderer.getWidth(text);
				}
			}

		} else if (MinecraftClient.getInstance().player != null) {
			String text = String.valueOf(getStackCount(stack, MinecraftClient.getInstance().player));
			context.drawText(MinecraftClient.getInstance().textRenderer, text, x + 17, y + 4, getColor(), shadow);
			drawingWidth += MinecraftClient.getInstance().textRenderer.getWidth(text);
		}

		return drawingWidth;
	}

	private void drawArrowsStacks(DrawContext context, int x, int y) {
		if (MinecraftClient.getInstance().player != null) {
			ItemStack[] arrows;
			if (separateArrowTypes) {
				arrows = new ItemStack[]{new ItemStack(Items.ARROW), new ItemStack(Items.SPECTRAL_ARROW), new ItemStack(Items.TIPPED_ARROW)};
				for (ItemStack arrowStack : arrows) {
					context.drawItem(arrowStack, x, y);
					String text = String.valueOf(getStackCount(arrowStack, MinecraftClient.getInstance().player));
					context.drawText(MinecraftClient.getInstance().textRenderer, text, x + 17, y + 4, getColor(), shadow);
					y += 16;
				}
			} else {
				ItemStack arrowStack = new ItemStack(Items.ARROW);
				context.drawItem(arrowStack, x, y);

				int totalCount = getStackCount(arrowStack, MinecraftClient.getInstance().player);
				totalCount += getStackCount(new ItemStack(Items.SPECTRAL_ARROW), MinecraftClient.getInstance().player);
				totalCount += getStackCount(new ItemStack(Items.TIPPED_ARROW), MinecraftClient.getInstance().player);
				context.drawText(MinecraftClient.getInstance().textRenderer, String.valueOf(totalCount), x + 17, y + 4, getColor(), shadow);
			}
		}
	}

	private double getDurabilityPercentage(ItemStack stack) {
		return (double) getDurabilityValue(stack) / stack.getMaxDamage() * 100;
	}

	private int getDurabilityValue(ItemStack stack) {
		return stack.getMaxDamage() - stack.getDamage();
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

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(Text.translatable("better_hud.armor_status"), parent, parentScrollAmount) {
			@Override
			protected void init() {
				super.buttonWidth = 230;

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(enabled)
								.setDefaultValue(true)
								.setOnToggle(toggled -> enabled = toggled)
								.setText(Text.translatable("better_hud.armor_status.config.enable"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(shadow)
								.setDefaultValue(true)
								.setOnToggle(toggled -> shadow = toggled)
								.setText(Text.translatable("better_hud.global.config.text_shadow"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(chromaColor)
								.setDefaultValue(false)
								.setOnToggle(toggled -> chromaColor = toggled)
								.setText(Text.translatable("better_hud.global.config.chroma_text_color"))
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setColor(color)
								.setDefaultColor(0xffffff)
								.setOnColorChange(value -> color = value)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.setText(Text.translatable("better_hud.global.config.text_color"))
								.build(),
						/*new ToggleButtonEntry.Builder() TODO faire en sorte de pouvoir afficher le fond
								.setToggleButtonWidth(buttonWidth)
								.setToggled(drawBackground)
								.setDefaultValue(false)
								.setOnToggle(toggled -> drawBackground = toggled)
								.setText(Text.translatable("better_hud.global.config.show_background"))
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setColor(backgroundColor)
								.setDefaultColor(0x313131)
								.setOnColorChange(newColor -> backgroundColor = newColor)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.setText(Text.translatable("better_hud.global.config.background_color"))
								.build(),*/
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showHelmet)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showHelmet = toggled)
								.setText(Text.translatable("better_hud.armor_status.config.show_helmet"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(JsonConfigHelper.getInstance().armorStatus.showChestplate)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showChestplate = toggled)
								.setText(Text.translatable("better_hud.armor_status.config.show_chestplate"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showLeggings)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showLeggings = toggled)
								.setText(Text.translatable("better_hud.armor_status.config.show_leggings"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showBoots)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showBoots = toggled)
								.setText(Text.translatable("better_hud.armor_status.config.show_boots"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showHeldItem)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showHeldItem = toggled)
								.setText(Text.translatable("better_hud.armor_status.config.show_held_item"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showOffHandItem)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showOffHandItem = toggled)
								.setText(Text.translatable("better_hud.armor_status.config.show_off_hand_item"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showArrowsWhenBowInHand)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showArrowsWhenBowInHand = toggled)
								.setText(Text.translatable("better_hud.armor_status.config.show_arrows"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(separateArrowTypes)
								.setDefaultValue(false)
								.setOnToggle(toggled -> separateArrowTypes = toggled)
								.setText(Text.translatable("better_hud.armor_status.config.separate_arrow_types"))
								.build(),
						new CyclingButtonEntry.Builder<DurabilityType>()
								.setCyclingButtonWidth(80)
								.setValue(durabilityType)
								.setDefaultValue(DurabilityType.PERCENTAGE)
								.setOnValueChange(value -> durabilityType = value)
								.setText(Text.translatable("better_hud.armor_status.config.show_durability"))
								.build(),
						new CyclingButtonEntry.Builder<DisplayMode>()
								.setCyclingButtonWidth(80)
								.setValue(displayMode)
								.setDefaultValue(DisplayMode.VERTICAL)
								.setOnValueChange(value -> displayMode = value)
								.setText(Text.translatable("better_hud.armor_status.config.orientation"))
								.build()
				);
			}
		};
	}

	private enum DurabilityType implements Translatable {
		NO("better_hud.enum.durability_type.no"),
		PERCENTAGE("better_hud.enum.durability_type.percentage"),
		VALUE("better_hud.enum.durability_type.value");

		private final String translationKey;

		DurabilityType(String translationKey) {
			this.translationKey = translationKey;
		}

		public String getTranslationKey() {
			return translationKey;
		}
	}
}
