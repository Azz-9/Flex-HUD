package me.Azz_9.better_hud.client.utils.compass;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class TamedEntityUtils {
	private static List<LivingEntity> tamedEntities = new ArrayList<>();

	public static void update() {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;
		tamedEntities = player.getWorld().getEntitiesByClass(
				LivingEntity.class,
				player.getBoundingBox().expand(100),
				entity -> {

					if (entity instanceof AbstractHorseEntity horseEntity && horseEntity.isTame() ||
							entity instanceof Tameable tameable && tameable.getOwner() != null && tameable.getOwner().getUuid().equals(player.getUuid())
					) {
						for (Entity value : entity.getPassengersDeep()) {
							if (value.getUuid().equals(player.getUuid())) {
								return false;
							}
						}
						return true;
					}
					return false;
				}
		);
	}

	public static List<LivingEntity> getTamedEntities() {
		return tamedEntities;
	}
}
