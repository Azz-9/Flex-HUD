package me.Azz_9.better_hud.client.configurableModules.modules.hud.vanilla;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableModules.modules.AbstractModule;
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
import org.joml.Matrix3x2fStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class BossBar extends AbstractModule implements MovableModule {
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
	public transient int width, height;
	private final transient int BOSS_BAR_GAP = 10;

	public double offsetX, offsetY;
	public AbstractHudElement.AnchorPosition anchorX, anchorY;
	public float scale = 1.0f;
	public ConfigBoolean hideInF3 = new ConfigBoolean(true, "better_hud.global.config.hide_in_f3");

	public BossBar(double offsetX, double offsetY, AbstractHudElement.AnchorPosition anchorX, AbstractHudElement.AnchorPosition anchorY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.anchorX = anchorX;
		this.anchorY = anchorY;
	}

	public void init() {
		this.width = 182;
		int textAndBarHeight = (BOSS_BAR_GAP + MinecraftClient.getInstance().textRenderer.fontHeight);
		this.height = (int) ((MinecraftClient.getInstance().getWindow().getScaledHeight() / 5.0) / textAndBarHeight) * textAndBarHeight;

		this.enabled.setConfigTextTranslationKey("better_hud.bossbar.config.enable");
	}

	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

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

		if (shouldNotRender()) {
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

	private boolean shouldNotRender() {
		return !JsonConfigHelper.getInstance().isEnabled || !this.enabled.getValue() || (this.hideInF3.getValue() && MinecraftClient.getInstance().getDebugHud().shouldShowDebugHud());
	}

	private void renderBossBar(DrawContext context, int x, int y, int width, int height, net.minecraft.entity.boss.BossBar bossBar) {
		this.renderBossBar(context, x, y, bossBar, width, height, BACKGROUND_TEXTURES, NOTCHED_BACKGROUND_TEXTURES);
		int progressWidth = MathHelper.lerpPositive(bossBar.getPercent(), 0, width);
		if (progressWidth > 0) {
			this.renderBossBar(context, x, y, bossBar, progressWidth, height, PROGRESS_TEXTURES, NOTCHED_PROGRESS_TEXTURES);
		}

	}

	private void renderBossBar(DrawContext context, int x, int y, net.minecraft.entity.boss.BossBar bossBar, int width, int height, Identifier[] textures, Identifier[] notchedTextures) {
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, textures[bossBar.getColor().ordinal()], width, height, 0, 0, x, y, width, height);
		if (bossBar.getStyle() != net.minecraft.entity.boss.BossBar.Style.PROGRESS) {
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, notchedTextures[bossBar.getStyle().ordinal() - 1], width, height, 0, 0, x, y, width, height);
		}

	}

	private void updateBossBars() {
		bossBars = ((BossBarAccessor) MinecraftClient.getInstance().inGameHud.getBossBarHud()).getBossBars();
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
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public double getOffsetX() {
		return offsetX;
	}

	@Override
	public double getOffsetY() {
		return offsetY;
	}

	@Override
	public AbstractHudElement.AnchorPosition getAnchorX() {
		return anchorX;
	}

	@Override
	public AbstractHudElement.AnchorPosition getAnchorY() {
		return anchorY;
	}

	@Override
	public void setPos(double offsetX, double offsetY, AbstractHudElement.AnchorPosition anchorX, AbstractHudElement.AnchorPosition anchorY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.anchorX = anchorX;
		this.anchorY = anchorY;
	}

	@Override
	public float getScale() {
		return scale;
	}

	@Override
	public void setScale(float scale) {
		/*int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
		int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

		float newX = getXWithScale(scale);
		float newY = getYWithScale(scale);
		float newScale = scale;

		if (newX < 0 || newX + getWidth() + scale > screenWidth || newY < 0 || newY + getHeight() + scale > screenHeight) {
			newScale = Math.min(scale, computeMaxScale());
			System.out.println(newScale);
		}*/

		this.scale = scale;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled.setValue(enabled);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				super.init();

				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 160;
				}

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
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
