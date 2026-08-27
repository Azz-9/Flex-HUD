package me.Azz_9.flex_hud.client.modules.hud.custom;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.hud.AbstractTextModule;

public class ToggleSprint extends AbstractTextModule {

	public ToggleSprint(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("toggle_sprint", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.toggle_sprint.config.enable");
	}

	@Override
	public void init() {
		this.setHeight(MINECRAFT.font.lineHeight);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.toggle_sprint");
	}

	@Override
	public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender() || MINECRAFT.player == null && !CommonClass.isEditingLayout) {
			return;
		}

		String statusMessage = getStatusMessage(MINECRAFT.player);

		setWidth(statusMessage);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		graphics.text(MINECRAFT.font, statusMessage, 0, 0, getColor(), this.shadow.getValue());

		matrices.popMatrix();
	}

	private static @NotNull String getStatusMessage(@Nullable LocalPlayer player) {
		String statusMessage = "";
		if (CommonClass.isEditingLayout || player == null) {
			statusMessage = "Sprinting (Toggled)";
		} else {
			boolean sprintKeyPressed = MINECRAFT.options.keySprint.isDown();
			boolean sprintToggled = MINECRAFT.options.toggleSprint().get();

			if (sprintToggled) {
				if (InputConstants.isKeyDown(MINECRAFT.options.keySprint.key.getValue())) {
					statusMessage = "Sprinting (Held)";
				} else if (sprintKeyPressed) {
					statusMessage = "Sprinting (Toggled)";
				} else if (player.isSprinting()) {
					statusMessage = "Sprinting (Vanilla)";
				}
			} else if (sprintKeyPressed) {
				statusMessage = "Sprinting (Held)";
			} else if (player.isSprinting()) {
				statusMessage = "Sprinting (Vanilla)";
			}
		}

		return statusMessage;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 180;
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
								.build()
				);
			}
		};
	}
}
