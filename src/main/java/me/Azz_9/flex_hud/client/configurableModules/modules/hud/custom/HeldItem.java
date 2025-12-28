package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.utils.ItemUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;

public class HeldItem extends AbstractTextElement {

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
	public Text getName() {
		return Text.translatable("flex_hud.held_item");
	}

	@Override
	public String getID() {
		return "held_item";
	}


	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender()) {
			return;
		}

		int gap = 2;

		String label = "";
		ItemStack stack;
		int textColor;
		if (Flex_hudClient.isInMoveElementScreen || client.player == null) {
			// placeholder
			label = "64/256";
			stack = new ItemStack(Items.DIAMOND_BLOCK);
			textColor = getColor();

		} else {
			stack = client.player.getMainHandStack();

			if (stack == null || stack.isEmpty() || stack.isOf(Items.AIR)) {
				return;
			}

			if (stack.isDamageable()) {
				if (durabilityType.getValue() == ArmorStatus.DurabilityType.PERCENTAGE) {
					label = Math.round(ItemUtils.getDurabilityPercentage(stack)) + "%";
				} else if (durabilityType.getValue() == ArmorStatus.DurabilityType.VALUE) {
					label = ItemUtils.getDurabilityValue(stack) + "/" + stack.getMaxDamage();
				}
				textColor = ColorHelper.withAlpha(255, stack.getItemBarColor());
			} else {
				label = stack.getCount() + "/" + ItemUtils.getStackCount(stack, client.player.getInventory());
				textColor = getColor();
			}
		}

		// si on update pas la width ici ça fait bug le MovableWidget (il n'a pas la bonne width)
		if (!label.isEmpty()) {
			setWidth(label, ITEM_SIZE + gap);
		} else {
			setWidth(ITEM_SIZE);
		}

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1.0f);

		drawBackground(context);

		if (getAnchorX() == AnchorPosition.END) {
			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
			context.drawText(textRenderer, label, 0, 4, getColor(), this.shadow.getValue());
			context.drawItem(stack, textRenderer.getWidth(label) + gap, 0);
		} else {
			context.drawItem(stack, 0, 0);
			context.drawText(MinecraftClient.getInstance().textRenderer, label, ITEM_SIZE + gap, 4, textColor, this.shadow.getValue());
		}

		matrices.pop();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
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
