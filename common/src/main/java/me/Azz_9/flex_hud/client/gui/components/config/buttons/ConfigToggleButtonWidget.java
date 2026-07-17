package me.Azz_9.flex_hud.client.gui.components.config.buttons;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.Constants.MOD_ID;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Ease;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.gui.Cursors;
import me.Azz_9.flex_hud.client.gui.components.TrackableChange;
import me.Azz_9.flex_hud.client.gui.components.config.DataGetter;
import me.Azz_9.flex_hud.client.gui.components.config.Observer;
import me.Azz_9.flex_hud.client.gui.components.config.ResetAware;

public class ConfigToggleButtonWidget extends Button implements TrackableChange, DataGetter<Boolean>, ResetAware {
	private static final Identifier UNFOCUSED_ENABLED_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "widget/toggle/unfocused_enabled");
	private static final Identifier UNFOCUSED_DISABLED_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "widget/toggle/unfocused_disabled");
	private static final Identifier FOCUSED_ENABLED_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "widget/toggle/focused_enabled");
	private static final Identifier FOCUSED_DISABLED_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "widget/toggle/focused_disabled");

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
				UNFOCUSED_ENABLED_SPRITE, UNFOCUSED_DISABLED_SPRITE,
				FOCUSED_ENABLED_SPRITE, FOCUSED_DISABLED_SPRITE
		);
		this.observers = observers;
		this.getTooltip = getTooltip;
		this.toggled = variable.getValue();

		if (this.getTooltip != null) this.setTooltip(this.getTooltip.apply(variable.getValue()));
	}

	@Override
	public void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		if (this.active) {
			if (this.isHovered()) graphics.requestCursor(Cursors.POINTING_HAND);

			drawSelectedTexture(graphics);

			if (this.isHoveredOrFocused()) {
				graphics.outline(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
		}

		if (this.textures != null) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.textures.get(this.toggled, this.isHovered() && this.active), this.getX() + this.width - this.height, this.getY(), this.height, this.height);
		}

		if (!this.active) {
			if (this.isHovered()) graphics.requestCursor(Cursors.NOT_ALLOWED);

			graphics.fill(getRight() - getHeight(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	private void drawSelectedTexture(GuiGraphicsExtractor graphics) {
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
				float eased = Ease.outQuad(progress);
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
			graphics.fill(getX(), getY(), getRight(), getBottom(), ARGB.color(alpha / 3, 0xC5C5C5));
		}
	}

	@Override
	public void onClick(@NonNull MouseButtonEvent click, boolean bl) {
		onClickAction();
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		if (input.isConfirmation()) {
			onClickAction();
			this.playDownSound(MINECRAFT.getSoundManager());
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
	public void revertChanges() {
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