package me.Azz_9.better_hud.client.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public abstract class AbstractCallbackScreen extends AbstractBackNavigableScreen {
	private final Text MESSAGE_TITLE;
	private final Text MESSAGE_CONTENT;
	private final ButtonWidget CALLBACK_DISCARD_BUTTON;
	private final ButtonWidget CALLBACK_CANCEL_BUTTON;
	private final ButtonWidget SAVE_BUTTON;
	private final ButtonWidget CANCEL_BUTTON;

	private boolean callbackScreen;
	private List<TrackableChange> trackableWidgets;

	protected AbstractCallbackScreen(Text title, Screen parent, Text callbackTitle, Text callbackContent) {
		super(title, parent);
		MESSAGE_TITLE = callbackTitle;
		MESSAGE_CONTENT = callbackContent;

		int callbackButtonsWidth = 190;
		int callbackButtonsHeight = 20;
		int callbackButtonsGap = 10;
		CALLBACK_DISCARD_BUTTON = ButtonWidget.builder(Text.translatable("better_hud.global.config.quit_and_discard"), (btn) -> this.cancel())
				.dimensions(
						this.width / 2 - callbackButtonsWidth - callbackButtonsGap / 2,
						this.height / 2,
						callbackButtonsWidth,
						callbackButtonsHeight
				).build();
		CALLBACK_DISCARD_BUTTON.active = false;

		CALLBACK_CANCEL_BUTTON = ButtonWidget.builder(Text.translatable("better_hud.global.config.cancel"), (btn) -> setCallbackScreen(false))
				.dimensions(
						this.width / 2 + callbackButtonsGap / 2,
						this.height / 2,
						callbackButtonsWidth,
						callbackButtonsHeight
				).build();
		CALLBACK_CANCEL_BUTTON.active = false;

		int buttonsWidth = 160;
		int buttonsHeight = 20;
		int buttonsGap = 10;
		CANCEL_BUTTON = ButtonWidget.builder(Text.translatable("better_hud.global.config.cancel"), (btn) -> onCancelButtonClick())
				.dimensions(
						this.width / 2 - buttonsWidth - buttonsGap / 2,
						this.height - 30,
						buttonsWidth,
						buttonsHeight
				).build();

		SAVE_BUTTON = ButtonWidget.builder(Text.translatable("better_hud.global.config.save_and_quit"), (btn) -> saveAndClose())
				.dimensions(
						this.width / 2 + buttonsGap / 2,
						this.height - 30,
						buttonsWidth,
						buttonsHeight
				).build();

		this.addDrawableChild(CALLBACK_DISCARD_BUTTON);
		this.addDrawableChild(CALLBACK_CANCEL_BUTTON);
		this.addDrawableChild(CANCEL_BUTTON);
		this.addDrawableChild(SAVE_BUTTON);
	}

	protected boolean renderCallback(DrawContext context, int mouseX, int mouseY, float delta) {
		if (callbackScreen) {
			super.renderBackground(context, mouseX, mouseY, delta);

			CALLBACK_DISCARD_BUTTON.render(context, mouseX, mouseY, delta);
			CALLBACK_CANCEL_BUTTON.render(context, mouseX, mouseY, delta);

			int textColor = 0xffffff;
			context.drawCenteredTextWithShadow(textRenderer, MESSAGE_TITLE, this.width / 2, this.height / 2 - 42, textColor);
			context.drawCenteredTextWithShadow(textRenderer, MESSAGE_CONTENT, this.width / 2, this.height / 2 - 30, textColor);
			return true;
		}
		return false;
	}

	protected void cancel() {
		trackableWidgets.forEach(TrackableChange::cancel);
		close();
	}

	protected void saveAndClose() {
		//TODO JsonHelper.saveConfig();
		close();
	}

	protected void onCancelButtonClick() {
		if (trackableWidgets.stream().anyMatch(TrackableChange::hasChanged)) {
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
		CALLBACK_DISCARD_BUTTON.active = callbackScreen;
		CALLBACK_CANCEL_BUTTON.active = callbackScreen;
	}
}
