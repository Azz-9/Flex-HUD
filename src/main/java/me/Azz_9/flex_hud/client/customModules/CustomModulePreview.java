package me.Azz_9.flex_hud.client.customModules;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;

import me.Azz_9.flex_hud.client.customModules.template.CompiledCustomText;

public class CustomModulePreview {

	private static final int DEFAULT_PREVIEW_COLOR = 0xffffffff;
	private static CompiledCustomText compiledText = CompiledCustomText.compile("");

	public static void unload() {
		compiledText = CompiledCustomText.compile("");
	}

	public static void load(String text) {
		compiledText = CompiledCustomText.compile(text);
	}

	public static void renderPreview(int x, int y, DrawContext context, float deltaTicks) {
		CompiledCustomText.RenderData renderData = compiledText.getRenderData();
		context.drawText(
				CLIENT.textRenderer,
				renderData.text(),
				x, y,
				DEFAULT_PREVIEW_COLOR,
				true
		);
	}
}
