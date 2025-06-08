package me.Azz_9.better_hud.client.screens.configurationScreen;

import me.Azz_9.better_hud.client.screens.AbstractCallbackScreen;
import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import me.Azz_9.better_hud.client.screens.modsList.ModsListScreen;
import me.Azz_9.better_hud.client.screens.widgets.buttons.TexturedButtonWidget;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons.ConfigColorButtonWidget;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons.ConfigToggleButtonWidget;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons.colorSelector.ColorBindable;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons.colorSelector.ColorSelector;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.fields.ConfigIntFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public abstract class AbstractConfigurationScreen extends AbstractCallbackScreen implements Observer {

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

	public static class ToggleButtonEntry extends ScrollableConfigList.AbstractConfigEntry {
		private final ConfigToggleButtonWidget<?> toggleButtonWidget;

		private <T> ToggleButtonEntry(
				int toggleButtonWidth,
				int toggleButtonHeight,
				boolean toggled,
				Consumer<Boolean> onToggle,
				int resetButtonSize,
				Text text,
				T disableWhen
		) {
			super(resetButtonSize, text);
			toggleButtonWidget = new ConfigToggleButtonWidget<>(toggleButtonWidth, toggleButtonHeight, toggled, onToggle, observers, disableWhen);
			setResetButtonPressAction((btn) -> toggleButtonWidget.setToInitialState());
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
			super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
			toggleButtonWidget.setPosition(x, y);
			toggleButtonWidget.render(context, mouseX, mouseY, tickProgress);
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return List.of(toggleButtonWidget, resetButtonWidget);
		}

		@Override
		public List<? extends Element> children() {
			return List.of(toggleButtonWidget, resetButtonWidget, textWidget);
		}

		@Override
		public TrackableChange getTrackableChangeWidget() {
			return toggleButtonWidget;
		}

		@Override
		public DataGetter<?> getDataGetter() {
			return toggleButtonWidget;
		}

		@Override
		public void onChange(DataGetter<?> dataGetter) {
			boolean active = !toggleButtonWidget.getDisableWhen().equals(dataGetter.getData());
			toggleButtonWidget.active = active;
			setActive(active);
		}

		// Builder
		public static class Builder extends AbstractBuilder {
			private int toggleButtonWidth;
			private int toggleButtonHeight = 20;
			private boolean toggled;
			private Consumer<Boolean> onToggle = t -> {
			};
			private ScrollableConfigList.AbstractConfigEntry dependency = null;
			private Object disableWhen;

			public Builder setToggleButtonWidth(int width) {
				this.toggleButtonWidth = width;
				return this;
			}

			public Builder setToggleButtonSize(int width, int height) {
				this.toggleButtonWidth = width;
				this.toggleButtonHeight = height;
				return this;
			}

			public Builder setToggled(boolean toggled) {
				this.toggled = toggled;
				return this;
			}

			public Builder setOnToggle(Consumer<Boolean> onToggle) {
				this.onToggle = onToggle;
				return this;
			}

			public <T> Builder setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
				dependency = entry;
				this.disableWhen = disableWhen;
				return this;
			}

			@Override
			public ToggleButtonEntry build() {
				ToggleButtonEntry entry = new ToggleButtonEntry(
						toggleButtonWidth, toggleButtonHeight,
						toggled, onToggle,
						resetButtonSize,
						text,
						disableWhen
				);
				if (dependency != null) {
					dependency.addObserver(entry);
					entry.onChange(dependency.getDataGetter());
				}
				return entry;
			}
		}
	}

	public static class ColorButtonEntry extends ScrollableConfigList.AbstractConfigEntry {
		private ConfigColorButtonWidget<?> colorButtonWidget;

		public <T> ColorButtonEntry(
				int colorButtonWidth,
				int colorButtonHeight,
				int color,
				Consumer<Integer> onColorChange,
				int resetButtonSize,
				Text text,
				T disableWhen
		) {
			super(resetButtonSize, text);
			colorButtonWidget = new ConfigColorButtonWidget<>(colorButtonWidth, colorButtonHeight, color, onColorChange, observers, disableWhen,
					(btn) -> {
						AbstractConfigurationScreen screen = (AbstractConfigurationScreen) MinecraftClient.getInstance().currentScreen;
						if (screen != null) {
							ColorSelector colorSelector = screen.colorSelector;
							if (colorSelector == null || !colorSelector.isFocused()) {
								screen.openColorSelector(this.colorButtonWidget);
							} else {
								screen.closeColorSelector();
							}
						}
					});
			setResetButtonPressAction((btn) -> colorButtonWidget.setToInitialState());
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
			super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
			colorButtonWidget.setPosition(x, y);

			colorButtonWidget.render(context, mouseX, mouseY, tickProgress);
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return List.of(colorButtonWidget, resetButtonWidget);
		}

		@Override
		public List<? extends Element> children() {
			return List.of(colorButtonWidget, resetButtonWidget, textWidget);
		}

		@Override
		public TrackableChange getTrackableChangeWidget() {
			return colorButtonWidget;
		}

		@Override
		public DataGetter<?> getDataGetter() {
			return colorButtonWidget;
		}

		@Override
		public void onChange(DataGetter<?> dataGetter) {
			boolean active = !colorButtonWidget.getDisableWhen().equals(dataGetter.getData());
			colorButtonWidget.active = active;
			setActive(active);
		}

		//Builder
		public static class Builder extends AbstractBuilder {
			private int colorButtonWidth;
			private int colorButtonHeight = 20;
			private int color;
			private Consumer<Integer> onColorChange = t -> {
			};
			private ScrollableConfigList.AbstractConfigEntry dependency = null;
			private Object disableWhen;

			public ColorButtonEntry.Builder setColorButtonWidth(int width) {
				this.colorButtonWidth = width;
				return this;
			}

			public ColorButtonEntry.Builder setColorButtonSize(int width, int height) {
				this.colorButtonWidth = width;
				this.colorButtonHeight = height;
				return this;
			}

			public ColorButtonEntry.Builder setColor(int color) {
				this.color = color;
				return this;
			}

			public ColorButtonEntry.Builder setOnColorChange(Consumer<Integer> onColorChange) {
				this.onColorChange = onColorChange;
				return this;
			}

			public <T> ColorButtonEntry.Builder setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
				dependency = entry;
				this.disableWhen = disableWhen;
				return this;
			}

			@Override
			public ColorButtonEntry build() {
				ColorButtonEntry entry = new ColorButtonEntry(
						colorButtonWidth, colorButtonHeight,
						color, onColorChange,
						resetButtonSize,
						text,
						disableWhen
				);
				if (dependency != null) {
					dependency.addObserver(entry);
					entry.onChange(dependency.getDataGetter());
				}
				return entry;
			}
		}
	}

	public static class IntFieldEntry extends ScrollableConfigList.AbstractConfigEntry {
		private ConfigIntFieldWidget<?> intFieldWidget;
		private TexturedButtonWidget increaseButton;
		private TexturedButtonWidget decreaseButton;

		private int increaseAndDecreaseButtonSize = 10;

		public <T> IntFieldEntry(
				int intFieldWidth,
				int intFieldHeight,
				int value,
				Integer min,
				Integer max,
				Consumer<Integer> onValueChange,
				int resetButtonSize,
				Text text,
				T disableWhen
		) {
			super(resetButtonSize, text);
			intFieldWidget = new ConfigIntFieldWidget<>(
					MinecraftClient.getInstance().textRenderer,
					intFieldWidth, intFieldHeight,
					value,
					min, max,
					onValueChange,
					observers,
					disableWhen
			);
			setResetButtonPressAction((btn) -> intFieldWidget.setToInitialState());

			increaseButton = new TexturedButtonWidget(increaseAndDecreaseButtonSize, increaseAndDecreaseButtonSize, new ButtonTextures(
					Identifier.of(MOD_ID, "widgets/buttons/int_field/increase/unfocused.png"),
					Identifier.of(MOD_ID, "widgets/buttons/int_field/increase/focused.png")
			), (btn) -> this.intFieldWidget.increase());
			decreaseButton = new TexturedButtonWidget(increaseAndDecreaseButtonSize, increaseAndDecreaseButtonSize, new ButtonTextures(
					Identifier.of(MOD_ID, "widgets/buttons/int_field/decrease/unfocused.png"),
					Identifier.of(MOD_ID, "widgets/buttons/int_field/decrease/focused.png")
			), (btn) -> this.intFieldWidget.decrease());
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
			super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
			intFieldWidget.setPosition(x + entryWidth - resetButtonWidget.getWidth() - 10 - increaseAndDecreaseButtonSize - intFieldWidget.getWidth(), y);
			increaseButton.setPosition(intFieldWidget.getRight(), y);
			decreaseButton.setPosition(intFieldWidget.getRight(), y + increaseAndDecreaseButtonSize);

			increaseButton.render(context, mouseX, mouseY, tickProgress);
			if (!increaseButton.active) {
				context.fill(increaseButton.getX(), increaseButton.getY(), increaseButton.getRight(), increaseButton.getBottom(), 0xcf4e4e4e);
			}
			decreaseButton.render(context, mouseX, mouseY, tickProgress);
			if (!decreaseButton.active) {
				context.fill(decreaseButton.getX(), decreaseButton.getY(), decreaseButton.getRight(), decreaseButton.getBottom(), 0xcf4e4e4e);
			}
			intFieldWidget.render(context, mouseX, mouseY, tickProgress);
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return List.of(intFieldWidget, increaseButton, decreaseButton, resetButtonWidget);
		}

		@Override
		public List<? extends Element> children() {
			return List.of(intFieldWidget, increaseButton, decreaseButton, resetButtonWidget, textWidget);
		}

		@Override
		public TrackableChange getTrackableChangeWidget() {
			return intFieldWidget;
		}

		@Override
		public DataGetter<?> getDataGetter() {
			return intFieldWidget;
		}

		@Override
		public void onChange(DataGetter<?> dataGetter) {
			boolean active = !intFieldWidget.getDisableWhen().equals(dataGetter.getData());
			intFieldWidget.active = active;
			intFieldWidget.setEditable(active);
			increaseButton.active = active;
			decreaseButton.active = active;
			setActive(active);
		}

		// Builder
		public static class Builder extends AbstractBuilder {
			private int intFieldWidth;
			private int intFieldHeight = 20;
			private int value;
			private Integer min = null;
			private Integer max = null;
			private Consumer<Integer> onValueChange = t -> {
			};
			private ScrollableConfigList.AbstractConfigEntry dependency = null;
			private Object disableWhen;

			public Builder setIntFieldWidth(int width) {
				this.intFieldWidth = width;
				return this;
			}

			public Builder setIntFieldSize(int width, int height) {
				this.intFieldWidth = width;
				this.intFieldHeight = height;
				return this;
			}

			public Builder setValue(int value) {
				this.value = value;
				return this;
			}

			public Builder setMin(int min) {
				this.min = min;
				return this;
			}

			public Builder setMax(int max) {
				this.max = max;
				return this;
			}

			public Builder setOnValueChange(Consumer<Integer> onValueChange) {
				this.onValueChange = onValueChange;
				return this;
			}

			public <T> Builder setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
				dependency = entry;
				this.disableWhen = disableWhen;
				return this;
			}

			@Override
			public IntFieldEntry build() {
				IntFieldEntry entry = new IntFieldEntry(
						intFieldWidth, intFieldHeight,
						value, min, max,
						onValueChange,
						resetButtonSize,
						text,
						disableWhen
				);
				if (dependency != null) {
					dependency.addObserver(entry);
					entry.onChange(dependency.getDataGetter());
				}
				return entry;
			}
		}
	}
}
