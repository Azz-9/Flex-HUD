package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;


import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
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
	public Text getName() {
		return Text.translatable("flex_hud.inventory_display");
	}

	@Override
	public String getID() {
		return "inventory_display";
	}


	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && CLIENT.player == null) {
			return;
		}

		List<ItemStack> inventory = new ArrayList<>();
		if (!Flex_hudClient.isInMoveElementScreen) {
			for (int i = 0; i < NUM_ROWS * NUM_COLS; i++) {
				if (CLIENT.player.getInventory().size() > 9 + i) {
					inventory.add(CLIENT.player.getInventory().getStack(9 + i));
				}
			}
		} else {
			for (int i = 0; i < NUM_ROWS * NUM_COLS; i++) {
				inventory.add(new ItemStack(Items.DIAMOND_BLOCK, 64));
			}
		}

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1.0f);

		if (!backgroundOpacity.getValue().equals(0)) {
			context.drawTexture(RenderLayer::getGuiTextured, HandledScreen.BACKGROUND_TEXTURE, 0, 0, 6, 82, 164, 56, 256, 256, ColorHelper.withAlpha(backgroundOpacity.getValue(), 0xffffff));
		}

		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLS; col++) {
				ItemStack stack = inventory.get(NUM_COLS * row + col);
				int x = PADDING + col * ITEM_SIZE;
				int y = PADDING + row * ITEM_SIZE;
				context.drawItem(stack, x, y);
				context.drawStackOverlay(CLIENT.textRenderer, stack, x, y, stack.getCount() > 1 ? String.valueOf(stack.getCount()) : null);
			}
		}

		matrices.pop();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (CLIENT.getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 175;
				} else {
					buttonWidth = 190;
				}

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
