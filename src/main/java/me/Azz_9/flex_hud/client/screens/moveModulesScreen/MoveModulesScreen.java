package me.Azz_9.flex_hud.client.screens.moveModulesScreen;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.DimensionHud;
import me.Azz_9.flex_hud.client.screens.AbstractCallbackScreen;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.actions.UndoManager;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.widgets.MovableWidget;
import me.Azz_9.flex_hud.client.screens.widgets.HelpWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class MoveModulesScreen extends AbstractCallbackScreen {
	private HelpWidget helpWidget;

	private final List<MovableWidget> movableWidgets = new ArrayList<>();

	public UndoManager undoManager = new UndoManager();

	private boolean firstFrame = true;

	public MoveModulesScreen(Screen parent) {
		super(Component.translatable("flex_hud.move_modules_screen"), parent, Component.translatable("flex_hud.global.config.callback.message_title"), Component.translatable("flex_hud.global.config.callback.message_content"));
	}

	@Override
	protected void init() {
		helpWidget = null;
		movableWidgets.clear();

		super.init();

		int helpWidgetPadding = 4;
		int helpWidgetSize = 20;
		helpWidget = new HelpWidget(helpWidgetPadding, this.height - helpWidgetPadding - helpWidgetSize, helpWidgetSize, helpWidgetSize, new Component[]{
				Component.translatable("flex_hud.move_module_screen.help_widget.line1"),
				Component.translatable("flex_hud.move_module_screen.help_widget.line2"),
				Component.translatable("flex_hud.move_module_screen.help_widget.line3"),
				Component.translatable("flex_hud.move_module_screen.help_widget.line4"),
				Component.translatable("flex_hud.move_module_screen.help_widget.line5"),
		});

		this.addRenderableWidget(helpWidget);

		for (AbstractMovableModule movableModule : ModulesHelper.getMovableModules()) {
			if (movableModule.isEnabled()) {
				// certains modules utilisent des placeholder, pour ces modules il faut forcer le tick pour que
				// les données utilisées soient les placeholders et que la taille du MovableWidget soit la bonne
				if (movableModule instanceof TickableModule tickable) tickable.tick();

				for (DimensionHud dimensionHud : movableModule.getDimensionHudList()) {
					if (dimensionHud.isEnabled()) {
						MovableWidget movableWidget = new MovableWidget(dimensionHud, this);
						movableWidgets.add(movableWidget);
						this.addRenderableWidget(movableWidget);
						registerTrackableWidget(movableWidget);
					}
				}
			}
		}
	}

	@Override
	public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		if (Minecraft.getInstance().level == null) {
			super.renderBackground(graphics, mouseX, mouseY, deltaTicks);
		}

		if (renderCallback(graphics, mouseX, mouseY, deltaTicks)) {
			return;
		}

		ModulesHelper.getHudElements().forEach(hudElement -> {
			if (Flex_hudClient.isDebug()) {
				hudElement.renderWithSpeedTest(graphics, DeltaTracker.ZERO);
			} else {
				hudElement.render(graphics, DeltaTracker.ZERO);
			}
		});

		if (firstFrame) {
			getMovableWidgets().forEach((movableWidget) -> {
				movableWidget.updateDimensionAndPosition();
				movableWidget.updateScaleHandle();
			});
			firstFrame = false;
		}

		movableWidgets.forEach(widget -> widget.draw(graphics, mouseX, mouseY, deltaTicks));

		helpWidget.render(graphics, mouseX, mouseY, deltaTicks);
	}

	public List<MovableWidget> getMovableWidgets() {
		return movableWidgets;
	}

	@Override
	public void renderBackground(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		if (isCallbackScreen() && Minecraft.getInstance().level != null) {
			super.renderBackground(graphics, mouseX, mouseY, deltaTicks);
		}
	}

	@Override
	protected void disableAllChildren() {
		super.disableAllChildren();
		helpWidget.active = false;
		movableWidgets.forEach(widget -> widget.active = false);
	}

	@Override
	protected void enableAllChildren() {
		super.enableAllChildren();
		helpWidget.active = true;
		movableWidgets.forEach(widget -> widget.active = true);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		movableWidgets.forEach(widget -> widget.mouseMoved(mouseX, mouseY));
	}

	@Override
	public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
		if (helpWidget.getDisplayHelp() && !helpWidget.isMouseOver(click.x(), click.y())) {
			helpWidget.onClick(click, doubled);
		}
		return super.mouseClicked(click, doubled);
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		if (input.key() == 87 && input.modifiers() == GLFW.GLFW_MOD_CONTROL) { // CTRL + Z
			undoManager.undo();
		} else if ((input.key() == GLFW.GLFW_KEY_Y && input.modifiers() == GLFW.GLFW_MOD_CONTROL) ||
				(input.key() == 87 && input.modifiers() == (GLFW.GLFW_MOD_CONTROL + GLFW.GLFW_MOD_SHIFT))) { // CTRL + Y or CTRL + SHIFT + Z
			undoManager.redo();
		}
		return super.keyPressed(input);
	}

	@Override
	public void onClose() {
		Flex_hudClient.isInMoveElementScreen = false;
		super.onClose();
	}

	public void onWidgetChange() {
		updateSaveButton();
	}
}
