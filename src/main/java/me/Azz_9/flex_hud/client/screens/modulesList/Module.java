package me.Azz_9.flex_hud.client.screens.modulesList;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;
import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

import me.Azz_9.flex_hud.client.configurableModules.Configurable;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;

public class Module {
	public String name;
	public String id;
	public Identifier icon;
	public AbstractConfigurationScreen configScreen;
	public ButtonWidget button;
	public ImmutableList<String> keywords;
	protected ModulesListScreen parent;
	protected int width;

	public Module(
			Configurable module,
			int buttonWidth,
			int buttonHeight,
			ModulesListScreen parent,
			Supplier<Tooltip> getTooltip,
			ImmutableList<String> keywords) {

		this.name = module.getName().getString();
		this.id = module.getID();
		this.icon = Identifier.of(MOD_ID, "modules_icons/" + id + ".png");
		this.configScreen = module.getConfigScreen(parent);
		this.button = createButton(buttonWidth, buttonHeight);

		if (getTooltip != null) {
			this.button.setTooltip(getTooltip.get());
		}
		this.keywords = keywords;
		this.parent = parent;
		this.width = buttonWidth;
	}

	public Module(
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

	protected ButtonWidget createButton(int buttonWidth, int buttonHeight) {
		return ButtonWidget.builder(Text.literal(name), (btn) -> {
					configScreen.setParentScrollAmount(parent.getModulesListWidget().getScrollY());
					CLIENT.setScreen(configScreen);
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

	public void renderButton(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		button.render(context, mouseX, mouseY, deltaTicks);
	}

	public List<ButtonWidget> buttons() {
		return List.of(button);
	}
}
