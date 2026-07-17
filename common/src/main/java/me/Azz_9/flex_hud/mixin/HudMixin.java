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
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.customModules.Variables;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.Crosshair;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.Scoreboard;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.Titles;

@Mixin(Hud.class)
public abstract class HudMixin {

	@Shadow
	private @Nullable Component title;
	@Shadow
	private @Nullable Component subtitle;
	@Shadow
	private int titleTime;
	@Shadow
	private int titleFadeInTime;
	@Shadow
	private int titleStayTime;
	@Shadow
	private int titleFadeOutTime;

	@Shadow
	public abstract Font getFont();

	@Unique
	private TitleState flex_hud$savedTitleState;
	@Unique
	private static final int flex_hud$SCOREBOARD_PADDING = 2;

	// trigger variables frame update on hud render
	@Inject(method = "extractRenderState", at = @At("HEAD"))
	private void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		Variables.frame();
	}

	// always display xp bar when compass overrides the locator bar
	@Inject(method = "willPrioritizeExperienceInfo", at = @At("RETURN"), cancellable = true)
	private void willPrioritizeExperienceInfo(CallbackInfoReturnable<Boolean> cir) {
		if (Modules.getInstance().isEnabled.getValue() &&
				Modules.getInstance().compass.isEnabled() &&
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
		Scoreboard scoreboard = Modules.getInstance().scoreboard;
		if (Modules.getInstance().isEnabled.getValue()
				&& scoreboard.enabled.getValue()
				&& (!scoreboard.showScoreboard.getValue()
				|| scoreboard.hideInF3.getValue() && MINECRAFT.debugEntries.isOverlayVisible())) {
			ci.cancel();
		}
	}

	@ModifyVariable(
			method = "extractScoreboardSidebar",
			at = @At("STORE"),
			name = "displayObjective"
	)
	private Objective modifyDisplayedObjective(Objective displayObjective) {
		if (Modules.getInstance().scoreboard.shouldShowInEditLayoutScreen() && CommonClass.isEditingLayout) {
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

		scoreboard.setWidth(biggestWidth + flex_hud$SCOREBOARD_PADDING * 2);
		scoreboard.setHeight(MINECRAFT.font.lineHeight + 1 + height);

		bottom.set(scoreboard.getHeight());
		left.set(flex_hud$SCOREBOARD_PADDING);
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

	// ------------------- Title -------------------
	// title
	@ModifyArgs(
			method = "extractTitle",
			at = @At(
					value = "INVOKE",
					target = "Lorg/joml/Matrix3x2fStack;translate(FF)Lorg/joml/Matrix3x2f;",
					ordinal = 0
			)
	)
	private void removeVanillaTranslation(Args args) {
		if (Modules.getInstance().isEnabled.getValue() && Modules.getInstance().titles.enabled.getValue()) {
			args.set(0, 0.0F);
			args.set(1, 0.0F);
		}
	}

	@ModifyArgs(
			method = "extractTitle",
			at = @At(
					value = "INVOKE",
					target = "Lorg/joml/Matrix3x2fStack;scale(FF)Lorg/joml/Matrix3x2f;",
					ordinal = 0
			)
	)
	private void scaleAndTranslateTitle(Args args, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Titles titles = Modules.getInstance().titles;

		if (!Modules.getInstance().isEnabled.getValue() || !titles.enabled.getValue()) {
			return;
		}

		float vanillaScaleX = args.get(0);
		float vanillaScaleY = args.get(1);

		if (title != null) {
			titles.setWidth(0, Math.round(getFont().width(title) * vanillaScaleX));
			titles.setHeight(0, Math.round(getFont().lineHeight * vanillaScaleY));
		} else {
			titles.setWidth(0, 0);
			titles.setHeight(0, 0);
		}

		graphics.pose().translate(
				titles.getRoundedX(0),
				titles.getRoundedY(0)
		);

		args.set(0, vanillaScaleX * titles.getScale(0));
		args.set(1, vanillaScaleY * titles.getScale(0));
	}

	@WrapOperation(
			method = "extractTitle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V",
					ordinal = 0
			)
	)
	private void modifyTitle(
			GuiGraphicsExtractor instance,
			Font font,
			Component str,
			int textX,
			int textY,
			int textWidth,
			int textColor,
			Operation<Void> original,
			@Local(name = "alpha") int alpha
	) {
		Titles titles = Modules.getInstance().titles;
		if (Modules.getInstance().isEnabled.getValue() && titles.enabled.getValue()) {
			if (titles.showTitle.getValue()) {
				titles.drawBackground(1, instance, textWidth, getFont().lineHeight, alpha / 255.0f);
				instance.text(font, str, 0, 0, ARGB.color(alpha, titles.getColor()), titles.shadow.getValue());
			}
		} else {
			original.call(instance, font, str, textX, textY, textWidth, textColor);
		}
	}

	// subtitle

	@ModifyArgs(
			method = "extractTitle",
			at = @At(
					value = "INVOKE",
					target = "Lorg/joml/Matrix3x2fStack;scale(FF)Lorg/joml/Matrix3x2f;",
					ordinal = 1
			)
	)
	private void scaleAndTranslateSubtitle(Args args, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Titles titles = Modules.getInstance().titles;

		if (!Modules.getInstance().isEnabled.getValue() || !titles.enabled.getValue()) {
			return;
		}

		float vanillaScaleX = args.get(0);
		float vanillaScaleY = args.get(1);

		if (subtitle != null) {
			titles.setWidth(1, Math.round(getFont().width(subtitle) * vanillaScaleX));
			titles.setHeight(1, Math.round(getFont().lineHeight * vanillaScaleY));
		} else {
			titles.setWidth(1, 0);
			titles.setHeight(1, 0);
		}

		graphics.pose().translate(
				titles.getRoundedX(1),
				titles.getRoundedY(1)
		);

		args.set(0, vanillaScaleX * titles.getScale(1));
		args.set(1, vanillaScaleY * titles.getScale(1));
	}

	@WrapOperation(
			method = "extractTitle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V",
					ordinal = 1
			)
	)
	private void modifySubtitle(
			GuiGraphicsExtractor instance,
			Font font,
			Component str,
			int textX,
			int textY,
			int textWidth,
			int textColor,
			Operation<Void> original,
			@Local(name = "alpha") int alpha
	) {
		Titles titles = Modules.getInstance().titles;
		if (Modules.getInstance().isEnabled.getValue() && titles.enabled.getValue()) {
			if (titles.showSubtitle.getValue()) {
				titles.drawBackground(1, instance, textWidth, getFont().lineHeight, alpha / 255.0f);
				instance.text(font, str, 0, 0, ARGB.color(alpha, titles.getColor()), titles.shadow.getValue());
			}
		} else {
			original.call(instance, font, str, textX, textY, textWidth, textColor);
		}
	}

	// placeholder
	@Inject(method = "extractTitle", at = @At("HEAD"), cancellable = true)
	private void beforeExtractTitle(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		Titles titles = Modules.getInstance().titles;
		if (Modules.getInstance().isEnabled.getValue()
				&& titles.enabled.getValue()
				&& (!titles.showTitle.getValue() && !titles.showSubtitle.getValue()
				|| titles.hideInF3.getValue() && MINECRAFT.debugEntries.isOverlayVisible())) {
			ci.cancel();
			return;
		}

		if (titles.shouldShowInEditLayoutScreen() && CommonClass.isEditingLayout) {
			flex_hud$savedTitleState = new TitleState(
					title,
					subtitle,
					titleTime,
					titleFadeInTime,
					titleStayTime,
					titleFadeOutTime
			);

			title = Titles.placeholderTitle;
			subtitle = Titles.placeholderSubtitle;

			titleFadeInTime = 0;
			titleStayTime = Integer.MAX_VALUE;
			titleFadeOutTime = 0;
			titleTime = Integer.MAX_VALUE;
		}
	}

	@Inject(method = "extractTitle", at = @At("RETURN"))
	private void restoreRealTitle(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (flex_hud$savedTitleState == null) {
			return;
		}

		title = flex_hud$savedTitleState.title();
		subtitle = flex_hud$savedTitleState.subtitle();
		titleTime = flex_hud$savedTitleState.titleTime();
		titleFadeInTime = flex_hud$savedTitleState.fadeInTime();
		titleStayTime = flex_hud$savedTitleState.stayTime();
		titleFadeOutTime = flex_hud$savedTitleState.fadeOutTime();

		flex_hud$savedTitleState = null;
	}

	@Unique
	private record TitleState(
			@Nullable Component title,
			@Nullable Component subtitle,
			int titleTime,
			int fadeInTime,
			int stayTime,
			int fadeOutTime
	) {
	}
}
