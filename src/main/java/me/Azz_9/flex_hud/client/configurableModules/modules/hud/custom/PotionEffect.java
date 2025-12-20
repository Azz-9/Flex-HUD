package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import com.google.common.collect.Ordering;
import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.Alignment;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.MultiRenderable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.RenderableImage;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.RenderableText;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

public class PotionEffect extends AbstractTextElement {
	private final ConfigEnum<Alignment> alignment = new ConfigEnum<>(Alignment.class, Alignment.AUTO, "flex_hud.potion_effect.config.alignment");

	public PotionEffect(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.potion_effect.config.enable");

		ConfigRegistry.register(getID(), "alignment", alignment);
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

		//List<Renderable> renderables = new ArrayList<>();
		List<MultiRenderable> renderables = new ArrayList<>();

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
			effectWidth = client.textRenderer.getWidth(effectString);

			String durationString = effect.isInfinite() ? "∞" : getDurationString(effect.getDuration() / 20);
			int textColor = (effect.isInfinite() ? getColor() : (getColor() & 0x00ffffff) | (getTimestampAlpha(effect.getDuration()) << 24));
			effectWidth = Math.max(effectWidth, client.textRenderer.getWidth(durationString));
			this.height = hudY + client.textRenderer.fontHeight;

			Identifier icon = InGameHud.getEffectTexture(effect.getEffectType());
			int iconSize = 18;
			int currentWidth = effectWidth + 2 + iconSize;
			this.width = Math.max(this.width, currentWidth);
			this.height = Math.max(this.height, hudY + iconSize);

			renderables.add(new MultiRenderable(hudX, currentWidth,
					new RenderableText(hudX, hudY, Text.of(effectString), getColor(), shadow.getValue()),
					new RenderableText(hudX, hudY + 10, Text.of(durationString), textColor, shadow.getValue()),
					new RenderableImage(effectWidth + 2, hudY, icon, iconSize, iconSize)
			));

			hudY += 25;
		}

		if (alignment.getValue() == Alignment.RIGHT || alignment.getValue() == Alignment.AUTO && getAnchorX() == AnchorPosition.END) {
			MultiRenderable.alignRight(renderables, this.width);
		} else if (alignment.getValue() == Alignment.CENTER || alignment.getValue() == Alignment.AUTO && getAnchorX() == AnchorPosition.CENTER) {
			MultiRenderable.alignCenter(renderables, this.width / 2);
		}

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(context);

		for (MultiRenderable multiRenderable : renderables) {
			multiRenderable.render(context, tickCounter);
		}

		matrices.popMatrix();
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
		float ticks = client.world.getTimeOfDay() + tickDelta;

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
	public List<String> getKeywords() {
		List<String> keywords = new ArrayList<>(super.getKeywords());
		keywords.add("buffs & debuffs");
		keywords.add("buffs and debuffs");
		return keywords;
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
						new CyclingButtonEntry.Builder<Alignment>()
								.setCyclingButtonWidth(80)
								.setVariable(alignment)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
