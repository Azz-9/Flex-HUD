package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;

public class PitchDisplay extends AbstractTextModule {

	private final ConfigBoolean displayWhenElytraIsEquipped = new ConfigBoolean(false, "flex_hud.pitch_display.config.display_when_elytra_equipped");
	private final ConfigBoolean showMarker = new ConfigBoolean(true, "flex_hud.pitch_display.config.show_marker");
	private final ConfigBoolean showDegrees = new ConfigBoolean(false, "flex_hud.pitch_display.config.show_degrees");
	private final ConfigInteger degreesDecimals = new ConfigInteger(0, "flex_hud.pitch_display.config.degrees_decimals", 0, 14);

	public PitchDisplay(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
		this.enabled.setConfigTextTranslationKey("flex_hud.pitch_display.config.enable");

		ConfigRegistry.register(getID(), "displayWhenElytraIsEquipped", displayWhenElytraIsEquipped);
		ConfigRegistry.register(getID(), "showMarker", showMarker);
		ConfigRegistry.register(getID(), "showDegrees", showDegrees);
		ConfigRegistry.register(getID(), "degreesDecimals", degreesDecimals);

		setHeight(150);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.pitch_display");
	}

	@Override
	public String getID() {
		return "pitch_display";
	}

	@Override
	public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		LocalPlayer player = MINECRAFT.player;

		String format = "%." + degreesDecimals.getValue() + "f";
		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && player == null) {
			return;
		}


		String pitchStr;
		float pitch;
		if (Flex_hudClient.isInMoveElementScreen) {
			pitch = 0;
			pitchStr = String.format(format, pitch);
		} else {
			// Calcul de la direction (pitch)
			pitch = (player.getXRot() % 360 + 360) % 360;
			pitchStr = String.format(format, -player.getXRot());
		}

		float degreesScale = 0.75f;

		String markerText = "▶";

		setWidth((int) ((showDegrees.getValue()
				? MINECRAFT.font.width(pitchStr) * degreesScale + 2
				: 0) +
				(showMarker.getValue()
						? (MINECRAFT.font.width(markerText) / 2.0 + 5)
						: 0) + 34));


		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		graphics.enableScissor(0, 0, getWidth(), getHeight());

		float hudX = 0;
		if (showDegrees.getValue()) {
			matrices.pushMatrix();
			matrices.translate(hudX, (getHeight() - MINECRAFT.font.lineHeight) / 2.0f);
			matrices.scale(degreesScale);
			graphics.text(MINECRAFT.font, pitchStr, 0, 0, getColor(), shadow.getValue());
			matrices.popMatrix();

			hudX += MINECRAFT.font.width(pitchStr) * degreesScale + 2;
		}

		if (this.showMarker.getValue()) {
			matrices.pushMatrix();
			matrices.translate(hudX, (getHeight() - MINECRAFT.font.lineHeight) / 2.0f);
			matrices.scale(0.5f, 1.0f);
			graphics.text(MINECRAFT.font, markerText, 0, 0, getColor(), this.shadow.getValue());
			matrices.popMatrix();

			hudX += MINECRAFT.font.width(markerText) / 2.0f + 5;
		}

		drawIntermediatePoint(graphics, matrices, -165, pitch, hudX);
		drawIntermediatePoint(graphics, matrices, -150, pitch, hudX);
		for (int angle = -135; angle <= 135; angle += 45) {
			drawPitchPoint(graphics, matrices, angle, pitch, hudX);
			drawIntermediatePoint(graphics, matrices, angle + 15, pitch, hudX);
			drawIntermediatePoint(graphics, matrices, angle + 30, pitch, hudX);
		}

		graphics.disableScissor();

		matrices.popMatrix();
	}

	@Override
	public boolean shouldNotRender() {
		LocalPlayer player = MINECRAFT.player;
		return super.shouldNotRender() || player != null && displayWhenElytraIsEquipped.getValue() && !player.getInventory().getItem(38).is(Items.ELYTRA);
	}

	private void drawPitchPoint(GuiGraphicsExtractor graphics, Matrix3x2fStack matrices, int angle, float pitch, float x) {
		String label = "|";
		String angleStr = String.valueOf(angle);
		angle = -angle;

		float angleDifference = (angle - pitch + 540) % 360 - 180;

		float scaleFactor = 1.25f;
		float angleScale = 0.75f;

		if (Math.abs(angleDifference) <= 120) {
			// Calculer la position Y de chaque point cardinal en fonction de l'angle
			float positionY = ((getHeight() / 2.0f) + (angleDifference * (getHeight() / 180.0f)));
			float pointWidth = MINECRAFT.font.width(label) * scaleFactor;
			float angleHeight = MINECRAFT.font.lineHeight * angleScale;

			matrices.pushMatrix();
			matrices.translate(x + 14, positionY - angleHeight / 2.0f);
			matrices.scale(angleScale, angleScale);
			graphics.text(MINECRAFT.font, angleStr, 0, 0, getColorWithFadeEffect(positionY), shadow.getValue());
			matrices.popMatrix();

			matrices.pushMatrix();
			matrices.translate(x + 9, positionY - pointWidth / 2.0f);
			matrices.scale(scaleFactor, scaleFactor);
			matrices.rotate((float) Math.toRadians(90));
			graphics.text(MINECRAFT.font, label, 0, 0, getColorWithFadeEffect(positionY), shadow.getValue());
			matrices.popMatrix();
		}
	}

	private void drawIntermediatePoint(GuiGraphicsExtractor graphics, Matrix3x2fStack matrices, int angle, float pitch, float x) {
		String label = "|";
		angle = -angle;

		float angleDifference = (angle - pitch + 540) % 360 - 180;

		if (Math.abs(angleDifference) <= 120) {
			float scaleFactor = 0.75f;
			// Calculer la position Y de chaque point cardinal en fonction de l'angle
			float positionY = ((getHeight() / 2.0f) + (angleDifference * (getHeight() / 180.0f)));
			float pointWidth = MINECRAFT.font.width(label) * scaleFactor;

			matrices.pushMatrix();
			matrices.translate(x + 5.6f, positionY - pointWidth / 2.0f);
			matrices.scale(scaleFactor, scaleFactor);
			matrices.rotate((float) Math.toRadians(90));
			graphics.text(MINECRAFT.font, label, 0, 0, getColorWithFadeEffect(positionY), shadow.getValue());
			matrices.popMatrix();
		}
	}

	private int getColorWithFadeEffect(float CenterYOfDrawing) {
		return ARGB.color(getAlpha(CenterYOfDrawing), getColor());
	}

	private int getAlpha(float CenterYOfDrawing) {
		double distanceFromCenter = Math.abs(CenterYOfDrawing - getHeight() / 2.0);

		int alpha = 0xff;
		if (distanceFromCenter > getHeight() / 4.0) {
			alpha = Math.max(0xff - (int) ((distanceFromCenter - getHeight() / 4.0) / (getHeight() / 4.0) * 0xff), 0);
		}

		return alpha;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {

			@Override
			protected void init() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 250;
				} else {
					buttonWidth = 190;
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
								.setVariable(displayWhenElytraIsEquipped)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showMarker)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showDegrees)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new IntFieldEntry.Builder()
								.setIntFieldWidth(20)
								.setVariable(degreesDecimals)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), false)
								.build()
				);
			}
		};
	}
}
