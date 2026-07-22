package me.Azz_9.flex_hud.mixin;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;

import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.BossBar;

@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void cancelRender(GuiGraphics graphics, CallbackInfo ci) {
		BossBar bossBar = Modules.getInstance().bossBar;
		if (Modules.getInstance().isEnabled.getValue()
				&& bossBar.enabled.getValue()
				&& (!bossBar.showBossBar.getValue()
				|| bossBar.hideInF3.getValue() && MINECRAFT.getDebugOverlay().showDebugScreen())) {
			ci.cancel();
		}
	}

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;nextStratum()V"
			)
	)
	private void beforeRender(GuiGraphics graphics, CallbackInfo ci) {
		BossBar bossBar = Modules.getInstance().bossBar;
		if (Modules.getInstance().isEnabled.getValue() && bossBar.enabled.getValue()) {
			Matrix3x2fStack matrices = graphics.pose();
			matrices.pushMatrix();
			matrices.translate(bossBar.getRoundedX(), bossBar.getRoundedY());
			matrices.scale(bossBar.getScale());
		}
	}

	@ModifyVariable(
			method = "render",
			at = @At("STORE"),
			index = 3
	)
	private int modifyScreenWidth(int screenWidth) {
		if (Modules.getInstance().isEnabled.getValue() && Modules.getInstance().bossBar.enabled.getValue()) {
			return Modules.getInstance().bossBar.getWidth();
		}
		return screenWidth;
	}

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"
			)
	)
	private void afterRender(GuiGraphics graphics, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue() && Modules.getInstance().bossBar.enabled.getValue()) {
			graphics.pose().popMatrix();
		}
	}

	// placeholder
	@ModifyExpressionValue(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Map;isEmpty()Z"
			)
	)
	private boolean modifyIsEmpty(boolean original) {
		if (Modules.getInstance().bossBar.shouldShowInEditLayoutScreen() && CommonClass.isEditingLayout) {
			return false;
		}
		return original;
	}

	@ModifyExpressionValue(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Map;values()Ljava/util/Collection;"
			)
	)
	private Collection<LerpingBossEvent> modifyEvents(Collection<LerpingBossEvent> original) {
		if (Modules.getInstance().bossBar.shouldShowInEditLayoutScreen() && CommonClass.isEditingLayout) {
			return List.of(BossBar.placeholderEvent);
		}

		return original;
	}
}
