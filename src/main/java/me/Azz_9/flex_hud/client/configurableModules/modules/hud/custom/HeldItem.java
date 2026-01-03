package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.utils.ItemUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

public class HeldItem extends AbstractTextModule {

	private final ConfigEnum<ArmorStatus.DurabilityType> durabilityType = new ConfigEnum<>(ArmorStatus.DurabilityType.class, ArmorStatus.DurabilityType.PERCENTAGE, "flex_hud.held_item.config.show_durability");

	private final int ITEM_SIZE = 16;

	public HeldItem(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
		this.enabled.setConfigTextTranslationKey("flex_hud.held_item.config.enable");

		ConfigRegistry.register(getID(), "durabilityType", durabilityType);
	}

	@Override
	public void init() {
		setHeight(ITEM_SIZE);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.held_item");
	}

	@Override
	public String getID() {
		return "held_item";
	}


	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();

		if (shouldNotRender()) {
			return;
		}

		int gap = 2;

		String label = "";
		ItemStack stack;
		int textColor;
		if (Flex_hudClient.isInMoveElementScreen || minecraft.player == null) {
			// placeholder
			label = "64/256";
			stack = new ItemStack(Items.DIAMOND_BLOCK);
			textColor = getColor();

		} else {
			stack = minecraft.player.getMainHandItem();

			if (stack.isEmpty() || stack.is(Items.AIR)) {
				return;
			}

			if (stack.isDamageableItem()) {
				if (durabilityType.getValue() == ArmorStatus.DurabilityType.PERCENTAGE) {
					label = Math.round(ItemUtils.getDurabilityPercentage(stack)) + "%";
				} else if (durabilityType.getValue() == ArmorStatus.DurabilityType.VALUE) {
					label = ItemUtils.getDurabilityValue(stack) + "/" + stack.getMaxDamage();
				}
				textColor = ARGB.color(255, stack.getBarColor());
			} else {
				label = stack.getCount() + "/" + ItemUtils.getStackCount(stack, minecraft.player.getInventory());
				textColor = getColor();
			}
		}

		// si on update pas la width ici ça fait bug le MovableWidget (il n'a pas la bonne width)
		if (!label.isEmpty()) {
			setWidth(label, ITEM_SIZE + gap);
		} else {
			setWidth(ITEM_SIZE);
		}

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		Font font = minecraft.font;
		if (getAnchorX() == AnchorPosition.END) {
			graphics.drawString(font, label, 0, 4, getColor(), this.shadow.getValue());
			graphics.renderItem(stack, font.width(label) + gap, 0);
		} else {
			graphics.renderItem(stack, 0, 0);
			graphics.drawString(font, label, ITEM_SIZE + gap, 4, textColor, this.shadow.getValue());
		}

		matrices.popMatrix();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (Minecraft.getInstance().getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 210;
				} else {
					buttonWidth = 170;
				}

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
						new CyclingButtonEntry.Builder<ArmorStatus.DurabilityType>()
								.setCyclingButtonWidth(80)
								.setVariable(durabilityType)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
