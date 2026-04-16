package me.Azz_9.flex_hud.client.mixin.weatherChanger;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.WeatherChanger;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin extends Level {

	protected ClientWorldMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
		super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
	}

	@Override
	public float getRainLevel(float delta) {
		if (ModulesHelper.getInstance().isEnabled.getValue() &&
				ModulesHelper.getInstance().weatherChanger.enabled.getValue()) {

			if (ModulesHelper.getInstance().weatherChanger.selectedWeather.getValue().equals(WeatherChanger.Weather.CLEAR)) {
				return 0f;

			} else return 1f;
		}
		return super.getRainLevel(delta);
	}

	@Override
	public float getThunderLevel(float delta) {
		if (ModulesHelper.getInstance().isEnabled.getValue() &&
				ModulesHelper.getInstance().weatherChanger.enabled.getValue()) {

			if (ModulesHelper.getInstance().weatherChanger.selectedWeather.getValue().equals(WeatherChanger.Weather.THUNDER)) {
				return 1f;
				
			} else return 0f;
		}
		return super.getThunderLevel(delta);
	}
}