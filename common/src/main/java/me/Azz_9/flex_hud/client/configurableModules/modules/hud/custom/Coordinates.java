package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.DisplayMode;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.Renderable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.RenderableText;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.joml.Matrix3x2fStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Coordinates extends AbstractTextElement {
	private ConfigBoolean showY = new ConfigBoolean(true, "flex_hud.coordinates.config.show_y");
	private ConfigInteger numberOfDigits = new ConfigInteger(0, "flex_hud.coordinates.config.number_of_digits", 0, 14);
	private ConfigBoolean showBiome = new ConfigBoolean(true, "flex_hud.coordinates.config.show_biome");
	private ConfigBoolean biomeSpecificColor = new ConfigBoolean(true, "flex_hud.coordinates.config.custom_biome_color");
	private ConfigBoolean showDirection = new ConfigBoolean(true, "flex_hud.coordinates.config.show_direction");
	private ConfigBoolean directionAbreviation = new ConfigBoolean(true, "flex_hud.coordinates.config.direction_abbreviation");
	private ConfigEnum<DisplayMode> displayMode = new ConfigEnum<>(DisplayMode.VERTICAL, "flex_hud.coordinates.config.orientation");

	//biome colors for coordinates overlay
	public static final Map<RegistryKey<Biome>, Integer> BIOME_COLORS = getBiomeColors();

	public Coordinates(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
	}

	@Override
	public void init() {
		this.enabled.setConfigTextTranslationKey("flex_hud.coordinates.config.enable");
	}

	@Override
	public String getID() {
		return "coordinates";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.coordinates");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender() || client.player == null) {
			return;
		}

		PlayerEntity player = client.player;

		List<Renderable> renderables = new ArrayList<>();

		// reset height and width
		this.height = 0;
		this.width = 0;

		// Get the truncated coordinates with the correct number of digits
		String xCoords = "X: " + BigDecimal.valueOf(player.getX()).setScale(this.numberOfDigits.getValue(), RoundingMode.FLOOR);
		String yCoords = "Y: " + BigDecimal.valueOf(player.getY()).setScale(this.numberOfDigits.getValue(), RoundingMode.FLOOR);
		String zCoords = "Z: " + BigDecimal.valueOf(player.getZ()).setScale(this.numberOfDigits.getValue(), RoundingMode.FLOOR);

		if (this.displayMode.getValue() == DisplayMode.VERTICAL) {

			int hudX = 0;
			int hudY = 0;

			renderables.add(new RenderableText(hudX, hudY, Text.of(xCoords), getColor(), this.shadow.getValue()));
			updateWidth(xCoords);
			if (this.showY.getValue()) {
				hudY += 10;
				renderables.add(new RenderableText(hudX, hudY, Text.of(yCoords), getColor(), this.shadow.getValue()));
				updateWidth(yCoords);
			}
			hudY += 10;
			renderables.add(new RenderableText(hudX, hudY, Text.of(zCoords), getColor(), this.shadow.getValue()));
			updateWidth(zCoords);

			if (this.showBiome.getValue()) {
				hudY += 10;
				renderBiome(hudX, hudY, renderables);
			}
			this.height = hudY + 10;

			if (this.showDirection.getValue()) {
				int widestCoords = Math.max(client.textRenderer.getWidth(xCoords), client.textRenderer.getWidth(yCoords));
				if (this.showY.getValue()) {
					widestCoords = Math.max(widestCoords, client.textRenderer.getWidth(zCoords));
				}
				hudX = 24 + widestCoords;
				hudY = 0;
				String[] direction = getDirection(player);
				String facing;
				String axisX = direction[2];
				String axisZ = direction[3];

				if (this.directionAbreviation.getValue()) {
					facing = direction[1];
				} else {
					facing = direction[0];
				}


				renderables.add(new RenderableText(hudX, hudY, Text.of(axisX), getColor(), this.shadow.getValue()));
				updateWidth(axisX, hudX);
				if (this.showY.getValue()) {
					hudY += 10;
					renderables.add(new RenderableText(hudX, hudY, Text.of(facing), getColor(), this.shadow.getValue()));
					updateWidth(facing, hudX);
				} else {
					renderables.add(new RenderableText(hudX + 8, hudY + 5, Text.of(facing), getColor(), this.shadow.getValue()));
					updateWidth(facing, hudX + 8);
				}
				hudY += 10;
				renderables.add(new RenderableText(hudX, hudY, Text.of(axisZ), getColor(), this.shadow.getValue()));
				updateWidth(axisZ, hudX);
			}

		} else {
			StringBuilder text = new StringBuilder();
			text.append(xCoords);
			if (this.showY.getValue()) {
				text.append("; ").append(yCoords);
			}
			text.append("; ").append(zCoords);
			text.insert(0, "(");
			text.append(")");
			if (this.showDirection.getValue()) {
				text.append(" ");
				if (this.directionAbreviation.getValue()) {
					text.append(getDirection(player)[1]);
				} else {
					text.append(getDirection(player)[0]);
				}
			}

			renderables.add(new RenderableText(0, 0, Text.of(text.toString()), getColor(), this.shadow.getValue()));
			updateWidth(text.toString());
			this.height = client.textRenderer.fontHeight;
			if (this.showBiome.getValue()) {
				renderBiome(0, 10, renderables);
				this.height += 10;
			}

		}

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(this.scale, this.scale);

		drawBackground(context);

		for (Renderable renderable : renderables) {
			renderable.render(context, tickCounter);
		}

		matrices.popMatrix();
	}

	private String[] getDirection(PlayerEntity p) {
		float yaw = (p.getYaw() % 360 + 360) % 360;

		if (337.5 < yaw || yaw < 22.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.south").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.south").getString(), "", "+"};
		} else if (22.5 <= yaw && yaw < 67.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.south_west").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.south_west").getString(), "-", "+"};
		} else if (67.5 <= yaw && yaw < 112.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.west").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.west").getString(), "-", ""};
		} else if (112.5 <= yaw && yaw < 157.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.north_west").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.north_west").getString(), "-", "-"};
		} else if (157.5 <= yaw && yaw < 202.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.north").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.north").getString(), "", "-"};
		} else if (202.5 <= yaw && yaw < 247.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.north_east").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.north_east").getString(), "+", "-"};
		} else if (247.5 <= yaw && yaw < 292.5) {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.east").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.east").getString(), "+", ""};
		} else {
			return new String[]{Text.translatable("flex_hud.coordinates.hud.direction.south_east").getString(),
					Text.translatable("flex_hud.coordinates.hud.direction_abbr.south_east").getString(), "+", "+"};
		}

	}

	private void renderBiome(int hudX, int hudY, List<Renderable> renderables) {
		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity p = client.player;

		if (client.world == null || p == null) {
			return;
		}

		RegistryKey<Biome> biomeKey = client.world.getBiome(p.getBlockPos()).getKey().orElse(null);
		String biomeName = client.world.getBiome(p.getBlockPos()).getIdAsString().replace("minecraft:", "");
		renderables.add(new RenderableText(hudX, hudY, Text.of("Biome: "), getColor(), this.shadow.getValue()));
		hudX += client.textRenderer.getWidth("Biome: ");
		int textColor = (biomeSpecificColor.getValue() ? BIOME_COLORS.getOrDefault(biomeKey, 0xffffffff) : getColor());
		renderables.add(new RenderableText(hudX, hudY, Text.of(biomeName), textColor, this.shadow.getValue()));
		updateWidth("Biome: " + biomeName);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 220;
				} else {
					buttonWidth = 185;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColor)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(color)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackground)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColor)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showY)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showBiome)
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(biomeSpecificColor)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showDirection)
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(directionAbreviation)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new IntFieldEntry.Builder()
								.setIntFieldWidth(20)
								.setVariable(numberOfDigits)
								.build(),
						new CyclingButtonEntry.Builder<DisplayMode>()
								.setCyclingButtonWidth(80)
								.setVariable(displayMode)
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
