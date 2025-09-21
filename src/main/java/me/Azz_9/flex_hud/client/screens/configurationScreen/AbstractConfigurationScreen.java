package me.Azz_9.flex_hud.client.screens.configurationScreen;

import me.Azz_9.flex_hud.client.screens.AbstractCallbackScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorBindable;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorSelector;
import me.Azz_9.flex_hud.client.screens.modulesList.ModulesListScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractConfigurationScreen extends AbstractCallbackScreen implements Observer, ColorSelectorGetter {

	protected int buttonWidth;
	protected int buttonHeight;
	private double parentScrollAmount;

	private ScrollableConfigList configList;

	@Nullable
	private ColorSelector colorSelector;

	public AbstractConfigurationScreen(Text title, Screen parent, int buttonWidth, int buttonHeight) {
		super(title, parent, Text.translatable("flex_hud.global.config.callback.message_title"), Text.translatable("flex_hud.global.config.callback.message_content"));
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
	}

	public AbstractConfigurationScreen(Text title, Screen parent) {
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
				MinecraftClient.getInstance(),
				buttonWidth + 62, Math.min(300, this.height - configListY - bottomMargin),
				configListY, (this.width - (buttonWidth + 62)) / 2,
				buttonHeight + 10, buttonWidth + 30,
				this);

		this.addDrawableChild(configList);
	}

	@Override
	public void close() {
		super.close();
		if (PARENT instanceof ModulesListScreen modulesListScreen) {
			modulesListScreen.getModulesList().setScrollY(parentScrollAmount);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (renderCallback(context, mouseX, mouseY, deltaTicks)) {
			return;
		}

		int textColor = 0xffffffff;
		int backgroundColor = 0x80000000;
		int padding = 2;
		context.fill(this.width / 2 - textRenderer.getWidth(title) / 2 - padding, 7 - padding, this.width / 2 + textRenderer.getWidth(title) / 2 + padding, 7 + textRenderer.fontHeight, backgroundColor);
		context.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 7, textColor);

		configList.render(context, mouseX, mouseY, deltaTicks);

		if (colorSelector != null && colorSelector.isFocused()) {
			colorSelector.updatePosition(configList.getY());
			if (colorSelector.getY() >= configList.getY()) {
				colorSelector.render(context, mouseX, mouseY, deltaTicks);
			} else {
				colorSelector.setFocused(false);
			}
		}
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (MinecraftClient.getInstance().world == null) {
			super.renderBackground(context, mouseX, mouseY, deltaTicks);
		}
	}

	@Override
	public void onChange(DataGetter<?> dataGetter) {
		updateSaveButton();
	}


	@Override
	public boolean mouseClicked(Click click, boolean doubled) {
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
	public boolean mouseReleased(Click click) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.mouseReleased(click)) {
				return true;
			}
		}
		return super.mouseReleased(click);
	}

	@Override
	public boolean mouseDragged(Click click, double offsetX, double offsetY) {
		if (colorSelector != null && colorSelector.isFocused() && colorSelector.isDraggingACursor()) {
			if (colorSelector.mouseDragged(click, offsetX, offsetY)) {
				return true;
			}
		}
		return super.mouseDragged(click, offsetX, offsetY);
	}

	@Override
	public boolean keyPressed(KeyInput input) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.keyPressed(input)) {
				return true;
			}
		}
		return super.keyPressed(input);
	}

	@Override
	public boolean charTyped(CharInput input) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.charTyped(input)) {
				return true;
			}
		}
		return super.charTyped(input);
	}


	public void openColorSelector(ColorBindable colorBindable) {
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
