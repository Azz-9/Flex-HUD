package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.tickables.LivingEntitiesTickable;
import me.Azz_9.flex_hud.compat.CompatManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class Compass extends AbstractTextElement {
	private final ConfigBoolean showMarker = new ConfigBoolean(true, "flex_hud.compass.config.show_marker");
	private final ConfigBoolean showDegrees = new ConfigBoolean(false, "flex_hud.compass.config.show_degrees");
	private final ConfigBoolean showIntermediatePoint = new ConfigBoolean(true, "flex_hud.compass.config.show_intermediate_point");
	public final ConfigBoolean showXaerosMapWaypoints = new ConfigBoolean(true, "flex_hud.compass.config.show_xaeros_map_waypoints");
	public final ConfigBoolean showJourneyMapWaypoints = new ConfigBoolean(true, "flex_hud.compass.config.show_journey_map_waypoints");
	public final ConfigBoolean overrideLocatorBar = new ConfigBoolean(false, "flex_hud.compass.config.override_locator_bar");
	public final ConfigBoolean showMobs = new ConfigBoolean(false, "flex_hud.compass.config.show_mobs");
	public final ConfigBoolean showTamedEntitiesPoint = new ConfigBoolean(false, "flex_hud.compass.config.show_tamed_entities_point");
	public final ConfigBoolean showOnlyPets = new ConfigBoolean(false, "flex_hud.compass.config.show_only_pets");
	private final ConfigEnum<IconsSize> iconsSize = new ConfigEnum<>(IconsSize.class, IconsSize.SMALL, "flex_hud.compass.config.icons_size");

	private final Identifier ARROW_UP = Identifier.ofVanilla("hud/locator_bar_arrow_up");
	private final Identifier ARROW_DOWN = Identifier.ofVanilla("hud/locator_bar_arrow_down");

	private List<XaeroWaypoint> xaeroWaypoints = new ArrayList<>();
	private List<JourneyMapWaypoint> journeyMapWaypoints = new ArrayList<>();

	public Compass(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.compass.config.enable");

		ConfigRegistry.register(getID(), "showMarker", showMarker);
		ConfigRegistry.register(getID(), "showDegrees", showDegrees);
		ConfigRegistry.register(getID(), "showIntermediatePoint", showIntermediatePoint);
		ConfigRegistry.register(getID(), "showXaerosMapWaypoints", showXaerosMapWaypoints);
		ConfigRegistry.register(getID(), "showJourneyMapWaypoints", showJourneyMapWaypoints);
		ConfigRegistry.register(getID(), "overrideLocatorBar", overrideLocatorBar);
		ConfigRegistry.register(getID(), "showMobs", showMobs);
		ConfigRegistry.register(getID(), "showTamedEntitiesPoint", showTamedEntitiesPoint);
		ConfigRegistry.register(getID(), "showOnlyPets", showOnlyPets);
		ConfigRegistry.register(getID(), "iconsSize", iconsSize);

		setHeight(30);
		setWidth(210);
		if (showDegrees.getValue()) setHeight(getHeight() + 8);
		switch (iconsSize.getValue()) {
			case MEDIUM -> setHeight(getHeight() + 8);
			case LARGE -> setHeight(getHeight() + 12);
		}

		showDegrees.setOnChange((value) -> {
			if (value) setHeight(getHeight() + 8);
			else setHeight(getHeight() - 8);
		});

		iconsSize.setOnChange((value) -> {
			switch (value) {
				case SMALL -> setHeight(getHeight() - 12);
				case MEDIUM -> setHeight(getHeight() + 8);
				case LARGE -> setHeight(getHeight() + 4);
			}
		});
	}

	@Override
	public String getID() {
		return "compass";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.compass");
	}

	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity player = client.player;

		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && player == null) {
			return;
		}

		float yaw;
		if (Flex_hudClient.isInMoveElementScreen) {
			yaw = 180;
		} else {
			// Calcul de la direction (yaw)
			yaw = (player.getYaw() % 360 + 360) % 360;
		}

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(context);

		context.enableScissor(0, 0, getWidth(), getHeight());

		int hudY = 10;
		if (showDegrees.getValue()) {
			hudY += 8;
		}
		switch (iconsSize.getValue()) {
			case MEDIUM -> hudY += 8;
			case LARGE -> hudY += 12;
		}

		// Affichage des points cardinaux
		drawCompassPoint(context, matrices, Text.translatable("flex_hud.compass.hud.direction_abbr.south"), 0, yaw, hudY);
		drawCompassPoint(context, matrices, Text.translatable("flex_hud.compass.hud.direction_abbr.south_west"), 45, yaw, hudY);
		drawCompassPoint(context, matrices, Text.translatable("flex_hud.compass.hud.direction_abbr.west"), 90, yaw, hudY);
		drawCompassPoint(context, matrices, Text.translatable("flex_hud.compass.hud.direction_abbr.north_west"), 135, yaw, hudY);
		drawCompassPoint(context, matrices, Text.translatable("flex_hud.compass.hud.direction_abbr.north"), 180, yaw, hudY);
		drawCompassPoint(context, matrices, Text.translatable("flex_hud.compass.hud.direction_abbr.north_east"), 225, yaw, hudY);
		drawCompassPoint(context, matrices, Text.translatable("flex_hud.compass.hud.direction_abbr.east"), 270, yaw, hudY);
		drawCompassPoint(context, matrices, Text.translatable("flex_hud.compass.hud.direction_abbr.south_east"), 315, yaw, hudY);

		// Affichage des points intermediaires
		if (this.showIntermediatePoint.getValue()) {
			hudY += 2;

			for (int i = 0; i < 8; i++) {
				drawIntermediatePoint(context, matrices, 15 * i * 3 + 15, yaw, hudY);
				drawIntermediatePoint(context, matrices, 15 * i * 3 + 30, yaw, hudY);
			}
		}

		if (!Flex_hudClient.isInMoveElementScreen) {
			if (showMobs.getValue()) {
				renderAllMobs(context, tickCounter, yaw, matrices);
			} else if (showTamedEntitiesPoint.getValue()) {
				if (showOnlyPets.getValue()) {
					renderPetEntities(context, tickCounter, yaw, matrices);
				} else {
					renderTamedEntityPoint(context, tickCounter, yaw, matrices);
				}
			}

			// Override locator bar
			if (overrideLocatorBar.getValue()) {
				renderLocatorBarWaypoints(context, matrices);
			}

			// Affichage des waypoints Xaero's minimap
			if (this.showXaerosMapWaypoints.getValue() && CompatManager.isXaeroMinimapLoaded()) {
				drawXaerosMapWaypoints(context, matrices, yaw, tickCounter);
			}

			if (this.showJourneyMapWaypoints.getValue() && CompatManager.isJourneyMapLoaded()) {
				drawJourneyMapWaypoints(context, matrices, yaw, tickCounter);
			}
		}

		context.disableScissor();

		if (this.showDegrees.getValue()) {
			String degrees = String.valueOf(Math.round(yaw));
			matrices.pushMatrix();
			matrices.translate((getWidth() / 2.0f) - (client.textRenderer.getWidth(degrees) / 2.0f) * 0.75f, 1);
			matrices.scale(0.75f, 0.75f);
			context.drawText(client.textRenderer, degrees, 0, 0, getColor(), this.shadow.getValue());
			matrices.popMatrix();
		}

		// Affichage du marqueur de direction
		if (this.showMarker.getValue()) {
			String markerText = "▼";

			matrices.pushMatrix();
			matrices.translate((getWidth() / 2.0f) - (client.textRenderer.getWidth(markerText) / 2.0f), this.showDegrees.getValue() ? 8 : 0);
			matrices.scale(1.0f, 0.5f);
			context.drawText(client.textRenderer, markerText, 0, 0, getColor(), this.shadow.getValue());
			matrices.popMatrix();
		}

		matrices.popMatrix();
	}

	private void drawCompassPoint(DrawContext drawContext, Matrix3x2fStack matrices, Text label, int angle, float yaw, int y) {
		MinecraftClient client = MinecraftClient.getInstance();

		float angleDifference = (angle - yaw + 540) % 360 - 180;

		if (Math.abs(angleDifference) <= 120) {
			float scaleFactor = 1.25f;
			// Calculer la position X de chaque point cardinal en fonction de l'angle
			float positionX = ((getWidth() / 2.0f) + (angleDifference * (getWidth() / 180.0f)));
			float pointWidth = client.textRenderer.getWidth(label) * scaleFactor;

			// Afficher le label des directions avec couleur et taille de texte ajustée
			matrices.pushMatrix();
			matrices.translate(positionX - pointWidth / 2.0f, y);
			matrices.scale(scaleFactor, scaleFactor);
			drawContext.drawText(client.textRenderer, label, 0, 0, getColorWithFadeEffect(positionX), this.shadow.getValue());
			matrices.popMatrix();
		}
	}

	private void drawIntermediatePoint(DrawContext drawContext, Matrix3x2fStack matrices, int angle, float yaw, int y) {
		MinecraftClient client = MinecraftClient.getInstance();

		float angleDifference = (angle - yaw + 540) % 360 - 180;

		if (Math.abs(angleDifference) <= 120) {
			// Calculer la position X de chaque point cardinal en fonction de l'angle
			float positionX = ((getWidth() / 2.0f) + (angleDifference * (getWidth() / 180.0f)));

			matrices.pushMatrix();
			matrices.translate(positionX - (client.textRenderer.getWidth("|") / 2.0f), y);
			matrices.scale(1.0f, 0.75f); // slightly smaller
			drawContext.drawText(client.textRenderer, "|", 0, 0, getColorWithFadeEffect(positionX), this.shadow.getValue());
			matrices.popMatrix();


			matrices.pushMatrix();
			matrices.translate(positionX - (client.textRenderer.getWidth(String.valueOf(angle)) / 4.0f), y + 8);
			matrices.scale(0.5f, 0.5f); // 2 times smaller
			drawContext.drawText(client.textRenderer, String.valueOf(angle), 0, 0, getColorWithFadeEffect(positionX), this.shadow.getValue());
			matrices.popMatrix();

		}

	}

	private float calculateAngle(double playerX, double playerZ, double pointX, double pointZ) {
		// Différences de coordonnées
		double deltaX = pointX - playerX;
		double deltaZ = pointZ - playerZ;

		// Calcul de l'angle en radians (atan2 gère les quadrants correctement)
		double angleRadians = Math.atan2(deltaX, deltaZ);

		// Conversion en degrés
		double angleDegrees = Math.toDegrees(angleRadians);

		return (float) -angleDegrees;
	}

	private void drawXaerosMapWaypoints(DrawContext context, Matrix3x2fStack matrices, float yaw, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity player = client.player;
		if (player == null) return;

		float y = this.showDegrees.getValue() ? 10 : 2;
		float scale = 0.75f;
		switch (iconsSize.getValue()) {
			case MEDIUM -> {
				scale = 1f;
				y += 4.75f;
			}
			case LARGE -> {
				scale = 1.25f;
				y += 6f;
			}
		}

		for (XaeroWaypoint waypoint : this.xaeroWaypoints) {
			if (waypoint.isDisabled()) continue;

			double x = waypoint.getX() + 0.5;
			double z = waypoint.getZ() + 0.5;

			Position lerpedPosition = player.getLerpedPos(tickCounter.getTickProgress(true));
			float angle = calculateAngle(lerpedPosition.getX(), lerpedPosition.getZ(), x, z);

			float angleDifference = (angle - yaw + 540) % 360 - 180;
			if (Math.abs(angleDifference) <= 120) {
				float positionX = ((getWidth() / 2.0f) + (angleDifference * (getWidth() / 180.0f)));

				int color = waypoint.getColor();
				int backgroundColor = ((getAlpha(positionX) / 2) << 24) | (color & 0x00ffffff);

				matrices.pushMatrix();
				matrices.translate(positionX - (client.textRenderer.getWidth(waypoint.getInitials()) / 2.0f), y);
				matrices.scale(scale, scale);
				renderTextWithBackground(context, waypoint.getInitials(), 0, 0, backgroundColor, 0xffffff | (getAlpha(positionX) << 24));
				matrices.popMatrix();
			}
		}
	}

	private void renderTextWithBackground(DrawContext context, String text, int x, int y, int backgroundColor, int textColor) {
		MinecraftClient client = MinecraftClient.getInstance();

		// Calculer la largeur et la hauteur du texte
		int textWidth = client.textRenderer.getWidth(text);
		int textHeight = client.textRenderer.fontHeight;

		// Dessiner le rectangle de fond
		context.fill(x - 2, y - 1, x + textWidth + 1, y + textHeight - 1, backgroundColor);

		// Dessiner le texte par-dessus le rectangle
		context.drawText(client.textRenderer, text, x, y, textColor, this.shadow.getValue());
	}

	private void drawJourneyMapWaypoints(DrawContext context, Matrix3x2fStack matrices, float yaw, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity player = client.player;
		if (player == null) return;

		float y = this.showDegrees.getValue() ? 10 : 2;
		float scale = 0.45f;
		switch (iconsSize.getValue()) {
			case MEDIUM -> {
				scale = 0.6f;
				y += 3f;
			}
			case LARGE -> {
				scale = 0.75f;
				y += 5f;
			}
		}

		for (JourneyMapWaypoint waypoint : this.journeyMapWaypoints) {
			if (waypoint.isDisabled() || !waypoint.isInPlayerDimension()) continue;

			double x = waypoint.getX() + 0.5;
			double z = waypoint.getZ() + 0.5;

			Position lerpedPosition = player.getLerpedPos(tickCounter.getTickProgress(true));
			float angle = calculateAngle(lerpedPosition.getX(), lerpedPosition.getZ(), x, z);

			float angleDifference = (angle - yaw + 540) % 360 - 180;
			if (Math.abs(angleDifference) <= 120) {
				float positionX = ((getWidth() / 2.0f) + (angleDifference * (getWidth() / 180.0f)));

				Identifier icon = waypoint.getIcon();
				int iconWidth = waypoint.getIconWidth();
				int iconHeight = waypoint.getIconHeight();
				if (icon == null || client.getResourceManager().getResource(icon).isEmpty()) {
					icon = Identifier.of(MOD_ID, "misc/journeymap-default-icon.png");
					iconWidth = 13;
					iconHeight = 13;
				}

				matrices.pushMatrix();
				matrices.translate(positionX - ((iconWidth * scale) / 2.0f), y);
				matrices.scale(scale, scale);
				context.drawTexture(RenderPipelines.GUI_TEXTURED, icon, 0, 0, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight, ColorHelper.withAlpha(getAlpha(positionX), waypoint.getColor()));
				matrices.popMatrix();
			}
		}
	}

	private int getAlpha(float CenterXOfDrawing) {
		double distanceFromCenter = Math.abs(CenterXOfDrawing - getWidth() / 2.0);

		int alpha = 0xff;
		if (distanceFromCenter > getWidth() / 4.0) {
			alpha = Math.max(0xff - (int) ((distanceFromCenter - getWidth() / 4.0) / (getWidth() / 4.0) * 0xff), 0);
		}

		return alpha;
	}

	private int getColorWithFadeEffect(float CenterXOfDrawing) {
		return ColorHelper.withAlpha(getAlpha(CenterXOfDrawing), getColor());
	}

	private void renderLocatorBarWaypoints(DrawContext context, Matrix3x2fStack matrices) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.getCameraEntity() == null || client.player == null) {
			return;
		}

		float y;
		float scale;
		switch (iconsSize.getValue()) {
			case SMALL -> {
				y = showDegrees.getValue() ? 10.5f : 2.5f;
				scale = 0.70f;
			}
			case MEDIUM -> {
				y = showDegrees.getValue() ? 13f : 5f;
				scale = 1f;
			}
			case LARGE -> {
				y = showDegrees.getValue() ? 15f : 7f;
				scale = 1.25f;
			}
			default -> {
				y = 13f;
				scale = 1f;
			}
		}

		client.player.networkHandler.getWaypointHandler().forEachWaypoint(client.getCameraEntity(), (waypoint) -> {
			if (!(Boolean) waypoint.getSource().left().map((uuid) -> uuid.equals(client.getCameraEntity().getUuid())).orElse(false)) {
				double angleDifference = waypoint.getRelativeYaw(client.world, client.gameRenderer.getCamera());

				if (Math.abs(angleDifference) <= 120) {
					// Calculer la position X de chaque point cardinal en fonction de l'angle
					double positionX = ((getWidth() / 2.0f) + (angleDifference * (getWidth() / 180.0f)));

					Waypoint.Config config = waypoint.getConfig();
					WaypointStyleAsset waypointStyleAsset = client.getWaypointStyleAssetManager().get(config.style);
					float distance = (float) Math.sqrt(waypoint.squaredDistanceTo(client.getCameraEntity()));
					Identifier waypointIdentifier = waypointStyleAsset.getSpriteForDistance(distance);
					int color = config.color.orElseGet(() -> waypoint.getSource().map((uuid) -> ColorHelper.withBrightness(ColorHelper.withAlpha(255, uuid.hashCode()), 0.9F), (name) -> ColorHelper.withBrightness(ColorHelper.withAlpha(255, name.hashCode()), 0.9F)));

					int textureSize = 9;

					matrices.pushMatrix();
					matrices.translate((float) (positionX - textureSize / 2.0), y);
					matrices.scale(scale, scale);


					context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, waypointIdentifier, 0, 0, textureSize, textureSize, ColorHelper.withAlpha(getAlpha((float) positionX), color));
					TrackedWaypoint.Pitch pitch = waypoint.getPitch(client.world, client.gameRenderer);
					if (pitch != TrackedWaypoint.Pitch.NONE) {
						int offset;
						Identifier arrowIdentifier;
						if (pitch == TrackedWaypoint.Pitch.DOWN) {
							offset = 8;
							arrowIdentifier = ARROW_DOWN;
						} else {
							offset = -4;
							arrowIdentifier = ARROW_UP;
						}

						context.drawGuiTexture(
								RenderPipelines.GUI_TEXTURED,
								arrowIdentifier,
								1, offset,
								7, 5,
								ColorHelper.withAlpha(getAlpha((float) positionX), 0xffffff)
						);
					}

					matrices.popMatrix();
				}
			}
		});
	}

	private void renderMobs(DrawContext context, RenderTickCounter tickCounter, float yaw, Matrix3x2fStack matrices, List<LivingEntitiesTickable.EntityTexture> entityTextures) {
		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity player = client.player;
		if (player == null) {
			return;
		}

		int y = this.showDegrees.getValue() ? 10 : 2;
		switch (iconsSize.getValue()) {
			case MEDIUM -> y += 3;
			case LARGE -> y += 5;
		}

		float scale = switch (iconsSize.getValue()) {
			case SMALL -> 0.75f;
			case MEDIUM -> 1f;
			case LARGE -> 1.25f;
		};

		for (LivingEntitiesTickable.EntityTexture entity : entityTextures) {

			int textureSize = 9;

			Position lerpedPosition = player.getLerpedPos(tickCounter.getTickProgress(true));
			Vec3d entityLerpedPos = entity.entity().getLerpedPos(tickCounter.getTickProgress(true));
			float angle = calculateAngle(lerpedPosition.getX(), lerpedPosition.getZ(), entityLerpedPos.x, entityLerpedPos.z);
			float angleDifference = (angle - yaw + 540) % 360 - 180;

			if (Math.abs(angleDifference) <= 120) {
				float positionX = ((getWidth() / 2.0f) + (angleDifference * (getWidth() / 180.0f)));

				matrices.pushMatrix();
				matrices.translate(positionX - (textureSize * scale) / 2.0f, y);
				matrices.scale(scale, scale);

				context.drawTexture(RenderPipelines.GUI_TEXTURED, entity.texture(), 0, 0, 0, 0, textureSize, textureSize, textureSize, textureSize, ColorHelper.withAlpha(getAlpha(positionX), 0xffffff));


				TrackedWaypoint.Pitch pitch = getEntityPitch(entity.entity(), client);
				if (pitch != TrackedWaypoint.Pitch.NONE) {
					int offset;
					Identifier arrowIdentifier;

					if (pitch == TrackedWaypoint.Pitch.DOWN) {
						offset = 8;
						arrowIdentifier = ARROW_DOWN;
					} else {
						offset = -4;
						arrowIdentifier = ARROW_UP;
					}

					context.drawGuiTexture(
							RenderPipelines.GUI_TEXTURED,
							arrowIdentifier,
							1, offset,
							7, 5,
							ColorHelper.withAlpha(getAlpha(positionX), 0xffffff)
					);
				}

				matrices.popMatrix();
			}
		}
	}

	private void renderPetEntities(DrawContext context, RenderTickCounter tickCounter, float yaw, Matrix3x2fStack matrices) {
		renderMobs(context, tickCounter, yaw, matrices, LivingEntitiesTickable.getPetsEntities());
	}

	private void renderTamedEntityPoint(DrawContext context, RenderTickCounter tickCounter, float yaw, Matrix3x2fStack matrices) {
		renderMobs(context, tickCounter, yaw, matrices, LivingEntitiesTickable.getTamedEntities());
	}

	private void renderAllMobs(DrawContext context, RenderTickCounter tickCounter, float yaw, Matrix3x2fStack matrices) {
		renderMobs(context, tickCounter, yaw, matrices, LivingEntitiesTickable.getMobEntities());
	}

	public TrackedWaypoint.Pitch getEntityPitch(Entity entity, MinecraftClient client) {
		// en gros c'est la même que le code de minecraft pour la locator bar
		Vec3d entityPos = entity.getLerpedPos(client.getRenderTickCounter().getTickProgress(true));
		Vec3d projected = client.gameRenderer.project(entityPos);

		boolean behind = projected.z > 1.0;
		double d = behind ? -projected.y : projected.y;

		if (d < -1.0) return TrackedWaypoint.Pitch.DOWN;
		if (d > 1.0) return TrackedWaypoint.Pitch.UP;

		if (behind) {
			if (projected.y > 0.0) return TrackedWaypoint.Pitch.UP;
			if (projected.y < 0.0) return TrackedWaypoint.Pitch.DOWN;
		}

		return TrackedWaypoint.Pitch.NONE;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 230;
				} else {
					buttonWidth = 175;
				}

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
								.setVariable(showMarker)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showDegrees)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showIntermediatePoint)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showXaerosMapWaypoints)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.setToggleable(CompatManager::isXaeroMinimapLoaded)
								.setGetTooltip((t) -> {
									if (!CompatManager.isXaeroMinimapLoaded()) {
										return Tooltip.of(Text.translatable("flex_hud.compass.config.show_xaeros_map_waypoints.not_installed_tooltip"));
									}
									return null;
								})
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showJourneyMapWaypoints)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.setToggleable(CompatManager::isJourneyMapLoaded)
								.setGetTooltip((t) -> {
									if (!CompatManager.isJourneyMapLoaded()) {
										return Tooltip.of(Text.translatable("flex_hud.compass.config.show_journey_map_waypoints.not_installed_tooltip"));
									}
									return null;
								})
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(overrideLocatorBar)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showMobs)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showTamedEntitiesPoint)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), true)
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showOnlyPets)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), false)
								.addDependency(this.getConfigList().getEntry(this.getConfigList().getEntryCount() - 2), true)
								.build(),
						new CyclingButtonEntry.Builder<IconsSize>()
								.setCyclingButtonWidth(80)
								.setVariable(iconsSize)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}

	public void setXaeroWaypoints(List<XaeroWaypoint> xaeroWaypoints) {
		this.xaeroWaypoints = xaeroWaypoints;
	}

	public void setJourneyMapWaypoints(List<JourneyMapWaypoint> journeyMapWaypoints) {
		this.journeyMapWaypoints = journeyMapWaypoints;
	}

	public abstract static class ModdedWaypoint {
		private final double x, z;

		public ModdedWaypoint(double x, double z) {
			this.x = x;
			this.z = z;
		}

		public double getX() {
			return x;
		}

		public double getZ() {
			return z;
		}
	}

	public static class XaeroWaypoint extends ModdedWaypoint {
		private final int COLOR;
		private final boolean DISABLED;
		private final String INITIALS;

		public XaeroWaypoint(double x, double z, int color, boolean disabled, String initials) {
			super(x, z);
			this.COLOR = color;
			this.DISABLED = disabled;
			this.INITIALS = initials;
		}

		public int getColor() {
			return COLOR;
		}

		public boolean isDisabled() {
			return DISABLED;
		}

		public String getInitials() {
			return INITIALS;
		}
	}

	public static class JourneyMapWaypoint extends ModdedWaypoint {
		private final int COLOR;
		private final boolean DISABLED;
		private final Identifier ICON;
		private final int ICON_WIDTH;
		private final int ICON_HEIGHT;
		private final boolean IS_IN_PLAYER_DIMENSION;

		public JourneyMapWaypoint(double x, double z, int color, boolean disabled, Identifier icon, int iconWidth, int iconHeight, boolean isInPlayerDimension) {
			super(x, z);
			this.COLOR = color;
			this.DISABLED = disabled;
			this.ICON = icon;
			this.ICON_WIDTH = iconWidth;
			this.ICON_HEIGHT = iconHeight;
			this.IS_IN_PLAYER_DIMENSION = isInPlayerDimension;
		}

		public int getColor() {
			return COLOR;
		}

		public boolean isDisabled() {
			return DISABLED;
		}

		public Identifier getIcon() {
			return ICON;
		}

		public int getIconWidth() {
			return ICON_WIDTH;
		}

		public int getIconHeight() {
			return ICON_HEIGHT;
		}

		public boolean isInPlayerDimension() {
			return IS_IN_PLAYER_DIMENSION;
		}
	}

	private enum IconsSize implements Translatable {
		SMALL("flex_hud.enum.compass.icons_size.small"),
		MEDIUM("flex_hud.enum.compass.icons_size.medium"),
		LARGE("flex_hud.enum.compass.icons_size.large");

		private final String translationKey;

		IconsSize(String translationKey) {
			this.translationKey = translationKey;
		}

		@Override
		public String getTranslationKey() {
			return translationKey;
		}
	}
}
