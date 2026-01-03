package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import com.google.common.collect.Ordering;
import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.Alignment;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.MultiRenderable;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.RenderableImage;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.RenderableText;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

public class PotionEffect extends AbstractTextModule {
	private final ConfigEnum<Alignment> alignment = new ConfigEnum<>(Alignment.class, Alignment.AUTO, "flex_hud.potion_effect.config.alignment");
	private final ConfigEnum<IconPlacement> iconPlacement = new ConfigEnum<>(IconPlacement.class, IconPlacement.RIGHT, "flex_hud.potion_effect.config.icon_placement");

	public PotionEffect(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.potion_effect.config.enable");

		ConfigRegistry.register(getID(), "alignment", alignment);
		ConfigRegistry.register(getID(), "iconPlacement", iconPlacement);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.potion_effect");
	}

	@Override
	public String getID() {
		return "potion_effect";
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();

		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && minecraft.player == null) {
			return;
		}

		//List<Renderable> renderables = new ArrayList<>();
		List<MultiRenderable> renderables = new ArrayList<>();

		int hudY = 0;

		List<MobEffectInstance> playerEffects;
		if (Flex_hudClient.isInMoveElementScreen) {
			playerEffects = List.of(new MobEffectInstance(MobEffects.SPEED, 1800, 1), new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200));
		} else {
			playerEffects = Ordering.natural().sortedCopy(minecraft.player.getActiveEffects());
		}

		setWidth(0);
		setHeight(0);

		int iconSize = 18;
		int textGap = 1;
		int textIconGap = 2;
		int effectGap = 6;

		// height
		final int horizontalHeight = Math.max(iconSize, minecraft.font.lineHeight * 2 + textGap);
		final int verticalHeight = iconSize + textIconGap + minecraft.font.lineHeight * 2 + textGap;

		for (MobEffectInstance effect : playerEffects) {
			if (effect == null) continue;

			int textWidth;

			String effectString = Component.translatable(effect.getDescriptionId()).getString() + " " + (effect.getAmplifier() + 1);
			String durationString = effect.isInfiniteDuration() ? "∞" : getDurationString(effect.getDuration() / 20);
			Identifier icon = Gui.getMobEffectSprite(effect.getEffect());

			textWidth = Math.max(
					minecraft.font.width(effectString),
					minecraft.font.width(durationString)
			);

			int durationColor = (effect.isInfiniteDuration() ? getColor() : ARGB.color(getTimestampAlpha(effect.getDuration()), getColor()));

			switch (iconPlacement.getValue()) {
				case TOP -> {
					setHeight(hudY + iconSize + textIconGap + minecraft.font.lineHeight * 2 + textGap);
					setWidth(Math.max(getWidth(), Math.max(textWidth, iconSize)));

					renderables.add(new MultiRenderable(0, textWidth,
							new RenderableText(0, hudY + iconSize + textIconGap, Component.literal(effectString), getColor(), shadow.getValue()),
							new RenderableText(0, hudY + iconSize + textIconGap + minecraft.font.lineHeight + textGap, Component.literal(durationString), durationColor, shadow.getValue())
					));
					renderables.add(new MultiRenderable(0, iconSize,
							new RenderableImage(0, hudY, icon, iconSize, iconSize)
					));

					hudY += verticalHeight + effectGap;
				}
				case RIGHT -> {
					int currentWidth = textWidth + textIconGap + iconSize;

					setWidth(Math.max(getWidth(), currentWidth));
					setHeight(Math.max(getHeight(), hudY + horizontalHeight));

					renderables.add(new MultiRenderable(0, currentWidth,
							new RenderableText(0, hudY, Component.literal(effectString), getColor(), shadow.getValue()),
							new RenderableText(0, hudY + minecraft.font.lineHeight + textGap, Component.literal(durationString), durationColor, shadow.getValue()),
							new RenderableImage(textWidth + textIconGap, hudY, icon, iconSize, iconSize)
					));

					hudY += horizontalHeight + effectGap;
				}
				case BOTTOM -> {
					setHeight(hudY + iconSize + textIconGap + minecraft.font.lineHeight * 2 + textGap);
					setWidth(Math.max(getWidth(), Math.max(textWidth, iconSize)));

					renderables.add(new MultiRenderable(0, textWidth,
							new RenderableText(0, hudY, Component.literal(effectString), getColor(), shadow.getValue()),
							new RenderableText(0, hudY + minecraft.font.lineHeight + textGap, Component.literal(durationString), durationColor, shadow.getValue())

					));
					renderables.add(new MultiRenderable(0, iconSize,
							new RenderableImage(0, hudY + minecraft.font.lineHeight * 2 + textGap + textIconGap, icon, iconSize, iconSize)
					));

					hudY += verticalHeight + effectGap;
				}
				case LEFT -> {
					int currentWidth = textWidth + textIconGap + iconSize;

					setWidth(Math.max(getWidth(), currentWidth));
					setHeight(Math.max(getHeight(), hudY + horizontalHeight));

					renderables.add(new MultiRenderable(0, currentWidth,
							new RenderableImage(0, hudY, icon, iconSize, iconSize),
							new RenderableText(iconSize + textIconGap, hudY, Component.literal(effectString), getColor(), shadow.getValue()),
							new RenderableText(iconSize + textIconGap, hudY + minecraft.font.lineHeight + textGap, Component.literal(durationString), durationColor, shadow.getValue())
					));

					hudY += horizontalHeight + effectGap;
				}
			}
		}

		if (alignment.getValue() == Alignment.RIGHT || alignment.getValue() == Alignment.AUTO && getAnchorX() == AnchorPosition.END) {
			MultiRenderable.alignRight(renderables, getWidth());
		} else if (alignment.getValue() == Alignment.CENTER || alignment.getValue() == Alignment.AUTO && getAnchorX() == AnchorPosition.CENTER) {
			MultiRenderable.alignCenter(renderables, getWidth() / 2);
		}

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		for (MultiRenderable multiRenderable : renderables) {
			multiRenderable.render(graphics, deltaTracker);
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
		Minecraft minecraft = Minecraft.getInstance();

		// Always opaque if paused or no world (e.g., title screen)
		if (minecraft.isPaused() || minecraft.level == null) {
			return 255;
		}

		// Smooth fraction of current tick [0..1]; works on Fabric/Yarn
		float tickDelta = minecraft.getDeltaTracker().getGameTimeDeltaTicks();

		// Add tickDelta to world time for smooth animation
		float ticks = minecraft.level.getDayTime() + tickDelta;

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
				if (Minecraft.getInstance().getLanguageManager().getSelected().equals("fr_fr")) {
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
