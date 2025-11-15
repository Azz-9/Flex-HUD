package me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor.CrosshairEditor;
import me.Azz_9.flex_hud.client.screens.widgets.HelpWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public abstract class AbstractCrosshairConfigScreen extends AbstractConfigurationScreen {
	private CrosshairEditor crosshairEditor = null;
	private HelpWidget helpWidget;

	public AbstractCrosshairConfigScreen(Text title, Screen parent) {
		super(title, parent);
	}

	@Override
	protected void init() {
		super.init();

		int helpWidgetPadding = 4;
		int helpWidgetSize = 20;
		helpWidget = new HelpWidget(helpWidgetPadding, this.height - helpWidgetPadding - helpWidgetSize, helpWidgetSize, helpWidgetSize, new Text[]{
				Text.translatable("flex_hud.crosshair_editor.help_widget.line1"),
				Text.translatable("flex_hud.crosshair_editor.help_widget.line2"),
				Text.translatable("flex_hud.crosshair_editor.help_widget.line3"),
				Text.translatable("flex_hud.crosshair_editor.help_widget.line4"),
		});
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.render(context, mouseX, mouseY, deltaTicks);

		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			crosshairEditor.render(context, mouseX, mouseY, deltaTicks);
			helpWidget.render(context, mouseX, mouseY, deltaTicks);
		}
	}

	public CrosshairEditor getCrosshairEditor() {
		return crosshairEditor;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (crosshairEditor.mouseClicked(mouseX, mouseY, button) || helpWidget.mouseClicked(mouseX, mouseY, button)) {
				return true;
			} else {
				closeEditor();
				return false;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (crosshairEditor.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
				return true;
			}
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (crosshairEditor.mouseReleased(mouseX, mouseY, button)) {
				return true;
			}
		}
		return super.mouseReleased(mouseX, mouseY, button);
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
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
				closeEditor();
				return true;
			} else return crosshairEditor.keyPressed(keyCode, scanCode, modifiers);
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
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

	@Override
	protected void saveAndClose() {
		ModulesHelper.getInstance().crosshair.crosshairTexture.updatePixels(ModulesHelper.getInstance().crosshair.pixels.getValue());
		super.saveAndClose();
	}

	public void closeEditor() {
		if (crosshairEditor != null) {
			crosshairEditor.setFocused(false);
		}
		this.enableAllChildren();
		setCancelAndSaveButtonsVisibility(true);
	}
}
