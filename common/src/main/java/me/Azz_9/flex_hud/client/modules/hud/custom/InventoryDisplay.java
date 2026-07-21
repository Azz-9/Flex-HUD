package me.Azz_9.flex_hud.client.modules.hud.custom;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigInteger;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.IntSliderEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.client.modules.hud.PlaceholderStacks;

public class InventoryDisplay extends AbstractMovableModule {

	private static final int NUM_ROWS = 3;
	private static final int NUM_COLS = 9;
	private static final int ITEM_SIZE = 18;
	private static final int PADDING = 2;

	private final ConfigInteger backgroundOpacity = new ConfigInteger(255, "flex_hud.inventory_display.config.background_opacity", 0, 255);

	public InventoryDisplay(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("inventory_display", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.inventory_display.config.enable");

		ConfigRegistry.register(getID(), "backgroundOpacity", backgroundOpacity);

		setHeight(PADDING + ITEM_SIZE * NUM_ROWS);
		setWidth(PADDING + ITEM_SIZE * NUM_COLS);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.inventory_display");
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker tickCounter) {
		if (shouldNotRender() || !CommonClass.isEditingLayout && MINECRAFT.player == null) {
			return;
		}

		List<ItemStack> inventory = new ArrayList<>();
		if (!CommonClass.isEditingLayout) {
			for (int i = 0; i < NUM_ROWS * NUM_COLS; i++) {
				if (MINECRAFT.player.getInventory().getContainerSize() > 9 + i) {
					inventory.add(MINECRAFT.player.getInventory().getItem(9 + i));
				}
			}
		} else if (MINECRAFT.level != null) {
			// we can no longer do new ItemStack outside a world since 26.1, we just display the module name above the inventory texture
			for (int i = 0; i < NUM_ROWS * NUM_COLS; i++) {
				ItemStack stack = PlaceholderStacks.of(Items.DIAMOND_BLOCK);
				stack.setCount(64);
				inventory.add(stack);
			}
		}

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		if (!backgroundOpacity.getValue().equals(0)) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, AbstractContainerScreen.INVENTORY_LOCATION, 0, 0, 6, 82, 164, 56, 256, 256, ARGB.color(backgroundOpacity.getValue(), 0xffffff));
		}

		if (MINECRAFT.level != null) {
			for (int row = 0; row < NUM_ROWS; row++) {
				for (int col = 0; col < NUM_COLS; col++) {
					ItemStack stack = inventory.get(NUM_COLS * row + col);
					int x = PADDING + col * ITEM_SIZE;
					int y = PADDING + row * ITEM_SIZE;
					graphics.renderItem(stack, x, y);
					graphics.renderItemDecorations(MINECRAFT.font, stack, x, y, stack.getCount() > 1 ? String.valueOf(stack.getCount()) : null);
				}
			}
		} else {
			graphics.drawString(
					MINECRAFT.font, getName(),
					(getWidth() - MINECRAFT.font.width(getName())) / 2,
					(getHeight() - MINECRAFT.font.lineHeight) / 2,
					0xffffffff, true
			);
		}

		matrices.popMatrix();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 175;
				} else {
					buttonWidth = 190;
				}

				super.initContent();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build()
				);
				this.addAllEntries(
						new IntSliderEntry.Builder()
								.setIntSliderWidth(80)
								.setVariable(backgroundOpacity)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeX)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeX(anchorModeX.getValue()))
								.build(),
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeY)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeY(anchorModeY.getValue()))
								.build()
				);
			}
		};
	}
}
