package me.Azz_9.flex_hud.client.modules.hud.custom;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.utils.CpsUtils;

public class Cps extends AbstractTextModule {

	private static final long HIDE_DELAY_MS = 2000L;
	private static final long FADE_OUT_DURATION_MS = 2000L;
	private static final long FADE_IN_DURATION_MS = 100L;

	public final ConfigBoolean showLeftClick = new ConfigBoolean(true, "flex_hud.cps.config.show_left_click");
	public final ConfigBoolean showRightClick = new ConfigBoolean(true, "flex_hud.cps.config.show_right_click");
	public final ConfigBoolean showSuffix = new ConfigBoolean(true, "flex_hud.cps.config.show_suffix");
	public final ConfigBoolean hideWhenNotClicking = new ConfigBoolean(false, "flex_hud.cps.config.hide_when_not_clicking");

	private float fadeAlpha = 1.0f;
	private long lastClickTime = 0;
	private long fadeInStartTime = 0;

	public Cps(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("cps", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.cps.config.enable");

		ConfigRegistry.register(getID(), "showLeftClick", showLeftClick);
		ConfigRegistry.register(getID(), "showRightClick", showRightClick);
		ConfigRegistry.register(getID(), "showSuffix", showSuffix);
		ConfigRegistry.register(getID(), "hideWhenNotClicking", hideWhenNotClicking);
	}

	@Override
	public void init() {
		setHeight(MINECRAFT.font.lineHeight);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.cps");
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender()) {
			return;
		}

		if (hideWhenNotClicking.getValue()) {
			long currentClickTime;
			if (showLeftClick.getValue() && showRightClick.getValue()) {
				currentClickTime = Math.max(CpsUtils.getLastLeftClickTime(), CpsUtils.getLastRightClickTime());
			} else if (showLeftClick.getValue()) {
				currentClickTime = CpsUtils.getLastLeftClickTime();
			} else if (showRightClick.getValue()) {
				currentClickTime = CpsUtils.getLastRightClickTime();
			} else {
				currentClickTime = 0;
			}

			if (currentClickTime > lastClickTime) {
				lastClickTime = currentClickTime;
			}

			long timeSinceLastClick = System.currentTimeMillis() - lastClickTime;
			boolean wasHidden = fadeAlpha <= 0.0f;
			if (timeSinceLastClick <= HIDE_DELAY_MS) {
				if (wasHidden) {
					if (fadeInStartTime == 0) {
						fadeInStartTime = System.currentTimeMillis();
					}
					fadeAlpha = Math.min(1.0f, (System.currentTimeMillis() - fadeInStartTime) / (float) FADE_IN_DURATION_MS);
				} else {
					fadeAlpha = Math.min(1.0f, (System.currentTimeMillis() - fadeInStartTime) / (float) FADE_IN_DURATION_MS);
				}
			} else {
				float fadeProgress = (timeSinceLastClick - HIDE_DELAY_MS) / (float) FADE_OUT_DURATION_MS;
				fadeAlpha = Math.max(0.0f, 1.0f - fadeProgress);
				if (fadeAlpha <= 0.0f) {
					fadeInStartTime = 0;
				}
			}

			if (fadeAlpha <= 0.0f) {
				return;
			}
		} else {
			fadeAlpha = 1.0f;
		}

		StringBuilder stringBuilder = new StringBuilder();
		if (this.showLeftClick.getValue()) {
			stringBuilder.append(CpsUtils.getLeftCps());
		}
		if (this.showLeftClick.getValue() && this.showRightClick.getValue()) {
			stringBuilder.append(" | ");
		}
		if (this.showRightClick.getValue()) {
			stringBuilder.append(CpsUtils.getRightCps());
		}

		if (showSuffix.getValue()) {
			stringBuilder.append(" CPS");
		}

		String text = stringBuilder.toString();

		setWidth(text);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(0, graphics, getWidth(), getHeight(), fadeAlpha);

		graphics.drawString(MINECRAFT.font, text, 0, 0, Colors.multiplyAlpha(getColor(), fadeAlpha), this.shadow.getValue());

		matrices.popMatrix();
	}

	@Override
	public boolean shouldShowInEditLayoutScreen() {
		return super.shouldShowInEditLayoutScreen() && showLeftClick.getValue() && showRightClick.getValue();
	}

	@Override
	public boolean shouldNotRender() {
		return super.shouldNotRender() || (!(this.showLeftClick.getValue() || this.showRightClick.getValue()) && !CommonClass.isEditingLayout);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 190;
				}

				super.initContent();

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
								.setVariable(hideInF3)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeX)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeX(anchorModeX.getValue()))
								.build(),
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeY)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeY(anchorModeY.getValue()))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showLeftClick)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showRightClick)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showSuffix)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideWhenNotClicking)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
