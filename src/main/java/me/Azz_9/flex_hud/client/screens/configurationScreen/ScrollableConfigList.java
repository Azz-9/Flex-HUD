package me.Azz_9.flex_hud.client.screens.configurationScreen;

import me.Azz_9.flex_hud.client.screens.AbstractSmoothScrollableList;
import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.ConfigResetButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ScrollableConfigList extends AbstractSmoothScrollableList<ScrollableConfigList.AbstractConfigEntry> {
	private final int itemWidth;
	private final Observer observer;

	public ScrollableConfigList(MinecraftClient minecraftClient, int width, int height, int y, int x, int itemHeight, int itemWidth, Observer observer) {
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
		return children().get(super.getEntryCount() - 1);
	}

	public void addConfigEntry(AbstractConfigEntry entry) {
		this.addEntry(entry);
		entry.addObserver(observer);
	}

	public abstract static class AbstractConfigEntry extends ElementListWidget.Entry<AbstractConfigEntry> implements Observer {
		protected ConfigResetButtonWidget resetButtonWidget;
		protected TextWidget textWidget;
		protected List<Observer> observers = new ArrayList<>();

		private final int resetButtonSize;

		private static final int TEXT_MARGIN_LEFT = 6;

		public AbstractConfigEntry(int resetButtonSize, Text text) {
			this.resetButtonSize = resetButtonSize;
			this.textWidget = new TextWidget(text, MinecraftClient.getInstance().textRenderer);
		}

		protected void setResetButtonPressAction(ButtonWidget.PressAction pressAction) {
			this.resetButtonWidget = new ConfigResetButtonWidget(resetButtonSize, resetButtonSize, pressAction);
		}

		@Override
		public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			this.resetButtonWidget.render(context, mouseX, mouseY, deltaTicks);
			this.textWidget.render(context, mouseX, mouseY, deltaTicks);
			//TODO check render
		}

		/*@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
			this.resetButtonWidget.setPosition(x + entryWidth - resetButtonWidget.getWidth(), y);
			this.textWidget.setPosition(x + TEXT_MARGIN_LEFT, (int) (y + (entryHeight - MinecraftClient.getInstance().textRenderer.fontHeight) / 2.0));

			this.resetButtonWidget.render(context, mouseX, mouseY, tickProgress);
			this.textWidget.render(context, mouseX, mouseY, tickProgress);
		}*/

		@Override
		public List<? extends Element> children() {
			return List.of(resetButtonWidget, textWidget);
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return List.of(resetButtonWidget);
		}

		public void setActive(boolean active) {
			resetButtonWidget.active = active;
			textWidget.setTextColor((active ? 0xffffff : 0xafafaf));
		}

		public void addObserver(Observer observer) {
			this.observers.add(observer);
		}

		public abstract TrackableChange getTrackableChangeWidget();

		public abstract DataGetter<?> getDataGetter();

		public abstract static class AbstractBuilder<T> {
			protected int resetButtonSize = 20;
			protected Text text;
			protected Function<T, Tooltip> getTooltip = null;

			public AbstractBuilder<T> setResetButtonSize(int size) {
				this.resetButtonSize = size;
				return this;
			}

			public AbstractBuilder<T> setText(Text text) {
				this.text = text;
				return this;
			}

			public AbstractBuilder<T> setGetTooltip(Function<T, Tooltip> tooltip) {
				this.getTooltip = tooltip;
				return this;
			}

			public abstract AbstractConfigEntry build();
		}
	}
}
