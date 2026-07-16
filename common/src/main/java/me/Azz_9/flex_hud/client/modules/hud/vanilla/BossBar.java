package me.Azz_9.flex_hud.client.modules.hud.vanilla;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.mixin.BossHealthOverlayAccessor;
import me.Azz_9.flex_hud.mixin.HudAccessor;

public class BossBar extends AbstractMovableModule {
	private static final int BOSS_BAR_GAP = 10;

	public final @NotNull ConfigBoolean showBossBar = new ConfigBoolean(true, "flex_hud.bossbar.config.show_bossbar");

	public static @Nullable LerpingBossEvent placeholderEvent;

	public BossBar(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("boss_bar", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.bossbar.config.enable");

		// show boss bar in f3, same behavior as minecraft boss bar
		this.hideInF3.setValue(false);
		this.hideInF3.setDefaultValue(false);

		ConfigRegistry.register(getID(), "showBossBar", showBossBar);
	}

	public void init() {
		placeholderEvent = new LerpingBossEvent(
				UUID.nameUUIDFromBytes("flex_hud:bossbar_placeholder".getBytes(StandardCharsets.UTF_8)),
				Modules.getInstance().bossBar.getName(),
				1.0f,
				BossEvent.BossBarColor.PURPLE,
				BossEvent.BossBarOverlay.PROGRESS,
				false,
				false,
				false
		);

		setWidth(BossHealthOverlayAccessor.getBarWidth());
		setHeight(BOSS_BAR_GAP + MINECRAFT.font.lineHeight);
	}

	public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		// render is handled in BossHealthOverlayMixin
		if (MINECRAFT.level == null) {
			((HudAccessor) MINECRAFT.gui.hud).invokeExtractBossOverlay(graphics, deltaTracker);
		}
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && showBossBar.getValue();
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.bossbar");
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {

				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 220;
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
								.setVariable(showBossBar)
								.addDependency(this.getConfigList().getFirstEntry(), false)
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
								.build()
				);
			}
		};
	}
}
