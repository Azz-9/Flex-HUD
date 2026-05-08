package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import org.jspecify.annotations.Nullable;

import me.Azz_9.flex_hud.client.customModules.CustomModuleRegistry;
import me.Azz_9.flex_hud.client.screens.TrackableChange;

public class ModuleNameField extends TextFieldWidget implements TrackableChange {

	private final @Nullable String INITIAL_CONTENT;
	private final @Nullable String INITIAL_ID;

	public ModuleNameField(int x, int y, int width, int height, @Nullable String initialContent) {
		super(CLIENT.textRenderer, x, y, width, height, Text.empty());
		this.INITIAL_CONTENT = initialContent;
		this.INITIAL_ID = initialContent == null ? null : CustomModuleRegistry.nameToId(initialContent.strip());
		setText(initialContent);
	}


	@Override
	public boolean hasChanged() {
		return INITIAL_CONTENT == null ? !getText().isEmpty() : !getText().equals(INITIAL_CONTENT);
	}

	@Override
	public boolean isValid() {
		return !getText().isBlank() && !isAlreadyRegistered();
	}

	@Override
	public void cancel() {
		if (INITIAL_CONTENT != null) {
			setText(INITIAL_CONTENT);
		}
	}

	public boolean isAlreadyRegistered() {
		String currentId = CustomModuleRegistry.nameToId(getText().strip());
		return !currentId.equals(INITIAL_ID) && CustomModuleRegistry.isRegistered(currentId);
	}
}
