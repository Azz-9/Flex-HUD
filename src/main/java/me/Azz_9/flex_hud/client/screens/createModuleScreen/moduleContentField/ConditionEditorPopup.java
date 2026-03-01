package me.Azz_9.flex_hud.client.screens.createModuleScreen.moduleContentField;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.Variables;
import me.Azz_9.flex_hud.client.customModules.text.CustomCondition;
import me.Azz_9.flex_hud.client.screens.createModuleScreen.ModuleContentEditorModel;
import me.Azz_9.flex_hud.client.screens.widgets.textFieldWidget.FilteredEditBox;

final class ConditionEditorPopup {
	private static final int FIELD_HEIGHT = 18;
	private static final int ROW_GAP = 4;
	private static final int LABEL_FIELD_GAP = 2;
	private static final int CONNECTOR_WIDTH = 40;
	private static final int OPERATOR_WIDTH = 30;
	private static final int THRESHOLD_WIDTH = 58;
	private static final int STRING_THRESHOLD_WIDTH = 110;
	private static final int REMOVE_WIDTH = 16;
	private static final int MIN_WIDTH = 260;
	private static final int MAX_WIDTH = 360;
	private static final List<CustomCondition.Operator> NUMERIC_OPERATOR_ORDER = List.of(
			CustomCondition.Operator.GREATER_THAN,
			CustomCondition.Operator.LOWER_THAN,
			CustomCondition.Operator.EQUAL,
			CustomCondition.Operator.GREATER_OR_EQUAL,
			CustomCondition.Operator.LOWER_OR_EQUAL,
			CustomCondition.Operator.NOT_EQUAL
	);
	private static final List<CustomCondition.Operator> STRING_OPERATOR_ORDER = List.of(
			CustomCondition.Operator.EQUAL,
			CustomCondition.Operator.NOT_EQUAL
	);

	private final ModuleContentField host;
	private final int elementIndex;
	private final List<ConditionRow> rows = new ArrayList<>();
	private final ModuleContentField contentField = new ModuleContentField(0, 0, 160, FIELD_HEIGHT, "");
	private AbstractWidget activeDragWidget;
	private Component error = Component.empty();

	private Bounds bounds = new Bounds(0, 0, 0, 0);
	private Bounds addBounds = new Bounds(0, 0, 0, 0);
	private Bounds saveBounds = new Bounds(0, 0, 0, 0);
	private Bounds cancelBounds = new Bounds(0, 0, 0, 0);
	private Bounds deleteBounds = new Bounds(0, 0, 0, 0);

	ConditionEditorPopup(ModuleContentField host, int elementIndex) {
		this.host = host;
		this.elementIndex = elementIndex;
		ModuleContentEditorModel.ConditionElement conditionElement = (ModuleContentEditorModel.ConditionElement) host.model.get(elementIndex);
		for (CustomCondition.Term term : conditionElement.condition().terms()) {
			CustomCondition.Clause clause = term.clause();
			rows.add(new ConditionRow(term.connector(), clause.operand().format(), clause.operator(), clause.threshold()));
		}
		if (rows.isEmpty()) {
			rows.add(defaultRow());
		}

		contentField.setText(conditionElement.content().serialize());
		contentField.setMaxLength(256);
		contentField.setRenderOverlaysInline(false);
	}

	int elementIndex() {
		return elementIndex;
	}

	void layout(ConditionDisplayItem conditionItem) {
		int width = computeWidth();
		int titleHeight = MINECRAFT.font.lineHeight;
		int errorHeight = error.getString().isBlank() ? 0 : MINECRAFT.font.wordWrapHeight(error, width - ModuleContentField.POPUP_PADDING * 2) + ModuleContentField.POPUP_GAP;
		int height = ModuleContentField.POPUP_PADDING * 2
				+ titleHeight + ModuleContentField.POPUP_GAP
				+ rows.size() * (FIELD_HEIGHT + ROW_GAP)
				+ FIELD_HEIGHT + ModuleContentField.POPUP_GAP
				+ MINECRAFT.font.lineHeight + LABEL_FIELD_GAP + FIELD_HEIGHT + ModuleContentField.POPUP_GAP
				+ errorHeight
				+ ModuleContentField.BUTTON_HEIGHT;

		int preferredX = host.getX() + ModuleContentField.TEXT_PADDING_X + conditionItem.x() - host.horizontalScroll;
		int preferredY = host.getBottom() + ModuleContentField.OVERLAY_GAP;
		Screen screen = MINECRAFT.gui.screen();
		if (screen != null && preferredY + height > screen.height - 4) {
			preferredY = host.getY() - height - ModuleContentField.OVERLAY_GAP;
		}

		bounds = new Bounds(host.clampX(preferredX, width), host.clampY(preferredY, height), width, height);

		int cursorY = bounds.y() + ModuleContentField.POPUP_PADDING + titleHeight + ModuleContentField.POPUP_GAP;
		int innerX = bounds.x() + ModuleContentField.POPUP_PADDING;
		int innerWidth = bounds.width() - ModuleContentField.POPUP_PADDING * 2;
		for (int index = 0; index < rows.size(); index++) {
			rows.get(index).layout(innerX, cursorY, innerWidth, index > 0);
			cursorY += FIELD_HEIGHT + ROW_GAP;
		}

		addBounds = new Bounds(innerX, cursorY, textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.add_condition_clause")), ModuleContentField.BUTTON_HEIGHT);
		cursorY += FIELD_HEIGHT + ModuleContentField.POPUP_GAP;

		contentField.setPosition(innerX, cursorY + MINECRAFT.font.lineHeight + LABEL_FIELD_GAP);
		contentField.setWidth(innerWidth);

		int buttonsY = bounds.bottom() - ModuleContentField.POPUP_PADDING - ModuleContentField.BUTTON_HEIGHT;
		int deleteWidth = textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.delete_condition"));
		int saveWidth = textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.apply"));
		int cancelWidth = textButtonWidth(Component.translatable("flex_hud.global.config.cancel"));
		int totalButtonsWidth = deleteWidth + ModuleContentField.POPUP_GAP + saveWidth + ModuleContentField.POPUP_GAP + cancelWidth;
		int buttonX = bounds.right() - ModuleContentField.POPUP_PADDING - totalButtonsWidth;

		deleteBounds = new Bounds(buttonX, buttonsY, deleteWidth, ModuleContentField.BUTTON_HEIGHT);
		buttonX += deleteWidth + ModuleContentField.POPUP_GAP;
		saveBounds = new Bounds(buttonX, buttonsY, saveWidth, ModuleContentField.BUTTON_HEIGHT);
		buttonX += saveWidth + ModuleContentField.POPUP_GAP;
		cancelBounds = new Bounds(buttonX, buttonsY, cancelWidth, ModuleContentField.BUTTON_HEIGHT);
	}

	void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		host.renderPanel(graphics, bounds);
		graphics.text(MINECRAFT.font, Component.translatable("flex_hud.create_module_screen.editor.condition"), bounds.x() + ModuleContentField.POPUP_PADDING, bounds.y() + ModuleContentField.POPUP_PADDING, ModuleContentField.TEXT_COLOR, false);

		for (ConditionRow row : rows) {
			row.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		}

		host.renderButton(graphics, addBounds, Component.translatable("flex_hud.create_module_screen.editor.add_condition_clause"), addBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, ModuleContentField.BUTTON_HORIZONTAL_PADDING, mouseX, mouseY);

		int contentLabelY = contentField.getY() - MINECRAFT.font.lineHeight - LABEL_FIELD_GAP;
		graphics.text(MINECRAFT.font, Component.translatable("flex_hud.create_module_screen.editor.condition_content"), contentField.getX(), contentLabelY, ModuleContentField.TEXT_COLOR, false);
		contentField.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		if (!error.getString().isBlank()) {
			int errorWidth = bounds.width() - ModuleContentField.POPUP_PADDING * 2;
			int errorHeight = MINECRAFT.font.wordWrapHeight(error, errorWidth);
			graphics.textWithWordWrap(MINECRAFT.font, error, bounds.x() + ModuleContentField.POPUP_PADDING, saveBounds.y() - ModuleContentField.POPUP_GAP - errorHeight, errorWidth, ModuleContentField.POPUP_ERROR_COLOR, false);
		}

		host.renderButtonCenterLabel(graphics, deleteBounds, Component.translatable("flex_hud.create_module_screen.editor.delete_condition"), deleteBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
		host.renderButtonCenterLabel(graphics, saveBounds, Component.translatable("flex_hud.create_module_screen.editor.apply"), saveBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
		host.renderButtonCenterLabel(graphics, cancelBounds, Component.translatable("flex_hud.global.config.cancel"), cancelBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);

		for (ConditionRow row : rows) {
			row.renderOverlays(graphics, mouseX, mouseY, deltaTicks);
		}
		contentField.renderOverlays(graphics, mouseX, mouseY, deltaTicks);
	}

	boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
		activeDragWidget = null;
		setAllFieldsFocused(false);
		for (ConditionRow row : rows) {
			if (row.mouseClicked(event, doubled)) {
				return true;
			}
		}
		if (contentField.isMouseOver(event.x(), event.y()) && contentField.mouseClicked(event, doubled)) {
			activeDragWidget = contentField;
			return true;
		}
		if (addBounds.contains(event.x(), event.y())) {
			rows.add(defaultRow());
			layoutFromCurrentAnchor();
			return true;
		}
		if (deleteBounds.contains(event.x(), event.y())) {
			host.applyConditionChange(elementIndex, null, null, true);
			return true;
		}
		if (saveBounds.contains(event.x(), event.y())) {
			save();
			return true;
		}
		if (cancelBounds.contains(event.x(), event.y())) {
			host.conditionEditorPopup = null;
			return true;
		}
		return bounds.contains(event.x(), event.y());
	}

	boolean mouseDragged(MouseButtonEvent event, double offsetX, double offsetY) {
		if (activeDragWidget != null) {
			return activeDragWidget.mouseDragged(event, offsetX, offsetY);
		}
		return false;
	}

	boolean mouseReleased(MouseButtonEvent event) {
		if (activeDragWidget != null) {
			boolean handled = activeDragWidget.mouseReleased(event);
			activeDragWidget = null;
			return handled;
		}
		return false;
	}

	boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		for (ConditionRow row : rows) {
			if (row.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
				return true;
			}
		}
		return contentField.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	boolean keyPressed(KeyEvent event) {
		for (ConditionRow row : rows) {
			if (row.keyPressed(event)) {
				return true;
			}
		}
		if (contentField.isFocused() && contentField.keyPressed(event)) {
			return true;
		}
		if (event.key() == GLFW.GLFW_KEY_ENTER || event.key() == GLFW.GLFW_KEY_KP_ENTER) {
			save();
			return true;
		}
		if (event.key() == GLFW.GLFW_KEY_TAB) {
			focusNextField();
			return true;
		}
		return false;
	}

	boolean charTyped(CharacterEvent event) {
		for (ConditionRow row : rows) {
			if (row.charTyped(event)) {
				return true;
			}
		}
		return contentField.isFocused() && contentField.charTyped(event);
	}

	boolean contains(double mouseX, double mouseY) {
		if (bounds.contains(mouseX, mouseY) || contentField.isMouseOver(mouseX, mouseY)) {
			return true;
		}
		for (ConditionRow row : rows) {
			if (row.contains(mouseX, mouseY)) {
				return true;
			}
		}
		return false;
	}

	boolean insertVariable(Variable<?> variable) {
		for (ConditionRow row : rows) {
			if (row.variableField().isFocused()) {
				row.variableField().setText(wrapOperand(variable.getKey()));
				layoutFromCurrentAnchor();
				return true;
			}
		}

		if (contentField.isFocused()) {
			contentField.insertVariable(variable);
			return true;
		}

		return false;
	}

	private void save() {
		CustomCondition.Condition condition = CustomCondition.parse(rawCondition(), Variables::get);
		if (condition == null) {
			error = Component.translatable("flex_hud.create_module_screen.editor.invalid_condition");
			layoutFromCurrentAnchor();
			return;
		}

		ModuleContentEditorModel content = ModuleContentEditorModel.parse(contentField.getText());
		host.applyConditionChange(elementIndex, condition, content, false);
	}

	private String rawCondition() {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < rows.size(); index++) {
			ConditionRow row = rows.get(index);
			if (index > 0) {
				builder.append(row.connector().symbol());
			}
			builder.append(row.rawClause());
		}
		return builder.toString();
	}

	private int computeWidth() {
		int contentWidth = 0;
		for (int index = 0; index < rows.size(); index++) {
			contentWidth = Math.max(contentWidth, rows.get(index).desiredWidth(index > 0));
		}
		contentWidth = Math.max(contentWidth, 180);
		contentWidth = Math.max(contentWidth, textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.add_condition_clause")));
		contentWidth = Math.max(contentWidth, buttonsRowWidth());
		contentWidth = Math.max(contentWidth, MINECRAFT.font.width(Component.translatable("flex_hud.create_module_screen.editor.condition")));
		return Math.clamp(contentWidth + ModuleContentField.POPUP_PADDING * 2, MIN_WIDTH, MAX_WIDTH);
	}

	private int buttonsRowWidth() {
		return textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.delete_condition"))
				+ ModuleContentField.POPUP_GAP
				+ textButtonWidth(Component.translatable("flex_hud.create_module_screen.editor.apply"))
				+ ModuleContentField.POPUP_GAP
				+ textButtonWidth(Component.translatable("flex_hud.global.config.cancel"));
	}

	private int textButtonWidth(Component text) {
		return MINECRAFT.font.width(text) + ModuleContentField.BUTTON_HORIZONTAL_PADDING * 2;
	}

	private static String wrapOperand(String operand) {
		String trimmedOperand = operand.strip();
		if (trimmedOperand.isEmpty() || trimmedOperand.startsWith("{") && trimmedOperand.endsWith("}")) {
			return trimmedOperand;
		}
		return "{" + trimmedOperand + "}";
	}

	private ConditionRow defaultRow() {
		Variable<?> variable = Variables.get("player.health");
		if (variable == null) {
			variable = Variables.getAllVariables().values().stream().findFirst().orElse(null);
		}
		return new ConditionRow(CustomCondition.Connector.AND, variable != null ? variable.getKey() : "", CustomCondition.Operator.GREATER_THAN, "0");
	}

	private void layoutFromCurrentAnchor() {
		ConditionDisplayItem conditionDisplayItem = host.findConditionDisplayItem(elementIndex);
		if (conditionDisplayItem != null) {
			layout(conditionDisplayItem);
		}
	}

	private void focusNextField() {
		List<AbstractWidget> fields = new ArrayList<>();
		for (ConditionRow row : rows) {
			fields.add(row.variableField());
			fields.add(row.thresholdField());
		}
		fields.add(contentField);

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
		for (ConditionRow row : rows) {
			row.variableField().setFocused(focused);
			row.thresholdField().setFocused(focused);
		}
		contentField.setFocused(focused);
	}

	private final class ConditionRow {
		private final ModuleContentField variableField;
		private final PopupTextFieldWidget thresholdField;
		private CustomCondition.Connector connector;
		private CustomCondition.Operator operator;
		private Bounds connectorBounds = new Bounds(0, 0, 0, 0);
		private Bounds operatorBounds = new Bounds(0, 0, 0, 0);
		private Bounds removeBounds = new Bounds(0, 0, 0, 0);

		private ConditionRow(CustomCondition.Connector connector, String variable, CustomCondition.Operator operator, String threshold) {
			this.connector = connector;
			this.variableField = new ModuleContentField(0, 0, 120, FIELD_HEIGHT, wrapOperand(variable));
			this.variableField.setMaxLength(128);
			this.variableField.setPlaceholder(Component.empty());
			this.variableField.setPlainTextInputEnabled(false);
			this.variableField.setRenderOverlaysInline(false);
			this.variableField.setStyleToolbarEnabled(false);
			this.operator = operator;
			this.thresholdField = new PopupTextFieldWidget(THRESHOLD_WIDTH, FIELD_HEIGHT);
			this.thresholdField.setValue(threshold);
			this.thresholdField.setMaxLength(128);
			updateTypeState();
		}

		private void layout(int x, int y, int width, boolean showConnector) {
			updateTypeState();
			int variableX = x;
			int rowWidth = width;
			if (showConnector) {
				connectorBounds = new Bounds(x, y, CONNECTOR_WIDTH, FIELD_HEIGHT);
				variableX = connectorBounds.right() + ModuleContentField.POPUP_GAP;
				rowWidth -= CONNECTOR_WIDTH + ModuleContentField.POPUP_GAP;
			} else {
				connectorBounds = new Bounds(0, 0, 0, 0);
			}

			int thresholdWidth = thresholdWidth();
			int variableWidth = rowWidth - OPERATOR_WIDTH - thresholdWidth - REMOVE_WIDTH - ModuleContentField.POPUP_GAP * 3;
			variableField.setPosition(variableX, y);
			variableField.setWidth(Math.max(80, variableWidth));
			operatorBounds = new Bounds(variableField.getRight() + ModuleContentField.POPUP_GAP, y, OPERATOR_WIDTH, FIELD_HEIGHT);
			thresholdField.setWidth(thresholdWidth);
			thresholdField.setPosition(operatorBounds.right() + ModuleContentField.POPUP_GAP, y);
			removeBounds = new Bounds(x + width - REMOVE_WIDTH, y, REMOVE_WIDTH, FIELD_HEIGHT);
		}

		private void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
			if (connectorBounds.width() > 0) {
				host.renderButtonCenterLabel(graphics, connectorBounds, ModuleContentField.conditionConnectorLabel(connector), connectorBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
			}
			variableField.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
			host.renderButtonCenterLabel(graphics, operatorBounds, Component.literal(operator.primarySymbol()), operatorBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
			thresholdField.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
			host.renderButtonCenterLabel(graphics, removeBounds, Component.literal("x"), removeBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
		}

		private void renderOverlays(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
			variableField.renderOverlays(graphics, mouseX, mouseY, deltaTicks);
		}

		private boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
			if (connectorBounds.width() > 0 && connectorBounds.contains(event.x(), event.y())) {
				connector = connector.next();
				return true;
			}
			if (operatorBounds.contains(event.x(), event.y())) {
				operator = nextOperator(operator, valueKind());
				return true;
			}
			if (removeBounds.contains(event.x(), event.y())) {
				rows.remove(this);
				if (rows.isEmpty()) {
					rows.add(defaultRow());
				}
				layoutFromCurrentAnchor();
				return true;
			}
			if (variableField.isMouseOver(event.x(), event.y()) && variableField.mouseClicked(event, doubled)) {
				activeDragWidget = variableField;
				return true;
			}
			if (thresholdField.isMouseOver(event.x(), event.y()) && thresholdField.mouseClicked(event, doubled)) {
				activeDragWidget = thresholdField;
				return true;
			}
			return false;
		}

		private boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
			return variableField.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}

		private boolean keyPressed(KeyEvent event) {
			boolean handled = (variableField.isFocused() && variableField.keyPressed(event))
					|| (thresholdField.isFocused() && thresholdField.keyPressed(event));
			if (handled) {
				updateTypeState();
			}
			return handled;
		}

		private boolean charTyped(CharacterEvent event) {
			boolean handled = (variableField.isFocused() && variableField.charTyped(event))
					|| (thresholdField.isFocused() && thresholdField.charTyped(event));
			if (handled) {
				updateTypeState();
			}
			return handled;
		}

		private String rawClause() {
			String threshold = valueKind() == CustomCondition.ValueKind.STRING
					? CustomCondition.formatStringLiteral(thresholdField.getValue())
					: thresholdField.getValue().strip();
			return variableField.getText().strip() + operator.primarySymbol() + threshold;
		}

		private int desiredWidth(boolean showConnector) {
			int thresholdWidth = thresholdWidth();
			int width = MINECRAFT.font.width(variableField.getText())
					+ OPERATOR_WIDTH
					+ thresholdWidth
					+ REMOVE_WIDTH
					+ ModuleContentField.POPUP_GAP * 3;
			if (showConnector) {
				width += CONNECTOR_WIDTH + ModuleContentField.POPUP_GAP;
			}
			return width;
		}

		private CustomCondition.Connector connector() {
			return connector;
		}

		private boolean contains(double mouseX, double mouseY) {
			return variableField.isMouseOver(mouseX, mouseY) || thresholdField.isMouseOver(mouseX, mouseY);
		}

		private ModuleContentField variableField() {
			return variableField;
		}

		private PopupTextFieldWidget thresholdField() {
			return thresholdField;
		}

		private CustomCondition.ValueKind valueKind() {
			CustomCondition.ValueKind valueKind = CustomCondition.resolveOperandValueKind(variableField.getText(), Variables::get);
			return valueKind != null ? valueKind : CustomCondition.ValueKind.UNKNOWN;
		}

		private void updateTypeState() {
			CustomCondition.ValueKind valueKind = valueKind();
			if (valueKind == CustomCondition.ValueKind.STRING) {
				if (!operator.supportsStringComparison()) {
					operator = CustomCondition.Operator.EQUAL;
				}
				thresholdField.setFilter(text -> true);
				return;
			}

			thresholdField.setFilter(ModuleContentField::isSignedDecimalInput);
		}

		private int thresholdWidth() {
			return valueKind() == CustomCondition.ValueKind.STRING ? STRING_THRESHOLD_WIDTH : THRESHOLD_WIDTH;
		}
	}

	private static CustomCondition.Operator nextOperator(CustomCondition.Operator operator, CustomCondition.ValueKind valueKind) {
		List<CustomCondition.Operator> operatorOrder = valueKind == CustomCondition.ValueKind.STRING ? STRING_OPERATOR_ORDER : NUMERIC_OPERATOR_ORDER;
		int index = operatorOrder.indexOf(operator);
		return operatorOrder.get((index + 1) % operatorOrder.size());
	}

	private static final class PopupTextFieldWidget extends FilteredEditBox {
		private PopupTextFieldWidget(int width, int height) {
			super(MINECRAFT.font, width, height, Component.empty());
		}

		@Override
		public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubled) {
			if (this.active && this.visible && this.isValidClickButton(event.buttonInfo())) {
				setFocused(this.isMouseOver(event.x(), event.y()));
			}
			return super.mouseClicked(event, doubled);
		}
	}
}
