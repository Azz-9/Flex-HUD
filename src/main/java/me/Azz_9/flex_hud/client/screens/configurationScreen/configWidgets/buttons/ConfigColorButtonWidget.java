package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorBindable;
import me.Azz_9.flex_hud.client.utils.EaseUtils;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class ConfigColorButtonWidget extends ClickableWidget implements TrackableChange, DataGetter<Integer>, ResetAware, ColorBindable {
	private ConfigInteger variable;
	private final int INITIAL_COLOR;
	private final List<Observer> observers;
	private final Consumer<ConfigColorButtonWidget> onClickAction;
	@Nullable
	private final Function<Integer, Tooltip> getTooltip;

	private long transitionStartTime = -1;
	private boolean hovering = false;
	private boolean transitioningIn = false;
	private boolean transitioningOut = false;
	private static final int TRANSITION_DURATION = 300;

	public ConfigColorButtonWidget(int x, int y, int width, int height, ConfigInteger variable, List<Observer> observers, Consumer<ConfigColorButtonWidget> onClickAction, @Nullable Function<Integer, Tooltip> getTooltip) {
		super(x, y, width, height, Text.empty());
		this.variable = variable;
		this.INITIAL_COLOR = variable.getValue();
		this.observers = observers;
		this.onClickAction = onClickAction;
		this.getTooltip = getTooltip;

		if (getTooltip != null) {
			this.setTooltip(getTooltip.apply(variable.getValue()));
		}
	}

	public ConfigColorButtonWidget(int width, int height, ConfigInteger variable, List<Observer> observers, Consumer<ConfigColorButtonWidget> onClickAction, @Nullable Function<Integer, Tooltip> getTooltip) {
		this(0, 0, width, height, variable, observers, onClickAction, getTooltip);
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.active) {

			drawSelectedTexture(context);

			if (this.isSelected()) {
				context.drawBorder(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
			context.drawBorder(getRight() - getHeight(), getY(), getHeight(), getHeight(), (this.isHovered() ? 0xffd0d0d0 : 0xff404040));
		}
		context.fill(getRight() - getHeight() + 1, getY() + 1, getRight() - 1, getBottom() - 1, variable.getValue() | 0xff000000);

		if (!this.active) {
			context.fill(getRight() - getHeight(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	private void drawSelectedTexture(DrawContext context) {
		boolean currentlyHovered = this.isHovered();

		// Handle transition triggers
		if (currentlyHovered && !hovering) {
			hovering = true;
			transitioningIn = true;
			transitioningOut = false;
			transitionStartTime = System.currentTimeMillis();
		} else if (!currentlyHovered && hovering) {
			hovering = false;
			transitioningOut = true;
			transitioningIn = false;
			transitionStartTime = System.currentTimeMillis();
		}

		// Calculate alpha
		int alpha = 0;
		if (transitioningIn || transitioningOut) {
			int elapsed = (int) (System.currentTimeMillis() - transitionStartTime);
			if (elapsed <= TRANSITION_DURATION) {
				float progress = (float) elapsed / TRANSITION_DURATION;
				float eased = EaseUtils.getEaseOutQuad(progress);
				if (transitioningOut) eased = 1 - eased;
				alpha = (int) (0xFF * eased);
			} else {
				alpha = transitioningIn ? 0xFF : 0x00;
				transitioningIn = false;
				transitioningOut = false;
			}
		} else if (hovering) {
			alpha = 0xFF;
		}

		if (alpha > 0) {
			Identifier selectedTexture = Identifier.of(MOD_ID, "widgets/buttons/selected.png");
			context.drawTexture(RenderPipelines.GUI_TEXTURED, selectedTexture, this.getX(), this.getY(), 0, 0, this.width, this.height, 120, 20, ColorHelper.withAlpha(alpha, 0xFFFFFF));
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		onClickAction.accept(this);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
			onClickAction.accept(this);
			return true;
		}
		return false;
	}

	@Override
	public void setToDefaultState() {
		this.onReceiveColor(variable.getDefaultValue());
	}

	@Override
	public boolean hasChanged() {
		return this.variable.getValue() != this.INITIAL_COLOR;
	}

	@Override
	public void cancel() {
		variable.setValue(INITIAL_COLOR);
	}

	@Override
	public Integer getData() {
		return getColor();
	}

	@Override
	public boolean isSelected() {
		return this.isFocused();
	}

	@Override
	public void onReceiveColor(int color) {
		if (this.variable.getValue() != color) {
			this.variable.setValue(color);

			for (Observer observer : observers) {
				observer.onChange(this);
			}

			if (this.getTooltip != null) this.setTooltip(this.getTooltip.apply(color));
		}
	}

	@Override
	public int getColor() {
		return variable.getValue();
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public boolean isCurrentValueDefault() {
		return variable.getValue().equals(variable.getDefaultValue());
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public int getBottom() {
		return this.getY() + this.getHeight();
	}

	@Override
	public int getRight() {
		return this.getX() + this.getWidth();
	}

	@Override
	public int getY() {
		return super.getY();
	}
}
