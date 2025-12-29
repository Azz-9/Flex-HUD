package me.Azz_9.flex_hud.client.mixin.reach;

/*@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

	@Inject(method = "attackEntity", at = @At("HEAD"))
	private void onAttack(LocalPlayer player, Entity target, CallbackInfo ci) {
		if (!ModulesHelper.getInstance().isEnabled.getValue() || !ModulesHelper.getInstance().reach.enabled.getValue()) {
			return;
		}

		if (target.isAttackable()) {
			if (!target.skipAttackInteraction(player)) {
				if (Minecraft.getInstance().player != null && player.getUUID().equals(Minecraft.getInstance().player.getUUID())) {
					ReachTickable.calculateReach(player, target);
				}
			}
		}
	}
}*/