package me.Azz_9.flex_hud.client.tickables;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class LivingEntityHeadTexturesTest {
	private static final String MINECRAFT_TEXTURES_ROOT = "assets/minecraft/textures/entity/";
	private static final String FLEX_HUD_HEADS_ROOT =
			"assets/flex_hud/textures/gui/sprites/hud/living_entities/minecraft/";
	private static final String EXCLUSIONS_FILE = "living-entity-head-exclusions.txt";

	private static Set<String> minecraftEntityTextures;
	private static List<ExclusionRule> exclusionRules;

	@BeforeAll
	static void discoverTextures() throws IOException, URISyntaxException {
		minecraftEntityTextures = findMinecraftEntityTextures();
		exclusionRules = readExclusionRules();
	}

	@Test
	void everyMinecraftEntityTextureIsEitherAHeadOrExplicitlyExcluded() {
		List<String> missingHeads = minecraftEntityTextures.stream()
				.filter(texture -> !headExists(texture))
				.filter(texture -> exclusionRules.stream().noneMatch(rule -> rule.matches(texture)))
				.sorted()
				.toList();

		assertTrue(
				missingHeads.isEmpty(),
				() -> "Minecraft entity textures without a Flex HUD head or exclusion:\n - "
						+ String.join("\n - ", missingHeads)
		);
	}

	@Test
	void everyExclusionStillMatchesAtLeastOneMissingHead() {
		List<String> obsoleteRules = exclusionRules.stream()
				.filter(rule -> minecraftEntityTextures.stream()
						.noneMatch(texture -> rule.matches(texture) && !headExists(texture)))
				.map(ExclusionRule::source)
				.toList();

		assertTrue(
				obsoleteRules.isEmpty(),
				() -> "Obsolete or redundant mob head exclusions:\n - "
						+ String.join("\n - ", obsoleteRules)
		);
	}

	@Test
	void entityTextureIdentifiersAreConvertedToSpriteIdentifiers() {
		Identifier entityTexture = Identifier.withDefaultNamespace("textures/entity/cow/cow.png");
		Identifier sprite = LivingEntitiesTickable.spriteFromEntityTexture(entityTexture);

		assertEquals("flex_hud:hud/living_entities/minecraft/cow/cow", sprite.toString());
		assertEquals(
				"flex_hud:textures/gui/sprites/hud/living_entities/minecraft/cow/cow.png",
				LivingEntitiesTickable.spriteResource(sprite).toString()
		);
	}

	private static boolean headExists(String relativeMinecraftTexture) {
		return LivingEntityHeadTexturesTest.class.getClassLoader()
				.getResource(FLEX_HUD_HEADS_ROOT + relativeMinecraftTexture) != null;
	}

	private static Set<String> findMinecraftEntityTextures() throws IOException, URISyntaxException {
		Path minecraftLocation = Path.of(
				LivingEntity.class.getProtectionDomain().getCodeSource().getLocation().toURI()
		);
		Set<String> textures = new TreeSet<>();

		if (Files.isDirectory(minecraftLocation)) {
			Path root = minecraftLocation.resolve(MINECRAFT_TEXTURES_ROOT);
			try (var paths = Files.walk(root)) {
				paths.filter(Files::isRegularFile)
						.map(root::relativize)
						.map(path -> path.toString().replace('\\', '/'))
						.filter(path -> path.endsWith(".png"))
						.forEach(textures::add);
			}
		} else {
			try (ZipFile minecraftJar = new ZipFile(minecraftLocation.toFile())) {
				minecraftJar.stream()
						.map(ZipEntry::getName)
						.filter(path -> path.startsWith(MINECRAFT_TEXTURES_ROOT))
						.filter(path -> path.endsWith(".png"))
						.map(path -> path.substring(MINECRAFT_TEXTURES_ROOT.length()))
						.forEach(textures::add);
			}
		}

		assertTrue(!textures.isEmpty(), "No Minecraft entity textures were discovered");
		return textures;
	}

	private static List<ExclusionRule> readExclusionRules() throws IOException {
		InputStream stream = LivingEntityHeadTexturesTest.class.getClassLoader()
				.getResourceAsStream(EXCLUSIONS_FILE);
		if (stream == null) {
			throw new IOException("Missing test resource: " + EXCLUSIONS_FILE);
		}

		List<ExclusionRule> rules = new ArrayList<>();
		Set<String> uniquePatterns = new HashSet<>();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream, StandardCharsets.UTF_8))) {
			int lineNumber = 0;
			for (String line; (line = reader.readLine()) != null; ) {
				lineNumber++;
				String pattern = line.strip();
				if (pattern.isEmpty() || pattern.startsWith("#")) {
					continue;
				}
				if (!uniquePatterns.add(pattern)) {
					throw new IOException("Duplicate exclusion at line " + lineNumber + ": " + pattern);
				}
				rules.add(new ExclusionRule(pattern, lineNumber));
			}
		}

		return List.copyOf(rules);
	}

	private record ExclusionRule(String pattern, int lineNumber) {
		boolean matches(String texture) {
			if (pattern.endsWith("/**")) {
				return texture.startsWith(pattern.substring(0, pattern.length() - 2));
			}
			return pattern.equals(texture);
		}

		String source() {
			return "line " + lineNumber + ": " + pattern;
		}
	}
}
