package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.tickables.ChromaColorTickable;
import me.Azz_9.flex_hud.client.utils.cps.CpsUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.HashMap;
import java.util.Map;

import static me.Azz_9.flex_hud.client.utils.DrawingUtils.drawBorder;

public class KeyStrokes extends AbstractTextElement {

	private final ConfigBoolean chromaColorPressed = new ConfigBoolean(false, "flex_hud.key_strokes.config.chroma_color_pressed");
	private final ConfigInteger colorPressed = new ConfigInteger(0x323232, "flex_hud.key_strokes.config.color_pressed");
	private final ConfigBoolean drawBackgroundPressed = new ConfigBoolean(true, "flex_hud.key_strokes.config.show_background_pressed");
	private final ConfigInteger backgroundColorPressed = new ConfigInteger(0xffffff, "flex_hud.key_strokes.config.background_color_pressed");
	private final ConfigBoolean showBorder = new ConfigBoolean(false, "flex_hud.key_strokes.config.show_border");
	private final ConfigInteger borderColor = new ConfigInteger(0xffffff, "flex_hud.key_strokes.config.border_color");
	public final ConfigBoolean displayCps = new ConfigBoolean(true, "flex_hud.key_strokes.config.display_cps");
	private final ConfigBoolean useArrow = new ConfigBoolean(false, "flex_hud.key_strokes.config.use_arrow");

	private final int borderThickness = 1;
	private final int gap = borderThickness;
	private final int keySize = 22;
	private final Map<KeyBinding, KeyAnimation> keyAnimations = new HashMap<>();

	private static class KeyAnimation {
		long lastChangeTime;
		boolean wasPressed;
	}

	public KeyStrokes(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.key_strokes.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
		this.drawBackground.setDefaultValue(true);
		this.drawBackground.setValue(true);

		ConfigRegistry.register(getID(), "chromaColorPressed", chromaColorPressed);
		ConfigRegistry.register(getID(), "colorPressed", colorPressed);
		ConfigRegistry.register(getID(), "drawBackgroundPressed", drawBackgroundPressed);
		ConfigRegistry.register(getID(), "backgroundColorPressed", backgroundColorPressed);
		ConfigRegistry.register(getID(), "showBorder", showBorder);
		ConfigRegistry.register(getID(), "borderColor", borderColor);
		ConfigRegistry.register(getID(), "displayCps", displayCps);
		ConfigRegistry.register(getID(), "useArrow", useArrow);
	}

	@Override
	public void init() {
		this.width = keySize * 3 + gap * 4;
		this.height = (int) (keySize * 3.5 + gap * 5);
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.key_strokes");
	}

	@Override
	public String getID() {
		return "key_strokes";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender()) {
			return;
		}

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		// forward key
		Text forwardText = useArrow.getValue() ? Text.of("▲") : client.options.forwardKey.getBoundKeyLocalizedText();
		renderMovementKey(context, keySize + gap * 2, gap, keySize, keySize, client.options.forwardKey, forwardText);

		// back key
		Text backText = useArrow.getValue() ? Text.of("▼") : client.options.backKey.getBoundKeyLocalizedText();
		renderMovementKey(context, keySize + gap * 2, keySize + gap * 2, keySize, keySize, client.options.backKey, backText);

		// right key
		Text rightText = useArrow.getValue() ? Text.of("▶") : client.options.rightKey.getBoundKeyLocalizedText();
		renderMovementKey(context, keySize * 2 + gap * 3, keySize + gap * 2, keySize, keySize, client.options.rightKey, rightText);

		// left key
		Text leftText = useArrow.getValue() ? Text.of("◀") : client.options.leftKey.getBoundKeyLocalizedText();
		renderMovementKey(context, gap, keySize + gap * 2, keySize, keySize, client.options.leftKey, leftText);

		// jump key
		renderJumpKey(context, gap, keySize * 2 + gap * 3, keySize * 3 + gap * 2, keySize / 2, client.options.jumpKey);

		if (displayCps.getValue()) {
			// attack key
			renderMouseKey(context, gap, (int) (keySize * 2.5) + gap * 4, (int) (keySize * 1.5) + gap / 2, keySize, client.options.attackKey, CpsUtils.getLeftCps(), Text.of("LMB"));

			// use key
			renderMouseKey(context, (int) (keySize * 1.5 + gap * 2.5) + 1, (int) (keySize * 2.5) + gap * 4, (int) (keySize * 1.5) + gap / 2, keySize, client.options.useKey, CpsUtils.getRightCps(), Text.of("RMB"));
		}

		matrices.popMatrix();
	}

	private float renderKey(DrawContext context, int x, int y, int keyWidth, int keyHeight, KeyBinding keyBinding) {
		boolean isPressed = keyBinding.isPressed();
		long now = System.currentTimeMillis();

		long fadeInDuration = 100;
		long fadeOutDuration = 400;

		KeyAnimation anim = keyAnimations.computeIfAbsent(keyBinding, k -> {
			KeyAnimation a = new KeyAnimation();
			a.lastChangeTime = -1;
			a.wasPressed = isPressed;
			return a;
		});

		if (anim.wasPressed != isPressed) {
			anim.lastChangeTime = now;
			anim.wasPressed = isPressed;
		}

		long elapsed = now - anim.lastChangeTime;

		float fadeFactor;
		if (isPressed) {
			fadeFactor = Math.min(1.0f, elapsed / (float) fadeInDuration);
		} else {
			fadeFactor = 1.0f - Math.min(1.0f, elapsed / (float) fadeOutDuration);
		}

		if (drawBackground.getValue()) {
			context.fill(x, y, x + keyWidth, y + keyHeight, getBackgroundColor());
		}
		if (drawBackgroundPressed.getValue()) {
			context.fill(x, y, x + keyWidth, y + keyHeight, ColorHelper.withAlpha(fadeFactor / 2, backgroundColorPressed.getValue()));
		}

		if (showBorder.getValue()) {
			drawBorder(context, x - borderThickness, y - borderThickness, keyWidth + borderThickness * 2, keyHeight + borderThickness * 2, borderThickness, getBorderColor());
		}

		return fadeFactor;
	}

	private void renderMovementKey(DrawContext context, int x, int y, int keyWidth, int keyHeight, KeyBinding keyBinding, Text label) {
		float fadeFactor = renderKey(context, x, y, keyWidth, keyHeight, keyBinding);

		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(x + (keyWidth - textRenderer.getWidth(label)) / 2.0f, y + (keyHeight - textRenderer.fontHeight) / 2.0f);

		context.drawText(textRenderer, label, 0, 0, getColor(fadeFactor), shadow.getValue());

		matrices.popMatrix();
	}

	private void renderJumpKey(DrawContext context, int x, int y, int keyWidth, int keyHeight, KeyBinding keyBinding) {
		float fadeFactor = renderKey(context, x, y, keyWidth, keyHeight, keyBinding);

		int barX1 = x + keyWidth / 4;
		int barY1 = y + keyHeight / 2 - 2;
		int barX2 = (int) (x + keyWidth * 0.75);
		int barY2 = y + keyHeight / 2;

		int color = getColor(fadeFactor);

		context.fill(barX1 + 1, barY1 + 1, barX2 + 1, barY2 + 1, getTextShadowColor(color));

		context.fill(barX1, barY1, barX2, barY2, color);
	}

	private void renderMouseKey(DrawContext context, int x, int y, int keyWidth, int keyHeight, KeyBinding keyBinding, int cps, Text label) {
		float fadeFactor = renderKey(context, x, y, keyWidth, keyHeight, keyBinding);

		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		int color = getColor(fadeFactor);

		Matrix3x2fStack matrices = context.getMatrices();

		matrices.pushMatrix();
		matrices.translate(x + (keyWidth - textRenderer.getWidth(label)) / 2.0f, y + keyHeight / 2.0f - textRenderer.fontHeight + 2);
		context.drawText(textRenderer, label, 0, 0, color, shadow.getValue());
		matrices.popMatrix();

		Text cpsLabel = Text.of(cps + " CPS");
		matrices.pushMatrix();
		matrices.translate(x + (keyWidth - textRenderer.getWidth(cpsLabel) * 0.7f) / 2.0f, y + keyHeight / 2.0f + 3);
		matrices.scale(0.7f);
		context.drawText(textRenderer, cpsLabel, 0, 0, color, shadow.getValue());
		matrices.popMatrix();
	}

	private int getTextShadowColor(int baseColor) {
		return ((baseColor & 0xFCFCFC) >> 2) | (baseColor & 0xFF000000);
	}

	private int getColor(float fadeFactor) {
		return ColorHelper.lerp(fadeFactor, getColor(), getColorPressed());
	}

	private int getColorPressed() {
		if (chromaColorPressed.getValue()) {
			return ChromaColorTickable.getColor();
		}
		return ColorHelper.withAlpha(255, colorPressed.getValue());
	}

	private int getBorderColor() {
		return ColorHelper.withAlpha(255, borderColor.getValue());
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 200;
				} else {
					buttonWidth = 170;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColor)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(color)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColorPressed)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(colorPressed)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackground)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColor)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackgroundPressed)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColorPressed)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showBorder)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(borderColor)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(displayCps)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(useArrow)
								.build()
				);
			}
		};
	}
}
