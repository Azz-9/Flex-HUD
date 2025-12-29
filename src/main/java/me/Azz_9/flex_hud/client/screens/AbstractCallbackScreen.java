package me.Azz_9.flex_hud.client.screens;

import me.Azz_9.flex_hud.client.configurableModules.ConfigLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCallbackScreen extends AbstractBackNavigableScreen {
	private final Component MESSAGE_TITLE;
	private final Component MESSAGE_CONTENT;
	private Button callbackDiscardButton;
	private Button callbackCancelButton;
	private Button cancelButton;
	private Button saveButton;

	private boolean callbackScreen;
	private final List<TrackableChange> trackableWidgets;

	protected AbstractCallbackScreen(Component title, Screen parent, Component callbackTitle, Component callbackContent) {
		super(title, parent);
		MESSAGE_TITLE = callbackTitle;
		MESSAGE_CONTENT = callbackContent;
		trackableWidgets = new ArrayList<>();
	}

	@Override
	protected void init() {
		int callbackButtonsWidth = 190;
		int callbackButtonsHeight = 20;
		int callbackButtonsGap = 10;
		callbackDiscardButton = Button.builder(Component.translatable("flex_hud.global.config.quit_and_discard"), (btn) -> this.cancel())
				.bounds(
						this.width / 2 - callbackButtonsWidth - callbackButtonsGap / 2,
						this.height / 2,
						callbackButtonsWidth,
						callbackButtonsHeight
				).build();
		callbackDiscardButton.active = callbackScreen;

		callbackCancelButton = Button.builder(Component.translatable("flex_hud.global.config.cancel"), (btn) -> setCallbackScreen(false))
				.bounds(
						this.width / 2 + callbackButtonsGap / 2,
						this.height / 2,
						callbackButtonsWidth,
						callbackButtonsHeight
				).build();
		callbackCancelButton.active = callbackScreen;


		int buttonsWidth = 160;
		int buttonsHeight = 20;
		int buttonsGap = 10;
		cancelButton = Button.builder(Component.translatable("flex_hud.global.config.cancel"), (btn) -> onCancelButtonClick())
				.bounds(
						this.width / 2 - buttonsWidth - buttonsGap / 2,
						this.height - 30,
						buttonsWidth,
						buttonsHeight
				).build();
		if (callbackScreen) cancelButton.active = false;

		saveButton = Button.builder(Component.translatable("flex_hud.global.config.save_and_quit"), (btn) -> saveAndClose())
				.bounds(
						this.width / 2 + buttonsGap / 2,
						this.height - 30,
						buttonsWidth,
						buttonsHeight
				).build();
		if (callbackScreen) saveButton.active = false;
		else updateSaveButton();


		this.addRenderableWidget(callbackDiscardButton);
		this.addRenderableWidget(callbackCancelButton);
		this.addRenderableWidget(cancelButton);
		this.addRenderableWidget(saveButton);
	}

	protected final boolean renderCallback(GuiGraphics context, int mouseX, int mouseY, float delta) {
		if (!callbackScreen) {
			cancelButton.render(context, mouseX, mouseY, delta);
			saveButton.render(context, mouseX, mouseY, delta);

			return false;
		} else {
			callbackDiscardButton.render(context, mouseX, mouseY, delta);
			callbackCancelButton.render(context, mouseX, mouseY, delta);

			int textColor = 0xffffffff;
			context.drawCenteredString(font, MESSAGE_TITLE, this.width / 2, this.height / 2 - 42, textColor);
			context.drawCenteredString(font, MESSAGE_CONTENT, this.width / 2, this.height / 2 - 30, textColor);

			return true;
		}
	}

	@Override
	public boolean keyPressed(@NonNull KeyEvent input) {
		if (shouldCloseOnEsc() && input.key() == GLFW.GLFW_KEY_ESCAPE) {
			if (callbackScreen) {
				setCallbackScreen(false);
			} else {
				onCancelButtonClick();
			}
			return true;
		}
		return super.keyPressed(input);
	}

	protected void cancel() {
		trackableWidgets.forEach(TrackableChange::cancel);
		onClose();
	}

	protected void saveAndClose() {
		ConfigLoader.saveConfig();
		onClose();
	}

	protected void onCancelButtonClick() {
		if (!Minecraft.getInstance().hasShiftDown() && trackableWidgets.stream().anyMatch(TrackableChange::hasChanged)) {
			setCallbackScreen(true);
		} else {
			cancel();
		}
	}

	protected void registerTrackableWidget(TrackableChange widget) {
		trackableWidgets.add(widget);
	}

	protected void unregisterTrackableWidget(TrackableChange widget) {
		trackableWidgets.remove(widget);
	}

	public List<TrackableChange> getTrackableWidgets() {
		return trackableWidgets;
	}

	public boolean isCallbackScreen() {
		return callbackScreen;
	}

	public void setCallbackScreen(boolean callbackScreen) {
		this.callbackScreen = callbackScreen;
		callbackDiscardButton.active = callbackScreen;
		callbackCancelButton.active = callbackScreen;
		if (callbackScreen) {
			disableAllChildren();
		} else {
			enableAllChildren();
		}
	}

	public void setSaveButtonActive(boolean active) {
		saveButton.active = active;
	}

	public void setCancelAndSaveButtonsVisibility(boolean visible) {
		cancelButton.visible = visible;
		saveButton.visible = visible;
	}

	public void updateSaveButton() {
		boolean foundAChange = false;
		for (TrackableChange widget : getTrackableWidgets()) {
			if (widget.hasChanged()) {
				foundAChange = true;
			}
			if (!widget.isValid()) {
				saveButton.active = false;
				return;
			}
		}
		saveButton.active = foundAChange;
	}

	protected void disableAllChildren() {
		cancelButton.active = false;
		saveButton.active = false;
	}

	protected void enableAllChildren() {
		cancelButton.active = true;
		updateSaveButton();
	}
}
