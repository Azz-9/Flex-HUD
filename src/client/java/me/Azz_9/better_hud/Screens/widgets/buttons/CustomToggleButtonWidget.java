package me.Azz_9.better_hud.Screens.widgets.buttons;

import me.Azz_9.better_hud.client.Interface.TrackableChange;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class CustomToggleButtonWidget extends ToggleButtonWidget implements TrackableChange {
	private final Consumer<Boolean> onToggle;
	private boolean initialState;

	public CustomToggleButtonWidget(int x, int y, int width, int height, boolean toggled, Consumer<Boolean> onToggle) {
		super(x, y, width, height, toggled);
		this.onToggle = onToggle;
		this.initialState = toggled;
		this.textures = new ButtonTextures(
				Identifier.of(MOD_ID, "widgets/buttons/toggle/unfocused_enabled.png"),
				Identifier.of(MOD_ID, "widgets/buttons/toggle/unfocused_disabled.png"),
				Identifier.of(MOD_ID, "widgets/buttons/toggle/focused_enabled.png"),
				Identifier.of(MOD_ID, "widgets/buttons/toggle/focused_disabled.png")
		);
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.isSelected() && this.active) {
			Identifier selectedTexture = Identifier.of(MOD_ID, "widgets/buttons/selected.png");
			context.drawTexture(RenderLayer::getGuiTexturedOverlay, selectedTexture, this.getX(), this.getY(), 0, 0, this.width, 20, 100, 20);
		}

		if (this.textures != null) {
			context.drawTexture(RenderLayer::getGuiTextured, this.textures.get(this.toggled, this.isSelected() && this.active), this.getX() + this.width - 20, this.getY(), 0, 0, 20, 20, 20, 20);
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
		onToggle.accept(this.toggled);
	}

	@Override
	public boolean hasChanged() {
		return toggled != initialState;
	}

	@Override
	public void cancel() {
		onToggle.accept(initialState);
	}

	public Consumer<Boolean> getOnToggle() {
		return onToggle;
	}
}
