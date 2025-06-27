package me.Azz_9.better_hud.client.screens;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.screens.modulesList.ModulesListScreen;
import me.Azz_9.better_hud.client.screens.moveModulesScreen.MoveModulesScreen;
import me.Azz_9.better_hud.client.screens.widgets.buttons.TexturedButtonWidget;
import me.Azz_9.better_hud.client.utils.EaseUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;
import static me.Azz_9.better_hud.client.Better_hudClient.openOptionScreenKeyBind;

public class OptionsScreen extends AbstractBackNavigableScreen {
	private long initTimestamp;

	public OptionsScreen() {
		super(Text.translatable("better_hud.options_screen"), null);
	}

	public OptionsScreen(Screen parent) {
		super(Text.translatable("better_hud.options_screen"), parent);
	}

	@Override
	protected void init() {
		initTimestamp = System.currentTimeMillis();

		ButtonWidget modsButton = ButtonWidget.builder(Text.translatable("better_hud.options_screen.modules"),
						(btn) -> MinecraftClient.getInstance().setScreen(new ModulesListScreen(this))
				).dimensions(width / 2 - 60, height / 2 - 10, 120, 20)
				.build();
		this.addDrawableChild(modsButton);

		if (MinecraftClient.getInstance().world != null) {
			TexturedButtonWidget moveButton = new TexturedButtonWidget(width / 2 - 10 + 80, height / 2 - 10, 20, 20,
					new ButtonTextures(Identifier.of(MOD_ID, "widgets/buttons/open_move_elements_screen/unfocused.png"),
							Identifier.of(MOD_ID, "widgets/buttons/open_move_elements_screen/focused.png")),
					(btn) -> {
						MinecraftClient.getInstance().setScreen(new MoveModulesScreen(this));
						Better_hudClient.isInMoveElementScreen = true;
					});
			this.addDrawableChild(moveButton);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		final int ANIMATION_DURATION = 500;
		float progress = Math.min((float) (System.currentTimeMillis() - initTimestamp) / ANIMATION_DURATION, 1.0f);
		float easedProgress = EaseUtils.getEaseOutQuad(progress);

		final Identifier modIcon = Identifier.of(MOD_ID, "icon.png");

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
