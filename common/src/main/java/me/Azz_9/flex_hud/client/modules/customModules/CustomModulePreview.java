package me.Azz_9.flex_hud.client.modules.customModules;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;

import me.Azz_9.flex_hud.client.modules.customModules.template.CompiledCustomText;

public class CustomModulePreview {

	private static final int DEFAULT_PREVIEW_COLOR = 0xffffffff;
	private static CompiledCustomText compiledText = CompiledCustomText.compile("");

	public static void unload() {
		replaceCompiledText(CompiledCustomText.compile(""));
	}

	public static void load(String text) {
		replaceCompiledText(CompiledCustomText.compile(text));
	}

	public static void recompile() {
		load(compiledText.getSource());
	}

	private static void replaceCompiledText(CompiledCustomText newCompiledText) {
		CompiledCustomText oldCompiledText = compiledText;
		compiledText = newCompiledText;
		oldCompiledText.close();
	}

	public static void renderPreview(int x, int y, GuiGraphics graphics, float deltaTicks) {
		CompiledCustomText.RenderData renderData = compiledText.getRenderData();
		graphics.drawString(
				MINECRAFT.font,
				renderData.text(),
				x, y,
				DEFAULT_PREVIEW_COLOR,
				true
		);
	}
}
