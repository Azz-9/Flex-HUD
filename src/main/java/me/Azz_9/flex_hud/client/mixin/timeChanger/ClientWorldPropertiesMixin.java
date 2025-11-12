package me.Azz_9.flex_hud.client.mixin.timeChanger;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
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
		if (ModulesHelper.getInstance().isEnabled.getValue() && ModulesHelper.getInstance().timeChanger.enabled.getValue()) {

			if (ModulesHelper.getInstance().timeChanger.selectedTime.getValue() >= 0 && !ModulesHelper.getInstance().timeChanger.useRealTime.getValue()) {
				cir.setReturnValue((long) ModulesHelper.getInstance().timeChanger.selectedTime.getValue());

			} else if (ModulesHelper.getInstance().timeChanger.useRealTime.getValue()) {
				cir.setReturnValue(TimeChanger.getRealTimeAsMinecraftTime());

			}
		} else cir.cancel();
	}
}