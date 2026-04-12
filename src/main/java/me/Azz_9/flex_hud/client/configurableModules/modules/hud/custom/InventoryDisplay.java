package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntSliderEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;

public class InventoryDisplay extends AbstractMovableModule {

	private static final int NUM_ROWS = 3;
	private static final int NUM_COLS = 9;
	private static final int ITEM_SIZE = 18;
	private static final int PADDING = 2;

	private final ConfigInteger backgroundOpacity = new ConfigInteger(255, "flex_hud.inventory_display.config.background_opacity", 0, 255);

	public InventoryDisplay(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
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
	public String getID() {
		return "inventory_display";
	}


	@Override
	public void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && MINECRAFT.player == null) {
			return;
		}

		List<ItemStack> inventory = new ArrayList<>();
		if (!Flex_hudClient.isInMoveElementScreen) {
			for (int i = 0; i < NUM_ROWS * NUM_COLS; i++) {
				if (MINECRAFT.player.getInventory().getContainerSize() > 9 + i) {
					inventory.add(MINECRAFT.player.getInventory().getItem(9 + i));
				}
			}
		} else {
			for (int i = 0; i < NUM_ROWS * NUM_COLS; i++) {
				inventory.add(new ItemStack(Items.DIAMOND_BLOCK, 64));
			}
		}

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		if (!backgroundOpacity.getValue().equals(0)) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, AbstractContainerScreen.INVENTORY_LOCATION, 0, 0, 6, 82, 164, 56, 256, 256, ARGB.color(backgroundOpacity.getValue(), 0xffffff));
		}

		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLS; col++) {
				ItemStack stack = inventory.get(NUM_COLS * row + col);
				int x = PADDING + col * ITEM_SIZE;
				int y = PADDING + row * ITEM_SIZE;
				graphics.item(stack, x, y);
				graphics.itemDecorations(MINECRAFT.font, stack, x, y, String.valueOf(stack.getCount()));
			}
		}

		matrices.popMatrix();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				super.init();

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
								.build()
				);
			}
		};
	}
}
