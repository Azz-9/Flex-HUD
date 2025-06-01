package me.Azz_9.better_hud.client.screens.configurationScreen;

import me.Azz_9.better_hud.client.screens.modsList.DataGetter;

public interface Observer {
	void onChange(DataGetter<?> dataGetter);
}
