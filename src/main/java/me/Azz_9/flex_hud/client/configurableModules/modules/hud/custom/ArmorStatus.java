package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.DisplayMode;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.MultiRenderable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.RenderableItem;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.RenderableText;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.utils.SpeedTester;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix3x2fStack;

import java.util.LinkedList;
import java.util.List;

public class ArmorStatus extends AbstractTextElement {
	private final ConfigBoolean showHelmet = new ConfigBoolean(true, "flex_hud.armor_status.config.show_helmet");
	private final ConfigBoolean showChestplate = new ConfigBoolean(true, "flex_hud.armor_status.config.show_chestplate");
	private final ConfigBoolean showLeggings = new ConfigBoolean(true, "flex_hud.armor_status.config.show_leggings");
	private final ConfigBoolean showBoots = new ConfigBoolean(true, "flex_hud.armor_status.config.show_boots");
	private final ConfigBoolean showHeldItem = new ConfigBoolean(true, "flex_hud.armor_status.config.show_held_item");
	private final ConfigBoolean showOffHandItem = new ConfigBoolean(true, "flex_hud.armor_status.config.show_off_hand_item");
	private final ConfigBoolean showArrowsWhenBowInHand = new ConfigBoolean(true, "flex_hud.armor_status.config.show_arrows");
	private final ConfigBoolean separateArrowTypes = new ConfigBoolean(false, "flex_hud.armor_status.config.separate_arrow_types");
	private final ConfigBoolean showDurabilityBar = new ConfigBoolean(false, "flex_hud.armor_status.config.show_durability_bar");
	private final ConfigEnum<DurabilityType> durabilityType = new ConfigEnum<>(DurabilityType.PERCENTAGE, "flex_hud.armor_status.config.show_durability");
	private final ConfigEnum<DisplayMode> displayMode = new ConfigEnum<>(DisplayMode.VERTICAL, "flex_hud.armor_status.config.orientation");

	public ArmorStatus(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.armor_status.config.enable");
	}

	@Override
	public String getID() {
		return "armor_status";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.armor_status");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && client.player == null) {
			return;
		}

		PlayerEntity player = client.player;

		// reset height and width
		this.height = (displayMode.getValue() == DisplayMode.HORIZONTAL) ? 16 : 0;
		this.width = 0;

		boolean[] booleans = new boolean[]{
				showHelmet.getValue(),
				showChestplate.getValue(),
				showLeggings.getValue(),
				showBoots.getValue(),
				showOffHandItem.getValue(),
				showHeldItem.getValue()

		};
		ItemStack[] items;
		if (!Flex_hudClient.isInMoveElementScreen) {
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
					new ItemStack(Items.BOW)
			};
		}

		int hudX = 0;
		int hudY = 0;

		int horizontalGap = 4;
		int verticalGap = 1;

		List<MultiRenderable> multiRenderables = new LinkedList<>();

		boolean shouldDrawArrows = false;
		for (int i = 0; i < booleans.length; i++) {
			if (booleans[i]) {
				ItemStack stack = items[i];

				if (!stack.isEmpty()) {
					int drawingWidth = drawItemStack(stack, hudX, hudY, multiRenderables);

					if (this.displayMode.getValue() == DisplayMode.VERTICAL) {
						hudY += 16 + verticalGap;
						this.height = hudY - verticalGap;
						this.width = Math.max(this.width, drawingWidth);
					} else {
						hudX += drawingWidth + horizontalGap;
						this.width = hudX;
					}

					if ((i == 4 || i == 5) && this.showArrowsWhenBowInHand.getValue() && (stack.getItem() == Items.BOW || stack.getItem() == Items.CROSSBOW)) {
						shouldDrawArrows = true;
					}
				}
			}
		}

		if (shouldDrawArrows) {
			drawArrowsStacks(hudX, hudY, horizontalGap, verticalGap, multiRenderables);
		}

		if (displayMode.getValue() == DisplayMode.VERTICAL) {
			if (anchorX == AnchorPosition.END) {
				MultiRenderable.alignRight(multiRenderables, this.width);
			} else if (anchorX == AnchorPosition.CENTER) {
				MultiRenderable.alignCenter(multiRenderables, this.width / 2);
			}
		}

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1.0f);

		drawBackground(context);

		for (MultiRenderable multiRenderable : multiRenderables) {
			multiRenderable.render(context, tickCounter);
		}

		matrices.pop();
	}

	//return width
	private int drawItemStack(ItemStack stack, int x, int y, List<MultiRenderable> multiRenderables) {
		int drawingWidth = 16;

		String text;
		int color;
		if (new ItemStack(stack.getItem()).isDamageable()) {
			switch (this.durabilityType.getValue()) {
				case PERCENTAGE -> {
					text = Math.round(getDurabilityPercentage(stack)) + "%";
					color = ColorHelper.withAlpha(255, stack.getItemBarColor());
					drawingWidth += MinecraftClient.getInstance().textRenderer.getWidth(text);
				}
				case VALUE -> {
					text = String.valueOf(getDurabilityValue(stack));
					color = ColorHelper.withAlpha(255, stack.getItemBarColor());
					drawingWidth += MinecraftClient.getInstance().textRenderer.getWidth(text);
				}
				default -> {
					text = "";
					color = 0;
				}
			}

		} else {
			text = String.valueOf(getStackCount(stack, MinecraftClient.getInstance().player));
			color = getColor();
			drawingWidth += MinecraftClient.getInstance().textRenderer.getWidth(text);
		}

		if (shadow.getValue()) drawingWidth++;

		if (displayMode.getValue() == DisplayMode.VERTICAL && anchorX == AnchorPosition.END) {
			multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
					new RenderableText(x, y + 4, Text.of(text), color, shadow.getValue()),
					new RenderableItem(x + MinecraftClient.getInstance().textRenderer.getWidth(text) + 1, y, 16, stack, showDurabilityBar.getValue())
			));
		} else {
			multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
					new RenderableItem(x, y, 16, stack, showDurabilityBar.getValue()),
					new RenderableText(x + 17, y + 4, Text.of(text), color, shadow.getValue())
			));
		}

		return drawingWidth;
	}

	private void drawArrowsStacks(int x, int y, int horizontalGap, int verticalGap, List<MultiRenderable> multiRenderables) {
		ItemStack[] arrows;

		if (separateArrowTypes.getValue()) {
			arrows = new ItemStack[]{new ItemStack(Items.ARROW), new ItemStack(Items.SPECTRAL_ARROW), new ItemStack(Items.TIPPED_ARROW)};
			for (ItemStack arrowStack : arrows) {
				String text = String.valueOf(getStackCount(arrowStack, MinecraftClient.getInstance().player));

				int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(text) + (shadow.getValue() ? 1 : 0);
				int drawingWidth = 17 + textWidth;

				if (displayMode.getValue() == DisplayMode.VERTICAL && anchorX == AnchorPosition.END) {
					multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
							new RenderableText(x, y + 4, Text.of(text), getColor(), shadow.getValue()),
							new RenderableItem(x + textWidth + 1, y, 16, arrowStack, showDurabilityBar.getValue())
					));
				} else {
					multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
							new RenderableItem(x, y, 16, arrowStack, showDurabilityBar.getValue()),
							new RenderableText(x + 17, y + 4, Text.of(text), getColor(), shadow.getValue())
					));
				}

				if (displayMode.getValue() == DisplayMode.VERTICAL) {
					y += 16 + verticalGap;
					this.height = y;
					updateWidth(text, x + 17 + (shadow.getValue() ? 1 : 0));
				} else {
					x += 16 + horizontalGap + MinecraftClient.getInstance().textRenderer.getWidth(text);
					this.width = x + (shadow.getValue() ? 1 : 0);
				}
			}
		} else {
			ItemStack arrowStack = new ItemStack(Items.ARROW);

			int totalCount = getStackCount(arrowStack, MinecraftClient.getInstance().player);
			totalCount += getStackCount(new ItemStack(Items.SPECTRAL_ARROW), MinecraftClient.getInstance().player);
			totalCount += getStackCount(new ItemStack(Items.TIPPED_ARROW), MinecraftClient.getInstance().player);
			String text = String.valueOf(totalCount);

			int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(text) + (shadow.getValue() ? 1 : 0);
			int drawingWidth = 17 + textWidth;
			this.width = Math.max(this.width, drawingWidth);
			//updateWidth(text, x + 17 + (shadow.getValue() ? 1 : 0));
			this.height += 16;

			if (displayMode.getValue() == DisplayMode.VERTICAL && anchorX == AnchorPosition.END) {
				multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
						new RenderableText(x, y + 4, Text.of(text), getColor(), shadow.getValue()),
						new RenderableItem(x + textWidth + 1, y, 16, arrowStack, showDurabilityBar.getValue())
				));
			} else {
				multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
						new RenderableItem(x, y, 16, arrowStack, showDurabilityBar.getValue()),
						new RenderableText(x + 17, y + 4, Text.of(text), getColor(), shadow.getValue())
				));
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
		if (Flex_hudClient.isInMoveElementScreen || player == null) return stack.getMaxCount();

		int itemCount = 0;

		for (int i = 0; i < player.getInventory().size(); ++i) {
			ItemStack itemStack = player.getInventory().getStack(i);
			if (itemStack.getItem().equals(stack.getItem()) && itemStack.getComponents().equals(stack.getComponents())) {
				itemCount += itemStack.getCount();
			}
		}

		return itemCount;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(Text.translatable("flex_hud.armor_status"), parent) {
			@Override
			protected void init() {
				super.buttonWidth = 230;

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColor)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(color)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackground)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColor)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showHelmet)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showChestplate)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showLeggings)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showBoots)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showHeldItem)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showOffHandItem)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showArrowsWhenBowInHand)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(separateArrowTypes)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showDurabilityBar)
								.build(),
						new CyclingButtonEntry.Builder<DurabilityType>()
								.setCyclingButtonWidth(80)
								.setVariable(durabilityType)
								.build(),
						new CyclingButtonEntry.Builder<DisplayMode>()
								.setCyclingButtonWidth(80)
								.setVariable(displayMode)
								.build()
				);
			}
		};
	}

	private enum DurabilityType implements Translatable {
		NO("flex_hud.enum.durability_type.no"),
		PERCENTAGE("flex_hud.enum.durability_type.percentage"),
		VALUE("flex_hud.enum.durability_type.value");

		private final String translationKey;

		DurabilityType(String translationKey) {
			this.translationKey = translationKey;
		}

		public String getTranslationKey() {
			return translationKey;
		}
	}
}
