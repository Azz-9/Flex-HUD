package me.Azz_9.flex_hud.client.modules.hud.vanilla;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.mixin.BossHealthOverlayAccessor;
import me.Azz_9.flex_hud.platform.Services;

public class BossBar extends AbstractMovableModule {
	private static final Identifier[] BACKGROUND_TEXTURES = new Identifier[]{
			Identifier.withDefaultNamespace("boss_bar/pink_background"), Identifier.withDefaultNamespace("boss_bar/blue_background"),
			Identifier.withDefaultNamespace("boss_bar/red_background"), Identifier.withDefaultNamespace("boss_bar/green_background"),
			Identifier.withDefaultNamespace("boss_bar/yellow_background"), Identifier.withDefaultNamespace("boss_bar/purple_background"),
			Identifier.withDefaultNamespace("boss_bar/white_background")
	};
	private static final Identifier[] PROGRESS_TEXTURES = new Identifier[]{
			Identifier.withDefaultNamespace("boss_bar/pink_progress"), Identifier.withDefaultNamespace("boss_bar/blue_progress"),
			Identifier.withDefaultNamespace("boss_bar/red_progress"), Identifier.withDefaultNamespace("boss_bar/green_progress"),
			Identifier.withDefaultNamespace("boss_bar/yellow_progress"), Identifier.withDefaultNamespace("boss_bar/purple_progress"),
			Identifier.withDefaultNamespace("boss_bar/white_progress")
	};
	private static final Identifier[] NOTCHED_BACKGROUND_TEXTURES = new Identifier[]{
			Identifier.withDefaultNamespace("boss_bar/notched_6_background"), Identifier.withDefaultNamespace("boss_bar/notched_10_background"),
			Identifier.withDefaultNamespace("boss_bar/notched_12_background"), Identifier.withDefaultNamespace("boss_bar/notched_20_background")
	};
	private static final Identifier[] NOTCHED_PROGRESS_TEXTURES = new Identifier[]{
			Identifier.withDefaultNamespace("boss_bar/notched_6_progress"), Identifier.withDefaultNamespace("boss_bar/notched_10_progress"),
			Identifier.withDefaultNamespace("boss_bar/notched_12_progress"), Identifier.withDefaultNamespace("boss_bar/notched_20_progress")
	};

	@NotNull
	private Map<UUID, LerpingBossEvent> events = new LinkedHashMap<>();
	private final int BOSS_BAR_GAP = 10;

	public final ConfigBoolean showBossBar = new ConfigBoolean(true, "flex_hud.bossbar.config.show_bossbar");

	public BossBar(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("boss_bar", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.bossbar.config.enable");

		// show boss bar even in f3, same behavior as minecraft boss bar
		this.hideInF3.setValue(false);
		this.hideInF3.setDefaultValue(false);

		ConfigRegistry.register(getID(), "showBossBar", showBossBar);
	}

	public void init() {
		setWidth(182);
		setHeight(BOSS_BAR_GAP + MINECRAFT.font.lineHeight);
	}

	public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender()) {
			return;
		}

		if (CommonClass.isEditingLayout) {
			Matrix3x2fStack matrices = graphics.pose();
			matrices.pushMatrix();
			matrices.translate(getRoundedX(), getRoundedY());
			matrices.scale(getScale());

			int bossBarWidth = getWidth();
			int bossBarHeight = 5;

			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURES[0], bossBarWidth, bossBarHeight, 0, 0, 0, 9, bossBarWidth, bossBarHeight);
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_TEXTURES[0], bossBarWidth, bossBarHeight, 0, 0, 0, 9, bossBarWidth, bossBarHeight);

			Component text = Component.literal("Boss bar");
			int textX = (bossBarWidth - MINECRAFT.font.width(text)) / 2;
			int textY = 0;
			graphics.text(MINECRAFT.font, text, textX, textY, 0xffffffff);

			matrices.popMatrix();
			return;
		}

		this.updateBossBars();
		if (this.events.isEmpty()) {
			return;
		}

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		int bossBarWidth = getWidth();
		int bossBarHeight = 5;

		int maxBossBars = (graphics.guiHeight() / 3) / (BOSS_BAR_GAP + MINECRAFT.font.lineHeight);

		int y = 9;
		int counter = 1;
		for (LerpingBossEvent clientBossBar : this.events.values()) {
			if (counter > maxBossBars) {
				break;
			}
			this.renderBossBar(graphics, 0, y, bossBarWidth, bossBarHeight, clientBossBar);

			Component text = clientBossBar.getName();
			int textX = (bossBarWidth - MINECRAFT.font.width(text)) / 2;
			int textY = y - MINECRAFT.font.lineHeight;
			graphics.text(MINECRAFT.font, text, textX, textY, Colors.WHITE);
			y += BOSS_BAR_GAP + MINECRAFT.font.lineHeight;
			counter += 1;
		}

		matrices.popMatrix();
	}

	private void renderBossBar(GuiGraphicsExtractor graphics, int x, int y, int width, int height, BossEvent event) {
		this.renderBossBar(graphics, x, y, event, width, height, width, BACKGROUND_TEXTURES, NOTCHED_BACKGROUND_TEXTURES);
		int progressWidth = Mth.lerpDiscrete(event.getProgress(), 0, width);
		if (progressWidth > 0) {
			this.renderBossBar(graphics, x, y, event, width, height, progressWidth, PROGRESS_TEXTURES, NOTCHED_PROGRESS_TEXTURES);
		}

	}

	private void renderBossBar(GuiGraphicsExtractor graphics, int x, int y, BossEvent event, int width, int height, int progressWidth, Identifier[] textures, Identifier[] notchedTextures) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, textures[event.getColor().ordinal()], width, height, 0, 0, x, y, progressWidth, height);
		if (event.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, notchedTextures[event.getOverlay().ordinal() - 1], width, height, 0, 0, x, y, progressWidth, height);
		}

	}

	private void updateBossBars() {
		events = ((BossHealthOverlayAccessor) MINECRAFT.gui.hud.getBossOverlay()).getBossBars();
	}

	@Override
	public boolean shouldNotRender() {
		return super.shouldNotRender() || !this.showBossBar.getValue();
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.bossbar");
	}

	@Override
	public Identifier getLayer() {
		return Services.PLATFORM.getBossBarIdentifier();
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
