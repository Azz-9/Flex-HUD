package me.Azz_9.flex_hud.client;

import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = "flex_hud")
public class Flex_hudForge {
	public Flex_hudForge() {
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		Flex_hud.init();
	}
}
