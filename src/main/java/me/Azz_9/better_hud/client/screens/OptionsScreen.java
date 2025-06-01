package me.Azz_9.better_hud.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import me.Azz_9.better_hud.client.screens.modsList.ModsListScreen;
import me.Azz_9.better_hud.client.screens.widgets.buttons.TexturedButtonWidget;
import me.Azz_9.better_hud.client.utils.EaseUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;
import static me.Azz_9.better_hud.client.Better_hudClient.openOptionScreenKeyBind;

public class OptionsScreen extends Screen {
	private long initTimestamp;

	public OptionsScreen() {
		super(Text.translatable("better_hud.options_screen"));
	}

	@Override
	protected void init() {
		initTimestamp = System.currentTimeMillis();

		ButtonWidget modsButton = ButtonWidget.builder(Text.translatable("better_hud.options_screen.mods"),
						(btn) -> MinecraftClient.getInstance().setScreen(new ModsListScreen(this))
				).dimensions(width / 2 - 60, height / 2 - 10, 120, 20)
				.build();

		TexturedButtonWidget moveButton = new TexturedButtonWidget(width / 2 - 10 + 80, height / 2 - 10, 20, 20,
				new ButtonTextures(Identifier.of(MOD_ID, "widgets/buttons/open_move_elements_screen/unfocused.png"),
						Identifier.of(MOD_ID, "widgets/buttons/open_move_elements_screen/focused.png")),
				(btn) -> {
					System.out.println("move button clicked");
					//MinecraftClient.getInstance().setScreen(new MoveElementsScreen(this));
					//Better_hudClient.isEditing = true;
				});


		this.addDrawableChild(modsButton);
		this.addDrawableChild(moveButton);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		final int ANIMATION_DURATION = 500;
		float progress = Math.min((float) (System.currentTimeMillis() - initTimestamp) / ANIMATION_DURATION, 1.0f);
		float easedProgress = EaseUtil.getEaseOutQuad(progress);

		final Identifier modIcon = Identifier.of(MOD_ID, "icon.png");

		// set the icon width and height
		int iconWidth = 64;
		int iconHeight = 64;

		// set x and y value for the icon
		int x = width / 2 - iconWidth / 2;
		// subtract 35 to make it a bit higher
		double y = height / 2.0 - iconHeight / 2.0 - 35;
		y -= 16 * easedProgress; // go up smoothly

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, easedProgress);

		super.render(context, mouseX, mouseY, delta);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x, y, 0);

		// Draw the icon
		context.drawTexture(RenderLayer::getGuiTexturedOverlay, modIcon, 0, 0, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);

		matrices.pop();

		context.drawText(textRenderer, ".", -10, 0, 0xffffff, false); // i don't know why but i need to put this in order to the texture to be rendered with the correct opacity

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Opacité à 100%
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
