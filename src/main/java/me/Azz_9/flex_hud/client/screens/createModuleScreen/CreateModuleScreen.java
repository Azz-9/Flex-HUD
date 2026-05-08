package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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

	private TextFieldWidget searchBar;
	private VariablesList variablesList;
	private TextWidget moduleNameTextWidget;
	private ModuleNameField moduleNameField;
	private TextWidget feedbackTextWidget;
	private TextWidget moduleContentTextWidget;
	private ModuleContentField moduleContentField;
	private TextWidget previewTextWidget;

	public CreateModuleScreen(Screen parent, @Nullable CustomModule customModule) {
		super(Text.translatable("flex_hud.configuration_screen.create_module"), parent, Text.translatable("flex_hud.global.config.callback.message_title"), Text.translatable("flex_hud.global.config.callback.message_content"));
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

		Text moduleNameText = Text.translatable("flex_hud.create_module_screen.module_name");
		moduleNameTextWidget = new TextWidget(
				variablesList.getRight() + GAP, height / 5,
				CLIENT.textRenderer.getWidth(moduleNameText), TEXT_FIELDS_HEIGHT,
				moduleNameText, CLIENT.textRenderer
		);
		moduleNameField = createModuleNameField(customModule != null ? customModule.getName().getString() : "");
		feedbackTextWidget = new TextWidget(
				moduleNameTextWidget.getX(), moduleNameField.getBottom(),
				width - moduleNameTextWidget.getX() - GAP, TEXT_FIELDS_HEIGHT,
				Text.empty(), CLIENT.textRenderer
		);

		Text moduleContentText = Text.translatable("flex_hud.create_module_screen.module_content");
		moduleContentTextWidget = new TextWidget(
				moduleNameTextWidget.getX(), moduleNameField.getBottom() + GAP,
				CLIENT.textRenderer.getWidth(moduleContentText), TEXT_FIELDS_HEIGHT,
				moduleContentText, CLIENT.textRenderer
		);
		moduleContentField = createModuleContentField(customModule != null ? customModule.getText() : "");

		Text previewText = Text.translatable("flex_hud.create_module_screen.preview");
		previewTextWidget = new TextWidget(
				moduleNameTextWidget.getX(), moduleContentField.getBottom() + GAP,
				CLIENT.textRenderer.getWidth(previewText), TEXT_FIELDS_HEIGHT,
				previewText, CLIENT.textRenderer
		);

		this.addDrawableChild(moduleContentField);
		this.addDrawableChild(searchBar);
		this.addDrawableChild(variablesList);
		this.addDrawableChild(moduleNameField);
	}

	private VariablesList createVariableList() {
		VariablesList variablesList = new VariablesList(
				PADDING, TEXT_FIELDS_HEIGHT + PADDING * 2,
				this.height - TEXT_FIELDS_HEIGHT - PADDING * 2
		);
		variablesList.setOnVariableClick((variable) -> {
			moduleContentField.write("{" + variable.getKey() + "}");
		});

		return variablesList;
	}

	private TextFieldWidget createSearchBar() {
		PlaceholderTextFieldWidget searchBar = new PlaceholderTextFieldWidget(
				CLIENT.textRenderer,
				PADDING, PADDING,
				variablesList.getWidth(), TEXT_FIELDS_HEIGHT,
				Text.empty()
		);
		searchBar.setPlaceholder(Text.translatable("flex_hud.create_module_screen.searchbar_placeholder"));
		searchBar.setChangedListener((text) -> {
			variablesList.search(text);

			variablesList.setOnVariableClick((variable) -> {
				moduleContentField.write("{" + variable.getKey() + "}");
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
		moduleNameField.setChangedListener((text) -> {
			if (moduleNameField.isAlreadyRegistered()) {
				feedbackTextWidget.setMessage(formatFeedback(Text.translatable("flex_hud.create_module_screen.module_name.name_already_used")));
			} else {
				feedbackTextWidget.setMessage(Text.empty());
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

	public void setParentScrollAmount(double parentScrollAmount) {
		this.parentScrollAmount = parentScrollAmount;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (renderCallback(context, mouseX, mouseY, deltaTicks)) {
			return;
		}

		variablesList.render(context, mouseX, mouseY, deltaTicks);
		searchBar.render(context, mouseX, mouseY, deltaTicks);

		moduleNameTextWidget.render(context, mouseX, mouseY, deltaTicks);
		moduleNameField.render(context, mouseX, mouseY, deltaTicks);
		feedbackTextWidget.render(context, mouseX, mouseY, deltaTicks);

		moduleContentTextWidget.render(context, mouseX, mouseY, deltaTicks);

		previewTextWidget.render(context, mouseX, mouseY, deltaTicks);

		CustomModulePreview.renderPreview(
				previewTextWidget.getX(), previewTextWidget.getBottom(),
				context, deltaTicks
		);

		// render content field last so the popups are above everything
		moduleContentField.render(context, mouseX, mouseY, deltaTicks);
	}

	@Override
	protected void saveAndClose() {
		String name = moduleNameField.getText().strip();
		if (name.isBlank()) {
			feedbackTextWidget.setMessage(formatFeedback(Text.translatable("flex_hud.create_module_screen.module_name.blank_name")));
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
			feedbackTextWidget.setMessage(formatFeedback(Text.translatable("flex_hud.create_module_screen.module_name.name_already_used")));
			return;
		}

		if (PARENT instanceof ModulesListScreen modulesListScreen) {
			modulesListScreen.refreshModulesList();
		}
		close();
	}

	private static Text formatFeedback(MutableText text) {
		return text.formatted(Formatting.RED, Formatting.ITALIC);
	}

	@Override
	public void close() {
		super.close();
		if (PARENT instanceof ModulesListScreen modulesListScreen) {
			modulesListScreen.getModulesListWidget().setScrollY(parentScrollAmount);
		}
	}

	@Override
	protected void disableAllChildren() {
		super.disableAllChildren();
		variablesList.active = false;
		moduleContentField.active = false;
	}

	@Override
	protected void enableAllChildren() {
		super.enableAllChildren();
		variablesList.active = true;
		moduleContentField.active = true;
	}
}
