package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import me.Azz_9.flex_hud.client.customModules.CustomModule;
import me.Azz_9.flex_hud.client.customModules.CustomModulePreview;
import me.Azz_9.flex_hud.client.customModules.CustomModuleRegistry;
import me.Azz_9.flex_hud.client.screens.AbstractCallbackScreen;
import me.Azz_9.flex_hud.client.screens.createModuleScreen.moduleContentField.ModuleContentField;
import me.Azz_9.flex_hud.client.screens.modulesList.ModulesListScreen;
import me.Azz_9.flex_hud.client.screens.widgets.textFieldWidget.PlaceholderTextFieldWidget;

public class CreateModuleScreen extends AbstractCallbackScreen {

	private static final int PADDING = 1;
	private static final int GAP = 20;
	private static final int TEXT_FIELDS_HEIGHT = 21;

	private double parentScrollAmount = 0;

	private final @Nullable CustomModule customModule;

	private EditBox searchBar;
	private VariablesList variablesList;
	private StringWidget moduleNameTextWidget;
	private ModuleNameField moduleNameField;
	private StringWidget feedbackTextWidget;
	private StringWidget moduleContentTextWidget;
	private Button addConditionButton;
	private ModuleContentField moduleContentField;
	private StringWidget previewTextWidget;

	public CreateModuleScreen(Screen parent, @Nullable CustomModule customModule) {
		super(Component.translatable("flex_hud.configuration_screen.create_module"), parent, Component.translatable("flex_hud.global.config.callback.message_title"), Component.translatable("flex_hud.global.config.callback.message_content"));
		this.customModule = customModule;
	}

	public CreateModuleScreen(Screen parent) {
		this(parent, null);
	}

	@Override
	protected void init() {
		super.init();

		CustomModulePreview.unload();

		variablesList = createVariableList();
		searchBar = createSearchBar();

		Component moduleNameText = Component.translatable("flex_hud.create_module_screen.module_name");
		moduleNameTextWidget = new StringWidget(
				variablesList.getRight() + GAP, height / 5,
				MINECRAFT.font.width(moduleNameText), TEXT_FIELDS_HEIGHT,
				moduleNameText, MINECRAFT.font
		);
		moduleNameField = createModuleNameField(customModule != null ? customModule.getName().getString() : "");
		feedbackTextWidget = new StringWidget(
				moduleNameTextWidget.getX(), moduleNameField.getBottom(),
				width - moduleNameTextWidget.getX() - GAP, TEXT_FIELDS_HEIGHT,
				Component.empty(), MINECRAFT.font
		);

		Component moduleContentText = Component.translatable("flex_hud.create_module_screen.module_content");
		moduleContentTextWidget = new StringWidget(
				moduleNameTextWidget.getX(), moduleNameField.getBottom() + GAP,
				MINECRAFT.font.width(moduleContentText), TEXT_FIELDS_HEIGHT,
				moduleContentText, MINECRAFT.font
		);
		moduleContentField = createModuleContentField(customModule != null ? customModule.getText() : "");
		addConditionButton = createAddConditionButton();

		Component previewText = Component.translatable("flex_hud.create_module_screen.preview");
		previewTextWidget = new StringWidget(
				moduleNameTextWidget.getX(), moduleContentField.getBottom() + GAP,
				MINECRAFT.font.width(previewText), TEXT_FIELDS_HEIGHT,
				previewText, MINECRAFT.font
		);

		this.addRenderableWidget(moduleContentField);
		this.addRenderableWidget(searchBar);
		this.addRenderableWidget(variablesList);
		this.addRenderableWidget(moduleNameField);
		this.addRenderableWidget(addConditionButton);
	}

	private VariablesList createVariableList() {
		VariablesList variablesList = new VariablesList(
				PADDING, TEXT_FIELDS_HEIGHT + PADDING * 2,
				this.height - TEXT_FIELDS_HEIGHT - PADDING * 2
		);
		variablesList.setOnVariableClick((variable) -> {
			moduleContentField.insertVariable(variable);
		});

		return variablesList;
	}

	private EditBox createSearchBar() {
		PlaceholderTextFieldWidget searchBar = new PlaceholderTextFieldWidget(
				MINECRAFT.font,
				PADDING, PADDING,
				variablesList.getWidth(), TEXT_FIELDS_HEIGHT,
				Component.empty()
		);
		searchBar.setPlaceholder(Component.translatable("flex_hud.create_module_screen.searchbar_placeholder"));
		searchBar.setResponder((text) -> {
			variablesList.search(text);

			variablesList.setOnVariableClick((variable) -> {
				moduleContentField.insertVariable(variable);
			});
		});

		return searchBar;
	}

	private ModuleNameField createModuleNameField(String initialText) {
		ModuleNameField moduleNameField = new ModuleNameField(
				moduleNameTextWidget.getX(), moduleNameTextWidget.getBottom(),
				width - moduleNameTextWidget.getX() - GAP, TEXT_FIELDS_HEIGHT,
				initialText
		);
		moduleNameField.setResponder((text) -> {
			if (moduleNameField.isAlreadyRegistered()) {
				feedbackTextWidget.setMessage(formatFeedback(Component.translatable("flex_hud.create_module_screen.module_name.name_already_used")));
			} else {
				feedbackTextWidget.setMessage(Component.empty());
			}
			updateSaveButton();
		});

		registerTrackableWidget(moduleNameField);

		return moduleNameField;
	}

	private ModuleContentField createModuleContentField(String initialText) {
		ModuleContentField moduleContentField = new ModuleContentField(
				moduleContentTextWidget.getX(), moduleContentTextWidget.getBottom(),
				width - moduleContentTextWidget.getX() - GAP, TEXT_FIELDS_HEIGHT,
				initialText
		);
		moduleContentField.setMaxLength(200);
		moduleContentField.setChangedListener((text) -> {
			CustomModulePreview.load(text);
			updateSaveButton();
		});
		CustomModulePreview.load(initialText);

		registerTrackableWidget(moduleContentField);

		return moduleContentField;
	}

	private Button createAddConditionButton() {
		Component label = Component.translatable("flex_hud.create_module_screen.editor.add_condition");
		int buttonWidth = MINECRAFT.font.width(label) + 12;
		return Button.builder(label, button -> moduleContentField.insertCondition())
				.bounds(moduleContentTextWidget.getRight() + 8, moduleContentTextWidget.getY() + 1, buttonWidth, TEXT_FIELDS_HEIGHT - 2)
				.build();
	}

	public void setParentScrollAmount(double parentScrollAmount) {
		this.parentScrollAmount = parentScrollAmount;
	}

	@Override
	public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (renderCallback(graphics, mouseX, mouseY, deltaTicks)) {
			return;
		}

		variablesList.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		searchBar.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		moduleNameTextWidget.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		moduleNameField.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		feedbackTextWidget.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		moduleContentTextWidget.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		addConditionButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		previewTextWidget.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		CustomModulePreview.renderPreview(
				previewTextWidget.getX(), previewTextWidget.getBottom(),
				graphics, deltaTicks
		);

		// render content field last so the popups are above everything
		moduleContentField.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
	}

	@Override
	protected void saveAndClose() {
		String name = moduleNameField.getValue().strip();
		if (name.isBlank()) {
			feedbackTextWidget.setMessage(formatFeedback(Component.translatable("flex_hud.create_module_screen.module_name.blank_name")));
			return;
		}

		String content = moduleContentField.getText();

		try {
			if (customModule == null) {
				CustomModuleRegistry.register(CustomModule.fromText(name, content));
			} else {
				CustomModuleRegistry.update(customModule, name, content);
			}
		} catch (IllegalStateException e) {
			feedbackTextWidget.setMessage(formatFeedback(Component.translatable("flex_hud.create_module_screen.module_name.name_already_used")));
			return;
		}

		if (PARENT instanceof ModulesListScreen modulesListScreen) {
			modulesListScreen.refreshModulesList();
		}
		onClose();
	}

	private static Component formatFeedback(MutableComponent text) {
		return text.withStyle(ChatFormatting.RED, ChatFormatting.ITALIC);
	}

	@Override
	public void onClose() {
		super.onClose();
		if (PARENT instanceof ModulesListScreen modulesListScreen) {
			modulesListScreen.getModulesListWidget().setScrollAmount(parentScrollAmount);
		}
	}

	@Override
	protected void disableAllChildren() {
		super.disableAllChildren();
		variablesList.active = false;
		addConditionButton.active = false;
		moduleContentField.active = false;
	}

	@Override
	protected void enableAllChildren() {
		super.enableAllChildren();
		variablesList.active = true;
		addConditionButton.active = true;
		moduleContentField.active = true;
	}
}
