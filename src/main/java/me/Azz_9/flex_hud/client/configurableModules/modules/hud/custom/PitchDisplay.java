package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

public class PitchDisplay extends AbstractTextModule {

	public PitchDisplay(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
		this.enabled.setConfigTextTranslationKey("flex_hud.pitch_display.config.enable");

		setHeight(210);
		setWidth(30);
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.pitch_display");
	}

	@Override
	public String getID() {
		return "pitch_display";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity player = client.player;

		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && player == null) {
			return;
		}

		float pitch;
		if (Flex_hudClient.isInMoveElementScreen) {
			pitch = 180;
		} else {
			// Calcul de la direction (pitch)
			pitch = (player.getPitch() % 360 + 360) % 360;
		}

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(context);

		for (int angle = 0; angle < 360; angle += 45) {
			drawPitchPoint(context, matrices, 0, pitch);
		}

		matrices.popMatrix();
	}

	private void drawPitchPoint(DrawContext drawContext, Matrix3x2fStack matrices, int angle, float pitch) {
		MinecraftClient client = MinecraftClient.getInstance();
		String label = "|";

		float angleDifference = (angle - pitch + 540) % 360 - 180;

		if (Math.abs(angleDifference) <= 120) {
			float scaleFactor = 1.25f;
			// Calculer la position Y de chaque point cardinal en fonction de l'angle
			float positionY = ((getWidth() / 2.0f) + (angleDifference * (getWidth() / 180.0f)));
			float pointWidth = client.textRenderer.getWidth(label) * scaleFactor;

			// Afficher le label des directions avec couleur et taille de texte ajustée
			matrices.pushMatrix();
			matrices.translate(positionY - pointWidth / 2.0f, 8);
			matrices.scale(scaleFactor, scaleFactor);
			matrices.rotate(90);
			drawContext.drawText(client.textRenderer, label, 0, 0, getColorWithFadeEffect(positionY), this.shadow.getValue());
			matrices.popMatrix();
		}
	}

	private int getColorWithFadeEffect(float CenterYOfDrawing) {
		return ColorHelper.withAlpha(getAlpha(CenterYOfDrawing), getColor());
	}

	private int getAlpha(float CenterYOfDrawing) {
		double distanceFromCenter = Math.abs(CenterYOfDrawing - getWidth() / 2.0);

		int alpha = 0xff;
		if (distanceFromCenter > getWidth() / 4.0) {
			alpha = Math.max(0xff - (int) ((distanceFromCenter - getWidth() / 4.0) / (getWidth() / 4.0) * 0xff), 0);
		}

		return alpha;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
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
								.build()
				);
			}
		};
	}
}
