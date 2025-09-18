package me.Azz_9.flex_hud.client.screens;

import me.Azz_9.flex_hud.client.configurableModules.JsonConfigHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCallbackScreen extends AbstractBackNavigableScreen {
	private final Text MESSAGE_TITLE;
	private final Text MESSAGE_CONTENT;
	private ButtonWidget callbackDiscardButton;
	private ButtonWidget callbackCancelButton;
	private ButtonWidget cancelButton;
	private ButtonWidget saveButton;

	private boolean callbackScreen;
	private final List<TrackableChange> trackableWidgets;

	protected AbstractCallbackScreen(Text title, Screen parent, Text callbackTitle, Text callbackContent) {
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
		callbackDiscardButton = ButtonWidget.builder(Text.translatable("flex_hud.global.config.quit_and_discard"), (btn) -> this.cancel())
				.dimensions(
						this.width / 2 - callbackButtonsWidth - callbackButtonsGap / 2,
						this.height / 2,
						callbackButtonsWidth,
						callbackButtonsHeight
				).build();
		callbackDiscardButton.active = false;

		callbackCancelButton = ButtonWidget.builder(Text.translatable("flex_hud.global.config.cancel"), (btn) -> setCallbackScreen(false))
				.dimensions(
						this.width / 2 + callbackButtonsGap / 2,
						this.height / 2,
						callbackButtonsWidth,
						callbackButtonsHeight
				).build();
		callbackCancelButton.active = false;


		int buttonsWidth = 160;
		int buttonsHeight = 20;
		int buttonsGap = 10;
		cancelButton = ButtonWidget.builder(Text.translatable("flex_hud.global.config.cancel"), (btn) -> onCancelButtonClick())
				.dimensions(
						this.width / 2 - buttonsWidth - buttonsGap / 2,
						this.height - 30,
						buttonsWidth,
						buttonsHeight
				).build();

		saveButton = ButtonWidget.builder(Text.translatable("flex_hud.global.config.save_and_quit"), (btn) -> saveAndClose())
				.dimensions(
						this.width / 2 + buttonsGap / 2,
						this.height - 30,
						buttonsWidth,
						buttonsHeight
				).build();
		updateSaveButton();


		this.addDrawableChild(callbackDiscardButton);
		this.addDrawableChild(callbackCancelButton);
		this.addDrawableChild(cancelButton);
		this.addDrawableChild(saveButton);
	}

	protected final boolean renderCallback(DrawContext context, int mouseX, int mouseY, float delta) {
		if (!callbackScreen) {
			cancelButton.render(context, mouseX, mouseY, delta);
			saveButton.render(context, mouseX, mouseY, delta);

			return false;
		} else {
			callbackDiscardButton.render(context, mouseX, mouseY, delta);
			callbackCancelButton.render(context, mouseX, mouseY, delta);

			int textColor = 0xffffffff;
			context.drawCenteredTextWithShadow(textRenderer, MESSAGE_TITLE, this.width / 2, this.height / 2 - 42, textColor);
			context.drawCenteredTextWithShadow(textRenderer, MESSAGE_CONTENT, this.width / 2, this.height / 2 - 30, textColor);

			return true;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (shouldCloseOnEsc() && keyCode == GLFW.GLFW_KEY_ESCAPE) {
			if (callbackScreen) {
				setCallbackScreen(false);
			} else {
				onCancelButtonClick();
			}
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	protected void cancel() {
		trackableWidgets.forEach(TrackableChange::cancel);
		close();
	}

	protected void saveAndClose() {
		JsonConfigHelper.saveConfig();
		close();
	}

	protected void onCancelButtonClick() {
		if (!Screen.hasShiftDown() && trackableWidgets.stream().anyMatch(TrackableChange::hasChanged)) {
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
