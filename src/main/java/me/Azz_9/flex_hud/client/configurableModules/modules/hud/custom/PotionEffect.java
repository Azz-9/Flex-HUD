package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import com.google.common.collect.Ordering;
import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.Renderable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.RenderableSprite;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.RenderableText;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PotionEffect extends AbstractTextElement {

	public PotionEffect(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.potion_effect.config.enable");
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.potion_effect");
	}

	@Override
	public String getID() {
		return "potion_effect";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && client.player == null) {
			return;
		}

		this.width = 0;

		List<Renderable> renderables = new ArrayList<>();

		int hudX = 0, hudY = 0;

		List<StatusEffectInstance> playerEffects;
		if (Flex_hudClient.isInMoveElementScreen) {
			playerEffects = List.of(new StatusEffectInstance(StatusEffects.SPEED, 1800, 1), new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200));
		} else {
			playerEffects = Ordering.natural().sortedCopy(client.player.getStatusEffects());
		}

		for (StatusEffectInstance effect : playerEffects) {
			if (effect == null) continue;

			int effectWidth;

			String effectString = Text.translatable(effect.getTranslationKey()).getString() + " " + (effect.getAmplifier() + 1);
			renderables.add(new RenderableText(hudX, hudY, Text.of(effectString), getColor(), shadow.getValue()));
			effectWidth = client.textRenderer.getWidth(effectString);

			String durationString = effect.isInfinite() ? "∞" : getDurationString(effect.getDuration() / 20);
			int textColor = (effect.isInfinite() ? getColor() : (getColor() & 0x00ffffff) | (getTimestampAlpha(effect.getDuration()) << 24));
			renderables.add(new RenderableText(hudX, hudY + 10, Text.of(durationString), textColor, shadow.getValue()));
			effectWidth = Math.max(effectWidth, client.textRenderer.getWidth(durationString));
			this.height = hudY + client.textRenderer.fontHeight;

			Sprite sprite = client.getStatusEffectSpriteManager().getSprite(effect.getEffectType());
			int iconSize = 18;
			renderables.add(new RenderableSprite(effectWidth + 2, hudY, sprite, iconSize, iconSize));
			this.width = Math.max(this.width, effectWidth + 2 + iconSize);
			this.height = Math.max(this.height, hudY + iconSize);

			hudY += 25;
		}

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawBackground(context);

		for (Renderable renderable : renderables) {
			renderable.render(context, tickCounter);
		}

		matrices.pop();
	}

	private String getDurationString(int duration) {
		if (duration < 3600) {
			return String.format("%02d:%02d", (duration / 60), (duration % 60));
		} else {
			return String.format("%02d:%02d:%02d", (duration / 3600), ((duration % 3600) / 60), (duration % 60));
		}
	}

	private int getTimestampAlpha(int duration) {
		MinecraftClient client = MinecraftClient.getInstance();

		// Always opaque if paused or no world (e.g., title screen)
		if (client.isPaused() || client.world == null) {
			return 255;
		}

		// Smooth fraction of current tick [0..1]; works on Fabric/Yarn
		float tickDelta = client.getRenderTickCounter().getTickDelta(true);

		// Add tickDelta to world time for smooth animation
		float ticks = client.world.getTime() + tickDelta;

		// 20 ticks ~= 1 second at 20 TPS
		float cycle = (ticks % 20.0f) / 20.0f;

		// Sinus pulse between 0 and 1
		float alpha01 = (float) (Math.sin(cycle * Math.PI * 2.0) * 0.5 + 0.5);

		if (duration <= 100) {
			// in this version alpha less than 4 make the text display like the alpha was 255
			return (int) Math.max((alpha01 * 255), 4); // faster pulse
		} else if (duration <= 200) {
			return (int) ((alpha01 * 0.5f + 0.5f) * 255); // softer pulse
		}

		return 255;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 180;
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
								.setVariable(hideInF3)
								.build()
				);
			}
		};
	}
}
