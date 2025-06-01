package me.Azz_9.better_hud.client.screens.widgets.buttons.configButtons;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class ConfigColorButtonWidget<T> extends ClickableWidget implements TrackableChange, DataGetter<Integer> {
	private int color;
	private final Consumer<Integer> ON_COlOR_CHANGE;
	private final int INITIAL_COLOR;
	private final List<Observer> observers;
	private final T disableWhen;

	public ConfigColorButtonWidget(int x, int y, int width, int height, int currentColor, Consumer<Integer> onColorChange, List<Observer> observers, T disableWhen) {
		super(x, y, width, height, Text.empty());
		this.color = currentColor;
		this.INITIAL_COLOR = currentColor;
		this.ON_COlOR_CHANGE = onColorChange;
		this.observers = observers;
		this.disableWhen = disableWhen;
	}

	public ConfigColorButtonWidget(int width, int height, int currentColor, Consumer<Integer> onColorChange, List<Observer> observers, T disableWhen) {
		this(0, 0, width, height, currentColor, onColorChange, observers, disableWhen);
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.active) {
			if (this.isHovered()) {
				Identifier selectedTexture = Identifier.of(MOD_ID, "widgets/buttons/selected.png");
				context.drawTexture(RenderLayer::getGuiTextured, selectedTexture, this.getX(), this.getY(), 0, 0, this.width, this.height, 100, 20);
			}
			if (this.isSelected()) {
				context.drawBorder(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
			context.drawBorder(getRight() - getHeight(), getY(), getHeight(), getHeight(), (this.isHovered() ? 0xffd0d0d0 : 0xff404040));
		}
		context.fill(getRight() - getHeight() + 1, getY() + 1, getRight() - 1, getBottom() - 1, color | 0xff000000);

		if (!this.active) {
			context.fill(getRight() - getHeight(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	@Override
	public void setToInitialState() {
		this.color = this.INITIAL_COLOR;
	}

	@Override
	public boolean hasChanged() {
		return this.color != this.INITIAL_COLOR;
	}

	@Override
	public void cancel() {
		ON_COlOR_CHANGE.accept(INITIAL_COLOR);
	}

	@Override
	public Integer getData() {
		return color;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public boolean isSelected() {
		return this.isFocused();
	}
}
