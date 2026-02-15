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
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;

public class Scoreboard extends AbstractMovableModule {

	public final ConfigBoolean showScoreboard = new ConfigBoolean(true, "flex_hud.scoreboard.config.show_scoreboard");
	private final ConfigBoolean showScore = new ConfigBoolean(true, "flex_hud.scoreboard.config.show_score");

	private final Objective PLACEHOLDER_OBJECTIVE;

	public Scoreboard(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
		this.enabled.setConfigTextTranslationKey("flex_hud.scoreboard.config.enable");

		ConfigRegistry.register(getID(), "showScoreboard", showScoreboard);
		ConfigRegistry.register(getID(), "showScore", showScore);

		net.minecraft.world.scores.Scoreboard scoreboard = new net.minecraft.world.scores.Scoreboard();
		PLACEHOLDER_OBJECTIVE = new Objective(
				scoreboard,
				"health",
				ObjectiveCriteria.HEALTH,
				Component.literal("Health"),
				ObjectiveCriteria.HEALTH.getDefaultRenderType(),
				false,
				null
		);
		scoreboard.getOrCreatePlayerScore(() -> "Player1", PLACEHOLDER_OBJECTIVE);
		scoreboard.getOrCreatePlayerScore(() -> "Player2", PLACEHOLDER_OBJECTIVE);
		scoreboard.getOrCreatePlayerScore(() -> "Player3", PLACEHOLDER_OBJECTIVE);
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
			objective = PLACEHOLDER_OBJECTIVE;
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
		int contentBackground = MINECRAFT.options.getBackgroundColor(0.3F);
		int titleBackground = MINECRAFT.options.getBackgroundColor(0.4F);
		int height = 1 + MINECRAFT.font.lineHeight + contentHeight;

		setWidth(width);
		setHeight(height);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		graphics.fill(0, 0, getWidth(), MINECRAFT.font.lineHeight, titleBackground);
		graphics.fill(0, MINECRAFT.font.lineHeight, getWidth(), getHeight(), contentBackground);
		graphics.drawString(MINECRAFT.font, text, (getWidth() - textWidth) / 2, 1, CommonColors.WHITE, false);

		for (int i = 0; i < entriesToDisplay.length; i++) {
			DisplayEntry displayEntry = entriesToDisplay[i];
			int y = getHeight() - (entriesToDisplay.length - i) * MINECRAFT.font.lineHeight;
			graphics.drawString(MINECRAFT.font, displayEntry.name, 0, y, CommonColors.WHITE, false);
			if (showScore.getValue()) {
				graphics.drawString(MINECRAFT.font, displayEntry.score, getWidth() - displayEntry.scoreWidth, y, CommonColors.WHITE, false);
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
