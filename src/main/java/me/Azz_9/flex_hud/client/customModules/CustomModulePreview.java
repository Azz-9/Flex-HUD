package me.Azz_9.flex_hud.client.customModules;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class CustomModulePreview {

	private static List<Token> tokens = new ArrayList<>();

	public static void unload() {
		tokens.clear();
	}

	public static void load(String text) {
		tokens = TokenParser.parseText(text);
	}

	public static void renderPreview(int x, int y, DrawContext context, float deltaTicks) {

		int hudX = 0;
		for (Token token : tokens) {
			context.drawText(
					CLIENT.textRenderer,
					token.getString(),
					x + hudX, y,
					0xffffffff,
					true
			);

			hudX += CLIENT.textRenderer.getWidth(token.getString());
		}
	}
}
