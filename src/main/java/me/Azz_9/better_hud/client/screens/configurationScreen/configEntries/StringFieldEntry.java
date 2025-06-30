package me.Azz_9.better_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigString;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.fields.ConfigTextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Predicate;

public class StringFieldEntry extends ScrollableConfigList.AbstractConfigEntry {
	private ConfigTextFieldWidget<?> textFieldWidget;

	private <T> StringFieldEntry(
			int textFieldWidth,
			int textFieldHeight,
			ConfigString variable,
			Predicate<String> isValid,
			int resetButtonSize,
			T disableWhen
	) {
		super(resetButtonSize, Text.translatable(variable.getConfigTextTranslationKey()));
		textFieldWidget = new ConfigTextFieldWidget<>(
				MinecraftClient.getInstance().textRenderer,
				textFieldWidth, textFieldHeight,
				variable,
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
		private ConfigString variable;
		private Predicate<String> isValid = s -> true;
		private ScrollableConfigList.AbstractConfigEntry dependency = null;
		private Object disableWhen;

		public Builder setStringFieldWidth(int width) {
			this.textFieldWidth = width;
			return this;
		}

		public Builder setStringFieldSize(int width, int height) {
			this.textFieldWidth = width;
			this.textFieldHeight = height;
			return this;
		}

		public Builder setVariable(ConfigString variable) {
			this.variable = variable;
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
					variable,
					isValid,
					resetButtonSize,
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
