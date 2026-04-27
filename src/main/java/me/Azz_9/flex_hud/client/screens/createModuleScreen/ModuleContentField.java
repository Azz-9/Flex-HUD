package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifier;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;
import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorBindable;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorSelector;
import me.Azz_9.flex_hud.client.tickables.ChromaColorTickable;

public class ModuleContentField extends ClickableWidget implements TrackableChange {

	private static final ButtonTextures TEXTURES = new ButtonTextures(
			Identifier.ofVanilla("widget/text_field"), Identifier.ofVanilla("widget/text_field_highlighted")
	);

	private static final int TEXT_PADDING_X = 4;
	private static final int TEXT_PADDING_Y = 4;
	private static final int SELECTION_COLOR = 0x66357dff;
	private static final int CARET_COLOR = 0xffffffff;
	private static final int TEXT_COLOR = 0xffffffff;
	private static final int PLACEHOLDER_COLOR = 0xff8a8f98;

	private static final int VARIABLE_BG_COLOR = 0xff24354c;
	private static final int VARIABLE_BORDER_COLOR = 0xff4d6d92;
	private static final int VARIABLE_SELECTED_BG_COLOR = 0xff365277;
	private static final int VARIABLE_PADDING_X = 4;
	private static final int VARIABLE_PADDING_Y = 2;
	private static final int VARIABLE_GAP = 4;
	private static final int MODIFIER_BG_COLOR = 0xff172332;
	private static final int MODIFIER_SELECTED_BG_COLOR = 0xff27415f;
	private static final int MODIFIER_PADDING_X = 4;
	private static final int MODIFIER_SEPARATOR_GAP = 6;
	private static final int MODIFIER_SEPARATOR_COLOR = 0xff7892b0;
	private static final int VARIABLE_PLUS_GAP = 5;
	private static final int DESCRIPTION_DELAY = 500;
	private static final int DESCRIPTION_MAX_WIDTH = 220;
	private static final int DESCRIPTION_PADDING = 4;
	private static final int DESCRIPTION_BACKGROUND = 0xf01e1f22;
	private static final int POPUP_BACKGROUND = 0xff1e1f22;
	private static final int POPUP_BORDER = 0xff4a4f59;
	private static final int POPUP_PADDING = 6;
	private static final int POPUP_GAP = 4;
	private static final int OVERLAY_GAP = 6;
	private static final int BUTTON_HEIGHT = 16;
	private static final int BUTTON_HORIZONTAL_PADDING = 5;
	private static final int BUTTON_BACKGROUND = 0xff2c3138;
	private static final int BUTTON_HOVERED_BACKGROUND = 0xff404651;
	private static final int BUTTON_ACTIVE_BACKGROUND = 0xff365277;
	private static final int BUTTON_MIXED_BACKGROUND = 0xff675a32;
	private static final int BUTTON_TEXT_COLOR = 0xffffffff;
	private static final int POPUP_ERROR_COLOR = 0xffff7070;
	private static final int CHIP_PLUS_WIDTH = 8;
	private static final int TOOLBAR_BUTTON_WIDTH = 18;
	private static final int TOOLBAR_ICON_BUTTON_WIDTH = 30;
	private static final List<Integer> PRESET_COLORS = List.of(
			0x000000, 0x0000aa, 0x00aa00, 0x00aaaa,
			0xaa0000, 0xaa00aa, 0xffaa00, 0xaaaaaa,
			0x555555, 0x5555ff, 0x55ff55, 0x55ffff,
			0xff5555, 0xff55ff, 0xffff55, 0xffffff
	);
	private static final int SCROLLBAR_THUMB_COLOR = 0xff636360;
	private static final int SCROLLBAR_THUMB_ACTIVE_COLOR = 0xffa8a8a4;
	private static final Pattern UNSIGNED_INTEGER_INPUT = Pattern.compile("\\d{0,9}");
	private static final Pattern UNSIGNED_TWO_DIGIT_INTEGER_INPUT = Pattern.compile("\\d{0,2}");
	private static final Pattern SIGNED_INTEGER_INPUT = Pattern.compile("-?\\d{0,9}");
	private static final Pattern SIGNED_NON_ZERO_INTEGER_INPUT = Pattern.compile("-?[1-9]\\d{0,8}");

	private final @Nullable String initialContent;

	private ModuleContentEditorModel model;
	private String rawText;
	private int maxLength = 200;
	private Consumer<String> changedListener = text -> {
	};
	private int caretIndex;
	private int selectionAnchor;
	private int horizontalScroll;
	private boolean draggingSelection;

	private List<DisplayItem> displayItems = List.of();
	private int[] caretPositions = new int[]{0};
	private int contentWidth;
	private @Nullable HoverTarget hoveredTarget;
	private @Nullable VariableHit hoveredVariableHit;
	private long hoverStartTime;
	private @Nullable SelectionBounds selectionBounds;
	private List<ToolbarButton> toolbarButtons = List.of();
	private @Nullable ModifierPickerPopup modifierPickerPopup;
	private @Nullable ModifierEditorPopup modifierEditorPopup;
	private @Nullable ColorPopup colorPopup;
	private @Nullable GradientPopup gradientPopup;

	public ModuleContentField(int x, int y, int width, int height, @Nullable String initialContent) {
		super(x, y, width, height, Text.empty());
		this.initialContent = initialContent;
		this.rawText = initialContent == null ? "" : initialContent;
		this.model = ModuleContentEditorModel.parse(rawText);
		this.caretIndex = model.size();
		this.selectionAnchor = caretIndex;
		rebuildLayout();
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		Identifier texture = TEXTURES.get(this.isInteractable(), this.isFocused());
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), getWidth(), getHeight());

		refreshOverlayLayout();
		updateHover(mouseX, mouseY);

		int innerLeft = getX() + TEXT_PADDING_X;
		int innerTop = getY() + TEXT_PADDING_Y;
		int innerRight = getRight() - TEXT_PADDING_X;
		int innerBottom = getBottom() - TEXT_PADDING_Y;
		int contentTextY = getContentTextY();

		context.enableScissor(innerLeft, innerTop, innerRight, innerBottom);
		renderSelection(context, innerLeft, contentTextY);
		renderContent(context, innerLeft, contentTextY);
		renderCaret(context, innerLeft, contentTextY);
		context.disableScissor();

		if (rawText.isEmpty() && !isFocused()) {
			context.drawText(CLIENT.textRenderer, Text.translatable("flex_hud.create_module_screen.module_content.placeholder"), innerLeft, contentTextY, PLACEHOLDER_COLOR, false);
		}

		setCursor(context);

		renderToolbar(context, mouseX, mouseY);
		renderModifierPicker(context, mouseX, mouseY, deltaTicks);
		renderModifierEditor(context, mouseX, mouseY, deltaTicks);
		renderColorPopup(context, mouseX, mouseY, deltaTicks);
		renderGradientPopup(context, mouseX, mouseY, deltaTicks);
		renderTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void setCursor(DrawContext context) {
		if (this.isHovered()) {
			if (!this.isInteractable()) {
				context.setCursor(StandardCursors.NOT_ALLOWED);
			} else if (hoveredVariableHit != null
					&& (hoveredVariableHit.kind() == VariableHitKind.PLUS || hoveredVariableHit.kind() == VariableHitKind.MODIFIER)) {
				context.setCursor(StandardCursors.POINTING_HAND);
			} else {
				context.setCursor(StandardCursors.IBEAM);
			}
		}
	}

	private void renderContent(DrawContext context, int innerLeft, int contentTextY) {
		for (DisplayItem item : displayItems) {
			int drawX = innerLeft + item.x() - horizontalScroll;
			if (drawX + item.width() < innerLeft || drawX > getRight() - TEXT_PADDING_X) {
				continue;
			}

			if (item instanceof TextDisplayItem textDisplayItem) {
				context.drawText(CLIENT.textRenderer, textDisplayItem.text(), drawX, contentTextY, textDisplayItem.color(), false);
				continue;
			}

			VariableDisplayItem variableDisplayItem = (VariableDisplayItem) item;
			int chipTop = getDisplayItemTop(variableDisplayItem, contentTextY);
			boolean selected = isIndexSelected(variableDisplayItem.modelIndex());
			int backgroundColor = selected ? VARIABLE_SELECTED_BG_COLOR : VARIABLE_BG_COLOR;
			int modifierBackgroundColor = selected ? MODIFIER_SELECTED_BG_COLOR : MODIFIER_BG_COLOR;
			context.fill(drawX, chipTop, drawX + variableDisplayItem.width(), getDisplayItemBottom(variableDisplayItem, contentTextY), backgroundColor);
			context.drawStrokedRectangle(drawX, chipTop, variableDisplayItem.width(), variableDisplayItem.height(), VARIABLE_BORDER_COLOR);

			context.drawText(CLIENT.textRenderer, variableDisplayItem.name(), drawX + VARIABLE_PADDING_X, contentTextY, variableDisplayItem.color(), false);

			for (int modifierIndex = 0; modifierIndex < variableDisplayItem.modifiers().size(); modifierIndex++) {
				ModifierPart modifierPart = variableDisplayItem.modifiers().get(modifierIndex);
				int modifierX = drawX + modifierPart.startX();
				context.fill(modifierX, chipTop + 1, modifierX + modifierPart.width(), chipTop + variableDisplayItem.height() - 1, modifierBackgroundColor);
				context.drawText(CLIENT.textRenderer, modifierPart.displayText(), modifierX + MODIFIER_PADDING_X, contentTextY, modifierPart.color(), false);
				if (modifierIndex < variableDisplayItem.modifiers().size() - 1) {
					int separatorX = modifierX + modifierPart.width() + MODIFIER_SEPARATOR_GAP / 2;
					context.fill(separatorX, chipTop + 2, separatorX + 1, chipTop + variableDisplayItem.height() - 1, MODIFIER_SEPARATOR_COLOR);
				}
			}

			context.drawText(CLIENT.textRenderer, "+", drawX + variableDisplayItem.plusX(), contentTextY, variableDisplayItem.color(), false);
		}
	}

	private void renderSelection(DrawContext context, int innerLeft, int contentTextY) {
		int selectionStart = Math.min(caretIndex, selectionAnchor);
		int selectionEnd = Math.max(caretIndex, selectionAnchor);
		if (selectionStart == selectionEnd) {
			return;
		}

		for (DisplayItem item : displayItems) {
			if (!isIndexSelected(item.modelIndex())) {
				continue;
			}

			int drawX = innerLeft + item.x() - horizontalScroll;
			context.fill(drawX, getDisplayItemTop(item, contentTextY), drawX + item.width(), getDisplayItemBottom(item, contentTextY), SELECTION_COLOR);
		}
	}

	private void renderCaret(DrawContext context, int innerLeft, int contentTextY) {
		if (!isFocused() || hasSelection()) {
			return;
		}

		if ((System.currentTimeMillis() / 500L) % 2L == 0L) {
			return;
		}

		int caretX = innerLeft + caretPositions[Math.clamp(caretIndex, 0, caretPositions.length - 1)] - horizontalScroll;
		context.fill(caretX, contentTextY - 1, caretX + 1, contentTextY + CLIENT.textRenderer.fontHeight + 1, CARET_COLOR);
	}

	private void renderTooltip(DrawContext context, int mouseX, int mouseY) {
		if (hoveredTarget == null || System.currentTimeMillis() - hoverStartTime < DESCRIPTION_DELAY) {
			return;
		}

		Text tooltip = hoveredTarget.tooltip();
		int innerWidth = Math.min(DESCRIPTION_MAX_WIDTH, CLIENT.textRenderer.getWidth(tooltip));
		int width = innerWidth + DESCRIPTION_PADDING * 2;
		int height = CLIENT.textRenderer.getWrappedLinesHeight(tooltip, innerWidth) + DESCRIPTION_PADDING * 2;

		int x = mouseX + 8;
		int y = mouseY + 8;
		Screen screen = CLIENT.currentScreen;
		if (screen != null) {
			if (x + width > screen.width) {
				x = mouseX - width - 8;
			}
			if (y + height > screen.height) {
				y = mouseY - height - 8;
			}
		}

		ScreenRect currentScissor = context.scissorStack.peekLast();
		if (currentScissor != null) {
			context.disableScissor();
		}
		try {
			context.fill(x, y, x + width, y + height, DESCRIPTION_BACKGROUND);
			context.drawWrappedText(CLIENT.textRenderer, tooltip, x + DESCRIPTION_PADDING, y + DESCRIPTION_PADDING, innerWidth, TEXT_COLOR, false);
		} finally {
			if (currentScissor != null) {
				context.enableScissor(currentScissor.getLeft(), currentScissor.getTop(), currentScissor.getRight(), currentScissor.getBottom());
			}
		}
	}

	private int centeredTextY(int top, int height) {
		return top + Math.max(0, Math.floorDiv(height - CLIENT.textRenderer.fontHeight + 1, 2));
	}

	private int getContentTextY() {
		return centeredTextY(getY(), getHeight());
	}

	private int getDisplayItemTop(DisplayItem item, int contentTextY) {
		return item instanceof VariableDisplayItem ? contentTextY - VARIABLE_PADDING_Y : contentTextY - 1;
	}

	private int getDisplayItemBottom(DisplayItem item, int contentTextY) {
		return getDisplayItemTop(item, contentTextY) + item.height() + 1;
	}

	private void renderToolbar(DrawContext context, int mouseX, int mouseY) {
		for (ToolbarButton button : toolbarButtons) {
			renderButtonCenterLabel(context, button.bounds(), button.label(), button.backgroundColor(mouseX, mouseY), BUTTON_TEXT_COLOR, mouseX, mouseY);
		}
	}

	private void renderModifierPicker(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (modifierPickerPopup != null) {
			modifierPickerPopup.render(context, mouseX, mouseY, deltaTicks);
		}
	}

	private void renderModifierEditor(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (modifierEditorPopup != null) {
			modifierEditorPopup.render(context, mouseX, mouseY, deltaTicks);
		}
	}

	private void renderColorPopup(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (colorPopup != null) {
			colorPopup.render(context, mouseX, mouseY, deltaTicks);
		}
	}

	private void renderGradientPopup(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (gradientPopup != null) {
			gradientPopup.render(context, mouseX, mouseY, deltaTicks);
		}
	}

	private void updateHover(int mouseX, int mouseY) {
		hoveredVariableHit = findVariableHit(mouseX, mouseY);
		HoverTarget newTarget = findHoverTarget(mouseX, mouseY);
		if (!Objects.equals(newTarget, hoveredTarget)) {
			hoveredTarget = newTarget;
			hoverStartTime = System.currentTimeMillis();
		}
	}

	private @Nullable HoverTarget findHoverTarget(int mouseX, int mouseY) {
		for (ToolbarButton button : toolbarButtons) {
			if (button.bounds().contains(mouseX, mouseY)) {
				return new HoverTarget(button.tooltip());
			}
		}

		if (modifierPickerPopup != null) {
			HoverTarget target = modifierPickerPopup.findHoverTarget(mouseX, mouseY);
			if (target != null) {
				return target;
			}
		}

		int innerLeft = getX() + TEXT_PADDING_X;
		int contentTextY = getContentTextY();
		for (DisplayItem item : displayItems) {
			int drawX = innerLeft + item.x() - horizontalScroll;
			if (mouseY < getDisplayItemTop(item, contentTextY) || mouseY > getDisplayItemBottom(item, contentTextY)) {
				continue;
			}
			if (mouseX < drawX || mouseX > drawX + item.width()) {
				continue;
			}

			if (item instanceof VariableDisplayItem variableDisplayItem) {
				int localX = mouseX - drawX;
				if (localX >= variableDisplayItem.plusX() && localX <= variableDisplayItem.plusX() + CHIP_PLUS_WIDTH) {
					return new HoverTarget(Text.translatable("flex_hud.create_module_screen.editor.add_modifier"));
				}

				for (ModifierPart modifierPart : variableDisplayItem.modifiers()) {
					if (modifierPart.startX() <= localX && localX <= modifierPart.startX() + modifierPart.width()) {
						return new HoverTarget(modifierPart.tooltip());
					}
				}

				return new HoverTarget(variableDisplayItem.element().variable().getDescription());
			}
		}
		return null;
	}

	@Override
	public boolean mouseClicked(Click click, boolean doubled) {
		if (!active) {
			return false;
		}

		if (gradientPopup != null && gradientPopup.mouseClicked(click, doubled)) {
			setFocused(true);
			return true;
		}
		if (colorPopup != null && colorPopup.mouseClicked(click, doubled)) {
			setFocused(true);
			return true;
		}
		if (modifierEditorPopup != null && modifierEditorPopup.mouseClicked(click, doubled)) {
			setFocused(true);
			return true;
		}
		if (modifierPickerPopup != null && modifierPickerPopup.mouseClicked(click, doubled)) {
			setFocused(true);
			return true;
		}

		ToolbarButton toolbarButton = findToolbarButton(click.x(), click.y());
		if (toolbarButton != null) {
			handleToolbarAction(toolbarButton.action());
			setFocused(true);
			return true;
		}

		if (!isInsideField(click.x(), click.y())) {
			closeTransientPopups();
			setFocused(false);
			return false;
		}

		setFocused(true);

		VariableHit variableHit = findVariableHit(click.x(), click.y());
		if (variableHit != null) {
			if (variableHit.kind() == VariableHitKind.PLUS) {
				openModifierPicker(variableHit.variableItem());
			} else if (variableHit.kind() == VariableHitKind.MODIFIER) {
				openModifierEditor(variableHit.variableItem(), variableHit.modifierIndex());
			} else if (doubled) {
				closeModifierPopups();
				closeSelectionPopups();
				selectWord(variableHit.variableItem().modelIndex());
			} else {
				handleBodyClick(click, variableHit.variableItem());
			}
			ensureCaretVisible();
			return true;
		}

		closeModifierPopups();
		closeSelectionPopups();

		if (doubled) {
			selectWordAt(click.x());
			draggingSelection = true;
			return true;
		}

		int clickedIndex = getClosestCaretIndex(click.x());
		if ((click.modifiers() & GLFW.GLFW_MOD_SHIFT) != 0) {
			caretIndex = clickedIndex;
		} else {
			caretIndex = clickedIndex;
			selectionAnchor = clickedIndex;
		}
		draggingSelection = true;
		ensureCaretVisible();
		refreshOverlayLayout();
		return true;
	}

	@Override
	public boolean mouseDragged(Click click, double offsetX, double offsetY) {
		if (gradientPopup != null && gradientPopup.mouseDragged(click, offsetX, offsetY)) {
			return true;
		}
		if (colorPopup != null && colorPopup.mouseDragged(click, offsetX, offsetY)) {
			return true;
		}
		if (modifierEditorPopup != null && modifierEditorPopup.mouseDragged(click, offsetX, offsetY)) {
			return true;
		}
		if (modifierPickerPopup != null && modifierPickerPopup.mouseDragged(click, offsetX, offsetY)) {
			return true;
		}

		if (!draggingSelection || !isFocused()) {
			return false;
		}

		caretIndex = getClosestCaretIndex(click.x());
		ensureCaretVisible();
		refreshOverlayLayout();
		return true;
	}

	@Override
	public boolean mouseReleased(Click click) {
		boolean handled = false;
		if (gradientPopup != null) {
			handled |= gradientPopup.mouseReleased(click);
		}
		if (colorPopup != null) {
			handled |= colorPopup.mouseReleased(click);
		}
		if (modifierEditorPopup != null) {
			handled |= modifierEditorPopup.mouseReleased(click);
		}
		if (modifierPickerPopup != null) {
			handled |= modifierPickerPopup.mouseReleased(click);
		}

		draggingSelection = false;
		return handled;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		boolean handled = false;
		if (modifierPickerPopup != null) {
			handled = modifierPickerPopup.mouseScrolled(mouseX, mouseY, verticalAmount);
		}

		return handled;
	}

	@Override
	public boolean charTyped(CharInput input) {
		if (!active) {
			return false;
		}

		if (gradientPopup != null && gradientPopup.charTyped(input)) {
			return true;
		}
		if (colorPopup != null && colorPopup.charTyped(input)) {
			return true;
		}
		if (modifierEditorPopup != null && modifierEditorPopup.charTyped(input)) {
			return true;
		}

		if (!isFocused() || !input.isValidChar()) {
			return false;
		}

		write(input.asString());
		return true;
	}

	@Override
	public boolean keyPressed(KeyInput input) {
		if (!active) {
			return false;
		}

		if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
			if (gradientPopup != null) {
				gradientPopup = null;
				return true;
			}
			if (colorPopup != null) {
				colorPopup = null;
				return true;
			}
			if (modifierEditorPopup != null) {
				modifierEditorPopup = null;
				return true;
			}
			if (modifierPickerPopup != null) {
				modifierPickerPopup = null;
				return true;
			}
		}

		if (gradientPopup != null && gradientPopup.keyPressed(input)) {
			return true;
		}
		if (colorPopup != null && colorPopup.keyPressed(input)) {
			return true;
		}
		if (modifierEditorPopup != null && modifierEditorPopup.keyPressed(input)) {
			return true;
		}

		if (!isFocused()) {
			return false;
		}

		if (input.isSelectAll()) {
			selectAll();
			return true;
		}
		if (input.isCopy()) {
			copySelectionToClipboard();
			return true;
		}
		if (input.isPaste()) {
			write(CLIENT.keyboard.getClipboard());
			return true;
		}
		if (input.isCut()) {
			cutSelectionToClipboard();
			return true;
		}

		switch (input.key()) {
			case GLFW.GLFW_KEY_BACKSPACE -> {
				erase(-1, input.hasCtrlOrCmd());
				return true;
			}
			case GLFW.GLFW_KEY_DELETE -> {
				erase(1, input.hasCtrlOrCmd());
				return true;
			}
			case GLFW.GLFW_KEY_LEFT -> {
				if (input.hasCtrlOrCmd()) {
					setCaret(getWordSkipPosition(-1), input.hasShift());
				} else {
					moveCaret(-1, input.hasShift());
				}
				return true;
			}
			case GLFW.GLFW_KEY_RIGHT -> {
				if (input.hasCtrlOrCmd()) {
					setCaret(getWordSkipPosition(1), input.hasShift());
				} else {
					moveCaret(1, input.hasShift());
				}
				return true;
			}
			case GLFW.GLFW_KEY_HOME -> {
				setCaret(0, input.hasShift());
				return true;
			}
			case GLFW.GLFW_KEY_END -> {
				setCaret(model.size(), input.hasShift());
				return true;
			}
			default -> {
				return false;
			}
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return isInsideField(mouseX, mouseY)
				|| containsBounds(mouseX, mouseY, toolbarButtons.stream().map(ToolbarButton::bounds).toList())
				|| (modifierPickerPopup != null && modifierPickerPopup.contains(mouseX, mouseY))
				|| (modifierEditorPopup != null && modifierEditorPopup.contains(mouseX, mouseY))
				|| (colorPopup != null && colorPopup.contains(mouseX, mouseY))
				|| (gradientPopup != null && gradientPopup.contains(mouseX, mouseY));
	}

	private boolean isInsideField(double mouseX, double mouseY) {
		return getX() <= mouseX && mouseX <= getRight() && getY() <= mouseY && mouseY <= getBottom();
	}

	private void moveCaret(int offset, boolean extendSelection) {
		if (hasSelection() && !extendSelection) {
			if (offset < 0) {
				setCaret(Math.min(caretIndex, selectionAnchor), false);
			} else {
				setCaret(Math.max(caretIndex, selectionAnchor), false);
			}
			return;
		}

		setCaret(Math.clamp(caretIndex + offset, 0, model.size()), extendSelection);
	}

	private int getWordSkipPosition(int wordOffset) {
		return getWordSkipPosition(wordOffset, caretIndex, true);
	}

	private int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
		int index = Math.clamp(cursorPosition, 0, model.size());
		boolean backwards = wordOffset < 0;
		int steps = Math.abs(wordOffset);

		for (int step = 0; step < steps; step++) {
			if (backwards) {
				while (skipOverSpaces && index > 0 && isWhitespaceElement(index - 1)) {
					index--;
				}
				while (index > 0 && !isWhitespaceElement(index - 1)) {
					index--;
				}
			} else {
				while (index < model.size() && !isWhitespaceElement(index)) {
					index++;
				}
				while (skipOverSpaces && index < model.size() && isWhitespaceElement(index)) {
					index++;
				}
			}
		}

		return index;
	}

	private void setCaret(int index, boolean extendSelection) {
		caretIndex = Math.clamp(index, 0, model.size());
		if (!extendSelection) {
			selectionAnchor = caretIndex;
		}
		ensureCaretVisible();
		refreshOverlayLayout();
	}

	private void selectAll() {
		caretIndex = model.size();
		selectionAnchor = 0;
		ensureCaretVisible();
		refreshOverlayLayout();
	}

	private void setSelection(int start, int end) {
		selectionAnchor = Math.clamp(start, 0, model.size());
		caretIndex = Math.clamp(end, 0, model.size());
		ensureCaretVisible();
		refreshOverlayLayout();
	}

	private void selectWordAt(double mouseX) {
		Integer clickedElementIndex = getElementIndexAt(mouseX);
		if (clickedElementIndex != null && !isWhitespaceElement(clickedElementIndex)) {
			selectWord(clickedElementIndex);
			return;
		}

		int clickedIndex = getClosestCaretIndex(mouseX);
		setSelection(getWordSkipPosition(-1, clickedIndex, true), getWordSkipPosition(1, clickedIndex, true));
	}

	private void selectWord(int elementIndex) {
		if (model.isEmpty()) {
			setSelection(0, 0);
			return;
		}

		int index = Math.clamp(elementIndex, 0, model.size() - 1);
		if (isWhitespaceElement(index)) {
			setSelection(index, Math.min(model.size(), index + 1));
			return;
		}

		int start = index;
		while (start > 0 && !isWhitespaceElement(start - 1)) {
			start--;
		}

		int end = index + 1;
		while (end < model.size() && !isWhitespaceElement(end)) {
			end++;
		}

		setSelection(start, end);
	}

	private @Nullable Integer getElementIndexAt(double mouseX) {
		int relativeX = (int) mouseX - (getX() + TEXT_PADDING_X) + horizontalScroll;
		for (DisplayItem item : displayItems) {
			if (item.x() <= relativeX && relativeX <= item.endX()) {
				return item.modelIndex();
			}
		}
		return null;
	}

	private boolean isWhitespaceElement(int index) {
		if (index < 0 || index >= model.size()) {
			return false;
		}

		ModuleContentEditorModel.InlineElement element = model.get(index);
		return element instanceof ModuleContentEditorModel.TextElement textElement
				&& !textElement.text().isEmpty()
				&& Character.isWhitespace(textElement.text().charAt(0));
	}

	private void erase(int offset, boolean words) {
		if (words) {
			eraseWords(offset);
		} else if (offset < 0) {
			deleteBackward();
		} else if (offset > 0) {
			deleteForward();
		}
	}

	private void eraseWords(int wordOffset) {
		if (hasSelection()) {
			deleteSelection();
			return;
		}

		int targetIndex = getWordSkipPosition(wordOffset);
		if (targetIndex == caretIndex) {
			return;
		}

		applyMutation(() -> {
			int start = Math.min(caretIndex, targetIndex);
			int end = Math.max(caretIndex, targetIndex);
			model.deleteRange(start, end);
			caretIndex = start;
			selectionAnchor = start;
		});
	}

	private void copySelectionToClipboard() {
		CLIENT.keyboard.setClipboard(getSelectedRawText());
	}

	private void cutSelectionToClipboard() {
		copySelectionToClipboard();
		deleteSelection();
	}

	private String getSelectedRawText() {
		if (!hasSelection()) {
			return "";
		}

		int start = Math.min(caretIndex, selectionAnchor);
		int end = Math.max(caretIndex, selectionAnchor);
		return model.copyRange(start, end).serialize();
	}

	private void deleteBackward() {
		if (hasSelection()) {
			deleteSelection();
			return;
		}

		if (caretIndex <= 0) {
			return;
		}

		applyMutation(() -> {
			model.deleteRange(caretIndex - 1, caretIndex);
			caretIndex--;
			selectionAnchor = caretIndex;
		});
	}

	private void deleteForward() {
		if (hasSelection()) {
			deleteSelection();
			return;
		}

		if (caretIndex >= model.size()) {
			return;
		}

		applyMutation(() -> model.deleteRange(caretIndex, caretIndex + 1));
	}

	private void deleteSelection() {
		if (!hasSelection()) {
			return;
		}

		int start = Math.min(caretIndex, selectionAnchor);
		int end = Math.max(caretIndex, selectionAnchor);
		applyMutation(() -> {
			model.deleteRange(start, end);
			caretIndex = start;
			selectionAnchor = start;
		});
	}

	public void write(String text) {
		ModuleContentEditorModel.StyleState insertionStyle = model.getInsertionStyle(Math.min(caretIndex, selectionAnchor));
		ModuleContentEditorModel fragment = ModuleContentEditorModel.parse(text, insertionStyle);
		applyMutation(() -> {
			if (hasSelection()) {
				int start = Math.min(caretIndex, selectionAnchor);
				int end = Math.max(caretIndex, selectionAnchor);
				model.deleteRange(start, end);
				caretIndex = start;
				selectionAnchor = start;
			}

			model.insertModel(caretIndex, fragment);
			caretIndex += fragment.size();
			selectionAnchor = caretIndex;
		});
	}

	private void applyMutation(Runnable mutation) {
		ModuleContentEditorModel before = model.copy();
		int beforeCaret = caretIndex;
		int beforeAnchor = selectionAnchor;
		String beforeRaw = rawText;

		mutation.run();

		String serialized = model.serialize();
		if (serialized.length() > maxLength) {
			model = before;
			caretIndex = beforeCaret;
			selectionAnchor = beforeAnchor;
			rawText = beforeRaw;
			rebuildLayout();
			return;
		}

		rawText = serialized;
		rebuildLayout();
		changedListener.accept(rawText);
	}

	private boolean hasSelection() {
		return caretIndex != selectionAnchor;
	}

	private boolean isIndexSelected(int index) {
		int start = Math.min(caretIndex, selectionAnchor);
		int end = Math.max(caretIndex, selectionAnchor);
		return start <= index && index < end;
	}

	private int getClosestCaretIndex(double mouseX) {
		int relativeX = (int) mouseX - (getX() + TEXT_PADDING_X) + horizontalScroll;
		for (DisplayItem item : displayItems) {
			if (relativeX <= item.x() + item.width() / 2) {
				return item.modelIndex();
			}
			if (relativeX <= item.endX()) {
				return item.modelIndex() + 1;
			}
		}
		return model.size();
	}

	private void ensureCaretVisible() {
		int innerWidth = getWidth() - TEXT_PADDING_X * 2;
		int caretX = caretPositions[Math.clamp(caretIndex, 0, caretPositions.length - 1)];
		if (caretX - horizontalScroll < 0) {
			horizontalScroll = caretX;
		} else if (caretX - horizontalScroll > innerWidth - 1) {
			horizontalScroll = caretX - innerWidth + 1;
		}
		horizontalScroll = Math.max(0, Math.min(horizontalScroll, Math.max(0, contentWidth - innerWidth)));
	}

	private void rebuildLayout() {
		List<ModuleContentEditorModel.InlineElement> elements = model.elements();
		Map<ModuleContentEditorModel.GradientColorLayer, GradientRegion> gradientRegions = new LinkedHashMap<>();
		displayItems = new ArrayList<>(elements.size());
		caretPositions = new int[elements.size() + 1];
		caretPositions[0] = 0;

		int x = 0;
		for (int index = 0; index < elements.size(); index++) {
			ModuleContentEditorModel.InlineElement element = elements.get(index);
			int resolvedColor = resolveColor(element, index, elements, gradientRegions);
			Style style = toMinecraftStyle(element.style(), resolvedColor);

			DisplayItem displayItem;
			if (element instanceof ModuleContentEditorModel.TextElement textElement) {
				int width = CLIENT.textRenderer.getWidth(Text.literal(textElement.text()).setStyle(style));
				displayItem = new TextDisplayItem(index, x, Math.max(width, 1), CLIENT.textRenderer.fontHeight, textElement.text(), resolvedColor);
			} else {
				displayItem = buildVariableDisplayItem(index, x, (ModuleContentEditorModel.VariableElement) element, style, resolvedColor);
			}

			displayItems.add(displayItem);
			x += displayItem.width();
			caretPositions[index + 1] = x;
		}

		contentWidth = x;
		sanitizeTransientState();
		ensureCaretVisible();
		refreshOverlayLayout();
	}

	private int resolveColor(ModuleContentEditorModel.InlineElement element,
	                         int index,
	                         List<ModuleContentEditorModel.InlineElement> elements,
	                         Map<ModuleContentEditorModel.GradientColorLayer, GradientRegion> gradientRegions) {
		List<ModuleContentEditorModel.ColorLayer> colorLayers = element.style().colorLayers();
		if (colorLayers.isEmpty()) {
			return TEXT_COLOR;
		}

		ModuleContentEditorModel.ColorLayer topLayer = colorLayers.getLast();
		if (topLayer instanceof ModuleContentEditorModel.StaticColorLayer staticColorLayer) {
			return 0xff000000 | staticColorLayer.rgb();
		}
		if (topLayer == ModuleContentEditorModel.ChromaColorLayer.INSTANCE) {
			return ChromaColorTickable.getColor();
		}

		ModuleContentEditorModel.GradientColorLayer gradientColorLayer = (ModuleContentEditorModel.GradientColorLayer) topLayer;
		GradientRegion region = gradientRegions.computeIfAbsent(gradientColorLayer, layer -> GradientRegion.create(layer, elements, this::measureElementWidth));
		return 0xff000000 | region.colorAt(index);
	}

	private int measureElementWidth(ModuleContentEditorModel.InlineElement element) {
		Style style = toMinecraftStyle(element.style(), TEXT_COLOR);
		if (element instanceof ModuleContentEditorModel.TextElement textElement) {
			return Math.max(1, CLIENT.textRenderer.getWidth(Text.literal(textElement.text()).setStyle(style)));
		}
		return buildVariableDisplayItem(0, 0, (ModuleContentEditorModel.VariableElement) element, style, TEXT_COLOR).width();
	}

	private VariableDisplayItem buildVariableDisplayItem(int modelIndex, int x, ModuleContentEditorModel.VariableElement variableElement, Style style, int color) {
		String name = variableElement.variable().getName().getString();
		int nameWidth = CLIENT.textRenderer.getWidth(Text.literal(name).setStyle(style));

		List<ModifierPart> modifiers = new ArrayList<>();
		int currentX = VARIABLE_PADDING_X + nameWidth + VARIABLE_GAP;
		for (int modifierIndex = 0; modifierIndex < variableElement.modifiers().size(); modifierIndex++) {
			Modifiers.ResolvedModifier<?, ?> modifier = variableElement.modifiers().get(modifierIndex);
			String displaySuffix = modifier.modifier().uiMetadata().displayFormatter().apply(modifier.arguments());
			String displayName = modifier.modifier().uiMetadata().getName(modifier.modifier().key()).getString();
			String displayText = displaySuffix.isBlank() ? displayName : displayName + "(" + displaySuffix + ")";
			int textWidth = CLIENT.textRenderer.getWidth(Text.literal(displayText).setStyle(style));
			int width = textWidth + MODIFIER_PADDING_X * 2;
			modifiers.add(new ModifierPart(displayText, width, color, modifier.modifier().uiMetadata().getDescription(modifier.modifier().key()), currentX, modifierIndex));
			currentX += width;
			if (modifierIndex < variableElement.modifiers().size() - 1) {
				currentX += MODIFIER_SEPARATOR_GAP;
			}
		}

		int width = VARIABLE_PADDING_X * 2 + nameWidth;
		if (!modifiers.isEmpty()) {
			width += VARIABLE_GAP;
			width += modifiers.stream().mapToInt(ModifierPart::width).sum();
			width += (modifiers.size() - 1) * MODIFIER_SEPARATOR_GAP;
		}
		width += VARIABLE_PLUS_GAP + CHIP_PLUS_WIDTH;
		int height = CLIENT.textRenderer.fontHeight + VARIABLE_PADDING_Y * 2;
		int plusX = width - VARIABLE_PADDING_X - CHIP_PLUS_WIDTH;

		return new VariableDisplayItem(modelIndex, x, width, height, color, variableElement, name, nameWidth, List.copyOf(modifiers), plusX);
	}

	private Style toMinecraftStyle(ModuleContentEditorModel.StyleState style, int color) {
		Style minecraftStyle = Style.EMPTY
				.withBold(style.bold())
				.withItalic(style.italic())
				.withUnderline(style.underline())
				.withStrikethrough(style.strikethrough())
				.withObfuscated(style.obfuscated())
				.withColor(TextColor.fromRgb(color & 0x00ffffff));
		return minecraftStyle;
	}

	private void refreshOverlayLayout() {
		selectionBounds = computeSelectionBounds();
		toolbarButtons = buildToolbarButtons(selectionBounds);

		if (modifierPickerPopup != null) {
			VariableDisplayItem variableItem = findVariableDisplayItem(modifierPickerPopup.elementIndex());
			if (variableItem != null) {
				modifierPickerPopup.layout(variableItem);
			}
		}

		if (modifierEditorPopup != null) {
			VariableDisplayItem variableItem = findVariableDisplayItem(modifierEditorPopup.elementIndex());
			if (variableItem != null) {
				modifierEditorPopup.layout(variableItem);
			}
		}

		if (colorPopup != null && selectionBounds != null) {
			colorPopup.layout(selectionBounds);
		}
		if (gradientPopup != null && selectionBounds != null) {
			gradientPopup.layout(selectionBounds);
		}
	}

	private @Nullable SelectionBounds computeSelectionBounds() {
		if (!hasSelection()) {
			return null;
		}

		int left = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int top = Integer.MAX_VALUE;
		int bottom = Integer.MIN_VALUE;
		int innerLeft = getX() + TEXT_PADDING_X;
		int contentTextY = getContentTextY();

		for (DisplayItem item : displayItems) {
			if (!isIndexSelected(item.modelIndex())) {
				continue;
			}
			int drawX = innerLeft + item.x() - horizontalScroll;
			left = Math.min(left, drawX);
			right = Math.max(right, drawX + item.width());
			top = Math.min(top, getDisplayItemTop(item, contentTextY));
			bottom = Math.max(bottom, getDisplayItemBottom(item, contentTextY));
		}

		if (left == Integer.MAX_VALUE || top == Integer.MAX_VALUE) {
			return null;
		}

		return new SelectionBounds(left, top, right, bottom);
	}

	private List<ToolbarButton> buildToolbarButtons(@Nullable SelectionBounds bounds) {
		if (bounds == null) {
			return List.of();
		}

		ModuleContentEditorModel.SelectionSummary summary = model.summarize(Math.min(caretIndex, selectionAnchor), Math.max(caretIndex, selectionAnchor));
		List<ToolbarButton> buttons = new ArrayList<>();
		int width = TOOLBAR_BUTTON_WIDTH * 5 + TOOLBAR_ICON_BUTTON_WIDTH * 2 + POPUP_GAP * 6 + POPUP_PADDING * 2;
		int x = clampX(bounds.centerX() - width / 2, width);
		int y = bounds.top() - BUTTON_HEIGHT - POPUP_PADDING * 2 - OVERLAY_GAP;
		if (y < 4) {
			y = bounds.bottom() + OVERLAY_GAP;
		}
		y = clampY(y, BUTTON_HEIGHT + POPUP_PADDING * 2);

		Bounds panelBounds = new Bounds(x, y, width, BUTTON_HEIGHT + POPUP_PADDING * 2);
		int cursorX = panelBounds.x() + POPUP_PADDING;
		int buttonY = panelBounds.y() + POPUP_PADDING;

		buttons.add(new ToolbarButton(ToolbarAction.NONE, panelBounds, Text.empty(), Text.empty(), ButtonState.NORMAL, null));
		buttons.add(new ToolbarButton(ToolbarAction.BOLD, new Bounds(cursorX, buttonY, TOOLBAR_BUTTON_WIDTH, BUTTON_HEIGHT), Text.literal("B"), Text.translatable("flex_hud.create_module_screen.editor.toolbar.bold"), triStateToButtonState(summary.bold()), null));
		cursorX += TOOLBAR_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.ITALIC, new Bounds(cursorX, buttonY, TOOLBAR_BUTTON_WIDTH, BUTTON_HEIGHT), Text.literal("I"), Text.translatable("flex_hud.create_module_screen.editor.toolbar.italic"), triStateToButtonState(summary.italic()), null));
		cursorX += TOOLBAR_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.UNDERLINE, new Bounds(cursorX, buttonY, TOOLBAR_BUTTON_WIDTH, BUTTON_HEIGHT), Text.literal("U"), Text.translatable("flex_hud.create_module_screen.editor.toolbar.underline"), triStateToButtonState(summary.underline()), null));
		cursorX += TOOLBAR_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.STRIKETHROUGH, new Bounds(cursorX, buttonY, TOOLBAR_BUTTON_WIDTH, BUTTON_HEIGHT), Text.literal("S"), Text.translatable("flex_hud.create_module_screen.editor.toolbar.strikethrough"), triStateToButtonState(summary.strikethrough()), null));
		cursorX += TOOLBAR_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.OBFUSCATED, new Bounds(cursorX, buttonY, TOOLBAR_BUTTON_WIDTH, BUTTON_HEIGHT), Text.literal("O"), Text.translatable("flex_hud.create_module_screen.editor.toolbar.obfuscated"), triStateToButtonState(summary.obfuscated()), null));
		cursorX += TOOLBAR_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.COLOR, new Bounds(cursorX, buttonY, TOOLBAR_ICON_BUTTON_WIDTH, BUTTON_HEIGHT), Text.translatable("flex_hud.create_module_screen.editor.toolbar.color_short"), Text.translatable("flex_hud.create_module_screen.editor.toolbar.color"), buttonState(summary.colorSummary().kind() == ModuleContentEditorModel.ColorSummaryKind.STATIC || summary.colorSummary().kind() == ModuleContentEditorModel.ColorSummaryKind.CHROMA, summary.colorSummary().kind() == ModuleContentEditorModel.ColorSummaryKind.MIXED), summary.colorSummary()));
		cursorX += TOOLBAR_ICON_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.GRADIENT, new Bounds(cursorX, buttonY, TOOLBAR_ICON_BUTTON_WIDTH, BUTTON_HEIGHT), Text.translatable("flex_hud.create_module_screen.editor.toolbar.gradient_short"), Text.translatable("flex_hud.create_module_screen.editor.toolbar.gradient"), buttonState(summary.colorSummary().kind() == ModuleContentEditorModel.ColorSummaryKind.GRADIENT, summary.colorSummary().kind() == ModuleContentEditorModel.ColorSummaryKind.MIXED), summary.colorSummary()));
		return List.copyOf(buttons);
	}

	private ButtonState triStateToButtonState(ModuleContentEditorModel.TriState state) {
		return switch (state) {
			case ON -> ButtonState.ACTIVE;
			case MIXED -> ButtonState.MIXED;
			case OFF -> ButtonState.NORMAL;
		};
	}

	private ButtonState buttonState(boolean active, boolean mixed) {
		if (mixed) {
			return ButtonState.MIXED;
		}
		return active ? ButtonState.ACTIVE : ButtonState.NORMAL;
	}

	private @Nullable ToolbarButton findToolbarButton(double mouseX, double mouseY) {
		for (int i = 1; i < toolbarButtons.size(); i++) {
			ToolbarButton button = toolbarButtons.get(i);
			if (button.bounds().contains(mouseX, mouseY)) {
				return button;
			}
		}
		return null;
	}

	private void handleToolbarAction(ToolbarAction action) {
		switch (action) {
			case BOLD -> toggleSelectionStyle(ModuleContentEditorModel.StyleFlag.BOLD);
			case ITALIC -> toggleSelectionStyle(ModuleContentEditorModel.StyleFlag.ITALIC);
			case UNDERLINE -> toggleSelectionStyle(ModuleContentEditorModel.StyleFlag.UNDERLINE);
			case STRIKETHROUGH -> toggleSelectionStyle(ModuleContentEditorModel.StyleFlag.STRIKETHROUGH);
			case OBFUSCATED -> toggleSelectionStyle(ModuleContentEditorModel.StyleFlag.OBFUSCATED);
			case COLOR -> toggleColorPopup();
			case GRADIENT -> toggleGradientPopup();
			case NONE -> {
			}
		}
	}

	private void toggleSelectionStyle(ModuleContentEditorModel.StyleFlag styleFlag) {
		if (!hasSelection()) {
			return;
		}

		int start = Math.min(caretIndex, selectionAnchor);
		int end = Math.max(caretIndex, selectionAnchor);
		ModuleContentEditorModel.SelectionSummary summary = model.summarize(start, end);
		boolean enable = switch (styleFlag) {
			case BOLD -> summary.bold() != ModuleContentEditorModel.TriState.ON;
			case ITALIC -> summary.italic() != ModuleContentEditorModel.TriState.ON;
			case UNDERLINE -> summary.underline() != ModuleContentEditorModel.TriState.ON;
			case STRIKETHROUGH -> summary.strikethrough() != ModuleContentEditorModel.TriState.ON;
			case OBFUSCATED -> summary.obfuscated() != ModuleContentEditorModel.TriState.ON;
		};

		applyMutation(() -> model.setStyleFlag(start, end, styleFlag, enable));
	}

	private void toggleColorPopup() {
		if (!hasSelection()) {
			return;
		}

		if (colorPopup != null) {
			colorPopup = null;
			return;
		}

		gradientPopup = null;
		int selectionStart = Math.min(caretIndex, selectionAnchor);
		int selectionEnd = Math.max(caretIndex, selectionAnchor);
		ModuleContentEditorModel.ColorSummary colorSummary = model.summarize(selectionStart, selectionEnd).colorSummary();
		int initialColor = colorSummary.primaryColor() != null ? colorSummary.primaryColor() : 0xffffff;
		colorPopup = new ColorPopup(selectionStart, selectionEnd, initialColor);
		refreshOverlayLayout();
	}

	private void toggleGradientPopup() {
		if (!hasSelection()) {
			return;
		}

		if (gradientPopup != null) {
			gradientPopup = null;
			return;
		}

		colorPopup = null;
		int selectionStart = Math.min(caretIndex, selectionAnchor);
		int selectionEnd = Math.max(caretIndex, selectionAnchor);
		ModuleContentEditorModel.ColorSummary colorSummary = model.summarize(selectionStart, selectionEnd).colorSummary();
		int startColor = colorSummary.primaryColor() != null ? colorSummary.primaryColor() : 0xffffff;
		int endColor = colorSummary.secondaryColor() != null ? colorSummary.secondaryColor() : 0x55ffff;
		gradientPopup = new GradientPopup(selectionStart, selectionEnd, startColor, endColor);
		refreshOverlayLayout();
	}

	private void applySelectionColorLayers(int selectionStart, int selectionEnd, List<ModuleContentEditorModel.ColorLayer> colorLayers) {
		if (selectionStart >= selectionEnd) {
			return;
		}
		applyMutation(() -> model.replaceColorLayers(selectionStart, selectionEnd, colorLayers));
	}

	private void sanitizeTransientState() {
		if (modifierPickerPopup != null && findVariableDisplayItem(modifierPickerPopup.elementIndex()) == null) {
			modifierPickerPopup = null;
		}
		if (modifierEditorPopup != null && findVariableDisplayItem(modifierEditorPopup.elementIndex()) == null) {
			modifierEditorPopup = null;
		}
		if (colorPopup != null && !colorPopup.matchesSelection()) {
			colorPopup = null;
		}
		if (gradientPopup != null && !gradientPopup.matchesSelection()) {
			gradientPopup = null;
		}
	}

	private void closeTransientPopups() {
		closeModifierPopups();
		closeSelectionPopups();
	}

	private void closeModifierPopups() {
		modifierPickerPopup = null;
		modifierEditorPopup = null;
	}

	private void closeSelectionPopups() {
		colorPopup = null;
		gradientPopup = null;
	}

	private void closeModifierPicker() {
		modifierPickerPopup = null;
	}

	private void renderButtonCenterLabel(DrawContext context, Bounds bounds, Text label, int backgroundColor, int textColor, double mouseX, double mouseY) {
		if (!label.getString().isEmpty()) {
			context.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), backgroundColor);
			context.drawStrokedRectangle(bounds.x(), bounds.y(), bounds.width(), bounds.height(), POPUP_BORDER);
			int textX = bounds.x() + (bounds.width() - CLIENT.textRenderer.getWidth(label)) / 2;
			int textY = centeredTextY(bounds.y(), bounds.height());
			context.drawText(CLIENT.textRenderer, label, textX, textY, textColor, false);

			if (bounds.contains(mouseX, mouseY)) {
				context.setCursor(StandardCursors.POINTING_HAND);
			}
		} else {
			context.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), POPUP_BACKGROUND);
			context.drawStrokedRectangle(bounds.x(), bounds.y(), bounds.width(), bounds.height(), POPUP_BORDER);
		}
	}

	private void renderButton(DrawContext context, Bounds bounds, Text label, int backgroundColor, int textColor, int padding, double mouseX, double mouseY) {
		if (!label.getString().isEmpty()) {
			context.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), backgroundColor);
			context.drawStrokedRectangle(bounds.x(), bounds.y(), bounds.width(), bounds.height(), POPUP_BORDER);
			int textX = bounds.x() + padding;
			int textY = centeredTextY(bounds.y(), bounds.height());
			context.drawText(CLIENT.textRenderer, label, textX, textY, textColor, false);

			if (bounds.contains(mouseX, mouseY)) {
				context.setCursor(StandardCursors.POINTING_HAND);
			}
		} else {
			context.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), POPUP_BACKGROUND);
			context.drawStrokedRectangle(bounds.x(), bounds.y(), bounds.width(), bounds.height(), POPUP_BORDER);
		}
	}

	private void renderPanel(DrawContext context, Bounds bounds) {
		context.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), POPUP_BACKGROUND);
		context.drawStrokedRectangle(bounds.x(), bounds.y(), bounds.width(), bounds.height(), POPUP_BORDER);
	}

	private int clampX(int x, int width) {
		Screen screen = CLIENT.currentScreen;
		if (screen == null) {
			return x;
		}
		return Math.clamp(x, 4, Math.max(4, screen.width - width - 4));
	}

	private int clampY(int y, int height) {
		Screen screen = CLIENT.currentScreen;
		if (screen == null) {
			return y;
		}
		return Math.clamp(y, 4, Math.max(4, screen.height - height - 4));
	}

	private boolean containsBounds(double mouseX, double mouseY, List<Bounds> boundsList) {
		for (Bounds bounds : boundsList) {
			if (bounds.contains(mouseX, mouseY)) {
				return true;
			}
		}
		return false;
	}

	private @Nullable VariableHit findVariableHit(double mouseX, double mouseY) {
		int innerLeft = getX() + TEXT_PADDING_X;
		int contentTextY = getContentTextY();
		for (DisplayItem item : displayItems) {
			if (!(item instanceof VariableDisplayItem variableDisplayItem)) {
				continue;
			}

			int drawX = innerLeft + item.x() - horizontalScroll;
			if (mouseY < getDisplayItemTop(item, contentTextY) || mouseY > getDisplayItemBottom(item, contentTextY)) {
				continue;
			}
			if (mouseX < drawX || mouseX > drawX + item.width()) {
				continue;
			}

			int localX = (int) mouseX - drawX;
			if (variableDisplayItem.plusX() <= localX && localX <= variableDisplayItem.plusX() + CHIP_PLUS_WIDTH) {
				return new VariableHit(VariableHitKind.PLUS, variableDisplayItem, -1);
			}

			for (ModifierPart modifierPart : variableDisplayItem.modifiers()) {
				if (modifierPart.startX() <= localX && localX <= modifierPart.startX() + modifierPart.width()) {
					return new VariableHit(VariableHitKind.MODIFIER, variableDisplayItem, modifierPart.index());
				}
			}

			return new VariableHit(VariableHitKind.BODY, variableDisplayItem, -1);
		}
		return null;
	}

	private void openModifierPicker(VariableDisplayItem variableItem) {
		modifierEditorPopup = null;
		closeSelectionPopups();
		modifierPickerPopup = new ModifierPickerPopup(variableItem.modelIndex());
		modifierPickerPopup.layout(variableItem);
	}

	private void openModifierEditor(VariableDisplayItem variableItem, int modifierIndex) {
		closeModifierPicker();
		closeSelectionPopups();
		modifierEditorPopup = new ModifierEditorPopup(variableItem.modelIndex(), modifierIndex);
		modifierEditorPopup.layout(variableItem);
	}

	private void handleBodyClick(Click click, VariableDisplayItem variableItem) {
		closeModifierPopups();
		closeSelectionPopups();
		int drawX = getX() + TEXT_PADDING_X + variableItem.x() - horizontalScroll;
		int clickedIndex = click.x() < drawX + variableItem.width() / 2.0 ? variableItem.modelIndex() : variableItem.modelIndex() + 1;
		if ((click.modifiers() & GLFW.GLFW_MOD_SHIFT) != 0) {
			caretIndex = clickedIndex;
		} else {
			caretIndex = clickedIndex;
			selectionAnchor = clickedIndex;
		}
		draggingSelection = true;
		refreshOverlayLayout();
	}

	private @Nullable VariableDisplayItem findVariableDisplayItem(int elementIndex) {
		for (DisplayItem item : displayItems) {
			if (item.modelIndex() == elementIndex && item instanceof VariableDisplayItem variableDisplayItem) {
				return variableDisplayItem;
			}
		}
		return null;
	}

	private List<Modifier<?, ?>> getCompatibleModifiers(ModuleContentEditorModel.VariableElement variableElement) {
		List<Modifier<?, ?>> compatible = new ArrayList<>();
		for (Modifier<?, ?> modifier : Modifiers.getAll()) {
			List<Modifiers.ResolvedModifier<?, ?>> candidateModifiers = new ArrayList<>(variableElement.modifiers());
			candidateModifiers.add(defaultResolvedModifier(modifier));
			if (isCompatibleModifierChain(variableElement.variable(), candidateModifiers)) {
				compatible.add(modifier);
			}
		}
		return compatible;
	}

	private boolean isCompatibleModifierChain(Variable<?> variable, List<Modifiers.ResolvedModifier<?, ?>> modifiers) {
		Object value = variable.getValue();
		Class<?> inputType = value != null ? value.getClass() : String.class;
		return Modifiers.compileFormatter(inputType, modifiers) != null;
	}

	private Modifiers.ResolvedModifier<?, ?> defaultResolvedModifier(Modifier<?, ?> modifier) {
		List<String> arguments = defaultArguments(modifier);
		String raw = modifier.uiMetadata().rawFormatter().apply(arguments);
		Modifiers.ResolvedModifier<?, ?> resolvedModifier = Modifiers.get(raw);
		return resolvedModifier != null ? resolvedModifier : new Modifiers.ResolvedModifier<>(modifier, arguments, raw);
	}

	private List<String> defaultArguments(Modifier<?, ?> modifier) {
		if (modifier.uiMetadata().editorKind() == Modifier.EditorKind.CONDITIONAL_BRANCHES) {
			return List.of("if_gt", "0", "");
		}

		List<String> arguments = new ArrayList<>(modifier.uiMetadata().parameters().size());
		for (Modifier.ParameterDefinition parameter : modifier.uiMetadata().parameters()) {
			arguments.add(defaultValue(modifier, parameter));
		}
		return arguments;
	}

	private String defaultValue(Modifier<?, ?> modifier, Modifier.ParameterDefinition parameter) {
		if (parameter.kind() == Modifier.ParameterKind.DECIMAL && modifier.key().equals("div")) {
			return "1";
		}

		return switch (parameter.kind()) {
			case INTEGER, DECIMAL -> "0";
			case CHARACTER -> " ";
			case CONDITIONAL_BRANCHES, TEXT -> "";
		};
	}

	private static boolean isUnsignedIntegerInput(String text) {
		return UNSIGNED_INTEGER_INPUT.matcher(text).matches();
	}

	private static boolean isUnsignedTwoDigitIntegerInput(String text) {
		return UNSIGNED_TWO_DIGIT_INTEGER_INPUT.matcher(text).matches();
	}

	private static boolean isSignedIntegerInput(String text) {
		return SIGNED_INTEGER_INPUT.matcher(text).matches();
	}

	private static boolean isSignedNonZeroIntegerInput(String text) {
		return text.isEmpty()
				|| text.equals("-")
				|| SIGNED_NON_ZERO_INTEGER_INPUT.matcher(text).matches();
	}

	private void applyModifierChange(int elementIndex, @Nullable Integer modifierIndex, Modifiers.ResolvedModifier<?, ?> newModifier, boolean deleteModifier) {
		if (!(model.get(elementIndex) instanceof ModuleContentEditorModel.VariableElement variableElement)) {
			return;
		}

		List<Modifiers.ResolvedModifier<?, ?>> modifiers = new ArrayList<>(variableElement.modifiers());
		if (deleteModifier) {
			if (modifierIndex != null && 0 <= modifierIndex && modifierIndex < modifiers.size()) {
				modifiers.remove((int) modifierIndex);
			}
		} else if (modifierIndex == null) {
			if (newModifier != null) {
				modifiers.add(newModifier);
			}
		} else if (newModifier != null && 0 <= modifierIndex && modifierIndex < modifiers.size()) {
			modifiers.set(modifierIndex, newModifier);
		}

		if (!isCompatibleModifierChain(variableElement.variable(), modifiers)) {
			if (modifierEditorPopup != null) {
				modifierEditorPopup.setError(Text.translatable("flex_hud.create_module_screen.editor.invalid_modifier"));
			}
			return;
		}

		applyMutation(() -> model.updateVariableModifiers(elementIndex, modifiers));
		modifierPickerPopup = null;
		modifierEditorPopup = null;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public boolean hasChanged() {
		return initialContent == null ? !rawText.isEmpty() : !rawText.equals(initialContent);
	}

	@Override
	public boolean isValid() {
		return !rawText.isEmpty();
	}

	@Override
	public void cancel() {
		String restored = initialContent == null ? "" : initialContent;
		rawText = restored;
		model = ModuleContentEditorModel.parse(restored);
		caretIndex = model.size();
		selectionAnchor = caretIndex;
		closeTransientPopups();
		rebuildLayout();
		changedListener.accept(rawText);
	}

	public String getText() {
		return rawText;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setChangedListener(Consumer<String> changedListener) {
		this.changedListener = changedListener;
	}

	private sealed interface DisplayItem permits TextDisplayItem, VariableDisplayItem {
		int modelIndex();

		int x();

		int width();

		int height();

		default int endX() {
			return x() + width();
		}
	}

	private record TextDisplayItem(int modelIndex, int x, int width, int height, String text,
	                               int color) implements DisplayItem {
	}

	private record VariableDisplayItem(int modelIndex,
	                                   int x,
	                                   int width,
	                                   int height,
	                                   int color,
	                                   ModuleContentEditorModel.VariableElement element,
	                                   String name,
	                                   int nameWidth,
	                                   List<ModifierPart> modifiers,
	                                   int plusX) implements DisplayItem {
	}

	private record ModifierPart(String displayText, int width, int color, Text tooltip, int startX, int index) {
	}

	private record HoverTarget(Text tooltip) {
	}

	private record SelectionBounds(int left, int top, int right, int bottom) {
		int centerX() {
			return (left + right) / 2;
		}
	}

	private record Bounds(int x, int y, int width, int height) {
		int right() {
			return x + width;
		}

		int bottom() {
			return y + height;
		}

		boolean contains(double mouseX, double mouseY) {
			return x <= mouseX && mouseX <= right() && y <= mouseY && mouseY <= bottom();
		}
	}

	private enum ButtonState {
		NORMAL,
		ACTIVE,
		MIXED
	}

	private enum ToolbarAction {
		NONE,
		BOLD,
		ITALIC,
		UNDERLINE,
		STRIKETHROUGH,
		OBFUSCATED,
		COLOR,
		GRADIENT
	}

	private record ToolbarButton(ToolbarAction action,
	                             Bounds bounds,
	                             Text label,
	                             Text tooltip,
	                             ButtonState state,
	                             ModuleContentEditorModel.ColorSummary colorSummary) {
		private int backgroundColor(int mouseX, int mouseY) {
			boolean hovered = bounds.contains(mouseX, mouseY);
			if (action == ToolbarAction.NONE) {
				return POPUP_BACKGROUND;
			}
			if (hovered) {
				return BUTTON_HOVERED_BACKGROUND;
			}
			return switch (state) {
				case ACTIVE -> BUTTON_ACTIVE_BACKGROUND;
				case MIXED -> BUTTON_MIXED_BACKGROUND;
				case NORMAL -> BUTTON_BACKGROUND;
			};
		}
	}

	private enum VariableHitKind {
		BODY,
		MODIFIER,
		PLUS
	}

	private record VariableHit(VariableHitKind kind, VariableDisplayItem variableItem, int modifierIndex) {
	}

	private record ModifierPickerEntry(Modifier<?, ?> modifier, Bounds bounds, Text tooltip) {
	}

	private final class ModifierPickerPopup {
		private static final int BUTTON_PADDING = 2;
		private static final int ROW_GAP = 1;
		private static final int MAX_HEIGHT = 180;
		private static final int SCROLLBAR_WIDTH = 4;
		private static final int SCROLLBAR_MARGIN = 3;

		private final int elementIndex;
		private Bounds bounds = new Bounds(0, 0, 0, 0);
		private List<ModifierPickerEntry> entries = List.of();
		private int contentHeight = 0;
		private int viewportHeight = 0; // hauteur dispo pour les entries
		private boolean isDraggingScrollbar = false;
		private double dragStartMouseY = 0;
		private double dragStartScrollOffset = 0;

		private double targetScroll = 0; // Target scroll amount (set by mouse wheel)
		private double currentScroll = 0; // Interpolated scroll amount (used for rendering)
		private final double SCROLL_SPEED = 25.0; // Pixels per notch
		private long lastUpdateTime = System.nanoTime();

		private ModifierPickerPopup(int elementIndex) {
			this.elementIndex = elementIndex;
		}

		private int elementIndex() {
			return elementIndex;
		}

		private boolean needsScrollbar() {
			return contentHeight > viewportHeight;
		}

		private int maxScroll() {
			return Math.max(0, contentHeight - viewportHeight);
		}

		private void setScrollPosition(double scrollPosition) {
			double clamped = MathHelper.clamp(scrollPosition, 0.0, maxScroll());
			currentScroll = clamped;
			targetScroll = clamped;
		}

		private void layout(VariableDisplayItem variableItem) {
			List<Modifier<?, ?>> compatibleModifiers = getCompatibleModifiers(variableItem.element());

			int titleHeight = CLIENT.textRenderer.fontHeight;
			int rowHeight = CLIENT.textRenderer.fontHeight + 4;

			int width = CLIENT.textRenderer.getWidth(Text.translatable("flex_hud.create_module_screen.editor.add_modifier")) + POPUP_PADDING * 2;
			for (Modifier<?, ?> modifier : compatibleModifiers) {
				width = Math.max(width, CLIENT.textRenderer.getWidth(modifier.uiMetadata().getName(modifier.key())) + BUTTON_PADDING * 2 + POPUP_PADDING * 2);
			}

			// Hauteur totale du contenu des entries
			contentHeight = compatibleModifiers.isEmpty()
					? rowHeight
					: compatibleModifiers.size() * (rowHeight + ROW_GAP) - ROW_GAP;

			int headerHeight = POPUP_PADDING + titleHeight + POPUP_GAP;
			int footerHeight = POPUP_PADDING;
			int maxViewportHeight = MAX_HEIGHT - headerHeight - footerHeight;
			viewportHeight = Math.min(contentHeight, maxViewportHeight);

			int totalHeight = headerHeight + viewportHeight + footerHeight;

			// On réserve de la place pour la scrollbar si besoin
			if (needsScrollbar()) {
				width += SCROLLBAR_WIDTH + SCROLLBAR_MARGIN;
			}

			int preferredX = getX() + TEXT_PADDING_X + variableItem.x() - horizontalScroll;
			int preferredY = getBottom() + OVERLAY_GAP;
			Screen screen = CLIENT.currentScreen;
			if (screen != null && preferredY + totalHeight > screen.height - 4) {
				preferredY = getY() - totalHeight - OVERLAY_GAP;
			}

			bounds = new Bounds(clampX(preferredX, width), clampY(preferredY, totalHeight), width, totalHeight);
			currentScroll = MathHelper.clamp(currentScroll, 0.0, maxScroll());
			targetScroll = MathHelper.clamp(targetScroll, 0.0, maxScroll());

			// Build entries (positions relatives au début du contenu, avant scroll)
			List<ModifierPickerEntry> builtEntries = new ArrayList<>();
			int entryY = 0;
			int entryWidth = bounds.width() - POPUP_PADDING * 2 - (needsScrollbar() ? SCROLLBAR_WIDTH + SCROLLBAR_MARGIN : 0);
			for (Modifier<?, ?> modifier : compatibleModifiers) {
				builtEntries.add(new ModifierPickerEntry(
						modifier,
						new Bounds(bounds.x() + POPUP_PADDING, entryY, entryWidth, rowHeight),
						modifier.uiMetadata().getDescription(modifier.key())
				));
				entryY += rowHeight + ROW_GAP;
			}
			entries = List.copyOf(builtEntries);
		}

		private int entryRenderY(ModifierPickerEntry entry) {
			// Position écran de l'entry selon le scroll
			return entry.bounds().y() + viewportOriginY() - (int) currentScroll;
		}

		private int viewportOriginY() {
			// Y écran du début de la zone viewport
			return bounds.y() + POPUP_PADDING + CLIENT.textRenderer.fontHeight + POPUP_GAP;
		}

		private void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
			renderPanel(context, bounds);
			// Titre
			context.drawText(
					CLIENT.textRenderer,
					Text.translatable("flex_hud.create_module_screen.editor.add_modifier"),
					bounds.x() + POPUP_PADDING,
					bounds.y() + POPUP_PADDING,
					TEXT_COLOR, false
			);

			if (entries.isEmpty()) {
				context.drawText(
						CLIENT.textRenderer,
						Text.translatable("flex_hud.create_module_screen.editor.no_modifier_available"),
						bounds.x() + POPUP_PADDING,
						viewportOriginY(),
						PLACEHOLDER_COLOR, false
				);
				return;
			}

			// Clip au viewport
			int vpOriginY = viewportOriginY();
			context.enableScissor(
					bounds.x(),
					vpOriginY,
					bounds.x() + bounds.width(),
					vpOriginY + viewportHeight
			);

			long currentTime = System.nanoTime();
			double deltaSeconds = (currentTime - lastUpdateTime) / 1_000_000_000.0; // Convertir en secondes
			lastUpdateTime = currentTime;

			double alpha = 1.0 - Math.exp(-SCROLL_SPEED * deltaSeconds);

			currentScroll += (targetScroll - currentScroll) * alpha;

			for (ModifierPickerEntry entry : entries) {
				int screenY = entryRenderY(entry);
				// Sauter les entries hors viewport
				if (screenY + entry.bounds().height() < vpOriginY) continue;
				if (screenY > vpOriginY + viewportHeight) break;

				Bounds renderBounds = new Bounds(entry.bounds().x(), screenY, entry.bounds().width(), entry.bounds().height());
				int background = renderBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND;
				renderButton(context, renderBounds, entry.modifier().uiMetadata().getName(entry.modifier().key()), background, BUTTON_TEXT_COLOR, BUTTON_PADDING, mouseX, mouseY);
			}

			context.disableScissor();

			// Scrollbar
			if (needsScrollbar()) {
				renderScrollbar(context, vpOriginY, mouseX, mouseY);
			}
		}

		private void renderScrollbar(DrawContext context, int vpOriginY, double mouseX, double mouseY) {
			int trackX = bounds.x() + bounds.width() - POPUP_PADDING - SCROLLBAR_WIDTH;
			int trackHeight = viewportHeight;

			// Thumb
			float ratio = (float) viewportHeight / contentHeight;
			int thumbHeight = Math.max(12, (int) (trackHeight * ratio));
			int maxThumbOffset = trackHeight - thumbHeight;
			int thumbOffset = (contentHeight > viewportHeight)
					? (int) ((float) currentScroll / (contentHeight - viewportHeight) * maxThumbOffset)
					: 0;

			// Thumb plus clair si hover ou drag actif
			boolean active = isDraggingScrollbar || isOverScrollbarThumb(mouseX, mouseY);
			int thumbColor = active ? SCROLLBAR_THUMB_ACTIVE_COLOR : SCROLLBAR_THUMB_COLOR;

			context.fill(
					trackX, vpOriginY + thumbOffset,
					trackX + SCROLLBAR_WIDTH, vpOriginY + thumbOffset + thumbHeight,
					thumbColor
			);

			if (isOverScrollbarThumb(mouseX, mouseY)) {
				context.setCursor(isDraggingScrollbar ? StandardCursors.RESIZE_NS : StandardCursors.POINTING_HAND);
			}
		}

		private boolean isOverScrollbarThumb(double mouseX, double mouseY) {
			if (!needsScrollbar()) return false;
			int trackX = bounds.x() + bounds.width() - POPUP_PADDING - SCROLLBAR_WIDTH;
			int vpOriginY = viewportOriginY();
			int trackHeight = viewportHeight;
			float ratio = (float) viewportHeight / contentHeight;
			int thumbHeight = Math.max(12, (int) (trackHeight * ratio));
			int maxThumbOffset = trackHeight - thumbHeight;
			int thumbOffset = (contentHeight > viewportHeight)
					? (int) ((float) currentScroll / (contentHeight - viewportHeight) * maxThumbOffset)
					: 0;
			int thumbY = vpOriginY + thumbOffset;
			return mouseX >= trackX && mouseX <= trackX + SCROLLBAR_WIDTH
					&& mouseY >= thumbY && mouseY <= thumbY + thumbHeight;
		}

		private boolean mouseScrolled(double mouseX, double mouseY, double amount) {
			if (!bounds.contains(mouseX, mouseY)) return false;
			targetScroll = MathHelper.clamp(targetScroll - amount * SCROLL_SPEED, 0.0, maxScroll());
			return true;
		}

		private boolean mouseClicked(Click click, boolean doubled) {
			// Démarrer le drag si clic sur le thumb
			if (isOverScrollbarThumb(click.x(), click.y())) {
				isDraggingScrollbar = true;
				dragStartMouseY = click.y();
				dragStartScrollOffset = currentScroll;
				targetScroll = currentScroll;
				return true;
			}

			int vpOriginY = viewportOriginY();
			for (ModifierPickerEntry entry : entries) {
				int screenY = entryRenderY(entry);
				Bounds renderBounds = new Bounds(entry.bounds().x(), screenY, entry.bounds().width(), entry.bounds().height());
				if (!renderBounds.contains(click.x(), click.y())) continue;
				if (click.y() < vpOriginY || click.y() > vpOriginY + viewportHeight) continue;

				if (entry.modifier().uiMetadata().editorKind() == Modifier.EditorKind.NONE) {
					applyModifierChange(elementIndex, null, defaultResolvedModifier(entry.modifier()), false);
				} else {
					modifierEditorPopup = new ModifierEditorPopup(elementIndex, null, entry.modifier(), defaultArguments(entry.modifier()));
					VariableDisplayItem variableDisplayItem = findVariableDisplayItem(elementIndex);
					if (variableDisplayItem != null) {
						modifierEditorPopup.layout(variableDisplayItem);
					}
					modifierPickerPopup = null;
				}
				return true;
			}
			return bounds.contains(click.x(), click.y());
		}

		private boolean mouseDragged(Click click, double deltaX, double deltaY) {
			if (!isDraggingScrollbar) return false;
			int trackHeight = viewportHeight;
			float ratio = (float) viewportHeight / contentHeight;
			int thumbHeight = Math.max(12, (int) (trackHeight * ratio));
			int maxThumbOffset = trackHeight - thumbHeight;
			// Convertir le déplacement souris en déplacement de scroll
			float scrollPerPixel = (float) (contentHeight - viewportHeight) / maxThumbOffset;
			int delta = (int) ((click.y() - dragStartMouseY) * scrollPerPixel);
			setScrollPosition(dragStartScrollOffset + delta);
			return true;
		}

		private boolean mouseReleased(Click click) {
			if (isDraggingScrollbar) {
				isDraggingScrollbar = false;
				return true;
			}
			return false;
		}

		private @Nullable HoverTarget findHoverTarget(int mouseX, int mouseY) {
			int vpOriginY = viewportOriginY();
			for (ModifierPickerEntry entry : entries) {
				int screenY = entryRenderY(entry);
				if (screenY < vpOriginY || screenY > vpOriginY + viewportHeight) continue;
				Bounds renderBounds = new Bounds(entry.bounds().x(), screenY, entry.bounds().width(), entry.bounds().height());
				if (renderBounds.contains(mouseX, mouseY)) {
					return new HoverTarget(entry.tooltip());
				}
			}
			return null;
		}

		private boolean contains(double mouseX, double mouseY) {
			return bounds.contains(mouseX, mouseY);
		}
	}

	private final class ModifierEditorPopup {
		private static final int FIELD_HEIGHT = 18;
		private static final int LABEL_FIELD_GAP = 2;
		private static final int ROW_GAP = 4;
		private static final int CONDITIONAL_REMOVE_WIDTH = 16;
		private static final int CONDITIONAL_OPERATOR_WIDTH = 28;
		private static final int CONDITIONAL_THRESHOLD_WIDTH = 46;

		private final int elementIndex;
		private final @Nullable Integer modifierIndex;
		private final Modifier<?, ?> modifier;
		private final List<PopupTextFieldWidget> parameterFields = new ArrayList<>();
		private final List<ConditionalBranchRow> conditionalRows = new ArrayList<>();
		private @Nullable Text error;

		private Bounds bounds = new Bounds(0, 0, 0, 0);
		private Bounds saveBounds = new Bounds(0, 0, 0, 0);
		private Bounds cancelBounds = new Bounds(0, 0, 0, 0);
		private @Nullable Bounds deleteBounds;
		private @Nullable Bounds addConditionalBounds;

		private ModifierEditorPopup(int elementIndex, @Nullable Integer modifierIndex) {
			this.elementIndex = elementIndex;
			this.modifierIndex = modifierIndex;
			ModuleContentEditorModel.VariableElement variableElement = (ModuleContentEditorModel.VariableElement) model.get(elementIndex);
			Modifiers.ResolvedModifier<?, ?> resolvedModifier = variableElement.modifiers().get(modifierIndex);
			this.modifier = resolvedModifier.modifier();
			buildFields(resolvedModifier.arguments());
		}

		private ModifierEditorPopup(int elementIndex, @Nullable Integer modifierIndex, Modifier<?, ?> modifier, List<String> initialArguments) {
			this.elementIndex = elementIndex;
			this.modifierIndex = modifierIndex;
			this.modifier = modifier;
			buildFields(initialArguments);
		}

		private int elementIndex() {
			return elementIndex;
		}

		private void setError(Text error) {
			this.error = error;
		}

		private void buildFields(List<String> initialArguments) {
			switch (modifier.uiMetadata().editorKind()) {
				case NONE -> {
				}
				case FIXED_FIELDS -> {
					for (int i = 0; i < modifier.uiMetadata().parameters().size(); i++) {
						Modifier.ParameterDefinition parameter = modifier.uiMetadata().parameters().get(i);
						String value = i < initialArguments.size() ? initialArguments.get(i) : defaultValue(modifier, parameter);
						PopupTextFieldWidget field = new PopupTextFieldWidget(140, FIELD_HEIGHT);
						field.setText(value);
						field.setMaxLength(parameter.kind() == Modifier.ParameterKind.CHARACTER ? 1 : 128);
						applyParameterTextPredicate(field, parameter);
						parameterFields.add(field);
					}
				}
				case CONDITIONAL_BRANCHES -> {
					if (initialArguments.isEmpty()) {
						conditionalRows.add(new ConditionalBranchRow("if_gt", "0", ""));
					} else {
						for (int i = 0; i < initialArguments.size(); i += 3) {
							conditionalRows.add(new ConditionalBranchRow(initialArguments.get(i), initialArguments.get(i + 1), initialArguments.get(i + 2)));
						}
					}
				}
			}
		}

		private void applyParameterTextPredicate(PopupTextFieldWidget field, Modifier.ParameterDefinition parameter) {
			Predicate<String> predicate = parameterTextPredicate(parameter);
			if (predicate != null) {
				field.setTextPredicate(predicate);
			}
		}

		private @Nullable Predicate<String> parameterTextPredicate(Modifier.ParameterDefinition parameter) {
			return switch (parameter.kind()) {
				case INTEGER -> switch (modifier.key()) {
					case "round", "floor", "ceil", "percent", "pad_left", "pad_right", "pad_center", "truncate" ->
							ModuleContentField::isUnsignedTwoDigitIntegerInput;
					case "pow" -> ModuleContentField::isUnsignedIntegerInput;
					default -> ModuleContentField::isUnsignedIntegerInput;
				};
				case DECIMAL -> modifier.key().equals("div")
						? ModuleContentField::isSignedNonZeroIntegerInput
						: ModuleContentField::isSignedIntegerInput;
				case TEXT, CHARACTER, CONDITIONAL_BRANCHES -> null;
			};
		}

		private void layout(VariableDisplayItem variableItem) {
			int width = 240;
			int titleHeight = CLIENT.textRenderer.fontHeight;
			int height = POPUP_PADDING * 2 + titleHeight + POPUP_GAP;
			switch (modifier.uiMetadata().editorKind()) {
				case NONE -> height += FIELD_HEIGHT;
				case FIXED_FIELDS -> {
					for (int i = 0; i < parameterFields.size(); i++) {
						height += CLIENT.textRenderer.fontHeight + LABEL_FIELD_GAP + FIELD_HEIGHT + (i == parameterFields.size() - 1 ? 0 : ROW_GAP);
					}
				}
				case CONDITIONAL_BRANCHES ->
						height += conditionalRows.size() * (FIELD_HEIGHT + ROW_GAP) + FIELD_HEIGHT + POPUP_GAP;
			}
			if (error != null) {
				height += CLIENT.textRenderer.fontHeight + POPUP_GAP;
			}
			height += BUTTON_HEIGHT + POPUP_GAP;

			int preferredX = getX() + TEXT_PADDING_X + variableItem.x() - horizontalScroll;
			int preferredY = getBottom() + OVERLAY_GAP;
			Screen screen = CLIENT.currentScreen;
			if (screen != null && preferredY + height > screen.height - 4) {
				preferredY = getY() - height - OVERLAY_GAP;
			}

			bounds = new Bounds(clampX(preferredX, width), clampY(preferredY, height), width, height);

			int cursorY = bounds.y() + POPUP_PADDING + titleHeight + POPUP_GAP;
			switch (modifier.uiMetadata().editorKind()) {
				case NONE -> {
				}
				case FIXED_FIELDS -> {
					for (PopupTextFieldWidget field : parameterFields) {
						field.setPosition(bounds.x() + POPUP_PADDING, cursorY + CLIENT.textRenderer.fontHeight + LABEL_FIELD_GAP);
						field.setWidth(bounds.width() - POPUP_PADDING * 2);
						cursorY += CLIENT.textRenderer.fontHeight + LABEL_FIELD_GAP + FIELD_HEIGHT + ROW_GAP;
					}
				}
				case CONDITIONAL_BRANCHES -> {
					for (ConditionalBranchRow row : conditionalRows) {
						row.layout(bounds.x() + POPUP_PADDING, cursorY, bounds.width() - POPUP_PADDING * 2);
						cursorY += FIELD_HEIGHT + ROW_GAP;
					}
					addConditionalBounds = new Bounds(bounds.x() + POPUP_PADDING, cursorY, 22, BUTTON_HEIGHT);
					cursorY += FIELD_HEIGHT + POPUP_GAP;
				}
			}

			int buttonsY = bounds.bottom() - POPUP_PADDING - BUTTON_HEIGHT;
			int deleteWidth = modifierIndex != null ? textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.delete_modifier")) : 0;
			int saveWidth = textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.apply"));
			int cancelWidth = textButtonWidth(Text.translatable("flex_hud.global.config.cancel"));
			int totalButtonsWidth = saveWidth + POPUP_GAP + cancelWidth + (modifierIndex != null ? POPUP_GAP + deleteWidth : 0);
			int buttonX = bounds.right() - POPUP_PADDING - totalButtonsWidth;

			if (modifierIndex != null) {
				deleteBounds = new Bounds(buttonX, buttonsY, deleteWidth, BUTTON_HEIGHT);
				buttonX += deleteWidth + POPUP_GAP;
			} else {
				deleteBounds = null;
			}
			saveBounds = new Bounds(buttonX, buttonsY, saveWidth, BUTTON_HEIGHT);
			buttonX += saveWidth + POPUP_GAP;
			cancelBounds = new Bounds(buttonX, buttonsY, cancelWidth, BUTTON_HEIGHT);
		}

		private void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
			renderPanel(context, bounds);
			context.drawText(CLIENT.textRenderer, modifier.uiMetadata().getName(modifier.key()), bounds.x() + POPUP_PADDING, bounds.y() + POPUP_PADDING, TEXT_COLOR, false);
			int cursorY = bounds.y() + POPUP_PADDING + CLIENT.textRenderer.fontHeight + POPUP_GAP;

			switch (modifier.uiMetadata().editorKind()) {
				case NONE ->
						context.drawText(CLIENT.textRenderer, modifier.uiMetadata().getDescription(modifier.key()), bounds.x() + POPUP_PADDING, cursorY, PLACEHOLDER_COLOR, false);
				case FIXED_FIELDS -> {
					for (int i = 0; i < parameterFields.size(); i++) {
						Modifier.ParameterDefinition parameter = modifier.uiMetadata().parameters().get(i);
						context.drawText(CLIENT.textRenderer, parameter.getName(modifier.key()), bounds.x() + POPUP_PADDING, cursorY, TEXT_COLOR, false);
						parameterFields.get(i).render(context, mouseX, mouseY, deltaTicks);
						cursorY += CLIENT.textRenderer.fontHeight + LABEL_FIELD_GAP + FIELD_HEIGHT + ROW_GAP;
					}
				}
				case CONDITIONAL_BRANCHES -> {
					for (ConditionalBranchRow row : conditionalRows) {
						row.render(context, mouseX, mouseY, deltaTicks);
					}
					if (addConditionalBounds != null) {
						renderButtonCenterLabel(context, addConditionalBounds, Text.literal("+"), addConditionalBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);
					}
				}
			}

			if (error != null) {
				context.drawText(CLIENT.textRenderer, error, bounds.x() + POPUP_PADDING, saveBounds.y() - POPUP_GAP - CLIENT.textRenderer.fontHeight, POPUP_ERROR_COLOR, false);
			}

			if (deleteBounds != null) {
				renderButtonCenterLabel(context, deleteBounds, Text.translatable("flex_hud.create_module_screen.editor.delete_modifier"), deleteBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);
			}
			renderButtonCenterLabel(context, saveBounds, Text.translatable("flex_hud.create_module_screen.editor.apply"), saveBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);
			renderButtonCenterLabel(context, cancelBounds, Text.translatable("flex_hud.global.config.cancel"), cancelBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);
		}

		private boolean mouseClicked(Click click, boolean doubled) {
			setAllFieldsFocused(false);
			for (PopupTextFieldWidget field : parameterFields) {
				if (field.mouseClicked(click, doubled)) {
					return true;
				}
			}
			for (ConditionalBranchRow row : conditionalRows) {
				if (row.mouseClicked(click, doubled)) {
					return true;
				}
			}
			if (addConditionalBounds != null && addConditionalBounds.contains(click.x(), click.y())) {
				conditionalRows.add(new ConditionalBranchRow("if_gt", "0", ""));
				layoutFromCurrentAnchor();
				return true;
			}
			if (deleteBounds != null && deleteBounds.contains(click.x(), click.y())) {
				applyModifierChange(elementIndex, modifierIndex, null, true);
				return true;
			}
			if (saveBounds.contains(click.x(), click.y())) {
				save();
				return true;
			}
			if (cancelBounds.contains(click.x(), click.y())) {
				modifierEditorPopup = null;
				return true;
			}
			return bounds.contains(click.x(), click.y());
		}

		private boolean mouseDragged(Click click, double offsetX, double offsetY) {
			for (PopupTextFieldWidget field : parameterFields) {
				if (field.mouseDragged(click, offsetX, offsetY)) {
					return true;
				}
			}
			for (ConditionalBranchRow row : conditionalRows) {
				if (row.mouseDragged(click, offsetX, offsetY)) {
					return true;
				}
			}
			return false;
		}

		private boolean mouseReleased(Click click) {
			boolean handled = false;
			for (PopupTextFieldWidget field : parameterFields) {
				handled |= field.mouseReleased(click);
			}
			for (ConditionalBranchRow row : conditionalRows) {
				handled |= row.mouseReleased(click);
			}
			return handled;
		}

		private boolean keyPressed(KeyInput input) {
			for (PopupTextFieldWidget field : parameterFields) {
				if (field.isFocused() && field.keyPressed(input)) {
					return true;
				}
			}
			for (ConditionalBranchRow row : conditionalRows) {
				if (row.keyPressed(input)) {
					return true;
				}
			}
			if (input.key() == GLFW.GLFW_KEY_ENTER || input.key() == GLFW.GLFW_KEY_KP_ENTER) {
				save();
				return true;
			}
			if (input.key() == GLFW.GLFW_KEY_TAB) {
				focusNextField();
				return true;
			}
			return false;
		}

		private boolean charTyped(CharInput input) {
			for (PopupTextFieldWidget field : parameterFields) {
				if (field.isFocused() && field.charTyped(input)) {
					return true;
				}
			}
			for (ConditionalBranchRow row : conditionalRows) {
				if (row.charTyped(input)) {
					return true;
				}
			}
			return false;
		}

		private void focusNextField() {
			List<PopupTextFieldWidget> fields = new ArrayList<>(parameterFields);
			for (ConditionalBranchRow row : conditionalRows) {
				fields.add(row.thresholdField());
				fields.add(row.resultField());
			}
			if (fields.isEmpty()) {
				return;
			}

			int focusedIndex = -1;
			for (int i = 0; i < fields.size(); i++) {
				if (fields.get(i).isFocused()) {
					focusedIndex = i;
					fields.get(i).setFocused(false);
					break;
				}
			}
			fields.get((focusedIndex + 1) % fields.size()).setFocused(true);
		}

		private void setAllFieldsFocused(boolean focused) {
			for (PopupTextFieldWidget field : parameterFields) {
				field.setFocused(focused);
			}
			for (ConditionalBranchRow row : conditionalRows) {
				row.thresholdField().setFocused(focused);
				row.resultField().setFocused(focused);
			}
		}

		private void save() {
			Modifiers.ResolvedModifier<?, ?> resolvedModifier = buildResolvedModifier();
			if (resolvedModifier == null) {
				error = Text.translatable("flex_hud.create_module_screen.editor.invalid_modifier");
				return;
			}
			applyModifierChange(elementIndex, modifierIndex, resolvedModifier, false);
		}

		private Modifiers.ResolvedModifier<?, ?> buildResolvedModifier() {
			List<String> arguments = new ArrayList<>();
			switch (modifier.uiMetadata().editorKind()) {
				case NONE -> {
				}
				case FIXED_FIELDS -> {
					for (int i = 0; i < parameterFields.size(); i++) {
						String value = parameterFields.get(i).getText();
						Modifier.ParameterKind kind = modifier.uiMetadata().parameters().get(i).kind();
						if (kind == Modifier.ParameterKind.CHARACTER && value.isEmpty()) {
							return null;
						}
						arguments.add(kind == Modifier.ParameterKind.CHARACTER ? value.substring(0, 1) : value);
					}
				}
				case CONDITIONAL_BRANCHES -> {
					for (ConditionalBranchRow row : conditionalRows) {
						if (row.thresholdField().getText().isBlank()) {
							return null;
						}
						arguments.add(row.operatorKey());
						arguments.add(row.thresholdField().getText());
						arguments.add(row.resultField().getText());
					}
				}
			}

			String raw = modifier.uiMetadata().rawFormatter().apply(arguments);
			Modifiers.ResolvedModifier<?, ?> resolvedModifier = Modifiers.get(raw);
			if (resolvedModifier == null || !resolvedModifier.modifier().key().equals(modifier.key())) {
				return null;
			}
			return resolvedModifier;
		}

		private int textButtonWidth(Text text) {
			return CLIENT.textRenderer.getWidth(text) + BUTTON_HORIZONTAL_PADDING * 2;
		}

		private void layoutFromCurrentAnchor() {
			VariableDisplayItem variableDisplayItem = findVariableDisplayItem(elementIndex);
			if (variableDisplayItem != null) {
				layout(variableDisplayItem);
			}
		}

		private boolean contains(double mouseX, double mouseY) {
			return bounds.contains(mouseX, mouseY);
		}

		private final class ConditionalBranchRow {
			private final PopupTextFieldWidget thresholdField;
			private final PopupTextFieldWidget resultField;
			private String operatorKey;
			private Bounds operatorBounds = new Bounds(0, 0, 0, 0);
			private Bounds removeBounds = new Bounds(0, 0, 0, 0);

			private ConditionalBranchRow(String operatorKey, String threshold, String result) {
				this.operatorKey = operatorKey;
				this.thresholdField = new PopupTextFieldWidget(CONDITIONAL_THRESHOLD_WIDTH, FIELD_HEIGHT);
				this.thresholdField.setText(threshold);
				this.thresholdField.setMaxLength(32);
				this.thresholdField.setTextPredicate(ModuleContentField::isSignedIntegerInput);
				this.resultField = new PopupTextFieldWidget(120, FIELD_HEIGHT);
				this.resultField.setText(result);
				this.resultField.setMaxLength(128);
			}

			private void layout(int x, int y, int width) {
				operatorBounds = new Bounds(x, y, CONDITIONAL_OPERATOR_WIDTH, FIELD_HEIGHT);
				thresholdField.setPosition(x + CONDITIONAL_OPERATOR_WIDTH + POPUP_GAP, y);
				resultField.setPosition(x + CONDITIONAL_OPERATOR_WIDTH + POPUP_GAP + CONDITIONAL_THRESHOLD_WIDTH + POPUP_GAP, y);
				resultField.setWidth(width - CONDITIONAL_OPERATOR_WIDTH - CONDITIONAL_THRESHOLD_WIDTH - CONDITIONAL_REMOVE_WIDTH - POPUP_GAP * 3);
				removeBounds = new Bounds(x + width - CONDITIONAL_REMOVE_WIDTH, y, CONDITIONAL_REMOVE_WIDTH, FIELD_HEIGHT);
			}

			private void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
				renderButtonCenterLabel(context, operatorBounds, Text.literal(displayOperator()), operatorBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);
				thresholdField.render(context, mouseX, mouseY, deltaTicks);
				resultField.render(context, mouseX, mouseY, deltaTicks);
				renderButtonCenterLabel(context, removeBounds, Text.literal("x"), removeBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);
			}

			private boolean mouseClicked(Click click, boolean doubled) {
				if (operatorBounds.contains(click.x(), click.y())) {
					operatorKey = switch (operatorKey) {
						case "if_gt" -> "if_lt";
						case "if_lt" -> "if_eq";
						default -> "if_gt";
					};
					return true;
				}
				if (removeBounds.contains(click.x(), click.y())) {
					conditionalRows.remove(this);
					if (conditionalRows.isEmpty()) {
						conditionalRows.add(new ConditionalBranchRow("if_gt", "0", ""));
					}
					layoutFromCurrentAnchor();
					return true;
				}
				return thresholdField.mouseClicked(click, doubled) || resultField.mouseClicked(click, doubled);
			}

			private boolean mouseDragged(Click click, double offsetX, double offsetY) {
				return thresholdField.mouseDragged(click, offsetX, offsetY)
						|| resultField.mouseDragged(click, offsetX, offsetY);
			}

			private boolean mouseReleased(Click click) {
				return thresholdField.mouseReleased(click) || resultField.mouseReleased(click);
			}

			private boolean keyPressed(KeyInput input) {
				return (thresholdField.isFocused() && thresholdField.keyPressed(input))
						|| (resultField.isFocused() && resultField.keyPressed(input));
			}

			private boolean charTyped(CharInput input) {
				return (thresholdField.isFocused() && thresholdField.charTyped(input))
						|| (resultField.isFocused() && resultField.charTyped(input));
			}

			private String displayOperator() {
				return switch (operatorKey) {
					case "if_gt" -> ">";
					case "if_lt" -> "<";
					case "if_eq" -> "=";
					default -> operatorKey;
				};
			}

			private String operatorKey() {
				return operatorKey;
			}

			private PopupTextFieldWidget thresholdField() {
				return thresholdField;
			}

			private PopupTextFieldWidget resultField() {
				return resultField;
			}
		}

		private static final class PopupTextFieldWidget extends TextFieldWidget {
			private PopupTextFieldWidget(int width, int height) {
				super(CLIENT.textRenderer, 0, 0, width, height, Text.empty());
			}

			@Override
			public boolean mouseClicked(Click click, boolean doubled) {
				if (this.active && this.visible && this.isValidClickButton(click.buttonInfo())) {
					setFocused(this.isMouseOver(click.x(), click.y()));
				}
				return super.mouseClicked(click, doubled);
			}
		}
	}

	private final class ColorPopup {
		private final int selectionStart;
		private final int selectionEnd;
		private final SelectionColorBindable bindable;
		private ColorSelector selector;
		private Bounds bounds = new Bounds(0, 0, 0, 0);
		private Bounds noneBounds = new Bounds(0, 0, 0, 0);
		private Bounds chromaBounds = new Bounds(0, 0, 0, 0);
		private Bounds closeBounds = new Bounds(0, 0, 0, 0);
		private List<Bounds> presetBounds = List.of();

		private ColorPopup(int selectionStart, int selectionEnd, int initialColor) {
			this.selectionStart = selectionStart;
			this.selectionEnd = selectionEnd;
			this.bindable = new SelectionColorBindable(initialColor);
			this.selector = new ColorSelector(bindable);
		}

		private void layout(SelectionBounds selectionBounds) {
			int width = Math.max(selector.getWidth() + POPUP_PADDING * 2, 12 * 4 + POPUP_GAP * 3 + POPUP_PADDING * 2);
			int headerWidth = textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.no_color"))
					+ textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.chroma"))
					+ textButtonWidth(Text.translatable("flex_hud.global.config.cancel"))
					+ POPUP_GAP * 2 + POPUP_PADDING * 2;
			width = Math.max(width, headerWidth);
			int height = POPUP_PADDING * 2 + BUTTON_HEIGHT + POPUP_GAP + 12 * 4 + POPUP_GAP * 3 + POPUP_GAP + selector.getHeight();

			int preferredX = selectionBounds.centerX() - width / 2;
			int preferredY = toolbarButtons.isEmpty() ? selectionBounds.bottom() + OVERLAY_GAP : toolbarButtons.getFirst().bounds().bottom() + OVERLAY_GAP;
			bounds = new Bounds(clampX(preferredX, width), clampY(preferredY, height), width, height);

			int buttonX = bounds.x() + POPUP_PADDING;
			noneBounds = new Bounds(buttonX, bounds.y() + POPUP_PADDING, textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.no_color")), BUTTON_HEIGHT);
			buttonX = noneBounds.right() + POPUP_GAP;
			chromaBounds = new Bounds(buttonX, bounds.y() + POPUP_PADDING, textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.chroma")), BUTTON_HEIGHT);
			closeBounds = new Bounds(bounds.right() - POPUP_PADDING - textButtonWidth(Text.translatable("flex_hud.global.config.cancel")), bounds.y() + POPUP_PADDING, textButtonWidth(Text.translatable("flex_hud.global.config.cancel")), BUTTON_HEIGHT);

			List<Bounds> swatches = new ArrayList<>(PRESET_COLORS.size());
			int swatchY = noneBounds.bottom() + POPUP_GAP;
			int swatchSize = 12;
			for (int i = 0; i < PRESET_COLORS.size(); i++) {
				int swatchX = bounds.x() + POPUP_PADDING + (i % 4) * (swatchSize + POPUP_GAP);
				int y = swatchY + (i / 4) * (swatchSize + POPUP_GAP);
				swatches.add(new Bounds(swatchX, y, swatchSize, swatchSize));
			}
			presetBounds = List.copyOf(swatches);

			selector.setPosition(bounds.x() + bounds.width() - POPUP_PADDING - selector.getWidth(), bounds.bottom() - POPUP_PADDING - selector.getHeight());
			selector.setFocused(true);
		}

		private void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
			renderPanel(context, bounds);
			renderButtonCenterLabel(context, noneBounds, Text.translatable("flex_hud.create_module_screen.editor.no_color"), noneBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);
			renderButtonCenterLabel(context, chromaBounds, Text.translatable("flex_hud.create_module_screen.editor.chroma"), chromaBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);
			renderButtonCenterLabel(context, closeBounds, Text.translatable("flex_hud.global.config.cancel"), closeBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);

			for (int i = 0; i < presetBounds.size(); i++) {
				Bounds swatch = presetBounds.get(i);
				int color = PRESET_COLORS.get(i);
				context.fill(swatch.x(), swatch.y(), swatch.right(), swatch.bottom(), 0xff000000 | color);
				context.drawStrokedRectangle(swatch.x(), swatch.y(), swatch.width(), swatch.height(), POPUP_BORDER);
			}

			selector.render(context, mouseX, mouseY, deltaTicks);
		}

		private boolean mouseClicked(Click click, boolean doubled) {
			if (selector.mouseClicked(click, doubled)) {
				return true;
			}
			if (noneBounds.contains(click.x(), click.y())) {
				applySelectionColorLayers(selectionStart, selectionEnd, List.of());
				colorPopup = null;
				return true;
			}
			if (chromaBounds.contains(click.x(), click.y())) {
				applySelectionColorLayers(selectionStart, selectionEnd, List.of(ModuleContentEditorModel.ChromaColorLayer.INSTANCE));
				colorPopup = null;
				return true;
			}
			if (closeBounds.contains(click.x(), click.y())) {
				colorPopup = null;
				return true;
			}
			for (int i = 0; i < presetBounds.size(); i++) {
				if (presetBounds.get(i).contains(click.x(), click.y())) {
					bindable.onReceiveColor(PRESET_COLORS.get(i));
					colorPopup = null;
					return true;
				}
			}
			return bounds.contains(click.x(), click.y());
		}

		private boolean mouseDragged(Click click, double offsetX, double offsetY) {
			return selector.mouseDragged(click, offsetX, offsetY);
		}

		private boolean mouseReleased(Click click) {
			return selector.mouseReleased(click);
		}

		private boolean keyPressed(KeyInput input) {
			return selector.keyPressed(input);
		}

		private boolean charTyped(CharInput input) {
			return selector.charTyped(input);
		}

		private boolean contains(double mouseX, double mouseY) {
			return bounds.contains(mouseX, mouseY) || selector.isMouseOver(mouseX, mouseY);
		}

		private boolean matchesSelection() {
			return hasSelection()
					&& selectionStart == Math.min(caretIndex, selectionAnchor)
					&& selectionEnd == Math.max(caretIndex, selectionAnchor);
		}

		private int textButtonWidth(Text text) {
			return CLIENT.textRenderer.getWidth(text) + BUTTON_HORIZONTAL_PADDING * 2;
		}

		private final class SelectionColorBindable implements ColorBindable {
			private int color;

			private SelectionColorBindable(int color) {
				this.color = color;
			}

			@Override
			public void onReceiveColor(int color) {
				this.color = color;
				applySelectionColorLayers(selectionStart, selectionEnd, List.of(new ModuleContentEditorModel.StaticColorLayer(color)));
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

	private final class GradientPopup {
		private final int selectionStart;
		private final int selectionEnd;
		private final GradientColorBindable bindable;
		private ColorSelector selector;
		private int startColor;
		private int endColor;
		private boolean editingStart = true;

		private Bounds bounds = new Bounds(0, 0, 0, 0);
		private Bounds startBounds = new Bounds(0, 0, 0, 0);
		private Bounds endBounds = new Bounds(0, 0, 0, 0);
		private Bounds clearBounds = new Bounds(0, 0, 0, 0);
		private Bounds closeBounds = new Bounds(0, 0, 0, 0);

		private GradientPopup(int selectionStart, int selectionEnd, int startColor, int endColor) {
			this.selectionStart = selectionStart;
			this.selectionEnd = selectionEnd;
			this.startColor = startColor;
			this.endColor = endColor;
			this.bindable = new GradientColorBindable();
			this.selector = new ColorSelector(bindable);
			applyGradient();
		}

		private void layout(SelectionBounds selectionBounds) {
			int width = Math.max(selector.getWidth() + POPUP_PADDING * 2, 210);
			int height = POPUP_PADDING * 2 + BUTTON_HEIGHT + POPUP_GAP + selector.getHeight();
			int preferredX = selectionBounds.centerX() - width / 2;
			int preferredY = toolbarButtons.isEmpty() ? selectionBounds.bottom() + OVERLAY_GAP : toolbarButtons.getFirst().bounds().bottom() + OVERLAY_GAP;
			bounds = new Bounds(clampX(preferredX, width), clampY(preferredY, height), width, height);

			int buttonX = bounds.x() + POPUP_PADDING;
			startBounds = new Bounds(buttonX, bounds.y() + POPUP_PADDING, 48, BUTTON_HEIGHT);
			buttonX = startBounds.right() + POPUP_GAP;
			endBounds = new Bounds(buttonX, bounds.y() + POPUP_PADDING, 48, BUTTON_HEIGHT);
			clearBounds = new Bounds(bounds.right() - POPUP_PADDING - textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.no_color")) - POPUP_GAP - textButtonWidth(Text.translatable("flex_hud.global.config.cancel")), bounds.y() + POPUP_PADDING, textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.no_color")), BUTTON_HEIGHT);
			closeBounds = new Bounds(bounds.right() - POPUP_PADDING - textButtonWidth(Text.translatable("flex_hud.global.config.cancel")), bounds.y() + POPUP_PADDING, textButtonWidth(Text.translatable("flex_hud.global.config.cancel")), BUTTON_HEIGHT);

			selector.setPosition(bounds.x() + bounds.width() - POPUP_PADDING - selector.getWidth(), bounds.bottom() - POPUP_PADDING - selector.getHeight());
			selector.setFocused(true);
		}

		private void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
			renderPanel(context, bounds);
			renderButtonCenterLabel(context, startBounds, Text.translatable("flex_hud.create_module_screen.editor.gradient_start"), startBounds.contains(mouseX, mouseY) || editingStart ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);
			renderButtonCenterLabel(context, endBounds, Text.translatable("flex_hud.create_module_screen.editor.gradient_end"), endBounds.contains(mouseX, mouseY) || !editingStart ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);
			renderButtonCenterLabel(context, clearBounds, Text.translatable("flex_hud.create_module_screen.editor.no_color"), clearBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);
			renderButtonCenterLabel(context, closeBounds, Text.translatable("flex_hud.global.config.cancel"), closeBounds.contains(mouseX, mouseY) ? BUTTON_HOVERED_BACKGROUND : BUTTON_BACKGROUND, BUTTON_TEXT_COLOR, mouseX, mouseY);

			renderColorPreview(context, startBounds, startColor);
			renderColorPreview(context, endBounds, endColor);
			selector.render(context, mouseX, mouseY, deltaTicks);
		}

		private void renderColorPreview(DrawContext context, Bounds bounds, int color) {
			context.fill(bounds.x() + 2, bounds.bottom() - 4, bounds.right() - 2, bounds.bottom() - 2, 0xff000000 | color);
		}

		private boolean mouseClicked(Click click, boolean doubled) {
			if (selector.mouseClicked(click, doubled)) {
				return true;
			}
			if (startBounds.contains(click.x(), click.y())) {
				editingStart = true;
				rebuildSelector();
				return true;
			}
			if (endBounds.contains(click.x(), click.y())) {
				editingStart = false;
				rebuildSelector();
				return true;
			}
			if (clearBounds.contains(click.x(), click.y())) {
				applySelectionColorLayers(selectionStart, selectionEnd, List.of());
				gradientPopup = null;
				return true;
			}
			if (closeBounds.contains(click.x(), click.y())) {
				gradientPopup = null;
				return true;
			}
			return bounds.contains(click.x(), click.y());
		}

		private boolean mouseDragged(Click click, double offsetX, double offsetY) {
			return selector.mouseDragged(click, offsetX, offsetY);
		}

		private boolean mouseReleased(Click click) {
			return selector.mouseReleased(click);
		}

		private boolean keyPressed(KeyInput input) {
			return selector.keyPressed(input);
		}

		private boolean charTyped(CharInput input) {
			return selector.charTyped(input);
		}

		private boolean contains(double mouseX, double mouseY) {
			return bounds.contains(mouseX, mouseY) || selector.isMouseOver(mouseX, mouseY);
		}

		private boolean matchesSelection() {
			return hasSelection()
					&& selectionStart == Math.min(caretIndex, selectionAnchor)
					&& selectionEnd == Math.max(caretIndex, selectionAnchor);
		}

		private void applyGradient() {
			applySelectionColorLayers(selectionStart, selectionEnd, List.of(new ModuleContentEditorModel.GradientColorLayer(startColor, endColor)));
		}

		private void rebuildSelector() {
			this.selector = new ColorSelector(bindable);
			selector.setPosition(bounds.x() + bounds.width() - POPUP_PADDING - selector.getWidth(), bounds.bottom() - POPUP_PADDING - selector.getHeight());
		}

		private int textButtonWidth(Text text) {
			return CLIENT.textRenderer.getWidth(text) + BUTTON_HORIZONTAL_PADDING * 2;
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

	private record GradientRegion(ModuleContentEditorModel.GradientColorLayer gradient,
	                              int startIndex,
	                              int endIndex,
	                              int totalWidth,
	                              int[] elementWidths) {
		private static GradientRegion create(ModuleContentEditorModel.GradientColorLayer gradient,
		                                     List<ModuleContentEditorModel.InlineElement> elements,
		                                     java.util.function.ToIntFunction<ModuleContentEditorModel.InlineElement> widthMeasurer) {
			int first = -1;
			int last = -1;
			for (int index = 0; index < elements.size(); index++) {
				if (elements.get(index).style().colorLayers().contains(gradient)) {
					if (first == -1) {
						first = index;
					}
					last = index;
				}
			}

			if (first == -1) {
				return new GradientRegion(gradient, 0, 0, 0, new int[0]);
			}

			int[] widths = new int[last - first + 1];
			int totalWidth = 0;
			for (int index = first; index <= last; index++) {
				int width = widthMeasurer.applyAsInt(elements.get(index));
				widths[index - first] = width;
				totalWidth += width;
			}

			return new GradientRegion(gradient, first, last + 1, totalWidth, widths);
		}

		private int colorAt(int elementIndex) {
			double currentX = 0.0;
			for (int index = startIndex; index < endIndex; index++) {
				int width = elementWidths[index - startIndex];
				if (index == elementIndex) {
					float progress = totalWidth <= 0 ? 0.0f : (float) ((currentX + width / 2.0) / totalWidth);
					return interpolateRgb(gradient.startColor(), gradient.endColor(), progress);
				}
				currentX += width;
			}
			return TEXT_COLOR;
		}
	}

	private static int interpolateRgb(int startColor, int endColor, float progress) {
		float clampedProgress = Math.clamp(progress, 0.0f, 1.0f);

		int startRed = (startColor >> 16) & 0xff;
		int startGreen = (startColor >> 8) & 0xff;
		int startBlue = startColor & 0xff;

		int endRed = (endColor >> 16) & 0xff;
		int endGreen = (endColor >> 8) & 0xff;
		int endBlue = endColor & 0xff;

		int red = Math.round(startRed + (endRed - startRed) * clampedProgress);
		int green = Math.round(startGreen + (endGreen - startGreen) * clampedProgress);
		int blue = Math.round(startBlue + (endBlue - startBlue) * clampedProgress);

		return red << 16 | green << 8 | blue;
	}
}
