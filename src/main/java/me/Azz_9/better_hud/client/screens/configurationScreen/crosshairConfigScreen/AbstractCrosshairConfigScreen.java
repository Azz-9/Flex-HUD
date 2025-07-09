package me.Azz_9.better_hud.client.screens.configurationScreen.crosshairConfigScreen;

import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor.CrosshairEditor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AbstractCrosshairConfigScreen extends AbstractConfigurationScreen {
	private CrosshairEditor crosshairEditor = null;

	public AbstractCrosshairConfigScreen(Text title, Screen parent) {
		super(title, parent);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.render(context, mouseX, mouseY, deltaTicks);

		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			crosshairEditor.render(context, mouseX, mouseY, deltaTicks);
		}
	}

	public CrosshairEditor getCrosshairEditor() {
		return crosshairEditor;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (crosshairEditor.mouseClicked(mouseX, mouseY, button)) {
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
		return super.mouseClicked(mouseX, mouseY, button);
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
		if (crosshairEditor != null && crosshairEditor.isFocused() && keyCode == GLFW.GLFW_KEY_ESCAPE) {
			closeEditor();
			return true;
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

	public void closeEditor() {
		if (crosshairEditor != null) {
			crosshairEditor.setFocused(false);
		}
		this.enableAllChildren();
		setCancelAndSaveButtonsVisibility(true);
	}
}
