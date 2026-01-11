package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;

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
			pitch = (player.getPitch() % 360 + 360) % 360;
			pitchStr = String.format(format, -player.getPitch());
		}

		float degreesScale = 0.75f;

		String markerText = "▶";

		setWidth((int) ((showDegrees.getValue()
				? client.textRenderer.getWidth(pitchStr) * degreesScale + 2
				: 0) +
				(showMarker.getValue()
						? (client.textRenderer.getWidth(markerText) / 2.0 + 5)
						: 0) + 34));


		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1.0f);

		drawBackground(context);

		context.enableScissor(0, 0, getWidth(), getHeight());

		float hudX = 0;
		if (showDegrees.getValue()) {
			matrices.push();
			matrices.translate(hudX, (getHeight() - client.textRenderer.fontHeight) / 2.0f, 0);
			matrices.scale(degreesScale, degreesScale, 1.0f);
			context.drawText(client.textRenderer, pitchStr, 0, 0, getColor(), shadow.getValue());
			matrices.pop();

			hudX += client.textRenderer.getWidth(pitchStr) * degreesScale + 2;
		}

		if (this.showMarker.getValue()) {
			matrices.push();
			matrices.translate(hudX, (getHeight() - client.textRenderer.fontHeight) / 2.0f, 0);
			matrices.scale(0.5f, 1.0f, 1.0f);
			context.drawText(client.textRenderer, markerText, 0, 0, getColor(), this.shadow.getValue());
			matrices.pop();

			hudX += client.textRenderer.getWidth(markerText) / 2.0f + 5;
		}

		drawIntermediatePoint(context, matrices, -165, pitch, hudX);
		drawIntermediatePoint(context, matrices, -150, pitch, hudX);
		for (int angle = -135; angle <= 135; angle += 45) {
			drawPitchPoint(context, matrices, angle, pitch, hudX);
			drawIntermediatePoint(context, matrices, angle + 15, pitch, hudX);
			drawIntermediatePoint(context, matrices, angle + 30, pitch, hudX);
		}

		context.disableScissor();

		matrices.pop();
	}

	@Override
	public boolean shouldNotRender() {
		PlayerEntity player = MinecraftClient.getInstance().player;
		return super.shouldNotRender() || player != null && displayWhenElytraIsEquipped.getValue() && !player.getInventory().getStack(38).isOf(Items.ELYTRA);
	}

	private void drawPitchPoint(DrawContext context, MatrixStack matrices, int angle, float pitch, float x) {
		MinecraftClient client = MinecraftClient.getInstance();
		String label = "|";
		String angleStr = String.valueOf(angle);
		angle = -angle;

		float angleDifference = (angle - pitch + 540) % 360 - 180;

		float scaleFactor = 1.25f;
		float angleScale = 0.75f;

		if (Math.abs(angleDifference) <= 120) {
			// Calculer la position Y de chaque point cardinal en fonction de l'angle
			float positionY = ((getHeight() / 2.0f) + (angleDifference * (getHeight() / 180.0f)));
			float pointWidth = client.textRenderer.getWidth(label) * scaleFactor;
			float angleHeight = client.textRenderer.fontHeight * angleScale;

			matrices.push();
			matrices.translate(x + 14, positionY - angleHeight / 2.0f, 0);
			matrices.scale(angleScale, angleScale, 1.0f);
			context.drawText(client.textRenderer, angleStr, 0, 0, getColorWithFadeEffect(positionY), shadow.getValue());
			matrices.pop();

			matrices.push();
			matrices.translate(x + 9, positionY - pointWidth / 2.0f, 0);
			matrices.scale(scaleFactor, scaleFactor, 1.0f);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
			context.drawText(client.textRenderer, label, 0, 0, getColorWithFadeEffect(positionY), shadow.getValue());
			matrices.pop();
		}
	}

	private void drawIntermediatePoint(DrawContext context, MatrixStack matrices, int angle, float pitch, float x) {
		MinecraftClient client = MinecraftClient.getInstance();
		String label = "|";
		angle = -angle;

		float angleDifference = (angle - pitch + 540) % 360 - 180;

		if (Math.abs(angleDifference) <= 120) {
			float scaleFactor = 0.75f;
			// Calculer la position Y de chaque point cardinal en fonction de l'angle
			float positionY = ((getHeight() / 2.0f) + (angleDifference * (getHeight() / 180.0f)));
			float pointWidth = client.textRenderer.getWidth(label) * scaleFactor;

			matrices.push();
			matrices.translate(x + 5.6f, positionY - pointWidth / 2.0f, 0);
			matrices.scale(scaleFactor, scaleFactor, 1.0f);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
			context.drawText(client.textRenderer, label, 0, 0, getColorWithFadeEffect(positionY), shadow.getValue());
			matrices.pop();
		}
	}

	private int getColorWithFadeEffect(float CenterYOfDrawing) {
		return ColorHelper.withAlpha(getAlpha(CenterYOfDrawing), getColor());
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
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
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
