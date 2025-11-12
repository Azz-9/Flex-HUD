package me.Azz_9.flex_hud.client.screens;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.screens.modulesList.ModulesListScreen;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.MoveModulesScreen;
import me.Azz_9.flex_hud.client.screens.widgets.buttons.IconButton;
import me.Azz_9.flex_hud.client.utils.EaseUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;
import static me.Azz_9.flex_hud.client.Flex_hudClient.openOptionScreenKeyBind;

public class OptionsScreen extends AbstractBackNavigableScreen {
	private long initTimestamp;
	private IconButton enableModButton;

	public OptionsScreen() {
		super(Text.translatable("flex_hud.options_screen"), null);
	}

	public OptionsScreen(Screen parent) {
		super(Text.translatable("flex_hud.options_screen"), parent);
	}

	@Override
	protected void init() {
		initTimestamp = System.currentTimeMillis();

		int squareButtonSize = 20;
		int buttonGap = 20;
		int centralButtonWidth = 120;

		enableModButton = new IconButton(
				(width - squareButtonSize - centralButtonWidth) / 2 - buttonGap,
				(height - squareButtonSize) / 2,
				squareButtonSize, squareButtonSize,
				null,
				14, 14,
				(btn) -> {
					ModulesHelper.getInstance().isEnabled.setValue(!ModulesHelper.getInstance().isEnabled.getValue());
					updateEnableButton();
				}
		);
		updateEnableButton();
		this.addDrawableChild(enableModButton);

		ButtonWidget modsButton = ButtonWidget.builder(Text.translatable("flex_hud.options_screen.modules"),
						(btn) -> MinecraftClient.getInstance().setScreen(new ModulesListScreen(this))
				).dimensions((width - centralButtonWidth) / 2, (height - squareButtonSize) / 2, centralButtonWidth, squareButtonSize)
				.build();
		this.addDrawableChild(modsButton);

		IconButton moveButton = new IconButton(
				(width - squareButtonSize + centralButtonWidth) / 2 + buttonGap,
				(height - squareButtonSize) / 2,
				squareButtonSize, squareButtonSize,
				Identifier.of(MOD_ID, "widgets/buttons/options_menu_buttons/move.png"),
				14, 14, (btn) -> {
			MinecraftClient.getInstance().setScreen(new MoveModulesScreen(this));
			Flex_hudClient.isInMoveElementScreen = true;
		});
		this.addDrawableChild(moveButton);
	}

	private void updateEnableButton() {
		if (ModulesHelper.getInstance().isEnabled.getValue()) {
			enableModButton.setTooltip(Tooltip.of(Text.translatable("flex_hud.options_screen.disable.tooltip")));
			enableModButton.setTexture(Identifier.of(MOD_ID, "widgets/buttons/options_menu_buttons/enabled.png"));
		} else {
			enableModButton.setTooltip(Tooltip.of(Text.translatable("flex_hud.options_screen.enable.tooltip")));
			enableModButton.setTexture(Identifier.of(MOD_ID, "widgets/buttons/options_menu_buttons/disabled.png"));
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		final int ANIMATION_DURATION = 500;
		float progress = Math.min((float) (System.currentTimeMillis() - initTimestamp) / ANIMATION_DURATION, 1.0f);
		float easedProgress = EaseUtils.getEaseOutQuad(progress);

		final Identifier modIcon = Identifier.of(MOD_ID, "logo-without-bg.png");

		// set the icon width and height
		int iconWidth = 64;
		int iconHeight = 64;

		// set x and y value for the icon
		int x = width / 2 - iconWidth / 2;
		// subtract 35 to make it a bit higher
		double y = height / 2.0 - iconHeight / 2.0 - 35;
		y -= 16 * easedProgress; // go up smoothly

		//TODO trouver comment remplacer ça
		//RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, easedProgress);

		super.render(context, mouseX, mouseY, delta);

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate((float) x, (float) y);

		// Draw the icon
		context.drawTexture(RenderPipelines.GUI_TEXTURED, modIcon, 0, 0, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);

		matrices.popMatrix();

		//RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Opacité à 100%

		if (!ModulesHelper.getInstance().isEnabled.getValue()) {
			context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.translatable("flex_hud.options_screen.mod_is_disabled_warning").formatted(Formatting.RED, Formatting.ITALIC), this.width / 2, this.height / 2 + 20, 0xffffffff);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (openOptionScreenKeyBind.matchesKey(keyCode, scanCode)) {
			this.close();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		//if the keybind is on a mouse button
		if (openOptionScreenKeyBind.matchesMouse(button)) {
			this.close();
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
