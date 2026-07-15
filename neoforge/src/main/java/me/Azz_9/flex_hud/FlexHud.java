package me.Azz_9.flex_hud;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import me.Azz_9.flex_hud.client.gui.screens.OptionsScreen;
import me.Azz_9.flex_hud.platform.NeoForgePlatformHelper;

@Mod(Constants.MOD_ID)
public class FlexHud {

	public FlexHud(IEventBus eventBus) {

		// This method is invoked by the NeoForge mod loader when it is ready
		// to load your mod. You can access NeoForge and Common code in this
		// project.

		// Use NeoForge to bootstrap the Common mod.

		NeoForgePlatformHelper.eventBus = eventBus;

		eventBus.addListener((RegisterGuiLayersEvent event) -> System.out.println("too late"));

		CommonClass.init();
		ModLoadingContext.get().registerExtensionPoint(
				IConfigScreenFactory.class,
				() -> (container, parent) -> new OptionsScreen(parent)
		);
	}
}