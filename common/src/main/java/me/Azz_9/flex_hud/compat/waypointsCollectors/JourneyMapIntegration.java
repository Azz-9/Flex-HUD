package me.Azz_9.flex_hud.compat.waypointsCollectors;

import org.jetbrains.annotations.NotNull;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.JourneyMapPlugin;
import me.Azz_9.flex_hud.Constants;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class JourneyMapIntegration implements IClientPlugin {

	private static IClientAPI api;

	@Override
	public String getModId() {
		return Constants.MOD_ID;
	}

	@Override
	public void initialize(@NotNull IClientAPI clientAPI) {
		api = clientAPI;
	}

	public static IClientAPI getAPI() {
		return api;
	}
}