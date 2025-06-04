package me.Azz_9.better_hud.client.screens.configurationScreen;

import me.Azz_9.better_hud.client.screens.AbstractCallbackScreen;
import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import me.Azz_9.better_hud.client.screens.modsList.ModsListScreen;
import me.Azz_9.better_hud.client.screens.widgets.buttons.configButtons.ConfigColorButtonWidget;
import me.Azz_9.better_hud.client.screens.widgets.buttons.configButtons.ConfigToggleButtonWidget;
import me.Azz_9.better_hud.client.screens.widgets.buttons.configButtons.colorSelector.GradientWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.config.Configuration;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractConfigurationScreen extends AbstractCallbackScreen implements Observer {

	protected final int buttonWidth;
	protected final int buttonHeight;
	private final int buttonGap;
	private final double parentScrollAmount;

	private ScrollableConfigList configList;

	public AbstractConfigurationScreen(Text title, Screen parent, int buttonWidth, int buttonHeight, int buttonGap, double parentScrollAmount) {
		super(title, parent, Text.translatable("better_hud.global.config.callback.message_title"), Text.translatable("better_hud.global.config.callback.message_content"));
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.buttonGap = buttonGap;
		this.parentScrollAmount = parentScrollAmount;
	}

	public AbstractConfigurationScreen(Text title, Screen parent, double parentScrollAmount) {
		this(title, parent, 150, 20, 10, parentScrollAmount);
	}

	public void addAllEntries(ScrollableConfigList.AbstractConfigEntry... entries) {
		for (ScrollableConfigList.AbstractConfigEntry entry : entries) {
			configList.addConfigEntry(entry);
			registerTrackableWidget(entry.getTrackableChangeWidget());
		}
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
		System.out.println(parentScrollAmount);
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
	}

	public ScrollableConfigList getConfigList() {
		return configList;
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
		public static class Builder extends ScrollableConfigList.AbstractConfigEntry.AbstractBuilder {
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
		private final ConfigColorButtonWidget<?> colorButtonWidget;
		private final GradientWidget gradientWidget;

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
			colorButtonWidget = new ConfigColorButtonWidget<>(colorButtonWidth, colorButtonHeight, color, onColorChange, observers, disableWhen);
			setResetButtonPressAction((btn) -> colorButtonWidget.setToInitialState());

			gradientWidget = new GradientWidget(100, 100);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
			super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
			colorButtonWidget.setPosition(x, y);
			gradientWidget.setPosition(colorButtonWidget.getRight() - gradientWidget.getWidth(), colorButtonWidget.getBottom());

			colorButtonWidget.render(context, mouseX, mouseY, tickProgress);
			gradientWidget.render(context, mouseX, mouseY, tickProgress);
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return List.of(colorButtonWidget, resetButtonWidget);
		}

		@Override
		public List<? extends Element> children() {
			return List.of(colorButtonWidget, resetButtonWidget, textWidget, gradientWidget);
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

		}
	}
}
