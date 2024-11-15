package me.Azz_9.better_hud;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Better_hud implements ModInitializer {

    public static final String MOD_ID = "better_hud";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        LOGGER.info("Better HUD has started up.");

    }
}
