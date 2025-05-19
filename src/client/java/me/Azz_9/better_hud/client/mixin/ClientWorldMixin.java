package me.Azz_9.better_hud.client.mixin;

import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableMods.mods.notHud.WeatherChanger;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {

	protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
	}

	@Override
	public float getRainGradient(float delta) {
		if (JsonConfigHelper.getInstance().isEnabled && JsonConfigHelper.getInstance().weatherChanger.enabled) {
			if (JsonConfigHelper.getInstance().weatherChanger.selectedWeather.equals(WeatherChanger.Weather.CLEAR)) {
				return 0f;
			} else return 1f;
		}
		return super.getRainGradient(delta);
	}

	@Override
	public float getThunderGradient(float delta) {
		if (JsonConfigHelper.getInstance().isEnabled && JsonConfigHelper.getInstance().weatherChanger.enabled) {
			if (JsonConfigHelper.getInstance().weatherChanger.selectedWeather.equals(WeatherChanger.Weather.THUNDER)) {
				return 1f;
			} else return 0f;
		}
		return super.getRainGradient(delta);
	}
}