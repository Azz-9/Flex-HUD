package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons;

import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import me.Azz_9.flex_hud.client.screens.widgets.buttons.TexturedButtonWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.Identifier;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class ConfigResetButtonWidget extends TexturedButtonWidget implements Observer {
	public ConfigResetButtonWidget(int x, int y, int width, int height, Button.OnPress onPress, int textureWidth, int textureHeight) {
		super(x, y, width, height, new WidgetSprites(
				Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/reset/unfocused.png"),
				Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/reset/focused.png")
		), onPress, textureWidth, textureHeight);
	}

	public ConfigResetButtonWidget(int x, int y, int width, int height, Button.OnPress onPress) {
		this(x, y, width, height, onPress, width, height);
	}

	public ConfigResetButtonWidget(int width, int height, Button.OnPress onPress, int textureWidth, int textureHeight) {
		this(0, 0, width, height, onPress, textureWidth, textureHeight);
	}

	public ConfigResetButtonWidget(int width, int height, Button.OnPress onPress) {
		this(0, 0, width, height, onPress, width, height);
	}

	@Override
	public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.renderContents(graphics, mouseX, mouseY, delta);

		if (!this.active) {
			graphics.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		} else {
			if (this.isHoveredOrFocused()) {
				graphics.renderOutline(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
		}
	}

	@Override
	public void onChange(DataGetter<?> dataGetter) {
		if (dataGetter instanceof ResetAware resetAware) {
			this.active = !resetAware.isCurrentValueDefault();
		}
	}
}
