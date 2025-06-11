package me.Azz_9.better_hud.client.screens.configurationScreen;

import me.Azz_9.better_hud.client.screens.AbstractCallbackScreen;
import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import me.Azz_9.better_hud.client.screens.modsList.ModsListScreen;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons.colorSelector.ColorBindable;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons.colorSelector.ColorSelector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractConfigurationScreen extends AbstractCallbackScreen implements Observer, ColorSelectorGetter {

	protected int buttonWidth;
	protected int buttonHeight;
	private final double parentScrollAmount;

	private ScrollableConfigList configList;

	@Nullable
	private ColorSelector colorSelector;

	public AbstractConfigurationScreen(Text title, Screen parent, int buttonWidth, int buttonHeight, double parentScrollAmount) {
		super(title, parent, Text.translatable("better_hud.global.config.callback.message_title"), Text.translatable("better_hud.global.config.callback.message_content"));
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.parentScrollAmount = parentScrollAmount;
	}

	public AbstractConfigurationScreen(Text title, Screen parent, double parentScrollAmount) {
		this(title, parent, 150, 20, parentScrollAmount);
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
		if (PARENT instanceof ModsListScreen modsListScreen) {
			modsListScreen.getModsList().setScrollY(parentScrollAmount);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (renderCallback(context, mouseX, mouseY, deltaTicks)) {
			return;
		}

		int textColor = 0xffffff;
		context.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 7, textColor);

		configList.render(context, mouseX, mouseY, deltaTicks);

		if (colorSelector != null && colorSelector.isFocused()) {
			colorSelector.render(context, mouseX, mouseY, deltaTicks);
		}
	}

	@Override
	public void onChange(DataGetter<?> dataGetter) {
		boolean foundAChange = false;
		for (TrackableChange widget : getTrackableWidgets()) {
			if (widget.hasChanged()) {
				foundAChange = true;
			}
			if (!widget.isValid()) {
				setSaveButtonActive(false);
				break;
			}
		}
		setSaveButtonActive(foundAChange);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.mouseClicked(mouseX, mouseY, button)) {
				return true;
			} else {
				closeColorSelector();
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
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (colorSelector != null && colorSelector.isFocused() && colorSelector.isDraggingACursor()) {
			if (colorSelector.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
				return true;
			}
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
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
	public boolean charTyped(char chr, int modifiers) {
		if (colorSelector != null && colorSelector.isFocused()) {
			if (colorSelector.charTyped(chr, modifiers)) {
				return true;
			}
		}
		return super.charTyped(chr, modifiers);
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
}
