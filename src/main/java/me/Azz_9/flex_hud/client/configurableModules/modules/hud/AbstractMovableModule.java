package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;

public abstract class AbstractMovableModule extends AbstractModule implements HudElement {

	private final List<DimensionHud> dimensionHudList = new ArrayList<>();

	public ConfigBoolean hideInF3 = new ConfigBoolean(true, "flex_hud.global.config.hide_in_f3");
	protected final @NotNull ConfigEnum<AnchorMode> anchorModeX = new ConfigEnum<>(AbstractMovableModule.AnchorMode.class, AbstractMovableModule.AnchorMode.AUTO, "flex_hud.global.config.anchor_mode_x");
	protected final @NotNull ConfigEnum<AnchorMode> anchorModeY = new ConfigEnum<>(AbstractMovableModule.AnchorMode.class, AbstractMovableModule.AnchorMode.AUTO, "flex_hud.global.config.anchor_mode_y");

	public AbstractMovableModule(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super();
		dimensionHudList.add(new DimensionHud(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY));

		DimensionHud.register(getID(), dimensionHudList);

		ConfigRegistry.register(getID(), "hideInF3", hideInF3);
		ConfigRegistry.register(getID(), "anchorModeX", anchorModeX);
		ConfigRegistry.register(getID(), "anchorModeY", anchorModeY);
	}

	@Override
	public boolean shouldNotRender() {
		return !ModulesHelper.getInstance().isEnabled.getValue() || !this.enabled.getValue() || (!Flex_hudClient.isInMoveElementScreen && this.hideInF3.getValue() && CLIENT.debugHudEntryList.isF3Enabled());
	}

	public int getHeight() {
		return this.getHeight(0);
	}

	public int getHeight(int index) {
		return this.dimensionHudList.get(index).getHeight();
	}

	public void setHeight(int height) {
		this.setHeight(0, height);
	}

	public void setHeight(int index, int height) {
		this.dimensionHudList.get(index).setHeight(height);
	}

	public int getWidth() {
		return this.getWidth(0);
	}

	public int getWidth(int index) {
		return this.dimensionHudList.get(index).getWidth();
	}

	public void setWidth(int width) {
		this.setWidth(0, width);
	}

	public void setWidth(int index, int width) {
		this.dimensionHudList.get(index).setWidth(width);
	}

	public double getOffsetX() {
		return this.getOffsetX(0);
	}

	public double getOffsetX(int index) {
		return this.dimensionHudList.get(index).getOffsetX();
	}

	public double getOffsetY() {
		return this.getOffsetY(0);
	}

	public double getOffsetY(int index) {
		return this.dimensionHudList.get(index).getOffsetY();
	}

	public int getRoundedX() {
		return this.getRoundedX(0);
	}

	public int getRoundedX(int index) {
		return this.dimensionHudList.get(index).getRoundedX();
	}

	public int getRoundedY() {
		return this.getRoundedY(0);
	}

	public int getRoundedY(int index) {
		return this.dimensionHudList.get(index).getRoundedY();
	}

	public @NotNull AnchorPosition getAnchorX() {
		return this.getAnchorX(0);
	}

	public @NotNull AnchorPosition getAnchorX(int index) {
		return this.dimensionHudList.get(index).getAnchorX();
	}

	public @NotNull AnchorPosition getAnchorY() {
		return this.getAnchorY(0);
	}

	public @NotNull AnchorPosition getAnchorY(int index) {
		return this.dimensionHudList.get(index).getAnchorY();
	}

	public @NotNull AnchorMode getAnchorModeX() {
		return anchorModeX.getValue();
	}

	public @NotNull AnchorMode getAnchorModeY() {
		return anchorModeY.getValue();
	}

	public void setAnchorModeX(AnchorMode newMode) {
		for (DimensionHud dim : dimensionHudList) {
			double currentX = dim.getX(); // position absolue actuelle
			int screenWidth = CLIENT.getWindow().getScaledWidth();

			AnchorPosition newAnchor = newMode.isFixed()
					? newMode.toAnchorPosition()
					: dim.resolveAutoAnchorX(currentX);

			double newOffsetX = switch (newAnchor) {
				case START -> currentX;
				case CENTER -> currentX - (screenWidth - dim.getScaledWidth()) / 2.0;
				case END -> currentX - screenWidth + dim.getScaledWidth();
			};

			dim.setPos(newOffsetX, dim.getOffsetY(), newAnchor, dim.getAnchorY());
		}
		anchorModeX.setValue(newMode);
	}

	public void setAnchorModeY(AnchorMode newMode) {
		for (DimensionHud dim : dimensionHudList) {
			double currentY = dim.getY();
			int screenHeight = CLIENT.getWindow().getScaledHeight();

			AnchorPosition newAnchor = newMode.isFixed()
					? newMode.toAnchorPosition()
					: dim.resolveAutoAnchorY(currentY);

			double newOffsetY = switch (newAnchor) {
				case START -> currentY;
				case CENTER -> currentY - (screenHeight - dim.getScaledHeight()) / 2.0;
				case END -> currentY - screenHeight + dim.getScaledHeight();
			};

			dim.setPos(dim.getOffsetX(), newOffsetY, dim.getAnchorX(), newAnchor);
		}
		anchorModeY.setValue(newMode);
	}

	public float getScale() {
		return this.getScale(0);
	}

	public float getScale(int index) {
		return this.dimensionHudList.get(index).getScale();
	}

	public void setScale(float scale) {
		this.setScale(0, scale);
	}

	public void setScale(int index, float scale) {
		this.dimensionHudList.get(index).setScale(scale);
	}

	public List<DimensionHud> getDimensionHudList() {
		return dimensionHudList;
	}

	public void setX(int index, double x) {
		DimensionHud dim = dimensionHudList.get(index);
		dim.setX(x, anchorModeX.getValue());
	}

	public void setY(int index, double y) {
		DimensionHud dim = dimensionHudList.get(index);
		dim.setY(y, anchorModeY.getValue());
	}

	public enum AnchorPosition {
		START,
		CENTER,
		END
	}

	public enum AnchorMode implements Translatable {
		AUTO("flex_hud.global.config.anchor_mode.auto"),
		START("flex_hud.global.config.anchor_mode.start"),
		CENTER("flex_hud.global.config.anchor_mode.center"),
		END("flex_hud.global.config.anchor_mode.end");

		private final String translationKey;

		AnchorMode(String translationKey) {
			this.translationKey = translationKey;
		}

		public boolean isFixed() {
			return this != AUTO;
		}

		/**
		 * Convertit en AnchorPosition effective (ne pas appeler sur AUTO)
		 */
		public AnchorPosition toAnchorPosition() {
			return switch (this) {
				case START -> AnchorPosition.START;
				case CENTER -> AnchorPosition.CENTER;
				case END -> AnchorPosition.END;
				default -> throw new IllegalStateException();
			};
		}

		@Override
		public String getTranslationKey() {
			return translationKey;
		}
	}
}
