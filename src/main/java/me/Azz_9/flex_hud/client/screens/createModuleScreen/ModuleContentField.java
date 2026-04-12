package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Consumer;

import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;
import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.tickables.ChromaColorTickable;

public class ModuleContentField extends ClickableWidget implements TrackableChange {

	private static final ButtonTextures TEXTURES = new ButtonTextures(
			Identifier.ofVanilla("widget/text_field"), Identifier.ofVanilla("widget/text_field_highlighted")
	);

	private static final int TEXT_PADDING_X = 4;
	private static final int TEXT_PADDING_Y = 4;
	private static final int SELECTION_COLOR = 0x66357dff;
	private static final int CARET_COLOR = 0xffffffff;
	private static final int TEXT_COLOR = 0xffffffff;
	private static final int PLACEHOLDER_COLOR = 0xff8a8f98;

	private static final int VARIABLE_BG_COLOR = 0xff24354c;
	private static final int VARIABLE_BORDER_COLOR = 0xff4d6d92;
	private static final int VARIABLE_SELECTED_BG_COLOR = 0xff365277;
	private static final int VARIABLE_PADDING_X = 4;
	private static final int VARIABLE_PADDING_Y = 2;
	private static final int VARIABLE_GAP = 4;
	private static final int VARIABLE_PLUS_GAP = 5;
	private static final int DESCRIPTION_DELAY = 500;
	private static final int DESCRIPTION_MAX_WIDTH = 220;
	private static final int DESCRIPTION_PADDING = 4;
	private static final int DESCRIPTION_BACKGROUND = 0xf01e1f22;

	private final @Nullable String initialContent;

	private ModuleContentEditorModel model;
	private String rawText;
	private int maxLength = 200;
	private Consumer<String> changedListener = text -> {
	};
	private int caretIndex;
	private int selectionAnchor;
	private int horizontalScroll;
	private boolean draggingSelection;

	private List<DisplayItem> displayItems = List.of();
	private int[] caretPositions = new int[]{0};
	private int contentWidth;
	private @Nullable HoverTarget hoveredTarget;
	private long hoverStartTime;

	public ModuleContentField(int x, int y, int width, int height, @Nullable String initialContent) {
		super(x, y, width, height, Text.empty());
		this.initialContent = initialContent;
		this.rawText = initialContent == null ? "" : initialContent;
		this.model = ModuleContentEditorModel.parse(rawText);
		this.caretIndex = model.size();
		this.selectionAnchor = caretIndex;
		rebuildLayout();
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		Identifier texture = TEXTURES.get(this.isInteractable(), this.isFocused());
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), getWidth(), getHeight());

		updateHover(mouseX, mouseY);

		int innerLeft = getX() + TEXT_PADDING_X;
		int innerTop = getY() + TEXT_PADDING_Y;
		int innerRight = getRight() - TEXT_PADDING_X;
		int innerBottom = getBottom() - TEXT_PADDING_Y;

		context.enableScissor(innerLeft, innerTop, innerRight, innerBottom);
		renderSelection(context, innerLeft, innerTop);
		renderContent(context, innerLeft, innerTop, mouseX, mouseY);
		renderCaret(context, innerLeft, innerTop);
		context.disableScissor();

		if (rawText.isEmpty() && !isFocused()) {
			context.drawText(CLIENT.textRenderer, Text.translatable("flex_hud.create_module_screen.module_content"), innerLeft, innerTop, PLACEHOLDER_COLOR, false);
		}

		renderTooltip(context, mouseX, mouseY);
	}

	private void renderContent(DrawContext context, int innerLeft, int innerTop, int mouseX, int mouseY) {
		for (DisplayItem item : displayItems) {
			int drawX = innerLeft + item.x() - horizontalScroll;
			if (drawX + item.width() < innerLeft || drawX > getRight() - TEXT_PADDING_X) {
				continue;
			}

			if (item instanceof TextDisplayItem textDisplayItem) {
				context.drawText(CLIENT.textRenderer, textDisplayItem.text(), drawX, innerTop, textDisplayItem.color(), false);
				continue;
			}

			VariableDisplayItem variableDisplayItem = (VariableDisplayItem) item;
			int backgroundColor = isIndexSelected(variableDisplayItem.modelIndex()) ? VARIABLE_SELECTED_BG_COLOR : VARIABLE_BG_COLOR;
			context.fill(drawX, innerTop - 1, drawX + variableDisplayItem.width(), innerTop + variableDisplayItem.height(), backgroundColor);
			context.drawStrokedRectangle(drawX, innerTop - 1, variableDisplayItem.width(), variableDisplayItem.height(), VARIABLE_BORDER_COLOR);

			int textX = drawX + VARIABLE_PADDING_X;
			context.drawText(CLIENT.textRenderer, variableDisplayItem.name(), textX, innerTop + VARIABLE_PADDING_Y - 1, variableDisplayItem.color(), false);
			textX += variableDisplayItem.nameWidth();

			for (ModifierPart modifierPart : variableDisplayItem.modifiers()) {
				textX += VARIABLE_GAP;
				context.drawText(CLIENT.textRenderer, modifierPart.displayText(), textX, innerTop + VARIABLE_PADDING_Y - 1, modifierPart.color(), false);
				textX += modifierPart.width();
			}

			context.drawText(CLIENT.textRenderer, "+", drawX + variableDisplayItem.plusX(), innerTop + VARIABLE_PADDING_Y - 1, variableDisplayItem.color(), false);
		}
	}

	private void renderSelection(DrawContext context, int innerLeft, int innerTop) {
		int selectionStart = Math.min(caretIndex, selectionAnchor);
		int selectionEnd = Math.max(caretIndex, selectionAnchor);
		if (selectionStart == selectionEnd) {
			return;
		}

		for (DisplayItem item : displayItems) {
			if (!isIndexSelected(item.modelIndex())) {
				continue;
			}

			int drawX = innerLeft + item.x() - horizontalScroll;
			context.fill(drawX, innerTop - 1, drawX + item.width(), innerTop + item.height(), SELECTION_COLOR);
		}
	}

	private void renderCaret(DrawContext context, int innerLeft, int innerTop) {
		if (!isFocused() || hasSelection()) {
			return;
		}

		if ((System.currentTimeMillis() / 500L) % 2L == 0L) {
			return;
		}

		int caretX = innerLeft + caretPositions[Math.clamp(caretIndex, 0, caretPositions.length - 1)] - horizontalScroll;
		context.fill(caretX, innerTop - 1, caretX + 1, innerTop + CLIENT.textRenderer.fontHeight + 1, CARET_COLOR);
	}

	private void renderTooltip(DrawContext context, int mouseX, int mouseY) {
		if (hoveredTarget == null || System.currentTimeMillis() - hoverStartTime < DESCRIPTION_DELAY) {
			return;
		}

		Text tooltip = hoveredTarget.tooltip();
		int innerWidth = Math.min(DESCRIPTION_MAX_WIDTH, CLIENT.textRenderer.getWidth(tooltip));
		int width = innerWidth + DESCRIPTION_PADDING * 2;
		int height = CLIENT.textRenderer.getWrappedLinesHeight(tooltip, innerWidth) + DESCRIPTION_PADDING * 2;

		int x = mouseX + 8;
		int y = mouseY + 8;
		Screen screen = CLIENT.currentScreen;
		if (screen != null) {
			if (x + width > screen.width) {
				x = mouseX - width - 8;
			}
			if (y + height > screen.height) {
				y = mouseY - height - 8;
			}
		}

		ScreenRect currentScissor = context.scissorStack.peekLast();
		context.disableScissor();
		context.fill(x, y, x + width, y + height, DESCRIPTION_BACKGROUND);
		context.drawWrappedText(CLIENT.textRenderer, tooltip, x + DESCRIPTION_PADDING, y + DESCRIPTION_PADDING, innerWidth, TEXT_COLOR, false);
		if (currentScissor != null) {
			context.enableScissor(currentScissor.getLeft(), currentScissor.getTop(), currentScissor.getRight(), currentScissor.getBottom());
		}
	}

	private void updateHover(int mouseX, int mouseY) {
		HoverTarget newTarget = findHoverTarget(mouseX, mouseY);
		if (!Objects.equals(newTarget, hoveredTarget)) {
			hoveredTarget = newTarget;
			hoverStartTime = System.currentTimeMillis();
		}
	}

	private @Nullable HoverTarget findHoverTarget(int mouseX, int mouseY) {
		int innerLeft = getX() + TEXT_PADDING_X;
		int innerTop = getY() + TEXT_PADDING_Y;
		for (DisplayItem item : displayItems) {
			int drawX = innerLeft + item.x() - horizontalScroll;
			if (mouseY < innerTop - 1 || mouseY > innerTop + item.height()) {
				continue;
			}
			if (mouseX < drawX || mouseX > drawX + item.width()) {
				continue;
			}

			if (item instanceof VariableDisplayItem variableDisplayItem) {
				int localX = mouseX - drawX;
				if (localX <= variableDisplayItem.nameWidth() + VARIABLE_PADDING_X * 2) {
					return new HoverTarget(variableDisplayItem.element().variable().getDescription());
				}

				int runningX = VARIABLE_PADDING_X + variableDisplayItem.nameWidth();
				for (ModifierPart modifierPart : variableDisplayItem.modifiers()) {
					runningX += VARIABLE_GAP;
					if (runningX <= localX && localX <= runningX + modifierPart.width()) {
						return new HoverTarget(modifierPart.tooltip());
					}
					runningX += modifierPart.width();
				}
			}
		}
		return null;
	}

	@Override
	public boolean mouseClicked(Click click, boolean doubled) {
		if (!active) {
			return false;
		}

		if (!isMouseOver(click.x(), click.y())) {
			setFocused(false);
			return false;
		}

		setFocused(true);
		int clickedIndex = getClosestCaretIndex(click.x());
		if ((click.modifiers() & GLFW.GLFW_MOD_SHIFT) != 0) {
			caretIndex = clickedIndex;
		} else {
			caretIndex = clickedIndex;
			selectionAnchor = clickedIndex;
		}
		draggingSelection = true;
		ensureCaretVisible();
		return true;
	}

	@Override
	public boolean mouseDragged(Click click, double offsetX, double offsetY) {
		if (!draggingSelection || !isFocused()) {
			return false;
		}

		caretIndex = getClosestCaretIndex(click.x());
		ensureCaretVisible();
		return true;
	}

	@Override
	public boolean mouseReleased(Click click) {
		draggingSelection = false;
		return false;
	}

	@Override
	public boolean charTyped(CharInput input) {
		if (!isFocused() || !active || !input.isValidChar()) {
			return false;
		}

		write(input.asString());
		return true;
	}

	@Override
	public boolean keyPressed(KeyInput input) {
		if (!isFocused() || !active) {
			return false;
		}

		switch (input.key()) {
			case GLFW.GLFW_KEY_LEFT -> {
				moveCaret(-1, hasShift(input));
				return true;
			}
			case GLFW.GLFW_KEY_RIGHT -> {
				moveCaret(1, hasShift(input));
				return true;
			}
			case GLFW.GLFW_KEY_HOME -> {
				setCaret(0, hasShift(input));
				return true;
			}
			case GLFW.GLFW_KEY_END -> {
				setCaret(model.size(), hasShift(input));
				return true;
			}
			case GLFW.GLFW_KEY_BACKSPACE -> {
				deleteBackward();
				return true;
			}
			case GLFW.GLFW_KEY_DELETE -> {
				deleteForward();
				return true;
			}
			default -> {
				if (hasControl(input) && input.key() == GLFW.GLFW_KEY_A) {
					caretIndex = model.size();
					selectionAnchor = 0;
					ensureCaretVisible();
					return true;
				}
			}
		}

		return false;
	}

	private boolean hasControl(KeyInput input) {
		return (input.modifiers() & GLFW.GLFW_MOD_CONTROL) != 0;
	}

	private boolean hasShift(KeyInput input) {
		return (input.modifiers() & GLFW.GLFW_MOD_SHIFT) != 0;
	}

	private void moveCaret(int offset, boolean extendSelection) {
		setCaret(Math.clamp(caretIndex + offset, 0, model.size()), extendSelection);
	}

	private void setCaret(int index, boolean extendSelection) {
		caretIndex = Math.clamp(index, 0, model.size());
		if (!extendSelection) {
			selectionAnchor = caretIndex;
		}
		ensureCaretVisible();
	}

	private void deleteBackward() {
		if (hasSelection()) {
			deleteSelection();
			return;
		}

		if (caretIndex <= 0) {
			return;
		}

		applyMutation(() -> {
			model.deleteRange(caretIndex - 1, caretIndex);
			caretIndex--;
			selectionAnchor = caretIndex;
		});
	}

	private void deleteForward() {
		if (hasSelection()) {
			deleteSelection();
			return;
		}

		if (caretIndex >= model.size()) {
			return;
		}

		applyMutation(() -> model.deleteRange(caretIndex, caretIndex + 1));
	}

	private void deleteSelection() {
		if (!hasSelection()) {
			return;
		}

		int start = Math.min(caretIndex, selectionAnchor);
		int end = Math.max(caretIndex, selectionAnchor);
		applyMutation(() -> {
			model.deleteRange(start, end);
			caretIndex = start;
			selectionAnchor = start;
		});
	}

	public void write(String text) {
		ModuleContentEditorModel.StyleState insertionStyle = model.getInsertionStyle(Math.min(caretIndex, selectionAnchor));
		ModuleContentEditorModel fragment = ModuleContentEditorModel.parse(text, insertionStyle);
		applyMutation(() -> {
			if (hasSelection()) {
				int start = Math.min(caretIndex, selectionAnchor);
				int end = Math.max(caretIndex, selectionAnchor);
				model.deleteRange(start, end);
				caretIndex = start;
				selectionAnchor = start;
			}

			model.insertModel(caretIndex, fragment);
			caretIndex += fragment.size();
			selectionAnchor = caretIndex;
		});
	}

	private void applyMutation(Runnable mutation) {
		ModuleContentEditorModel before = model.copy();
		int beforeCaret = caretIndex;
		int beforeAnchor = selectionAnchor;
		String beforeRaw = rawText;

		mutation.run();

		String serialized = model.serialize();
		if (serialized.length() > maxLength) {
			model = before;
			caretIndex = beforeCaret;
			selectionAnchor = beforeAnchor;
			rawText = beforeRaw;
			rebuildLayout();
			return;
		}

		rawText = serialized;
		rebuildLayout();
		changedListener.accept(rawText);
	}

	private boolean hasSelection() {
		return caretIndex != selectionAnchor;
	}

	private boolean isIndexSelected(int index) {
		int start = Math.min(caretIndex, selectionAnchor);
		int end = Math.max(caretIndex, selectionAnchor);
		return start <= index && index < end;
	}

	private int getClosestCaretIndex(double mouseX) {
		int relativeX = (int) mouseX - (getX() + TEXT_PADDING_X) + horizontalScroll;
		for (DisplayItem item : displayItems) {
			if (relativeX <= item.x() + item.width() / 2) {
				return item.modelIndex();
			}
			if (relativeX <= item.endX()) {
				return item.modelIndex() + 1;
			}
		}
		return model.size();
	}

	private void ensureCaretVisible() {
		int innerWidth = getWidth() - TEXT_PADDING_X * 2;
		int caretX = caretPositions[Math.clamp(caretIndex, 0, caretPositions.length - 1)];
		if (caretX - horizontalScroll < 0) {
			horizontalScroll = caretX;
		} else if (caretX - horizontalScroll > innerWidth - 1) {
			horizontalScroll = caretX - innerWidth + 1;
		}
		horizontalScroll = Math.max(0, Math.min(horizontalScroll, Math.max(0, contentWidth - innerWidth)));
	}

	private void rebuildLayout() {
		List<ModuleContentEditorModel.InlineElement> elements = model.elements();
		Map<ModuleContentEditorModel.GradientColorLayer, GradientRegion> gradientRegions = new LinkedHashMap<>();
		displayItems = new ArrayList<>(elements.size());
		caretPositions = new int[elements.size() + 1];
		caretPositions[0] = 0;

		int x = 0;
		for (int index = 0; index < elements.size(); index++) {
			ModuleContentEditorModel.InlineElement element = elements.get(index);
			int resolvedColor = resolveColor(element, index, elements, gradientRegions);
			Style style = toMinecraftStyle(element.style(), resolvedColor);

			DisplayItem displayItem;
			if (element instanceof ModuleContentEditorModel.TextElement textElement) {
				int width = CLIENT.textRenderer.getWidth(Text.literal(textElement.text()).setStyle(style));
				displayItem = new TextDisplayItem(index, x, Math.max(width, 1), CLIENT.textRenderer.fontHeight, textElement.text(), resolvedColor);
			} else {
				displayItem = buildVariableDisplayItem(index, x, (ModuleContentEditorModel.VariableElement) element, style, resolvedColor);
			}

			displayItems.add(displayItem);
			x += displayItem.width();
			caretPositions[index + 1] = x;
		}

		contentWidth = x;
		ensureCaretVisible();
	}

	private int resolveColor(ModuleContentEditorModel.InlineElement element,
	                         int index,
	                         List<ModuleContentEditorModel.InlineElement> elements,
	                         Map<ModuleContentEditorModel.GradientColorLayer, GradientRegion> gradientRegions) {
		List<ModuleContentEditorModel.ColorLayer> colorLayers = element.style().colorLayers();
		if (colorLayers.isEmpty()) {
			return TEXT_COLOR;
		}

		ModuleContentEditorModel.ColorLayer topLayer = colorLayers.getLast();
		if (topLayer instanceof ModuleContentEditorModel.StaticColorLayer staticColorLayer) {
			return 0xff000000 | staticColorLayer.rgb();
		}
		if (topLayer == ModuleContentEditorModel.ChromaColorLayer.INSTANCE) {
			return ChromaColorTickable.getColor();
		}

		ModuleContentEditorModel.GradientColorLayer gradientColorLayer = (ModuleContentEditorModel.GradientColorLayer) topLayer;
		GradientRegion region = gradientRegions.computeIfAbsent(gradientColorLayer, layer -> GradientRegion.create(layer, elements, this::measureElementWidth));
		return 0xff000000 | region.colorAt(index, elements);
	}

	private int measureElementWidth(ModuleContentEditorModel.InlineElement element) {
		Style style = toMinecraftStyle(element.style(), TEXT_COLOR);
		if (element instanceof ModuleContentEditorModel.TextElement textElement) {
			return Math.max(1, CLIENT.textRenderer.getWidth(Text.literal(textElement.text()).setStyle(style)));
		}
		return buildVariableDisplayItem(0, 0, (ModuleContentEditorModel.VariableElement) element, style, TEXT_COLOR).width();
	}

	private VariableDisplayItem buildVariableDisplayItem(int modelIndex, int x, ModuleContentEditorModel.VariableElement variableElement, Style style, int color) {
		String name = variableElement.variable().getName().getString();
		int nameWidth = CLIENT.textRenderer.getWidth(Text.literal(name).setStyle(style));

		List<ModifierPart> modifiers = new ArrayList<>();
		int modifiersWidth = 0;
		for (Modifiers.ResolvedModifier<?, ?> modifier : variableElement.modifiers()) {
			String displaySuffix = modifier.modifier().uiMetadata().displayFormatter().apply(modifier.arguments());
			String displayName = modifier.modifier().uiMetadata().getName(modifier.modifier().key()).getString();
			String displayText = displaySuffix.isBlank() ? displayName : displayName + " " + displaySuffix;
			int width = CLIENT.textRenderer.getWidth(Text.literal(displayText).setStyle(style));
			modifiers.add(new ModifierPart(displayText, width, color, modifier.modifier().uiMetadata().getDescription(modifier.modifier().key())));
			modifiersWidth += width + VARIABLE_GAP;
		}
		if (!modifiers.isEmpty()) {
			modifiersWidth -= VARIABLE_GAP;
		}

		int plusWidth = CLIENT.textRenderer.getWidth("+");
		int width = VARIABLE_PADDING_X * 2 + nameWidth + (modifiers.isEmpty() ? 0 : VARIABLE_GAP + modifiersWidth) + VARIABLE_PLUS_GAP + plusWidth;
		int height = CLIENT.textRenderer.fontHeight + VARIABLE_PADDING_Y * 2;
		int plusX = width - VARIABLE_PADDING_X - plusWidth;

		return new VariableDisplayItem(modelIndex, x, width, height, color, variableElement, name, nameWidth, List.copyOf(modifiers), plusX);
	}

	private Style toMinecraftStyle(ModuleContentEditorModel.StyleState style, int color) {
		Style minecraftStyle = Style.EMPTY
				.withBold(style.bold())
				.withItalic(style.italic())
				.withUnderline(style.underline())
				.withStrikethrough(style.strikethrough())
				.withObfuscated(style.obfuscated())
				.withColor(TextColor.fromRgb(color & 0x00ffffff));
		return minecraftStyle;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public boolean hasChanged() {
		return initialContent == null ? !rawText.isEmpty() : !rawText.equals(initialContent);
	}

	@Override
	public boolean isValid() {
		return !rawText.isEmpty();
	}

	@Override
	public void cancel() {
		String restored = initialContent == null ? "" : initialContent;
		rawText = restored;
		model = ModuleContentEditorModel.parse(restored);
		caretIndex = model.size();
		selectionAnchor = caretIndex;
		rebuildLayout();
		changedListener.accept(rawText);
	}

	public String getText() {
		return rawText;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setChangedListener(Consumer<String> changedListener) {
		this.changedListener = changedListener;
	}

	private sealed interface DisplayItem permits TextDisplayItem, VariableDisplayItem {
		int modelIndex();

		int x();

		int width();

		int height();

		default int endX() {
			return x() + width();
		}
	}

	private record TextDisplayItem(int modelIndex, int x, int width, int height, String text,
	                               int color) implements DisplayItem {
	}

	private record VariableDisplayItem(int modelIndex,
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

	private record ModifierPart(String displayText, int width, int color, Text tooltip) {
	}

	private record HoverTarget(Text tooltip) {
	}

	private record GradientRegion(ModuleContentEditorModel.GradientColorLayer gradient,
	                              int startIndex,
	                              int endIndex,
	                              int totalWidth,
	                              int[] elementWidths) {
		private static GradientRegion create(ModuleContentEditorModel.GradientColorLayer gradient,
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

		private int colorAt(int elementIndex, List<ModuleContentEditorModel.InlineElement> elements) {
			double currentX = 0.0;
			for (int index = startIndex; index < endIndex; index++) {
				int width = elementWidths[index - startIndex];
				if (index == elementIndex) {
					float progress = totalWidth <= 0 ? 0.0f : (float) ((currentX + width / 2.0) / totalWidth);
					return interpolateRgb(gradient.startColor(), gradient.endColor(), progress);
				}
				currentX += width;
			}
			return TEXT_COLOR;
		}
	}

	private static int interpolateRgb(int startColor, int endColor, float progress) {
		float clampedProgress = Math.clamp(progress, 0.0f, 1.0f);
		int startRed = (startColor >> 16) & 0xff;
		int startGreen = (startColor >> 8) & 0xff;
		int startBlue = startColor & 0xff;
		int endRed = (endColor >> 16) & 0xff;
		int endGreen = (endColor >> 8) & 0xff;
		int endBlue = endColor & 0xff;

		int red = Math.round(startRed + (endRed - startRed) * clampedProgress);
		int green = Math.round(startGreen + (endGreen - startGreen) * clampedProgress);
		int blue = Math.round(startBlue + (endBlue - startBlue) * clampedProgress);
		return red << 16 | green << 8 | blue;
	}
}
