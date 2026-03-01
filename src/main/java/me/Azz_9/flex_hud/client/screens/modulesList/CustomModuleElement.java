package me.Azz_9.flex_hud.client.screens.modulesList;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;
import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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

	private final ButtonWidget editButton;
	private final ButtonWidget deleteButton;

	private Runnable onDelete;

	public CustomModuleElement(
			CustomModule module,
			int buttonWidth,
			int buttonHeight,
			ModulesListScreen parent,
			Supplier<Tooltip> getTooltip,
			ImmutableList<String> keywords,
			Runnable onDelete) {

		super(module, buttonWidth, buttonHeight, parent, getTooltip, keywords);
		this.module = module;
		this.onDelete = onDelete;
		editButton = createEditButton();
		deleteButton = createDeleteButton();
	}

	@Override
	protected ButtonWidget createButton(int buttonWidth, int buttonHeight) {
		return super.createButton(buttonWidth - GAP * 2 - BUTTONS_SIZE * 2, buttonHeight);
	}

	private ButtonWidget createEditButton() {
		IconButton editButton = new IconButton(
				0, 0,
				BUTTONS_SIZE, BUTTONS_SIZE,
				Identifier.of(MOD_ID, "widgets/buttons/edit.png"),
				14, 14,
				(btn) -> {
					CreateModuleScreen createModuleScreen = new CreateModuleScreen(parent, module);
					createModuleScreen.setParentScrollAmount(parent.getModulesListWidget().getScrollY());
					CLIENT.setScreen(createModuleScreen);
				});
		editButton.setTooltip(Tooltip.of(Text.translatable("flex_hud.configuration_screen.edit_module")));
		return editButton;
	}

	private ButtonWidget createDeleteButton() {
		IconButton deleteButton = new IconButton(
				0, 0,
				BUTTONS_SIZE, BUTTONS_SIZE,
				Identifier.of(MOD_ID, "widgets/buttons/delete.png"),
				14, 14,
				(btn) -> onDelete.run());
		deleteButton.setTooltip(Tooltip.of(Text.translatable("flex_hud.configuration_screen.delete_module")));
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
	public void renderButton(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.renderButton(context, mouseX, mouseY, deltaTicks);
		editButton.render(context, mouseX, mouseY, deltaTicks);
		deleteButton.render(context, mouseX, mouseY, deltaTicks);
	}

	@Override
	public List<ButtonWidget> buttons() {
		List<ButtonWidget> list = new ArrayList<>(super.buttons());
		list.add(editButton);
		list.add(deleteButton);
		return list;
	}
}
