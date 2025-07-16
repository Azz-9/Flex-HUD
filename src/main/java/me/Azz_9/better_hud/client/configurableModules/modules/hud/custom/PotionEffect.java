package me.Azz_9.better_hud.client.configurableModules.modules.hud.custom;

import com.google.common.collect.Ordering;
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
		for (StatusEffectInstance effect : Ordering.natural().sortedCopy(client.player.getStatusEffects())) {
			int effectWidth;

			String effectString = Text.translatable(effect.getTranslationKey()).getString() + " " + (effect.getAmplifier() + 1);
			renderables.add(new RenderableText(hudX, hudY, Text.of(effectString), getColor(), shadow.getValue()));
			effectWidth = client.textRenderer.getWidth(effectString);

			String durationString = effect.isInfinite() ? "∞" : getDurationString(effect.getDuration() / 20);
			renderables.add(new RenderableText(hudX, hudY + 10, Text.of(durationString), getColor(), shadow.getValue()));
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
