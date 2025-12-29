package me.Azz_9.flex_hud.client.utils;


import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class FaviconUtils {
	private static Identifier currentServerFavicon = null;

	public static void registerServerIcon(byte[] faviconBytes) {
		DynamicTexture texture = createTextureFromBytes(faviconBytes);
		if (texture == null) {
			currentServerFavicon = null;
			return;
		}

		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		currentServerFavicon = Identifier.fromNamespaceAndPath(MOD_ID, "server_icon");

		textureManager.register(currentServerFavicon, texture);
	}

	public static DynamicTexture createTextureFromBytes(byte[] favicon) {
		if (favicon == null) return null;

		try {
			InputStream stream = new ByteArrayInputStream(favicon);
			NativeImage image = NativeImage.read(stream);
			return new DynamicTexture(() -> "server_icon", image);
		} catch (Exception e) {
			FlexHudLogger.warn("Failed to load server favicon: {}", e.getMessage());
			return null;
		}
	}

	public static Identifier getCurrentServerFavicon() {
		return currentServerFavicon;
	}
}
