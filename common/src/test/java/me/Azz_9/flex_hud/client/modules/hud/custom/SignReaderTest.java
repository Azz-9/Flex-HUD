package me.Azz_9.flex_hud.client.modules.hud.custom;

import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.state.properties.WoodType;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SignReaderTest {

	@BeforeAll
	static void bootstrapMinecraft() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	void everyWoodTypeHasSignTextures() throws IllegalAccessException {
		List<String> missingTextures = new ArrayList<>();

		for (WoodType woodType : getWoodTypes()) {
			checkTexture(SignReader.getSignTexture(woodType), missingTextures);
			checkTexture(SignReader.getHangingSignTexture(woodType), missingTextures);
		}

		assertTrue(
				missingTextures.isEmpty(),
				() -> "Missing sign textures:\n - "
						+ String.join("\n - ", missingTextures)
		);
	}

	private static boolean resourceExists(@NotNull ResourceLocation location) {
		String classpathPath = "assets/"
				+ location.getNamespace()
				+ "/"
				+ location.getPath();

		URL resource = SignReaderTest.class
				.getClassLoader()
				.getResource(classpathPath);

		return resource != null;
	}

	private static void checkTexture(
			@NotNull ResourceLocation texture,
			@NotNull List<String> missingTextures
	) {
		if (!resourceExists(texture)) {
			missingTextures.add(texture.toString());
		}
	}

	private static List<WoodType> getWoodTypes() throws IllegalAccessException {
		List<WoodType> woodTypes = new ArrayList<>();

		for (Field field : WoodType.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())
					&& field.getType() == WoodType.class) {
				woodTypes.add((WoodType) field.get(null));
			}
		}

		return woodTypes;
	}
}
