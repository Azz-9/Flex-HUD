package me.Azz_9.better_hud.client.configurableModules.modules.hud.custom;

import com.google.common.collect.Ordering;
import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.renderable.Renderable;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.renderable.RenderableImage;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.renderable.RenderableText;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PotionEffect extends AbstractHudElement {
	public static List<Long> times = new LinkedList<>();

	public PotionEffect(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
	}

	@Override
	public void init() {
		this.enabled.setConfigTextTranslationKey("better_hud.potion_effect.config.enable");
	}

	@Override
	public Text getName() {
		return Text.translatable("better_hud.potion_effect");
	}

	@Override
	public String getID() {
		return "potion_effect";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		long a = System.nanoTime();

		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender() || client.player == null) {
			return;
		}

		this.width = 0;

		List<Renderable> renderables = new ArrayList<>();

		int hudX = 0, hudY = 0;

		List<StatusEffectInstance> playerEffects;
		if (Better_hudClient.isInMoveElementScreen) {
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

			Identifier icon = effect.getEffectType().getKey().map(RegistryKey::getValue).map((id) -> id.withPrefixedPath("mob_effect/")).orElseGet(MissingSprite::getMissingSpriteId);
			int iconSize = 18;
			renderables.add(new RenderableImage(effectWidth + 2, hudY, icon, iconSize, iconSize));
			this.width = Math.max(this.width, effectWidth + 2 + iconSize);
			this.height = Math.max(this.height, hudY + iconSize);

			hudY += 25;
		}

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(this.scale, this.scale);

		drawBackground(context);

		for (Renderable renderable : renderables) {
			renderable.render(context, tickCounter);
		}

		matrices.popMatrix();

		long b = System.nanoTime();
		times.add(b - a);
		if (times.size() > 1000) {
			times.removeFirst();
		}
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
		float tickDelta = client.getRenderTickCounter().getDynamicDeltaTicks();

		// Add tickDelta to world time for smooth animation
		float ticks = client.world.getTime() + tickDelta;

		// 20 ticks ~= 1 second at 20 TPS
		float cycle = (ticks % 20.0f) / 20.0f;

		// Sinus pulse between 0 and 1
		float alpha01 = (float) (Math.sin(cycle * Math.PI * 2.0) * 0.5 + 0.5);

		if (duration <= 100) {
			return (int) (alpha01 * 255); // faster pulse
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
