package me.Azz_9.better_hud.screens;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCallbackScreen extends Screen {
	private final ButtonWidget DISCARD_BUTTON;
	private final ButtonWidget CANCEL_BUTTON;
	private boolean callbackScreen = false;

	private final Text MESSAGE_TITLE;
	private final Text MESSAGE_CONTENT;

	private final List<TrackableChange> trackableWidgets = new ArrayList<>();

	protected AbstractCallbackScreen(Text title, Text callbackTitle, Text callbackContent) {
		super(title);
		this.MESSAGE_TITLE = callbackTitle;
		this.MESSAGE_CONTENT = callbackContent;

		Window window = MinecraftClient.getInstance().getWindow();

		DISCARD_BUTTON = ButtonWidget.builder(Text.translatable("better_hud.global.config.quit_and_discard"), (btn) -> this.cancel())
				.dimensions(window.getScaledWidth() / 2 - 195, window.getScaledHeight() / 2, 190, 20)
				.build();
		CANCEL_BUTTON = ButtonWidget.builder(Text.translatable("better_hud.global.config.cancel"), (btn) -> setCallbackScreen(false))
				.dimensions(window.getScaledWidth() / 2 + 5, window.getScaledHeight() / 2, 190, 20)
				.build();

		DISCARD_BUTTON.active = false;
		CANCEL_BUTTON.active = false;

		super.addDrawableChild(DISCARD_BUTTON);
		super.addDrawableChild(CANCEL_BUTTON);
	}

	protected boolean renderCallback(DrawContext context, int mouseX, int mouseY, float delta) {
		if (callbackScreen) {
			super.renderBackground(context, mouseX, mouseY, delta);

			DISCARD_BUTTON.render(context, mouseX, mouseY, delta);
			CANCEL_BUTTON.render(context, mouseX, mouseY, delta);

			context.drawCenteredTextWithShadow(textRenderer, MESSAGE_TITLE, this.width / 2, this.height / 2 - 42, 0xffffff);
			context.drawCenteredTextWithShadow(textRenderer, MESSAGE_CONTENT, this.width / 2, this.height / 2 - 30, 0xffffff);
			return true;
		}
		return false;
	}

	protected void cancel() {
		trackableWidgets.forEach(TrackableChange::cancel);
		close();
	}

	public void onCancelButtonClick() {
		if (trackableWidgets.stream().anyMatch(TrackableChange::hasChanged)) {
			setCallbackScreen(true);
		} else {
			cancel();
		}
	}

	public List<TrackableChange> getTrackableWidgets() {
		return trackableWidgets;
	}

	protected void registerTrackableWidget(TrackableChange widget) {
		trackableWidgets.add(widget);
	}

	protected void unregisterTrackableWidget(TrackableChange widget) {
		trackableWidgets.remove(widget);
	}

	public boolean isCallbackScreen() {
		return callbackScreen;
	}

	public void setCallbackScreen(boolean callbackScreen) {
		this.callbackScreen = callbackScreen;
		DISCARD_BUTTON.active = callbackScreen;
		CANCEL_BUTTON.active = callbackScreen;
	}
}
