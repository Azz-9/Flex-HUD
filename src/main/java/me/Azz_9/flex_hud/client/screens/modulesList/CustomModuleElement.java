package me.Azz_9.flex_hud.client.screens.modulesList;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;
import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import me.Azz_9.flex_hud.client.customModules.CustomModule;
import me.Azz_9.flex_hud.client.screens.createModuleScreen.CreateModuleScreen;
import me.Azz_9.flex_hud.client.screens.widgets.buttons.IconButton;

public class CustomModuleElement extends Module {

	private static final int GAP = 5;
	private static final int BUTTONS_SIZE = 20;

	private final CustomModule module;

	private final Button editButton;
	private final Button deleteButton;

	private Runnable onDelete;

	public CustomModuleElement(
			CustomModule module,
			int buttonWidth,
			int buttonHeight,
			ModulesListScreen parent,
			Supplier<Tooltip> getTooltip,
			ImmutableList<String> keywords,
			Runnable onDelete) {

		super(module, buttonWidth, buttonHeight, parent, getTooltip, keywords, Identifier.fromNamespaceAndPath(MOD_ID, "modules_icons/custom_module.png"));
		this.module = module;
		this.onDelete = onDelete;
		editButton = createEditButton();
		deleteButton = createDeleteButton();
	}

	@Override
	protected Button createButton(int buttonWidth, int buttonHeight) {
		return super.createButton(buttonWidth - GAP * 2 - BUTTONS_SIZE * 2, buttonHeight);
	}

	private Button createEditButton() {
		IconButton editButton = new IconButton(
				0, 0,
				BUTTONS_SIZE, BUTTONS_SIZE,
				Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/edit.png"),
				14, 14,
				(btn) -> {
					CreateModuleScreen createModuleScreen = new CreateModuleScreen(parent, module);
					createModuleScreen.setParentScrollAmount(parent.getModulesListWidget().scrollAmount());
					MINECRAFT.setScreen(createModuleScreen);
				});
		editButton.setTooltip(Tooltip.create(Component.translatable("flex_hud.configuration_screen.edit_module")));
		return editButton;
	}

	private Button createDeleteButton() {
		IconButton deleteButton = new IconButton(
				0, 0,
				BUTTONS_SIZE, BUTTONS_SIZE,
				Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/delete.png"),
				14, 14,
				(btn) -> onDelete.run());
		deleteButton.setTooltip(Tooltip.create(Component.translatable("flex_hud.configuration_screen.delete_module")));
		return deleteButton;
	}

	@Override
	public void setButtonX(int x) {
		super.setButtonX(x);
		editButton.setX(x + width - BUTTONS_SIZE * 2 - GAP);
		deleteButton.setX(x + width - BUTTONS_SIZE);
	}

	@Override
	public void setButtonY(int y) {
		super.setButtonY(y);
		editButton.setY(y);
		deleteButton.setY(y);
	}

	@Override
	public void renderButton(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		super.renderButton(graphics, mouseX, mouseY, deltaTicks);
		editButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		deleteButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
	}

	@Override
	public List<Button> buttons() {
		List<Button> list = new ArrayList<>(super.buttons());
		list.add(editButton);
		list.add(deleteButton);
		return list;
	}
}
