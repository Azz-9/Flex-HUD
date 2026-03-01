package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import org.jspecify.annotations.Nullable;

import me.Azz_9.flex_hud.client.customModules.CustomModuleRegistry;
import me.Azz_9.flex_hud.client.screens.TrackableChange;

public class ModuleNameField extends EditBox implements TrackableChange {

	private final @Nullable String INITIAL_CONTENT;
	private final @Nullable String INITIAL_ID;

	public ModuleNameField(int x, int y, int width, int height, @Nullable String initialContent) {
		super(MINECRAFT.font, x, y, width, height, Component.empty());
		this.INITIAL_CONTENT = initialContent;
		this.INITIAL_ID = initialContent == null ? null : CustomModuleRegistry.nameToId(initialContent.strip());
		setValue(initialContent);
	}


	@Override
	public boolean hasChanged() {
		return INITIAL_CONTENT == null ? !getValue().isEmpty() : !getValue().equals(INITIAL_CONTENT);
	}

	@Override
	public boolean isValid() {
		return !getValue().isBlank() && !isAlreadyRegistered();
	}

	@Override
	public void cancel() {
		if (INITIAL_CONTENT != null) {
			setValue(INITIAL_CONTENT);
		}
	}

	public boolean isAlreadyRegistered() {
		String currentId = CustomModuleRegistry.nameToId(getValue().strip());
		return !currentId.equals(INITIAL_ID) && CustomModuleRegistry.isRegistered(currentId);
	}
}
