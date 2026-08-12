package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.ClientClockManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.notHud.TimeChanger;

@Mixin(ClientClockManager.ClientClockInstance.class)
public abstract class ClientClockManagerMixin {

	@Inject(method = "totalTicks", at = @At("RETURN"), cancellable = true)
	private void getTotalTicks(CallbackInfoReturnable<Long> cir) {
		if (Modules.getInstance().isEnabled.getValue() &&
				Modules.getInstance().timeChanger.enabled.getValue()) {

			if (Modules.getInstance().timeChanger.selectedTime.getValue() >= 0 && !Modules.getInstance().timeChanger.useRealTime.getValue()) {
				cir.setReturnValue((long) Modules.getInstance().timeChanger.selectedTime.getValue());

			} else if (Modules.getInstance().timeChanger.useRealTime.getValue()) {
				cir.setReturnValue(TimeChanger.getRealTimeAsMinecraftTime());
			}

		} else cir.cancel();
	}
}
