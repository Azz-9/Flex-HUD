package me.Azz_9.flex_hud.client.configurableModules.modules.hud.vanilla;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.client.mixin.scoreboard.InGameHudAccessor;
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

	private final ScoreboardObjective PLACEHOLDER_SCOREBOARD_OBJECTIVE;
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

		net.minecraft.scoreboard.Scoreboard scoreboard = new net.minecraft.scoreboard.Scoreboard();
		PLACEHOLDER_SCOREBOARD_OBJECTIVE = new ScoreboardObjective(
				scoreboard,
				"health",
				ScoreboardCriterion.HEALTH,
				Text.of("Health"),
				ScoreboardCriterion.HEALTH.getDefaultRenderType(),
				false,
				null
		);
	}

	@Override
	public void init() {
		net.minecraft.scoreboard.Scoreboard scoreboard = PLACEHOLDER_SCOREBOARD_OBJECTIVE.getScoreboard();
		scoreboard.getOrCreateScore(() -> "Player1", PLACEHOLDER_SCOREBOARD_OBJECTIVE);
		scoreboard.getOrCreateScore(() -> "Player2", PLACEHOLDER_SCOREBOARD_OBJECTIVE);
		scoreboard.getOrCreateScore(() -> "Player3", PLACEHOLDER_SCOREBOARD_OBJECTIVE);
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.scoreboard");
	}

	@Override
	public String getID() {
		return "scoreboard";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && (CLIENT.world == null || CLIENT.player == null)) {
			return;
		}

		ScoreboardObjective scoreboardObjective = null;
		if (Flex_hudClient.isInMoveElementScreen) {
			scoreboardObjective = PLACEHOLDER_SCOREBOARD_OBJECTIVE;
		} else {
			net.minecraft.scoreboard.Scoreboard scoreboard = CLIENT.world.getScoreboard();
			Team team = scoreboard.getScoreHolderTeam(CLIENT.player.getNameForScoreboard());
			if (team != null) {
				ScoreboardDisplaySlot scoreboardDisplaySlot = ScoreboardDisplaySlot.fromFormatting(team.getColor());
				if (scoreboardDisplaySlot != null) {
					scoreboardObjective = scoreboard.getObjectiveForSlot(scoreboardDisplaySlot);
				}
			}

			scoreboardObjective = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
		}

		if (scoreboardObjective != null) {
			this.renderScoreboardSidebar(context, scoreboardObjective);
		}
	}

	private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective) {
		net.minecraft.scoreboard.Scoreboard scoreboard = objective.getScoreboard();
		NumberFormat numberFormat = objective.getNumberFormatOr(StyledNumberFormat.RED);

		@Environment(EnvType.CLIENT)
		record SidebarEntry(Text name, Text score, int scoreWidth) {
		}

		SidebarEntry[] sidebarEntrys = scoreboard.getScoreboardEntries(objective)
				.stream()
				.filter(score -> !score.hidden())
				.sorted(((InGameHudAccessor) CLIENT.inGameHud).getScoreboardEntryComparator())
				.limit(15L)
				.map(scoreboardEntry -> {
					Team team = scoreboard.getScoreHolderTeam(scoreboardEntry.owner());
					Text name = scoreboardEntry.name();
					name = Team.decorateName(team, name);
					Text scoreText = Text.empty();
					int scoreWidth = 0;
					if (showScore.getValue()) {
						scoreText = scoreboardEntry.formatted(numberFormat);
						scoreWidth = CLIENT.textRenderer.getWidth(scoreText);
					}
					return new SidebarEntry(name, scoreText, scoreWidth);
				}).toArray(SidebarEntry[]::new);
		Text text = objective.getDisplayName();
		int textWidth = CLIENT.textRenderer.getWidth(text);
		int width = textWidth;

		for (SidebarEntry sidebarEntry : sidebarEntrys) {
			width = Math.max(
					width, CLIENT.textRenderer.getWidth(sidebarEntry.name) +
							(sidebarEntry.scoreWidth > 0
									? CLIENT.textRenderer.getWidth(": ") + sidebarEntry.scoreWidth
									: 0)
			);
		}

		int contentHeight = sidebarEntrys.length * CLIENT.textRenderer.fontHeight;
		int contentBackground = ColorHelper.withAlpha(77, backgroundColor.getValue());
		int titleBackground = ColorHelper.withAlpha(102, backgroundColor.getValue());
		int height = 1 + CLIENT.textRenderer.fontHeight + contentHeight;

		setWidth(width + PADDING * 2);
		setHeight(height);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1.0f);

		if (drawBackground.getValue()) {
			context.fill(0, 0, getWidth(), CLIENT.textRenderer.fontHeight, titleBackground);
			context.fill(0, CLIENT.textRenderer.fontHeight, getWidth(), getHeight(), contentBackground);
		}
		context.drawText(CLIENT.textRenderer, text, (getWidth() - textWidth) / 2, 1, Colors.WHITE, shadow.getValue());

		for (int i = 0; i < sidebarEntrys.length; i++) {
			SidebarEntry sidebarEntry = sidebarEntrys[i];
			int y = getHeight() - (sidebarEntrys.length - i) * CLIENT.textRenderer.fontHeight;
			context.drawText(CLIENT.textRenderer, sidebarEntry.name, PADDING, y, Colors.WHITE, shadow.getValue());
			if (showScore.getValue()) {
				context.drawText(CLIENT.textRenderer, sidebarEntry.score, getWidth() - sidebarEntry.scoreWidth - PADDING, y, Colors.WHITE, shadow.getValue());
			}
		}

		matrices.pop();
	}

	@Override
	public boolean shouldNotRender() {
		return super.shouldNotRender() || !this.showScoreboard.getValue();
	}

	@Override
	public Identifier getLayer() {
		return IdentifiedLayer.SCOREBOARD;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (CLIENT.getLanguageManager().getLanguage().equals("fr_fr")) {
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
