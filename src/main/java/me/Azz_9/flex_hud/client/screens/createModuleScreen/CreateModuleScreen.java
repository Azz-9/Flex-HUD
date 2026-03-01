package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import org.jspecify.annotations.Nullable;

import me.Azz_9.flex_hud.client.customModules.CustomModule;
import me.Azz_9.flex_hud.client.customModules.CustomModuleRegistry;
import me.Azz_9.flex_hud.client.screens.AbstractCallbackScreen;
import me.Azz_9.flex_hud.client.screens.modulesList.ModulesListScreen;
import me.Azz_9.flex_hud.client.screens.widgets.textFieldWidget.PlaceholderTextFieldWidget;

public class CreateModuleScreen extends AbstractCallbackScreen {

	private static final int PADDING = 1;
	private static final int GAP = 10;
	private static final int TEXT_FIELDS_HEIGHT = 20;
	private static final int MODULE_NAME_WIDTH = 300;

	private double parentScrollAmount = 0;

	private final @Nullable CustomModule customModule;

	private TextFieldWidget searchBar;
	private VariablesList variablesList;
	private TextWidget textWidget;
	private ModuleNameField moduleNameField;
	private ModuleContentField moduleContentField;

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

		variablesList = createVariableList();
		searchBar = createSearchBar();
		Text text = Text.translatable("flex_hud.create_module_screen.module_name");
		textWidget = new TextWidget(
				variablesList.getRight() + GAP, PADDING,
				CLIENT.textRenderer.getWidth(text), TEXT_FIELDS_HEIGHT,
				text, CLIENT.textRenderer
		);
		moduleNameField = createModuleNameField(customModule != null ? customModule.getName().getString() : "");
		moduleContentField = createModuleContentField(customModule != null ? customModule.getText() : "");

		this.addDrawableChild(searchBar);
		this.addDrawableChild(variablesList);
		this.addDrawableChild(moduleNameField);
		this.addDrawableChild(moduleContentField);
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
		return new ModuleNameField(
				textWidget.getRight() + GAP, PADDING,
				MODULE_NAME_WIDTH, TEXT_FIELDS_HEIGHT,
				initialText
		);
	}

	private ModuleContentField createModuleContentField(String initialText) {
		ModuleContentField moduleContentField = new ModuleContentField(
				variablesList.getRight() + GAP, moduleNameField.getBottom() + GAP,
				width - variablesList.getRight() - GAP - PADDING, TEXT_FIELDS_HEIGHT,
				initialText
		);
		moduleContentField.setMaxLength(200);
		moduleContentField.setChangedListener((text) -> updateSaveButton());

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
		textWidget.render(context, mouseX, mouseY, deltaTicks);
		moduleNameField.render(context, mouseX, mouseY, deltaTicks);
		moduleContentField.render(context, mouseX, mouseY, deltaTicks);
	}

	@Override
	protected void saveAndClose() {
		try {
			CustomModuleRegistry.register(CustomModule.fromText("salut", moduleContentField.getText()));
		} catch (IllegalStateException e) {

			return;
		}
		close();
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
