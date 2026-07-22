package me.Azz_9.flex_hud.client.gui.screens;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.Constants;
import me.Azz_9.flex_hud.client.gui.components.HelpWidget;
import me.Azz_9.flex_hud.client.gui.components.MovableWidget;
import me.Azz_9.flex_hud.client.gui.undoManager.UndoManager;
import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.TickableModule;
import me.Azz_9.flex_hud.client.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.client.modules.hud.DimensionHud;

public class EditLayoutScreen extends AbstractSavableScreen {
	private static final int HELP_WIDGET_PADDING = 4;
	private static final int HELP_WIDGET_SIZE = 20;

	private HelpWidget helpWidget;

	private final List<MovableWidget> movableWidgets = new ArrayList<>();

	public UndoManager undoManager = new UndoManager();

	private boolean firstFrame = true;

	public EditLayoutScreen(Screen parent) {
		super(Component.translatable("flex_hud.edit_layout_screen"), parent);
	}

	@Override
	protected void initContent() {
		helpWidget = null;
		movableWidgets.clear();

		helpWidget = new HelpWidget(HELP_WIDGET_PADDING, this.height - HELP_WIDGET_PADDING - HELP_WIDGET_SIZE, HELP_WIDGET_SIZE, HELP_WIDGET_SIZE, new Component[]{
				Component.translatable("flex_hud.move_module_screen.help_widget.line1"),
				Component.translatable("flex_hud.move_module_screen.help_widget.line2"),
				Component.translatable("flex_hud.move_module_screen.help_widget.line3"),
				Component.translatable("flex_hud.move_module_screen.help_widget.line4"),
				Component.translatable("flex_hud.move_module_screen.help_widget.line5"),
		});

		this.addRenderableWidget(helpWidget);

		for (AbstractMovableModule movableModule : Modules.getMovableModules()) {
			if (movableModule.shouldShowInEditLayoutScreen()) {
				// certains modules utilisent des placeholder, pour ces modules il faut forcer le tick pour que
				// les données utilisées soient les placeholders et que la taille du MovableWidget soit la bonne
				if (movableModule instanceof TickableModule tickable && tickable.shouldTick()) tickable.tick();

				for (DimensionHud dimensionHud : movableModule.getDimensionHudList()) {
					if (dimensionHud.isEnabled()) {
						MovableWidget movableWidget = new MovableWidget(dimensionHud, movableModule.getAnchorModeX(), movableModule.getAnchorModeY(), this);
						movableWidgets.add(movableWidget);
						this.addRenderableWidget(movableWidget);
						registerTracked(movableWidget);
					}
				}
			}
		}
	}

	@Override
	public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float a) {
		if (this.minecraft.level == null) {
			this.renderPanorama(graphics, a);
		}
	}

	@Override
	public void renderBeforeOtherElements(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		if (MINECRAFT.level == null) {
			Modules.getMovableModules().forEach(movableModule -> {
				if (movableModule.shouldShowInEditLayoutScreen()) {
					if (Constants.DEBUG) {
						movableModule.renderWithPerfTest(graphics, DeltaTracker.ZERO);
					} else {
						movableModule.render(graphics, DeltaTracker.ZERO);
					}
				}
			});
		}

		if (firstFrame) {
			getMovableWidgets().forEach((movableWidget) -> {
				movableWidget.updateDimensionAndPosition();
				movableWidget.updateScaleHandle();
			});
			firstFrame = false;
		}
	}

	public List<MovableWidget> getMovableWidgets() {
		return movableWidgets;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		helpWidget.handleOutsideClick(mouseX, mouseY);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (undoManager.handleKeyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void onActuallyClose() {
		CommonClass.isEditingLayout = false;
	}
}
