package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.mixin.toggleSprintSneak.KeyBindingAccessor;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;

public class ToggleSprint extends AbstractTextModule {

	public ToggleSprint(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
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
	public String getID() {
		return "toggle_sprint";
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender() || MINECRAFT.player == null && !Flex_hudClient.isInMoveElementScreen) {
			return;
		}

		String statusMessage = getStatusMessage(MINECRAFT.player);

		setWidth(statusMessage);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		graphics.drawString(MINECRAFT.font, statusMessage, 0, 0, getColor(), this.shadow.getValue());

		matrices.popMatrix();
	}

	private static @NotNull String getStatusMessage(@Nullable LocalPlayer player) {
		String statusMessage = "";
		if (Flex_hudClient.isInMoveElementScreen || player == null) {
			statusMessage = "Sprinting (Toggled)";
		} else {
			boolean sprintKeyPressed = MINECRAFT.options.keySprint.isDown();
			boolean sprintToggled = MINECRAFT.options.toggleSprint().get();

			if (sprintToggled) {
				if (InputConstants.isKeyDown(MINECRAFT.getWindow(), ((KeyBindingAccessor) MINECRAFT.options.keySprint).getBoundKey().getValue())) {
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
