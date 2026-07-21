package me.Azz_9.flex_hud.client.gui.components.config.modulesList;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.Constants.MOD_ID;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import me.Azz_9.flex_hud.client.gui.screens.CreateModuleScreen;
import me.Azz_9.flex_hud.client.gui.screens.ModulesListScreen;
import me.Azz_9.flex_hud.client.modules.customModules.CustomModule;

public class CustomModuleElement extends ModuleElement {

	private static final Identifier EDIT_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "icon/edit");
	private static final Identifier DELETE_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "icon/delete");
	private static final int GAP = 5;
	private static final int BUTTONS_SIZE = 20;

	private final CustomModule module;

	private final Button editButton;
	private final Button deleteButton;

	private final Runnable onDelete;

	public CustomModuleElement(
			CustomModule module,
			int buttonWidth,
			int buttonHeight,
			ModulesListScreen parent,
			Supplier<Tooltip> getTooltip,
			ImmutableList<String> keywords,
			Runnable onDelete) {

		super(module, buttonWidth, buttonHeight, parent, getTooltip, keywords, Identifier.fromNamespaceAndPath(MOD_ID, "icon/modules/custom_module"));
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
		return SpriteIconButton.TextAndIcon.builder(
						Component.translatable("flex_hud.configuration_screen.edit_module"),
						(btn) -> {
							CreateModuleScreen createModuleScreen = new CreateModuleScreen(parent, module);
							createModuleScreen.setParentScrollAmount(parent.getModulesListWidget().scrollAmount());
							MINECRAFT.setScreen(createModuleScreen);
						},
						true
				)
				.withTootip()
				.size(BUTTONS_SIZE, BUTTONS_SIZE)
				.sprite(EDIT_SPRITE, 14, 14)
				.build();
	}

	private Button createDeleteButton() {
		return SpriteIconButton.TextAndIcon.builder(
						Component.translatable("flex_hud.configuration_screen.delete_module"),
						(btn) -> onDelete.run(),
						true
				)
				.withTootip()
				.size(BUTTONS_SIZE, BUTTONS_SIZE)
				.sprite(DELETE_SPRITE, 14, 14)
				.build();
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
	public void renderButton(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		super.renderButton(graphics, mouseX, mouseY, deltaTicks);
		editButton.render(graphics, mouseX, mouseY, deltaTicks);
		deleteButton.render(graphics, mouseX, mouseY, deltaTicks);
	}

	@Override
	public List<Button> buttons() {
		List<Button> list = new ArrayList<>(super.buttons());
		list.add(editButton);
		list.add(deleteButton);
		return list;
	}
}
