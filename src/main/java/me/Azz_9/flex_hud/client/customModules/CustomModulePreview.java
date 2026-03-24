package me.Azz_9.flex_hud.client.customModules;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.customModules.token.Token;
import me.Azz_9.flex_hud.client.customModules.token.TokenParser;

public class CustomModulePreview {

	private static final int DEFAULT_PREVIEW_COLOR = 0xffffffff;
	private static final int ERROR_PREVIEW_COLOR = 0xffff5555;
	private static final String INVALID_MODIFIER_PLACEHOLDER = "<invalid modifiers>";

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
			String tokenString;
			int color = DEFAULT_PREVIEW_COLOR;
			try {
				tokenString = token.getString();
			} catch (RuntimeException ignored) {
				tokenString = INVALID_MODIFIER_PLACEHOLDER;
				color = ERROR_PREVIEW_COLOR;
			}

			context.drawText(
					CLIENT.textRenderer,
					tokenString,
					x + hudX, y,
					color,
					true
			);

			hudX += CLIENT.textRenderer.getWidth(tokenString);
		}
	}
}
