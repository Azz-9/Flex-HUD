package me.Azz_9.flex_hud.client.utils.crosshair;

import me.Azz_9.flex_hud.client.utils.FlexHudLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class DynamicTexture {
	private final int width;
	private final int height;
	private final NativeImageBackedTexture texture;
	private final Identifier id;

	public DynamicTexture(String name, int width, int height) {
		this.width = width;
		this.height = height;
		this.texture = new NativeImageBackedTexture(width, height, true);
		this.id = Identifier.of(MOD_ID, name);
		MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
	}

	public Identifier getId() {
		return id;
	}

	public void updatePixels(int[][] pixels) {
		if (texture.getImage() == null) {
			FlexHudLogger.warn("Dynamic texture error : texture.getImage() is null");
			return;
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				texture.getImage().setColorArgb(x, y, pixels[y][x]);
			}
		}
		texture.upload();
	}

	public void close() {
		texture.close();
	}
}
