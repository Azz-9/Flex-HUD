package me.Azz_9.flex_hud.compat.jade.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.util.Mth;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.BossBar;
import snownee.jade.impl.ui.BoxElement;

@Mixin(BoxElement.class)
public abstract class BoxElementMixin {

	@ModifyExpressionValue(
			method = "updateExpectedRect",
			at = @At(
					value = "INVOKE",
					target = "Lsnownee/jade/util/ClientProxy;getBossBarRect()Lnet/minecraft/client/renderer/Rect2i;"
			)
	)
	private Rect2i modifyBossBarRect(Rect2i original) {
		BossBar bossBar = Modules.getInstance().bossBar;
		if (!Modules.getInstance().isEnabled.getValue() || !bossBar.enabled.getValue()) {
			return original;
		}

		if (!bossBar.showBossBar.getValue()) {
			return new Rect2i(0, 0, 0, 0);
		}

		return new Rect2i(
				bossBar.getRoundedX(),
				bossBar.getRoundedY(),
				Mth.ceil(bossBar.getWidth() * bossBar.getScale()),
				Mth.ceil(bossBar.getHeight() * bossBar.getScale())
		);
	}
}
