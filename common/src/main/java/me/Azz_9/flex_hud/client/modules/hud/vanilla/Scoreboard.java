package me.Azz_9.flex_hud.client.modules.hud.vanilla;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.config.option.ConfigInteger;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.mixin.GuiAccessor;

public class Scoreboard extends AbstractMovableModule {

	public final @NotNull ConfigBoolean showScoreboard = new ConfigBoolean(true, "flex_hud.scoreboard.config.show_scoreboard");
	public final @NotNull ConfigBoolean showScore = new ConfigBoolean(true, "flex_hud.scoreboard.config.show_score");
	public final @NotNull ConfigBoolean drawBackground = new ConfigBoolean(true, "flex_hud.global.config.show_background");
	public final @NotNull ConfigInteger backgroundColor = new ConfigInteger(0x000000, "flex_hud.global.config.background_color");
	public final @NotNull ConfigBoolean shadow = new ConfigBoolean(false, "flex_hud.global.config.text_shadow");

	public static Objective placeholderObjective;

	public Scoreboard(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("scoreboard", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.scoreboard.config.enable");

		// show scoreboard in f3, same behavior as minecraft scoreboard
		this.hideInF3.setValue(false);
		this.hideInF3.setDefaultValue(false);

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
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		// render is handled in HudMixin
		if (MINECRAFT.level == null) {
			((GuiAccessor) MINECRAFT.gui).invokeDisplayScoreboardSidebar(graphics, placeholderObjective);
		}
	}

	@Override
	public boolean shouldRunSpeedTest() {
		return MINECRAFT.level == null;
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && showScoreboard.getValue();
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
