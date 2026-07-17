package me.Azz_9.flex_hud.client.gui.components.customModule.moduleContentField;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.Azz_9.flex_hud.client.gui.Cursors;
import me.Azz_9.flex_hud.client.modules.customModules.modifiers.Modifier;

final class ModifierPickerPopup {
	private static final int BUTTON_PADDING = 2;
	private static final int ROW_GAP = 1;
	private static final int MAX_HEIGHT = 180;
	private static final int SCROLLBAR_WIDTH = 4;
	private static final int SCROLLBAR_MARGIN = 3;
	private static final int SEARCH_FIELD_HEIGHT = 18;
	private static final int SEARCH_FIELD_MIN_WIDTH = 120;
	private static final double SCROLL_SNAP_DISTANCE = 0.5;

	private final ModuleContentField host;
	private final int elementIndex;
	private final PopupTextFieldWidget searchField = new PopupTextFieldWidget(SEARCH_FIELD_MIN_WIDTH, SEARCH_FIELD_HEIGHT);
	private Bounds bounds = new Bounds(0, 0, 0, 0);
	private List<ModifierPickerEntry> entries = List.of();
	private int contentHeight = 0;
	private int viewportHeight = 0;
	private boolean isDraggingScrollbar = false;
	private double dragStartMouseY = 0;
	private double dragStartScrollOffset = 0;

	private double targetScroll = 0;
	private double currentScroll = 0;
	private final double scrollSpeed = 25.0;
	private long lastUpdateTime = System.nanoTime();

	ModifierPickerPopup(ModuleContentField host, int elementIndex) {
		this.host = host;
		this.elementIndex = elementIndex;
		searchField.setPlaceholder(Component.translatable("flex_hud.create_module_screen.searchbar_placeholder"));
		searchField.setMaxLength(64);
		searchField.setResponder(text -> setScrollPosition(0.0));
		searchField.setFocused(true);
	}

	int elementIndex() {
		return elementIndex;
	}

	private boolean needsScrollbar() {
		return contentHeight > viewportHeight;
	}

	private int maxScroll() {
		return Math.max(0, contentHeight - viewportHeight);
	}

	private void setScrollPosition(double scrollPosition) {
		double clamped = Mth.clamp(scrollPosition, 0.0, maxScroll());
		currentScroll = clamped;
		targetScroll = clamped;
	}

	void layout(VariableDisplayItem variableItem) {
		List<Modifier<?, ?>> compatibleModifiers = host.getCompatibleModifiers(variableItem.element());
		List<Modifier<?, ?>> filteredModifiers = filterModifiers(compatibleModifiers);

		int titleHeight = MINECRAFT.font.lineHeight;
		int rowHeight = MINECRAFT.font.lineHeight + 4;

		int contentWidth = SEARCH_FIELD_MIN_WIDTH;
		int width = MINECRAFT.font.width(Component.translatable("flex_hud.create_module_screen.editor.add_modifier")) + ModuleContentField.POPUP_PADDING * 2;
		for (Modifier<?, ?> modifier : compatibleModifiers) {
			contentWidth = Math.max(contentWidth, MINECRAFT.font.width(modifier.uiMetadata().getName(modifier.key())) + BUTTON_PADDING * 2);
		}
		width = Math.max(width, contentWidth + ModuleContentField.POPUP_PADDING * 2);

		contentHeight = filteredModifiers.isEmpty()
				? rowHeight
				: filteredModifiers.size() * (rowHeight + ROW_GAP) - ROW_GAP;

		int headerHeight = ModuleContentField.POPUP_PADDING + titleHeight + ModuleContentField.POPUP_GAP + SEARCH_FIELD_HEIGHT + ModuleContentField.POPUP_GAP;
		int footerHeight = ModuleContentField.POPUP_PADDING;
		int maxViewportHeight = MAX_HEIGHT - headerHeight - footerHeight;
		viewportHeight = Math.min(contentHeight, maxViewportHeight);

		int totalHeight = headerHeight + viewportHeight + footerHeight;
		if (needsScrollbar()) {
			width += SCROLLBAR_WIDTH + SCROLLBAR_MARGIN;
		}

		int preferredX = host.getX() + ModuleContentField.TEXT_PADDING_X + variableItem.x() - host.horizontalScroll;
		int preferredY = host.getBottom() + ModuleContentField.OVERLAY_GAP;
		Screen screen = MINECRAFT.screen;
		if (screen != null && preferredY + totalHeight > screen.height - 4) {
			preferredY = host.getY() - totalHeight - ModuleContentField.OVERLAY_GAP;
		}

		bounds = new Bounds(host.clampX(preferredX, width), host.clampY(preferredY, totalHeight), width, totalHeight);
		currentScroll = Mth.clamp(currentScroll, 0.0, maxScroll());
		targetScroll = Mth.clamp(targetScroll, 0.0, maxScroll());
		searchField.setPosition(bounds.x() + ModuleContentField.POPUP_PADDING, bounds.y() + ModuleContentField.POPUP_PADDING + titleHeight + ModuleContentField.POPUP_GAP);
		searchField.setWidth(bounds.width() - ModuleContentField.POPUP_PADDING * 2 - (needsScrollbar() ? SCROLLBAR_WIDTH + SCROLLBAR_MARGIN : 0));

		List<ModifierPickerEntry> builtEntries = new ArrayList<>();
		int entryY = 0;
		int entryWidth = bounds.width() - ModuleContentField.POPUP_PADDING * 2 - (needsScrollbar() ? SCROLLBAR_WIDTH + SCROLLBAR_MARGIN : 0);
		for (Modifier<?, ?> modifier : filteredModifiers) {
			builtEntries.add(new ModifierPickerEntry(
					modifier,
					new Bounds(bounds.x() + ModuleContentField.POPUP_PADDING, entryY, entryWidth, rowHeight),
					modifier.uiMetadata().getDescription(modifier.key())
			));
			entryY += rowHeight + ROW_GAP;
		}
		entries = List.copyOf(builtEntries);
	}

	private int entryRenderY(ModifierPickerEntry entry) {
		return entry.bounds().y() + viewportOriginY() - (int) Math.round(currentScroll);
	}

	private int viewportOriginY() {
		return bounds.y() + ModuleContentField.POPUP_PADDING + MINECRAFT.font.lineHeight + ModuleContentField.POPUP_GAP + SEARCH_FIELD_HEIGHT + ModuleContentField.POPUP_GAP;
	}

	void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		updateScrollAnimation();

		host.renderPanel(graphics, bounds);
		graphics.text(
				MINECRAFT.font,
				Component.translatable("flex_hud.create_module_screen.editor.add_modifier"),
				bounds.x() + ModuleContentField.POPUP_PADDING,
				bounds.y() + ModuleContentField.POPUP_PADDING,
				ModuleContentField.TEXT_COLOR,
				false
		);
		searchField.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		if (entries.isEmpty()) {
			graphics.text(
					MINECRAFT.font,
					Component.translatable("flex_hud.create_module_screen.editor.no_modifier_available"),
					bounds.x() + ModuleContentField.POPUP_PADDING,
					viewportOriginY(),
					ModuleContentField.PLACEHOLDER_COLOR,
					false
			);
			return;
		}

		int vpOriginY = viewportOriginY();
		graphics.enableScissor(
				bounds.x(),
				vpOriginY,
				bounds.x() + bounds.width(),
				vpOriginY + viewportHeight
		);

		for (ModifierPickerEntry entry : entries) {
			int screenY = entryRenderY(entry);
			if (screenY + entry.bounds().height() < vpOriginY) continue;
			if (screenY > vpOriginY + viewportHeight) break;

			Bounds renderBounds = new Bounds(entry.bounds().x(), screenY, entry.bounds().width(), entry.bounds().height());
			int background = renderBounds.contains(mouseX, mouseY) ? ModuleContentField.BUTTON_HOVERED_BACKGROUND : ModuleContentField.BUTTON_BACKGROUND;
			host.renderButton(graphics, renderBounds, entry.modifier().uiMetadata().getName(entry.modifier().key()), background, ModuleContentField.BUTTON_TEXT_COLOR, BUTTON_PADDING, mouseX, mouseY);
		}

		graphics.disableScissor();

		if (needsScrollbar()) {
			renderScrollbar(graphics, vpOriginY, mouseX, mouseY);
		}
	}

	private void renderScrollbar(GuiGraphicsExtractor graphics, int vpOriginY, double mouseX, double mouseY) {
		int trackX = bounds.x() + bounds.width() - ModuleContentField.POPUP_PADDING - SCROLLBAR_WIDTH;
		int trackHeight = viewportHeight;
		float ratio = (float) viewportHeight / contentHeight;
		int thumbHeight = Math.max(12, (int) (trackHeight * ratio));
		int maxThumbOffset = trackHeight - thumbHeight;
		int thumbOffset = (maxScroll() > 0)
				? (int) Math.round(currentScroll / maxScroll() * maxThumbOffset)
				: 0;

		boolean active = isDraggingScrollbar || isOverScrollbarThumb(mouseX, mouseY);
		int thumbColor = active ? ModuleContentField.SCROLLBAR_THUMB_ACTIVE_COLOR : ModuleContentField.SCROLLBAR_THUMB_COLOR;

		graphics.fill(
				trackX, vpOriginY + thumbOffset,
				trackX + SCROLLBAR_WIDTH, vpOriginY + thumbOffset + thumbHeight,
				thumbColor
		);

		if (isOverScrollbarThumb(mouseX, mouseY)) {
			graphics.requestCursor(isDraggingScrollbar ? Cursors.RESIZE_NS : Cursors.POINTING_HAND);
		}
	}

	private boolean isOverScrollbarThumb(double mouseX, double mouseY) {
		if (!needsScrollbar()) return false;
		int trackX = bounds.x() + bounds.width() - ModuleContentField.POPUP_PADDING - SCROLLBAR_WIDTH;
		int vpOriginY = viewportOriginY();
		int trackHeight = viewportHeight;
		float ratio = (float) viewportHeight / contentHeight;
		int thumbHeight = Math.max(12, (int) (trackHeight * ratio));
		int maxThumbOffset = trackHeight - thumbHeight;
		int thumbOffset = (maxScroll() > 0)
				? (int) Math.round(currentScroll / maxScroll() * maxThumbOffset)
				: 0;
		int thumbY = vpOriginY + thumbOffset;
		return mouseX >= trackX && mouseX <= trackX + SCROLLBAR_WIDTH
				&& mouseY >= thumbY && mouseY <= thumbY + thumbHeight;
	}

	boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (!bounds.contains(mouseX, mouseY)) return false;
		targetScroll = Mth.clamp(targetScroll - amount * scrollSpeed, 0.0, maxScroll());
		return true;
	}

	boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
		if (searchField.mouseClicked(event, doubled)) {
			return true;
		}
		if (isOverScrollbarThumb(event.x(), event.y())) {
			isDraggingScrollbar = true;
			dragStartMouseY = event.y();
			dragStartScrollOffset = currentScroll;
			targetScroll = currentScroll;
			return true;
		}

		int vpOriginY = viewportOriginY();
		for (ModifierPickerEntry entry : entries) {
			int screenY = entryRenderY(entry);
			Bounds renderBounds = new Bounds(entry.bounds().x(), screenY, entry.bounds().width(), entry.bounds().height());
			if (!renderBounds.contains(event.x(), event.y())) continue;
			if (event.y() < vpOriginY || event.y() > vpOriginY + viewportHeight) continue;

			if (entry.modifier().uiMetadata().editorKind() == Modifier.EditorKind.NONE) {
				host.applyModifierChange(elementIndex, null, host.defaultResolvedModifier(entry.modifier()), false);
			} else {
				host.modifierEditorPopup = new ModifierEditorPopup(host, elementIndex, null, entry.modifier(), host.defaultArguments(entry.modifier()));
				VariableDisplayItem variableDisplayItem = host.findVariableDisplayItem(elementIndex);
				if (variableDisplayItem != null) {
					host.modifierEditorPopup.layout(variableDisplayItem);
				}
				host.modifierPickerPopup = null;
			}
			return true;
		}
		return bounds.contains(event.x(), event.y());
	}

	boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		if (searchField.mouseDragged(event, deltaX, deltaY)) {
			return true;
		}
		if (!isDraggingScrollbar) return false;
		int trackHeight = viewportHeight;
		float ratio = (float) viewportHeight / contentHeight;
		int thumbHeight = Math.max(12, (int) (trackHeight * ratio));
		int maxThumbOffset = trackHeight - thumbHeight;
		if (maxThumbOffset <= 0) {
			return false;
		}
		float scrollPerPixel = (float) maxScroll() / maxThumbOffset;
		int delta = (int) ((event.y() - dragStartMouseY) * scrollPerPixel);
		setScrollPosition(dragStartScrollOffset + delta);
		return true;
	}

	boolean mouseReleased(MouseButtonEvent event) {
		if (searchField.mouseReleased(event)) {
			return true;
		}
		if (isDraggingScrollbar) {
			isDraggingScrollbar = false;
			return true;
		}
		return false;
	}

	boolean keyPressed(KeyEvent event) {
		return searchField.isFocused() && searchField.keyPressed(event);
	}

	boolean charTyped(CharacterEvent event) {
		return searchField.isFocused() && searchField.charTyped(event);
	}

	@Nullable HoverTarget findHoverTarget(int mouseX, int mouseY) {
		int vpOriginY = viewportOriginY();
		for (ModifierPickerEntry entry : entries) {
			int screenY = entryRenderY(entry);
			if (screenY < vpOriginY || screenY > vpOriginY + viewportHeight) continue;
			Bounds renderBounds = new Bounds(entry.bounds().x(), screenY, entry.bounds().width(), entry.bounds().height());
			if (renderBounds.contains(mouseX, mouseY)) {
				return new HoverTarget(entry.tooltip());
			}
		}
		return null;
	}

	boolean contains(double mouseX, double mouseY) {
		return bounds.contains(mouseX, mouseY);
	}

	private void updateScrollAnimation() {
		long currentTime = System.nanoTime();
		double deltaSeconds = (currentTime - lastUpdateTime) / 1_000_000_000.0;
		lastUpdateTime = currentTime;

		double alpha = 1.0 - Math.exp(-scrollSpeed * deltaSeconds);
		currentScroll += (targetScroll - currentScroll) * alpha;
		if (Math.abs(targetScroll - currentScroll) < SCROLL_SNAP_DISTANCE) {
			currentScroll = targetScroll;
		}
		currentScroll = Mth.clamp(currentScroll, 0.0, maxScroll());
	}

	private List<Modifier<?, ?>> filterModifiers(List<Modifier<?, ?>> modifiers) {
		String query = searchField.getValue().strip().toLowerCase(Locale.ROOT);
		if (query.isEmpty()) {
			return modifiers;
		}

		List<Modifier<?, ?>> filtered = new ArrayList<>();
		for (Modifier<?, ?> modifier : modifiers) {
			String name = modifier.uiMetadata().getName(modifier.key()).getString().toLowerCase(Locale.ROOT);
			String key = modifier.key().toLowerCase(Locale.ROOT);
			if (name.contains(query) || key.contains(query)) {
				filtered.add(modifier);
			}
		}
		return filtered;
	}
}
