package me.Azz_9.flex_hud.client.modules.hud.custom;

import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BiomeDisplayTest {

	@Test
	public void everyBiomeHaveAColor() throws Exception {
		Field biomeColorsField = BiomeDisplay.class.getDeclaredField("BIOME_COLORS");
		biomeColorsField.setAccessible(true);

		@SuppressWarnings("unchecked")
		Map<ResourceKey<Biome>, Integer> biomeColors = (Map<ResourceKey<Biome>, Integer>) biomeColorsField.get(null);

		List<ResourceKey<Biome>> missingBiomes = new ArrayList<>();
		for (Field biomeField : Biomes.class.getDeclaredFields()) {
			@SuppressWarnings("unchecked")
			ResourceKey<Biome> biome = (ResourceKey<Biome>) biomeField.get(null);

			if (!biomeColors.containsKey(biome)) {
				missingBiomes.add(biome);
			}
		}

		assertTrue(
				missingBiomes.isEmpty(),
				() -> "Missing colors for biomes:\n" +
						missingBiomes.stream()
								.map(biome -> "- " + biome.identifier().getPath())
								.sorted()
								.collect(Collectors.joining("\n")) + "\n"
		);
	}
}
