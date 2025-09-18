package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons;

import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import me.Azz_9.flex_hud.client.screens.widgets.buttons.TexturedButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.util.Identifier;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class ConfigResetButtonWidget extends TexturedButtonWidget implements Observer {
	public ConfigResetButtonWidget(int x, int y, int width, int height, PressAction pressAction, int textureWidth, int textureHeight) {
		super(x, y, width, height, new ButtonTextures(
				Identifier.of(MOD_ID, "widgets/buttons/reset/unfocused.png"),
				Identifier.of(MOD_ID, "widgets/buttons/reset/focused.png")
		), pressAction, textureWidth, textureHeight);
	}

	public ConfigResetButtonWidget(int x, int y, int width, int height, PressAction pressAction) {
		this(x, y, width, height, pressAction, width, height);
	}

	public ConfigResetButtonWidget(int width, int height, PressAction pressAction, int textureWidth, int textureHeight) {
		this(0, 0, width, height, pressAction, textureWidth, textureHeight);
	}

	public ConfigResetButtonWidget(int width, int height, PressAction pressAction) {
		this(0, 0, width, height, pressAction, width, height);
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		super.renderWidget(context, mouseX, mouseY, delta);

		if (!this.active) {
			context.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		} else if (this.isSelected()) {
			context.drawBorder(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
		}
	}

	@Override
	public void onChange(DataGetter<?> dataGetter) {
		if (dataGetter instanceof ResetAware resetAware) {
			this.active = !resetAware.isCurrentValueDefault();
		}
	}
}
