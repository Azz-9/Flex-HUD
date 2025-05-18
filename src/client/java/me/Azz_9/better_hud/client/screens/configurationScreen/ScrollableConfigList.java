package me.Azz_9.better_hud.client.screens.configurationScreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;

import java.util.List;

public class ScrollableConfigList extends ElementListWidget<ScrollableConfigList.Entry> {
	//TODO

	public ScrollableConfigList(MinecraftClient minecraftClient, int i, int j, int k, int l) {
		super(minecraftClient, i, j, k, l);
	}

	public ScrollableConfigList(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	public static class Entry extends ElementListWidget.Entry<Entry> {
		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {

		}

		@Override
		public List<? extends Element> children() {
			return List.of();
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return List.of();
		}
	}
}
