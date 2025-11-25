package me.Azz_9.flex_hud.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class FaviconUtils {
	private static Identifier currentServerFavicon = null;

	public static void registerServerIcon(byte[] faviconBytes) {
		NativeImageBackedTexture texture = createTextureFromBytes(faviconBytes);
		if (texture == null) {
			currentServerFavicon = null;
			return;
		}

		TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
		currentServerFavicon = Identifier.of(MOD_ID, "server_icon");

		textureManager.registerTexture(currentServerFavicon, texture);
	}

	public static NativeImageBackedTexture createTextureFromBytes(byte[] favicon) {
		if (favicon == null) return null;

		try {
			InputStream stream = new ByteArrayInputStream(favicon);
			NativeImage image = NativeImage.read(stream);
			return new NativeImageBackedTexture(image);
		} catch (Exception e) {
			FlexHudLogger.warn("Failed to load server favicon: {}", e.getMessage());
			return null;
		}
	}

	public static Identifier getCurrentServerFavicon() {
		return currentServerFavicon;
	}
}
