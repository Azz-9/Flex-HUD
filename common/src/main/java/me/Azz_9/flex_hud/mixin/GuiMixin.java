package me.Azz_9.flex_hud.mixin;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.scores.Objective;

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

@Mixin(Gui.class)
public abstract class GuiMixin {

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
	// trigger variables frame update on hud render
	@Inject(method = "render", at = @At("HEAD"))
	private void render(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
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

	// potion effect
	@Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
	private void renderEffects(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue() && Modules.getInstance().potionEffect.enabled.getValue()) {
			ci.cancel();
		}
	}

	// ------------------- Crosshair -------------------
	@WrapOperation(
			method = "renderCrosshair",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
					ordinal = 0
			)
	)
	private void replaceCrosshair(
			GuiGraphics graphics,
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
			method = "renderCrosshair",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
					ordinal = 1
			),
			index = 0
	)
	private RenderPipeline modifyFullAttackIndicatorPipeline(RenderPipeline original) {
		return flex_hud$getAttackIndicatorPipeline(original);
	}

	@ModifyArg(
			method = "renderCrosshair",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
					ordinal = 2
			),
			index = 0
	)
	private RenderPipeline modifyAttackIndicatorBackgroundPipeline(RenderPipeline original) {
		return flex_hud$getAttackIndicatorPipeline(original);
	}

	@ModifyArg(
			method = "renderCrosshair",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"
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
	@Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
	private void renderScoreboardSidebar(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		Scoreboard scoreboard = Modules.getInstance().scoreboard;
		if (Modules.getInstance().isEnabled.getValue()
				&& scoreboard.enabled.getValue()
				&& (!scoreboard.showScoreboard.getValue()
				|| scoreboard.hideInF3.getValue() && MINECRAFT.debugEntries.isOverlayVisible())) {
			ci.cancel();
		}
	}

	@ModifyVariable(
			method = "renderScoreboardSidebar",
			at = @At("STORE"),
			index = 6
	)
	private Objective modifyDisplayedObjective(Objective displayObjective) {
		if (Modules.getInstance().scoreboard.shouldShowInEditLayoutScreen() && CommonClass.isEditingLayout) {
			return Scoreboard.placeholderObjective;
		}
		return displayObjective;
	}

	@WrapWithCondition(
			method = "displayScoreboardSidebar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
			)
	)
	private boolean conditionBackground(GuiGraphics instance, int x0, int y0, int x1, int y1, int col) {
		return !Modules.getInstance().isEnabled.getValue()
				|| !Modules.getInstance().scoreboard.enabled.getValue()
				|| Modules.getInstance().scoreboard.drawBackground.getValue();
	}

	@WrapOperation(
			method = "displayScoreboardSidebar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"
			)
	)
	private void textShadow(GuiGraphics instance, Font font, Component str, int x, int y, int color, boolean dropShadow, Operation<Void> original) {
		if (Modules.getInstance().isEnabled.getValue() && Modules.getInstance().scoreboard.enabled.getValue()) {
			original.call(instance, font, str, x, y, color, Modules.getInstance().scoreboard.shadow.getValue());
		} else {
			original.call(instance, font, str, x, y, color, dropShadow);
		}
	}

	@Inject(method = "displayScoreboardSidebar", at = @At("RETURN"))
	private void popMatrix(GuiGraphics graphics, Objective objective, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue() && Modules.getInstance().scoreboard.enabled.getValue()) {
			graphics.pose().popMatrix();
		}
	}

	// ------------------- Title -------------------
	// title
	@ModifyArgs(
			method = "renderTitle",
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
			method = "renderTitle",
			at = @At(
					value = "INVOKE",
					target = "Lorg/joml/Matrix3x2fStack;scale(FF)Lorg/joml/Matrix3x2f;",
					ordinal = 0
			)
	)
	private void scaleAndTranslateTitle(Args args, GuiGraphics graphics, DeltaTracker deltaTracker) {
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
			method = "renderTitle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V",
					ordinal = 0
			)
	)
	private void modifyTitle(
			GuiGraphics instance,
			Font font,
			Component str,
			int textX,
			int textY,
			int textWidth,
			int textColor,
			Operation<Void> original,
			@Local(index = 5) int alpha
	) {
		Titles titles = Modules.getInstance().titles;
		if (Modules.getInstance().isEnabled.getValue() && titles.enabled.getValue()) {
			if (titles.showTitle.getValue()) {
				titles.drawBackground(1, instance, textWidth, getFont().lineHeight, alpha / 255.0f);
				instance.drawString(font, str, 0, 0, ARGB.color(alpha, titles.getColor()), titles.shadow.getValue());
			}
		} else {
			original.call(instance, font, str, textX, textY, textWidth, textColor);
		}
	}

	// subtitle

	@ModifyArgs(
			method = "renderTitle",
			at = @At(
					value = "INVOKE",
					target = "Lorg/joml/Matrix3x2fStack;scale(FF)Lorg/joml/Matrix3x2f;",
					ordinal = 1
			)
	)
	private void scaleAndTranslateSubtitle(Args args, GuiGraphics graphics, DeltaTracker deltaTracker) {
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
			method = "renderTitle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V",
					ordinal = 1
			)
	)
	private void modifySubtitle(
			GuiGraphics instance,
			Font font,
			Component str,
			int textX,
			int textY,
			int textWidth,
			int textColor,
			Operation<Void> original,
			@Local(index = 5) int alpha
	) {
		Titles titles = Modules.getInstance().titles;
		if (Modules.getInstance().isEnabled.getValue() && titles.enabled.getValue()) {
			if (titles.showSubtitle.getValue()) {
				titles.drawBackground(1, instance, textWidth, getFont().lineHeight, alpha / 255.0f);
				instance.drawString(font, str, 0, 0, ARGB.color(alpha, titles.getColor()), titles.shadow.getValue());
			}
		} else {
			original.call(instance, font, str, textX, textY, textWidth, textColor);
		}
	}

	// placeholder
	@Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
	private void beforeRenderTitle(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
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

	@Inject(method = "renderTitle", at = @At("RETURN"))
	private void restoreRealTitle(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
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
