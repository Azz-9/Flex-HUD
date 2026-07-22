package me.Azz_9.flex_hud.utils;


import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.Constants.MOD_ID;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import me.Azz_9.flex_hud.FlexHudLogger;

public class FaviconUtils {
	private static ResourceLocation currentServerFavicon = null;

	public static void registerServerIcon(byte[] faviconBytes) {
		DynamicTexture texture = createTextureFromBytes(faviconBytes);
		if (texture == null) {
			currentServerFavicon = null;
			return;
		}

		TextureManager textureManager = MINECRAFT.getTextureManager();
		currentServerFavicon = ResourceLocation.fromNamespaceAndPath(MOD_ID, "server_icon");

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

	public static ResourceLocation getCurrentServerFavicon() {
		return currentServerFavicon;
	}
}
