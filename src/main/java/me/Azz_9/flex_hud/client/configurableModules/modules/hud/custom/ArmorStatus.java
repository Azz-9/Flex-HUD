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
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
	public Component getName() {
		return Component.translatable("flex_hud.armor_status");
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();
		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && minecraft.player == null) {
			return;
		}

		ItemStack[] items;
		if (!Flex_hudClient.isInMoveElementScreen) {
			LocalPlayer player = minecraft.player;

			items = new ItemStack[]{
					player.getInventory().getItem(39),
					player.getInventory().getItem(38),
					player.getInventory().getItem(37),
					player.getInventory().getItem(36),
					player.getOffhandItem(),
					player.getMainHandItem()
			};
		} else {
			items = new ItemStack[]{
					new ItemStack(net.minecraft.world.item.Items.DIAMOND_HELMET),
					new ItemStack(Items.DIAMOND_CHESTPLATE),
					new ItemStack(Items.DIAMOND_LEGGINGS),
					new ItemStack(Items.DIAMOND_BOOTS),
					new ItemStack(Items.SHIELD),
					new ItemStack(Items.BOW)
			};
		}

		// reset height and width
		setHeight((displayMode.getValue() == DisplayMode.HORIZONTAL) ? 16 : 0);
		setWidth(0);

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
						setHeight(hudY - verticalGap);
						setWidth(Math.max(getWidth(), drawingWidth));
					} else {
						hudX += drawingWidth + horizontalGap;
						setWidth(hudX);
					}

					if ((i == 4 || i == 5) && this.showArrowsWhenBowInHand.getValue() && (stack.is(Items.BOW) || stack.is(Items.CROSSBOW))) {
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
				MultiRenderable.alignRight(multiRenderables, getWidth());
			} else if (alignment.getValue() == Alignment.CENTER || alignment.getValue() == Alignment.AUTO && getAnchorX() == AnchorPosition.CENTER) {
				MultiRenderable.alignCenter(multiRenderables, getWidth() / 2);
			}
		}

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		for (MultiRenderable multiRenderable : multiRenderables) {
			multiRenderable.render(graphics, deltaTracker);
		}

		matrices.popMatrix();
	}

	//return width
	private int drawItemStack(ItemStack stack, int x, int y, List<MultiRenderable> multiRenderables) {
		int drawingWidth = 16;

		String text;
		int color;
		// creating a new item to make "unbreakable" items display durability
		if (new ItemStack(stack.getItem()).isDamageableItem()) {
			switch (this.durabilityType.getValue()) {
				case PERCENTAGE -> {
					text = Math.round(ItemUtils.getDurabilityPercentage(stack)) + "%";
					color = ARGB.color(255, stack.getBarColor());
					drawingWidth += Minecraft.getInstance().font.width(text);
				}
				case VALUE -> {
					text = String.valueOf(ItemUtils.getDurabilityValue(stack));
					color = ARGB.color(255, stack.getBarColor());
					drawingWidth += Minecraft.getInstance().font.width(text);
				}
				default -> {
					text = "";
					color = 0;
				}
			}

		} else {
			if (Flex_hudClient.isInMoveElementScreen || Minecraft.getInstance().player == null) {
				text = String.valueOf(stack.getMaxStackSize());
			} else {
				text = String.valueOf(ItemUtils.getStackCount(stack, Minecraft.getInstance().player.getInventory()));
			}
			color = getColor();
			drawingWidth += Minecraft.getInstance().font.width(text);
		}

		if (shadow.getValue() && !text.isEmpty()) drawingWidth++;

		if (displayMode.getValue() == DisplayMode.VERTICAL && (alignment.getValue() == Alignment.AUTO && getAnchorX() == AnchorPosition.END || alignment.getValue() == Alignment.RIGHT)) {
			multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
					new RenderableText(x, y + 4, Component.literal(text), color, shadow.getValue()),
					new RenderableItem(x + Minecraft.getInstance().font.width(text) + 1, y, 16, stack, showDurabilityBar.getValue())
			));
		} else {
			multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
					new RenderableItem(x, y, 16, stack, showDurabilityBar.getValue()),
					new RenderableText(x + 17, y + 4, Component.literal(text), color, shadow.getValue())
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

				if (Flex_hudClient.isInMoveElementScreen || Minecraft.getInstance().player == null) {
					text = String.valueOf(new ItemStack(arrow).getMaxStackSize());
				} else {
					text = String.valueOf(ItemUtils.getItemCount(arrow, Minecraft.getInstance().player.getInventory()));
				}
				drawingWidth += Minecraft.getInstance().font.width(text);

				if (shadow.getValue()) drawingWidth++;

				if (displayMode.getValue() == DisplayMode.VERTICAL && (alignment.getValue() == Alignment.AUTO && getAnchorX() == AnchorPosition.END || alignment.getValue() == Alignment.RIGHT)) {
					multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
							new RenderableText(x, y + 4, Component.literal(text), getColor(), shadow.getValue()),
							new RenderableItem(x + Minecraft.getInstance().font.width(text) + 1, y, 16, arrow, showDurabilityBar.getValue())
					));
				} else {
					multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
							new RenderableItem(x, y, 16, arrow, showDurabilityBar.getValue()),
							new RenderableText(x + 17, y + 4, Component.literal(text), getColor(), shadow.getValue())
					));
				}

				if (displayMode.getValue() == DisplayMode.VERTICAL) {
					y += 16 + verticalGap;
					setHeight(y);
					setWidth(Math.max(getWidth(), drawingWidth));
				} else {
					x += drawingWidth + horizontalGap;
					setWidth(x + (shadow.getValue() ? 1 : 0));
				}
			}
		} else {
			ItemStack arrowStack = new ItemStack(Items.ARROW);

			LocalPlayer player = Minecraft.getInstance().player;
			int totalCount;
			if (Flex_hudClient.isInMoveElementScreen || player == null) {
				totalCount = arrowStack.getMaxStackSize();
			} else {
				totalCount = ItemUtils.getItemCount(Items.ARROW, player.getInventory());
				totalCount += ItemUtils.getItemCount(Items.SPECTRAL_ARROW, player.getInventory());
				totalCount += ItemUtils.getItemCount(Items.TIPPED_ARROW, player.getInventory());
			}

			String text = String.valueOf(totalCount);

			int textWidth = Minecraft.getInstance().font.width(text) + (shadow.getValue() ? 1 : 0);
			int drawingWidth = 17 + textWidth;
			setWidth(Math.max(getWidth(), drawingWidth));
			setHeight(getHeight() + 16);

			if (displayMode.getValue() == DisplayMode.VERTICAL && (alignment.getValue() == Alignment.AUTO && getAnchorX() == AnchorPosition.END || alignment.getValue() == Alignment.RIGHT)) {
				multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
						new RenderableText(x, y + 4, Component.literal(text), getColor(), shadow.getValue()),
						new RenderableItem(x + textWidth + 1, y, 16, arrowStack, showDurabilityBar.getValue())
				));
			} else {
				multiRenderables.add(new MultiRenderable(x, x + drawingWidth,
						new RenderableItem(x, y, 16, arrowStack, showDurabilityBar.getValue()),
						new RenderableText(x + 17, y + 4, Component.literal(text), getColor(), shadow.getValue())
				));
			}
		}
	}


	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(Component.translatable("flex_hud.armor_status"), parent) {
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
