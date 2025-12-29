package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import me.Azz_9.flex_hud.client.utils.Cursors;
import me.Azz_9.flex_hud.client.utils.EaseUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class ConfigToggleButtonWidget extends Button implements TrackableChange, DataGetter<Boolean>, ResetAware {
	private final ConfigBoolean variable;
	private final boolean INITIAL_STATE;
	private final List<Observer> observers;
	@Nullable
	private final Function<Boolean, Tooltip> getTooltip;
	private final WidgetSprites textures;

	private boolean toggled;

	// hover effect
	private long transitionStartTime = -1;
	private boolean hovering = false;
	private boolean transitioningIn = false;
	private boolean transitioningOut = false;
	private static final int TRANSITION_DURATION = 300;


	public ConfigToggleButtonWidget(int width, int height, ConfigBoolean variable, List<Observer> observers, @Nullable Function<Boolean, Tooltip> getTooltip) {
		super(0, 0, width, height, Component.translatable(Objects.requireNonNull(variable.getConfigTextTranslationKey())), btn -> {
		}, DEFAULT_NARRATION);
		this.variable = variable;
		this.INITIAL_STATE = variable.getValue();
		this.textures = new WidgetSprites(
				Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/toggle/unfocused_enabled.png"),
				Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/toggle/unfocused_disabled.png"),
				Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/toggle/focused_enabled.png"),
				Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/toggle/focused_disabled.png")
		);
		this.observers = observers;
		this.getTooltip = getTooltip;
		this.toggled = variable.getValue();

		if (this.getTooltip != null) this.setTooltip(this.getTooltip.apply(variable.getValue()));
	}

	@Override
	public void renderContents(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (this.active) {
			if (this.isHovered()) graphics.requestCursor(Cursors.POINTING_HAND);

			drawSelectedTexture(graphics);

			if (this.isHoveredOrFocused()) {
				graphics.renderOutline(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
		}

		if (this.textures != null) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.textures.get(this.toggled, this.isHovered() && this.active), this.getX() + this.width - this.height, this.getY(), 0, 0, this.height, this.height, 20, 20);
		}

		if (!this.active) {
			if (this.isHovered()) graphics.requestCursor(Cursors.NOT_ALLOWED);

			graphics.fill(getRight() - getHeight(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	private void drawSelectedTexture(GuiGraphics graphics) {
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
			Identifier selectedTexture = Identifier.fromNamespaceAndPath(MOD_ID, "widgets/buttons/selected.png");
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, selectedTexture, this.getX(), this.getY(), 0, 0, this.width, this.height, 120, 20, ARGB.color(alpha, 0xFFFFFF));
		}
	}

	@Override
	public void onClick(@NonNull MouseButtonEvent click, boolean bl) {
		onClickAction();
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		if (input.key() == GLFW.GLFW_KEY_ENTER || input.key() == GLFW.GLFW_KEY_KP_ENTER) {
			onClickAction();
			this.playDownSound(Minecraft.getInstance().getSoundManager());
		}

		return super.keyPressed(input);
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

	@Override
	public Boolean getData() {
		return variable.getValue();
	}

	@Override
	public boolean isHoveredOrFocused() {
		return this.isFocused();
	}

	@Override
	public boolean isCurrentValueDefault() {
		return toggled == variable.getDefaultValue();
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}
}