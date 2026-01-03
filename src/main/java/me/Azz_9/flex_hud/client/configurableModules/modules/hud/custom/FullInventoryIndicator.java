package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

public class FullInventoryIndicator extends AbstractTextModule implements TickableModule {

	private final ConfigBoolean playSound = new ConfigBoolean(true, "flex_hud.full_inventory_indicator.config.play_sound");

	private boolean isInventoryFull = false;

	public FullInventoryIndicator(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
		this.enabled.setConfigTextTranslationKey("flex_hud.full_inventory_indicator.config.enable");

		this.color.setValue(0xff0000);
		this.color.setDefaultValue(0xff0000);

		ConfigRegistry.register(getID(), "playSound", playSound);
	}

	@Override
	public void init() {
		setHeight(Minecraft.getInstance().font.lineHeight);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.full_inventory_indicator");
	}

	@Override
	public String getID() {
		return "full_inventory_indicator";
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && Minecraft.getInstance().player == null) {
			return;
		}

		if (isInventoryFull || Flex_hudClient.isInMoveElementScreen) {
			Component label = Component.translatable("flex_hud.full_inventory_indicator.label");
			setWidth(label.getString());

			Matrix3x2fStack matrices = graphics.pose();
			matrices.pushMatrix();
			matrices.translate(getRoundedX(), getRoundedY());
			matrices.scale(getScale());

			drawBackground(graphics);

			graphics.drawString(Minecraft.getInstance().font, Component.translatable("flex_hud.full_inventory_indicator.label"), 0, 0, getColor(), shadow.getValue());

			matrices.popMatrix();
		}
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (Minecraft.getInstance().getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 220;
				} else {
					buttonWidth = 175;
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
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(playSound)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}

	@Override
	public void tick() {
		if (Minecraft.getInstance().player == null) {
			return;
		}

		for (int i = 0; i < 36; i++) {
			ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(i);
			if (stack.is(Items.AIR)) {
				isInventoryFull = false;
				return;
			}
		}

		if (!isInventoryFull && playSound.getValue()) {
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0f, 2.0f));
		}

		isInventoryFull = true;
	}
}
