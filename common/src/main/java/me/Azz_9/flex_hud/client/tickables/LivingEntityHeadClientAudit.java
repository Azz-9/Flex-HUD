package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.FlexHudLogger;

/**
 * Manual client-side audit used after updating Minecraft. It checks the default
 * render state of every vanilla mob once a world has been joined.
 */
public final class LivingEntityHeadClientAudit {
	public static final String SYSTEM_PROPERTY = "flex_hud.auditMobHeads";

	private static boolean hasRun;

	private LivingEntityHeadClientAudit() {
	}

	public static void runIfEnabled(Minecraft minecraft) {
		if (hasRun || !Boolean.getBoolean(SYSTEM_PROPERTY)) {
			return;
		}

		hasRun = true;
		run(minecraft);
	}

	static void run(Minecraft minecraft) {
		if (minecraft.level == null) {
			throw new IllegalStateException("The mob head audit requires a loaded client world");
		}

		List<String> missingHeads = new ArrayList<>();
		List<String> uncreatableEntityTypes = new ArrayList<>();
		int checkedMobs = 0;
		int nextAuditEntityId = -1;

		for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
			Identifier entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
			if (!Identifier.DEFAULT_NAMESPACE.equals(entityTypeId.getNamespace())) {
				continue;
			}

			Entity entity;
			try {
				entity = entityType.create(minecraft.level, EntitySpawnReason.COMMAND);
			} catch (Exception exception) {
				uncreatableEntityTypes.add(entityTypeId + " (" + exception.getClass().getSimpleName() + ")");
				continue;
			}

			if (!(entity instanceof Mob mob)) {
				continue;
			}

			mob.setId(nextAuditEntityId--);
			checkedMobs++;
			Identifier sprite = LivingEntitiesTickable.getMobHeadTexture(mob);
			if (sprite == null || LivingEntitiesTickable.spriteDoesNotExist(sprite)) {
				missingHeads.add(entityTypeId + " -> " + (sprite == null ? "no sprite" : sprite));
			}
		}

		if (checkedMobs == 0) {
			throw new IllegalStateException("Mob head client audit did not create any vanilla mob");
		}
		if (!missingHeads.isEmpty()) {
			throw new IllegalStateException(buildFailureMessage(missingHeads));
		}
		if (!uncreatableEntityTypes.isEmpty()) {
			FlexHudLogger.warn(
					"Mob head client audit skipped entity factories that cannot be invoked directly:\n - {}",
					String.join("\n - ", uncreatableEntityTypes)
			);
		}

		FlexHudLogger.info("Mob head client audit succeeded: {} vanilla mobs checked.", checkedMobs);
	}

	private static String buildFailureMessage(List<String> missingHeads) {
		StringBuilder message = new StringBuilder("Mob head client audit failed.");
		appendSection(message, "Missing head sprites", missingHeads);
		return message.toString();
	}

	private static void appendSection(StringBuilder message, String title, List<String> entries) {
		if (entries.isEmpty()) {
			return;
		}

		message.append('\n').append(title).append(':');
		for (String entry : entries) {
			message.append("\n - ").append(entry);
		}
	}
}
