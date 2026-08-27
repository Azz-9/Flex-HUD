package me.Azz_9.flex_hud.client.modules.hud.vanilla;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.modules.hud.DimensionHud;

public class Titles extends AbstractTextModule {

	public final @NotNull ConfigBoolean showTitle = new ConfigBoolean(true, "flex_hud.titles.config.show_title");
	public final @NotNull ConfigBoolean showSubtitle = new ConfigBoolean(true, "flex_hud.titles.config.show_subtitle");

	public static Component placeholderTitle;
	public static Component placeholderSubtitle;

	public Titles(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("titles", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		enabled.setConfigTextTranslationKey("flex_hud.titles.config.enable");

		hideInF3.setDefaultValue(false);
		hideInF3.setValue(false);

		// subtitle
		getDimensionHudList().add(new DimensionHud(defaultOffsetX, defaultOffsetY + 41, defaultAnchorX, defaultAnchorY));

		DimensionHud.register(getID(), getDimensionHudList());

		ConfigRegistry.register(getID(), "showTitle", showTitle);
		ConfigRegistry.register(getID(), "showSubtitle", showSubtitle);
	}

	@Override
	public void init() {
		placeholderTitle = Component.translatable("flex_hud.titles.placeholder.title");
		placeholderSubtitle = Component.translatable("flex_hud.titles.placeholder.subtitle");
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker tickCounter) {
		// render is handled in HudMixin
		if (MINECRAFT.level == null) {
			MINECRAFT.gui.renderTitle(graphics, tickCounter);
		}
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.titles");
	}

	@Override
	public boolean shouldShowInEditLayoutScreen() {
		return super.shouldShowInEditLayoutScreen() && (showTitle.getValue() || showSubtitle.getValue());
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 180;
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
								.setVariable(showTitle)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showSubtitle)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.addDependency(this.getConfigList().getFirstEntry(), false)
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
								.build()
				);
			}
		};
	}

	@Override
	public List<String> getKeywords() {
		List<String> keywords = super.getKeywords();
		keywords.add("subtitles");
		return keywords;
	}
}
