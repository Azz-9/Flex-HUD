package me.Azz_9.better_hud.screens.modsConfigScreen;

import com.google.common.collect.Lists;
import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsList.ConfigurationScreen;
import me.Azz_9.better_hud.screens.widgets.buttons.*;
import me.Azz_9.better_hud.screens.widgets.colorSelector.ColorEntryWidget;
import me.Azz_9.better_hud.screens.widgets.colorSelector.GradientWidget;
import me.Azz_9.better_hud.screens.widgets.colorSelector.HueBarWidget;
import me.Azz_9.better_hud.screens.widgets.fields.IntFieldWidget;
import me.Azz_9.better_hud.screens.widgets.fields.StringFieldWidget;
import me.Azz_9.better_hud.screens.widgets.sliders.IntSliderWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ModsConfigAbstract extends Screen {
	protected final ModConfig INSTANCE = ModConfig.getInstance();
	private final Screen parent;
	private final double scrollAmount;
	private final List<TrackableChange> trackableWidgets = new ArrayList<>();
	private ButtonWidget saveButton;
	private ButtonWidget cancelButton;

	private boolean isColorSelectorOpen = false;

	private final List<Drawable> drawables = Lists.newArrayList();
	private final List<Drawable> colorSelectorDrawables = Lists.newArrayList();

	private int buttonWidth = 150;
	private int buttonHeight = 20;
	private int centerX;
	private int centerY;
	protected int startY = 50;

	private ScrollableConfigList configList;

	protected ModsConfigAbstract(Text title, Screen parent, double scrollAmount) {
		super(title);
		this.parent = parent;
		this.scrollAmount = scrollAmount;
	}

	public void setButtonWidth(int width) {
		buttonWidth = width;
		configList.setWidth(width);
		centerX = (this.width - configList.getWidth()) / 2;
		configList.setX(centerX);
	}

	public int getButtonWidth() {
		return buttonWidth;
	}

	public void setButtonHeight(int height) {
		buttonHeight = height;
		centerY = (this.height - buttonHeight) / 2;
	}

	public int getButtonHeight() {
		return buttonHeight;
	}

	public int getCenterX() {
		return centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	@Override
	protected void init() {
		this.centerX = (this.width - buttonWidth) / 2;
		this.centerY = (this.height - buttonHeight) / 2;

		int configListY = Math.max(MinecraftClient.getInstance().getWindow().getScaledHeight() / 10, 20);

		cancelButton = ButtonWidget.builder(Text.translatable("better_hud.global.config.cancel"), (btn) -> this.cancel())
				.dimensions(this.width / 2 - 165, this.height - 30, 160, 20)
				.build();
		saveButton = ButtonWidget.builder(Text.translatable("better_hud.global.config.save_and_quit"), (btn) -> this.saveAndClose())
				.dimensions(this.width / 2 + 5, this.height - 30, 160, 20)
				.build();
		saveButton.active = false;

		configList = new ScrollableConfigList(MinecraftClient.getInstance(),
				getButtonWidth() + 62, Math.min(300, MinecraftClient.getInstance().getWindow().getScaledHeight() - configListY - 50),
				configListY, (this.width - (getButtonWidth() + 62)) / 2,
				30, buttonWidth + 30);

		this.addDrawableChild(cancelButton);
		this.addDrawableChild(saveButton);
		this.addSelectableChild(configList);
	}

	protected CustomToggleButtonWidget addToggleButton(Text text, boolean currentValue, boolean defaultValue, Consumer<Boolean> consumer) {
		CustomToggleButtonWidget toggleButtonWidget = new CustomToggleButtonWidget(buttonWidth, buttonHeight, currentValue, consumer);

		ResetButtonWidget resetButtonWidget = new ResetButtonWidget(buttonHeight, buttonHeight, (btn) -> {
			toggleButtonWidget.setToggled(defaultValue);
			toggleButtonWidget.getON_TOGGLE().accept(defaultValue);
		});

		TextWidget textWidget = new TextWidget(text, textRenderer);

		this.configList.addButton(new ScrollableConfigList.ButtonEntry(toggleButtonWidget, resetButtonWidget, textWidget));

		registerTrackableWidget(toggleButtonWidget);

		return toggleButtonWidget;
	}

	protected void addDependentToggleButton(Text text, boolean currentValue, boolean defaultValue, Consumer<Boolean> consumer, CustomToggleButtonWidget dependencyToggleButton, boolean disableIf) {
		DependentCustomToggleButtonWidget toggleButtonWidget = new DependentCustomToggleButtonWidget(buttonWidth, buttonHeight, currentValue, consumer, dependencyToggleButton, disableIf);

		DependentResetButtonWidget resetButtonWidget = new DependentResetButtonWidget(buttonHeight, buttonHeight, (btn) -> {
			toggleButtonWidget.setToggled(defaultValue);
			toggleButtonWidget.getON_TOGGLE().accept(defaultValue);
		}, dependencyToggleButton, disableIf);

		DependentTextWidget textWidget = new DependentTextWidget(text, textRenderer, dependencyToggleButton, disableIf);

		this.configList.addButton(new ScrollableConfigList.ButtonEntry(toggleButtonWidget, resetButtonWidget, textWidget));

		registerTrackableWidget(toggleButtonWidget);
	}

	protected void addColorButton(Text text, int currentColor, int defaultColor, Consumer<Integer> consumer) {
		ColorButtonWidget colorButtonWidget = new ColorButtonWidget(buttonWidth, buttonHeight, currentColor, (btn) -> {
			isColorSelectorOpen = !isColorSelectorOpen;
			configList.setActiveToEveryEntry(!isColorSelectorOpen);
		}, width, height, consumer);

		ResetButtonWidget resetButtonWidget = new ResetButtonWidget(20, 20, (btn) -> {
			colorButtonWidget.getGRADIENT_WIDGET().setColor(defaultColor);
			colorButtonWidget.getHUE_BAR_WIDGET().setHue(defaultColor);
		});

		TextWidget textWidget = new TextWidget(text, textRenderer);

		this.addDrawableChild(colorButtonWidget);

		this.addDrawableColorSelector(colorButtonWidget.getGRADIENT_WIDGET());
		super.addDrawableChild(colorButtonWidget.getGRADIENT_WIDGET());
		this.addDrawableColorSelector(colorButtonWidget.getHUE_BAR_WIDGET());
		super.addDrawableChild(colorButtonWidget.getHUE_BAR_WIDGET());
		this.addDrawableColorSelector(colorButtonWidget.getCOLOR_ENTRY_WIDGET());
		super.addDrawableChild(colorButtonWidget.getCOLOR_ENTRY_WIDGET());

		this.addDrawableChild(resetButtonWidget);

		this.addDrawableChild(textWidget);

		this.configList.addButton(new ScrollableConfigList.ButtonEntry(colorButtonWidget, resetButtonWidget, textWidget));

		registerTrackableWidget(colorButtonWidget);
	}

	protected void addIntField(Text text, int currentValue, int defaultValue, Integer min, Integer max, Consumer<Integer> consumer) {
		IntFieldWidget intFieldWidget = new IntFieldWidget(textRenderer, 20, 20, min, max, currentValue, value -> consumer.accept(Integer.valueOf(value)));
		String tooltipText = "";
		if (min != null) {
			tooltipText += Text.translatable("better_hud.global.config.min").getString() + ": " + min;
			if (max != null) {
				tooltipText += "\n";
			}
		}
		if (max != null) {
			tooltipText += Text.translatable("better_hud.global.config.max").getString() + ": " + max;
		}
		intFieldWidget.setTooltip(Tooltip.of(Text.literal(tooltipText)));

		ResetButtonWidget resetButtonWidget = new ResetButtonWidget(20, 20, (btn) -> {
			intFieldWidget.setText(String.valueOf(defaultValue));
		});

		TextWidget textWidget = new TextWidget(text, textRenderer);

		this.configList.addButton(new ScrollableConfigList.ButtonEntry(intFieldWidget, resetButtonWidget, textWidget));

		registerTrackableWidget(intFieldWidget);
	}

	protected void addIntField(int x, int y, int buttonWidth, Text text, int currentValue, int defaultValue, Consumer<Integer> consumer) {
		addIntField(text, currentValue, defaultValue, null, null, consumer);
	}


	protected void addTextField(int x, int y, int buttonWidth, int buttonHeight, int fieldWidth, Text text, String currentValue, String defaultValue, Consumer<String> consumer, Predicate<String> isValid) {
		StringFieldWidget textFieldWidget = new StringFieldWidget(textRenderer, fieldWidth, buttonHeight, currentValue, consumer, isValid);
		textFieldWidget.setTooltip(Tooltip.of(Text.literal("hh: ").append(Text.translatable("better_hud.global.hours").append("\nmm: ").append(Text.translatable("better_hud.global.minutes").append("\nss: ").append(Text.translatable("better_hud.global.seconds"))))));

		ResetButtonWidget resetButtonWidget = new ResetButtonWidget(20, 20, (btn) -> {
			textFieldWidget.setText(defaultValue);
		});

		TextWidget textWidget = new TextWidget(text, textRenderer);

		this.configList.addButton(new ScrollableConfigList.ButtonEntry(textFieldWidget, resetButtonWidget, textWidget));

		registerTrackableWidget(textFieldWidget);
	}

	protected void addTextField(int x, int y, int buttonWidth, int buttonHeight, int fieldWidth, Text text, String currentValue, String defaultValue, Consumer<String> consumer) {
		addTextField(x, y, buttonWidth, buttonHeight, fieldWidth, text, currentValue, defaultValue, consumer, null);
	}

	protected <E extends Enum<E>> void addCyclingStringButton(int x, int y, int buttonWidth, Text text, Class<E> enumClass, E currentValue, E defaultValue, Consumer<E> consumer, Function<E, Optional<Text[]>> tooltipSupplier) {
		E[] enumValues = enumClass.getEnumConstants();

		final int[] index = {0};
		for (int i = 0; i < enumValues.length; i++) {
			if (enumValues[i] == currentValue) {
				index[0] = i;
				break;
			}
		}

		final int[] defaultValueIndex = {0};
		for (int i = 0; i < enumValues.length; i++) {
			if (enumValues[i] == defaultValue) {
				defaultValueIndex[0] = i;
				break;
			}
		}

		CyclingButtonWidget cyclingButtonWidget = new CyclingButtonWidget(70, 20, currentValue, Text.of(enumValues[index[0]].name()), btn -> {
			// Passer à la prochaine valeur de l'enum
			index[0] = (index[0] + 1) % enumValues.length;

			// Mettre à jour le texte du bouton
			btn.setMessage(Text.of(enumValues[index[0]].name()));

			// Appeler le consumer avec la nouvelle valeur
			consumer.accept(enumValues[index[0]]);

			if (tooltipSupplier != null) {
				Optional<Text[]> initialTooltipText = tooltipSupplier.apply(enumValues[index[0]]);
				btn.setTooltip(initialTooltipText.map(texts -> Tooltip.of(texts[0])).orElse(null));
			}
		}, enumClass, consumer);

		if (tooltipSupplier != null) {
			Optional<Text[]> initialTooltipText = tooltipSupplier.apply(currentValue);
			cyclingButtonWidget.setTooltip(initialTooltipText.map(texts -> Tooltip.of(texts[0])).orElse(null));
		}

		ResetButtonWidget resetButtonWidget = new ResetButtonWidget(20, 20, (btn) -> {
			index[0] = defaultValueIndex[0];
			cyclingButtonWidget.setMessage(Text.of(enumValues[index[0]].name()));
			consumer.accept(enumValues[index[0]]);
		});

		TextWidget textWidget = new TextWidget(text, textRenderer);

		this.configList.addButton(new ScrollableConfigList.ButtonEntry(cyclingButtonWidget, resetButtonWidget, textWidget));

		registerTrackableWidget(cyclingButtonWidget);
	}

	protected <E extends Enum<E>> void addCyclingStringButton(int x, int y, int buttonWidth, Text text, Class<E> enumClass, E currentValue, E defaultValue, Consumer<E> consumer) {
		addCyclingStringButton(x, y, buttonWidth, text, enumClass, currentValue, defaultValue, consumer, null);
	}

	protected void addIntSlider(int x, int y, int buttonWidth, int buttonHeight, int sliderWidth, Text text, int currentValue, int defaultValue, int min, int max, Consumer<Integer> consumer, Integer step) {

		IntSliderWidget sliderWidget = new IntSliderWidget(sliderWidth, buttonHeight, (double) currentValue / max, step, min, max, consumer);

		ResetButtonWidget resetButtonWidget = new ResetButtonWidget(20, 20, (btn) -> {
			sliderWidget.setValue(defaultValue);
		});

		TextWidget textWidget = new TextWidget(text, textRenderer);

		this.configList.addButton(new ScrollableConfigList.ButtonEntry(sliderWidget, resetButtonWidget, textWidget));

		registerTrackableWidget(sliderWidget);
	}

	protected void addIntSlider(int x, int y, int buttonWidth, int buttonHeight, int sliderWidth, Text text, int currentValue, int defaultValue, int min, int max, Consumer<Integer> consumer) {
		addIntSlider(x, y, buttonWidth, buttonHeight, sliderWidth, text, currentValue, defaultValue, min, max, consumer, null);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);

		// render title
		context.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 7, 0xffffff);

		// render list
		this.configList.render(context, mouseX, mouseY, delta);

		checkForChanges();
		checkValid();

		saveButton.render(context, mouseX, mouseY, delta);
		cancelButton.render(context, mouseX, mouseY, delta);


		// Render the color selector over others widgets
		for (Drawable drawable : this.colorSelectorDrawables) {
			if (drawable instanceof GradientWidget gradient) {
				ColorButtonWidget button = gradient.getCOLOR_BUTTON_WIDGET();
				gradient.active = button.isSelectingColor;
				if (!gradient.active) {
					continue; // if player is not selecting a color don't render the gradient
				} else {
					button.active = true;
					// Display the background of the color selector
					context.fill(button.getColorSelectorX(), button.getColorSelectorY(),
							button.getColorSelectorX() + button.getColorSelectorWidth(),
							button.getColorSelectorY() + button.getColorSelectorHeight(),
							0xff1e1f22);
				}
			} else if (drawable instanceof HueBarWidget hueBar) {
				hueBar.active = hueBar.getCOLOR_BUTTON_WIDGET().isSelectingColor;
				if (!hueBar.active) continue; // if player is not selecting a color don't render the hue bar
			} else if (drawable instanceof ColorEntryWidget entry) {
				entry.active = entry.getCOLOR_BUTTON_WIDGET().isSelectingColor;
				if (!entry.active) continue; // if player is not selecting a color don't render the color entry
			}

			drawable.render(context, mouseX, mouseY, delta);
		}
	}

	@Override
	public <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
		this.drawables.add(drawableElement);
		return super.addDrawableChild(drawableElement);
	}

	public <T extends Element & Drawable & Selectable> void addDrawableColorSelector(T drawableElement) {
		this.colorSelectorDrawables.add(drawableElement);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!cancelButton.isHovered() && !saveButton.isHovered()) {
			for (Drawable drawable : this.drawables) {
				if (drawable instanceof ColorButtonWidget colorButtonWidget && colorButtonWidget.isSelectingColor &&
						!colorButtonWidget.isHovered() && !colorButtonWidget.isMouseHoverColorSelector(mouseX, mouseY)) {

					colorButtonWidget.onClick(mouseX, mouseY);
					return true;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
			this.cancel();
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void close() {
		client.setScreen(parent);
		if (parent instanceof ConfigurationScreen configScreen) {
			configScreen.getFeatureList().setScrollY(scrollAmount);
		}
	}

	private void saveAndClose() {
		ModConfig.saveConfig();
		close();
	}

	public void cancel() {
		trackableWidgets.forEach(TrackableChange::cancel);
		close();
	}

	protected void registerTrackableWidget(TrackableChange widget) {
		trackableWidgets.add(widget);
	}

	private void checkForChanges() {
		saveButton.active = trackableWidgets.stream().anyMatch(TrackableChange::hasChanged); // Met à jour l'état du bouton de sauvegarde
	}

	private void checkValid() {
		if (saveButton.active) {
			saveButton.active = trackableWidgets.stream().allMatch(TrackableChange::isValid);
		}
	}

	@Override
	protected void clearChildren() {
		super.clearChildren();
		drawables.clear();
	}
}