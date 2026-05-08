package me.Azz_9.flex_hud.client.screens.createModuleScreen.moduleContentField;

import net.minecraft.text.Text;

import java.util.List;

import me.Azz_9.flex_hud.client.customModules.modifiers.Modifier;
import me.Azz_9.flex_hud.client.screens.createModuleScreen.ModuleContentEditorModel;

sealed interface DisplayItem permits TextDisplayItem, VariableDisplayItem {
	int modelIndex();

	int x();

	int width();

	int height();

	default int endX() {
		return x() + width();
	}
}

record TextDisplayItem(int modelIndex, int x, int width, int height, String text, int color) implements DisplayItem {
}

record VariableDisplayItem(int modelIndex,
                           int x,
                           int width,
                           int height,
                           int color,
                           ModuleContentEditorModel.VariableElement element,
                           String name,
                           int nameWidth,
                           List<ModifierPart> modifiers,
                           int plusX) implements DisplayItem {
}

record ModifierPart(String displayText, int width, int color, Text tooltip, int startX, int index) {
}

record HoverTarget(Text tooltip) {
}

record SelectionBounds(int left, int top, int right, int bottom) {
	int centerX() {
		return (left + right) / 2;
	}
}

record Bounds(int x, int y, int width, int height) {
	int right() {
		return x + width;
	}

	int bottom() {
		return y + height;
	}

	boolean contains(double mouseX, double mouseY) {
		return x <= mouseX && mouseX <= right() && y <= mouseY && mouseY <= bottom();
	}
}

enum VariableHitKind {
	BODY,
	MODIFIER,
	PLUS
}

record VariableHit(VariableHitKind kind, VariableDisplayItem variableItem, int modifierIndex) {
}

record ModifierPickerEntry(Modifier<?, ?> modifier, Bounds bounds, Text tooltip) {
}

record GradientRegion(ModuleContentEditorModel.GradientColorLayer gradient,
                      int startIndex,
                      int endIndex,
                      int totalWidth,
                      int[] elementWidths) {
	static GradientRegion create(ModuleContentEditorModel.GradientColorLayer gradient,
	                             List<ModuleContentEditorModel.InlineElement> elements,
	                             java.util.function.ToIntFunction<ModuleContentEditorModel.InlineElement> widthMeasurer) {
		int first = -1;
		int last = -1;
		for (int index = 0; index < elements.size(); index++) {
			if (elements.get(index).style().colorLayers().contains(gradient)) {
				if (first == -1) {
					first = index;
				}
				last = index;
			}
		}

		if (first == -1) {
			return new GradientRegion(gradient, 0, 0, 0, new int[0]);
		}

		int[] widths = new int[last - first + 1];
		int totalWidth = 0;
		for (int index = first; index <= last; index++) {
			int width = widthMeasurer.applyAsInt(elements.get(index));
			widths[index - first] = width;
			totalWidth += width;
		}

		return new GradientRegion(gradient, first, last + 1, totalWidth, widths);
	}

	int colorAt(int elementIndex) {
		double currentX = 0.0;
		for (int index = startIndex; index < endIndex; index++) {
			int width = elementWidths[index - startIndex];
			if (index == elementIndex) {
				float progress = totalWidth <= 0 ? 0.0f : (float) ((currentX + width / 2.0) / totalWidth);
				return ModuleContentField.interpolateRgb(gradient.startColor(), gradient.endColor(), progress);
			}
			currentX += width;
		}
		return ModuleContentField.TEXT_COLOR;
	}
}
