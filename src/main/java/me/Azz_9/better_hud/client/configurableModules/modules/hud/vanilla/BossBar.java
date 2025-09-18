package me.Azz_9.better_hud.client.configurableModules.modules.hud.vanilla;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.MovableModule;
import me.Azz_9.better_hud.client.mixin.bossBar.BossBarAccessor;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class BossBar extends AbstractHudElement implements MovableModule {
	private static final Identifier[] BACKGROUND_TEXTURES = new Identifier[]{
			Identifier.ofVanilla("boss_bar/pink_background"), Identifier.ofVanilla("boss_bar/blue_background"),
			Identifier.ofVanilla("boss_bar/red_background"), Identifier.ofVanilla("boss_bar/green_background"),
			Identifier.ofVanilla("boss_bar/yellow_background"), Identifier.ofVanilla("boss_bar/purple_background"),
			Identifier.ofVanilla("boss_bar/white_background")
	};
	private static final Identifier[] PROGRESS_TEXTURES = new Identifier[]{
			Identifier.ofVanilla("boss_bar/pink_progress"), Identifier.ofVanilla("boss_bar/blue_progress"),
			Identifier.ofVanilla("boss_bar/red_progress"), Identifier.ofVanilla("boss_bar/green_progress"),
			Identifier.ofVanilla("boss_bar/yellow_progress"), Identifier.ofVanilla("boss_bar/purple_progress"),
			Identifier.ofVanilla("boss_bar/white_progress")
	};
	private static final Identifier[] NOTCHED_BACKGROUND_TEXTURES = new Identifier[]{
			Identifier.ofVanilla("boss_bar/notched_6_background"), Identifier.ofVanilla("boss_bar/notched_10_background"),
			Identifier.ofVanilla("boss_bar/notched_12_background"), Identifier.ofVanilla("boss_bar/notched_20_background")
	};
	private static final Identifier[] NOTCHED_PROGRESS_TEXTURES = new Identifier[]{
			Identifier.ofVanilla("boss_bar/notched_6_progress"), Identifier.ofVanilla("boss_bar/notched_10_progress"),
			Identifier.ofVanilla("boss_bar/notched_12_progress"), Identifier.ofVanilla("boss_bar/notched_20_progress")
	};

	private transient Map<UUID, ClientBossBar> bossBars = new LinkedHashMap<>();
	private final transient int BOSS_BAR_GAP = 10;

	public final ConfigBoolean showBossBar = new ConfigBoolean(true, "better_hud.bossbar.config.show_bossbar");

	public BossBar(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
	}

	public void init() {
		this.width = 182;
		this.height = (BOSS_BAR_GAP + MinecraftClient.getInstance().textRenderer.fontHeight);

		this.enabled.setConfigTextTranslationKey("better_hud.bossbar.config.enable");
	}

	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		
		if (shouldNotRender()) {
			return;
		}

		if (Better_hudClient.isInMoveElementScreen) {
			Matrix3x2fStack matrices = context.getMatrices();
			matrices.pushMatrix();
			matrices.translate(getRoundedX(), getRoundedY());
			matrices.scale(scale, scale);

			int bossBarWidth = width;
			int bossBarHeight = 5;

			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURES[0], bossBarWidth, bossBarHeight, 0, 0, 0, 9, bossBarWidth, bossBarHeight);
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PROGRESS_TEXTURES[0], bossBarWidth, bossBarHeight, 0, 0, 0, 9, bossBarWidth, bossBarHeight);

			Text text = Text.of("Boss bar");
			int textX = (bossBarWidth - client.textRenderer.getWidth(text)) / 2;
			int textY = 0;
			context.drawTextWithShadow(client.textRenderer, text, textX, textY, 0xffffffff);

			matrices.popMatrix();
			return;
		}

		this.updateBossBars();
		if (this.bossBars.isEmpty()) {
			return;
		}

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(scale, scale);

		int bossBarWidth = width;
		int bossBarHeight = 5;

		int maxBossBars = (context.getScaledWindowHeight() / 3) / (BOSS_BAR_GAP + client.textRenderer.fontHeight);

		int y = 9;
		int counter = 1;
		for (ClientBossBar clientBossBar : this.bossBars.values()) {
			if (counter > maxBossBars) {
				break;
			}
			this.renderBossBar(context, 0, y, bossBarWidth, bossBarHeight, clientBossBar);

			Text text = clientBossBar.getName();
			int textX = (bossBarWidth - client.textRenderer.getWidth(text)) / 2;
			int textY = y - client.textRenderer.fontHeight;
			context.drawTextWithShadow(client.textRenderer, text, textX, textY, 0xffffffff);
			y += BOSS_BAR_GAP + client.textRenderer.fontHeight;
			counter += 1;
		}

		matrices.popMatrix();
	}

	private void renderBossBar(DrawContext context, int x, int y, int width, int height, net.minecraft.entity.boss.BossBar bossBar) {
		this.renderBossBar(context, x, y, bossBar, width, height, width, BACKGROUND_TEXTURES, NOTCHED_BACKGROUND_TEXTURES);
		int progressWidth = MathHelper.lerpPositive(bossBar.getPercent(), 0, width);
		if (progressWidth > 0) {
			this.renderBossBar(context, x, y, bossBar, width, height, progressWidth, PROGRESS_TEXTURES, NOTCHED_PROGRESS_TEXTURES);
		}

	}

	private void renderBossBar(DrawContext context, int x, int y, net.minecraft.entity.boss.BossBar bossBar, int width, int height, int progressWidth, Identifier[] textures, Identifier[] notchedTextures) {
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, textures[bossBar.getColor().ordinal()], width, height, 0, 0, x, y, progressWidth, height);
		if (bossBar.getStyle() != net.minecraft.entity.boss.BossBar.Style.PROGRESS) {
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, notchedTextures[bossBar.getStyle().ordinal() - 1], width, height, 0, 0, x, y, progressWidth, height);
		}

	}

	private void updateBossBars() {
		bossBars = ((BossBarAccessor) MinecraftClient.getInstance().inGameHud.getBossBarHud()).getBossBars();
	}

	@Override
	protected boolean shouldNotRender() {
		return super.shouldNotRender() || !this.showBossBar.getValue();
	}

	@Override
	public String getID() {
		return "boss_bar";
	}

	@Override
	public Text getName() {
		return Text.translatable("better_hud.bossbar");
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {

				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 220;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showBossBar)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.build()
				);
			}
		};
	}
}
