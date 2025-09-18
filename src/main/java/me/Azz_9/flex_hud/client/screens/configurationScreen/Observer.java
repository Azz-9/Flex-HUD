package me.Azz_9.flex_hud.client.screens.configurationScreen;

import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;

public interface Observer {
	void onChange(DataGetter<?> dataGetter);
}
