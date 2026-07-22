package me.Azz_9.flex_hud.client.gui.screens;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.Azz_9.flex_hud.client.config.ConfigLoader;
import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.components.config.ColorSelectorGetter;
import me.Azz_9.flex_hud.client.gui.components.config.ScrollableConfigList;
import me.Azz_9.flex_hud.client.gui.components.config.colorSelector.ColorBindable;
import me.Azz_9.flex_hud.client.gui.components.config.colorSelector.ColorSelector;

public abstract class AbstractConfigurationScreen extends AbstractSavableScreen implements ColorSelectorGetter {

	protected int buttonWidth;
	protected int buttonHeight;
	private double parentScrollAmount;

	private ScrollableConfigList configList;

	@Nullable
	private ColorSelector colorSelector;

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
	public @Nullable ColorSelector getColorSelector() {
		return colorSelector;
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

		if (colorSelector != null && colorSelector.isFocused()) {
			colorSelector.updatePosition(configList.getY());
			if (colorSelector.getY() >= configList.getY()) {
				colorSelector.render(graphics, mouseX, mouseY, deltaTicks);
			} else {
				colorSelector.setFocused(false);
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
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.mouseClicked(mouseX, mouseY, button)) {
				return true;
			} else {
				boolean res = super.mouseClicked(mouseX, mouseY, button);
				closeColorSelector();
				return res;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.mouseReleased(mouseX, mouseY, button)) {
				return true;
			}
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (colorSelector != null && colorSelector.isFocused() && colorSelector.isDraggingACursor()) {
			if (colorSelector.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
				return true;
			}
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.keyPressed(keyCode, scanCode, modifiers)) {
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.charTyped(codePoint, modifiers)) {
				return true;
			}
		}
		return super.charTyped(codePoint, modifiers);
	}


	public void openColorSelector(@NotNull ColorBindable colorBindable) {
		this.colorSelector = new ColorSelector(colorBindable);
		this.colorSelector.setFocused(true);
	}

	public void closeColorSelector() {
		if (this.colorSelector != null) {
			this.colorSelector.setFocused(false);
		}
	}

	@Override
	protected synchronized void onSave() {
		ConfigLoader.saveConfig();
	}
}
