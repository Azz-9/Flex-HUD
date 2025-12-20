package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.Alignment;
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
import me.Azz_9.flex_hud.client.utils.ItemUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;
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
	private final ConfigEnum<DurabilityType> durabilityType = new ConfigEnum<>(DurabilityType.class, DurabilityType.PERCENTAGE, "flex_hud.armor_status.config.show_durability");
	private final ConfigEnum<DisplayMode> displayMode = new ConfigEnum<>(DisplayMode.class, DisplayMode.VERTICAL, "flex_hud.armor_status.config.orientation");
	private final ConfigEnum<Alignment> alignment = new ConfigEnum<>(Alignment.class, Alignment.AUTO, "flex_hud.armor_status.config.alignment");

	public ArmorStatus(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.armor_status.config.enable");

		ConfigRegistry.register(getID(), "showHelmet", showHelmet);
		ConfigRegistry.register(getID(), "showChestplate", showChestplate);
		ConfigRegistry.register(getID(), "showLeggings", showLeggings);
		ConfigRegistry.register(getID(), "showBoots", showBoots);
		ConfigRegistry.register(getID(), "showHeldItem", showHeldItem);
		ConfigRegistry.register(getID(), "showOffHandItem", showOffHandItem);
		ConfigRegistry.register(getID(), "showArrowsWhenBowInHand", showArrowsWhenBowInHand);
		ConfigRegistry.register(getID(), "separateArrowTypes", separateArrowTypes);
		ConfigRegistry.register(getID(), "showDurabilityBar", showDurabilityBar);
		ConfigRegistry.register(getID(), "durabilityType", durabilityType);
		ConfigRegistry.register(getID(), "displayMode", displayMode);
		ConfigRegistry.register(getID(), "alignment", alignment);
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

		ItemStack[] items;
		if (!Flex_hudClient.isInMoveElementScreen) {
			PlayerEntity player = client.player;

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

					if ((i == 4 || i == 5) && this.showArrowsWhenBowInHand.getValue() && (stack.isOf(Items.BOW) || stack.isOf(Items.CROSSBOW))) {
						shouldDrawArrows = true;
					}
				}
			}
		}

		if (shouldDrawArrows) {
			drawArrowsStacks(hudX, hudY, horizontalGap, verticalGap, multiRenderables);
		}

		if (displayMode.getValue() == DisplayMode.VERTICAL) {
			if (alignment.getValue() == Alignment.RIGHT || alignment.getValue() == Alignment.AUTO && getAnchorX() == AnchorPosition.END) {
				MultiRenderable.alignRight(multiRenderables, this.width);
			} else if (alignment.getValue() == Alignment.CENTER || alignment.getValue() == Alignment.AUTO && getAnchorX() == AnchorPosition.CENTER) {
				MultiRenderable.alignCenter(multiRenderables, this.width / 2);
			}
		}

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(context);

		for (MultiRenderable multiRenderable : multiRenderables) {
			multiRenderable.render(context, tickCounter);
		}

		matrices.popMatrix();
	}

	//return width
	private int drawItemStack(ItemStack stack, int x, int y, List<MultiRenderable> multiRenderables) {
		int drawingWidth = 16;

		String text;
		int color;
		// creating a new item to make "unbreakable" items display durability
		if (new ItemStack(stack.getItem()).isDamageable()) {
			switch (this.durabilityType.getValue()) {
				case PERCENTAGE -> {
					text = Math.round(ItemUtils.getDurabilityPercentage(stack)) + "%";
					color = ColorHelper.withAlpha(255, stack.getItemBarColor());
					drawingWidth += MinecraftClient.getInstance().textRenderer.getWidth(text);
				}
				case VALUE -> {
					text = String.valueOf(ItemUtils.getDurabilityValue(stack));
					color = ColorHelper.withAlpha(255, stack.getItemBarColor());
					drawingWidth += MinecraftClient.getInstance().textRenderer.getWidth(text);
				}
				default -> {
					text = "";
					color = 0;
				}
			}

		} else {
			if (Flex_hudClient.isInMoveElementScreen || MinecraftClient.getInstance().player == null) {
				text = String.valueOf(stack.getMaxCount());
			} else {
				text = String.valueOf(ItemUtils.getStackCount(stack, MinecraftClient.getInstance().player.getInventory()));
			}
			color = getColor();
			drawingWidth += MinecraftClient.getInstance().textRenderer.getWidth(text);
		}

		if (shadow.getValue() && !text.isEmpty()) drawingWidth++;

		if (displayMode.getValue() == DisplayMode.VERTICAL && (getAnchorX() == AnchorPosition.END || alignment.getValue() == Alignment.RIGHT)) {
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
		if (separateArrowTypes.getValue()) {
			Item[] arrows = new Item[]{Items.ARROW, Items.SPECTRAL_ARROW, Items.TIPPED_ARROW};
			for (Item arrow : arrows) {
				int drawingWidth = 16;
				String text;

				if (Flex_hudClient.isInMoveElementScreen || MinecraftClient.getInstance().player == null) {
					text = String.valueOf(new ItemStack(arrow).getMaxCount());
				} else {
					text = String.valueOf(ItemUtils.getItemCount(arrow, MinecraftClient.getInstance().player.getInventory()));
				}
				drawingWidth += MinecraftClient.getInstance().textRenderer.getWidth(text);

				if (shadow.getValue()) drawingWidth++;

				if (displayMode.getValue() == DisplayMode.VERTICAL && (getAnchorX() == AnchorPosition.END || alignment.getValue() == Alignment.RIGHT)) {
					multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
							new RenderableText(x, y + 4, Text.of(text), getColor(), shadow.getValue()),
							new RenderableItem(x + MinecraftClient.getInstance().textRenderer.getWidth(text) + 1, y, 16, arrow, showDurabilityBar.getValue())
					));
				} else {
					multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
							new RenderableItem(x, y, 16, arrow, showDurabilityBar.getValue()),
							new RenderableText(x + 17, y + 4, Text.of(text), getColor(), shadow.getValue())
					));
				}

				if (displayMode.getValue() == DisplayMode.VERTICAL) {
					y += 16 + verticalGap;
					this.height = y;
					this.width = Math.max(this.width, drawingWidth);
				} else {
					x += drawingWidth + horizontalGap;
					this.width = x + (shadow.getValue() ? 1 : 0);
				}
			}
		} else {
			ItemStack arrowStack = new ItemStack(Items.ARROW);

			PlayerEntity player = MinecraftClient.getInstance().player;
			int totalCount;
			if (Flex_hudClient.isInMoveElementScreen || player == null) {
				totalCount = arrowStack.getMaxCount();
			} else {
				totalCount = ItemUtils.getItemCount(Items.ARROW, player.getInventory());
				totalCount += ItemUtils.getItemCount(Items.SPECTRAL_ARROW, player.getInventory());
				totalCount += ItemUtils.getItemCount(Items.TIPPED_ARROW, player.getInventory());
			}

			String text = String.valueOf(totalCount);

			int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(text) + (shadow.getValue() ? 1 : 0);
			int drawingWidth = 17 + textWidth;
			this.width = Math.max(this.width, drawingWidth);
			this.height += 16;

			if (displayMode.getValue() == DisplayMode.VERTICAL && (getAnchorX() == AnchorPosition.END || alignment.getValue() == Alignment.RIGHT)) {
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
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(color)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackground)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showHelmet)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showChestplate)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showLeggings)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showBoots)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showHeldItem)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showOffHandItem)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showArrowsWhenBowInHand)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(separateArrowTypes)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showDurabilityBar)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new CyclingButtonEntry.Builder<DurabilityType>()
								.setCyclingButtonWidth(80)
								.setVariable(durabilityType)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new CyclingButtonEntry.Builder<DisplayMode>()
								.setCyclingButtonWidth(80)
								.setVariable(displayMode)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new CyclingButtonEntry.Builder<Alignment>()
								.setCyclingButtonWidth(80)
								.setVariable(alignment)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), DisplayMode.HORIZONTAL)
								.build()
				);
			}
		};
	}

	enum DurabilityType implements Translatable {
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
