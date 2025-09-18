package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import me.Azz_9.flex_hud.client.utils.EaseUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Function;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class ConfigToggleButtonWidget<T> extends ToggleButtonWidget implements TrackableChange, DataGetter<Boolean>, ResetAware {
	private final ConfigBoolean variable;
	private final boolean INITIAL_STATE;
	private final List<Observer> observers;
	private final T disableWhen;
	@Nullable
	private final Function<Boolean, Tooltip> getTooltip;

	private long transitionStartTime = -1;
	private boolean hovering = false;
	private boolean transitioningIn = false;
	private boolean transitioningOut = false;
	private static final int TRANSITION_DURATION = 300;


	public ConfigToggleButtonWidget(int width, int height, ConfigBoolean variable, List<Observer> observers, T disableWhen, @Nullable Function<Boolean, Tooltip> getTooltip) {
		super(0, 0, width, height, variable.getValue());
		this.variable = variable;
		this.INITIAL_STATE = variable.getValue();
		this.textures = new ButtonTextures(
				Identifier.of(MOD_ID, "widgets/buttons/toggle/unfocused_enabled.png"),
				Identifier.of(MOD_ID, "widgets/buttons/toggle/unfocused_disabled.png"),
				Identifier.of(MOD_ID, "widgets/buttons/toggle/focused_enabled.png"),
				Identifier.of(MOD_ID, "widgets/buttons/toggle/focused_disabled.png")
		);
		this.observers = observers;
		this.disableWhen = disableWhen;
		this.getTooltip = getTooltip;

		if (this.getTooltip != null) this.setTooltip(this.getTooltip.apply(variable.getValue()));
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.active) {

			drawSelectedTexture(context);

			if (this.isSelected()) {
				context.drawBorder(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
		}

		if (this.textures != null) {
			context.drawTexture(RenderPipelines.GUI_TEXTURED, this.textures.get(this.toggled, this.isHovered() && this.active), this.getX() + this.width - this.height, this.getY(), 0, 0, this.height, this.height, 20, 20);
		}

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
		onClickAction();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
			onClickAction();
			this.playDownSound(MinecraftClient.getInstance().getSoundManager());
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void onClickAction() {
		this.toggled = !this.toggled;
		variable.setValue(toggled);

		for (Observer observer : observers) {
			observer.onChange(this);
		}

		if (this.getTooltip != null) this.setTooltip(this.getTooltip.apply(variable.getValue()));
	}

	@Override
	public void setToDefaultState() {
		toggled = variable.getDefaultValue();
		variable.setToDefault();

		for (Observer observer : observers) {
			observer.onChange(this);
		}

		if (this.getTooltip != null) this.setTooltip(this.getTooltip.apply(variable.getValue()));
	}

	@Override
	public boolean hasChanged() {
		return variable.getValue() != INITIAL_STATE;
	}

	@Override
	public void cancel() {
		variable.setValue(INITIAL_STATE);
	}

	public T getDisableWhen() {
		return disableWhen;
	}

	@Override
	public Boolean getData() {
		return variable.getValue();
	}

	@Override
	public boolean isSelected() {
		return this.isFocused();
	}

	@Override
	public boolean isCurrentValueDefault() {
		return toggled == variable.getDefaultValue();
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}
}