package me.Azz_9.flex_hud.client.modules.hud.vanilla;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.config.option.ConfigInteger;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.mixin.HudAccessor;
import me.Azz_9.flex_hud.platform.Services;

public class Scoreboard extends AbstractMovableModule {

	public final ConfigBoolean showScoreboard = new ConfigBoolean(true, "flex_hud.scoreboard.config.show_scoreboard");
	private final ConfigBoolean showScore = new ConfigBoolean(true, "flex_hud.scoreboard.config.show_score");
	private final ConfigBoolean drawBackground = new ConfigBoolean(true, "flex_hud.global.config.show_background");
	private final ConfigInteger backgroundColor = new ConfigInteger(0x000000, "flex_hud.global.config.background_color");
	private final ConfigBoolean shadow = new ConfigBoolean(false, "flex_hud.global.config.text_shadow");

	private Objective placeholderObjective;
	private static final int PADDING = 2;

	public Scoreboard(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("scoreboard", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
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
	public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender() || !CommonClass.isEditingLayout && (MINECRAFT.level == null || MINECRAFT.player == null)) {
			return;
		}

		Objective objective = null;
		if (CommonClass.isEditingLayout) {
			objective = placeholderObjective;
		} else {
			net.minecraft.world.scores.Scoreboard scoreboard = MINECRAFT.level.getScoreboard();
			PlayerTeam playerTeam = scoreboard.getPlayersTeam(MINECRAFT.player.getScoreboardName());
			if (playerTeam != null && playerTeam.getColor().isPresent()) {
				objective = scoreboard.getDisplayObjective(playerTeam.getColor().get().displaySlot());
			}

			objective = objective != null ? objective : scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
		}

		if (objective != null) {
			graphics.nextStratum();
			this.renderScoreboardSidebar(graphics, objective);
		}
	}

	private void renderScoreboardSidebar(GuiGraphicsExtractor graphics, Objective objective) {
		net.minecraft.world.scores.Scoreboard scoreboard = objective.getScoreboard();
		NumberFormat objectiveScoreFormat = objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);

		record DisplayEntry(Component name, Component score, int scoreWidth) {
		}

		DisplayEntry[] entriesToDisplay = scoreboard.listPlayerScores(objective)
				.stream()
				.filter((input) -> !input.isHidden())
				.sorted(((HudAccessor) MINECRAFT.gui.hud).getScoreDisplayOrder())
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
		graphics.text(MINECRAFT.font, text, (getWidth() - textWidth) / 2, 1, CommonColors.WHITE, shadow.getValue());

		for (int i = 0; i < entriesToDisplay.length; i++) {
			DisplayEntry displayEntry = entriesToDisplay[i];
			int y = getHeight() - (entriesToDisplay.length - i) * MINECRAFT.font.lineHeight;
			graphics.text(MINECRAFT.font, displayEntry.name, PADDING, y, CommonColors.WHITE, shadow.getValue());
			if (showScore.getValue()) {
				graphics.text(MINECRAFT.font, displayEntry.score, getWidth() - displayEntry.scoreWidth - PADDING, y, CommonColors.WHITE, shadow.getValue());
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
		return Services.PLATFORM.getScoreboardIdentifier();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 195;
				} else {
					buttonWidth = 170;
				}

				super.initContent();

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
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeX)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeX(anchorModeX.getValue()))
								.build(),
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeY)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeY(anchorModeY.getValue()))
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
