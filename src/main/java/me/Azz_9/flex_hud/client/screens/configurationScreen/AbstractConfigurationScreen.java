package me.Azz_9.flex_hud.client.screens.configurationScreen;

import me.Azz_9.flex_hud.client.screens.AbstractCallbackScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorBindable;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorSelector;
import me.Azz_9.flex_hud.client.screens.modulesList.ModulesListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public abstract class AbstractConfigurationScreen extends AbstractCallbackScreen implements Observer, ColorSelectorGetter {

	protected int buttonWidth;
	protected int buttonHeight;
	private double parentScrollAmount;

	private ScrollableConfigList configList;

	@Nullable
	private ColorSelector colorSelector;

	public AbstractConfigurationScreen(Component title, Screen parent, int buttonWidth, int buttonHeight) {
		super(title, parent, Component.translatable("flex_hud.global.config.callback.message_title"), Component.translatable("flex_hud.global.config.callback.message_content"));
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
	}

	public AbstractConfigurationScreen(Component title, Screen parent) {
		this(title, parent, 150, 20);
	}

	public void addAllEntries(ScrollableConfigList.AbstractConfigEntry... entries) {
		for (ScrollableConfigList.AbstractConfigEntry entry : entries) {
			configList.addConfigEntry(entry);
			registerTrackableWidget(entry.getTrackableChangeWidget());
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
	protected void init() {
		super.init();

		int configListY = Math.max(this.height / 10, 20);
		int bottomMargin = 50;
		this.configList = new ScrollableConfigList(
				Minecraft.getInstance(),
				buttonWidth + 62, Math.min(300, this.height - configListY - bottomMargin),
				configListY, (this.width - (buttonWidth + 62)) / 2,
				buttonHeight + 10, buttonWidth + 30,
				this);

		this.addRenderableWidget(configList);
	}

	@Override
	public void onClose() {
		super.onClose();
		if (PARENT instanceof ModulesListScreen modulesListScreen) {
			modulesListScreen.getModulesListWidget().setScrollAmount(parentScrollAmount);
		}
	}

	@Override
	public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		if (renderCallback(graphics, mouseX, mouseY, deltaTicks)) {
			return;
		}

		int textColor = 0xffffffff;
		int backgroundColor = 0x80000000;
		int padding = 2;
		graphics.fill(this.width / 2 - font.width(title) / 2 - padding, 7 - padding, this.width / 2 + font.width(title) / 2 + padding, 7 + font.lineHeight, backgroundColor);
		graphics.drawCenteredString(font, title, this.width / 2, 7, textColor);

		configList.render(graphics, mouseX, mouseY, deltaTicks);

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
	public void renderBackground(@NonNull GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
		if (Minecraft.getInstance().level == null) {
			super.renderBackground(context, mouseX, mouseY, deltaTicks);
		}
	}

	@Override
	public void onChange(DataGetter<?> dataGetter) {
		updateSaveButton();
	}


	@Override
	public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.mouseClicked(click, doubled)) {
				return true;
			} else {
				boolean res = super.mouseClicked(click, doubled);
				closeColorSelector();
				return res;
			}
		}
		return super.mouseClicked(click, doubled);
	}

	@Override
	public boolean mouseReleased(@NonNull MouseButtonEvent click) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.mouseReleased(click)) {
				return true;
			}
		}
		return super.mouseReleased(click);
	}

	@Override
	public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
		if (colorSelector != null && colorSelector.isFocused() && colorSelector.isDraggingACursor()) {
			if (colorSelector.mouseDragged(click, offsetX, offsetY)) {
				return true;
			}
		}
		return super.mouseDragged(click, offsetX, offsetY);
	}

	@Override
	public boolean keyPressed(@NonNull KeyEvent input) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.keyPressed(input)) {
				return true;
			}
		}
		return super.keyPressed(input);
	}

	@Override
	public boolean charTyped(@NonNull CharacterEvent input) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.charTyped(input)) {
				return true;
			}
		}
		return super.charTyped(input);
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
	protected void disableAllChildren() {
		super.disableAllChildren();
		closeColorSelector();
		configList.active = false;
	}

	@Override
	protected void enableAllChildren() {
		super.enableAllChildren();
		configList.active = true;
	}
}
