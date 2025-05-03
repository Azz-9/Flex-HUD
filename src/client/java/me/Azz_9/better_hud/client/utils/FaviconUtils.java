package me.Azz_9.better_hud.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class FaviconUtils {
	public static NativeImageBackedTexture createTextureFromBytes(byte[] favicon) {
		if (favicon == null) return null;

		try {
			InputStream stream = new ByteArrayInputStream(favicon);
			NativeImage image = NativeImage.read(stream);
			return new NativeImageBackedTexture(() -> "server_icon", image);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Identifier registerServerIcon(byte[] faviconBytes) {
		NativeImageBackedTexture texture = createTextureFromBytes(faviconBytes);
		if (texture == null) return null;

		TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
		Identifier id = Identifier.of(MOD_ID, "server_icon");

		textureManager.registerTexture(id, texture);
		return id;
	}
}
