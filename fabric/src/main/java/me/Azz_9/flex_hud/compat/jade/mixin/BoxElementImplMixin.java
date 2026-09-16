package me.Azz_9.flex_hud.compat.jade.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.BossBar;
import snownee.jade.api.ui.Rect2f;
import snownee.jade.impl.ui.BoxElementImpl;

@Mixin(BoxElementImpl.class)
public abstract class BoxElementImplMixin {

	@ModifyExpressionValue(
			method = "updateExpectedRect",
			at = @At(
					value = "INVOKE",
					target = "Lsnownee/jade/util/ClientProxy;getBossBarRect()Lsnownee/jade/api/ui/Rect2f;"
			)
	)
	private Rect2f modifyBossBarRect(Rect2f original) {
		BossBar bossBar = Modules.getInstance().bossBar;
		if (!Modules.getInstance().isEnabled.getValue() || !bossBar.enabled.getValue()) {
			return original;
		}

		if (!bossBar.showBossBar.getValue()) {
			return new Rect2f();
		}

		return new Rect2f(
				bossBar.getRoundedX(),
				bossBar.getRoundedY(),
				bossBar.getWidth() * bossBar.getScale(),
				bossBar.getHeight() * bossBar.getScale()
		);
	}
}
