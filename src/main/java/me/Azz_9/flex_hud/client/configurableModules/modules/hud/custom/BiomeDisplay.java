package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.Map;

import static java.util.Map.entry;

public class BiomeDisplay extends AbstractTextElement {
	private final ConfigBoolean biomeSpecificColor = new ConfigBoolean(true, "flex_hud.biome_display.config.biome_specific_color");

	//biome colors for coordinates overlay
	public static final Map<ResourceKey<Biome>, Integer> BIOME_COLORS = getBiomeColors();

	public BiomeDisplay(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.biome_display.config.enable");

		ConfigRegistry.register(getID(), "biomeSpecificColor", biomeSpecificColor);
	}

	@Override
	public void init() {
		setHeight(Minecraft.getInstance().font.lineHeight);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.biome_display");
	}

	@Override
	public String getID() {
		return "biome_display";
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();

		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && minecraft.player == null) {
			return;
		}

		ResourceKey<Biome> biomeKey;
		String biomeName;

		if (Flex_hudClient.isInMoveElementScreen) {
			biomeKey = Biomes.PLAINS;
			biomeName = "plains";
		} else {
			LocalPlayer player = minecraft.player;

			if (minecraft.level == null) {
				return;
			}

			biomeKey = minecraft.level.getBiome(player.getOnPos()).unwrapKey().orElse(null);

			if (biomeKey == null) return;

			biomeName = biomeKey.identifier().getPath();
		}

		String prefix = "Biome: ";
		int prefixWidth = minecraft.font.width(prefix);
		setWidth(biomeName, prefixWidth);

		int biomeTextColor = (biomeSpecificColor.getValue() ?
				BIOME_COLORS.getOrDefault(biomeKey, 0xffffffff) :
				getColor());

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		graphics.drawString(minecraft.font, prefix, 0, 0, getColor(), shadow.getValue());
		graphics.drawString(minecraft.font, biomeName, prefixWidth, 0, biomeTextColor, shadow.getValue());

		matrices.popMatrix();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(color)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackground)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(biomeSpecificColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}

	private static Map<ResourceKey<Biome>, Integer> getBiomeColors() {
		return Map.<ResourceKey<Biome>, Integer>ofEntries(
				entry(Biomes.THE_VOID, 0xffffffff),
				entry(Biomes.PLAINS, 0xff5e9d34),
				entry(Biomes.SUNFLOWER_PLAINS, 0xfffcd500),
				entry(Biomes.SNOWY_PLAINS, 0xff9dbcf0),
				entry(Biomes.ICE_SPIKES, 0xff9dbcf0),
				entry(Biomes.DESERT, 0xffe5d9af),
				entry(Biomes.SWAMP, 0xff436024),
				entry(Biomes.MANGROVE_SWAMP, 0xff436024),
				entry(Biomes.FOREST, 0xff4d8a25),
				entry(Biomes.FLOWER_FOREST, 0xfffc7dea),
				entry(Biomes.BIRCH_FOREST, 0xffcccccc),
				entry(Biomes.DARK_FOREST, 0xff366821),
				entry(Biomes.OLD_GROWTH_BIRCH_FOREST, 0xffcccccc),
				entry(Biomes.OLD_GROWTH_PINE_TAIGA, 0xff4d6a4c),
				entry(Biomes.OLD_GROWTH_SPRUCE_TAIGA, 0xff4d6a4c),
				entry(Biomes.TAIGA, 0xff4d6a4c),
				entry(Biomes.SNOWY_TAIGA, 0xff9dbcf0),
				entry(Biomes.SAVANNA, 0xff807b39),
				entry(Biomes.SAVANNA_PLATEAU, 0xff807b39),
				entry(Biomes.WINDSWEPT_HILLS, 0xff4d8a25),
				entry(Biomes.WINDSWEPT_GRAVELLY_HILLS, 0xffff4d8a),
				entry(Biomes.WINDSWEPT_FOREST, 0xff4d8a25),
				entry(Biomes.WINDSWEPT_SAVANNA, 0xff807b39),
				entry(Biomes.JUNGLE, 0xff1c6e06),
				entry(Biomes.SPARSE_JUNGLE, 0xff1c6e06),
				entry(Biomes.BAMBOO_JUNGLE, 0xff678c39),
				entry(Biomes.BADLANDS, 0xffb15b25),
				entry(Biomes.ERODED_BADLANDS, 0xffb15b25),
				entry(Biomes.WOODED_BADLANDS, 0xffb15b25),
				entry(Biomes.MEADOW, 0xff8d8d8d),
				entry(Biomes.CHERRY_GROVE, 0xffbf789b),
				entry(Biomes.GROVE, 0xff4d8a25),
				entry(Biomes.SNOWY_SLOPES, 0xff9dbcf0),
				entry(Biomes.FROZEN_PEAKS, 0xff8d8d8d),
				entry(Biomes.JAGGED_PEAKS, 0xff8d8d8d),
				entry(Biomes.STONY_PEAKS, 0xff8d8d8d),
				entry(Biomes.RIVER, 0xff005eec),
				entry(Biomes.FROZEN_RIVER, 0xff9dbcf0),
				entry(Biomes.BEACH, 0xffe5d9af),
				entry(Biomes.SNOWY_BEACH, 0xff9dbcf0),
				entry(Biomes.STONY_SHORE, 0xff8d8d8d),
				entry(Biomes.WARM_OCEAN, 0xff00fccf),
				entry(Biomes.LUKEWARM_OCEAN, 0xff00fccf),
				entry(Biomes.DEEP_LUKEWARM_OCEAN, 0xff00fccf),
				entry(Biomes.OCEAN, 0xff005eec),
				entry(Biomes.DEEP_OCEAN, 0xff005eec),
				entry(Biomes.COLD_OCEAN, 0xff9dbcf0),
				entry(Biomes.DEEP_COLD_OCEAN, 0xff9dbcf0),
				entry(Biomes.FROZEN_OCEAN, 0xff9dbcf0),
				entry(Biomes.DEEP_FROZEN_OCEAN, 0xff9dbcf0),
				entry(Biomes.MUSHROOM_FIELDS, 0xff746f82),
				entry(Biomes.DRIPSTONE_CAVES, 0xff816759),
				entry(Biomes.LUSH_CAVES, 0xff576b2c),
				entry(Biomes.DEEP_DARK, 0xff0d1f25),
				entry(Biomes.NETHER_WASTES, 0xff880f0f),
				entry(Biomes.WARPED_FOREST, 0xff0f8976),
				entry(Biomes.CRIMSON_FOREST, 0xff880f0f),
				entry(Biomes.SOUL_SAND_VALLEY, 0xff62493b),
				entry(Biomes.BASALT_DELTAS, 0xff55576a),
				entry(Biomes.THE_END, 0xff6d00a3),
				entry(Biomes.END_HIGHLANDS, 0xff6d00a3),
				entry(Biomes.END_MIDLANDS, 0xff6d00a3),
				entry(Biomes.SMALL_END_ISLANDS, 0xff6d00a3),
				entry(Biomes.END_BARRENS, 0xff6d00a3)
		);
	}
}
