package me.Azz_9.flex_hud.client.gui.screens;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.CommonClass.openOptionScreenKeyBind;
import static me.Azz_9.flex_hud.Constants.MOD_ID;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.config.ConfigLoader;
import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.components.DynamicSpriteIconButton;
import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.utils.Ease;

public class OptionsScreen extends AbstractBackNavigableScreen {
	private static final ResourceLocation ENABLED_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "icon/options_screen/enabled");
	private static final ResourceLocation DISABLED_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "icon/options_screen/disabled");
	private static final ResourceLocation EDIT_LAYOUT_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "icon/options_screen/edit_layout");
	private static final ResourceLocation LOGO_WITHOUT_BG = ResourceLocation.fromNamespaceAndPath(MOD_ID, "icon/logo_without_bg");

	private static final int SQUARE_BUTTONS_SIZE = 20;
	private static final int BUTTON_GAP = 20;
	private static final int CENTRAL_BUTTON_WIDTH = 120;

	private static final int ANIMATION_DURATION = 500;

	private long initTimestamp;
	private DynamicSpriteIconButton enableModButton;
	private final List<AbstractWidget> widgets = new ArrayList<>();

	public OptionsScreen() {
		super(Component.translatable("flex_hud.options_screen"), null);
	}

	public OptionsScreen(Screen parent) {
		super(Component.translatable("flex_hud.options_screen"), parent);
	}

	@Override
	protected void init() {
		initTimestamp = System.currentTimeMillis();

		// enable mod button
		enableModButton = new DynamicSpriteIconButton(
				(width - SQUARE_BUTTONS_SIZE - CENTRAL_BUTTON_WIDTH) / 2 - BUTTON_GAP,
				(height - SQUARE_BUTTONS_SIZE) / 2,
				SQUARE_BUTTONS_SIZE, SQUARE_BUTTONS_SIZE,
				null,
				14, 14,
				(btn) -> {
					Modules.getInstance().isEnabled.setValue(!Modules.getInstance().isEnabled.getValue());
					updateEnableButton();
				});
		updateEnableButton();

		// modules button
		Button modulesButton = Button.builder(Component.translatable("flex_hud.options_screen.modules"),
						(btn) -> MINECRAFT.setScreen(new ModulesListScreen(this))
				).bounds((width - CENTRAL_BUTTON_WIDTH) / 2, (height - SQUARE_BUTTONS_SIZE) / 2, CENTRAL_BUTTON_WIDTH, SQUARE_BUTTONS_SIZE)
				.build();

		// edit layout button
		SpriteIconButton editLayoutButton = SpriteIconButton.TextAndIcon.builder(
						Component.empty(),
						(btn) -> {
							MINECRAFT.setScreen(new EditLayoutScreen(this));
							CommonClass.isEditingLayout = true;
						},
						true
				)
				.size(SQUARE_BUTTONS_SIZE, SQUARE_BUTTONS_SIZE)
				.sprite(EDIT_LAYOUT_SPRITE, 14, 14)
				.build();
		editLayoutButton.setPosition(
				(width - SQUARE_BUTTONS_SIZE + CENTRAL_BUTTON_WIDTH) / 2 + BUTTON_GAP,
				(height - SQUARE_BUTTONS_SIZE) / 2
		);

		widgets.add(enableModButton);
		widgets.add(modulesButton);
		widgets.add(editLayoutButton);
		this.addRenderableWidget(enableModButton);
		this.addRenderableWidget(modulesButton);
		this.addRenderableWidget(editLayoutButton);
	}

	private void updateEnableButton() {
		if (Modules.getInstance().isEnabled.getValue()) {
			enableModButton.setTooltip(Tooltip.create(Component.translatable("flex_hud.options_screen.disable.tooltip")));
			enableModButton.setSprite(ENABLED_SPRITE);
		} else {
			enableModButton.setTooltip(Tooltip.create(Component.translatable("flex_hud.options_screen.enable.tooltip")));
			enableModButton.setSprite(DISABLED_SPRITE);
		}
	}

	@Override
	public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		float progress = Math.min((float) (System.currentTimeMillis() - initTimestamp) / ANIMATION_DURATION, 1.0f);
		float easedProgress = Ease.outQuad(progress);

		// set the icon width and height
		int iconWidth = 64;
		int iconHeight = 64;

		// set x and y value for the icon
		int x = width / 2 - iconWidth / 2;
		// subtract 35 to make it a bit higher
		double y = height / 2.0 - iconHeight / 2.0 - 35;
		y -= 16 * easedProgress; // go up smoothly

		for (AbstractWidget widget : widgets) {
			widget.setAlpha(easedProgress);
		}

		super.render(graphics, mouseX, mouseY, delta);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate((float) x, (float) y);

		// Draw the icon
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LOGO_WITHOUT_BG, 0, 0, iconWidth, iconHeight, easedProgress);

		matrices.popMatrix();

		if (!Modules.getInstance().isEnabled.getValue()) {
			graphics.drawCenteredString(
					MINECRAFT.font,
					Component.translatable("flex_hud.options_screen.mod_is_disabled_warning").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC),
					this.width / 2, this.height / 2 + 20,
					Colors.WHITE
			);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (openOptionScreenKeyBind.matches(keyCode, scanCode)) {
			this.onClose();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		//if the keybind is on a mouse button
		if (openOptionScreenKeyBind.matchesMouse(button)) {
			this.onClose();
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void onClose() {
		ConfigLoader.saveConfig();
		super.onClose();
	}
}
