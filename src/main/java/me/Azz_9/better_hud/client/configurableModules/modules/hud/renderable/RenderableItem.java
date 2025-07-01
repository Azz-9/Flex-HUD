package me.Azz_9.better_hud.client.configurableModules.modules.hud.renderable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;

public class RenderableItem extends Renderable {
	private ItemStack stack;

	public RenderableItem(int x, int y, ItemStack stack) {
		super(x, y);
		this.stack = stack;
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		context.drawItem(stack, x, y);
	}
}
