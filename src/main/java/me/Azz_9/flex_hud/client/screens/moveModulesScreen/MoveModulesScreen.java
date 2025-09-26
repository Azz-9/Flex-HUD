package me.Azz_9.flex_hud.client.screens.moveModulesScreen;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.MovableModule;
import me.Azz_9.flex_hud.client.screens.AbstractCallbackScreen;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.actions.UndoManager;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.widgets.MovableWidget;
import me.Azz_9.flex_hud.client.screens.widgets.HelpWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class MoveModulesScreen extends AbstractCallbackScreen {
	private HelpWidget helpWidget;

	private final List<MovableWidget> movableWidgets = new ArrayList<>();

	public UndoManager undoManager = new UndoManager();

	private boolean firstFrame = true;

	public MoveModulesScreen(Screen parent) {
		super(Text.translatable("flex_hud.move_elements_screen"), parent, Text.translatable("flex_hud.global.config.callback.message_title"), Text.translatable("flex_hud.global.config.callback.message_content"));
	}

	@Override
	protected void init() {
		helpWidget = null;
		movableWidgets.clear();

		super.init();

		int helpWidgetPadding = 4;
		int helpWidgetSize = 20;
		helpWidget = new HelpWidget(helpWidgetPadding, this.height - helpWidgetPadding - helpWidgetSize, helpWidgetSize, helpWidgetSize, new Text[]{
				Text.translatable("flex_hud.move_module_screen.help_widget.line1"),
				Text.translatable("flex_hud.move_module_screen.help_widget.line2"),
				Text.translatable("flex_hud.move_module_screen.help_widget.line3"),
				Text.translatable("flex_hud.move_module_screen.help_widget.line4"),
				Text.translatable("flex_hud.move_module_screen.help_widget.line5"),
		});

		this.addDrawableChild(helpWidget);

		for (MovableModule movableModule : JsonConfigHelper.getMovableModules()) {
			if (movableModule.isEnabled()) {
				MovableWidget movableWidget = new MovableWidget(movableModule, this);
				movableWidgets.add(movableWidget);
				this.addDrawableChild(movableWidget);
				registerTrackableWidget(movableWidget);
			}
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (firstFrame) {
			getMovableWidgets().forEach((movableWidget) -> {
				movableWidget.updateDimensionAndPosition();
				movableWidget.updateScaleHandle();
			});
			firstFrame = false;
		}

		if (renderCallback(context, mouseX, mouseY, deltaTicks)) {
			return;
		}

		movableWidgets.forEach(widget -> widget.render(context, deltaTicks));

		helpWidget.render(context, mouseX, mouseY, deltaTicks);
	}

	public List<MovableWidget> getMovableWidgets() {
		return movableWidgets;
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (isCallbackScreen()) {
			super.renderBackground(context, mouseX, mouseY, deltaTicks);
		}
	}

	@Override
	protected void disableAllChildren() {
		helpWidget.active = false;
		movableWidgets.forEach(widget -> widget.active = false);
	}

	@Override
	protected void enableAllChildren() {
		helpWidget.active = true;
		movableWidgets.forEach(widget -> widget.active = true);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		movableWidgets.forEach(widget -> widget.mouseMoved(mouseX, mouseY));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (helpWidget.getDisplayHelp() && !helpWidget.isMouseOver(mouseX, mouseY)) {
			helpWidget.onClick(mouseX, mouseY);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 87 && modifiers == GLFW.GLFW_MOD_CONTROL) { // CTRL + Z
			undoManager.undo();
		} else if ((keyCode == GLFW.GLFW_KEY_Y && modifiers == GLFW.GLFW_MOD_CONTROL) ||
				(keyCode == 87 && modifiers == (GLFW.GLFW_MOD_CONTROL + GLFW.GLFW_MOD_SHIFT))) { // CTRL + Y or CTRL + SHIFT + Z
			undoManager.redo();
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void close() {
		Flex_hudClient.isInMoveElementScreen = false;
		super.close();
	}

	public void onWidgetChange() {
		updateSaveButton();
	}
}
