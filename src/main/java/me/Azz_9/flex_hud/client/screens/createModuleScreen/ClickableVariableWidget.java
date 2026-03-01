package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import me.Azz_9.flex_hud.client.customModules.Variable;

public class ClickableVariableWidget extends AbstractWidget.WithInactiveMessage {

	private final Variable<?> variable;
	private final VariableWidget variableWidget;
	private Consumer<Variable<?>> onClick = null;

	public ClickableVariableWidget(int x, int y, Variable<?> variable) {
		super(x, y, 0, 0, variable.getName());
		this.variable = variable;
		variableWidget = new VariableWidget(x, y, variable);
		setWidth(variableWidget.getWidth());
		setHeight(variableWidget.getHeight());
	}

	public void setOnClick(Consumer<Variable<?>> onClick) {
		this.onClick = onClick;
	}

	@Override
	public void onClick(@NotNull MouseButtonEvent event, boolean doubleClick) {
		if (onClick != null) {
			onClick.accept(variable);
		}
	}

	@Override
	public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		variableWidget.setX(x);
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		variableWidget.setY(y);
	}

	@Override
	protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		variableWidget.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		handleCursor(graphics);
	}

	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
	}
}
