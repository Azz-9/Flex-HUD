package me.Azz_9.better_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.fields.ConfigIntFieldWidget;
import me.Azz_9.better_hud.client.screens.widgets.buttons.TexturedButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class IntFieldEntry extends ScrollableConfigList.AbstractConfigEntry {
	private final ConfigIntFieldWidget<?> intFieldWidget;
	private final TexturedButtonWidget increaseButton;
	private final TexturedButtonWidget decreaseButton;

	private final int increaseAndDecreaseButtonsSize = 10;

	private <T> IntFieldEntry(
			int intFieldWidth,
			int intFieldHeight,
			ConfigInteger variable,
			int resetButtonSize,
			T disableWhen,
			Function<Integer, Tooltip> getTooltip
	) {
		super(resetButtonSize, Text.translatable(variable.getConfigTextTranslationKey()));
		intFieldWidget = new ConfigIntFieldWidget<>(
				MinecraftClient.getInstance().textRenderer,
				intFieldWidth, intFieldHeight,
				variable,
				observers,
				disableWhen,
				getTooltip
		);
		setResetButtonPressAction((btn) -> intFieldWidget.setToDefaultState());

		increaseButton = new TexturedButtonWidget(increaseAndDecreaseButtonsSize, increaseAndDecreaseButtonsSize, new ButtonTextures(
				Identifier.of(MOD_ID, "widgets/buttons/int_field/increase/unfocused.png"),
				Identifier.of(MOD_ID, "widgets/buttons/int_field/increase/focused.png")
		), (btn) -> this.intFieldWidget.increase());
		decreaseButton = new TexturedButtonWidget(increaseAndDecreaseButtonsSize, increaseAndDecreaseButtonsSize, new ButtonTextures(
				Identifier.of(MOD_ID, "widgets/buttons/int_field/decrease/unfocused.png"),
				Identifier.of(MOD_ID, "widgets/buttons/int_field/decrease/focused.png")
		), (btn) -> this.intFieldWidget.decrease());

		intFieldWidget.setIncreaseButton(increaseButton);
		intFieldWidget.setDecreaseButton(decreaseButton);

		intFieldWidget.addObserver(this.resetButtonWidget);
		this.resetButtonWidget.onChange(intFieldWidget);
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
		intFieldWidget.setPosition(x + entryWidth - resetButtonWidget.getWidth() - 10 - increaseAndDecreaseButtonsSize - intFieldWidget.getWidth(), y);
		increaseButton.setPosition(intFieldWidget.getRight(), y);
		decreaseButton.setPosition(intFieldWidget.getRight(), y + increaseAndDecreaseButtonsSize);

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
		resetButtonWidget.active = active && !intFieldWidget.isCurrentValueDefault();
	}

	// Builder
	public static class Builder extends AbstractBuilder<Integer> {
		private int intFieldWidth;
		private int intFieldHeight = 20;
		private ConfigInteger variable;
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

		public Builder setVariable(ConfigInteger variable) {
			this.variable = variable;
			return this;
		}

		public <T> Builder setDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependency = entry;
			this.disableWhen = disableWhen;
			return this;
		}

		@Override
		public IntFieldEntry build() {
			if (variable == null)
				throw new IllegalStateException("IntFieldEntry requires a variable to be set using setVariable()!");

			IntFieldEntry entry = new IntFieldEntry(
					intFieldWidth, intFieldHeight,
					variable,
					resetButtonSize,
					disableWhen,
					getTooltip
			);
			if (dependency != null) {
				dependency.addObserver(entry);
				entry.onChange(dependency.getDataGetter());
			}
			return entry;
		}
	}
}