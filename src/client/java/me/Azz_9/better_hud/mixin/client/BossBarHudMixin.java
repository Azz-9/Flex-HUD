package me.Azz_9.better_hud.mixin.client;

import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin {
	@ModifyVariable(
			method = "renderBossBar(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/entity/boss/BossBar;I[Lnet/minecraft/util/Identifier;[Lnet/minecraft/util/Identifier;)V",
			at = @At("HEAD"),
			ordinal = 1,
			argsOnly = true)
	private int modifyFirstValue(int value) {
		return value + 20;
	}

	@ModifyVariable(
			method = "renderBossBar(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/entity/boss/BossBar;I[Lnet/minecraft/util/Identifier;[Lnet/minecraft/util/Identifier;)V",
			at = @At("HEAD"),
			ordinal = 2,
			argsOnly = true)
	private int modifySecondValue(int value) {
		return value + 100;
	}
}