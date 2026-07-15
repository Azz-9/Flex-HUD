package me.Azz_9.flex_hud.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.Crosshair;

@Mixin(Hud.class)
public abstract class CrosshairMixin {

	@WrapOperation(
			method = "extractCrosshair",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
					ordinal = 0
			)
	)
	private void replaceCrosshair(
			GuiGraphicsExtractor graphics,
			RenderPipeline renderPipeline,
			Identifier location,
			int x,
			int y,
			int width,
			int height,
			Operation<Void> original
	) {
		Crosshair crosshair = Modules.getInstance().crosshair;

		if (crosshair.shouldReplaceVanillaCrosshair()) {
			crosshair.renderReplacement(graphics);
		} else {
			original.call(graphics, renderPipeline, location, x, y, width, height);
		}
	}

	@ModifyArg(
			method = "extractCrosshair",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
					ordinal = 1
			),
			index = 0
	)
	private RenderPipeline modifyFullAttackIndicatorPipeline(RenderPipeline original) {
		return flexHud$getAttackIndicatorPipeline(original);
	}

	/**
	 * Modifie le pipeline du fond de la barre de recharge.
	 */
	@ModifyArg(
			method = "extractCrosshair",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
					ordinal = 2
			),
			index = 0
	)
	private RenderPipeline modifyAttackIndicatorBackgroundPipeline(RenderPipeline original) {
		return flexHud$getAttackIndicatorPipeline(original);
	}

	@ModifyArg(
			method = "extractCrosshair",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"
			),
			index = 0
	)
	private RenderPipeline modifyAttackIndicatorProgressPipeline(RenderPipeline original) {
		return flexHud$getAttackIndicatorPipeline(original);
	}

	@Unique
	private static RenderPipeline flexHud$getAttackIndicatorPipeline(
			RenderPipeline original
	) {
		Crosshair crosshair = Modules.getInstance().crosshair;
		if (crosshair.shouldReplaceVanillaCrosshair() && crosshair.disableBlending.getValue()) {
			return RenderPipelines.GUI_TEXTURED;
		}

		return original;
	}
}
