package me.Azz_9.flex_hud.client.gui.components.config;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.components.AbstractSmoothScrollableList;
import me.Azz_9.flex_hud.client.gui.components.TrackableChange;
import me.Azz_9.flex_hud.client.gui.components.config.buttons.ConfigResetButtonWidget;

public class ScrollableConfigList extends AbstractSmoothScrollableList<ScrollableConfigList.AbstractConfigEntry> {
	private final int itemWidth;

	public ScrollableConfigList(Minecraft minecraftClient, int width, int height, int y, int x, int itemHeight, int itemWidth) {
		super(minecraftClient, width, height, y, itemHeight);
		setX(x);
		this.itemWidth = itemWidth;
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

	@Override
	public int addEntry(@NotNull AbstractConfigEntry entry) {
		return super.addEntry(entry);
	}

	public abstract static class AbstractConfigEntry extends ContainerObjectSelectionList.Entry<AbstractConfigEntry> implements Observer {
		protected ConfigResetButtonWidget resetButtonWidget;
		//protected TextWidget textWidget;
		private final Component text;
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
			this.textColor = Colors.WHITE;
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
			textY = (int) (y + (this.resetButtonSize - MINECRAFT.font.lineHeight) / 2.0);
		}

		@Override
		public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			this.resetButtonWidget.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
			graphics.text(MINECRAFT.font, text, textX, textY, textColor, true);
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
			textColor = active ? Colors.WHITE : 0xffafafaf;
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
			protected final List<Observer> observers = new ArrayList<>();

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

			public AbstractBuilder<T> addObserver(Observer observer) {
				observers.add(observer);
				return this;
			}

			public abstract AbstractConfigEntry build();
		}

		public record Dependency<T>(AbstractConfigEntry entry, T disableWhen) {
		}
	}
}
