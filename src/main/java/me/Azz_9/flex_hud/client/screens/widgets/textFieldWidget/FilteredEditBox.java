package me.Azz_9.flex_hud.client.screens.widgets.textFieldWidget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.function.Predicate;

public class FilteredEditBox extends EditBox {

	private Predicate<String> filter;

	public FilteredEditBox(Font font, int width, int height, Component narration) {
		super(font, width, height, narration);
	}

	protected void setFilter(final Predicate<String> filter) {
		this.filter = filter;
	}

	@Override
	public void setValue(@NonNull String value) {
		if (filter.test(value)) {
			super.setValue(value);
		}
	}
}
