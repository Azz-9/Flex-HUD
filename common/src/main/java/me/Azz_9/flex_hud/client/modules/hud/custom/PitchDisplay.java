package me.Azz_9.flex_hud.client.modules.hud.custom;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.config.option.ConfigInteger;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.IntFieldEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.hud.AbstractTextModule;

public class PitchDisplay extends AbstractTextModule {

	private final ConfigBoolean displayWhenElytraIsEquipped = new ConfigBoolean(false, "flex_hud.pitch_display.config.display_when_elytra_equipped");
	private final ConfigBoolean showMarker = new ConfigBoolean(true, "flex_hud.pitch_display.config.show_marker");
	private final ConfigBoolean showDegrees = new ConfigBoolean(false, "flex_hud.pitch_display.config.show_degrees");
	private final ConfigInteger degreesDecimals = new ConfigInteger(0, "flex_hud.pitch_display.config.degrees_decimals", 0, 14);

	public PitchDisplay(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("pitch_display", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
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
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		LocalPlayer player = MINECRAFT.player;

		String format = "%." + degreesDecimals.getValue() + "f";
		if (shouldNotRender() || !CommonClass.isEditingLayout && player == null) {
			return;
		}


		String pitchStr;
		float pitch;
		if (CommonClass.isEditingLayout) {
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


		PoseStack matrices = graphics.pose();
		matrices.pushPose();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1);

		drawBackground(graphics);

		graphics.enableScissor(0, 0, getWidth(), getHeight());

		float hudX = 0;
		if (showDegrees.getValue()) {
			matrices.pushPose();
			matrices.translate(hudX, (getHeight() - MINECRAFT.font.lineHeight) / 2.0f, 0);
			matrices.scale(degreesScale, degreesScale, 1);
			graphics.drawString(MINECRAFT.font, pitchStr, 0, 0, getColor(), shadow.getValue());
			matrices.popPose();

			hudX += MINECRAFT.font.width(pitchStr) * degreesScale + 2;
		}

		if (this.showMarker.getValue()) {
			matrices.pushPose();
			matrices.translate(hudX, (getHeight() - MINECRAFT.font.lineHeight) / 2.0f, 0);
			matrices.scale(0.5f, 1.0f, 1);
			graphics.drawString(MINECRAFT.font, markerText, 0, 0, getColor(), this.shadow.getValue());
			matrices.popPose();

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

		matrices.popPose();
	}

	@Override
	public boolean shouldNotRender() {
		LocalPlayer player = MINECRAFT.player;
		return super.shouldNotRender() || player != null && displayWhenElytraIsEquipped.getValue() && !player.getInventory().getItem(38).is(Items.ELYTRA);
	}

	private void drawPitchPoint(GuiGraphics graphics, PoseStack matrices, int angle, float pitch, float x) {
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

			matrices.pushPose();
			matrices.translate(x + 14, positionY - angleHeight / 2.0f, 0);
			matrices.scale(angleScale, angleScale, 1);
			graphics.drawString(MINECRAFT.font, angleStr, 0, 0, getColorWithFadeEffect(positionY), shadow.getValue());
			matrices.popPose();

			matrices.pushPose();
			matrices.translate(x + 9, positionY - pointWidth / 2.0f, 0);
			matrices.scale(scaleFactor, scaleFactor, 1);
			matrices.mulPose(Axis.ZP.rotationDegrees(90.0F));
			graphics.drawString(MINECRAFT.font, label, 0, 0, getColorWithFadeEffect(positionY), shadow.getValue());
			matrices.popPose();
		}
	}

	private void drawIntermediatePoint(GuiGraphics graphics, PoseStack matrices, int angle, float pitch, float x) {
		String label = "|";
		angle = -angle;

		float angleDifference = (angle - pitch + 540) % 360 - 180;

		if (Math.abs(angleDifference) <= 120) {
			float scaleFactor = 0.75f;
			// Calculer la position Y de chaque point cardinal en fonction de l'angle
			float positionY = ((getHeight() / 2.0f) + (angleDifference * (getHeight() / 180.0f)));
			float pointWidth = MINECRAFT.font.width(label) * scaleFactor;

			matrices.pushPose();
			matrices.translate(x + 5.6f, positionY - pointWidth / 2.0f, 0);
			matrices.scale(scaleFactor, scaleFactor, 1);
			matrices.mulPose(Axis.ZP.rotationDegrees(90.0F));
			graphics.drawString(MINECRAFT.font, label, 0, 0, getColorWithFadeEffect(positionY), shadow.getValue());
			matrices.popPose();
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
			protected void initContent() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 250;
				} else {
					buttonWidth = 190;
				}

				super.initContent();

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
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeX)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeX(anchorModeX.getValue()))
								.build(),
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeY)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeY(anchorModeY.getValue()))
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
