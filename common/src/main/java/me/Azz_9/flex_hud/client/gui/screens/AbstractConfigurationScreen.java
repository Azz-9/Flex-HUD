package me.Azz_9.flex_hud.client.gui.screens;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import me.Azz_9.flex_hud.client.config.ConfigLoader;
import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.components.config.ColorPickerGetter;
import me.Azz_9.flex_hud.client.gui.components.config.ScrollableConfigList;
import me.Azz_9.flex_hud.client.gui.components.config.colorPicker.ColorBindable;
import me.Azz_9.flex_hud.client.gui.components.config.colorPicker.ColorPicker;

public abstract class AbstractConfigurationScreen extends AbstractSavableScreen implements ColorPickerGetter {

	protected int buttonWidth;
	protected int buttonHeight;
	private double parentScrollAmount;

	private ScrollableConfigList configList;

	@Nullable
	private ColorPicker colorPicker;

	public AbstractConfigurationScreen(Component title, Screen parent, int buttonWidth, int buttonHeight) {
		super(title, parent);
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
	}

	public AbstractConfigurationScreen(Component title, Screen parent) {
		this(title, parent, 165, 20);
	}

	public void addAllEntries(ScrollableConfigList.AbstractConfigEntry... entries) {
		for (ScrollableConfigList.AbstractConfigEntry entry : entries) {
			configList.addEntry(entry);
			registerTracked(entry.getTrackableChangeWidget());
		}
	}

	public ScrollableConfigList getConfigList() {
		return configList;
	}

	@Override
	public @Nullable ColorPicker getColorPicker() {
		return colorPicker;
	}

	public void setParentScrollAmount(double parentScrollAmount) {
		this.parentScrollAmount = parentScrollAmount;
	}

	@Override
	protected void initContent() {
		int configListY = Math.max(this.height / 10, 20);
		int bottomMargin = 50;
		this.configList = new ScrollableConfigList(
				MINECRAFT,
				buttonWidth + 62, Math.min(300, this.height - configListY - bottomMargin),
				configListY, (this.width - (buttonWidth + 62)) / 2,
				buttonHeight + 10, buttonWidth + 30);

		this.addRenderableWidget(configList);
	}

	@Override
	public void onActuallyClose() {
		if (PARENT instanceof ModulesListScreen modulesListScreen) {
			modulesListScreen.getModulesListWidget().setScrollAmount(parentScrollAmount);
		}
	}

	@Override
	public void renderBeforePopup(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		int textColor = Colors.WHITE;
		int backgroundColor = Colors.BLACK_SEMI_TRANSPARENT;
		int padding = 2;
		graphics.fill(this.width / 2 - font.width(title) / 2 - padding, 7 - padding, this.width / 2 + font.width(title) / 2 + padding, 7 + font.lineHeight, backgroundColor);
		graphics.centeredText(font, title, this.width / 2, 7, textColor);

		if (colorPicker != null && colorPicker.isFocused()) {
			colorPicker.updatePosition(configList.getY());
			if (colorPicker.getY() >= configList.getY()) {
				colorPicker.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
			} else {
				colorPicker.setFocused(false);
			}
		}
	}

	@Override
	public void extractBackground(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
		if (MINECRAFT.level == null) {
			super.extractBackground(context, mouseX, mouseY, deltaTicks);
		}
	}

	@Override
	public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
		if (colorPicker != null && colorPicker.isFocused()) {
			if (colorPicker.mouseClicked(click, doubled)) {
				return true;
			} else {
				boolean res = super.mouseClicked(click, doubled);
				closeColorPicker();
				return res;
			}
		}
		return super.mouseClicked(click, doubled);
	}

	@Override
	public boolean mouseReleased(@NonNull MouseButtonEvent click) {
		if (colorPicker != null && colorPicker.isFocused()) {
			if (colorPicker.mouseReleased(click)) {
				return true;
			}
		}
		return super.mouseReleased(click);
	}

	@Override
	public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
		if (colorPicker != null && colorPicker.isFocused() && colorPicker.isDraggingACursor()) {
			if (colorPicker.mouseDragged(click, offsetX, offsetY)) {
				return true;
			}
		}
		return super.mouseDragged(click, offsetX, offsetY);
	}

	@Override
	public boolean keyPressed(@NonNull KeyEvent input) {
		if (colorPicker != null && colorPicker.isFocused()) {
			if (colorPicker.keyPressed(input)) {
				return true;
			}
		}
		return super.keyPressed(input);
	}

	@Override
	public boolean charTyped(@NonNull CharacterEvent input) {
		if (colorPicker != null && colorPicker.isFocused()) {
			if (colorPicker.charTyped(input)) {
				return true;
			}
		}
		return super.charTyped(input);
	}


	public void openColorPicker(@NotNull ColorBindable colorBindable) {
		this.colorPicker = new ColorPicker(colorBindable);
		this.colorPicker.setFocused(true);
	}

	public void closeColorPicker() {
		if (this.colorPicker != null) {
			this.colorPicker.setFocused(false);
		}
	}

	@Override
	protected synchronized void onSave() {
		ConfigLoader.saveConfig();
	}
}
