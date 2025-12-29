package me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen;

import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor.CrosshairEditor;
import me.Azz_9.flex_hud.client.screens.widgets.HelpWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

public abstract class AbstractCrosshairConfigScreen extends AbstractConfigurationScreen {
	private CrosshairEditor crosshairEditor = null;
	private HelpWidget helpWidget;

	public AbstractCrosshairConfigScreen(Component title, Screen parent) {
		super(title, parent);
	}

	@Override
	protected void init() {
		super.init();

		int helpWidgetPadding = 4;
		int helpWidgetSize = 20;
		helpWidget = new HelpWidget(helpWidgetPadding, this.height - helpWidgetPadding - helpWidgetSize, helpWidgetSize, helpWidgetSize, new Component[]{
				Component.translatable("flex_hud.crosshair_editor.help_widget.line1"),
				Component.translatable("flex_hud.crosshair_editor.help_widget.line2"),
				Component.translatable("flex_hud.crosshair_editor.help_widget.line3"),
				Component.translatable("flex_hud.crosshair_editor.help_widget.line4"),
		});
	}

	@Override
	public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		super.render(graphics, mouseX, mouseY, deltaTicks);

		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			crosshairEditor.render(graphics, mouseX, mouseY, deltaTicks);
			helpWidget.render(graphics, mouseX, mouseY, deltaTicks);
		}
	}

	public CrosshairEditor getCrosshairEditor() {
		return crosshairEditor;
	}

	@Override
	public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (crosshairEditor.mouseClicked(click, doubled) || helpWidget.mouseClicked(click, doubled)) {
				return true;
			} else {
				closeEditor();
				return false;
			}
		}
		return super.mouseClicked(click, doubled);
	}

	@Override
	public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (crosshairEditor.mouseDragged(click, offsetX, offsetY)) {
				return true;
			}
		}
		return super.mouseDragged(click, offsetX, offsetY);
	}

	@Override
	public boolean mouseReleased(@NonNull MouseButtonEvent click) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (crosshairEditor.mouseReleased(click)) {
				return true;
			}
		}
		return super.mouseReleased(click);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (crosshairEditor.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
				return true;
			}
		}
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	public boolean keyPressed(@NonNull KeyEvent input) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
				closeEditor();
				return true;
			} else return crosshairEditor.keyPressed(input);
		}
		return super.keyPressed(input);
	}

	@Override
	public boolean charTyped(@NonNull CharacterEvent input) {
		return crosshairEditor.charTyped(input);
	}

	public void openEditor(CrosshairButtonWidget<?> crosshairButtonWidget) {
		if (crosshairEditor == null) {
			crosshairEditor = new CrosshairEditor(crosshairButtonWidget);
		}
		crosshairEditor.setFocused(true);
		crosshairEditor.updateTexture(crosshairButtonWidget.getData());
		this.disableAllChildren();
		setCancelAndSaveButtonsVisibility(false);
	}

	public void closeEditor() {
		if (crosshairEditor != null) {
			crosshairEditor.setFocused(false);
		}
		this.enableAllChildren();
		setCancelAndSaveButtonsVisibility(true);
	}
}
