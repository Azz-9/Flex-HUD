package me.Azz_9.flex_hud.client.gui.components.config.buttons;

import static me.Azz_9.flex_hud.Constants.MOD_ID;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.components.config.DataGetter;
import me.Azz_9.flex_hud.client.gui.components.config.Observer;
import me.Azz_9.flex_hud.client.gui.components.config.ResetAware;
import me.Azz_9.flex_hud.utils.DrawingUtils;

public class ConfigResetButtonWidget extends ImageButton implements Observer {

	private static final ResourceLocation UNFOCUSED_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "widget/reset/unfocused");
	private static final ResourceLocation FOCUSED_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "widget/reset/focused");

	public ConfigResetButtonWidget(int x, int y, int width, int height, Button.OnPress onPress) {
		super(x, y, width, height, new WidgetSprites(UNFOCUSED_SPRITE, FOCUSED_SPRITE), onPress);
	}

	public ConfigResetButtonWidget(int width, int height, Button.OnPress onPress) {
		super(0, 0, width, height, new WidgetSprites(UNFOCUSED_SPRITE, FOCUSED_SPRITE), onPress);
	}

	@Override
	public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.renderWidget(graphics, mouseX, mouseY, delta);

		if (!this.active) {
			graphics.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		} else {
			if (this.isHoveredOrFocused()) {
				DrawingUtils.drawBorder(graphics, getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, Colors.WHITE);
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
