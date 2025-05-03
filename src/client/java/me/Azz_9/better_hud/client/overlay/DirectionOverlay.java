package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.utils.ChromaColor;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.Direction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.Azz_9.better_hud.client.Better_hudClient.isXaerosMinimapLoaded;

public class DirectionOverlay extends HudElement {
	public boolean showMarker = true;
	public boolean showIntermediatePoint = true;
	public boolean showXaerosMapWaypoints = true;

	public DirectionOverlay(double defaultX, double defaultY) { // by default this element is horizontally centered
		super(defaultX, defaultY);
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden || CLIENT.player == null) {
			return;
		}

		int usedColor = (chromaColor ? ChromaColor.getColor() : this.color);

		PlayerEntity player = CLIENT.player;

		int screenWidth = CLIENT.getWindow().getScaledWidth();

		this.width = screenWidth / 4;
		this.height = 30;

		// Calcul de la direction (yaw)
		float yaw = (player.getYaw() % 360 + 360) % 360;

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.enableScissor(0, 0, this.width, this.height);

		// Affichage des points cardinaux
		drawCompassPoint(drawContext, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.south"), 0, yaw, usedColor);
		drawCompassPoint(drawContext, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.south_west"), 45, yaw, usedColor);
		drawCompassPoint(drawContext, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.west"), 90, yaw, usedColor);
		drawCompassPoint(drawContext, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.north_west"), 135, yaw, usedColor);
		drawCompassPoint(drawContext, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.north"), 180, yaw, usedColor);
		drawCompassPoint(drawContext, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.north_east"), 225, yaw, usedColor);
		drawCompassPoint(drawContext, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.east"), 270, yaw, usedColor);
		drawCompassPoint(drawContext, matrices, Text.translatable("better_hud.direction.hud.direction_abbr.south_east"), 315, yaw, usedColor);

		// Affichage des points intermediaires
		if (this.showIntermediatePoint) {
			for (int i = 0; i < 8; i++) {
				drawIntermediatePoint(drawContext, matrices, 15 * i * 3 + 15, yaw, usedColor);
				drawIntermediatePoint(drawContext, matrices, 15 * i * 3 + 30, yaw, usedColor);
			}
		}

		// Affichage des waypoints Xaero's minimap
		if (this.showXaerosMapWaypoints && isXaerosMinimapLoaded) {
			drawXaerosMapWaypoints(drawContext, matrices, yaw);
		}

		drawContext.disableScissor();

		// Affichage du marqueur de direction
		if (this.showMarker) {
			matrices.push();
			matrices.translate((this.width / 2.0f) - (CLIENT.textRenderer.getWidth("▼") / 2.0f), 0, 0.0f);
			matrices.scale(1.0f, 0.5f, 1.0f);
			drawContext.drawText(CLIENT.textRenderer, "▼", 0, 0, usedColor, this.shadow);
			matrices.pop();
		}

		if (drawBackground) {
			drawContext.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor);
		}

		matrices.pop();

	}

	private void drawCompassPoint(DrawContext drawContext, MatrixStack matrices, Text label, int angle, float yaw, int usedColor) {
		MinecraftClient CLIENT = MinecraftClient.getInstance();
		int screenWidth = CLIENT.getWindow().getScaledWidth();

		float angleDifference = (angle - yaw + 540) % 360 - 180;

		if (Math.abs(angleDifference) <= 120) {
			// Calculer la position X de chaque point cardinal en fonction de l'angle
			float positionX = ((this.width / 2.0f) + (angleDifference * (screenWidth / 720.0f)));
			positionX = positionX - (CLIENT.textRenderer.getWidth(label) / 1.6f);

			// Afficher le label des directions avec couleur et taille de texte ajustée
			matrices.push();
			matrices.translate(positionX, 10, 0);
			matrices.scale(1.25f, 1.25f, 1.5f); // make the text 1.5 times bigger
			drawContext.drawText(CLIENT.textRenderer, label, 0, 0, usedColor, this.shadow);
			matrices.pop();

		}
	}

	private void drawIntermediatePoint(DrawContext drawContext, MatrixStack matrices, int angle, float yaw, int usedColor) {
		MinecraftClient CLIENT = MinecraftClient.getInstance();
		int screenWidth = CLIENT.getWindow().getScaledWidth();

		float angleDifference = (angle - yaw + 540) % 360 - 180;

		if (Math.abs(angleDifference) <= 120) {
			// Calculer la position X de chaque point cardinal en fonction de l'angle
			float positionX = ((this.width / 2.0f) + (angleDifference * (screenWidth / 720.0f)));

			matrices.push();
			matrices.translate(positionX - (CLIENT.textRenderer.getWidth("|") / 2.0f), 12, 0);
			matrices.scale(1.0f, 0.75f, 1.0f);
			drawContext.drawText(CLIENT.textRenderer, "|", 0, 0, usedColor, this.shadow);
			matrices.pop();


			matrices.push();
			matrices.translate(positionX - (CLIENT.textRenderer.getWidth(String.valueOf(angle)) / 4.0f), 20, 0);
			matrices.scale(0.5f, 0.5f, 1.0f); // 2 times smaller
			drawContext.drawText(CLIENT.textRenderer, String.valueOf(angle), 0, 0, usedColor, this.shadow);
			matrices.pop();

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
		MinecraftClient CLIENT = MinecraftClient.getInstance();
		File mcDir = CLIENT.runDirectory;

		if (CLIENT.getCurrentServerEntry() == null) {
			// Utilisez le nom du monde local
			String worldName = CLIENT.getServer().getSaveProperties().getLevelName();
			return new File(mcDir, "xaero/minimap/" + worldName);
		} else {
			// Utilisez l'adresse du serveur
			String serverAddress = CLIENT.getCurrentServerEntry().address;
			return new File(mcDir, "xaero/minimap/Multiplayer_" + serverAddress);
		}
	}

	private File getCurrentDimensionWaypointsFile() {
		File worldWaypointsFolder = getXaeroWaypointsFolder();
		Identifier dimension = MinecraftClient.getInstance().player.getWorld().getRegistryKey().getValue();

		if (dimension.equals(World.OVERWORLD.getValue())) {
			return new File(worldWaypointsFolder, "dim%0/waypoints.txt");
		} else if (dimension.equals(World.NETHER.getValue())) {
			return new File(worldWaypointsFolder, "dim%-1/waypoints.txt");
		} else if (dimension.equals(World.END.getValue())) {
			return new File(worldWaypointsFolder, "dim%1/waypoints.txt");
		}
		return null;
	}

	// Méthode pour charger les waypoints depuis un fichier
	public static List<Waypoint> loadWaypoints(File file) {
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
				e.printStackTrace();
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

	private void drawXaerosMapWaypoints(DrawContext drawContext, MatrixStack matrices, float yaw) {
		MinecraftClient CLIENT = MinecraftClient.getInstance();
		int screenWidth = CLIENT.getWindow().getScaledWidth();
		List<Waypoint> waypoints = loadWaypoints(Objects.requireNonNull(getCurrentDimensionWaypointsFile()));

		for (Waypoint waypoint : waypoints) {
			if (!waypoint.disabled) {
				PlayerEntity player = CLIENT.player;
				float angle = calculateAngle(player.getX(), player.getZ(), waypoint.x, waypoint.z);

				float angleDifference = (angle - yaw + 540) % 360 - 180;

				if (Math.abs(angleDifference) <= 120) {
					// Calculer la position X de chaque point cardinal en fonction de l'angle
					float positionX = ((this.width / 2.0f) + (angleDifference * (screenWidth / 720.0f)));

					int backgroundColor;
					try {
						backgroundColor = (0x7F << 24) | Formatting.byColorIndex(waypoint.color).getColorValue();
					} catch (Exception e) {
						backgroundColor = 0x7FFFFFFF; // Color par défaut si le format n'est pas reconnu
					}

					matrices.push();
					matrices.translate(positionX - (CLIENT.textRenderer.getWidth(waypoint.initials) / 2.0f), 2, 0);
					matrices.scale(0.75f, 0.75f, 1);
					renderTextWithBackground(drawContext, waypoint.initials, 0, 0, backgroundColor);
					matrices.pop();
				}
			}
		}
	}

	private void renderTextWithBackground(DrawContext drawContext, String text, int x, int y, int backgroundColor) {
		MinecraftClient CLIENT = MinecraftClient.getInstance();

		// Calculer la largeur et la hauteur du texte
		int textWidth = CLIENT.textRenderer.getWidth(text);
		int textHeight = CLIENT.textRenderer.fontHeight;

		// Dessiner le rectangle de fond
		drawContext.fill(x - 2, y - 1, x + textWidth + 1, y + textHeight - 1, backgroundColor);

		// Dessiner le texte par-dessus le rectangle
		drawContext.drawText(CLIENT.textRenderer, text, x, y, (chromaColor ? ChromaColor.getColor() : this.color), this.shadow);
	}

	@Override
	public Screen getConfigScreen(Screen parent) {
		return new Direction(parent, 0);
	}
}
// TODO faire l'effet fondu sur les extrémités de la boussole (trop dur aled)