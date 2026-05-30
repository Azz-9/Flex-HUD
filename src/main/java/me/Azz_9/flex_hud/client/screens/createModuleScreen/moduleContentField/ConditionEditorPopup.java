package me.Azz_9.flex_hud.client.screens.createModuleScreen.moduleContentField;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.Variables;
import me.Azz_9.flex_hud.client.customModules.text.CustomCondition;
import me.Azz_9.flex_hud.client.screens.createModuleScreen.ModuleContentEditorModel;

final class ConditionEditorPopup {
	private static final int FIELD_HEIGHT = 18;
	private static final int ROW_GAP = 4;
	private static final int LABEL_FIELD_GAP = 2;
	private static final int CONNECTOR_WIDTH = 40;
	private static final int OPERATOR_WIDTH = 30;
	private static final int THRESHOLD_WIDTH = 58;
	private static final int REMOVE_WIDTH = 16;
	private static final int MIN_WIDTH = 260;
	private static final int MAX_WIDTH = 360;
	private static final List<CustomCondition.Operator> OPERATOR_ORDER = List.of(
			CustomCondition.Operator.GREATER_THAN,
			CustomCondition.Operator.LOWER_THAN,
			CustomCondition.Operator.EQUAL,
			CustomCondition.Operator.GREATER_OR_EQUAL,
			CustomCondition.Operator.LOWER_OR_EQUAL,
			CustomCondition.Operator.NOT_EQUAL
	);

	private final ModuleContentField host;
	private final int elementIndex;
	private final List<ConditionRow> rows = new ArrayList<>();
	private final ModuleContentField contentField = new ModuleContentField(0, 0, 160, FIELD_HEIGHT, "");
	private ClickableWidget activeDragWidget;
	private Text error = Text.empty();

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
			rows.add(new ConditionRow(term.connector(), clause.operand().format(), clause.operator(), clause.threshold().toPlainString()));
		}
		if (rows.isEmpty()) {
			rows.add(defaultRow());
		}

		contentField.setText(conditionElement.content().serialize());
		contentField.setMaxLength(256);
	}

	int elementIndex() {
		return elementIndex;
	}

	void layout(ConditionDisplayItem conditionItem) {
		int width = computeWidth();
		int titleHeight = CLIENT.textRenderer.fontHeight;
		int errorHeight = error.getString().isBlank() ? 0 : CLIENT.textRenderer.getWrappedLinesHeight(error, width - ModuleContentField.POPUP_PADDING * 2) + ModuleContentField.POPUP_GAP;
		int height = ModuleContentField.POPUP_PADDING * 2
				+ titleHeight + ModuleContentField.POPUP_GAP
				+ rows.size() * (FIELD_HEIGHT + ROW_GAP)
				+ FIELD_HEIGHT + ModuleContentField.POPUP_GAP
				+ CLIENT.textRenderer.fontHeight + LABEL_FIELD_GAP + FIELD_HEIGHT + ModuleContentField.POPUP_GAP
				+ errorHeight
				+ ModuleContentField.BUTTON_HEIGHT;

		int preferredX = host.getX() + ModuleContentField.TEXT_PADDING_X + conditionItem.x() - host.horizontalScroll;
		int preferredY = host.getBottom() + ModuleContentField.OVERLAY_GAP;
		net.minecraft.client.gui.screen.Screen screen = CLIENT.currentScreen;
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

		addBounds = new Bounds(innerX, cursorY, textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.add_condition_clause")), ModuleContentField.BUTTON_HEIGHT);
		cursorY += FIELD_HEIGHT + ModuleContentField.POPUP_GAP;

		contentField.setPosition(innerX, cursorY + CLIENT.textRenderer.fontHeight + LABEL_FIELD_GAP);
		contentField.setWidth(innerWidth);

		int buttonsY = bounds.bottom() - ModuleContentField.POPUP_PADDING - ModuleContentField.BUTTON_HEIGHT;
		int deleteWidth = textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.delete_condition"));
		int saveWidth = textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.apply"));
		int cancelWidth = textButtonWidth(Text.translatable("flex_hud.global.config.cancel"));
		int totalButtonsWidth = deleteWidth + ModuleContentField.POPUP_GAP + saveWidth + ModuleContentField.POPUP_GAP + cancelWidth;
		int buttonX = bounds.right() - ModuleContentField.POPUP_PADDING - totalButtonsWidth;

		deleteBounds = new Bounds(buttonX, buttonsY, deleteWidth, ModuleContentField.BUTTON_HEIGHT);
		buttonX += deleteWidth + ModuleContentField.POPUP_GAP;
		saveBounds = new Bounds(buttonX, buttonsY, saveWidth, ModuleContentField.BUTTON_HEIGHT);
		buttonX += saveWidth + ModuleContentField.POPUP_GAP;
		cancelBounds = new Bounds(buttonX, buttonsY, cancelWidth, ModuleContentField.BUTTON_HEIGHT);
	}

	void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		host.renderPanel(context, bounds);
		context.drawText(CLIENT.textRenderer, Text.translatable("flex_hud.create_module_screen.editor.condition"), bounds.x() + ModuleContentField.POPUP_PADDING, bounds.y() + ModuleContentField.POPUP_PADDING, ModuleContentField.TEXT_COLOR, false);

		for (ConditionRow row : rows) {
			row.render(context, mouseX, mouseY, deltaTicks);
		}

		host.renderButton(context, addBounds, Text.translatable("flex_hud.create_module_screen.editor.add_condition_clause"), addBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, ModuleContentField.BUTTON_HORIZONTAL_PADDING, mouseX, mouseY);

		int contentLabelY = contentField.getY() - CLIENT.textRenderer.fontHeight - LABEL_FIELD_GAP;
		context.drawText(CLIENT.textRenderer, Text.translatable("flex_hud.create_module_screen.editor.condition_content"), contentField.getX(), contentLabelY, ModuleContentField.TEXT_COLOR, false);
		contentField.render(context, mouseX, mouseY, deltaTicks);

		if (!error.getString().isBlank()) {
			int errorWidth = bounds.width() - ModuleContentField.POPUP_PADDING * 2;
			int errorHeight = CLIENT.textRenderer.getWrappedLinesHeight(error, errorWidth);
			context.drawWrappedText(CLIENT.textRenderer, error, bounds.x() + ModuleContentField.POPUP_PADDING, saveBounds.y() - ModuleContentField.POPUP_GAP - errorHeight, errorWidth, ModuleContentField.POPUP_ERROR_COLOR, false);
		}

		host.renderButtonCenterLabel(context, deleteBounds, Text.translatable("flex_hud.create_module_screen.editor.delete_condition"), deleteBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
		host.renderButtonCenterLabel(context, saveBounds, Text.translatable("flex_hud.create_module_screen.editor.apply"), saveBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
		host.renderButtonCenterLabel(context, cancelBounds, Text.translatable("flex_hud.global.config.cancel"), cancelBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
	}

	boolean mouseClicked(Click click, boolean doubled) {
		activeDragWidget = null;
		setAllFieldsFocused(false);
		for (ConditionRow row : rows) {
			if (row.mouseClicked(click, doubled)) {
				return true;
			}
		}
		if (contentField.isMouseOver(click.x(), click.y()) && contentField.mouseClicked(click, doubled)) {
			activeDragWidget = contentField;
			return true;
		}
		if (addBounds.contains(click.x(), click.y())) {
			rows.add(defaultRow());
			layoutFromCurrentAnchor();
			return true;
		}
		if (deleteBounds.contains(click.x(), click.y())) {
			host.applyConditionChange(elementIndex, null, null, true);
			return true;
		}
		if (saveBounds.contains(click.x(), click.y())) {
			save();
			return true;
		}
		if (cancelBounds.contains(click.x(), click.y())) {
			host.conditionEditorPopup = null;
			return true;
		}
		return bounds.contains(click.x(), click.y());
	}

	boolean mouseDragged(Click click, double offsetX, double offsetY) {
		if (activeDragWidget != null) {
			return activeDragWidget.mouseDragged(click, offsetX, offsetY);
		}
		return false;
	}

	boolean mouseReleased(Click click) {
		if (activeDragWidget != null) {
			boolean handled = activeDragWidget.mouseReleased(click);
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

	boolean keyPressed(KeyInput input) {
		for (ConditionRow row : rows) {
			if (row.keyPressed(input)) {
				return true;
			}
		}
		if (contentField.isFocused() && contentField.keyPressed(input)) {
			return true;
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

	boolean charTyped(CharInput input) {
		for (ConditionRow row : rows) {
			if (row.charTyped(input)) {
				return true;
			}
		}
		return contentField.isFocused() && contentField.charTyped(input);
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
			error = Text.translatable("flex_hud.create_module_screen.editor.invalid_condition");
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
		contentWidth = Math.max(contentWidth, textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.add_condition_clause")));
		contentWidth = Math.max(contentWidth, buttonsRowWidth());
		contentWidth = Math.max(contentWidth, CLIENT.textRenderer.getWidth(Text.translatable("flex_hud.create_module_screen.editor.condition")));
		return Math.clamp(contentWidth + ModuleContentField.POPUP_PADDING * 2, MIN_WIDTH, MAX_WIDTH);
	}

	private int buttonsRowWidth() {
		return textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.delete_condition"))
				+ ModuleContentField.POPUP_GAP
				+ textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.apply"))
				+ ModuleContentField.POPUP_GAP
				+ textButtonWidth(Text.translatable("flex_hud.global.config.cancel"));
	}

	private int textButtonWidth(Text text) {
		return CLIENT.textRenderer.getWidth(text) + ModuleContentField.BUTTON_HORIZONTAL_PADDING * 2;
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
		List<ClickableWidget> fields = new ArrayList<>();
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
			this.variableField.setStyleToolbarEnabled(false);
			this.operator = operator;
			this.thresholdField = new PopupTextFieldWidget(THRESHOLD_WIDTH, FIELD_HEIGHT);
			this.thresholdField.setText(threshold);
			this.thresholdField.setMaxLength(32);
			this.thresholdField.setTextPredicate(ModuleContentField::isSignedDecimalInput);
		}

		private void layout(int x, int y, int width, boolean showConnector) {
			int variableX = x;
			int rowWidth = width;
			if (showConnector) {
				connectorBounds = new Bounds(x, y, CONNECTOR_WIDTH, FIELD_HEIGHT);
				variableX = connectorBounds.right() + ModuleContentField.POPUP_GAP;
				rowWidth -= CONNECTOR_WIDTH + ModuleContentField.POPUP_GAP;
			} else {
				connectorBounds = new Bounds(0, 0, 0, 0);
			}

			int variableWidth = rowWidth - OPERATOR_WIDTH - THRESHOLD_WIDTH - REMOVE_WIDTH - ModuleContentField.POPUP_GAP * 3;
			variableField.setPosition(variableX, y);
			variableField.setWidth(Math.max(80, variableWidth));
			operatorBounds = new Bounds(variableField.getRight() + ModuleContentField.POPUP_GAP, y, OPERATOR_WIDTH, FIELD_HEIGHT);
			thresholdField.setPosition(operatorBounds.right() + ModuleContentField.POPUP_GAP, y);
			removeBounds = new Bounds(x + width - REMOVE_WIDTH, y, REMOVE_WIDTH, FIELD_HEIGHT);
		}

		private void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
			if (connectorBounds.width() > 0) {
				host.renderButtonCenterLabel(context, connectorBounds, ModuleContentField.conditionConnectorLabel(connector), connectorBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
			}
			variableField.render(context, mouseX, mouseY, deltaTicks);
			host.renderButtonCenterLabel(context, operatorBounds, Text.literal(operator.primarySymbol()), operatorBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
			thresholdField.render(context, mouseX, mouseY, deltaTicks);
			host.renderButtonCenterLabel(context, removeBounds, Text.literal("x"), removeBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
		}

		private boolean mouseClicked(Click click, boolean doubled) {
			if (connectorBounds.width() > 0 && connectorBounds.contains(click.x(), click.y())) {
				connector = connector.next();
				return true;
			}
			if (operatorBounds.contains(click.x(), click.y())) {
				operator = nextOperator(operator);
				return true;
			}
			if (removeBounds.contains(click.x(), click.y())) {
				rows.remove(this);
				if (rows.isEmpty()) {
					rows.add(defaultRow());
				}
				layoutFromCurrentAnchor();
				return true;
			}
			if (variableField.isMouseOver(click.x(), click.y()) && variableField.mouseClicked(click, doubled)) {
				activeDragWidget = variableField;
				return true;
			}
			if (thresholdField.isMouseOver(click.x(), click.y()) && thresholdField.mouseClicked(click, doubled)) {
				activeDragWidget = thresholdField;
				return true;
			}
			return false;
		}

		private boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
			return variableField.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}

		private boolean keyPressed(KeyInput input) {
			return (variableField.isFocused() && variableField.keyPressed(input))
					|| (thresholdField.isFocused() && thresholdField.keyPressed(input));
		}

		private boolean charTyped(CharInput input) {
			return (variableField.isFocused() && variableField.charTyped(input))
					|| (thresholdField.isFocused() && thresholdField.charTyped(input));
		}

		private String rawClause() {
			return variableField.getText().strip() + operator.primarySymbol() + thresholdField.getText().strip();
		}

		private int desiredWidth(boolean showConnector) {
			int width = CLIENT.textRenderer.getWidth(variableField.getText())
					+ OPERATOR_WIDTH
					+ THRESHOLD_WIDTH
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
	}

	private static CustomCondition.Operator nextOperator(CustomCondition.Operator operator) {
		int index = OPERATOR_ORDER.indexOf(operator);
		return OPERATOR_ORDER.get((index + 1) % OPERATOR_ORDER.size());
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
