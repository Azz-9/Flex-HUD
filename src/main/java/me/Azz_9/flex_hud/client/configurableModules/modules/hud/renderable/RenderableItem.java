package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import me.Azz_9.flex_hud.client.mixin.drawContext.GuiGraphicsAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderableItem extends Renderable {
	@NotNull
	private final net.minecraft.world.item.ItemStack stack;
	private final boolean drawItemBar;

	public RenderableItem(int x, int y, int width, @NotNull ItemStack stack, boolean drawItemBar) {
		super(x, y, width);
		this.stack = stack;
		this.drawItemBar = drawItemBar;
	}

	public RenderableItem(int x, int y, int width, @NotNull Item item, boolean drawItemBar) {
		super(x, y, width);
		this.stack = new ItemStack(item);
		this.drawItemBar = drawItemBar;
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		graphics.renderItem(stack, x, y);

		if (!stack.isEmpty()) {
			graphics.pose().pushMatrix();

			if (drawItemBar) {
				((GuiGraphicsAccessor) graphics).flex_hud$renderItemBar(stack, x, y);
			}
			((GuiGraphicsAccessor) graphics).flex_hud$renderItemCooldown(stack, x, y);

			graphics.pose().popMatrix();
		}
	}
}
