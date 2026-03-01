package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.function.Consumer;

import me.Azz_9.flex_hud.client.customModules.Variable;

public class ClickableVariableWidget extends ClickableWidget {

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
	public void onClick(double mouseX, double mouseY) {
		if (onClick != null) {
			onClick.accept(variable);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button);
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
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		variableWidget.render(context, mouseX, mouseY, deltaTicks);
	}

	public void renderDescriptionOverlay(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		variableWidget.renderDescriptionOverlay(context, mouseX, mouseY, deltaTicks);
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}
}
