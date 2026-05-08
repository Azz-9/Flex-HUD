package me.Azz_9.flex_hud.client.screens.createModuleScreen.moduleContentField;

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
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifier;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;
import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorBindable;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorSelector;
import me.Azz_9.flex_hud.client.screens.createModuleScreen.ModuleContentEditorModel;
import me.Azz_9.flex_hud.client.tickables.ChromaColorTickable;

public class ModuleContentField extends ClickableWidget implements TrackableChange {

	private static final ButtonTextures TEXTURES = new ButtonTextures(
			Identifier.ofVanilla("widget/text_field"), Identifier.ofVanilla("widget/text_field_highlighted")
	);

	static final int TEXT_PADDING_X = 4;
	private static final int TEXT_PADDING_Y = 4;
	private static final int SELECTION_COLOR = 0x66357dff;
	private static final int CARET_COLOR = 0xffffffff;
	static final int TEXT_COLOR = 0xffffffff;
	static final int PLACEHOLDER_COLOR = 0xff8a8f98;

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
	static final int POPUP_PADDING = 6;
	static final int POPUP_GAP = 4;
	static final int OVERLAY_GAP = 6;
	static final int BUTTON_HEIGHT = 16;
	static final int BUTTON_HORIZONTAL_PADDING = 5;
	static final int BUTTON_BACKGROUND = 0xff2c3138;
	static final int BUTTON_HOVERED_BACKGROUND = 0xff404651;
	private static final int BUTTON_ACTIVE_BACKGROUND = 0xff365277;
	private static final int BUTTON_MIXED_BACKGROUND = 0xff675a32;
	static final int BUTTON_TEXT_COLOR = 0xffffffff;
	static final int POPUP_ERROR_COLOR = 0xffff7070;
	private static final int CHIP_PLUS_WIDTH = 8;
	private static final int TOOLBAR_BUTTON_WIDTH = 18;
	private static final int TOOLBAR_ICON_BUTTON_WIDTH = 30;
	private static final List<Integer> PRESET_COLORS = List.of(
			0x000000, 0x0000aa, 0x00aa00, 0x00aaaa,
			0xaa0000, 0xaa00aa, 0xffaa00, 0xaaaaaa,
			0x555555, 0x5555ff, 0x55ff55, 0x55ffff,
			0xff5555, 0xff55ff, 0xffff55, 0xffffff
	);
	static final int SCROLLBAR_THUMB_COLOR = 0xff636360;
	static final int SCROLLBAR_THUMB_ACTIVE_COLOR = 0xffa8a8a4;
	private static final Pattern UNSIGNED_INTEGER_INPUT = Pattern.compile("\\d{0,9}");
	private static final Pattern UNSIGNED_TWO_DIGIT_INTEGER_INPUT = Pattern.compile("\\d{0,2}");
	private static final Pattern SIGNED_INTEGER_INPUT = Pattern.compile("-?\\d{0,9}");
	private static final Pattern SIGNED_NON_ZERO_INTEGER_INPUT = Pattern.compile("-?[1-9]\\d{0,8}");

	private final @Nullable String initialContent;

	ModuleContentEditorModel model;
	private String rawText;
	private int maxLength = 200;
	private Consumer<String> changedListener = text -> {
	};
	private int caretIndex;
	private int selectionAnchor;
	int horizontalScroll;
	private boolean draggingSelection;

	private List<DisplayItem> displayItems = List.of();
	private int[] caretPositions = new int[]{0};
	private int contentWidth;
	private @Nullable HoverTarget hoveredTarget;
	private @Nullable VariableHit hoveredVariableHit;
	private long hoverStartTime;
	private @Nullable SelectionBounds selectionBounds;
	private List<ToolbarButton> toolbarButtons = List.of();
	@Nullable ModifierPickerPopup modifierPickerPopup;
	@Nullable ModifierEditorPopup modifierEditorPopup;
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

		if (rawText.isEmpty()) {
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
		if (modifierPickerPopup != null && modifierPickerPopup.charTyped(input)) {
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
		if (modifierPickerPopup != null && modifierPickerPopup.keyPressed(input)) {
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
		horizontalScroll = Math.clamp(horizontalScroll, 0, Math.max(0, contentWidth - innerWidth));
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
		if (topLayer instanceof ModuleContentEditorModel.StaticColorLayer(int rgb)) {
			return 0xff000000 | rgb;
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
		return Style.EMPTY
				.withBold(style.bold())
				.withItalic(style.italic())
				.withUnderline(style.underline())
				.withStrikethrough(style.strikethrough())
				.withObfuscated(style.obfuscated())
				.withColor(TextColor.fromRgb(color & 0x00ffffff));
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

	void renderButtonCenterLabel(DrawContext context, Bounds bounds, Text label, int backgroundColor, int textColor, double mouseX, double mouseY) {
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

	void renderButton(DrawContext context, Bounds bounds, Text label, int backgroundColor, int textColor, int padding, double mouseX, double mouseY) {
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

	void renderPanel(DrawContext context, Bounds bounds) {
		context.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), POPUP_BACKGROUND);
		context.drawStrokedRectangle(bounds.x(), bounds.y(), bounds.width(), bounds.height(), POPUP_BORDER);
	}

	int clampX(int x, int width) {
		Screen screen = CLIENT.currentScreen;
		if (screen == null) {
			return x;
		}
		return Math.clamp(x, 4, Math.max(4, screen.width - width - 4));
	}

	int clampY(int y, int height) {
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
		modifierPickerPopup = new ModifierPickerPopup(this, variableItem.modelIndex());
		modifierPickerPopup.layout(variableItem);
	}

	private void openModifierEditor(VariableDisplayItem variableItem, int modifierIndex) {
		closeModifierPicker();
		closeSelectionPopups();
		modifierEditorPopup = new ModifierEditorPopup(this, variableItem.modelIndex(), modifierIndex);
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

	@Nullable VariableDisplayItem findVariableDisplayItem(int elementIndex) {
		for (DisplayItem item : displayItems) {
			if (item.modelIndex() == elementIndex && item instanceof VariableDisplayItem variableDisplayItem) {
				return variableDisplayItem;
			}
		}
		return null;
	}

	List<Modifier<?, ?>> getCompatibleModifiers(ModuleContentEditorModel.VariableElement variableElement) {
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

	Modifiers.ResolvedModifier<?, ?> defaultResolvedModifier(Modifier<?, ?> modifier) {
		List<String> arguments = defaultArguments(modifier);
		String raw = modifier.uiMetadata().rawFormatter().apply(arguments);
		Modifiers.ResolvedModifier<?, ?> resolvedModifier = Modifiers.get(raw);
		return resolvedModifier != null ? resolvedModifier : new Modifiers.ResolvedModifier<>(modifier, arguments, raw);
	}

	List<String> defaultArguments(Modifier<?, ?> modifier) {
		if (modifier.uiMetadata().editorKind() == Modifier.EditorKind.CONDITIONAL_BRANCHES) {
			return List.of("if_gt", "0", "");
		}

		List<String> arguments = new ArrayList<>(modifier.uiMetadata().parameters().size());
		for (Modifier.ParameterDefinition parameter : modifier.uiMetadata().parameters()) {
			arguments.add(defaultValue(modifier, parameter));
		}
		return arguments;
	}

	String defaultValue(Modifier<?, ?> modifier, Modifier.ParameterDefinition parameter) {
		if (parameter.kind() == Modifier.ParameterKind.DECIMAL && modifier.key().equals("div")) {
			return "1";
		}

		return switch (parameter.kind()) {
			case INTEGER, DECIMAL -> "0";
			case CHARACTER -> " ";
			case CONDITIONAL_BRANCHES, TEXT -> "";
		};
	}

	static boolean isUnsignedIntegerInput(String text) {
		return UNSIGNED_INTEGER_INPUT.matcher(text).matches();
	}

	static boolean isUnsignedTwoDigitIntegerInput(String text) {
		return UNSIGNED_TWO_DIGIT_INTEGER_INPUT.matcher(text).matches();
	}

	static boolean isSignedIntegerInput(String text) {
		return SIGNED_INTEGER_INPUT.matcher(text).matches();
	}

	static boolean isSignedNonZeroIntegerInput(String text) {
		return text.isEmpty()
				|| text.equals("-")
				|| SIGNED_NON_ZERO_INTEGER_INPUT.matcher(text).matches();
	}

	void applyModifierChange(int elementIndex, @Nullable Integer modifierIndex, Modifiers.ResolvedModifier<?, ?> newModifier, boolean deleteModifier) {
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

	private final class ColorPopup {
		private final int selectionStart;
		private final int selectionEnd;
		private final SelectionColorBindable bindable;
		private final ColorSelector selector;
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


	static int interpolateRgb(int startColor, int endColor, float progress) {
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
