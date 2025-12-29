package me.Azz_9.flex_hud.client.configurableModules.modules.hud.vanilla;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.HudElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigFloat;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigIntGrid;
import me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.AbstractCrosshairConfigScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.CrosshairEditorEntry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

public class Crosshair extends AbstractModule implements HudElement {

	private static final RenderPipeline CROSSHAIR_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation("pipeline/crosshair_no_tex")
			.withBlend(new BlendFunction(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO))
			.build()
	);

	private static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_full");
	private static final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_background");
	private static final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_progress");

	public int size = 15;
	public final ConfigFloat scale = new ConfigFloat(1.0f);

	public final ConfigIntGrid pixels = new ConfigIntGrid(
			new int[][]{
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			}, "flex_hud.crosshair.config.custom_texture");

	public final ConfigBoolean disableBlending = new ConfigBoolean(false, "flex_hud.crosshair.config.disable_blending");

	public Crosshair() {
		this.enabled.setConfigTextTranslationKey("flex_hud.crosshair.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);

		ConfigRegistry.register(getID(), "scale", scale);
		ConfigRegistry.register(getID(), "pixels", pixels);
		ConfigRegistry.register(getID(), "disableBlending", disableBlending);
	}

	@Override
	public String getID() {
		return "crosshair";
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.crosshair");
	}

	@Override
	public Identifier getLayer() {
		return VanillaHudElements.CROSSHAIR;
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();

		if (shouldNotRender() || minecraft.player == null ||
				!minecraft.options.getCameraType().isFirstPerson() || minecraft.gameMode == null ||
				(minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR && !this.canRenderCrosshairForSpectator(minecraft.hitResult)) ||
				this.shouldNotRenderCrosshair()) {
			return;
		}

		int screenWidth = graphics.guiWidth();
		int screenHeight = graphics.guiHeight();
		double startX = screenWidth / 2.0 - (size / 2.0) * scale.getValue();
		double startY = screenHeight / 2.0 - (size / 2.0) * scale.getValue();

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate((float) startX, (float) startY);
		matrices.scale(scale.getValue());

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (pixels.getValue()[y][x] >> 24 != 0) {
					if (disableBlending.getValue()) {
						graphics.fill(x, y, x + 1, y + 1, pixels.getValue()[y][x]);
					} else {
						graphics.fill(CROSSHAIR_PIPELINE, x, y, x + 1, y + 1, pixels.getValue()[y][x]);
					}
				}
			}
		}

		matrices.popMatrix();

		if (minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
			float attackCooldownProgress = minecraft.player.getAttackStrengthScale(0.0F);
			boolean renderMaxAttackIndicator = false;
			if (minecraft.crosshairPickEntity instanceof LivingEntity && attackCooldownProgress >= 1.0F && minecraft.hitResult != null) {
				renderMaxAttackIndicator = minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
				renderMaxAttackIndicator &= minecraft.crosshairPickEntity.isAlive();
				AttackRange attackRange = minecraft.player.getActiveItem().get(DataComponents.ATTACK_RANGE);
				renderMaxAttackIndicator &= attackRange == null || attackRange.isInRange(minecraft.player, minecraft.hitResult.getLocation());
			}

			int y = graphics.guiHeight() / 2 - 7 + 16;
			int x = graphics.guiWidth() / 2 - 8;
			if (renderMaxAttackIndicator) {
				graphics.blitSprite((disableBlending.getValue() ? RenderPipelines.GUI_TEXTURED : RenderPipelines.CROSSHAIR), CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, x, y, 16, 16);
			} else if (attackCooldownProgress < 1.0F) {
				int progress = (int) (attackCooldownProgress * 17.0F);
				graphics.blitSprite((disableBlending.getValue() ? RenderPipelines.GUI_TEXTURED : RenderPipelines.CROSSHAIR), CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, x, y, 16, 4);
				graphics.blitSprite((disableBlending.getValue() ? RenderPipelines.GUI_TEXTURED : RenderPipelines.CROSSHAIR), CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, x, y, progress, 4);
			}
		}
	}

	public boolean shouldNotRenderCrosshair() {
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft.getDebugOverlay().showDebugScreen() && minecraft.options.getCameraType() == CameraType.FIRST_PERSON && minecraft.player != null && !minecraft.player.isReducedDebugInfo() && !(Boolean) minecraft.options.reducedDebugInfo().get();
	}

	private boolean canRenderCrosshairForSpectator(@Nullable HitResult hitResult) {
		Minecraft minecraft = Minecraft.getInstance();

		if (hitResult == null || minecraft.level == null) {
			return false;
		} else if (hitResult.getType() == HitResult.Type.ENTITY) {
			return ((EntityHitResult) hitResult).getEntity() instanceof MenuProvider;
		} else if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
			Level level = minecraft.level;
			return level.getBlockState(pos).getMenuProvider(level, pos) != null;
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldNotRender() {
		return !ModulesHelper.getInstance().isEnabled.getValue() || !this.enabled.getValue();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractCrosshairConfigScreen(getName(), parent) {
			@Override
			protected void init() {
				if (Minecraft.getInstance().getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 165;
				} else {
					buttonWidth = 155;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build()
				);
				this.addAllEntries(
						new CrosshairEditorEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(pixels)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(disableBlending)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
