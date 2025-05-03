package me.Azz_9.better_hud.screens.modsConfigScreen;

import me.Azz_9.better_hud.screens.widgets.buttons.ColorButtonWidget;
import me.Azz_9.better_hud.screens.widgets.buttons.ResetButtonWidget;
import me.Azz_9.better_hud.screens.widgets.fields.IntFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextWidget;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ScrollableConfigList extends ElementListWidget<ScrollableConfigList.ButtonEntry> {
	private final List<ButtonEntry> buttonsList = new ArrayList<>();
	private int itemWidth;

	private boolean isDraggingGradientWidget = false;
	private boolean isDraggingHueBarWidget = false;

	public ScrollableConfigList(MinecraftClient client, int width, int height, int y, int x, int itemHeight, int itemWidth) {
		super(client, width, height, y, itemHeight);
		setX(x);
		this.itemWidth = itemWidth;
	}

	public void addButton(ButtonEntry buttonEntry) {
		this.buttonsList.add(buttonEntry);
		this.addEntry(buttonEntry);
	}

	@Override
	public int getRowWidth() {
		return itemWidth;
	}

	@Override
	public void setWidth(int buttonWidth) {
		super.setWidth(buttonWidth + 62);
		itemWidth = buttonWidth + 30;
	}

	public void setActiveToEveryEntry(boolean active) {
		for (ButtonEntry buttonEntry : buttonsList) {
			if (buttonEntry.configButton instanceof IntFieldWidget intFieldWidget) {
				intFieldWidget.getIncrease().active = active;
				intFieldWidget.getDecrease().active = active;
			}
			if (!(buttonEntry.configButton instanceof ColorButtonWidget colorButtonWidget) || !colorButtonWidget.isSelectingColor) {
				buttonEntry.configButton.active = active;
				if (buttonEntry.configButton instanceof ColorButtonWidget colorButtonWidget) {
					System.out.println(buttonEntry.textWidget.getMessage().getString() + " " + colorButtonWidget.isSelectingColor + " " + colorButtonWidget.active + " " + colorButtonWidget.hashCode());
				}
			}
			buttonEntry.resetButtonWidget.active = active;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (ButtonEntry buttonEntry : buttonsList) {
			if (buttonEntry.configButton instanceof ColorButtonWidget colorButtonWidget && colorButtonWidget.isSelectingColor) {
				if (colorButtonWidget.getGRADIENT_WIDGET().mouseClicked(mouseX, mouseY, button)) {
					isDraggingGradientWidget = true;
					return true;
				}
				if (colorButtonWidget.getHUE_BAR_WIDGET().mouseClicked(mouseX, mouseY, button)) {
					isDraggingHueBarWidget = true;
					return true;
				}
				if (colorButtonWidget.getCOLOR_ENTRY_WIDGET().mouseClicked(mouseX, mouseY, button)) {
					return true;
				}
			}

			if (buttonEntry.configButton instanceof IntFieldWidget intFieldWidget) {
				intFieldWidget.getIncrease().mouseClicked(mouseX, mouseY, button);
				intFieldWidget.getDecrease().mouseClicked(mouseX, mouseY, button);
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		for (ButtonEntry buttonEntry : buttonsList) {
			if (buttonEntry.configButton instanceof ColorButtonWidget colorButtonWidget && colorButtonWidget.isSelectingColor) {
				if (isDraggingGradientWidget) {
					colorButtonWidget.getGRADIENT_WIDGET().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
					return true;
				}
				if (isDraggingHueBarWidget) {
					colorButtonWidget.getGRADIENT_WIDGET().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
					return true;
				}
			}
		}

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		// show the cursor back after having hidden it when selecting a color on the color selector
		long window = MinecraftClient.getInstance().getWindow().getHandle();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		isDraggingGradientWidget = false;
		isDraggingHueBarWidget = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	public List<ButtonEntry> getButtonsList() {
		return buttonsList;
	}

	public static class ButtonEntry extends ElementListWidget.Entry<ButtonEntry> {
		ClickableWidget configButton;
		ResetButtonWidget resetButtonWidget;
		TextWidget textWidget;

		private final int TEXT_MARGIN_LEFT = 6;
		private final int GAP = 10; // GAP between the button and the reset button

		public ButtonEntry(ClickableWidget configButton, ResetButtonWidget resetButtonWidget, TextWidget textWidget) {
			this.configButton = configButton;
			this.resetButtonWidget = resetButtonWidget;
			this.textWidget = textWidget;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.configButton.setPosition(x + entryWidth - resetButtonWidget.getWidth() - GAP - configButton.getWidth(), y);
			this.resetButtonWidget.setPosition(x + entryWidth - resetButtonWidget.getWidth(), y);
			this.textWidget.setPosition(x + TEXT_MARGIN_LEFT, (int) (y + (configButton.getHeight() - MinecraftClient.getInstance().textRenderer.fontHeight) / 2.0));

			if (configButton instanceof IntFieldWidget intFieldWidget) {
				intFieldWidget.setX(intFieldWidget.getX() - intFieldWidget.getBUTTONS_WIDTH());

				intFieldWidget.getIncrease().render(context, mouseX, mouseY, tickDelta);
				intFieldWidget.getDecrease().render(context, mouseX, mouseY, tickDelta);
			}

			this.configButton.render(context, mouseX, mouseY, tickDelta);
			this.resetButtonWidget.render(context, mouseX, mouseY, tickDelta);
			this.textWidget.render(context, mouseX, mouseY, tickDelta);
		}

		@Override
		public List<ClickableWidget> selectableChildren() {
			return this.children();
		}

		@Override
		public List<ClickableWidget> children() {
			if (configButton instanceof ColorButtonWidget colorButtonWidget) {
				return List.of(
						colorButtonWidget,
						colorButtonWidget.getCOLOR_ENTRY_WIDGET(),
						colorButtonWidget.getGRADIENT_WIDGET(),
						colorButtonWidget.getHUE_BAR_WIDGET(),
						resetButtonWidget
				);
			}
			return List.of(this.configButton, this.resetButtonWidget);
		}
	}
}