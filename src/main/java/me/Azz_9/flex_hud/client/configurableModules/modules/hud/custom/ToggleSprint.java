package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.mixin.toggleSprintSneak.KeyBindingAccessor;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;

public class ToggleSprint extends AbstractTextModule {

	public ToggleSprint(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.toggle_sprint.config.enable");
	}

	@Override
	public void init() {
		this.setHeight(CLIENT.textRenderer.fontHeight);
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.toggle_sprint");
	}

	@Override
	public String getID() {
		return "toggle_sprint";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender() || CLIENT.player == null && !Flex_hudClient.isInMoveElementScreen) {
			return;
		}

		String statusMessage = getStatusMessage(CLIENT.player);

		setWidth(statusMessage);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1.0f);

		drawBackground(context);

		context.drawText(CLIENT.textRenderer, statusMessage, 0, 0, getColor(), this.shadow.getValue());

		matrices.pop();
	}

	private static @NotNull String getStatusMessage(@Nullable ClientPlayerEntity player) {
		String statusMessage = "";
		if (Flex_hudClient.isInMoveElementScreen || player == null) {
			statusMessage = "Sprinting (Toggled)";
		} else {
			boolean sprintKeyPressed = CLIENT.options.sprintKey.isPressed();
			boolean sprintToggled = CLIENT.options.getSprintToggled().getValue();

			if (sprintToggled) {
				if (InputUtil.isKeyPressed(CLIENT.getWindow().getHandle(), ((KeyBindingAccessor) CLIENT.options.sprintKey).getBoundKey().getCode())) {
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
				if (CLIENT.getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 180;
				} else {
					buttonWidth = 160;
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
								.build()
				);
			}
		};
	}
}
