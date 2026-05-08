package me.Azz_9.flex_hud.client.screens.createModuleScreen.moduleContentField;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import me.Azz_9.flex_hud.client.customModules.modifiers.Modifier;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;
import me.Azz_9.flex_hud.client.screens.createModuleScreen.ModuleContentEditorModel;

final class ModifierEditorPopup {
	private static final int FIELD_HEIGHT = 18;
	private static final int LABEL_FIELD_GAP = 2;
	private static final int ROW_GAP = 4;
	private static final int CONDITIONAL_REMOVE_WIDTH = 16;
	private static final int CONDITIONAL_OPERATOR_WIDTH = 28;
	private static final int CONDITIONAL_THRESHOLD_WIDTH = 46;
	private static final int DESCRIPTION_MIN_WIDTH = 140;
	private static final int DESCRIPTION_MAX_WIDTH = 220;
	private static final int SHORT_NUMERIC_FIELD_WIDTH = 52;
	private static final int NUMERIC_FIELD_WIDTH = 72;
	private static final int TEXT_FIELD_WIDTH = 140;
	private static final int CONDITIONAL_RESULT_WIDTH = 120;

	private final ModuleContentField host;
	private final int elementIndex;
	private final @Nullable Integer modifierIndex;
	private final Modifier<?, ?> modifier;
	private final List<PopupTextFieldWidget> parameterFields = new ArrayList<>();
	private final List<ConditionalBranchRow> conditionalRows = new ArrayList<>();
	private @Nullable Text error;
	private int wrappedDescriptionWidth = DESCRIPTION_MIN_WIDTH;

	private Bounds bounds = new Bounds(0, 0, 0, 0);
	private Bounds saveBounds = new Bounds(0, 0, 0, 0);
	private Bounds cancelBounds = new Bounds(0, 0, 0, 0);
	private @Nullable Bounds deleteBounds;
	private @Nullable Bounds addConditionalBounds;

	ModifierEditorPopup(ModuleContentField host, int elementIndex, @Nullable Integer modifierIndex) {
		this.host = host;
		this.elementIndex = elementIndex;
		this.modifierIndex = modifierIndex;
		ModuleContentEditorModel.VariableElement variableElement = (ModuleContentEditorModel.VariableElement) host.model.get(elementIndex);
		Modifiers.ResolvedModifier<?, ?> resolvedModifier = variableElement.modifiers().get(modifierIndex);
		this.modifier = resolvedModifier.modifier();
		buildFields(resolvedModifier.arguments());
	}

	ModifierEditorPopup(ModuleContentField host, int elementIndex, @Nullable Integer modifierIndex, Modifier<?, ?> modifier, List<String> initialArguments) {
		this.host = host;
		this.elementIndex = elementIndex;
		this.modifierIndex = modifierIndex;
		this.modifier = modifier;
		buildFields(initialArguments);
	}

	int elementIndex() {
		return elementIndex;
	}

	void setError(@Nullable Text error) {
		this.error = error;
	}

	private void buildFields(List<String> initialArguments) {
		switch (modifier.uiMetadata().editorKind()) {
			case NONE -> {
			}
			case FIXED_FIELDS -> {
				for (int i = 0; i < modifier.uiMetadata().parameters().size(); i++) {
					Modifier.ParameterDefinition parameter = modifier.uiMetadata().parameters().get(i);
					String value = i < initialArguments.size() ? initialArguments.get(i) : host.defaultValue(modifier, parameter);
					PopupTextFieldWidget field = new PopupTextFieldWidget(desiredFieldWidth(parameter), FIELD_HEIGHT);
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

	void layout(VariableDisplayItem variableItem) {
		int width = computePopupWidth();
		int titleHeight = CLIENT.textRenderer.fontHeight;
		int height = ModuleContentField.POPUP_PADDING * 2 + titleHeight + ModuleContentField.POPUP_GAP;
		switch (modifier.uiMetadata().editorKind()) {
			case NONE -> height += wrappedDescriptionHeight();
			case FIXED_FIELDS -> {
				for (int i = 0; i < parameterFields.size(); i++) {
					height += CLIENT.textRenderer.fontHeight + LABEL_FIELD_GAP + FIELD_HEIGHT + (i == parameterFields.size() - 1 ? 0 : ROW_GAP);
				}
			}
			case CONDITIONAL_BRANCHES ->
					height += conditionalRows.size() * (FIELD_HEIGHT + ROW_GAP) + FIELD_HEIGHT + ModuleContentField.POPUP_GAP;
		}
		if (error != null) {
			height += CLIENT.textRenderer.fontHeight + ModuleContentField.POPUP_GAP;
		}
		height += ModuleContentField.BUTTON_HEIGHT + ModuleContentField.POPUP_GAP;

		int preferredX = host.getX() + ModuleContentField.TEXT_PADDING_X + variableItem.x() - host.horizontalScroll;
		int preferredY = host.getBottom() + ModuleContentField.OVERLAY_GAP;
		net.minecraft.client.gui.screen.Screen screen = CLIENT.currentScreen;
		if (screen != null && preferredY + height > screen.height - 4) {
			preferredY = host.getY() - height - ModuleContentField.OVERLAY_GAP;
		}

		bounds = new Bounds(host.clampX(preferredX, width), host.clampY(preferredY, height), width, height);

		int cursorY = bounds.y() + ModuleContentField.POPUP_PADDING + titleHeight + ModuleContentField.POPUP_GAP;
		switch (modifier.uiMetadata().editorKind()) {
			case NONE -> {
			}
			case FIXED_FIELDS -> {
				for (PopupTextFieldWidget field : parameterFields) {
					field.setPosition(bounds.x() + ModuleContentField.POPUP_PADDING, cursorY + CLIENT.textRenderer.fontHeight + LABEL_FIELD_GAP);
					field.setWidth(bounds.width() - ModuleContentField.POPUP_PADDING * 2);
					cursorY += CLIENT.textRenderer.fontHeight + LABEL_FIELD_GAP + FIELD_HEIGHT + ROW_GAP;
				}
			}
			case CONDITIONAL_BRANCHES -> {
				for (ConditionalBranchRow row : conditionalRows) {
					row.layout(bounds.x() + ModuleContentField.POPUP_PADDING, cursorY, bounds.width() - ModuleContentField.POPUP_PADDING * 2);
					cursorY += FIELD_HEIGHT + ROW_GAP;
				}
				addConditionalBounds = new Bounds(bounds.x() + ModuleContentField.POPUP_PADDING, cursorY, 22, ModuleContentField.BUTTON_HEIGHT);
				cursorY += FIELD_HEIGHT + ModuleContentField.POPUP_GAP;
			}
		}

		int buttonsY = bounds.bottom() - ModuleContentField.POPUP_PADDING - ModuleContentField.BUTTON_HEIGHT;
		int deleteWidth = modifierIndex != null ? textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.delete_modifier")) : 0;
		int saveWidth = textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.apply"));
		int cancelWidth = textButtonWidth(Text.translatable("flex_hud.global.config.cancel"));
		int totalButtonsWidth = saveWidth + ModuleContentField.POPUP_GAP + cancelWidth + (modifierIndex != null ? ModuleContentField.POPUP_GAP + deleteWidth : 0);
		int buttonX = bounds.right() - ModuleContentField.POPUP_PADDING - totalButtonsWidth;

		if (modifierIndex != null) {
			deleteBounds = new Bounds(buttonX, buttonsY, deleteWidth, ModuleContentField.BUTTON_HEIGHT);
			buttonX += deleteWidth + ModuleContentField.POPUP_GAP;
		} else {
			deleteBounds = null;
		}
		saveBounds = new Bounds(buttonX, buttonsY, saveWidth, ModuleContentField.BUTTON_HEIGHT);
		buttonX += saveWidth + ModuleContentField.POPUP_GAP;
		cancelBounds = new Bounds(buttonX, buttonsY, cancelWidth, ModuleContentField.BUTTON_HEIGHT);
	}

	void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		host.renderPanel(context, bounds);
		context.drawText(CLIENT.textRenderer, modifier.uiMetadata().getName(modifier.key()), bounds.x() + ModuleContentField.POPUP_PADDING, bounds.y() + ModuleContentField.POPUP_PADDING, ModuleContentField.TEXT_COLOR, false);
		int cursorY = bounds.y() + ModuleContentField.POPUP_PADDING + CLIENT.textRenderer.fontHeight + ModuleContentField.POPUP_GAP;

		switch (modifier.uiMetadata().editorKind()) {
			case NONE ->
					context.drawWrappedText(CLIENT.textRenderer, modifier.uiMetadata().getDescription(modifier.key()), bounds.x() + ModuleContentField.POPUP_PADDING, cursorY, wrappedDescriptionWidth, ModuleContentField.PLACEHOLDER_COLOR, false);
			case FIXED_FIELDS -> {
				for (int i = 0; i < parameterFields.size(); i++) {
					Modifier.ParameterDefinition parameter = modifier.uiMetadata().parameters().get(i);
					context.drawText(CLIENT.textRenderer, parameter.getName(modifier.key()), bounds.x() + ModuleContentField.POPUP_PADDING, cursorY, ModuleContentField.TEXT_COLOR, false);
					parameterFields.get(i).render(context, mouseX, mouseY, deltaTicks);
					cursorY += CLIENT.textRenderer.fontHeight + LABEL_FIELD_GAP + FIELD_HEIGHT + ROW_GAP;
				}
			}
			case CONDITIONAL_BRANCHES -> {
				for (ConditionalBranchRow row : conditionalRows) {
					row.render(context, mouseX, mouseY, deltaTicks);
				}
				if (addConditionalBounds != null) {
					host.renderButtonCenterLabel(context, addConditionalBounds, Text.literal("+"), addConditionalBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
				}
			}
		}

		if (error != null) {
			context.drawText(CLIENT.textRenderer, error, bounds.x() + ModuleContentField.POPUP_PADDING, saveBounds.y() - ModuleContentField.POPUP_GAP - CLIENT.textRenderer.fontHeight, ModuleContentField.POPUP_ERROR_COLOR, false);
		}

		if (deleteBounds != null) {
			host.renderButtonCenterLabel(context, deleteBounds, Text.translatable("flex_hud.create_module_screen.editor.delete_modifier"), deleteBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
		}
		host.renderButtonCenterLabel(context, saveBounds, Text.translatable("flex_hud.create_module_screen.editor.apply"), saveBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
		host.renderButtonCenterLabel(context, cancelBounds, Text.translatable("flex_hud.global.config.cancel"), cancelBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
	}

	boolean mouseClicked(Click click, boolean doubled) {
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
			host.applyModifierChange(elementIndex, modifierIndex, null, true);
			return true;
		}
		if (saveBounds.contains(click.x(), click.y())) {
			save();
			return true;
		}
		if (cancelBounds.contains(click.x(), click.y())) {
			host.modifierEditorPopup = null;
			return true;
		}
		return bounds.contains(click.x(), click.y());
	}

	boolean mouseDragged(Click click, double offsetX, double offsetY) {
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

	boolean mouseReleased(Click click) {
		boolean handled = false;
		for (PopupTextFieldWidget field : parameterFields) {
			handled |= field.mouseReleased(click);
		}
		for (ConditionalBranchRow row : conditionalRows) {
			handled |= row.mouseReleased(click);
		}
		return handled;
	}

	boolean keyPressed(KeyInput input) {
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

	boolean charTyped(CharInput input) {
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
		host.applyModifierChange(elementIndex, modifierIndex, resolvedModifier, false);
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

	private int computePopupWidth() {
		int buttonsWidth = buttonsRowWidth();
		int titleWidth = CLIENT.textRenderer.getWidth(modifier.uiMetadata().getName(modifier.key()));
		int contentWidth = switch (modifier.uiMetadata().editorKind()) {
			case NONE -> computeDescriptionWidth(buttonsWidth, titleWidth);
			case FIXED_FIELDS -> computeFieldsWidth();
			case CONDITIONAL_BRANCHES -> conditionalContentWidth();
		};
		int innerWidth = Math.max(Math.max(titleWidth, buttonsWidth), contentWidth);
		return innerWidth + ModuleContentField.POPUP_PADDING * 2;
	}

	private int computeFieldsWidth() {
		int width = 0;
		for (int i = 0; i < modifier.uiMetadata().parameters().size(); i++) {
			Modifier.ParameterDefinition parameter = modifier.uiMetadata().parameters().get(i);
			width = Math.max(width, CLIENT.textRenderer.getWidth(parameter.getName(modifier.key())));
			width = Math.max(width, desiredFieldWidth(parameter));
		}
		return width;
	}

	private int buttonsRowWidth() {
		int deleteWidth = modifierIndex != null ? textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.delete_modifier")) : 0;
		int saveWidth = textButtonWidth(Text.translatable("flex_hud.create_module_screen.editor.apply"));
		int cancelWidth = textButtonWidth(Text.translatable("flex_hud.global.config.cancel"));
		return saveWidth + ModuleContentField.POPUP_GAP + cancelWidth + (modifierIndex != null ? ModuleContentField.POPUP_GAP + deleteWidth : 0);
	}

	private int computeDescriptionWidth(int buttonsWidth, int titleWidth) {
		int preferredWidth = Math.max(CLIENT.textRenderer.getWidth(modifier.uiMetadata().getDescription(modifier.key())), DESCRIPTION_MIN_WIDTH);
		wrappedDescriptionWidth = Math.clamp(preferredWidth, DESCRIPTION_MIN_WIDTH, DESCRIPTION_MAX_WIDTH);
		wrappedDescriptionWidth = Math.max(wrappedDescriptionWidth, Math.max(buttonsWidth, titleWidth));
		return wrappedDescriptionWidth;
	}

	private int wrappedDescriptionHeight() {
		return CLIENT.textRenderer.getWrappedLinesHeight(modifier.uiMetadata().getDescription(modifier.key()), wrappedDescriptionWidth);
	}

	private int conditionalContentWidth() {
		return CONDITIONAL_OPERATOR_WIDTH
				+ CONDITIONAL_THRESHOLD_WIDTH
				+ CONDITIONAL_RESULT_WIDTH
				+ CONDITIONAL_REMOVE_WIDTH
				+ ModuleContentField.POPUP_GAP * 3;
	}

	private int desiredFieldWidth(Modifier.ParameterDefinition parameter) {
		return switch (parameter.kind()) {
			case CHARACTER -> FIELD_HEIGHT;
			case INTEGER -> switch (modifier.key()) {
				case "round", "floor", "ceil", "percent", "pad_left", "pad_right", "pad_center", "truncate" ->
						SHORT_NUMERIC_FIELD_WIDTH;
				default -> NUMERIC_FIELD_WIDTH;
			};
			case DECIMAL -> NUMERIC_FIELD_WIDTH;
			case TEXT, CONDITIONAL_BRANCHES -> TEXT_FIELD_WIDTH;
		};
	}

	private int textButtonWidth(Text text) {
		return CLIENT.textRenderer.getWidth(text) + ModuleContentField.BUTTON_HORIZONTAL_PADDING * 2;
	}

	private void layoutFromCurrentAnchor() {
		VariableDisplayItem variableDisplayItem = host.findVariableDisplayItem(elementIndex);
		if (variableDisplayItem != null) {
			layout(variableDisplayItem);
		}
	}

	boolean contains(double mouseX, double mouseY) {
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
			thresholdField.setPosition(x + CONDITIONAL_OPERATOR_WIDTH + ModuleContentField.POPUP_GAP, y);
			resultField.setPosition(x + CONDITIONAL_OPERATOR_WIDTH + ModuleContentField.POPUP_GAP + CONDITIONAL_THRESHOLD_WIDTH + ModuleContentField.POPUP_GAP, y);
			resultField.setWidth(width - CONDITIONAL_OPERATOR_WIDTH - CONDITIONAL_THRESHOLD_WIDTH - CONDITIONAL_REMOVE_WIDTH - ModuleContentField.POPUP_GAP * 3);
			removeBounds = new Bounds(x + width - CONDITIONAL_REMOVE_WIDTH, y, CONDITIONAL_REMOVE_WIDTH, FIELD_HEIGHT);
		}

		private void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
			host.renderButtonCenterLabel(context, operatorBounds, Text.literal(displayOperator()), operatorBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
			thresholdField.render(context, mouseX, mouseY, deltaTicks);
			resultField.render(context, mouseX, mouseY, deltaTicks);
			host.renderButtonCenterLabel(context, removeBounds, Text.literal("x"), removeBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND, ModuleContentField.BUTTON_TEXT_COLOR, mouseX, mouseY);
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
