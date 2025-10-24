package me.Azz_9.flex_hud.client.mixin.timeChanger;

import me.Azz_9.flex_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.TimeChanger;
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
		if (JsonConfigHelper.getInstance().isEnabled && JsonConfigHelper.getInstance().timeChanger.enabled.getValue()) {

			if (JsonConfigHelper.getInstance().timeChanger.selectedTime.getValue() >= 0 && !JsonConfigHelper.getInstance().timeChanger.useRealTime.getValue()) {
				cir.setReturnValue((long) JsonConfigHelper.getInstance().timeChanger.selectedTime.getValue());

			} else if (JsonConfigHelper.getInstance().timeChanger.useRealTime.getValue()) {
				cir.setReturnValue(TimeChanger.getRealTimeAsMinecraftTime());

			}
		} else cir.cancel();
	}
}