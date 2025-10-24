package me.Azz_9.flex_hud.client.screens.modulesList;

import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

import static me.Azz_9.flex_hud.client.Flex_hud.MOD_ID;

public class Module {
	public String name;
	public String id;
	public Identifier icon;
	public AbstractConfigurationScreen configScreen;
	public ButtonWidget button;

	public Module(String name, String id, AbstractConfigurationScreen configScreen, int buttonWidth, int buttonHeight, ModulesListScreen parent, Supplier<Tooltip> getTooltip) {
		if (name == null) {
			this.setAllNull();
		} else {
			this.name = name;
			this.id = id;
			this.icon = Identifier.of(MOD_ID, "modules_icons/" + id + ".png");
			this.configScreen = configScreen;
			this.button = ButtonWidget.builder(Text.literal(name), (btn) -> {
						configScreen.setParentScrollAmount(parent.getModulesListWidget().getScrollY());
						MinecraftClient.getInstance().setScreen(configScreen);
					})
					.size(buttonWidth, buttonHeight)
					.build();

			if (getTooltip != null) {
				this.button.setTooltip(getTooltip.get());
			}
		}
	}

	private void setAllNull() {
		this.name = null;
		this.id = null;
		this.icon = null;
		this.configScreen = null;
		this.button = null;
	}

	public boolean exists() {
		return this.name != null;
	}
}
