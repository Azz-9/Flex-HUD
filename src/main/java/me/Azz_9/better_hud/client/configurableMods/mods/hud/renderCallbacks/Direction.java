package me.Azz_9.better_hud.client.configurableMods.mods.hud.renderCallbacks;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Matrix3x2fStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Direction extends AbstractHudElement {
	public boolean showMarker = true;
	public boolean showIntermediatePoint = true;
	public boolean showXaerosMapWaypoints = true;

	public Direction(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	public void render(DrawContext context, RenderTickCounter tickCounter) {
		super.render(context, tickCounter);

		MinecraftClient client = MinecraftClient.getInstance();

		if (!JsonConfigHelper.getInstance().isEnabled || !this.enabled || client == null || client.options.hudHidden || client.player == null) {
			return;
		}

		PlayerEntity player = client.player;

		int screenWidth = client.getWindow().getScaledWidth();

		this.width = screenWidth / 4;
		this.height = 30;

		// Calcul de la direction (yaw)
		float yaw = (player.getYaw() % 360 + 360) % 360;

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh));
		matrices.scale(this.scale, this.scale);

		context.enableScissor(0, 0, this.width, this.height);

		// Affichage des points cardinaux
		drawCompassPoint(context, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.south"), 0, yaw);
		drawCompassPoint(context, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.south_west"), 45, yaw);
		drawCompassPoint(context, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.west"), 90, yaw);
		drawCompassPoint(context, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.north_west"), 135, yaw);
		drawCompassPoint(context, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.north"), 180, yaw);
		drawCompassPoint(context, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.north_east"), 225, yaw);
		drawCompassPoint(context, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.east"), 270, yaw);
		drawCompassPoint(context, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.south_east"), 315, yaw);

		// Affichage des points intermediaires
		if (this.showIntermediatePoint) {
			for (int i = 0; i < 8; i++) {
				drawIntermediatePoint(context, matrices, 15 * i * 3 + 15, yaw);
				drawIntermediatePoint(context, matrices, 15 * i * 3 + 30, yaw);
			}
		}

		// Affichage des waypoints Xaero's minimap
		if (this.showXaerosMapWaypoints && Better_hudClient.isXaerosMinimapLoaded) {
			drawXaerosMapWaypoints(context, matrices, yaw);
		}

		context.disableScissor();

		// Affichage du marqueur de direction
		if (this.showMarker) {
			String markerText = "▼";

			matrices.pushMatrix();
			matrices.translate((this.width / 2.0f) - (client.textRenderer.getWidth(markerText) / 2.0f), 0);
			matrices.scale(1.0f, 0.5f);
			context.drawText(client.textRenderer, markerText, 0, 0, getColor(), this.shadow);
			matrices.popMatrix();
		}

		matrices.popMatrix();
	}

	private void drawCompassPoint(DrawContext drawContext, Matrix3x2fStack matrices, Text label, int angle, float yaw) {
		MinecraftClient client = MinecraftClient.getInstance();
		int screenWidth = client.getWindow().getScaledWidth();

		float angleDifference = (angle - yaw + 540) % 360 - 180;

		if (Math.abs(angleDifference) <= 120) {
			float scaleFactor = 1.25f;
			// Calculer la position X de chaque point cardinal en fonction de l'angle
			float positionX = ((this.width / 2.0f) + (angleDifference * (screenWidth / 720.0f)));
			float pointWidth = client.textRenderer.getWidth(label) * scaleFactor;

			// Afficher le label des directions avec couleur et taille de texte ajustée
			matrices.pushMatrix();
			matrices.translate(positionX - pointWidth / 2.0f, 10);
			matrices.scale(scaleFactor, scaleFactor);
			drawContext.drawText(client.textRenderer, label, 0, 0, getColorWithFadeEffect(positionX), this.shadow);
			matrices.popMatrix();
		}
	}

	private void drawIntermediatePoint(DrawContext drawContext, Matrix3x2fStack matrices, int angle, float yaw) {
		MinecraftClient CLIENT = MinecraftClient.getInstance();
		int screenWidth = CLIENT.getWindow().getScaledWidth();

		float angleDifference = (angle - yaw + 540) % 360 - 180;

		if (Math.abs(angleDifference) <= 120) {
			// Calculer la position X de chaque point cardinal en fonction de l'angle
			float positionX = ((this.width / 2.0f) + (angleDifference * (screenWidth / 720.0f)));

			matrices.pushMatrix();
			matrices.translate(positionX - (CLIENT.textRenderer.getWidth("|") / 2.0f), 12);
			matrices.scale(1.0f, 0.75f); // slightly smaller
			drawContext.drawText(CLIENT.textRenderer, "|", 0, 0, getColorWithFadeEffect(positionX), this.shadow);
			matrices.popMatrix();


			matrices.pushMatrix();
			matrices.translate(positionX - (CLIENT.textRenderer.getWidth(String.valueOf(angle)) / 4.0f), 20);
			matrices.scale(0.5f, 0.5f); // 2 times smaller
			drawContext.drawText(CLIENT.textRenderer, String.valueOf(angle), 0, 0, getColorWithFadeEffect(positionX), this.shadow);
			matrices.popMatrix();

		}

	}

	private static class Waypoint {
		public String initials;
		public double x, z;
		public int color;
		public boolean disabled;

		public Waypoint(String initials, double x, double z, int color, boolean disabled) {
			this.initials = initials;
			this.x = x;
			this.z = z;
			this.color = color;
			this.disabled = disabled;
		}

		@Override
		public String toString() {
			return "Waypoint{initials='" + initials + "', x=" + x + ", z=" + z + ", color=" + color + ", disabled=" + disabled + "}";
		}
	}

	private File getXaeroWaypointsFolder() {
		MinecraftClient client = MinecraftClient.getInstance();
		File mcDir = client.runDirectory;

		if (client.getCurrentServerEntry() == null && client.getServer() != null) {
			// Utilisez le nom du monde local
			String worldName = client.getServer().getSaveProperties().getLevelName();
			return new File(mcDir, "xaero/minimap/" + worldName);
		} else {
			// Utilisez l'adresse du serveur
			String serverAddress = client.getCurrentServerEntry().address;
			return new File(mcDir, "xaero/minimap/Multiplayer_" + serverAddress);
		}
	}

	private File getCurrentDimensionWaypointsFile() {
		MinecraftClient client = MinecraftClient.getInstance();
		File worldWaypointsFolder = getXaeroWaypointsFolder();

		if (client.player != null) {
			Identifier dimension = client.player.getWorld().getRegistryKey().getValue();

			if (dimension.equals(World.OVERWORLD.getValue())) {
				return new File(worldWaypointsFolder, "dim%0/waypoints.txt");
			} else if (dimension.equals(World.NETHER.getValue())) {
				return new File(worldWaypointsFolder, "dim%-1/waypoints.txt");
			} else if (dimension.equals(World.END.getValue())) {
				return new File(worldWaypointsFolder, "dim%1/waypoints.txt");
			}
		}
		return null;
	}

	// Méthode pour charger les waypoints depuis un fichier
	private List<Waypoint> loadWaypoints(File file) {
		List<Waypoint> waypoints = new ArrayList<>();
		if (file.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String line;

				while ((line = reader.readLine()) != null) {
					// Ignorer les lignes qui commencent par "#"
					if (line.startsWith("#") || line.trim().isEmpty()) {
						continue;
					}

					// Vérifier si la ligne commence par "waypoint:"
					if (line.startsWith("waypoint:")) {
						// Supprimer "waypoint:" et diviser par ":"
						String[] parts = line.substring("waypoint:".length()).split(":");

						// Vérifier qu'il y a assez de parties
						if (parts.length >= 7) {
							String initials = parts[1];
							int x = Integer.parseInt(parts[2]);
							int z = Integer.parseInt(parts[4]);
							int color = Integer.parseInt(parts[5]);
							boolean disabled = Boolean.parseBoolean(parts[6]);

							// Ajouter un waypoint à la liste
							waypoints.add(new Waypoint(initials, x + 0.5, z + 0.5, color, disabled));
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		return waypoints;
	}

	private float calculateAngle(double playerX, double playerZ, double waypointX, double waypointZ) {
		// Différences de coordonnées
		double deltaX = waypointX - playerX;
		double deltaZ = waypointZ - playerZ;

		// Calcul de l'angle en radians (atan2 gère les quadrants correctement)
		double angleRadians = Math.atan2(deltaX, deltaZ);

		// Conversion en degrés
		double angleDegrees = Math.toDegrees(angleRadians);

		return (float) -angleDegrees;
	}

	private void drawXaerosMapWaypoints(DrawContext drawContext, Matrix3x2fStack matrices, float yaw) {
		MinecraftClient client = MinecraftClient.getInstance();
		int screenWidth = client.getWindow().getScaledWidth();
		List<Waypoint> waypoints = loadWaypoints(Objects.requireNonNull(getCurrentDimensionWaypointsFile()));

		for (Waypoint waypoint : waypoints) {
			if (!waypoint.disabled && client.player != null) {
				PlayerEntity player = client.player;
				float angle = calculateAngle(player.getX(), player.getZ(), waypoint.x, waypoint.z);

				float angleDifference = (angle - yaw + 540) % 360 - 180;

				if (Math.abs(angleDifference) <= 120) {
					// Calculer la position X de chaque point cardinal en fonction de l'angle
					float positionX = ((this.width / 2.0f) + (angleDifference * (screenWidth / 720.0f)));

					int backgroundColor;
					try {
						backgroundColor = ((getAlpha(positionX) / 2) << 24) | Formatting.byColorIndex(waypoint.color).getColorValue();
					} catch (Exception e) {
						backgroundColor = 0x00FFFFFF | (getAlpha(positionX) / 2) << 24; // Color par défaut si le format n'est pas reconnu
					}

					matrices.pushMatrix();
					matrices.translate(positionX - (client.textRenderer.getWidth(waypoint.initials) / 2.0f), 2);
					matrices.scale(0.75f, 0.75f);
					renderTextWithBackground(drawContext, waypoint.initials, 0, 0, backgroundColor, getColorWithFadeEffect(positionX));
					matrices.popMatrix();
				}
			}
		}
	}

	private void renderTextWithBackground(DrawContext drawContext, String text, int x, int y, int backgroundColor, int textColor) {
		MinecraftClient client = MinecraftClient.getInstance();

		// Calculer la largeur et la hauteur du texte
		int textWidth = client.textRenderer.getWidth(text);
		int textHeight = client.textRenderer.fontHeight;

		// Dessiner le rectangle de fond
		drawContext.fill(x - 2, y - 1, x + textWidth + 1, y + textHeight - 1, backgroundColor);

		// Dessiner le texte par-dessus le rectangle
		drawContext.drawText(client.textRenderer, text, x, y, textColor, this.shadow);
	}

	private int getAlpha(float CenterXOfDrawing) {
		double distanceFromCenter = Math.abs(CenterXOfDrawing - width / 2.0);

		int alpha = 0xff;
		if (distanceFromCenter > width / 4.0) {
			alpha = Math.max(0xff - (int) ((distanceFromCenter - width / 4.0) / (width / 4.0) * 0xff), 0);
		}

		return alpha;
	}

	private int getColorWithFadeEffect(float CenterXOfDrawing) {
		return (getAlpha(CenterXOfDrawing) << 24) | (getColor() & 0x00ffffff);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(Text.translatable("better_hud.direction"), parent, parentScrollAmount) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 230;
				} else {
					buttonWidth = 170;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(enabled)
								.setDefaultValue(true)
								.setOnToggle((toggled) -> enabled = toggled)
								.setText(Text.translatable("better_hud.direction.config.enable"))
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
								.setToggled(showMarker)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showMarker = toggled)
								.setText(Text.translatable("better_hud.direction.config.show_marker"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showIntermediatePoint)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showIntermediatePoint = toggled)
								.setText(Text.translatable("better_hud.direction.config.show_intermediate_point"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(showXaerosMapWaypoints)
								.setDefaultValue(true)
								.setOnToggle(toggled -> showXaerosMapWaypoints = toggled)
								.setText(Text.translatable("better_hud.direction.config.show_xaeros_map_waypoints"))
								.build()
				);
			}
		};
	}
}
