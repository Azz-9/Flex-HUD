package me.Azz_9.flex_hud.client.mixin.weatherChanger;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.WeatherChanger;
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
		if (ModulesHelper.getInstance().isEnabled.getValue() && ModulesHelper.getInstance().weatherChanger.enabled.getValue()) {
			if (ModulesHelper.getInstance().weatherChanger.selectedWeather.getValue().equals(WeatherChanger.Weather.CLEAR)) {
				return 0f;
			} else return 1f;
		}
		return super.getRainGradient(delta);
	}

	@Override
	public float getThunderGradient(float delta) {
		if (ModulesHelper.getInstance().isEnabled.getValue() && ModulesHelper.getInstance().weatherChanger.enabled.getValue()) {
			if (ModulesHelper.getInstance().weatherChanger.selectedWeather.getValue().equals(WeatherChanger.Weather.THUNDER)) {
				return 1f;
			} else return 0f;
		}
		return super.getThunderGradient(delta);
	}
}