package me.Azz_9.better_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.fields.ConfigTextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class StringFieldEntry extends ScrollableConfigList.AbstractConfigEntry {
	private ConfigTextFieldWidget<?> textFieldWidget;

	private <T> StringFieldEntry(
			int textFieldWidth,
			int textFieldHeight,
			String value,
			String defaultValue,
			Consumer<String> onValueChange,
			Predicate<String> isValid,
			int resetButtonSize,
			Text text,
			T disableWhen
	) {
		super(resetButtonSize, text);
		textFieldWidget = new ConfigTextFieldWidget<>(
				MinecraftClient.getInstance().textRenderer,
				textFieldWidth, textFieldHeight,
				value, defaultValue,
				onValueChange,
				observers,
				disableWhen,
				isValid
		);
		setResetButtonPressAction((btn) -> textFieldWidget.setToDefaultState());

		textFieldWidget.addObserver((Observer) this.resetButtonWidget);
		((Observer) this.resetButtonWidget).onChange(textFieldWidget);
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
		textFieldWidget.setPosition(x + entryWidth - resetButtonWidget.getWidth() - 10 - textFieldWidget.getWidth(), y);

		textFieldWidget.render(context, mouseX, mouseY, tickProgress);
	}

	@Override
	public List<? extends Selectable> selectableChildren() {
		return List.of(textFieldWidget, resetButtonWidget);
	}

	@Override
	public List<? extends Element> children() {
		return List.of(resetButtonWidget, textWidget, textFieldWidget);
	}

	@Override
	public TrackableChange getTrackableChangeWidget() {
		return textFieldWidget;
	}

	@Override
	public DataGetter<?> getDataGetter() {
		return textFieldWidget;
	}

	@Override
	public void onChange(DataGetter<?> dataGetter) {
		boolean active = !textFieldWidget.getDisableWhen().equals(dataGetter.getData());
		textFieldWidget.active = active;
		textFieldWidget.setEditable(active);
		setActive(active);
		resetButtonWidget.active = active && !textFieldWidget.isCurrentValueDefault();
	}

	// Builder
	public static class Builder extends AbstractBuilder {
		private int textFieldWidth;
		private int textFieldHeight = 20;
		private String value;
		private String defaultValue;
		private Consumer<String> onValueChange = t -> {
		};
		private Predicate<String> isValid = s -> true;
		private ScrollableConfigList.AbstractConfigEntry dependency = null;
		private Object disableWhen;

		public Builder setIntFieldWidth(int width) {
			this.textFieldWidth = width;
			return this;
		}

		public Builder setIntFieldSize(int width, int height) {
			this.textFieldWidth = width;
			this.textFieldHeight = height;
			return this;
		}

		public Builder setValue(String value) {
			this.value = value;
			return this;
		}

		public Builder setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder setOnValueChange(Consumer<String> onValueChange) {
			this.onValueChange = onValueChange;
			return this;
		}

		public Builder setIsValid(Predicate<String> isValid) {
			this.isValid = isValid;
			return this;
		}

		public <T> Builder setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependency = entry;
			this.disableWhen = disableWhen;
			return this;
		}

		@Override
		public StringFieldEntry build() {
			StringFieldEntry entry = new StringFieldEntry(
					textFieldWidth, textFieldHeight,
					value, defaultValue,
					onValueChange,
					isValid,
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
