package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class FullInventoryIndicator extends AbstractTextElement implements TickableModule {

	private ConfigBoolean playSound = new ConfigBoolean(true, "flex_hud.full_inventory_indicator.config.play_sound");

	private boolean isInventoryFull;

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
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.full_inventory_indicator");
	}

	@Override
	public String getID() {
		return "full_inventory_indicator";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && MinecraftClient.getInstance().player == null) {
			return;
		}

		if (isInventoryFull || Flex_hudClient.isInMoveElementScreen) {
			Text label = Text.translatable("flex_hud.full_inventory_indicator.label");
			setWidth(label.getString());

			MatrixStack matrices = context.getMatrices();
			matrices.push();
			matrices.translate(getRoundedX(), getRoundedY(), 0);
			matrices.scale(getScale(), getScale(), 1.0f);

			drawBackground(context);

			context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("flex_hud.full_inventory_indicator.label"), 0, 0, getColor(), shadow.getValue());

			matrices.pop();
		}
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 220;
				} else {
					buttonWidth = 175;
				}

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
								.setVariable(playSound)
								.build()
				);
			}
		};
	}

	@Override
	public void tick() {
		if (MinecraftClient.getInstance().player == null) {
			return;
		}

		for (int i = 0; i < 36; i++) {
			ItemStack stack = MinecraftClient.getInstance().player.getInventory().getStack(i);
			if (stack.isOf(Items.AIR)) {
				isInventoryFull = false;
				return;
			}
		}

		if (!isInventoryFull && playSound.getValue()) {
			MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER.value(), 1.0f, 2.0f));
		}

		isInventoryFull = true;
	}
}
