package me.Azz_9.better_hud.client.configurableMods.mods.hud.renderCallbacks;

import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.DisplayMode;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
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
import java.util.Map;

import static java.util.Map.entry;

public class Coordinates extends AbstractHudElement {
	public boolean showY = true;
	public int numberOfDigits = 0;
	public boolean showBiome = true;
	public boolean biomeSpecificColor = true;
	public boolean showDirection = true;
	public boolean directionAbreviation = true;
	public DisplayMode displayMode = DisplayMode.VERTICAL;

	//biome colors for coordinates overlay
	public static final Map<RegistryKey<Biome>, Integer> BIOME_COLORS = getBiomeColors();

	public Coordinates(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		super.render(context, tickCounter);

		MinecraftClient client = MinecraftClient.getInstance();

		if (!JsonConfigHelper.getInstance().isEnabled || !this.enabled || client == null || client.options.hudHidden || client.player == null) {
			return;
		}

		PlayerEntity player = client.player;

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh));
		matrices.scale(this.scale, this.scale);

		// Get the truncated coordinates with the correct amount of digits
		String xCoords = "X: " + BigDecimal.valueOf(player.getX()).setScale(this.numberOfDigits, RoundingMode.DOWN);
		String yCoords = "Y: " + BigDecimal.valueOf(player.getY()).setScale(this.numberOfDigits, RoundingMode.DOWN);
		String zCoords = "Z: " + BigDecimal.valueOf(player.getZ()).setScale(this.numberOfDigits, RoundingMode.DOWN);

		if (this.displayMode == DisplayMode.VERTICAL) {

			int hudX = 0;
			int hudY = 0;

			context.drawText(client.textRenderer, xCoords, hudX, hudY, getColor(), this.shadow);
			updateWidth(xCoords);
			if (this.showY) {
				hudY += 10;
				context.drawText(client.textRenderer, yCoords, hudX, hudY, getColor(), this.shadow);
				updateWidth(yCoords);
			}
			hudY += 10;
			context.drawText(client.textRenderer, zCoords, hudX, hudY, getColor(), this.shadow);
			updateWidth(zCoords);

			if (this.showBiome) {
				hudY += 10;
				renderBiome(context, hudX, hudY);
			}
			this.height = hudY + 10;

			if (this.showDirection) {
				int widestCoords = Math.max(client.textRenderer.getWidth(xCoords), client.textRenderer.getWidth(yCoords));
				if (this.showY) {
					widestCoords = Math.max(widestCoords, client.textRenderer.getWidth(zCoords));
				}
				hudX = 24 + widestCoords;
				hudY = 0;
				String[] direction = getDirection(player);
				String facing;
				String axisX = direction[2];
				String axisZ = direction[3];

				if (this.directionAbreviation) {
					facing = direction[1];
				} else {
					facing = direction[0];
				}


				context.drawText(client.textRenderer, axisX, hudX, hudY, getColor(), this.shadow);
				updateWidth(axisX, hudX);
				if (this.showY) {
					hudY += 10;
					context.drawText(client.textRenderer, facing, hudX, hudY, getColor(), this.shadow);
					updateWidth(facing, hudX);
				} else {
					context.drawText(client.textRenderer, facing, hudX + 8, hudY + 5, getColor(), this.shadow);
					updateWidth(facing, hudX + 8);
				}
				hudY += 10;
				context.drawText(client.textRenderer, axisZ, hudX, hudY, getColor(), this.shadow);
				updateWidth(axisZ, hudX);
			}

		} else {
			StringBuilder text = new StringBuilder();
			text.append(xCoords);
			if (this.showY) {
				text.append("; ").append(yCoords);
			}
			text.append("; ").append(zCoords);
			text.insert(0, "(");
			text.append(")");
			if (this.showDirection) {
				text.append(" ");
				if (this.directionAbreviation) {
					text.append(getDirection(player)[1]);
				} else {
					text.append(getDirection(player)[0]);
				}
			}

			context.drawText(client.textRenderer, text.toString(), 0, 0, getColor(), this.shadow);
			updateWidth(text.toString());
			this.height = client.textRenderer.fontHeight;
			if (this.showBiome) {
				renderBiome(context, 0, 10);
				this.height += 10;
			}

		}

		matrices.popMatrix();
	}

	private String[] getDirection(PlayerEntity p) {
		float yaw = (p.getYaw() % 360 + 360) % 360;

		if (337.5 < yaw || yaw < 22.5) {
			return new String[]{Text.translatable("better_hud.coordinates.hud.direction.south").getString(),
					Text.translatable("better_hud.coordinates.hud.direction_abbr.south").getString(), "", "+"};
		} else if (22.5 <= yaw && yaw < 67.5) {
			return new String[]{Text.translatable("better_hud.coordinates.hud.direction.south_west").getString(),
					Text.translatable("better_hud.coordinates.hud.direction_abbr.south_west").getString(), "-", "+"};
		} else if (67.5 <= yaw && yaw < 112.5) {
			return new String[]{Text.translatable("better_hud.coordinates.hud.direction.west").getString(),
					Text.translatable("better_hud.coordinates.hud.direction_abbr.west").getString(), "-", ""};
		} else if (112.5 <= yaw && yaw < 157.5) {
			return new String[]{Text.translatable("better_hud.coordinates.hud.direction.north_west").getString(),
					Text.translatable("better_hud.coordinates.hud.direction_abbr.north_west").getString(), "-", "-"};
		} else if (157.5 <= yaw && yaw < 202.5) {
			return new String[]{Text.translatable("better_hud.coordinates.hud.direction.north").getString(),
					Text.translatable("better_hud.coordinates.hud.direction_abbr.north").getString(), "", "-"};
		} else if (202.5 <= yaw && yaw < 247.5) {
			return new String[]{Text.translatable("better_hud.coordinates.hud.direction.north_east").getString(),
					Text.translatable("better_hud.coordinates.hud.direction_abbr.north_east").getString(), "+", "-"};
		} else if (247.5 <= yaw && yaw < 292.5) {
			return new String[]{Text.translatable("better_hud.coordinates.hud.direction.east").getString(),
					Text.translatable("better_hud.coordinates.hud.direction_abbr.east").getString(), "+", ""};
		} else {
			return new String[]{Text.translatable("better_hud.coordinates.hud.direction.south_east").getString(),
					Text.translatable("better_hud.coordinates.hud.direction_abbr.south_east").getString(), "+", "+"};
		}

	}

	private void renderBiome(DrawContext drawContext, int hudX, int hudY) {
		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity p = client.player;

		if (client.world == null || p == null) {
			return;
		}

		RegistryKey<Biome> biomeKey = client.world.getBiome(p.getBlockPos()).getKey().orElse(null);
		String biomeName = client.world.getBiome(p.getBlockPos()).getIdAsString().replace("minecraft:", "");
		drawContext.drawText(client.textRenderer, "Biome: ", hudX, hudY, getColor(), this.shadow);
		hudX += client.textRenderer.getWidth("Biome: ");
		int textColor = (biomeSpecificColor ? BIOME_COLORS.getOrDefault(biomeKey, 0xffffffff) : getColor());
		drawContext.drawText(client.textRenderer, biomeName, hudX, hudY, textColor, this.shadow);
		updateWidth("Biome: " + biomeName);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(Text.translatable("better_hud.coordinates"), parent, parentScrollAmount) {
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
								.setToggled(enabled)
								.setDefaultValue(true)
								.setOnToggle((toggled) -> enabled = toggled)
								.setText(Text.translatable("better_hud.coordinates.config.enable"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(shadow)
								.setDefaultValue(true)
								.setOnToggle(toggled -> shadow = toggled)
								.setText(Text.translatable("better_hud.global.config.text_shadow"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(chromaColor)
								.setDefaultValue(false)
								.setOnToggle(toggled -> chromaColor = toggled)
								.setText(Text.translatable("better_hud.global.config.chroma_text_color"))
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setColor(color)
								.setDefaultColor(0xffffff)
								.setOnColorChange(newColor -> color = newColor)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.setText(Text.translatable("better_hud.global.config.text_color"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(drawBackground)
								.setDefaultValue(false)
								.setOnToggle(toggled -> drawBackground = toggled)
								.setText(Text.translatable("better_hud.global.config.show_background"))
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setColor(backgroundColor)
								.setDefaultColor(0x313131)
								.setOnColorChange(newColor -> backgroundColor = newColor)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.setText(Text.translatable("better_hud.global.config.background_color"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showY)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showY = toggled)
								.setText(Text.translatable("better_hud.coordinates.config.show_y"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showBiome)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showBiome = toggled)
								.setText(Text.translatable("better_hud.coordinates.config.show_biome"))
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(biomeSpecificColor)
								.setDefaultValue(true)
								.setOnToggle(toggled -> biomeSpecificColor = toggled)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.setText(Text.translatable("better_hud.coordinates.config.custom_biome_color"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showDirection)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showDirection = toggled)
								.setText(Text.translatable("better_hud.coordinates.config.show_direction"))
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(directionAbreviation)
								.setDefaultValue(true)
								.setOnToggle(toggled -> directionAbreviation = toggled)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.setText(Text.translatable("better_hud.coordinates.config.direction_abbreviation"))
								.build(),
						new IntFieldEntry.Builder()
								.setIntFieldWidth(20)
								.setValue(numberOfDigits)
								.setMin(0)
								.setMax(14)
								.setDefaultValue(0)
								.setOnValueChange(value -> numberOfDigits = value)
								.setText(Text.translatable("better_hud.coordinates.config.number_of_digits"))
								.build(),
						new CyclingButtonEntry.Builder<DisplayMode>()
								.setCyclingButtonWidth(80)
								.setValue(displayMode)
								.setDefaultValue(DisplayMode.VERTICAL)
								.setOnValueChange(value -> displayMode = value)
								.setText(Text.translatable("better_hud.coordinates.config.orientation"))
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
				entry(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, 0xffccccc),
				entry(BiomeKeys.OLD_GROWTH_PINE_TAIGA, 0xff4d6a4c),
				entry(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, 0xff4d6a4),
				entry(BiomeKeys.TAIGA, 0xff4d6a4c),
				entry(BiomeKeys.SNOWY_TAIGA, 0xff9dbcf0),
				entry(BiomeKeys.SAVANNA, 0xff807b39),
				entry(BiomeKeys.SAVANNA_PLATEAU, 0xff807b39),
				entry(BiomeKeys.WINDSWEPT_HILLS, 0xff4d8a25),
				entry(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, 0xff4d8a),
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
