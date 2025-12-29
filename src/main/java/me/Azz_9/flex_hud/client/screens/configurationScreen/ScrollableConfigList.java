package me.Azz_9.flex_hud.client.screens.configurationScreen;

import me.Azz_9.flex_hud.client.screens.AbstractSmoothScrollableList;
import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.ConfigResetButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ScrollableConfigList extends AbstractSmoothScrollableList<ScrollableConfigList.AbstractConfigEntry> {
	private final int itemWidth;
	private final Observer observer;

	public ScrollableConfigList(Minecraft minecraftClient, int width, int height, int y, int x, int itemHeight, int itemWidth, Observer observer) {
		super(minecraftClient, width, height, y, itemHeight);
		setX(x);
		this.itemWidth = itemWidth;
		this.observer = observer;
	}

	@Override
	public int getRowWidth() {
		return itemWidth;
	}

	public AbstractConfigEntry getLastEntry() {
		return children().get(super.getItemCount() - 1);
	}

	public AbstractConfigEntry getFirstEntry() {
		return children().getFirst();
	}

	public AbstractConfigEntry getEntry(int index) {
		return children().get(index);
	}

	@Override
	public int getItemCount() {
		return super.getItemCount();
	}

	public void addConfigEntry(AbstractConfigEntry entry) {
		this.addEntry(entry);
		entry.addObserver(observer);
	}

	public abstract static class AbstractConfigEntry extends ContainerObjectSelectionList.Entry<AbstractConfigEntry> implements Observer {
		protected ConfigResetButtonWidget resetButtonWidget;
		//protected TextWidget textWidget;
		private Component text;
		private int textX, textY;
		private int textColor;
		protected List<Observer> observers = new ArrayList<>();

		private final int resetButtonSize;

		private static final int TEXT_MARGIN_LEFT = 6;

		public AbstractConfigEntry(int resetButtonSize, Component text) {
			this.resetButtonSize = resetButtonSize;
			this.text = text;
			this.textX = 0;
			this.textY = 0;
			this.textColor = 0xffffffff;
		}

		protected void setResetButtonPressAction(Button.OnPress onPress) {
			this.resetButtonWidget = new ConfigResetButtonWidget(resetButtonSize, resetButtonSize, onPress);
		}

		@Override
		public void setX(int x) {
			super.setX(x);
			this.resetButtonWidget.setX(x + getWidth() - resetButtonWidget.getWidth());
			textX = x + TEXT_MARGIN_LEFT;
		}

		@Override
		public void setY(int y) {
			super.setY(y);
			this.resetButtonWidget.setY(y);
			textY = (int) (y + (this.resetButtonSize - Minecraft.getInstance().font.lineHeight) / 2.0);
		}

		@Override
		public void renderContent(@NonNull GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			this.resetButtonWidget.render(graphics, mouseX, mouseY, deltaTicks);
			graphics.drawString(Minecraft.getInstance().font, text, textX, textY, textColor, true);
		}

		@Override
		public @NonNull List<? extends GuiEventListener> children() {
			return List.of(resetButtonWidget);
		}

		@Override
		public @NonNull List<? extends NarratableEntry> narratables() {
			return List.of(resetButtonWidget);
		}

		public void setActive(boolean active) {
			resetButtonWidget.active = active;
			textColor = active ? 0xffffffff : 0xffafafaf;
		}

		public void addObserver(Observer observer) {
			this.observers.add(observer);
		}

		public abstract TrackableChange getTrackableChangeWidget();

		public abstract DataGetter<?> getDataGetter();

		public abstract static class AbstractBuilder<T> {
			protected int resetButtonSize = 20;
			protected Component text;
			protected Function<T, Tooltip> getTooltip = null;

			public AbstractBuilder<T> setResetButtonSize(int size) {
				this.resetButtonSize = size;
				return this;
			}

			public AbstractBuilder<T> setText(Component text) {
				this.text = text;
				return this;
			}

			public AbstractBuilder<T> setGetTooltip(Function<T, Tooltip> tooltip) {
				this.getTooltip = tooltip;
				return this;
			}

			public abstract AbstractConfigEntry build();
		}

		public record Dependency<T>(AbstractConfigEntry entry, T disableWhen) {
		}
	}
}
