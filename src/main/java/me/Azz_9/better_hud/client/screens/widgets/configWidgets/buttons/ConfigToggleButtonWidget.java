package me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class ConfigToggleButtonWidget<T> extends ToggleButtonWidget implements TrackableChange, DataGetter<Boolean> {
	private final Consumer<Boolean> ON_TOGGLE;
	private final boolean INITIAL_STATE;
	private final List<Observer> observers;
	private final T disableWhen;

	public ConfigToggleButtonWidget(int width, int height, boolean toggled, Consumer<Boolean> onToggle, List<Observer> observers, T disableWhen) {
		super(0, 0, width, height, toggled);
		this.ON_TOGGLE = onToggle;
		this.INITIAL_STATE = toggled;
		this.textures = new ButtonTextures(
				Identifier.of(MOD_ID, "widgets/buttons/toggle/unfocused_enabled.png"),
				Identifier.of(MOD_ID, "widgets/buttons/toggle/unfocused_disabled.png"),
				Identifier.of(MOD_ID, "widgets/buttons/toggle/focused_enabled.png"),
				Identifier.of(MOD_ID, "widgets/buttons/toggle/focused_disabled.png")
		);
		this.observers = observers;
		this.disableWhen = disableWhen;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.active) {
			if (this.isHovered()) {
				Identifier selectedTexture = Identifier.of(MOD_ID, "widgets/buttons/selected.png");
				context.drawTexture(RenderLayer::getGuiTextured, selectedTexture, this.getX(), this.getY(), 0, 0, this.width, this.height, 100, 20);
			}
			if (this.isSelected()) {
				context.drawBorder(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
		}

		if (this.textures != null) {
			context.drawTexture(RenderLayer::getGuiTextured, this.textures.get(this.toggled, this.isHovered() && this.active), this.getX() + this.width - this.height, this.getY(), 0, 0, this.height, this.height, 20, 20);
		}

		if (!this.active) {
			context.fill(getRight() - getHeight(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
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
		ON_TOGGLE.accept(this.toggled);

		for (Observer observer : observers) {
			observer.onChange(this);
		}
	}

	@Override
	public void setToInitialState() {
		if (hasChanged()) {
			onClickAction();
		}
	}

	@Override
	public boolean hasChanged() {
		return toggled != INITIAL_STATE;
	}

	@Override
	public void cancel() {
		ON_TOGGLE.accept(INITIAL_STATE);
	}

	public Consumer<Boolean> getON_TOGGLE() {
		return ON_TOGGLE;
	}

	public T getDisableWhen() {
		return disableWhen;
	}

	@Override
	public Boolean getData() {
		return toggled;
	}

	@Override
	public boolean isSelected() {
		return this.isFocused();
	}
}