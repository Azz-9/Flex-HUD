package me.Azz_9.better_hud.mixin.client;

import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.client.utils.TimeChanger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.Properties.class)
public abstract class ClientWorldPropertiesMixin {

	@Inject(at = @At("RETURN"), method = "getTimeOfDay", cancellable = true)
	@Environment(EnvType.CLIENT)
	public void getTimeOfDay(CallbackInfoReturnable<Long> cir) {
		if (ModConfig.getInstance().isEnabled && ModConfig.getInstance().timeChanger.enabled) {
			if (ModConfig.getInstance().timeChanger.selectedTime >= 0 && !ModConfig.getInstance().timeChanger.useRealTime) {
				cir.setReturnValue((long) ModConfig.getInstance().timeChanger.selectedTime);
			} else if (ModConfig.getInstance().timeChanger.useRealTime) {
				cir.setReturnValue(TimeChanger.getInstance().getRealTimeAsMinecraftTime());
			}
		}
		else cir.cancel();
	}
}