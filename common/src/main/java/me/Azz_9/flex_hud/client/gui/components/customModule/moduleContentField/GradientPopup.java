package me.Azz_9.flex_hud.client.gui.components.customModule.moduleContentField;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

import me.Azz_9.flex_hud.client.gui.components.config.colorPicker.ColorBindable;
import me.Azz_9.flex_hud.client.gui.components.config.colorPicker.ColorPicker;
import me.Azz_9.flex_hud.client.gui.components.customModule.ModuleContentEditorModel;

final class GradientPopup {
	private static final int START_END_BUTTON_WIDTH = 48;

	private final ModuleContentField host;
	private final int selectionStart;
	private final int selectionEnd;
	private final GradientColorBindable bindable;
	private ColorPicker selector;
	private int startColor;
	private int endColor;
	private boolean editingStart = true;

	private Bounds bounds = new Bounds(0, 0, 0, 0);
	private Bounds startBounds = new Bounds(0, 0, 0, 0);
	private Bounds endBounds = new Bounds(0, 0, 0, 0);
	private Bounds clearBounds = new Bounds(0, 0, 0, 0);

	GradientPopup(ModuleContentField host, int selectionStart, int selectionEnd, int startColor, int endColor) {
		this.host = host;
		this.selectionStart = selectionStart;
		this.selectionEnd = selectionEnd;
		this.startColor = startColor;
		this.endColor = endColor;
		this.bindable = new GradientColorBindable();
		this.selector = new ColorPicker(bindable);
		applyGradient();
	}

	void layout(SelectionBounds selectionBounds) {
		int width = START_END_BUTTON_WIDTH * 2
				+ textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.no_color"))
				+ ModuleContentField.POPUP_GAP * 2 + ModuleContentField.POPUP_PADDING * 2;
		int height = ModuleContentField.POPUP_PADDING * 2 + ModuleContentField.BUTTON_HEIGHT + ModuleContentField.POPUP_GAP + selector.getHeight();
		int preferredX = selectionBounds.centerX() - width / 2;
		int preferredY = host.selectionPopupPreferredY(selectionBounds);
		bounds = new Bounds(host.clampX(preferredX, width), host.clampY(preferredY, height), width, height);

		int buttonX = bounds.x() + ModuleContentField.POPUP_PADDING;
		startBounds = new Bounds(buttonX, bounds.y() + ModuleContentField.POPUP_PADDING, START_END_BUTTON_WIDTH, ModuleContentField.BUTTON_HEIGHT);
		buttonX = startBounds.right() + ModuleContentField.POPUP_GAP;
		endBounds = new Bounds(buttonX, bounds.y() + ModuleContentField.POPUP_PADDING, START_END_BUTTON_WIDTH, ModuleContentField.BUTTON_HEIGHT);
		clearBounds = new Bounds(bounds.right() - ModuleContentField.POPUP_PADDING - textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.no_color")), bounds.y() + ModuleContentField.POPUP_PADDING, textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.no_color")), ModuleContentField.BUTTON_HEIGHT);

		selector.setWidthGradientGrowth(bounds.width() - ModuleContentField.POPUP_PADDING * 2);
		selector.setPosition(bounds.x() + ModuleContentField.POPUP_PADDING, bounds.bottom() - ModuleContentField.POPUP_PADDING - selector.getHeight());
		selector.setFocused(true);
	}

	void extractRenderState(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		host.renderPanel(graphics, bounds);
		host.renderButtonCenterLabel(graphics, startBounds, Component.translatable("flex_hud.create_module_screen.editor.gradient_start"), startBounds.contains(mouseX, mouseY) || editingStart ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
		host.renderButtonCenterLabel(graphics, endBounds, Component.translatable("flex_hud.create_module_screen.editor.gradient_end"), endBounds.contains(mouseX, mouseY) || !editingStart ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
		host.renderButtonCenterLabel(graphics, clearBounds, Component.translatable("flex_hud.create_module_screen.editor.no_color"), clearBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);

		extractColorPreview(graphics, startBounds, startColor);
		extractColorPreview(graphics, endBounds, endColor);
		selector.render(graphics, mouseX, mouseY, deltaTicks);
	}

	private void extractColorPreview(GuiGraphics graphics, Bounds bounds, int color) {
		graphics.fill(bounds.x() + 2, bounds.bottom() - 4, bounds.right() - 2, bounds.bottom() - 2, 0xff000000 | color);
	}

	boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
		if (selector.mouseClicked(event, doubled)) {
			return true;
		}
		if (startBounds.contains(event.x(), event.y())) {
			editingStart = true;
			rebuildSelector();
			return true;
		}
		if (endBounds.contains(event.x(), event.y())) {
			editingStart = false;
			rebuildSelector();
			return true;
		}
		if (clearBounds.contains(event.x(), event.y())) {
			host.applySelectionColorLayers(selectionStart, selectionEnd, List.of());
			host.closeGradientPopup();
			return true;
		}
		if (!bounds.contains(event.x(), event.y())) {
			host.closeGradientPopup();
			return true;
		}
		return true;
	}

	boolean mouseDragged(MouseButtonEvent event, double offsetX, double offsetY) {
		return selector.mouseDragged(event, offsetX, offsetY);
	}

	boolean mouseReleased(MouseButtonEvent event) {
		return selector.mouseReleased(event);
	}

	boolean keyPressed(KeyEvent event) {
		return selector.keyPressed(event);
	}

	boolean charTyped(CharacterEvent event) {
		return selector.charTyped(event);
	}

	boolean contains(double mouseX, double mouseY) {
		return bounds.contains(mouseX, mouseY) || selector.isMouseOver(mouseX, mouseY);
	}

	boolean matchesSelection() {
		return host.selectionMatches(selectionStart, selectionEnd);
	}

	private void applyGradient() {
		host.applySelectionColorLayers(selectionStart, selectionEnd, List.of(new ModuleContentEditorModel.GradientColorLayer(startColor, endColor)));
	}

	private void rebuildSelector() {
		this.selector = new ColorPicker(bindable);
		selector.setPosition(bounds.x() + bounds.width() - ModuleContentField.POPUP_PADDING - selector.getWidth(), bounds.bottom() - ModuleContentField.POPUP_PADDING - selector.getHeight());
	}

	private int textButtonWidth(Component text) {
		return MINECRAFT.font.width(text) + ModuleContentField.BUTTON_HORIZONTAL_PADDING * 2;
	}

	private final class GradientColorBindable implements ColorBindable {
		@Override
		public void onReceiveColor(int color) {
			if (editingStart) {
				startColor = color;
			} else {
				endColor = color;
			}
			applyGradient();
		}

		@Override
		public int getColor() {
			return editingStart ? startColor : endColor;
		}

		@Override
		public int getRight() {
			return bounds.right();
		}

		@Override
		public int getY() {
			return bounds.y();
		}

		@Override
		public int getBottom() {
			return bounds.bottom();
		}
	}
}
