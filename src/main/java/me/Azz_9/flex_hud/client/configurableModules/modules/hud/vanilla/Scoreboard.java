package me.Azz_9.flex_hud.client.configurableModules.modules.hud.vanilla;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.client.mixin.scoreboard.GuiAccessor;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;

public class Scoreboard extends AbstractMovableModule {

	public final ConfigBoolean showScoreboard = new ConfigBoolean(true, "flex_hud.scoreboard.config.show_scoreboard");
	private final ConfigBoolean showScore = new ConfigBoolean(true, "flex_hud.scoreboard.config.show_score");
	private final ConfigBoolean drawBackground = new ConfigBoolean(true, "flex_hud.global.config.show_background");
	private final ConfigInteger backgroundColor = new ConfigInteger(0x000000, "flex_hud.global.config.background_color");
	private final ConfigBoolean shadow = new ConfigBoolean(false, "flex_hud.global.config.text_shadow");

	private Objective placeholderObjective;
	private static final int PADDING = 2;

	public Scoreboard(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
		this.enabled.setConfigTextTranslationKey("flex_hud.scoreboard.config.enable");

		ConfigRegistry.register(getID(), "showScoreboard", showScoreboard);
		ConfigRegistry.register(getID(), "showScore", showScore);
		ConfigRegistry.register(getID(), "drawBackground", drawBackground);
		ConfigRegistry.register(getID(), "backgroundColor", backgroundColor);
		ConfigRegistry.register(getID(), "shadow", shadow);
	}

	@Override
	public void init() {
		net.minecraft.world.scores.Scoreboard scoreboard = new net.minecraft.world.scores.Scoreboard();
		placeholderObjective = new Objective(
				scoreboard,
				"health",
				ObjectiveCriteria.HEALTH,
				Component.literal("Health"),
				ObjectiveCriteria.HEALTH.getDefaultRenderType(),
				false,
				null
		);
		scoreboard.getOrCreatePlayerScore(() -> "Player1", placeholderObjective);
		scoreboard.getOrCreatePlayerScore(() -> "Player2", placeholderObjective);
		scoreboard.getOrCreatePlayerScore(() -> "Player3", placeholderObjective);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.scoreboard");
	}

	@Override
	public String getID() {
		return "scoreboard";
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && (MINECRAFT.level == null || MINECRAFT.player == null)) {
			return;
		}

		Objective objective = null;
		if (Flex_hudClient.isInMoveElementScreen) {
			objective = placeholderObjective;
		} else {
			net.minecraft.world.scores.Scoreboard scoreboard = MINECRAFT.level.getScoreboard();
			PlayerTeam playerTeam = scoreboard.getPlayersTeam(MINECRAFT.player.getScoreboardName());
			if (playerTeam != null) {
				DisplaySlot displaySlot = DisplaySlot.teamColorToSlot(playerTeam.getColor());
				if (displaySlot != null) {
					objective = scoreboard.getDisplayObjective(displaySlot);
				}
			}

			objective = objective != null ? objective : scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
		}

		if (objective != null) {
			graphics.nextStratum();
			this.renderScoreboardSidebar(graphics, objective);
		}
	}

	private void renderScoreboardSidebar(GuiGraphics graphics, Objective objective) {
		net.minecraft.world.scores.Scoreboard scoreboard = objective.getScoreboard();
		NumberFormat objectiveScoreFormat = objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);

		@Environment(EnvType.CLIENT)
		record DisplayEntry(Component name, Component score, int scoreWidth) {
		}

		DisplayEntry[] entriesToDisplay = scoreboard.listPlayerScores(objective)
				.stream()
				.filter((input) -> !input.isHidden())
				.sorted(((GuiAccessor) MINECRAFT.gui).getScoreDisplayOrder())
				.limit(15L)
				.map((score) -> {
					PlayerTeam team = scoreboard.getPlayersTeam(score.owner());
					Component ownerName = score.ownerName();
					Component name = PlayerTeam.formatNameForTeam(team, ownerName);
					Component scoreString = Component.empty();
					int scoreWidth = 0;
					if (showScore.getValue()) {
						scoreString = score.formatValue(objectiveScoreFormat);
						scoreWidth = MINECRAFT.font.width(scoreString);
					}
					return new DisplayEntry(name, scoreString, scoreWidth);
				}).toArray(DisplayEntry[]::new);


		Component text = objective.getDisplayName();
		int textWidth = MINECRAFT.font.width(text);
		int width = textWidth;

		for (DisplayEntry displayEntry : entriesToDisplay) {
			width = Math.max(
					width, MINECRAFT.font.width(displayEntry.name) +
							(displayEntry.scoreWidth > 0
									? MINECRAFT.font.width(": ") + displayEntry.scoreWidth
									: 0)
			);
		}

		int contentHeight = entriesToDisplay.length * MINECRAFT.font.lineHeight;
		int contentBackground = ARGB.color(0.3f, backgroundColor.getValue());
		int titleBackground = ARGB.color(0.4f, backgroundColor.getValue());
		int height = 1 + MINECRAFT.font.lineHeight + contentHeight;

		setWidth(width + PADDING * 2);
		setHeight(height);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		if (drawBackground.getValue()) {
			graphics.fill(0, 0, getWidth(), MINECRAFT.font.lineHeight, titleBackground);
			graphics.fill(0, MINECRAFT.font.lineHeight, getWidth(), getHeight(), contentBackground);
		}
		graphics.drawString(MINECRAFT.font, text, (getWidth() - textWidth) / 2, 1, CommonColors.WHITE, shadow.getValue());

		for (int i = 0; i < entriesToDisplay.length; i++) {
			DisplayEntry displayEntry = entriesToDisplay[i];
			int y = getHeight() - (entriesToDisplay.length - i) * MINECRAFT.font.lineHeight;
			graphics.drawString(MINECRAFT.font, displayEntry.name, PADDING, y, CommonColors.WHITE, shadow.getValue());
			if (showScore.getValue()) {
				graphics.drawString(MINECRAFT.font, displayEntry.score, getWidth() - displayEntry.scoreWidth - PADDING, y, CommonColors.WHITE, shadow.getValue());
			}
		}

		matrices.popMatrix();
	}

	@Override
	public boolean shouldNotRender() {
		return super.shouldNotRender() || !this.showScoreboard.getValue();
	}

	@Override
	public Identifier getLayer() {
		return VanillaHudElements.SCOREBOARD;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 195;
				} else {
					buttonWidth = 170;
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
								.setVariable(showScoreboard)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.addDependency(this.getConfigList().getFirstEntry(), false)
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
								.setVariable(showScore)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
