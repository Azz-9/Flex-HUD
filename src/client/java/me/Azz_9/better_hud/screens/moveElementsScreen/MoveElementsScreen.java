package me.Azz_9.better_hud.screens.moveElementsScreen;

import com.google.common.collect.Lists;
import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import me.Azz_9.better_hud.client.overlay.HudElement;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.widgets.help.HelpWidget;
import me.Azz_9.better_hud.screens.widgets.movableWidget.MovableWidget;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static me.Azz_9.better_hud.client.Better_hudClient.hudElements;

public class MoveElementsScreen extends Screen {
	private final ModConfig INSTANCE = ModConfig.getInstance();
	private final Screen parent;

	private ButtonWidget saveButton;
	private HelpWidget helpWidget;

	private final List<Drawable> drawables = Lists.newArrayList();
	private final List<SnapLines> elements = new ArrayList<>();

	private final List<TrackableChange> trackableWidgets = new ArrayList<>();

	private final List<MovableWidget> prevModifiedMovableWidgets = new LinkedList<>();
	private final List<MovableWidget> redoModifiedMovableWidgets = new LinkedList<>();

	public MoveElementsScreen(Screen parent) {
		super(Text.literal("Move Elements"));
		this.parent = parent;
	}

	public interface SnapLines {
		boolean XcenteredLine();

		boolean YcenteredLine();
	}

	@Override
	protected void init() {
		ButtonWidget cancelButton = ButtonWidget.builder(Text.literal("Cancel"), (btn) -> this.cancel())
				.dimensions(this.width / 2 - 125, this.height - 30, 120, 20)
				.build();
		saveButton = ButtonWidget.builder(Text.literal("Save and quit"), (btn) -> this.saveAndClose())
				.dimensions(this.width / 2 + 5, this.height - 30, 120, 20)
				.build();
		saveButton.active = false;

		addChild(cancelButton);
		addChild(saveButton);

		helpWidget = new HelpWidget(4, this.height - 24, 20, 20);
		addChild(helpWidget);

		if (!INSTANCE.isEnabled) {
			return;
		}
		for (HudRenderCallback element : hudElements) {
			if (element instanceof HudElement hudElement && hudElement.isEnabled()) {
				MovableWidget widget = new MovableWidget((int) hudElement.x, (int) hudElement.y, hudElement.scale, hudElement.getWidth(), hudElement.getHeight(), hudElement, this);
				addChild(widget);
			}
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		for (Drawable drawable : drawables) {
			drawable.render(context, mouseX, mouseY, delta);
		}

		//draw centered snap lines
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(width / 2.0f - 0.5, 0, 0);
		if (elements.stream().anyMatch(SnapLines::XcenteredLine)) {
			context.fill(0, 0, 1, height, 0xffff0000);
		}
		matrices.pop();
		matrices.push();
		matrices.translate(0, height / 2.0f - 0.5, 0);
		if (elements.stream().anyMatch(SnapLines::YcenteredLine)) {
			context.fill(0, 0, width, 1, 0xffff0000);
		}
		matrices.pop();

		checkForChanges();
	}

	@Override
	protected void clearChildren() {
		super.clearChildren();
		drawables.clear();
		elements.clear();
	}

	private void addChild(MovableWidget widget) {
		elements.add(widget);
		trackableWidgets.add(widget);
		drawables.add(widget);
		addDrawableChild(widget);
	}

	private void addChild(ButtonWidget button) {
		drawables.add(button);
		addDrawableChild(button);
	}

	private void addChild(HelpWidget button) {
		drawables.add(button);
		addDrawableChild(button);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && helpWidget.getDisplayHelp() && !helpWidget.isHovered()) {
			helpWidget.onClick(mouseX, mouseY);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256 && this.shouldCloseOnEsc()) {
			cancel();
		} else if (keyCode == 87 && modifiers == 2 && !prevModifiedMovableWidgets.isEmpty()) {
			redoModifiedMovableWidgets.add(prevModifiedMovableWidgets.removeLast());
			redoModifiedMovableWidgets.getLast().undo();
		} else if (((keyCode == 89 && modifiers == 2) || (keyCode == 87 && modifiers == 3)) && !redoModifiedMovableWidgets.isEmpty()) {
			prevModifiedMovableWidgets.add(redoModifiedMovableWidgets.removeLast());
			prevModifiedMovableWidgets.getLast().redo();
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void addModifiedWidget(MovableWidget movableWidget) {
		prevModifiedMovableWidgets.add(movableWidget);
		if (prevModifiedMovableWidgets.size() > 100) {
			prevModifiedMovableWidgets.removeFirst();
		}
		redoModifiedMovableWidgets.clear();
	}

	private void saveAndClose() {
		ModConfig.saveConfig();
		close();
	}

	public void cancel() {
		trackableWidgets.forEach(TrackableChange::cancel);
		close();
	}

	@Override
	public void close() {
		Better_hudClient.isEditing = false;
		client.setScreen(parent);
	}

	private void checkForChanges() {
		saveButton.active = trackableWidgets.stream().anyMatch(TrackableChange::hasChanged); // Met à jour l'état du bouton de sauvegarde
	}
}
