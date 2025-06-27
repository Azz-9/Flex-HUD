package me.Azz_9.better_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

import java.util.LinkedList;
import java.util.List;

public class DayCounter extends AbstractHudElement {
	public static List<Long> times = new LinkedList<>();

	public DayCounter(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled = false;
	}

	@Override
	public void init() {
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
	}

	@Override
	public String getID() {
		return "day_counter";
	}

	@Override
	public Text getName() {
		return Text.translatable("better_hud.day_counter");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		long a = System.nanoTime();

		MinecraftClient client = MinecraftClient.getInstance();

		if (!JsonConfigHelper.getInstance().isEnabled || !this.enabled || client == null || (this.hideInF3 && client.getDebugHud().shouldShowDebugHud()) || client.world == null) {
			return;
		}

		long time = client.world.getTimeOfDay() / 24000;
		Text text = Text.translatable("better_hud.day_counter.hud.prefix").append(" " + (int) time);

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(Math.round(getX()), Math.round(getY()));
		matrices.scale(this.scale, this.scale);

		// render background using calculated width and height from the previous render
		if (drawBackground) {
			context.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor);
		}

		context.drawText(client.textRenderer, text, 0, 0, getColor(), this.shadow);

		setWidth(text.getString());

		matrices.popMatrix();

		long b = System.nanoTime();
		times.add(b - a);
		if (times.size() > 1000) {
			times.removeFirst();
		}
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(getName(), parent, parentScrollAmount) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 160;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(enabled)
								.setDefaultValue(false)
								.setOnToggle((toggled) -> enabled = toggled)
								.setText(Text.translatable("better_hud.day_counter.config.enable"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(shadow)
								.setDefaultValue(true)
								.setOnToggle(toggled -> shadow = toggled)
								.setText(Text.translatable("better_hud.global.config.text_shadow"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(chromaColor)
								.setDefaultValue(false)
								.setOnToggle(toggled -> chromaColor = toggled)
								.setText(Text.translatable("better_hud.global.config.chroma_text_color"))
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setColor(color)
								.setDefaultColor(0xffffff)
								.setOnColorChange(newColor -> color = newColor)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.setText(Text.translatable("better_hud.global.config.text_color"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(drawBackground)
								.setDefaultValue(false)
								.setOnToggle(toggled -> drawBackground = toggled)
								.setText(Text.translatable("better_hud.global.config.show_background"))
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setColor(backgroundColor)
								.setDefaultColor(0x313131)
								.setOnColorChange(newColor -> backgroundColor = newColor)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.setText(Text.translatable("better_hud.global.config.background_color"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(hideInF3)
								.setDefaultValue(true)
								.setOnToggle(toggled -> hideInF3 = toggled)
								.setText(Text.translatable("better_hud.global.config.hide_in_f3"))
								.build()
				);
			}
		};
	}
}
