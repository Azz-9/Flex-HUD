package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import me.Azz_9.flex_hud.client.mixin.drawContext.DrawContextAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderableItem extends Renderable {
	@NotNull
	private final ItemStack stack;
	private final boolean drawItemBar;

	public RenderableItem(int x, int y, int width, @NotNull ItemStack stack, boolean drawItemBar) {
		super(x, y, width);
		this.stack = stack;
		this.drawItemBar = drawItemBar;
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		context.drawItem(stack, x, y);

		if (!stack.isEmpty()) {
			context.getMatrices().push();

			if (drawItemBar) {
				((DrawContextAccessor) context).flex_hud$drawItemBar(stack, x, y);
			}
			((DrawContextAccessor) context).flex_hud$drawCooldownProgress(stack, x, y);

			context.getMatrices().pop();
		}
	}
}
