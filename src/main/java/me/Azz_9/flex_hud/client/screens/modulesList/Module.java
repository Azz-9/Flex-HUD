package me.Azz_9.flex_hud.client.screens.modulesList;

import com.google.common.collect.ImmutableList;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class Module {
	public String name;
	public String id;
	public Identifier icon;
	public AbstractConfigurationScreen configScreen;
	public Button button;
	public ImmutableList<String> keywords;

	public Module(String name, String id, AbstractConfigurationScreen configScreen, int buttonWidth, int buttonHeight, ModulesListScreen parent, Supplier<Tooltip> getTooltip, ImmutableList<String> keywords) {
		if (name == null) {
			this.setAllNull();
		} else {
			this.name = name;
			this.id = id;
			this.icon = Identifier.fromNamespaceAndPath(MOD_ID, "modules_icons/" + id + ".png");
			this.configScreen = configScreen;
			this.button = Button.builder(Component.literal(name), (btn) -> {
						configScreen.setParentScrollAmount(parent.getModulesListWidget().scrollAmount());
						Minecraft.getInstance().setScreen(configScreen);
					})
					.size(buttonWidth, buttonHeight)
					.build();

			if (getTooltip != null) {
				this.button.setTooltip(getTooltip.get());
			}
			this.keywords = keywords;
		}
	}

	private void setAllNull() {
		this.name = null;
		this.id = null;
		this.icon = null;
		this.configScreen = null;
		this.button = null;
		this.keywords = null;
	}

	public boolean exists() {
		return this.name != null;
	}
}
