package me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.ScrollableConfigList;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.fields.ConfigIntFieldWidget;
import me.Azz_9.flex_hud.client.screens.widgets.buttons.TexturedButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class IntFieldEntry extends ScrollableConfigList.AbstractConfigEntry {
	private final ConfigIntFieldWidget intFieldWidget;
	private final TexturedButtonWidget increaseButton;
	private final TexturedButtonWidget decreaseButton;

	private final int increaseAndDecreaseButtonsSize = 10;

	private final List<Dependency<?>> dependencies = new ArrayList<>();

	private IntFieldEntry(
			int intFieldWidth,
			int intFieldHeight,
			ConfigInteger variable,
			int resetButtonSize,
			Function<Integer, Tooltip> getTooltip
	) {
		super(resetButtonSize, Text.translatable(variable.getConfigTextTranslationKey()));
		intFieldWidget = new ConfigIntFieldWidget(
				MinecraftClient.getInstance().textRenderer,
				intFieldWidth, intFieldHeight,
				variable,
				observers,
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
	public void setX(int x) {
		super.setX(x);
		intFieldWidget.setX(x + getWidth() - resetButtonWidget.getWidth() - 10 - increaseAndDecreaseButtonsSize - intFieldWidget.getWidth());
		increaseButton.setX(intFieldWidget.getRight());
		decreaseButton.setX(intFieldWidget.getRight());
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		intFieldWidget.setY(y);
		increaseButton.setY(y);
		decreaseButton.setY(y + increaseAndDecreaseButtonsSize);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
		super.render(context, mouseX, mouseY, hovered, deltaTicks);

		increaseButton.render(context, mouseX, mouseY, deltaTicks);
		if (!increaseButton.active) {
			context.fill(increaseButton.getX(), increaseButton.getY(), increaseButton.getRight(), increaseButton.getBottom(), 0xcf4e4e4e);
		}
		decreaseButton.render(context, mouseX, mouseY, deltaTicks);
		if (!decreaseButton.active) {
			context.fill(decreaseButton.getX(), decreaseButton.getY(), decreaseButton.getRight(), decreaseButton.getBottom(), 0xcf4e4e4e);
		}
		intFieldWidget.render(context, mouseX, mouseY, deltaTicks);
	}

	@Override
	public List<? extends Selectable> selectableChildren() {
		return List.of(intFieldWidget, increaseButton, decreaseButton, resetButtonWidget);
	}

	@Override
	public List<? extends Element> children() {
		return List.of(intFieldWidget, increaseButton, decreaseButton, resetButtonWidget);
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
		boolean shouldDisable = false;

		for (Dependency<?> dependency : dependencies) {
			Object value = dependency.entry().getDataGetter().getData();
			if (Objects.equals(value, dependency.disableWhen())) {
				shouldDisable = true;
				break;
			}
		}

		setActive(!shouldDisable);
	}

	@Override
	public void setActive(boolean active) {
		intFieldWidget.active = active;
		intFieldWidget.setEditable(active);
		increaseButton.active = active;
		decreaseButton.active = active;
		super.setActive(active);
		resetButtonWidget.active = active && !intFieldWidget.isCurrentValueDefault();
	}

	public <T> void addDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
		dependencies.add(new Dependency<>(entry, disableWhen));
	}

	// Builder
	public static class Builder extends AbstractBuilder<Integer> {
		private int intFieldWidth;
		private int intFieldHeight = 20;
		private ConfigInteger variable;
		private final List<Dependency<?>> dependencies = new ArrayList<>();

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

		public <T> Builder addDependency(ScrollableConfigList.AbstractConfigEntry entry, T disableWhen) {
			dependencies.add(new Dependency<>(entry, disableWhen));
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
					getTooltip
			);
			for (Dependency<?> dependency : dependencies) {
				entry.addDependency(dependency.entry(), dependency.disableWhen());
				dependency.entry().addObserver(entry);
				entry.onChange(dependency.entry().getDataGetter());
			}
			return entry;
		}
	}
}