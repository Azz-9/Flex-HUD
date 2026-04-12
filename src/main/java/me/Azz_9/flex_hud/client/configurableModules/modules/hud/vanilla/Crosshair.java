package me.Azz_9.flex_hud.client.configurableModules.modules.hud.vanilla;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

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

public class Crosshair extends AbstractModule implements HudElement {

	private static final RenderPipeline CROSSHAIR_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation("pipeline/crosshair_no_tex")
			.withBlend(new BlendFunction(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO))
			.build()
	);

	private static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_full");
	private static final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_background");
	private static final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_progress");

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

		ConfigRegistry.register(getID(), "scale", scale);
		ConfigRegistry.register(getID(), "pixels", pixels);
		ConfigRegistry.register(getID(), "disableBlending", disableBlending);
	}

	@Override
	public String getID() {
		return "crosshair";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.crosshair");
	}

	@Override
	public Identifier getLayer() {
		return VanillaHudElements.CROSSHAIR;
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender() || CLIENT.player == null ||
				!CLIENT.options.getPerspective().isFirstPerson() || CLIENT.interactionManager == null ||
				(CLIENT.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR && !this.shouldRenderSpectatorCrosshair(CLIENT.crosshairTarget)) ||
				this.shouldNotRenderCrosshair()) {
			return;
		}

		int screenWidth = CLIENT.getWindow().getScaledWidth();
		int screenHeight = CLIENT.getWindow().getScaledHeight();
		double startX = screenWidth / 2.0 - (size / 2.0) * scale.getValue();
		double startY = screenHeight / 2.0 - (size / 2.0) * scale.getValue();

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate((float) startX, (float) startY);
		matrices.scale(scale.getValue());

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (pixels.getValue()[y][x] >> 24 != 0) {
					if (disableBlending.getValue()) {
						context.fill(x, y, x + 1, y + 1, pixels.getValue()[y][x]);
					} else {
						context.fill(CROSSHAIR_PIPELINE, x, y, x + 1, y + 1, pixels.getValue()[y][x]);
					}
				}
			}
		}

		matrices.popMatrix();

		if (CLIENT.options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR) {
			float attackCooldownProgress = CLIENT.player.getAttackCooldownProgress(0.0F);
			boolean renderFullAttackIndicator = false;
			if (CLIENT.targetedEntity instanceof LivingEntity && attackCooldownProgress >= 1.0F) {
				renderFullAttackIndicator = CLIENT.player.getAttackCooldownProgressPerTick() > 5.0F;
				renderFullAttackIndicator &= CLIENT.targetedEntity.isAlive();
			}

			int y = context.getScaledWindowHeight() / 2 - 7 + 16;
			int x = context.getScaledWindowWidth() / 2 - 8;
			if (renderFullAttackIndicator) {
				context.drawGuiTexture((disableBlending.getValue() ? RenderPipelines.GUI_TEXTURED : RenderPipelines.CROSSHAIR), CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, x, y, 16, 16);
			} else if (attackCooldownProgress < 1.0F) {
				int l = (int) (attackCooldownProgress * 17.0F);
				context.drawGuiTexture((disableBlending.getValue() ? RenderPipelines.GUI_TEXTURED : RenderPipelines.CROSSHAIR), CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, x, y, 16, 4);
				context.drawGuiTexture((disableBlending.getValue() ? RenderPipelines.GUI_TEXTURED : RenderPipelines.CROSSHAIR), CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, x, y, l, 4);
			}
		}
	}

	public boolean shouldNotRenderCrosshair() {
		return CLIENT.getDebugHud().shouldShowDebugHud() && CLIENT.options.getPerspective() == Perspective.FIRST_PERSON && CLIENT.player != null && !CLIENT.player.hasReducedDebugInfo() && !(Boolean) CLIENT.options.getReducedDebugInfo().getValue();
	}

	private boolean shouldRenderSpectatorCrosshair(@Nullable HitResult hitResult) {
		if (hitResult == null || CLIENT.world == null) {
			return false;
		} else if (hitResult.getType() == HitResult.Type.ENTITY) {
			return ((EntityHitResult) hitResult).getEntity() instanceof NamedScreenHandlerFactory;
		} else if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
			World world = CLIENT.world;
			return world.getBlockState(blockPos).createScreenHandlerFactory(world, blockPos) != null;
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
				if (CLIENT.getLanguageManager().getLanguage().equals("fr_fr")) {
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
