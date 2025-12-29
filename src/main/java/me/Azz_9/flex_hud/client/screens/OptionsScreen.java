package me.Azz_9.flex_hud.client.screens;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.screens.modulesList.ModulesListScreen;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.MoveModulesScreen;
import me.Azz_9.flex_hud.client.screens.widgets.buttons.IconButton;
import me.Azz_9.flex_hud.client.utils.EaseUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;
import static me.Azz_9.flex_hud.client.Flex_hudClient.openOptionScreenKeyBind;

public class OptionsScreen extends AbstractBackNavigableScreen {
	private long initTimestamp;
	private IconButton enableModButton;

	public OptionsScreen() {
		super(Component.translatable("flex_hud.options_screen"), null);
	}

	public OptionsScreen(Screen parent) {
		super(Component.translatable("flex_hud.options_screen"), parent);
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
		this.addRenderableWidget(enableModButton);

		Button modsButton = Button.builder(Component.translatable("flex_hud.options_screen.modules"),
						(btn) -> Minecraft.getInstance().setScreen(new ModulesListScreen(this))
				).bounds((width - centralButtonWidth) / 2, (height - squareButtonSize) / 2, centralButtonWidth, squareButtonSize)
				.build();
		this.addRenderableWidget(modsButton);

		IconButton moveButton = new IconButton(
				(width - squareButtonSize + centralButtonWidth) / 2 + buttonGap,
				(height - squareButtonSize) / 2,
				squareButtonSize, squareButtonSize,
				Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/options_menu_buttons/move.png"),
				14, 14, (btn) -> {
			Minecraft.getInstance().setScreen(new MoveModulesScreen(this));
			Flex_hudClient.isInMoveElementScreen = true;
		});
		this.addRenderableWidget(moveButton);
	}

	private void updateEnableButton() {
		if (ModulesHelper.getInstance().isEnabled.getValue()) {
			enableModButton.setTooltip(Tooltip.create(Component.translatable("flex_hud.options_screen.disable.tooltip")));
			enableModButton.setTexture(Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/options_menu_buttons/enabled.png"));
		} else {
			enableModButton.setTooltip(Tooltip.create(Component.translatable("flex_hud.options_screen.enable.tooltip")));
			enableModButton.setTexture(Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/options_menu_buttons/disabled.png"));
		}
	}

	@Override
	public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		final int ANIMATION_DURATION = 500;
		float progress = Math.min((float) (System.currentTimeMillis() - initTimestamp) / ANIMATION_DURATION, 1.0f);
		float easedProgress = EaseUtils.getEaseOutQuad(progress);

		final Identifier modIcon = Identifier.fromNamespaceAndPath(MOD_ID, "logo-without-bg.png");

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

		super.render(graphics, mouseX, mouseY, delta);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate((float) x, (float) y);

		// Draw the icon
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, modIcon, 0, 0, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);

		matrices.popMatrix();

		//RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Opacité à 100%

		if (!ModulesHelper.getInstance().isEnabled.getValue()) {
			graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("flex_hud.options_screen.mod_is_disabled_warning").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC), this.width / 2, this.height / 2 + 20, 0xffffffff);
		}
	}

	@Override
	public boolean keyPressed(@NonNull KeyEvent input) {
		if (openOptionScreenKeyBind.matches(input)) {
			this.onClose();
			return true;
		}
		return super.keyPressed(input);
	}

	@Override
	public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
		//if the keybind is on a mouse button
		if (openOptionScreenKeyBind.matchesMouse(click)) {
			this.onClose();
			return true;
		}
		return super.mouseClicked(click, doubled);
	}
}
