package me.Azz_9.flex_hud.client.modules.hud.custom;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.ARGB;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import java.io.InputStream;
import java.util.List;

import me.Azz_9.flex_hud.FlexHudLogger;
import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.TickableModule;
import me.Azz_9.flex_hud.client.modules.hud.AbstractTextModule;

public class ResourcePack extends AbstractTextModule implements TickableModule {

	public ConfigBoolean showDescription = new ConfigBoolean(true, "flex_hud.resource_pack.config.show_description");

	private @Nullable Pack lastSelectedPack = null;
	private @Nullable String selectedPackId = null;
	private @Nullable ResourceLocation selectedPackIcon = null;
	private @Nullable StringWidget selectedPackTitleWidget = null;
	private @Nullable MultiLineTextWidget selectedPackDescriptionWidget = null;

	private static final int ICON_SIZE = 32;
	private static final int GAP = 2;
	private static final int MAX_WIDTH = 160;
	private static final int MAX_TEXT_WIDTH = MAX_WIDTH - ICON_SIZE - GAP * 2;
	private static final int MAX_DESCRIPTION_ROWS = 2;

	public ResourcePack(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("resource_pack", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.resource_pack.config.enable");

		ConfigRegistry.register(getID(), "showDescription", showDescription);

		showDescription.setOnChange(this::updateTitleWidgetY);

		setHeight(ICON_SIZE);
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender() || lastSelectedPack == null) {
			return;
		}

		Font font = MINECRAFT.font;
		if (!lastSelectedPack.getId().equals(selectedPackId)) {
			// id
			selectedPackId = lastSelectedPack.getId();
			// icon
			selectedPackIcon = loadPackIcon(lastSelectedPack);
			// title
			selectedPackTitleWidget = new StringWidget(lastSelectedPack.getTitle(), font);
			selectedPackTitleWidget.setX(ICON_SIZE + GAP);
			updateTitleWidgetY(showDescription.getValue());
			selectedPackTitleWidget.setWidth(MAX_TEXT_WIDTH);
			selectedPackTitleWidget.alignLeft();
			// description
			selectedPackDescriptionWidget = new MultiLineTextWidget(ICON_SIZE + GAP, font.lineHeight + GAP * 2, lastSelectedPack.getDescription(), font);
			selectedPackDescriptionWidget.setMaxWidth(MAX_TEXT_WIDTH);
			selectedPackDescriptionWidget.setMaxRows(MAX_DESCRIPTION_ROWS);

			setWidth(ICON_SIZE + GAP + Math.min(
					Math.max(
							selectedPackTitleWidget.getWidth(),
							selectedPackDescriptionWidget.getWidth()
					),
					MAX_TEXT_WIDTH
			));
		}

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		if (selectedPackIcon != null) {
			graphics.blit(
					RenderPipelines.GUI_TEXTURED, selectedPackIcon,
					0, 0, 0, 0,
					ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE
			);
		}
		if (selectedPackTitleWidget != null) {
			MutableComponent component = selectedPackTitleWidget.getMessage().copy()
					.withColor(getColor());
			if (!shadow.getValue()) component.setStyle(component.getStyle().withShadowColor(0));
			selectedPackTitleWidget.setMessage(component);

			selectedPackTitleWidget.render(graphics, 0, 0, deltaTracker.getGameTimeDeltaTicks());
		}
		if (showDescription.getValue() && selectedPackDescriptionWidget != null) {
			MutableComponent component = selectedPackDescriptionWidget.getMessage().copy()
					.withColor(ARGB.setBrightness(getColor(), 0.8f));
			if (!shadow.getValue()) component.setStyle(component.getStyle().withShadowColor(0));
			selectedPackDescriptionWidget.setMessage(component);

			selectedPackDescriptionWidget.render(graphics, 0, 0, deltaTracker.getGameTimeDeltaTicks());
		}

		matrices.popMatrix();
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.resource_pack");
	}

	private ResourceLocation loadPackIcon(Pack pack) {
		try (PackResources packResources = pack.open()) {
			IoSupplier<InputStream> resource = packResources.getRootResource("pack.png");
			if (resource == null) {
				return PackSelectionScreen.DEFAULT_ICON;
			}

			String id = pack.getId();
			ResourceLocation location = ResourceLocation.withDefaultNamespace(
					"pack/" + Util.sanitizeName(id, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(id) + "/icon"
			);

			try (InputStream stream = resource.get()) {
				NativeImage iconImage = NativeImage.read(stream);
				MINECRAFT.getTextureManager().register(location, new DynamicTexture(location::toString, iconImage));
				return location;
			}
		} catch (Exception e) {
			FlexHudLogger.warn("Failed to load icon from pack {}", pack.getId(), e);
			return PackSelectionScreen.DEFAULT_ICON;
		}
	}

	private void updateTitleWidgetY(boolean showDescription) {
		if (selectedPackTitleWidget != null) {
			selectedPackTitleWidget.setY(showDescription ? GAP : (ICON_SIZE - MINECRAFT.font.lineHeight) / 2);
		}
	}

	@Override
	public List<String> getKeywords() {
		List<String> keywords = super.getKeywords();
		keywords.add("texture pack");
		return keywords;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
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
								.setVariable(showDescription)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}

	@Override
	public void tick() {
		List<Pack> selectedPacks = MINECRAFT.getResourcePackRepository().getSelectedPacks()
				.stream()
				.filter(pack -> !pack.isRequired() || pack.getId().equals("vanilla"))
				.toList();
		if (selectedPacks.isEmpty()) {
			lastSelectedPack = null;
		} else {
			lastSelectedPack = selectedPacks.getLast();
		}
	}
}
