package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable.MultiRenderable;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigDouble;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigFloat;
import me.Azz_9.flex_hud.client.utils.BoolBinding;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DimensionHud implements MovableModule {
	private final ConfigDouble offsetX, offsetY;
	@NotNull
	private final ConfigEnum<AbstractMovableModule.AnchorPosition> anchorX, anchorY;
	private final ConfigFloat scale;

	private int width, height;
	private BoolBinding enabledBinding;
	private boolean displayed;

	private final List<MultiRenderable> multiRenderables = new ArrayList<>();

	public DimensionHud(double defaultOffsetX, double defaultOffsetY, @NotNull AbstractMovableModule.AnchorPosition defaultAnchorX, @NotNull AbstractMovableModule.AnchorPosition defaultAnchorY) {
		this.offsetX = new ConfigDouble(defaultOffsetX);
		this.offsetY = new ConfigDouble(defaultOffsetY);
		this.anchorX = new ConfigEnum<>(AbstractMovableModule.AnchorPosition.class, defaultAnchorX);
		this.anchorY = new ConfigEnum<>(AbstractMovableModule.AnchorPosition.class, defaultAnchorY);
		this.scale = new ConfigFloat(1.0f);

		this.enabledBinding = () -> true;
		this.displayed = true;
	}

	public double getOffsetX() {
		return offsetX.getValue();
	}

	public double getOffsetY() {
		return offsetY.getValue();
	}

	public AbstractMovableModule.AnchorPosition getAnchorX() {
		return anchorX.getValue();
	}

	public AbstractMovableModule.AnchorPosition getAnchorY() {
		return anchorY.getValue();
	}

	@Override
	public void setPos(double offsetX, double offsetY, AbstractMovableModule.AnchorPosition anchorX, AbstractMovableModule.AnchorPosition anchorY) {
		this.offsetX.setValue(offsetX);
		this.offsetY.setValue(offsetY);
		this.anchorX.setValue(anchorX);
		this.anchorY.setValue(anchorY);
	}

	public float getScale() {
		return scale.getValue();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isEnabled() {
		return enabledBinding.get();
	}

	public void bindEnabled(BoolBinding binding) {
		this.enabledBinding = binding;
	}

	public void setOffsetX(double offsetX) {
		this.offsetX.setValue(offsetX);
	}

	public void setOffsetY(double offsetY) {
		this.offsetY.setValue(offsetY);
	}

	public void setAnchorX(AbstractMovableModule.AnchorPosition anchorX) {
		this.anchorX.setValue(anchorX);
	}

	public void setAnchorY(AbstractMovableModule.AnchorPosition anchorY) {
		this.anchorY.setValue(anchorY);
	}

	public void setScale(float scale) {
		this.scale.setValue(scale);
	}

	public boolean isDisplayed() {
		return displayed && enabledBinding.get();
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	public void clearMultiRenderables() {
		multiRenderables.clear();
	}

	public void addMultiRenderable(MultiRenderable multiRenderable) {
		this.multiRenderables.add(multiRenderable);
	}

	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (isDisplayed()) {
			for (MultiRenderable multiRenderable : multiRenderables) {
				multiRenderable.render(graphics, deltaTracker);
			}
		}
	}

	public List<MultiRenderable> getMultiRenderables() {
		return multiRenderables;
	}

	public static void register(String id, List<DimensionHud> dimensionHuds) {
		if (dimensionHuds.size() == 1) {
			DimensionHud dimensionHud = dimensionHuds.getFirst();
			ConfigRegistry.register(id, "offsetX", dimensionHud.offsetX);
			ConfigRegistry.register(id, "offsetY", dimensionHud.offsetY);
			ConfigRegistry.register(id, "anchorX", dimensionHud.anchorX);
			ConfigRegistry.register(id, "anchorY", dimensionHud.anchorY);
			ConfigRegistry.register(id, "scale", dimensionHud.scale);
		} else {
			for (int i = 0; i < dimensionHuds.size(); i++) {
				DimensionHud dimensionHud = dimensionHuds.get(i);
				ConfigRegistry.register(id, "offsetX-" + i, dimensionHud.offsetX);
				ConfigRegistry.register(id, "offsetY-" + i, dimensionHud.offsetY);
				ConfigRegistry.register(id, "anchorX-" + i, dimensionHud.anchorX);
				ConfigRegistry.register(id, "anchorY-" + i, dimensionHud.anchorY);
				ConfigRegistry.register(id, "scale-" + i, dimensionHud.scale);
			}
		}
	}
}
