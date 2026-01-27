package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMovableModule extends AbstractModule implements HudElement {

	private final List<DimensionHud> dimensionHudList = new ArrayList<>();

	public ConfigBoolean hideInF3 = new ConfigBoolean(true, "flex_hud.global.config.hide_in_f3");

	public AbstractMovableModule(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super();
		dimensionHudList.add(new DimensionHud(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY));

		DimensionHud.register(getID(), dimensionHudList);

		ConfigRegistry.register(getID(), "hideInF3", hideInF3);
	}

	@Override
	public boolean shouldNotRender() {
		return !ModulesHelper.getInstance().isEnabled.getValue() || !this.enabled.getValue() || (!Flex_hudClient.isInMoveElementScreen && this.hideInF3.getValue() && MinecraftClient.getInstance().debugHudEntryList.isF3Enabled());
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

	public enum AnchorPosition {
		START,
		CENTER,
		END
	}
}
