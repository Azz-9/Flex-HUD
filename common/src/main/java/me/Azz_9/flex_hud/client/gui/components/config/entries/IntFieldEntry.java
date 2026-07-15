package me.Azz_9.flex_hud.client.gui.components.config.entries;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.Constants.MOD_ID;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.config.option.ConfigInteger;
import me.Azz_9.flex_hud.client.gui.components.TrackableChange;
import me.Azz_9.flex_hud.client.gui.components.config.DataGetter;
import me.Azz_9.flex_hud.client.gui.components.config.Observer;
import me.Azz_9.flex_hud.client.gui.components.config.ScrollableConfigList;
import me.Azz_9.flex_hud.client.gui.components.config.fields.ConfigIntFieldWidget;

public class IntFieldEntry extends ScrollableConfigList.AbstractConfigEntry {
	private static final Identifier INCREASE_UNFOCUSED = Identifier.fromNamespaceAndPath(MOD_ID, "widget/increase/unfocused");
	private static final Identifier INCREASE_FOCUSED = Identifier.fromNamespaceAndPath(MOD_ID, "widget/increase/focused");
	private static final Identifier DECREASE_UNFOCUSED = Identifier.fromNamespaceAndPath(MOD_ID, "widget/decrease/unfocused");
	private static final Identifier DECREASE_FOCUSED = Identifier.fromNamespaceAndPath(MOD_ID, "widget/decrease/focused");

	private final ConfigIntFieldWidget intFieldWidget;
	private final ImageButton increaseButton;
	private final ImageButton decreaseButton;

	private final int increaseAndDecreaseButtonsSize = 10;

	private final List<Dependency<?>> dependencies = new ArrayList<>();

	private IntFieldEntry(
			int intFieldWidth,
			int intFieldHeight,
			ConfigInteger variable,
			int resetButtonSize,
			Function<Integer, Tooltip> getTooltip
	) {
		super(resetButtonSize, Component.translatable(Objects.requireNonNull(variable.getConfigTextTranslationKey())));
		intFieldWidget = new ConfigIntFieldWidget(
				MINECRAFT.font,
				intFieldWidth, intFieldHeight,
				variable,
				observers,
				getTooltip
		);
		setResetButtonPressAction((btn) -> intFieldWidget.setToDefaultState());

		increaseButton = new ImageButton(
				0, 0,
				increaseAndDecreaseButtonsSize, increaseAndDecreaseButtonsSize,
				new WidgetSprites(INCREASE_UNFOCUSED, INCREASE_FOCUSED),
				(btn) -> this.intFieldWidget.increase()
		);

		decreaseButton = new ImageButton(
				0, 0,
				increaseAndDecreaseButtonsSize, increaseAndDecreaseButtonsSize,
				new WidgetSprites(DECREASE_UNFOCUSED, DECREASE_FOCUSED),
				(btn) -> this.intFieldWidget.decrease()
		);

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
	public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
		super.extractContent(graphics, mouseX, mouseY, hovered, deltaTicks);

		increaseButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		if (!increaseButton.active) {
			graphics.fill(increaseButton.getX(), increaseButton.getY(), increaseButton.getRight(), increaseButton.getBottom(), 0xcf4e4e4e);
		}
		decreaseButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		if (!decreaseButton.active) {
			graphics.fill(decreaseButton.getX(), decreaseButton.getY(), decreaseButton.getRight(), decreaseButton.getBottom(), 0xcf4e4e4e);
		}
		intFieldWidget.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
	}

	@Override
	public @NonNull List<? extends NarratableEntry> narratables() {
		return List.of(intFieldWidget, increaseButton, decreaseButton, resetButtonWidget);
	}

	@Override
	public @NonNull List<? extends GuiEventListener> children() {
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
			for (Observer observer : observers) {
				entry.addObserver(observer);
			}
			for (Dependency<?> dependency : dependencies) {
				entry.addDependency(dependency.entry(), dependency.disableWhen());
				dependency.entry().addObserver(entry);
				entry.onChange(dependency.entry().getDataGetter());
			}
			return entry;
		}
	}
}