package me.Azz_9.better_hud.mixin;

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientWorld.class)
public class TimeChangerMixin {

    /*// Stockez votre heure client-side
    private static long clientTime = -1;

    // Méthode pour définir l'heure du client
    public static void setClientTime(long time) {
        clientTime = time;
    }

    @Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
    private void overrideTimeOfDay(CallbackInfoReturnable<Long> cir) {
        if (clientTime != -1) {
            // Si une heure personnalisée est définie, on la remplace
            cir.setReturnValue(clientTime);
        }
    }*/

}
