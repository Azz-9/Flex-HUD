package me.Azz_9.flex_hud.client.tickables;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.Compass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.TraderLlama;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

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
	public void tick(Minecraft minecraft) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || minecraft.level == null) return;

		tamedEntitiesTextures.clear();
		mobEntitiesTextures.clear();
		petsEntitiesTextures.clear();

		for (Entity entity : minecraft.level.entitiesForRendering()) {
			if (entity instanceof Mob mob) {


				Vec3 eyePosition = player.getEyePosition(0);
				if (!mob.shouldRender(eyePosition.x(), eyePosition.y(), eyePosition.z())) {
					continue;
				}

				boolean passenger = false;
				for (Entity value : mob.getIndirectPassengers()) {
					if (value.getUUID().equals(player.getUUID())) {
						passenger = true;
						break;
					}
				}
				if (passenger) {
					continue;
				}

				Identifier id = null;
				switch (mob) {
					case EnderDragon enderDragonEntity ->
							id = Identifier.fromNamespaceAndPath(MOD_ID, "living_entities/minecraft/enderdragon/dragon.png");
					case SnowGolem snowGolemEntity -> {
						if (snowGolemEntity.hasPumpkin()) {
							id = Identifier.fromNamespaceAndPath(MOD_ID, "living_entities/minecraft/snow_golem.png");
						} else {
							id = Identifier.fromNamespaceAndPath(MOD_ID, "living_entities/minecraft/snow_golem_pumpkinless.png");
						}
					}
					case TraderLlama traderLlamaEntity -> {
						switch (traderLlamaEntity.getVariant()) {
							case GRAY ->
									id = Identifier.fromNamespaceAndPath(MOD_ID, "living_entities/minecraft/llama/trader/gray.png");
							case BROWN ->
									id = Identifier.fromNamespaceAndPath(MOD_ID, "living_entities/minecraft/llama/trader/brown.png");
							case CREAMY ->
									id = Identifier.fromNamespaceAndPath(MOD_ID, "living_entities/minecraft/llama/trader/creamy.png");
							case WHITE ->
									id = Identifier.fromNamespaceAndPath(MOD_ID, "living_entities/minecraft/llama/trader/white.png");
						}
					}
					default -> {


						EntityRenderer<? super LivingEntity, ?> renderer = minecraft.getEntityRenderDispatcher().getRenderer(mob);

						if (renderer instanceof LivingEntityRenderer<?, ?, ?> livingRenderer) {

							try {
								@SuppressWarnings("unchecked")
								LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?> casted = (LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>) livingRenderer;

								LivingEntityRenderState state = casted.createRenderState(mob, 0);

								Identifier minecraft_id = casted.getTextureLocation(state);
								id = Identifier.fromNamespaceAndPath(MOD_ID, "living_entities/" + minecraft_id.getNamespace() + minecraft_id.getPath().replace("textures/entity", ""));

							} catch (Exception ignored) {
							}
						}
					}
				}

				// if the texture is not found, skip this entity
				if (id == null || minecraft.getResourceManager().getResource(id).isEmpty()) {
					continue;
				}

				mobEntitiesTextures.add(new EntityTexture(id, mob));

				if (mob instanceof AbstractHorse horseEntity && horseEntity.isTamed() ||
						mob instanceof TamableAnimal tameable
								&& tameable.getOwner() != null
								&& tameable.getOwner().getUUID().equals(player.getUUID())) {

					tamedEntitiesTextures.add(new EntityTexture(id, mob));

					if (mob instanceof Wolf
							|| mob instanceof Cat
							|| mob instanceof Parrot) {

						petsEntitiesTextures.add(new EntityTexture(id, mob));
					}
				}
			}
		}
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
