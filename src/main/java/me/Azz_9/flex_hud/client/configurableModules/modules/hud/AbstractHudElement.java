package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigDouble;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigFloat;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractHudElement extends AbstractModule implements MovableModule, HudElement {

	public ConfigDouble offsetX, offsetY;
	@NotNull
	public ConfigEnum<AnchorPosition> anchorX, anchorY;
	public ConfigFloat scale;

	public ConfigBoolean hideInF3 = new ConfigBoolean(true, "flex_hud.global.config.hide_in_f3");

	private int height, width;

	public AbstractHudElement(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super();
		this.offsetX = new ConfigDouble(defaultOffsetX);
		this.offsetY = new ConfigDouble(defaultOffsetY);
		this.anchorX = new ConfigEnum<>(AnchorPosition.class, defaultAnchorX);
		this.anchorY = new ConfigEnum<>(AnchorPosition.class, defaultAnchorY);
		this.scale = new ConfigFloat(1.0f);

		ConfigRegistry.register(getID(), "offsetX", offsetX);
		ConfigRegistry.register(getID(), "offsetY", offsetY);
		ConfigRegistry.register(getID(), "anchorX", anchorX);
		ConfigRegistry.register(getID(), "anchorY", anchorY);
		ConfigRegistry.register(getID(), "scale", scale);
		ConfigRegistry.register(getID(), "hideInF3", hideInF3);
	}

	@Override
	public boolean shouldNotRender() {
		return !ModulesHelper.getInstance().isEnabled.getValue() || !this.enabled.getValue() || (!Flex_hudClient.isInMoveElementScreen && this.hideInF3.getValue() && Minecraft.getInstance().debugEntries.isOverlayVisible());
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public double getOffsetX() {
		return offsetX.getValue();
	}

	@Override
	public double getOffsetY() {
		return offsetY.getValue();
	}

	@Override
	public @NotNull AnchorPosition getAnchorX() {
		return anchorX.getValue();
	}

	@Override
	public @NotNull AnchorPosition getAnchorY() {
		return anchorY.getValue();
	}

	@Override
	public void setPos(double offsetX, double offsetY, AnchorPosition anchorX, AnchorPosition anchorY) {
		this.offsetX.setValue(offsetX);
		this.offsetY.setValue(offsetY);
		this.anchorX.setValue(anchorX);
		this.anchorY.setValue(anchorY);
	}

	@Override
	public float getScale() {
		return scale.getValue();
	}

	@Override
	public void setScale(float scale) {
		this.scale.setValue(scale);
	}

	public enum AnchorPosition {
		START,
		CENTER,
		END
	}
}
