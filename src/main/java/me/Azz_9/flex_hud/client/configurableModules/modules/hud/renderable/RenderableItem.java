package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.configurableModules.modules.hud.PlaceholderStacks;
import me.Azz_9.flex_hud.client.mixin.drawContext.GuiGraphicsExtractorAccessor;

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
		this.stack = PlaceholderStacks.of(item);
		this.drawItemBar = drawItemBar;
	}

	@Override
	public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		graphics.item(stack, x, y);

		if (!stack.isEmpty()) {
			graphics.pose().pushMatrix();

			if (drawItemBar) {
				((GuiGraphicsExtractorAccessor) graphics).flex_hud$renderItemBar(stack, x, y);
			}
			((GuiGraphicsExtractorAccessor) graphics).flex_hud$renderItemCooldown(stack, x, y);

			graphics.pose().popMatrix();
		}
	}
}
