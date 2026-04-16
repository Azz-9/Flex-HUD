package me.Azz_9.flex_hud.compat.waypointsCollectors;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.common.JourneyMapPlugin;
import me.Azz_9.flex_hud.client.Flex_hudClient;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class JourneyMapIntegration implements IClientPlugin {

	private static IClientAPI api;

	@Override
	public String getModId() {
		return Flex_hudClient.MOD_ID;
	}

	@Override
	public void initialize(IClientAPI clientAPI) {
		api = clientAPI;
	}

	public static IClientAPI getAPI() {
		return api;
	}
}