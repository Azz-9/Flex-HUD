package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import org.jspecify.annotations.Nullable;

import me.Azz_9.flex_hud.client.customModules.CustomModuleRegistry;
import me.Azz_9.flex_hud.client.screens.TrackableChange;

public class ModuleNameField extends TextFieldWidget implements TrackableChange {

	private final @Nullable String INITIAL_CONTENT;

	public ModuleNameField(int x, int y, int width, int height, @Nullable String initialContent) {
		super(CLIENT.textRenderer, x, y, width, height, Text.empty());
		this.INITIAL_CONTENT = initialContent;
	}


	@Override
	public boolean hasChanged() {
		return INITIAL_CONTENT == null ? !getText().isEmpty() : !getText().equals(INITIAL_CONTENT);
	}

	@Override
	public boolean isValid() {
		return !getText().isEmpty() && CustomModuleRegistry.isRegistered(getText());
	}

	@Override
	public void cancel() {
		if (INITIAL_CONTENT != null) {
			setText(INITIAL_CONTENT);
		}
	}
}
