package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;

import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import me.Azz_9.flex_hud.client.screens.TrackableChange;

public class ModuleContentField2 extends ClickableWidget implements TrackableChange {

	private static final ButtonTextures TEXTURES = new ButtonTextures(
			Identifier.ofVanilla("widget/text_field"), Identifier.ofVanilla("widget/text_field_highlighted")
	);

	// tokens
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_.]+)}");

	private static final int TOKEN_PADDING_X = 4;
	private static final int TOKEN_PADDING_Y = 2;
	private static final int TOKEN_BG_COLOR = 0xff3b82f6;
	private static final int TOKEN_TEXT_COLOR = 0xffffffff;

	// cursor
	private static final char VERTICAL_CURSOR = '|';
	private int cursorPos = 0;

	private boolean editable = true;

	// text
	private @NonNull String text = "";
	private int textXOffset = 5;
	private int textYOffset;
	private boolean textShadow = false;
	private int maxLength = 32;
	private Consumer<String> changedListener;
	private Predicate<String> textPredicate = Objects::nonNull;

	// selection
	private int selectionStart;
	private int selectionEnd;

	public ModuleContentField2(int x, int y, int width, int height) {
		super(x, y, width, height, Text.of(""));
		textYOffset = (getHeight() - CLIENT.textRenderer.fontHeight) / 2;
	}

	private void onChanged(String newText) {
		if (this.changedListener != null) {
			this.changedListener.accept(newText);
		}
	}

	// render

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (!visible) return;

		renderBackground(context);

		context.drawText(CLIENT.textRenderer, text, getX() + textXOffset, getY() + textYOffset, TOKEN_TEXT_COLOR, textShadow);
	}

	private void renderBackground(DrawContext context) {
		Identifier identifier = TEXTURES.get(this.isInteractable(), this.isFocused());
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	public boolean isActive() {
		return this.isInteractable() && this.isFocused() && this.isEditable();
	}

	// selection

	public void setSelectionStart(int cursor) {
		this.selectionStart = MathHelper.clamp(cursor, 0, this.text.length());
	}

	public void setSelectionEnd(int index) {
		this.selectionEnd = MathHelper.clamp(index, 0, this.text.length());
	}

	// cursor

	public void moveCursor(int offset, boolean shiftKeyPressed) {
		this.setCursor(getCursorPos() + offset, shiftKeyPressed);
	}

	public void setCursor(int cursor, boolean select) {
		this.setSelectionStart(cursor);
		if (!select) {
			this.setSelectionEnd(this.selectionStart);
		}

		this.onChanged(this.text);
	}

	public void setCursorToStart(boolean shiftKeyPressed) {
		this.setCursor(0, shiftKeyPressed);
	}

	public void setCursorToEnd(boolean shiftKeyPressed) {
		this.setCursor(this.text.length(), shiftKeyPressed);
	}

	// inputs

	@Override
	public boolean charTyped(CharInput input) {
		if (!this.isActive()) {
			return false;
		} else if (input.isValidChar()) {
			if (this.editable) {
				this.write(input.asString());
			}

			return true;
		} else {
			return false;
		}
	}

	// text

	public void write(String text) {
		int selectionStart = Math.min(this.selectionStart, this.selectionEnd);
		int selectionEnd = Math.max(this.selectionStart, this.selectionEnd);
		int remainingChars = this.maxLength - this.text.length() + (selectionEnd - selectionStart);
		if (remainingChars > 0) {
			String string = StringHelper.stripInvalidChars(text);
			int length = string.length();
			if (remainingChars < length) {
				if (Character.isHighSurrogate(string.charAt(remainingChars - 1))) {
					remainingChars--;
				}

				string = string.substring(0, remainingChars);
				length = remainingChars;
			}

			String newText = new StringBuilder(this.text).replace(selectionStart, selectionEnd, string).toString();
			if (this.textPredicate.test(newText)) {
				this.text = newText;
				this.setSelectionStart(selectionStart + length);
				this.setSelectionEnd(this.selectionStart);
				this.onChanged(this.text);
			}
		}
	}

	// trackable change

	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public void cancel() {

	}

	// getters / setters

	public @NonNull String getText() {
		return text;
	}

	public void setText(@NonNull String text) {
		this.text = text;
	}

	public boolean isEditable() {
		return editable;
	}

	public int getCursorPos() {
		return cursorPos;
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);
		textYOffset = (getHeight() - CLIENT.textRenderer.fontHeight) / 2;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setChangedListener(Consumer<String> changedListener) {
		this.changedListener = changedListener;
	}

	public void setTextPredicate(Predicate<String> textPredicate) {
		this.textPredicate = textPredicate;
	}

	// narratinos

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}
}