package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.tickables.ChromaColorTickable;
import me.Azz_9.flex_hud.client.utils.cps.CpsUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.HashMap;
import java.util.Map;

import static me.Azz_9.flex_hud.client.utils.DrawingUtils.drawBorder;

public class KeyStrokes extends AbstractTextModule {

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
	private final Map<KeyMapping, KeyAnimation> keyAnimations = new HashMap<>();

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
		setWidth(keySize * 3 + gap * 4);
		setHeight((int) (keySize * 3.5 + gap * 5));
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.key_strokes");
	}

	@Override
	public String getID() {
		return "key_strokes";
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();

		if (shouldNotRender()) {
			return;
		}

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		// forward key
		Component forwardText = useArrow.getValue() ? Component.literal("▲") : minecraft.options.keyUp.getTranslatedKeyMessage();
		renderMovementKey(graphics, keySize + gap * 2, gap, keySize, keySize, minecraft.options.keyUp, forwardText);

		// back key
		Component backText = useArrow.getValue() ? Component.literal("▼") : minecraft.options.keyDown.getTranslatedKeyMessage();
		renderMovementKey(graphics, keySize + gap * 2, keySize + gap * 2, keySize, keySize, minecraft.options.keyDown, backText);

		// right key
		Component rightText = useArrow.getValue() ? Component.literal("▶") : minecraft.options.keyRight.getTranslatedKeyMessage();
		renderMovementKey(graphics, keySize * 2 + gap * 3, keySize + gap * 2, keySize, keySize, minecraft.options.keyRight, rightText);

		// left key
		Component leftText = useArrow.getValue() ? Component.literal("◀") : minecraft.options.keyLeft.getTranslatedKeyMessage();
		renderMovementKey(graphics, gap, keySize + gap * 2, keySize, keySize, minecraft.options.keyLeft, leftText);

		// jump key
		renderJumpKey(graphics, gap, keySize * 2 + gap * 3, keySize * 3 + gap * 2, keySize / 2, minecraft.options.keyJump);

		if (displayCps.getValue()) {
			// attack key
			renderMouseKey(graphics, gap, (int) (keySize * 2.5) + gap * 4, (int) (keySize * 1.5) + gap / 2, keySize, minecraft.options.keyAttack, CpsUtils.getLeftCps(), Component.literal("LMB"));

			// use key
			renderMouseKey(graphics, (int) (keySize * 1.5 + gap * 2.5) + 1, (int) (keySize * 2.5) + gap * 4, (int) (keySize * 1.5) + gap / 2, keySize, minecraft.options.keyUse, CpsUtils.getRightCps(), Component.literal("RMB"));
		}

		matrices.popMatrix();
	}

	private float renderKey(GuiGraphics graphics, int x, int y, int keyWidth, int keyHeight, KeyMapping keyMapping) {
		boolean isPressed = keyMapping.isDown();
		long now = System.currentTimeMillis();

		long fadeInDuration = 100;
		long fadeOutDuration = 400;

		KeyAnimation anim = keyAnimations.computeIfAbsent(keyMapping, k -> {
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
			graphics.fill(x, y, x + keyWidth, y + keyHeight, getBackgroundColor());
		}
		if (drawBackgroundPressed.getValue()) {
			graphics.fill(x, y, x + keyWidth, y + keyHeight, ARGB.color(fadeFactor / 2, backgroundColorPressed.getValue()));
		}

		if (showBorder.getValue()) {
			drawBorder(graphics, x - borderThickness, y - borderThickness, keyWidth + borderThickness * 2, keyHeight + borderThickness * 2, borderThickness, getBorderColor());
		}

		return fadeFactor;
	}

	private void renderMovementKey(GuiGraphics graphics, int x, int y, int keyWidth, int keyHeight, KeyMapping keyMapping, Component label) {
		float fadeFactor = renderKey(graphics, x, y, keyWidth, keyHeight, keyMapping);

		Font font = Minecraft.getInstance().font;

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(x + (keyWidth - font.width(label)) / 2.0f, y + (keyHeight - font.lineHeight) / 2.0f);

		graphics.drawString(font, label, 0, 0, getColor(fadeFactor), shadow.getValue());

		matrices.popMatrix();
	}

	private void renderJumpKey(GuiGraphics graphics, int x, int y, int keyWidth, int keyHeight, KeyMapping keyMapping) {
		float fadeFactor = renderKey(graphics, x, y, keyWidth, keyHeight, keyMapping);

		int barX1 = x + keyWidth / 4;
		int barY1 = y + keyHeight / 2 - 2;
		int barX2 = (int) (x + keyWidth * 0.75);
		int barY2 = y + keyHeight / 2;

		int color = getColor(fadeFactor);

		graphics.fill(barX1 + 1, barY1 + 1, barX2 + 1, barY2 + 1, getTextShadowColor(color));

		graphics.fill(barX1, barY1, barX2, barY2, color);
	}

	private void renderMouseKey(GuiGraphics graphics, int x, int y, int keyWidth, int keyHeight, KeyMapping keyMapping, int cps, Component label) {
		float fadeFactor = renderKey(graphics, x, y, keyWidth, keyHeight, keyMapping);

		Font font = Minecraft.getInstance().font;
		int color = getColor(fadeFactor);

		Matrix3x2fStack matrices = graphics.pose();

		matrices.pushMatrix();
		matrices.translate(x + (keyWidth - font.width(label)) / 2.0f, y + keyHeight / 2.0f - font.lineHeight + 2);
		graphics.drawString(font, label, 0, 0, color, shadow.getValue());
		matrices.popMatrix();

		Component cpsLabel = Component.literal(cps + " CPS");
		matrices.pushMatrix();
		matrices.translate(x + (keyWidth - font.width(cpsLabel) * 0.7f) / 2.0f, y + keyHeight / 2.0f + 3);
		matrices.scale(0.7f);
		graphics.drawString(font, cpsLabel, 0, 0, color, shadow.getValue());
		matrices.popMatrix();
	}

	private int getTextShadowColor(int baseColor) {
		return ((baseColor & 0xFCFCFC) >> 2) | (baseColor & 0xFF000000);
	}

	private int getColor(float fadeFactor) {
		return ARGB.srgbLerp(fadeFactor, getColor(), getColorPressed());
	}

	private int getColorPressed() {
		if (chromaColorPressed.getValue()) {
			return ChromaColorTickable.getColor();
		}
		return ARGB.color(255, colorPressed.getValue());
	}

	private int getBorderColor() {
		return ARGB.color(255, borderColor.getValue());
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (Minecraft.getInstance().getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 200;
				} else {
					buttonWidth = 170;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(color)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColorPressed)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(colorPressed)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackground)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackgroundPressed)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColorPressed)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showBorder)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(borderColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(displayCps)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(useArrow)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
