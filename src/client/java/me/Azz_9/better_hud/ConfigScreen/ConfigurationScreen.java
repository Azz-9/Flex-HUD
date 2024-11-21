package me.Azz_9.better_hud.ConfigScreen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigurationScreen extends Screen {
	protected ConfigurationScreen(Text title) {
		super(title);
	}

	private final List<String> modsList = new ArrayList<>() {{
		add("Coordinates");
		add("FPS");
		add("Clock");
		add("Armor status");
		add("Direction");
		add("Day Counter");
		add("Ping category");
		add("Server address");
		add("Weather changer (HS)");
		add("Memory usage");
		add("CPS (HS)");
		add("Time changer (HS)");
		add("Durability ping");
		add("Speedometer");
		add("Reach");
		add("Combo (HS)");
		add("playtime");
		add("stopwatch");
	}};

	@Override
	protected void init() {

		final int buttonWidth = 120;
		final int buttonHeight = 20;
		final int padding = 10;
		final int columns = 4;
		int baseY = 150;

		for (int i = 0; i < modsList.size(); i++) {
			String modName = modsList.get(i);
			int column = i % columns;
			int row = i / columns;

			int x = width / 2 - (columns * (buttonWidth + padding) - padding) / 2 + column * (buttonWidth + padding);
			int y = baseY + row * (buttonHeight + padding) + padding / 2;
			if (y >= -buttonHeight && y <= height) {
				ButtonWidget modButton = ButtonWidget.builder(Text.of(modName), (btn) -> {
					System.out.println("Bonjour " + modName);
				}).dimensions(x, y, buttonWidth, buttonHeight).build();

				this.addDrawableChild(modButton);
			}
		}

	}

}