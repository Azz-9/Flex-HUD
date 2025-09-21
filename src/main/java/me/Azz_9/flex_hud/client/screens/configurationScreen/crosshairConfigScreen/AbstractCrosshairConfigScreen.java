package me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen;

import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor.CrosshairEditor;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.widgets.HelpWidget;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
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
	public boolean mouseClicked(Click click, boolean doubled) {
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
	public boolean mouseDragged(Click click, double offsetX, double offsetY) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (crosshairEditor.mouseDragged(click, offsetX, offsetY)) {
				return true;
			}
		}
		return super.mouseDragged(click, offsetX, offsetY);
	}

	@Override
	public boolean mouseReleased(Click click) {
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
	public boolean keyPressed(KeyInput input) {
		if (crosshairEditor != null && crosshairEditor.isFocused()) {
			if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
				closeEditor();
				return true;
			} else return crosshairEditor.keyPressed(input);
		}
		return super.keyPressed(input);
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
