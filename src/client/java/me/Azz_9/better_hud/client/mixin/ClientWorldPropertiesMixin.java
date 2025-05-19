package me.Azz_9.better_hud.client.mixin;

import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableMods.mods.notHud.TimeChanger;
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
		if (JsonConfigHelper.getInstance().isEnabled && JsonConfigHelper.getInstance().timeChanger.enabled) {

			if (JsonConfigHelper.getInstance().timeChanger.selectedTime >= 0 && !JsonConfigHelper.getInstance().timeChanger.useRealTime) {
				cir.setReturnValue((long) JsonConfigHelper.getInstance().timeChanger.selectedTime);

			} else if (JsonConfigHelper.getInstance().timeChanger.useRealTime) {
				cir.setReturnValue(TimeChanger.getRealTimeAsMinecraftTime());

			}
		}
		else cir.cancel();
	}
}