package me.Azz_9.flex_hud.client.gui.components.config.modulesList;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.Constants.MOD_ID;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.Supplier;

import me.Azz_9.flex_hud.client.config.Configurable;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.gui.screens.ModulesListScreen;

public class ModuleElement {
	public String name;
	public String id;
	public Identifier icon;
	public AbstractConfigurationScreen configScreen;
	public Button button;
	public ImmutableList<String> keywords;
	protected ModulesListScreen parent;
	protected int width;

	public ModuleElement(
			Configurable module,
			int buttonWidth,
			int buttonHeight,
			ModulesListScreen parent,
			Supplier<Tooltip> getTooltip,
			ImmutableList<String> keywords) {

		this.name = module.getName().getString();
		this.id = module.getID();
		this.icon = Identifier.fromNamespaceAndPath(MOD_ID, "icon/modules/" + id);
		this.configScreen = module.getConfigScreen(parent);
		this.button = createButton(buttonWidth, buttonHeight);

		if (getTooltip != null) {
			this.button.setTooltip(getTooltip.get());
		}
		this.keywords = keywords;
		this.parent = parent;
		this.width = buttonWidth;
	}

	public ModuleElement(
			Configurable module,
			int buttonWidth,
			int buttonHeight,
			ModulesListScreen parent,
			Supplier<Tooltip> getTooltip,
			ImmutableList<String> keywords,
			Identifier icon) {

		this.name = module.getName().getString();
		this.id = module.getID();
		this.icon = icon;
		this.configScreen = module.getConfigScreen(parent);
		this.button = createButton(buttonWidth, buttonHeight);

		if (getTooltip != null) {
			this.button.setTooltip(getTooltip.get());
		}
		this.keywords = keywords;
		this.parent = parent;
		this.width = buttonWidth;
	}

	protected Button createButton(int buttonWidth, int buttonHeight) {
		return Button.builder(Component.literal(name), (btn) -> {
					configScreen.setParentScrollAmount(parent.getModulesListWidget().scrollAmount());
					MINECRAFT.setScreen(configScreen);
				})
				.size(buttonWidth, buttonHeight)
				.build();
	}

	public void setButtonX(int x) {
		button.setX(x);
	}

	public void setButtonY(int y) {
		button.setY(y);
	}

	public void renderButton(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
		button.render(context, mouseX, mouseY, deltaTicks);
	}

	public List<Button> buttons() {
		return List.of(button);
	}
}
