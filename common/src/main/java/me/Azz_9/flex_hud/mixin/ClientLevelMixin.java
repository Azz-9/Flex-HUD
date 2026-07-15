package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

import org.spongepowered.asm.mixin.Mixin;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.notHud.WeatherChanger;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {

	protected ClientLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
		super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
	}

	@Override
	public float getRainLevel(float delta) {
		if (Modules.getInstance().isEnabled.getValue() &&
				Modules.getInstance().weatherChanger.enabled.getValue()) {

			if (Modules.getInstance().weatherChanger.selectedWeather.getValue().equals(WeatherChanger.Weather.CLEAR)) {
				return 0f;

			} else return 1f;
		}
		return super.getRainLevel(delta);
	}

	@Override
	public float getThunderLevel(float delta) {
		if (Modules.getInstance().isEnabled.getValue() &&
				Modules.getInstance().weatherChanger.enabled.getValue()) {

			if (Modules.getInstance().weatherChanger.selectedWeather.getValue().equals(WeatherChanger.Weather.THUNDER)) {
				return 1f;

			} else return 0f;
		}
		return super.getThunderLevel(delta);
	}
}