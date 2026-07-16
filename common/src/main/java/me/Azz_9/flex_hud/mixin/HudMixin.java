package me.Azz_9.flex_hud.mixin;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.scores.Objective;

import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.customModules.Variables;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.Crosshair;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.Scoreboard;

@Mixin(Hud.class)
public abstract class HudMixin {

	@Unique
	private static final int flex_hud$PADDING = 2;

	// trigger variables frame update on hud render
	@Inject(method = "extractRenderState", at = @At("HEAD"))
	private void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		Variables.frame();
	}

	// always display xp bar when compass overrides the locator bar
	@Inject(method = "willPrioritizeExperienceInfo", at = @At("RETURN"), cancellable = true)
	private void willPrioritizeExperienceInfo(CallbackInfoReturnable<Boolean> cir) {
		if (Modules.getInstance().isEnabled.getValue() &&
				Modules.getInstance().compass.enabled.getValue() &&
				Modules.getInstance().compass.overrideLocatorBar.getValue()) {
			cir.setReturnValue(true);
		}
	}

	// ------------------- Crosshair -------------------
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
		return flex_hud$getAttackIndicatorPipeline(original);
	}

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
		return flex_hud$getAttackIndicatorPipeline(original);
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
		return flex_hud$getAttackIndicatorPipeline(original);
	}

	@Unique
	private static RenderPipeline flex_hud$getAttackIndicatorPipeline(RenderPipeline original) {
		Crosshair crosshair = Modules.getInstance().crosshair;
		if (crosshair.shouldReplaceVanillaCrosshair() && crosshair.disableBlending.getValue()) {
			return RenderPipelines.GUI_TEXTURED;
		}

		return original;
	}

	// ------------------- Scoreboard -------------------
	@Inject(method = "extractScoreboardSidebar", at = @At("HEAD"), cancellable = true)
	private void extractScoreboardSidebar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue()
				&& Modules.getInstance().scoreboard.enabled.getValue()
				&& (!Modules.getInstance().scoreboard.showScoreboard.getValue() && !CommonClass.isEditingLayout
				|| Modules.getInstance().scoreboard.hideInF3.getValue() && MINECRAFT.debugEntries.isOverlayVisible())) { //
			ci.cancel();
		}
	}

	@ModifyVariable(
			method = "extractScoreboardSidebar",
			at = @At("STORE"),
			name = "displayObjective"
	)
	private Objective modifyDisplayedObjective(Objective displayObjective) {
		if (Modules.getInstance().isEnabled.getValue() && Modules.getInstance().scoreboard.enabled.getValue() && CommonClass.isEditingLayout) {
			return Scoreboard.placeholderObjective;
		}
		return displayObjective;
	}

	@ModifyExpressionValue(
			method = "lambda$displayScoreboardSidebar$1",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/scores/PlayerScoreEntry;formatValue(Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"
			)
	)
	private MutableComponent modifyScoreString(MutableComponent original) {
		if (Modules.getInstance().isEnabled.getValue()
				&& Modules.getInstance().scoreboard.enabled.getValue()
				&& Modules.getInstance().scoreboard.showScore.getValue()) {
			return original;
		}
		return Component.empty();
	}

	@ModifyExpressionValue(
			method = "lambda$displayScoreboardSidebar$1",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/Font;width(Lnet/minecraft/network/chat/FormattedText;)I"
			)
	)
	private int modifyScoreWidth(int original) {
		if (Modules.getInstance().isEnabled.getValue()
				&& Modules.getInstance().scoreboard.enabled.getValue()
				&& Modules.getInstance().scoreboard.showScore.getValue()) {
			return original;
		}
		return 0;
	}

	@ModifyVariable(
			method = "displayScoreboardSidebar",
			at = @At("STORE"),
			name = "headerY"
	)
	private int beforeRender(
			int headerY,
			GuiGraphicsExtractor graphics,
			Objective objective,
			@Local(name = "biggestWidth") int biggestWidth,
			@Local(name = "height") int height,
			@Local(name = "bottom") LocalIntRef bottom,
			@Local(name = "left") LocalIntRef left,
			@Local(name = "right") LocalIntRef right,
			@Local(name = "backgroundColor") LocalIntRef backgroundColor,
			@Local(name = "headerBackgroundColor") LocalIntRef headerBackgroundColor
	) {
		Scoreboard scoreboard = Modules.getInstance().scoreboard;

		if (!Modules.getInstance().isEnabled.getValue() || !scoreboard.enabled.getValue()) {
			return headerY;
		}

		scoreboard.setWidth(biggestWidth + flex_hud$PADDING * 2);
		scoreboard.setHeight(MINECRAFT.font.lineHeight + 1 + height);

		bottom.set(scoreboard.getHeight());
		left.set(flex_hud$PADDING);
		right.set(scoreboard.getWidth());
		backgroundColor.set(ARGB.color(0.3f, scoreboard.backgroundColor.getValue()));
		headerBackgroundColor.set(ARGB.color(0.4f, scoreboard.backgroundColor.getValue()));

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(scoreboard.getRoundedX(), scoreboard.getRoundedY());
		matrices.scale(scoreboard.getScale());

		return scoreboard.getHeight() - height;
	}

	@WrapWithCondition(
			method = "displayScoreboardSidebar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"
			)
	)
	private boolean conditionBackground(GuiGraphicsExtractor instance, int x0, int y0, int x1, int y1, int col) {
		return !Modules.getInstance().isEnabled.getValue()
				|| !Modules.getInstance().scoreboard.enabled.getValue()
				|| Modules.getInstance().scoreboard.drawBackground.getValue();
	}

	@WrapOperation(
			method = "displayScoreboardSidebar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"
			)
	)
	private void textShadow(GuiGraphicsExtractor instance, Font font, Component str, int x, int y, int color, boolean dropShadow, Operation<Void> original) {
		if (Modules.getInstance().isEnabled.getValue() && Modules.getInstance().scoreboard.enabled.getValue()) {
			original.call(instance, font, str, x, y, color, Modules.getInstance().scoreboard.shadow.getValue());
		} else {
			original.call(instance, font, str, x, y, color, dropShadow);
		}
	}

	@Inject(method = "displayScoreboardSidebar", at = @At("RETURN"))
	private void popMatrix(GuiGraphicsExtractor graphics, Objective objective, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue() && Modules.getInstance().scoreboard.enabled.getValue()) {
			graphics.pose().popMatrix();
		}
	}
}
