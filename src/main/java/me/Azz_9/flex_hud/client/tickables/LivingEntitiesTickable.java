package me.Azz_9.flex_hud.client.tickables;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;
import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.Compass;

public class LivingEntitiesTickable implements Tickable {
	private static final List<EntityTexture> tamedEntitiesTextures = new ArrayList<>();
	private static final List<EntityTexture> mobEntitiesTextures = new ArrayList<>();
	private static final List<EntityTexture> petsEntitiesTextures = new ArrayList<>();

	static {
		TickRegistry.register(new LivingEntitiesTickable());
	}

	@Override
	public boolean shouldTick() {
		Compass compass = ModulesHelper.getInstance().compass;
		return compass.showMobs.getValue() || compass.showTamedEntitiesPoint.getValue();
	}

	@Override
	public void tick(MinecraftClient client) {
		PlayerEntity player = CLIENT.player;
		if (player == null || CLIENT.world == null) return;

		tamedEntitiesTextures.clear();
		mobEntitiesTextures.clear();
		petsEntitiesTextures.clear();

		for (Entity entity : CLIENT.world.getEntities()) {
			if (entity instanceof MobEntity mobEntity) {


				Vec3d cameraPos = player.getCameraPosVec(0);
				if (!mobEntity.shouldRender(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ())) {
					continue;
				}

				boolean passenger = false;
				for (Entity value : mobEntity.getPassengersDeep()) {
					if (value.getUuid().equals(player.getUuid())) {
						passenger = true;
						break;
					}
				}
				if (passenger) {
					continue;
				}

				Identifier id = getMobHeadTexture(mobEntity);

				// if the texture is not found, skip this entity
				if (id == null || CLIENT.getResourceManager().getResource(id).isEmpty()) {
					continue;
				}

				mobEntitiesTextures.add(new EntityTexture(id, mobEntity));

				if (mobEntity instanceof AbstractHorseEntity horseEntity && horseEntity.isTame() ||
						mobEntity instanceof Tameable tameable
								&& tameable.getOwner() != null
								&& tameable.getOwner().getUuid().equals(player.getUuid())) {

					tamedEntitiesTextures.add(new EntityTexture(id, mobEntity));

					if (mobEntity instanceof WolfEntity
							|| mobEntity instanceof CatEntity
							|| mobEntity instanceof ParrotEntity) {

						petsEntitiesTextures.add(new EntityTexture(id, mobEntity));
					}
				}
			}
		}
	}

	private Identifier getMobHeadTexture(MobEntity mobEntity) {
		Identifier id = null;
		switch (mobEntity) {
			case EnderDragonEntity enderDragonEntity ->
					id = Identifier.of(MOD_ID, "living_entities/minecraft/enderdragon/dragon.png");
			case SnowGolemEntity snowGolemEntity -> {
				String path = "living_entities/minecraft/snow_golem";
				if (!snowGolemEntity.hasPumpkin())
					path += "_pumpkinless";

				id = Identifier.of(MOD_ID, path + ".png");
			}
			case TraderLlamaEntity traderLlamaEntity -> {
				String path = "living_entities/minecraft/llama/trader/llama_" + traderLlamaEntity.getVariant().asString() + ".png";
				id = Identifier.of(MOD_ID, path);
			}
			default -> {
				EntityRenderer<? super LivingEntity, ?> renderer = CLIENT.getEntityRenderDispatcher().getRenderer(mobEntity);

				if (renderer instanceof LivingEntityRenderer<?, ?, ?> livingRenderer) {

					try {
						@SuppressWarnings("unchecked")
						LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?> casted = (LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>) livingRenderer;

						LivingEntityRenderState state = casted.getAndUpdateRenderState(mobEntity, 0);

						Identifier minecraft_id = casted.getTexture(state);
						id = Identifier.of(MOD_ID, "living_entities/" + minecraft_id.getNamespace() + minecraft_id.getPath().replace("textures/entity", ""));

					} catch (Exception ignored) {
					}
				}
			}
		}

		return id;
	}

	public static List<EntityTexture> getTamedEntities() {
		return tamedEntitiesTextures;
	}

	public static List<EntityTexture> getMobEntities() {
		return mobEntitiesTextures;
	}

	public static List<EntityTexture> getPetsEntities() {
		return petsEntitiesTextures;
	}

	public record EntityTexture(Identifier texture, LivingEntity entity) {
	}
}
