package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import com.google.common.collect.Ordering;
import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
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
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

public class PotionEffect extends AbstractTextElement {
	private final ConfigEnum<Alignment> alignment = new ConfigEnum<>(Alignment.class, Alignment.AUTO, "flex_hud.potion_effect.config.alignment");
	private final ConfigEnum<IconPlacement> iconPlacement = new ConfigEnum<>(IconPlacement.class, IconPlacement.RIGHT, "flex_hud.potion_effect.config.icon_placement");

	public PotionEffect(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.potion_effect.config.enable");

		ConfigRegistry.register(getID(), "alignment", alignment);
		ConfigRegistry.register(getID(), "iconPlacement", iconPlacement);
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
		this.height = 0;

		//List<Renderable> renderables = new ArrayList<>();
		List<MultiRenderable> renderables = new ArrayList<>();

		int hudY = 0;

		List<StatusEffectInstance> playerEffects;
		if (Flex_hudClient.isInMoveElementScreen) {
			playerEffects = List.of(new StatusEffectInstance(StatusEffects.SPEED, 1800, 1), new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200));
		} else {
			playerEffects = Ordering.natural().sortedCopy(client.player.getStatusEffects());
		}

		int iconSize = 18;
		int textGap = 1;
		int textIconGap = 2;
		int effectGap = 6;

		// height
		final int horizontalHeight = Math.max(iconSize, client.textRenderer.fontHeight * 2 + textGap);
		final int verticalHeight = iconSize + textIconGap + client.textRenderer.fontHeight * 2 + textGap;

		for (StatusEffectInstance effect : playerEffects) {
			if (effect == null) continue;

			int textWidth;

			String effectString = Text.translatable(effect.getTranslationKey()).getString() + " " + (effect.getAmplifier() + 1);
			String durationString = effect.isInfinite() ? "∞" : getDurationString(effect.getDuration() / 20);
			Identifier icon = InGameHud.getEffectTexture(effect.getEffectType());

			textWidth = Math.max(
					client.textRenderer.getWidth(effectString),
					client.textRenderer.getWidth(durationString)
			);

			int durationColor = (effect.isInfinite() ? getColor() : ColorHelper.withAlpha(getTimestampAlpha(effect.getDuration()), getColor()));

			switch (iconPlacement.getValue()) {
				case TOP -> {
					this.height = hudY + iconSize + textIconGap + client.textRenderer.fontHeight * 2 + textGap;
					this.width = Math.max(this.width, Math.max(textWidth, iconSize));

					renderables.add(new MultiRenderable(0, textWidth,
							new RenderableText(0, hudY + iconSize + textIconGap, Text.of(effectString), getColor(), shadow.getValue()),
							new RenderableText(0, hudY + iconSize + textIconGap + client.textRenderer.fontHeight + textGap, Text.of(durationString), durationColor, shadow.getValue())
					));
					renderables.add(new MultiRenderable(0, iconSize,
							new RenderableImage(0, hudY, icon, iconSize, iconSize)
					));

					hudY += verticalHeight + effectGap;
				}
				case RIGHT -> {
					int currentWidth = textWidth + textIconGap + iconSize;

					this.width = Math.max(this.width, currentWidth);
					this.height = Math.max(this.height, hudY + horizontalHeight);

					renderables.add(new MultiRenderable(0, currentWidth,
							new RenderableText(0, hudY, Text.of(effectString), getColor(), shadow.getValue()),
							new RenderableText(0, hudY + client.textRenderer.fontHeight + textGap, Text.of(durationString), durationColor, shadow.getValue()),
							new RenderableImage(textWidth + textIconGap, hudY, icon, iconSize, iconSize)
					));

					hudY += horizontalHeight + effectGap;
				}
				case BOTTOM -> {
					this.height = hudY + iconSize + textIconGap + client.textRenderer.fontHeight * 2 + textGap;
					this.width = Math.max(this.width, Math.max(textWidth, iconSize));

					renderables.add(new MultiRenderable(0, textWidth,
							new RenderableText(0, hudY, Text.of(effectString), getColor(), shadow.getValue()),
							new RenderableText(0, hudY + client.textRenderer.fontHeight + textGap, Text.of(durationString), durationColor, shadow.getValue())

					));
					renderables.add(new MultiRenderable(0, iconSize,
							new RenderableImage(0, hudY + client.textRenderer.fontHeight * 2 + textGap + textIconGap, icon, iconSize, iconSize)
					));

					hudY += verticalHeight + effectGap;
				}
				case LEFT -> {
					int currentWidth = textWidth + textIconGap + iconSize;

					this.width = Math.max(this.width, currentWidth);
					this.height = Math.max(this.height, hudY + horizontalHeight);

					renderables.add(new MultiRenderable(0, currentWidth,
							new RenderableImage(0, hudY, icon, iconSize, iconSize),
							new RenderableText(iconSize + textIconGap, hudY, Text.of(effectString), getColor(), shadow.getValue()),
							new RenderableText(iconSize + textIconGap, hudY + client.textRenderer.fontHeight + textGap, Text.of(durationString), durationColor, shadow.getValue())
					));

					hudY += horizontalHeight + effectGap;
				}
			}
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
					buttonWidth = 200;
				} else {
					buttonWidth = 165;
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
								.build(),
						new CyclingButtonEntry.Builder<IconPlacement>()
								.setCyclingButtonWidth(80)
								.setVariable(iconPlacement)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}

	public enum IconPlacement implements Translatable {
		TOP("flex_hud.enum.potion_effect.icon_placement.top"),
		RIGHT("flex_hud.enum.potion_effect.icon_placement.right"),
		BOTTOM("flex_hud.enum.potion_effect.icon_placement.bottom"),
		LEFT("flex_hud.enum.potion_effect.icon_placement.left");

		private final String translationKey;

		IconPlacement(String translationKey) {
			this.translationKey = translationKey;
		}

		@Override
		public String getTranslationKey() {
			return translationKey;
		}
	}
}
