package me.Azz_9.flex_hud.client.gui.components.config.colorSelector;

public interface ColorBindable {
	void onReceiveColor(int color);

	int getColor();

	int getRight();

	int getY();

	int getBottom();
}
