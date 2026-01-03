package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Coordinates extends AbstractTextModule {
	private final ConfigBoolean showY = new ConfigBoolean(true, "flex_hud.coordinates.config.show_y");
	private final ConfigInteger numberOfDigits = new ConfigInteger(0, "flex_hud.coordinates.config.number_of_digits", 0, 14);
	private final ConfigBoolean showDirection = new ConfigBoolean(true, "flex_hud.coordinates.config.show_direction");
	private final ConfigBoolean directionAbreviation = new ConfigBoolean(true, "flex_hud.coordinates.config.direction_abbreviation");
	private final ConfigEnum<DisplayMode> displayMode = new ConfigEnum<>(DisplayMode.class, DisplayMode.VERTICAL, "flex_hud.coordinates.config.orientation");

	public Coordinates(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.coordinates.config.enable");

		ConfigRegistry.register(getID(), "showY", showY);
		ConfigRegistry.register(getID(), "numberOfDigits", numberOfDigits);
		ConfigRegistry.register(getID(), "showDirection", showDirection);
		ConfigRegistry.register(getID(), "directionAbreviation", directionAbreviation);
		ConfigRegistry.register(getID(), "displayMode", displayMode);
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

		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && client.player == null) {
			return;
		}

		PlayerEntity player = client.player;

		List<Renderable> renderables = new ArrayList<>();

		// reset height and width
		setHeight(0);
		setWidth(0);

		double playerX;
		double playerY;
		double playerZ;
		if (Flex_hudClient.isInMoveElementScreen) {
			playerX = 12;
			playerY = 73;
			playerZ = -48;
		} else {
			playerX = player.getX();
			playerY = player.getY();
			playerZ = player.getZ();
		}

		// Get the truncated coordinates with the correct number of digits
		String xCoords = "X: " + BigDecimal.valueOf(playerX).setScale(this.numberOfDigits.getValue(), RoundingMode.FLOOR);
		String yCoords = "Y: " + BigDecimal.valueOf(playerY).setScale(this.numberOfDigits.getValue(), RoundingMode.FLOOR);
		String zCoords = "Z: " + BigDecimal.valueOf(playerZ).setScale(this.numberOfDigits.getValue(), RoundingMode.FLOOR);

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

			setHeight(hudY + 10);

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
			setHeight(client.textRenderer.fontHeight);
		}

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1.0f);

		drawBackground(context);

		for (Renderable renderable : renderables) {
			renderable.render(context, tickCounter);
		}

		matrices.pop();
	}

	private String[] getDirection(PlayerEntity p) {
		float yaw;
		if (Flex_hudClient.isInMoveElementScreen) {
			yaw = 45;
		} else {
			yaw = (p.getYaw() % 360 + 360) % 360;
		}

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
								.setVariable(showY)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showDirection)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(directionAbreviation)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new IntFieldEntry.Builder()
								.setIntFieldWidth(20)
								.setVariable(numberOfDigits)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new CyclingButtonEntry.Builder<DisplayMode>()
								.setCyclingButtonWidth(80)
								.setVariable(displayMode)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
