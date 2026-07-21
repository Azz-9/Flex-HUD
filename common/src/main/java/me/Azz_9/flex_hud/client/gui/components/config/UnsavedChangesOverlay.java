package me.Azz_9.flex_hud.client.gui.components.config;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.gui.Colors;

/**
 * An overlay rendered on top of the settings screen when the user tries to close
 * with unsaved changes.
 *
 * <p>While the overlay is active:</p>
 * <ul>
 *   <li>The background (settings screen content) is darkened.</li>
 *   <li>All normal screen interactions are blocked — mouse events are consumed
 *       before reaching widgets behind.</li>
 *   <li>Tooltips from the underlying screen are suppressed (the overlay sits above
 *       all other rendering, so hovering rules naturally won't trigger).</li>
 * </ul>
 *
 * <p>The overlay presents two buttons:</p>
 * <ul>
 *   <li><b>Cancel</b> — dismiss the overlay and return to editing.</li>
 *   <li><b>Quit and discard changes</b> — revert all options and close the screen.</li>
 * </ul>
 */
public final class UnsavedChangesOverlay extends AbstractWidget implements Popup {

	// -------------------------------------------------------------------------
	// Layout
	// -------------------------------------------------------------------------

	private static final int DIALOG_WIDTH = 280;
	private static final int DIALOG_HEIGHT = 100;
	private static final int BUTTON_WIDTH = 120;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 8;

	// -------------------------------------------------------------------------
	// Callbacks
	// -------------------------------------------------------------------------

	private final @NotNull Runnable onCancel;
	private final @NotNull Runnable onDiscardAndQuit;

	// -------------------------------------------------------------------------
	// Child buttons
	// -------------------------------------------------------------------------

	private final @NotNull Button cancelButton;
	private final @NotNull Button discardButton;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	/**
	 * @param screenWidth      full screen width (overlay covers the whole screen)
	 * @param screenHeight     full screen height
	 * @param onCancel         called when the user dismisses the overlay
	 * @param onDiscardAndQuit called when the user confirms quitting without saving
	 */
	public UnsavedChangesOverlay(
			int screenWidth,
			int screenHeight,
			@NotNull Runnable onCancel,
			@NotNull Runnable onDiscardAndQuit
	) {
		super(0, 0, screenWidth, screenHeight, Component.empty());
		this.onCancel = onCancel;
		this.onDiscardAndQuit = onDiscardAndQuit;

		int dialogX = (screenWidth - DIALOG_WIDTH) / 2;
		int dialogY = (screenHeight - DIALOG_HEIGHT) / 2;

		int buttonsY = dialogY + DIALOG_HEIGHT - BUTTON_HEIGHT - 12;
		int totalBtnW = BUTTON_WIDTH * 2 + BUTTON_GAP;
		int btnStartX = dialogX + (DIALOG_WIDTH - totalBtnW) / 2;

		cancelButton = Button.builder(
						CommonComponents.GUI_CANCEL,
						btn -> onCancel.run())
				.pos(btnStartX, buttonsY)
				.size(BUTTON_WIDTH, BUTTON_HEIGHT)
				.build();

		discardButton = Button.builder(
						Component.translatable("flex_hud.global.config.quit_and_discard"),
						btn -> onDiscardAndQuit.run())
				.pos(btnStartX + BUTTON_WIDTH + BUTTON_GAP, buttonsY)
				.size(BUTTON_WIDTH, BUTTON_HEIGHT)
				.build();
	}

	// -------------------------------------------------------------------------
	// Rendering
	// -------------------------------------------------------------------------


	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		// Full-screen dark veil
		graphics.fill(0, 0, getWidth(), getHeight(), Colors.BLACK_TRANSPARENT);

		int dialogX = (getWidth() - DIALOG_WIDTH) / 2;
		int dialogY = (getHeight() - DIALOG_HEIGHT) / 2;

		// Dialog background
		graphics.fill(dialogX, dialogY, dialogX + DIALOG_WIDTH, dialogY + DIALOG_HEIGHT, Colors.DARK_GRAY);
		graphics.renderOutline(dialogX, dialogY, DIALOG_WIDTH, DIALOG_HEIGHT, Colors.GRAY);

		// Title
		Component title = Component.translatable("flex_hud.global.config.callback.message_title");
		int titleW = MINECRAFT.font.width(title);
		graphics.drawString(MINECRAFT.font, title,
				dialogX + (DIALOG_WIDTH - titleW) / 2, dialogY + 14, Colors.WHITE, true);

		// Body
		Component body = Component.translatable("flex_hud.global.config.callback.message_content");
		int bodyX = dialogX + 12;
		int bodyY = dialogY + 34;
		graphics.drawWordWrap(MINECRAFT.font, body, bodyX, bodyY, DIALOG_WIDTH - 24, Colors.LIGHT_GRAY);

		// Buttons
		cancelButton.render(graphics, mouseX, mouseY, deltaTicks);
		discardButton.render(graphics, mouseX, mouseY, deltaTicks);
	}

	// -------------------------------------------------------------------------
	// Input — consume everything so nothing behind is clickable
	// -------------------------------------------------------------------------


	@Override
	public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
		cancelButton.mouseClicked(event, doubleClick);
		discardButton.mouseClicked(event, doubleClick);
		return true; // always consume
	}

	@Override
	public boolean mouseReleased(@NotNull MouseButtonEvent event) {
		cancelButton.mouseReleased(event);
		discardButton.mouseReleased(event);
		return true;
	}

	@Override
	public boolean mouseDragged(@NotNull MouseButtonEvent event, double dx, double dy) {
		return true; // consume
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double hAmount, double vAmount) {
		return true; // consume
	}

	@Override
	public boolean keyPressed(@NotNull KeyEvent event) {
		if (event.isEscape()) {
			onCancel.run();
			return true;
		}
		return true; // consume all keys
	}

	@Override
	public boolean charTyped(@NotNull CharacterEvent event) {
		return true; // consume
	}

	@Override
	public void updateWidgetNarration(@NotNull NarrationElementOutput output) {
		output.add(NarratedElementType.TITLE, Component.translatable("flex_hud.global.config.callback.message_title"));
	}

	// -------------------------------------------------------------------------
	// Resize support
	// -------------------------------------------------------------------------

	public void resize(int newScreenWidth, int newScreenHeight) {
		this.width = newScreenWidth;
		this.height = newScreenHeight;

		int dialogX = (newScreenWidth - DIALOG_WIDTH) / 2;
		int dialogY = (newScreenHeight - DIALOG_HEIGHT) / 2;
		int buttonsY = dialogY + DIALOG_HEIGHT - BUTTON_HEIGHT - 12;
		int totalBtnW = BUTTON_WIDTH * 2 + BUTTON_GAP;
		int btnStartX = dialogX + (DIALOG_WIDTH - totalBtnW) / 2;

		cancelButton.setX(btnStartX);
		cancelButton.setY(buttonsY);
		discardButton.setX(btnStartX + BUTTON_WIDTH + BUTTON_GAP);
		discardButton.setY(buttonsY);
	}
}
