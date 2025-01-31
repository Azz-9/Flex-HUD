package me.Azz_9.better_hud.Screens.ModsList;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class Feature {
	public String name;
	public String id;
	public Identifier icon;
	public Runnable onClick;
	public ButtonWidget button;

	public Feature(String name, String id, Runnable onClick, int buttonWidth, int buttonHeight) {
		if (name == null) {
			this.setAllNull();
		} else {
			this.name = name;
			this.id = id;
			this.icon = Identifier.of(MOD_ID, "mods_icons/" + id + ".png");
			this.onClick = onClick;
			this.button = ButtonWidget.builder(Text.literal(name), (btn) -> onClick.run())
					.size(buttonWidth, buttonHeight)
					.build();
		}
	}

	private void setAllNull() {
		this.name = null;
		this.id = null;
		this.icon = null;
		this.onClick = null;
		this.button = null;
	}

	public boolean exists() {
		return this.name != null;
	}

	@Override
	public String toString() {
		return "Feature{" +
				"name='" + name + '\'' +
				", id='" + id + '\'' +
				", icon=" + icon +
				", onClick=" + onClick +
				'}';
	}

}