package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.tickables.RaycastTickable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

public class Distance extends AbstractTextModule implements TickableModule {

	private final ConfigInteger digits = new ConfigInteger(0, "flex_hud.distance.config.number_of_digits", 0, 16);

	@NotNull
	private String distanceText = "";

	public Distance(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
		this.enabled.setConfigTextTranslationKey("flex_hud.distance.config.enable");

		ConfigRegistry.register(getID(), "digits", digits);
	}

	@Override
	public void init() {
		setHeight(Minecraft.getInstance().font.lineHeight);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.distance");
	}

	@Override
	public String getID() {
		return "distance";
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && Minecraft.getInstance().getCameraEntity() == null) {
			return;
		}

		setWidth(distanceText);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		graphics.drawString(Minecraft.getInstance().font, distanceText, 0, 0, getColor(), shadow.getValue());

		matrices.popMatrix();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (Minecraft.getInstance().getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 160;
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
						new IntFieldEntry.Builder()
								.setIntFieldWidth(20)
								.setVariable(digits)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}

	@Override
	public void tick() {
		if (Flex_hudClient.isInMoveElementScreen) {
			String format = "[%." + digits.getValue() + "f]";
			distanceText = String.format(format, 20f);
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		HitResult hitResult = RaycastTickable.getHitResult();

		if (minecraft.getCameraEntity() == null || minecraft.player == null || hitResult == null) return;

		if (hitResult.getType() == HitResult.Type.MISS) {
			distanceText = "[∞]";
		} else if (hitResult.getType() == HitResult.Type.BLOCK) {
			Vec3 lerpedPos = minecraft.getCameraEntity().getPosition(0);
			float eyeHeight = minecraft.getCameraEntity().getEyeHeight(minecraft.player.getPose());
			Vec3 eyePos = new Vec3(lerpedPos.x(), lerpedPos.y() + eyeHeight, lerpedPos.z());

			double distance = hitResult.getLocation().distanceTo(eyePos);

			// format distance to string with selected amount of digits
			String format = "[%." + digits.getValue() + "f]";
			distanceText = String.format(format, distance);
		}
	}
}