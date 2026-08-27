package me.Azz_9.flex_hud.client.gui.components.customModule.moduleContentField;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

import me.Azz_9.flex_hud.client.gui.components.config.colorPicker.ColorBindable;
import me.Azz_9.flex_hud.client.gui.components.config.colorPicker.ColorPicker;
import me.Azz_9.flex_hud.client.gui.components.customModule.ModuleContentEditorModel;

final class ColorPopup {
	private final ModuleContentField host;
	private final int selectionStart;
	private final int selectionEnd;
	private final ColorPicker selector;
	private Bounds bounds = new Bounds(0, 0, 0, 0);
	private Bounds noneBounds = new Bounds(0, 0, 0, 0);
	private Bounds chromaBounds = new Bounds(0, 0, 0, 0);

	ColorPopup(ModuleContentField host, int selectionStart, int selectionEnd, int initialColor) {
		this.host = host;
		this.selectionStart = selectionStart;
		this.selectionEnd = selectionEnd;
		SelectionColorBindable bindable = new SelectionColorBindable(initialColor);
		this.selector = new ColorPicker(bindable);
	}

	void layout(SelectionBounds selectionBounds) {
		int width = textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.no_color"))
				+ textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.chroma"))
				+ ModuleContentField.POPUP_GAP + ModuleContentField.POPUP_PADDING * 2;
		int height = ModuleContentField.POPUP_PADDING * 2 + ModuleContentField.BUTTON_HEIGHT + ModuleContentField.POPUP_GAP + selector.getHeight();

		int preferredX = selectionBounds.centerX() - width / 2;
		int preferredY = host.selectionPopupPreferredY(selectionBounds);
		bounds = new Bounds(host.clampX(preferredX, width), host.clampY(preferredY, height), width, height);

		int buttonX = bounds.x() + ModuleContentField.POPUP_PADDING;
		noneBounds = new Bounds(buttonX, bounds.y() + ModuleContentField.POPUP_PADDING, textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.no_color")), ModuleContentField.BUTTON_HEIGHT);
		buttonX = noneBounds.right() + ModuleContentField.POPUP_GAP;
		chromaBounds = new Bounds(buttonX, bounds.y() + ModuleContentField.POPUP_PADDING, textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.chroma")), ModuleContentField.BUTTON_HEIGHT);

		selector.setWidthGradientGrowth(bounds.width() - ModuleContentField.POPUP_PADDING * 2);
		selector.setPosition(bounds.x() + bounds.width() - ModuleContentField.POPUP_PADDING - selector.getWidth(), bounds.bottom() - ModuleContentField.POPUP_PADDING - selector.getHeight());
		selector.setFocused(true);
	}

	void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		host.renderPanel(graphics, bounds);
		host.renderButtonCenterLabel(graphics, noneBounds, Component.translatable("flex_hud.create_module_screen.editor.no_color"), noneBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
		host.renderButtonCenterLabel(graphics, chromaBounds, Component.translatable("flex_hud.create_module_screen.editor.chroma"), chromaBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);

		selector.render(graphics, mouseX, mouseY, deltaTicks);
	}

	boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (selector.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}
		if (noneBounds.contains(mouseX, mouseY)) {
			host.applySelectionColorLayers(selectionStart, selectionEnd, List.of());
			host.closeColorPopup();
			return true;
		}
		if (chromaBounds.contains(mouseX, mouseY)) {
			host.applySelectionColorLayers(selectionStart, selectionEnd, List.of(ModuleContentEditorModel.ChromaColorLayer.INSTANCE));
			host.closeColorPopup();
			return true;
		}
		if (!bounds.contains(mouseX, mouseY)) {
			host.closeColorPopup();
			return true;
		}
		return true;
	}

	boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		return selector.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	boolean mouseReleased(double mouseX, double mouseY, int button) {
		return selector.mouseReleased(mouseX, mouseY, button);
	}

	boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return selector.keyPressed(keyCode, scanCode, modifiers);
	}

	boolean charTyped(char codePoint, int modifiers) {
		return selector.charTyped(codePoint, modifiers);
	}

	boolean contains(double mouseX, double mouseY) {
		return bounds.contains(mouseX, mouseY) || selector.isMouseOver(mouseX, mouseY);
	}

	boolean matchesSelection() {
		return host.selectionMatches(selectionStart, selectionEnd);
	}

	private int textButtonWidth(Component text) {
		return MINECRAFT.font.width(text) + ModuleContentField.BUTTON_HORIZONTAL_PADDING * 2;
	}

	private final class SelectionColorBindable implements ColorBindable {
		private int color;

		private SelectionColorBindable(int color) {
			this.color = color;
		}

		@Override
		public void onReceiveColor(int color) {
			this.color = color;
			host.applySelectionColorLayers(selectionStart, selectionEnd, List.of(new ModuleContentEditorModel.StaticColorLayer(color)));
		}

		@Override
		public int getColor() {
			return color;
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
