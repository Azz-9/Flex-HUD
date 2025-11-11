package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.Arrays;
import java.util.List;

public class MultiRenderable {
	private final List<Renderable> renderables;
	private int left;
	private int right;

	public MultiRenderable(int left, int right, Renderable... renderable) {
		this.renderables = Arrays.stream(renderable).toList();
		this.left = left;
		this.right = right;
	}

	public MultiRenderable(int left, int right, List<Renderable> renderables) {
		this.renderables = renderables;
		this.left = left;
		this.right = right;
	}

	public void render(DrawContext context, RenderTickCounter tickCounter) {
		for (Renderable renderable : renderables) {
			renderable.render(context, tickCounter);
		}
	}

	public void addX(int offsetX) {
		for (Renderable renderable : renderables) {
			renderable.x += offsetX;
		}
	}

	public static void alignRight(List<MultiRenderable> renderables, int right) {
		for (MultiRenderable multiRenderable : renderables) {
			multiRenderable.addX(right - multiRenderable.right);
		}
	}

	public static void alignCenter(List<MultiRenderable> renderables, int centerX) {
		for (MultiRenderable multiRenderable : renderables) {
			multiRenderable.addX(centerX - (multiRenderable.right - multiRenderable.left) / 2);
		}
	}
}
