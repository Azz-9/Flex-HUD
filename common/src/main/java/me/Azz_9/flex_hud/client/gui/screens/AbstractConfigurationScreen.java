package me.Azz_9.flex_hud.client.gui.screens;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	public void renderBeforePopup(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		int textColor = Colors.WHITE;
		int backgroundColor = Colors.BLACK_SEMI_TRANSPARENT;
		int padding = 2;
		graphics.fill(this.width / 2 - font.width(title) / 2 - padding, 7 - padding, this.width / 2 + font.width(title) / 2 + padding, 7 + font.lineHeight, backgroundColor);
		graphics.drawCenteredString(font, title, this.width / 2, 7, textColor);

		if (colorPicker != null && colorPicker.isFocused()) {
			colorPicker.updatePosition(configList.getY());
			if (colorPicker.getY() >= configList.getY()) {
				colorPicker.render(graphics, mouseX, mouseY, deltaTicks);
			} else {
				colorPicker.setFocused(false);
			}
		}
	}

	@Override
	public void renderBackground(@NotNull GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
		if (MINECRAFT.level == null) {
			super.renderBackground(context, mouseX, mouseY, deltaTicks);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (colorPicker != null && colorPicker.isFocused()) {
			if (colorPicker.mouseClicked(mouseX, mouseY, button)) {
				return true;
			} else {
				boolean res = super.mouseClicked(mouseX, mouseY, button);
				closeColorPicker();
				return res;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (colorPicker != null && colorPicker.isFocused()) {
			if (colorPicker.mouseReleased(mouseX, mouseY, button)) {
				return true;
			}
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (colorPicker != null && colorPicker.isFocused() && colorPicker.isDraggingACursor()) {
			if (colorPicker.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
				return true;
			}
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (colorPicker != null && colorPicker.isFocused()) {
			if (colorPicker.keyPressed(keyCode, scanCode, modifiers)) {
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (colorPicker != null && colorPicker.isFocused()) {
			if (colorPicker.charTyped(codePoint, modifiers)) {
				return true;
			}
		}
		return super.charTyped(codePoint, modifiers);
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
