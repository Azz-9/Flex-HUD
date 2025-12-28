package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static java.util.Map.entry;

public class BiomeDisplay extends AbstractTextElement {
	private final ConfigBoolean biomeSpecificColor = new ConfigBoolean(true, "flex_hud.biome_display.config.biome_specific_color");

	//biome colors for coordinates overlay
	public static final Map<RegistryKey<Biome>, Integer> BIOME_COLORS = getBiomeColors();

	public BiomeDisplay(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.biome_display.config.enable");

		ConfigRegistry.register(getID(), "biomeSpecificColor", biomeSpecificColor);
	}

	@Override
	public void init() {
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.biome_display");
	}

	@Override
	public String getID() {
		return "biome_display";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && client.player == null) {
			return;
		}

		RegistryKey<Biome> biomeKey;
		String biomeName;

		if (Flex_hudClient.isInMoveElementScreen) {
			biomeKey = BiomeKeys.PLAINS;
			biomeName = "plains";
		} else {
			PlayerEntity player = client.player;

			if (client.world == null) {
				return;
			}

			biomeKey = client.world.getBiome(player.getBlockPos()).getKey().orElse(null);

			if (biomeKey == null) return;

			biomeName = biomeKey.getValue().getPath();
		}

		String prefix = "Biome: ";
		int prefixWidth = client.textRenderer.getWidth(prefix);
		setWidth(biomeName, prefixWidth);

		int biomeTextColor = (biomeSpecificColor.getValue() ?
				BIOME_COLORS.getOrDefault(biomeKey, 0xffffffff) :
				getColor());

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1.0f);

		drawBackground(context);

		context.drawText(client.textRenderer, prefix, 0, 0, getColor(), shadow.getValue());
		context.drawText(client.textRenderer, biomeName, prefixWidth, 0, biomeTextColor, shadow.getValue());

		matrices.pop();
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

	private static Map<RegistryKey<Biome>, Integer> getBiomeColors() {
		return Map.<RegistryKey<Biome>, Integer>ofEntries(
				entry(BiomeKeys.THE_VOID, 0xffffffff),
				entry(BiomeKeys.PLAINS, 0xff5e9d34),
				entry(BiomeKeys.SUNFLOWER_PLAINS, 0xfffcd500),
				entry(BiomeKeys.SNOWY_PLAINS, 0xff9dbcf0),
				entry(BiomeKeys.ICE_SPIKES, 0xff9dbcf0),
				entry(BiomeKeys.DESERT, 0xffe5d9af),
				entry(BiomeKeys.SWAMP, 0xff436024),
				entry(BiomeKeys.MANGROVE_SWAMP, 0xff436024),
				entry(BiomeKeys.FOREST, 0xff4d8a25),
				entry(BiomeKeys.FLOWER_FOREST, 0xfffc7dea),
				entry(BiomeKeys.BIRCH_FOREST, 0xffcccccc),
				entry(BiomeKeys.DARK_FOREST, 0xff366821),
				entry(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, 0xffcccccc),
				entry(BiomeKeys.OLD_GROWTH_PINE_TAIGA, 0xff4d6a4c),
				entry(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, 0xff4d6a4c),
				entry(BiomeKeys.TAIGA, 0xff4d6a4c),
				entry(BiomeKeys.SNOWY_TAIGA, 0xff9dbcf0),
				entry(BiomeKeys.SAVANNA, 0xff807b39),
				entry(BiomeKeys.SAVANNA_PLATEAU, 0xff807b39),
				entry(BiomeKeys.WINDSWEPT_HILLS, 0xff4d8a25),
				entry(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, 0xffff4d8a),
				entry(BiomeKeys.WINDSWEPT_FOREST, 0xff4d8a25),
				entry(BiomeKeys.WINDSWEPT_SAVANNA, 0xff807b39),
				entry(BiomeKeys.JUNGLE, 0xff1c6e06),
				entry(BiomeKeys.SPARSE_JUNGLE, 0xff1c6e06),
				entry(BiomeKeys.BAMBOO_JUNGLE, 0xff678c39),
				entry(BiomeKeys.BADLANDS, 0xffb15b25),
				entry(BiomeKeys.ERODED_BADLANDS, 0xffb15b25),
				entry(BiomeKeys.WOODED_BADLANDS, 0xffb15b25),
				entry(BiomeKeys.MEADOW, 0xff8d8d8d),
				entry(BiomeKeys.CHERRY_GROVE, 0xffbf789b),
				entry(BiomeKeys.GROVE, 0xff4d8a25),
				entry(BiomeKeys.SNOWY_SLOPES, 0xff9dbcf0),
				entry(BiomeKeys.FROZEN_PEAKS, 0xff8d8d8d),
				entry(BiomeKeys.JAGGED_PEAKS, 0xff8d8d8d),
				entry(BiomeKeys.STONY_PEAKS, 0xff8d8d8d),
				entry(BiomeKeys.RIVER, 0xff005eec),
				entry(BiomeKeys.FROZEN_RIVER, 0xff9dbcf0),
				entry(BiomeKeys.BEACH, 0xffe5d9af),
				entry(BiomeKeys.SNOWY_BEACH, 0xff9dbcf0),
				entry(BiomeKeys.STONY_SHORE, 0xff8d8d8d),
				entry(BiomeKeys.WARM_OCEAN, 0xff00fccf),
				entry(BiomeKeys.LUKEWARM_OCEAN, 0xff00fccf),
				entry(BiomeKeys.DEEP_LUKEWARM_OCEAN, 0xff00fccf),
				entry(BiomeKeys.OCEAN, 0xff005eec),
				entry(BiomeKeys.DEEP_OCEAN, 0xff005eec),
				entry(BiomeKeys.COLD_OCEAN, 0xff9dbcf0),
				entry(BiomeKeys.DEEP_COLD_OCEAN, 0xff9dbcf0),
				entry(BiomeKeys.FROZEN_OCEAN, 0xff9dbcf0),
				entry(BiomeKeys.DEEP_FROZEN_OCEAN, 0xff9dbcf0),
				entry(BiomeKeys.MUSHROOM_FIELDS, 0xff746f82),
				entry(BiomeKeys.DRIPSTONE_CAVES, 0xff816759),
				entry(BiomeKeys.LUSH_CAVES, 0xff576b2c),
				entry(BiomeKeys.DEEP_DARK, 0xff0d1f25),
				entry(BiomeKeys.NETHER_WASTES, 0xff880f0f),
				entry(BiomeKeys.WARPED_FOREST, 0xff0f8976),
				entry(BiomeKeys.CRIMSON_FOREST, 0xff880f0f),
				entry(BiomeKeys.SOUL_SAND_VALLEY, 0xff62493b),
				entry(BiomeKeys.BASALT_DELTAS, 0xff55576a),
				entry(BiomeKeys.THE_END, 0xff6d00a3),
				entry(BiomeKeys.END_HIGHLANDS, 0xff6d00a3),
				entry(BiomeKeys.END_MIDLANDS, 0xff6d00a3),
				entry(BiomeKeys.SMALL_END_ISLANDS, 0xff6d00a3),
				entry(BiomeKeys.END_BARRENS, 0xff6d00a3)
		);
	}
}
