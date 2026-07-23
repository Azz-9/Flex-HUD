package me.Azz_9.flex_hud.client.gui.components.customModule.moduleContentField;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import me.Azz_9.flex_hud.client.gui.Cursors;
import me.Azz_9.flex_hud.client.gui.components.TrackableChange;
import me.Azz_9.flex_hud.client.gui.components.customModule.ModuleContentEditorModel;
import me.Azz_9.flex_hud.client.modules.customModules.Variable;
import me.Azz_9.flex_hud.client.modules.customModules.Variables;
import me.Azz_9.flex_hud.client.modules.customModules.modifiers.Modifier;
import me.Azz_9.flex_hud.client.modules.customModules.modifiers.Modifiers;
import me.Azz_9.flex_hud.client.modules.customModules.text.CustomCondition;
import me.Azz_9.flex_hud.client.tickables.ChromaColorTickable;

public class ModuleContentField extends AbstractWidget implements TrackableChange {

	private static final WidgetSprites SPRITES = new WidgetSprites(
			Identifier.withDefaultNamespace("widget/text_field"), Identifier.withDefaultNamespace("widget/text_field_highlighted")
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
	private static final int CONDITION_BG_COLOR = 0xff322a42;
	private static final int CONDITION_BORDER_COLOR = 0xff745c9c;
	private static final int CONDITION_SELECTED_BG_COLOR = 0xff4a3a66;
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
	static final int SCROLLBAR_THUMB_COLOR = 0xff636360;
	static final int SCROLLBAR_THUMB_ACTIVE_COLOR = 0xffa8a8a4;
	private static final Pattern UNSIGNED_INTEGER_INPUT = Pattern.compile("\\d{0,9}");
	private static final Pattern UNSIGNED_TWO_DIGIT_INTEGER_INPUT = Pattern.compile("\\d{0,2}");
	private static final Pattern SIGNED_INTEGER_INPUT = Pattern.compile("-?\\d{0,9}");
	private static final Pattern SIGNED_DECIMAL_INPUT = Pattern.compile("-?\\d{0,9}(\\.\\d{0,6})?");
	private static final Pattern SIGNED_NON_ZERO_INTEGER_INPUT = Pattern.compile("-?[1-9]\\d{0,8}");

	private final @Nullable String initialContent;
	private @Nullable Component placeholder = Component.translatable("flex_hud.create_module_screen.module_content.placeholder");
	private boolean styleToolbarEnabled = true;
	private boolean plainTextInputEnabled = true;
	private boolean renderOverlaysInline = true;

	ModuleContentEditorModel model;
	private String rawText;
	private int maxLength = 200;
	private Consumer<String> changedListener = _ -> {
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
	private @Nullable ConditionDisplayItem hoveredConditionItem;
	private long hoverStartTime;
	private List<ToolbarButton> toolbarButtons = List.of();
	@Nullable ModifierPickerPopup modifierPickerPopup;
	@Nullable ModifierEditorPopup modifierEditorPopup;
	@Nullable ConditionEditorPopup conditionEditorPopup;
	private @Nullable ColorPopup colorPopup;
	private @Nullable GradientPopup gradientPopup;

	public ModuleContentField(int x, int y, int width, int height, @Nullable String initialContent) {
		super(x, y, width, height, Component.empty());
		this.initialContent = initialContent;
		this.rawText = initialContent == null ? "" : initialContent;
		this.model = ModuleContentEditorModel.parse(rawText);
		this.caretIndex = model.size();
		this.selectionAnchor = caretIndex;
		rebuildLayout();
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		MINECRAFT.onTextInputFocusChange(this, focused);
		if (!focused) {
			draggingSelection = false;
		}
	}

	@Override
	public boolean capturesInput() {
		return active && isFocused();
	}

	@Override
	protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		Identifier texture = SPRITES.get(this.isActive(), this.isFocused());
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), getWidth(), getHeight());

		refreshOverlayLayout();
		updateHover(mouseX, mouseY);

		int innerLeft = getX() + TEXT_PADDING_X;
		int innerTop = getY() + TEXT_PADDING_Y;
		int innerRight = getRight() - TEXT_PADDING_X;
		int innerBottom = getBottom() - TEXT_PADDING_Y;
		int contentTextY = getContentTextY();

		graphics.enableScissor(innerLeft, innerTop, innerRight, innerBottom);
		renderSelection(graphics, innerLeft, contentTextY);
		renderContent(graphics, innerLeft, contentTextY);
		renderCaret(graphics, innerLeft, contentTextY);
		graphics.disableScissor();

		if (rawText.isEmpty() && placeholder != null && !placeholder.getString().isEmpty()) {
			graphics.text(MINECRAFT.font, placeholder, innerLeft, contentTextY, PLACEHOLDER_COLOR, false);
		}

		handleCursor(graphics);

		if (renderOverlaysInline) {
			renderOverlays(graphics, mouseX, mouseY, deltaTicks);
		}
	}

	void renderOverlays(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		renderToolbar(graphics, mouseX, mouseY);
		renderModifierPicker(graphics, mouseX, mouseY, deltaTicks);
		renderModifierEditor(graphics, mouseX, mouseY, deltaTicks);
		renderConditionEditor(graphics, mouseX, mouseY, deltaTicks);
		renderColorPopup(graphics, mouseX, mouseY, deltaTicks);
		renderGradientPopup(graphics, mouseX, mouseY, deltaTicks);
		renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void handleCursor(@NotNull GuiGraphicsExtractor graphics) {
		if (this.isHovered()) {
			if (!this.isActive()) {
				graphics.requestCursor(Cursors.NOT_ALLOWED);
			} else if ((hoveredVariableHit != null
					&& (hoveredVariableHit.kind() == VariableHitKind.PLUS || hoveredVariableHit.kind() == VariableHitKind.MODIFIER))
					|| hoveredConditionItem != null) {
				graphics.requestCursor(Cursors.POINTING_HAND);
			} else {
				graphics.requestCursor(Cursors.IBEAM);
			}
		}
	}

	private void renderContent(GuiGraphicsExtractor graphics, int innerLeft, int contentTextY) {
		for (DisplayItem item : displayItems) {
			int drawX = innerLeft + item.x() - horizontalScroll;
			if (drawX + item.width() < innerLeft || drawX > getRight() - TEXT_PADDING_X) {
				continue;
			}

			if (item instanceof TextDisplayItem textDisplayItem) {
				graphics.text(MINECRAFT.font, textDisplayItem.text(), drawX, contentTextY, textDisplayItem.color(), false);
				continue;
			}

			if (item instanceof ConditionDisplayItem conditionDisplayItem) {
				int chipTop = getDisplayItemTop(conditionDisplayItem, contentTextY);
				boolean selected = isIndexSelected(conditionDisplayItem.modelIndex());
				int backgroundColor = selected ? CONDITION_SELECTED_BG_COLOR : CONDITION_BG_COLOR;
				graphics.fill(drawX, chipTop, drawX + conditionDisplayItem.width(), getDisplayItemBottom(conditionDisplayItem, contentTextY), backgroundColor);
				graphics.outline(drawX, chipTop, conditionDisplayItem.width(), conditionDisplayItem.height(), CONDITION_BORDER_COLOR);
				graphics.text(MINECRAFT.font, conditionDisplayItem.displayText(), drawX + VARIABLE_PADDING_X, contentTextY, conditionDisplayItem.color(), false);
				continue;
			}

			VariableDisplayItem variableDisplayItem = (VariableDisplayItem) item;
			int chipTop = getDisplayItemTop(variableDisplayItem, contentTextY);
			boolean selected = isIndexSelected(variableDisplayItem.modelIndex());
			int backgroundColor = selected ? VARIABLE_SELECTED_BG_COLOR : VARIABLE_BG_COLOR;
			int modifierBackgroundColor = selected ? MODIFIER_SELECTED_BG_COLOR : MODIFIER_BG_COLOR;
			graphics.fill(drawX, chipTop, drawX + variableDisplayItem.width(), getDisplayItemBottom(variableDisplayItem, contentTextY), backgroundColor);
			graphics.outline(drawX, chipTop, variableDisplayItem.width(), variableDisplayItem.height(), VARIABLE_BORDER_COLOR);

			graphics.text(MINECRAFT.font, variableDisplayItem.name(), drawX + VARIABLE_PADDING_X, contentTextY, variableDisplayItem.color(), false);

			for (int modifierIndex = 0; modifierIndex < variableDisplayItem.modifiers().size(); modifierIndex++) {
				ModifierPart modifierPart = variableDisplayItem.modifiers().get(modifierIndex);
				int modifierX = drawX + modifierPart.startX();
				graphics.fill(modifierX, chipTop + 1, modifierX + modifierPart.width(), chipTop + variableDisplayItem.height() - 1, modifierBackgroundColor);
				graphics.text(MINECRAFT.font, modifierPart.displayText(), modifierX + MODIFIER_PADDING_X, contentTextY, modifierPart.color(), false);
				if (modifierIndex < variableDisplayItem.modifiers().size() - 1) {
					int separatorX = modifierX + modifierPart.width() + MODIFIER_SEPARATOR_GAP / 2;
					graphics.fill(separatorX, chipTop + 2, separatorX + 1, chipTop + variableDisplayItem.height() - 1, MODIFIER_SEPARATOR_COLOR);
				}
			}

			graphics.text(MINECRAFT.font, "+", drawX + variableDisplayItem.plusX(), contentTextY, variableDisplayItem.color(), false);
		}
	}

	private void renderSelection(GuiGraphicsExtractor graphics, int innerLeft, int contentTextY) {
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
			graphics.fill(drawX, getDisplayItemTop(item, contentTextY), drawX + item.width(), getDisplayItemBottom(item, contentTextY), SELECTION_COLOR);
		}
	}

	private void renderCaret(GuiGraphicsExtractor graphics, int innerLeft, int contentTextY) {
		if (!isFocused() || hasSelection()) {
			return;
		}

		if ((System.currentTimeMillis() / 500L) % 2L == 0L) {
			return;
		}

		int caretX = innerLeft + caretPositions[Math.clamp(caretIndex, 0, caretPositions.length - 1)] - horizontalScroll;
		graphics.fill(caretX, contentTextY - 1, caretX + 1, contentTextY + MINECRAFT.font.lineHeight + 1, CARET_COLOR);
	}

	private void renderTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		if (hoveredTarget == null || System.currentTimeMillis() - hoverStartTime < DESCRIPTION_DELAY) {
			return;
		}

		Component tooltip = hoveredTarget.tooltip();
		if (tooltip.getString().isEmpty()) {
			return;
		}
		int innerWidth = Math.min(DESCRIPTION_MAX_WIDTH, MINECRAFT.font.width(tooltip));
		int width = innerWidth + DESCRIPTION_PADDING * 2;
		int height = MINECRAFT.font.wordWrapHeight(tooltip, innerWidth) + DESCRIPTION_PADDING * 2;

		int x = mouseX + 8;
		int y = mouseY + 8;
		Screen screen = MINECRAFT.gui.screen();
		if (screen != null) {
			if (x + width > screen.width) {
				x = mouseX - width - 8;
			}
			if (y + height > screen.height) {
				y = mouseY - height - 8;
			}
		}

		ScreenRectangle currentScissor = graphics.scissorStack.peek();
		if (currentScissor != null) {
			graphics.disableScissor();
		}
		try {
			graphics.fill(x, y, x + width, y + height, DESCRIPTION_BACKGROUND);
			graphics.textWithWordWrap(MINECRAFT.font, tooltip, x + DESCRIPTION_PADDING, y + DESCRIPTION_PADDING, innerWidth, TEXT_COLOR, false);
		} finally {
			if (currentScissor != null) {
				graphics.enableScissor(currentScissor.left(), currentScissor.top(), currentScissor.right(), currentScissor.bottom());
			}
		}
	}

	private int centeredTextY(int top, int height) {
		return top + Math.max(0, Math.floorDiv(height - MINECRAFT.font.lineHeight + 1, 2));
	}

	private int getContentTextY() {
		return centeredTextY(getY(), getHeight());
	}

	private int getDisplayItemTop(DisplayItem item, int contentTextY) {
		return isChipItem(item) ? contentTextY - VARIABLE_PADDING_Y : contentTextY - 1;
	}

	private int getDisplayItemBottom(DisplayItem item, int contentTextY) {
		return getDisplayItemTop(item, contentTextY) + item.height() + 1;
	}

	private boolean isChipItem(DisplayItem item) {
		return item instanceof VariableDisplayItem || item instanceof ConditionDisplayItem;
	}

	private void renderToolbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		if (!styleToolbarEnabled) {
			return;
		}
		for (ToolbarButton button : toolbarButtons) {
			renderButtonCenterLabel(graphics, button.bounds(), button.label(), button.backgroundColor(mouseX, mouseY), BUTTON_TEXT_COLOR, mouseX, mouseY);
		}
	}

	private void renderModifierPicker(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (modifierPickerPopup != null) {
			modifierPickerPopup.render(graphics, mouseX, mouseY, deltaTicks);
		}
	}

	private void renderModifierEditor(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (modifierEditorPopup != null) {
			modifierEditorPopup.render(graphics, mouseX, mouseY, deltaTicks);
		}
	}

	private void renderConditionEditor(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (conditionEditorPopup != null) {
			conditionEditorPopup.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		}
	}

	private void renderColorPopup(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (colorPopup != null) {
			colorPopup.render(graphics, mouseX, mouseY, deltaTicks);
		}
	}

	private void renderGradientPopup(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (gradientPopup != null) {
			gradientPopup.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		}
	}

	private void updateHover(int mouseX, int mouseY) {
		hoveredVariableHit = findVariableHit(mouseX, mouseY);
		hoveredConditionItem = findConditionDisplayItemAt(mouseX, mouseY);
		HoverTarget newTarget = findHoverTarget(mouseX, mouseY);
		if (!Objects.equals(newTarget, hoveredTarget)) {
			hoveredTarget = newTarget;
			hoverStartTime = System.currentTimeMillis();
		}
	}

	private @Nullable HoverTarget findHoverTarget(int mouseX, int mouseY) {
		for (int i = 1; i < toolbarButtons.size(); i++) {
			ToolbarButton button = toolbarButtons.get(i);
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
					return new HoverTarget(Component.translatable("flex_hud.create_module_screen.editor.add_modifier"));
				}

				for (ModifierPart modifierPart : variableDisplayItem.modifiers()) {
					if (modifierPart.startX() <= localX && localX <= modifierPart.startX() + modifierPart.width()) {
						return new HoverTarget(modifierPart.tooltip());
					}
				}

				return new HoverTarget(variableDisplayItem.element().variable().getDescription());
			}

			if (item instanceof ConditionDisplayItem conditionDisplayItem) {
				return new HoverTarget(Component.literal(conditionDisplayText(conditionDisplayItem.element().condition())));
			}
		}
		return null;
	}

	@Override
	public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
		if (!active) {
			return false;
		}

		if (gradientPopup != null && gradientPopup.mouseClicked(event, doubleClick)) {
			setFocused(true);
			return true;
		}
		if (colorPopup != null && colorPopup.mouseClicked(event, doubleClick)) {
			setFocused(true);
			return true;
		}
		if (modifierEditorPopup != null && modifierEditorPopup.mouseClicked(event, doubleClick)) {
			setFocused(true);
			return true;
		}
		if (conditionEditorPopup != null && conditionEditorPopup.mouseClicked(event, doubleClick)) {
			setFocused(true);
			return true;
		}
		if (modifierPickerPopup != null && modifierPickerPopup.mouseClicked(event, doubleClick)) {
			setFocused(true);
			return true;
		}

		ToolbarButton toolbarButton = findToolbarButton(event.x(), event.y());
		if (toolbarButton != null) {
			handleToolbarAction(toolbarButton.action());
			setFocused(true);
			return true;
		}

		if (!isInsideField(event.x(), event.y())) {
			closeTransientPopups();
			setFocused(false);
			return false;
		}

		setFocused(true);

		VariableHit variableHit = findVariableHit(event.x(), event.y());
		if (variableHit != null) {
			if (variableHit.kind() == VariableHitKind.PLUS) {
				openModifierPicker(variableHit.variableItem());
			} else if (variableHit.kind() == VariableHitKind.MODIFIER) {
				openModifierEditor(variableHit.variableItem(), variableHit.modifierIndex());
			} else if (doubleClick) {
				closeModifierPopups();
				closeSelectionPopups();
				selectWord(variableHit.variableItem().modelIndex());
			} else {
				handleBodyClick(event, variableHit.variableItem());
			}
			ensureCaretVisible();
			return true;
		}

		ConditionDisplayItem conditionHit = findConditionDisplayItemAt(event.x(), event.y());
		if (conditionHit != null) {
			openConditionEditor(conditionHit);
			caretIndex = conditionHit.modelIndex() + 1;
			selectionAnchor = caretIndex;
			ensureCaretVisible();
			refreshOverlayLayout();
			return true;
		}

		closeModifierPopups();
		closeSelectionPopups();

		if (doubleClick) {
			selectWordAt(event.x());
			draggingSelection = true;
			return true;
		}

		int clickedIndex = getClosestCaretIndex(event.x());
		if ((event.modifiers() & InputConstants.MOD_SHIFT) != 0) {
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
	public boolean mouseDragged(@NotNull MouseButtonEvent event, double dx, double dy) {
		if (gradientPopup != null && gradientPopup.mouseDragged(event, dx, dy)) {
			return true;
		}
		if (colorPopup != null && colorPopup.mouseDragged(event, dx, dy)) {
			return true;
		}
		if (modifierEditorPopup != null && modifierEditorPopup.mouseDragged(event, dx, dy)) {
			return true;
		}
		if (conditionEditorPopup != null && conditionEditorPopup.mouseDragged(event, dx, dy)) {
			return true;
		}
		if (modifierPickerPopup != null && modifierPickerPopup.mouseDragged(event, dx, dy)) {
			return true;
		}

		if (!draggingSelection || !isFocused()) {
			return false;
		}

		caretIndex = getClosestCaretIndex(event.x());
		ensureCaretVisible();
		refreshOverlayLayout();
		return true;
	}

	@Override
	public boolean mouseReleased(@NotNull MouseButtonEvent event) {
		boolean handled = false;
		if (gradientPopup != null) {
			handled |= gradientPopup.mouseReleased(event);
		}
		if (colorPopup != null) {
			handled |= colorPopup.mouseReleased(event);
		}
		if (modifierEditorPopup != null) {
			handled |= modifierEditorPopup.mouseReleased(event);
		}
		if (conditionEditorPopup != null) {
			handled |= conditionEditorPopup.mouseReleased(event);
		}
		if (modifierPickerPopup != null) {
			handled |= modifierPickerPopup.mouseReleased(event);
		}

		draggingSelection = false;
		refreshOverlayLayout();
		return handled;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		boolean handled = false;
		if (conditionEditorPopup != null) {
			handled = conditionEditorPopup.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}
		if (modifierPickerPopup != null) {
			handled |= modifierPickerPopup.mouseScrolled(mouseX, mouseY, verticalAmount);
		}

		return handled;
	}

	@Override
	public boolean charTyped(@NotNull CharacterEvent event) {
		if (!active) {
			return false;
		}

		if (gradientPopup != null && gradientPopup.charTyped(event)) {
			return true;
		}
		if (colorPopup != null && colorPopup.charTyped(event)) {
			return true;
		}
		if (modifierEditorPopup != null && modifierEditorPopup.charTyped(event)) {
			return true;
		}
		if (conditionEditorPopup != null && conditionEditorPopup.charTyped(event)) {
			return true;
		}
		if (modifierPickerPopup != null && modifierPickerPopup.charTyped(event)) {
			return true;
		}

		if (!isFocused() || !event.isAllowedChatCharacter()) {
			return false;
		}

		insertText(event.codepointAsString());
		return true;
	}

	@Override
	public boolean keyPressed(@NotNull KeyEvent event) {
		if (!active) {
			return false;
		}

		if (event.isEscape()) {
			if (gradientPopup != null) {
				closeGradientPopup();
				return true;
			}
			if (colorPopup != null) {
				closeColorPopup();
				return true;
			}
			if (modifierEditorPopup != null) {
				modifierEditorPopup = null;
				return true;
			}
			if (conditionEditorPopup != null) {
				conditionEditorPopup = null;
				return true;
			}
			if (modifierPickerPopup != null) {
				modifierPickerPopup = null;
				return true;
			}
		}

		if (gradientPopup != null && gradientPopup.keyPressed(event)) {
			return true;
		}
		if (colorPopup != null && colorPopup.keyPressed(event)) {
			return true;
		}
		if (modifierEditorPopup != null && modifierEditorPopup.keyPressed(event)) {
			return true;
		}
		if (conditionEditorPopup != null && conditionEditorPopup.keyPressed(event)) {
			return true;
		}
		if (modifierPickerPopup != null && modifierPickerPopup.keyPressed(event)) {
			return true;
		}

		if (!isFocused()) {
			return false;
		}

		if (event.isSelectAll()) {
			selectAll();
			return true;
		}
		if (event.isCopy()) {
			copySelectionToClipboard();
			return true;
		}
		if (event.isPaste()) {
			insertText(MINECRAFT.keyboardHandler.getClipboard());
			return true;
		}
		if (event.isCut()) {
			cutSelectionToClipboard();
			return true;
		}

		switch (event.keycode()) {
			case InputConstants.KEYCODE_BACKSPACE -> {
				erase(-1, event.hasControlDownWithQuirk());
				return true;
			}
			case InputConstants.KEYCODE_DELETE -> {
				erase(1, event.hasControlDownWithQuirk());
				return true;
			}
			case InputConstants.KEYCODE_LEFT -> {
				if (event.hasControlDownWithQuirk()) {
					setCaret(getWordSkipPosition(-1), event.hasShiftDown());
				} else {
					moveCaret(-1, event.hasShiftDown());
				}
				return true;
			}
			case InputConstants.KEYCODE_RIGHT -> {
				if (event.hasControlDownWithQuirk()) {
					setCaret(getWordSkipPosition(1), event.hasShiftDown());
				} else {
					moveCaret(1, event.hasShiftDown());
				}
				return true;
			}
			case InputConstants.KEYCODE_HOME -> {
				setCaret(0, event.hasShiftDown());
				return true;
			}
			case InputConstants.KEYCODE_END -> {
				setCaret(model.size(), event.hasShiftDown());
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
				|| (conditionEditorPopup != null && conditionEditorPopup.contains(mouseX, mouseY))
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
		MINECRAFT.keyboardHandler.setClipboard(getSelectedRawText());
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

	public void insertText(String text) {
		ModuleContentEditorModel.StyleState insertionStyle = model.getInsertionStyle(Math.min(caretIndex, selectionAnchor));
		ModuleContentEditorModel fragment = ModuleContentEditorModel.parse(text, insertionStyle);
		if (!canInsertFragment(fragment)) {
			return;
		}
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

	public void insertVariable(Variable<?> variable) {
		if (conditionEditorPopup != null && conditionEditorPopup.insertVariable(variable)) {
			return;
		}
		insertText("{" + variable.getKey() + "}");
	}

	public void insertCondition() {
		Variable<?> variable = defaultConditionVariable();
		if (variable == null) {
			return;
		}

		ModuleContentEditorModel.StyleState insertionStyle = model.getInsertionStyle(Math.min(caretIndex, selectionAnchor));
		CustomCondition.Condition condition = CustomCondition.defaultCondition(variable);
		ModuleContentEditorModel content = ModuleContentEditorModel.empty();
		int[] insertedIndex = new int[]{Math.min(caretIndex, selectionAnchor)};

		applyMutation(() -> {
			if (hasSelection()) {
				int start = Math.min(caretIndex, selectionAnchor);
				int end = Math.max(caretIndex, selectionAnchor);
				model.deleteRange(start, end);
				caretIndex = start;
				selectionAnchor = start;
			}

			insertedIndex[0] = caretIndex;
			model.insertCondition(caretIndex, condition, content, insertionStyle);
			caretIndex++;
			selectionAnchor = caretIndex;
		});

		ConditionDisplayItem conditionItem = findConditionDisplayItem(insertedIndex[0]);
		if (conditionItem != null) {
			openConditionEditor(conditionItem);
		}
	}

	private @Nullable Variable<?> defaultConditionVariable() {
		Variable<?> health = Variables.get("player.health");
		if (health != null) {
			return health;
		}
		return Variables.getAllVariables().values().stream().findFirst().orElse(null);
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

	boolean hasSelection() {
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
				int width = MINECRAFT.font.width(Component.literal(textElement.text()).setStyle(style));
				displayItem = new TextDisplayItem(index, x, Math.max(width, 1), MINECRAFT.font.lineHeight, textElement.text(), resolvedColor);
			} else if (element instanceof ModuleContentEditorModel.VariableElement variableElement) {
				displayItem = buildVariableDisplayItem(index, x, variableElement, style, resolvedColor);
			} else {
				displayItem = buildConditionDisplayItem(index, x, (ModuleContentEditorModel.ConditionElement) element, style, resolvedColor);
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
			return Math.max(1, MINECRAFT.font.width(Component.literal(textElement.text()).setStyle(style)));
		}
		if (element instanceof ModuleContentEditorModel.VariableElement variableElement) {
			return buildVariableDisplayItem(0, 0, variableElement, style, TEXT_COLOR).width();
		}
		return buildConditionDisplayItem(0, 0, (ModuleContentEditorModel.ConditionElement) element, style, TEXT_COLOR).width();
	}

	private VariableDisplayItem buildVariableDisplayItem(int modelIndex, int x, ModuleContentEditorModel.VariableElement variableElement, Style style, int color) {
		String name = variableElement.variable().getName().getString();
		int nameWidth = MINECRAFT.font.width(Component.literal(name).setStyle(style));

		List<ModifierPart> modifiers = new ArrayList<>();
		int currentX = VARIABLE_PADDING_X + nameWidth + VARIABLE_GAP;
		for (int modifierIndex = 0; modifierIndex < variableElement.modifiers().size(); modifierIndex++) {
			Modifiers.ResolvedModifier<?, ?> modifier = variableElement.modifiers().get(modifierIndex);
			String displaySuffix = modifier.modifier().uiMetadata().displayFormatter().apply(modifier.arguments());
			String displayName = modifier.modifier().uiMetadata().getName(modifier.modifier().key()).getString();
			String displayText = displaySuffix.isBlank() ? displayName : displayName + "(" + displaySuffix + ")";
			int textWidth = MINECRAFT.font.width(Component.literal(displayText).setStyle(style));
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
		int height = MINECRAFT.font.lineHeight + VARIABLE_PADDING_Y * 2;
		int plusX = width - VARIABLE_PADDING_X - CHIP_PLUS_WIDTH;

		return new VariableDisplayItem(modelIndex, x, width, height, color, variableElement, name, nameWidth, List.copyOf(modifiers), plusX);
	}

	private ConditionDisplayItem buildConditionDisplayItem(int modelIndex, int x, ModuleContentEditorModel.ConditionElement conditionElement, Style style, int color) {
		String displayText = Component.translatable("flex_hud.create_module_screen.editor.condition_chip").getString()
				+ " " + conditionDisplayText(conditionElement.condition())
				+ " -> " + conditionElement.content().visibleText();
		int textWidth = MINECRAFT.font.width(Component.literal(displayText).setStyle(style));
		int width = textWidth + VARIABLE_PADDING_X * 2;
		int height = MINECRAFT.font.lineHeight + VARIABLE_PADDING_Y * 2;
		return new ConditionDisplayItem(modelIndex, x, width, height, color, conditionElement, displayText);
	}

	static Component conditionConnectorLabel(CustomCondition.Connector connector) {
		return Component.translatable("flex_hud.create_module_screen.editor.condition_connector." + connector.key());
	}

	private static String conditionDisplayText(CustomCondition.Condition condition) {
		return condition.displayText(connector -> conditionConnectorLabel(connector).getString());
	}

	private Style toMinecraftStyle(ModuleContentEditorModel.StyleState style, int color) {
		return Style.EMPTY
				.withBold(style.bold())
				.withItalic(style.italic())
				.withUnderlined(style.underline())
				.withStrikethrough(style.strikethrough())
				.withObfuscated(style.obfuscated())
				.withColor(TextColor.fromRgb(color & 0x00ffffff));
	}

	private void refreshOverlayLayout() {
		SelectionBounds selectionBounds = computeSelectionBounds();
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

		if (conditionEditorPopup != null) {
			ConditionDisplayItem conditionItem = findConditionDisplayItem(conditionEditorPopup.elementIndex());
			if (conditionItem != null) {
				conditionEditorPopup.layout(conditionItem);
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
		if (draggingSelection || !hasSelection()) {
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
		if (!styleToolbarEnabled) {
			return List.of();
		}
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

		buttons.add(new ToolbarButton(ToolbarAction.NONE, panelBounds, Component.empty(), Component.empty(), ButtonState.NORMAL, null));
		buttons.add(new ToolbarButton(ToolbarAction.BOLD, new Bounds(cursorX, buttonY, TOOLBAR_BUTTON_WIDTH, BUTTON_HEIGHT), Component.literal("B"), Component.translatable("flex_hud.create_module_screen.editor.toolbar.bold"), triStateToButtonState(summary.bold()), null));
		cursorX += TOOLBAR_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.ITALIC, new Bounds(cursorX, buttonY, TOOLBAR_BUTTON_WIDTH, BUTTON_HEIGHT), Component.literal("I"), Component.translatable("flex_hud.create_module_screen.editor.toolbar.italic"), triStateToButtonState(summary.italic()), null));
		cursorX += TOOLBAR_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.UNDERLINE, new Bounds(cursorX, buttonY, TOOLBAR_BUTTON_WIDTH, BUTTON_HEIGHT), Component.literal("U"), Component.translatable("flex_hud.create_module_screen.editor.toolbar.underline"), triStateToButtonState(summary.underline()), null));
		cursorX += TOOLBAR_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.STRIKETHROUGH, new Bounds(cursorX, buttonY, TOOLBAR_BUTTON_WIDTH, BUTTON_HEIGHT), Component.literal("S"), Component.translatable("flex_hud.create_module_screen.editor.toolbar.strikethrough"), triStateToButtonState(summary.strikethrough()), null));
		cursorX += TOOLBAR_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.OBFUSCATED, new Bounds(cursorX, buttonY, TOOLBAR_BUTTON_WIDTH, BUTTON_HEIGHT), Component.literal("O"), Component.translatable("flex_hud.create_module_screen.editor.toolbar.obfuscated"), triStateToButtonState(summary.obfuscated()), null));
		cursorX += TOOLBAR_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.COLOR, new Bounds(cursorX, buttonY, TOOLBAR_ICON_BUTTON_WIDTH, BUTTON_HEIGHT), Component.translatable("flex_hud.create_module_screen.editor.toolbar.color_short"), Component.translatable("flex_hud.create_module_screen.editor.toolbar.color"), buttonState(summary.colorSummary().kind() == ModuleContentEditorModel.ColorSummaryKind.STATIC || summary.colorSummary().kind() == ModuleContentEditorModel.ColorSummaryKind.CHROMA, summary.colorSummary().kind() == ModuleContentEditorModel.ColorSummaryKind.MIXED), summary.colorSummary()));
		cursorX += TOOLBAR_ICON_BUTTON_WIDTH + POPUP_GAP;
		buttons.add(new ToolbarButton(ToolbarAction.GRADIENT, new Bounds(cursorX, buttonY, TOOLBAR_ICON_BUTTON_WIDTH, BUTTON_HEIGHT), Component.translatable("flex_hud.create_module_screen.editor.toolbar.gradient_short"), Component.translatable("flex_hud.create_module_screen.editor.toolbar.gradient"), buttonState(summary.colorSummary().kind() == ModuleContentEditorModel.ColorSummaryKind.GRADIENT, summary.colorSummary().kind() == ModuleContentEditorModel.ColorSummaryKind.MIXED), summary.colorSummary()));
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
			closeColorPopup();
			return;
		}

		closeGradientPopup();
		int selectionStart = Math.min(caretIndex, selectionAnchor);
		int selectionEnd = Math.max(caretIndex, selectionAnchor);
		ModuleContentEditorModel.ColorSummary colorSummary = model.summarize(selectionStart, selectionEnd).colorSummary();
		int initialColor = colorSummary.primaryColor() != null ? colorSummary.primaryColor() : 0xffffff;
		colorPopup = new ColorPopup(this, selectionStart, selectionEnd, initialColor);
		refreshOverlayLayout();
	}

	private void toggleGradientPopup() {
		if (!hasSelection()) {
			return;
		}

		if (gradientPopup != null) {
			closeGradientPopup();
			return;
		}

		closeColorPopup();
		int selectionStart = Math.min(caretIndex, selectionAnchor);
		int selectionEnd = Math.max(caretIndex, selectionAnchor);
		ModuleContentEditorModel.ColorSummary colorSummary = model.summarize(selectionStart, selectionEnd).colorSummary();
		int startColor = colorSummary.primaryColor() != null ? colorSummary.primaryColor() : 0xffffff;
		int endColor = colorSummary.secondaryColor() != null ? colorSummary.secondaryColor() : 0x55ffff;
		gradientPopup = new GradientPopup(this, selectionStart, selectionEnd, startColor, endColor);
		refreshOverlayLayout();
	}

	void applySelectionColorLayers(int selectionStart, int selectionEnd, List<ModuleContentEditorModel.ColorLayer> colorLayers) {
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
		if (conditionEditorPopup != null && findConditionDisplayItem(conditionEditorPopup.elementIndex()) == null) {
			conditionEditorPopup = null;
		}
		if (colorPopup != null && !colorPopup.matchesSelection()) {
			closeColorPopup();
		}
		if (gradientPopup != null && !gradientPopup.matchesSelection()) {
			closeGradientPopup();
		}
	}

	private void closeTransientPopups() {
		closeModifierPopups();
		conditionEditorPopup = null;
		closeSelectionPopups();
	}

	private void closeModifierPopups() {
		modifierPickerPopup = null;
		modifierEditorPopup = null;
		conditionEditorPopup = null;
	}

	private void closeSelectionPopups() {
		closeColorPopup();
		closeGradientPopup();
	}

	private void closeModifierPicker() {
		modifierPickerPopup = null;
	}

	void closeColorPopup() {
		colorPopup = null;
	}

	void closeGradientPopup() {
		gradientPopup = null;
	}

	int selectionPopupPreferredY(SelectionBounds selectionBounds) {
		return toolbarButtons.isEmpty() ? selectionBounds.bottom() + OVERLAY_GAP : toolbarButtons.getFirst().bounds().bottom() + OVERLAY_GAP;
	}

	boolean selectionMatches(int selectionStart, int selectionEnd) {
		return hasSelection()
				&& selectionStart == Math.min(caretIndex, selectionAnchor)
				&& selectionEnd == Math.max(caretIndex, selectionAnchor);
	}

	void renderButtonCenterLabel(GuiGraphicsExtractor graphics, Bounds bounds, Component label, int backgroundColor, int textColor, double mouseX, double mouseY) {
		if (!label.getString().isEmpty()) {
			graphics.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), backgroundColor);
			graphics.outline(bounds.x(), bounds.y(), bounds.width(), bounds.height(), POPUP_BORDER);
			int textX = bounds.x() + (bounds.width() - MINECRAFT.font.width(label)) / 2;
			int textY = centeredTextY(bounds.y(), bounds.height());
			graphics.text(MINECRAFT.font, label, textX, textY, textColor, false);

			if (bounds.contains(mouseX, mouseY)) {
				graphics.requestCursor(Cursors.POINTING_HAND);
			}
		} else {
			graphics.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), POPUP_BACKGROUND);
			graphics.outline(bounds.x(), bounds.y(), bounds.width(), bounds.height(), POPUP_BORDER);
		}
	}

	void renderButton(GuiGraphicsExtractor graphics, Bounds bounds, Component label, int backgroundColor, int textColor, int padding, double mouseX, double mouseY) {
		if (!label.getString().isEmpty()) {
			graphics.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), backgroundColor);
			graphics.outline(bounds.x(), bounds.y(), bounds.width(), bounds.height(), POPUP_BORDER);
			int textX = bounds.x() + padding;
			int textY = centeredTextY(bounds.y(), bounds.height());
			graphics.text(MINECRAFT.font, label, textX, textY, textColor, false);

			if (bounds.contains(mouseX, mouseY)) {
				graphics.requestCursor(Cursors.POINTING_HAND);
			}
		} else {
			graphics.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), POPUP_BACKGROUND);
			graphics.outline(bounds.x(), bounds.y(), bounds.width(), bounds.height(), POPUP_BORDER);
		}
	}

	void renderPanel(GuiGraphicsExtractor graphics, Bounds bounds) {
		graphics.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), POPUP_BACKGROUND);
		graphics.outline(bounds.x(), bounds.y(), bounds.width(), bounds.height(), POPUP_BORDER);
	}

	int clampX(int x, int width) {
		Screen screen = MINECRAFT.gui.screen();
		if (screen == null) {
			return x;
		}
		return Math.clamp(x, 4, Math.max(4, screen.width - width - 4));
	}

	int clampY(int y, int height) {
		Screen screen = MINECRAFT.gui.screen();
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

	private @Nullable ConditionDisplayItem findConditionDisplayItemAt(double mouseX, double mouseY) {
		int innerLeft = getX() + TEXT_PADDING_X;
		int contentTextY = getContentTextY();
		for (DisplayItem item : displayItems) {
			if (!(item instanceof ConditionDisplayItem conditionDisplayItem)) {
				continue;
			}

			int drawX = innerLeft + item.x() - horizontalScroll;
			if (mouseY < getDisplayItemTop(item, contentTextY) || mouseY > getDisplayItemBottom(item, contentTextY)) {
				continue;
			}
			if (drawX <= mouseX && mouseX <= drawX + item.width()) {
				return conditionDisplayItem;
			}
		}
		return null;
	}

	private void openModifierPicker(VariableDisplayItem variableItem) {
		modifierEditorPopup = null;
		conditionEditorPopup = null;
		closeSelectionPopups();
		modifierPickerPopup = new ModifierPickerPopup(this, variableItem.modelIndex());
		modifierPickerPopup.layout(variableItem);
	}

	private void openModifierEditor(VariableDisplayItem variableItem, int modifierIndex) {
		closeModifierPicker();
		conditionEditorPopup = null;
		closeSelectionPopups();
		modifierEditorPopup = new ModifierEditorPopup(this, variableItem.modelIndex(), modifierIndex);
		modifierEditorPopup.layout(variableItem);
	}

	private void openConditionEditor(ConditionDisplayItem conditionItem) {
		closeModifierPopups();
		closeSelectionPopups();
		conditionEditorPopup = new ConditionEditorPopup(this, conditionItem.modelIndex());
		conditionEditorPopup.layout(conditionItem);
	}

	private void handleBodyClick(MouseButtonEvent event, VariableDisplayItem variableItem) {
		closeModifierPopups();
		closeSelectionPopups();
		int drawX = getX() + TEXT_PADDING_X + variableItem.x() - horizontalScroll;
		int clickedIndex = event.x() < drawX + variableItem.width() / 2.0 ? variableItem.modelIndex() : variableItem.modelIndex() + 1;
		if ((event.modifiers() & InputConstants.MOD_SHIFT) != 0) {
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

	@Nullable ConditionDisplayItem findConditionDisplayItem(int elementIndex) {
		for (DisplayItem item : displayItems) {
			if (item.modelIndex() == elementIndex && item instanceof ConditionDisplayItem conditionDisplayItem) {
				return conditionDisplayItem;
			}
		}
		return null;
	}

	List<Modifier<?, ?>> getCompatibleModifiers(ModuleContentEditorModel.VariableElement variableElement) {
		List<Modifier<?, ?>> compatible = new ArrayList<>();
		for (Modifier<?, ?> modifier : Modifiers.getAll()) {
			if (modifier.key().equals("conditional")) {
				continue;
			}
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

	static boolean isSignedDecimalInput(String text) {
		return SIGNED_DECIMAL_INPUT.matcher(text).matches();
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
				modifierEditorPopup.setError(Component.translatable("flex_hud.create_module_screen.editor.invalid_modifier"));
			}
			return;
		}

		applyMutation(() -> model.updateVariableModifiers(elementIndex, modifiers));
		modifierPickerPopup = null;
		modifierEditorPopup = null;
	}

	void applyConditionChange(int elementIndex, CustomCondition.@Nullable Condition condition, @Nullable ModuleContentEditorModel content, boolean deleteCondition) {
		if (elementIndex < 0 || elementIndex >= model.size() || !(model.get(elementIndex) instanceof ModuleContentEditorModel.ConditionElement)) {
			return;
		}

		if (deleteCondition) {
			applyMutation(() -> {
				model.deleteRange(elementIndex, elementIndex + 1);
				caretIndex = Math.clamp(elementIndex, 0, model.size());
				selectionAnchor = caretIndex;
			});
		} else if (condition != null && content != null) {
			applyMutation(() -> model.updateCondition(elementIndex, condition, content));
		}

		conditionEditorPopup = null;
	}

	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
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
	public void revertChanges() {
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

	public void setText(String text) {
		rawText = text == null ? "" : text;
		model = ModuleContentEditorModel.parse(rawText);
		caretIndex = model.size();
		selectionAnchor = caretIndex;
		horizontalScroll = 0;
		closeTransientPopups();
		rebuildLayout();
		changedListener.accept(rawText);
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setChangedListener(Consumer<String> changedListener) {
		this.changedListener = changedListener;
	}

	public void setPlaceholder(@Nullable Component placeholder) {
		this.placeholder = placeholder;
	}

	public void setPlainTextInputEnabled(boolean plainTextInputEnabled) {
		this.plainTextInputEnabled = plainTextInputEnabled;
	}

	public void setRenderOverlaysInline(boolean renderOverlaysInline) {
		this.renderOverlaysInline = renderOverlaysInline;
	}

	public void setStyleToolbarEnabled(boolean styleToolbarEnabled) {
		this.styleToolbarEnabled = styleToolbarEnabled;
		refreshOverlayLayout();
	}

	private boolean canInsertFragment(ModuleContentEditorModel fragment) {
		if (plainTextInputEnabled) {
			return true;
		}

		for (ModuleContentEditorModel.InlineElement element : fragment.elements()) {
			if (!(element instanceof ModuleContentEditorModel.VariableElement)) {
				return false;
			}
		}
		return true;
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
	                             Component label,
	                             Component tooltip,
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
}
