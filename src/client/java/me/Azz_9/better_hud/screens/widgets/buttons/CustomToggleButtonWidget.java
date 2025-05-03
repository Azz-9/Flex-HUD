package me.Azz_9.better_hud.screens.widgets.buttons;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class CustomToggleButtonWidget extends ToggleButtonWidget implements TrackableChange {
	private final Consumer<Boolean> ON_TOGGLE;
	private final boolean INITIAL_STATE;

	private final List<Dependents> dependents = new ArrayList<>();

	public CustomToggleButtonWidget(int width, int height, boolean toggled, Consumer<Boolean> onToggle) {
		super(0, 0, width, height, toggled);
		this.ON_TOGGLE = onToggle;
		this.INITIAL_STATE = toggled;
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
			context.drawTexture(RenderLayer::getGuiTextured, selectedTexture, this.getX(), this.getY(), 0, 0, this.width, 20, 100, 20);
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
		ON_TOGGLE.accept(this.toggled);
		dependents.forEach((element) -> element.onDependencyChange(toggled));
	}

	public void addDependents(Dependents d) {
		d.onDependencyChange(this.toggled);
		dependents.add(d);
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
}
