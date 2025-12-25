package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.tickables.RaycastTickable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class Distance extends AbstractTextElement implements TickableModule {

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
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.distance");
	}

	@Override
	public String getID() {
		return "distance";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && MinecraftClient.getInstance().getCameraEntity() == null) {
			return;
		}

		setWidth(distanceText);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1.0f);

		drawBackground(context);

		context.drawText(MinecraftClient.getInstance().textRenderer, distanceText, 0, 0, getColor(), shadow.getValue());

		matrices.pop();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
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

		MinecraftClient client = MinecraftClient.getInstance();
		HitResult hitResult = RaycastTickable.getHitResult();

		if (client.getCameraEntity() == null || client.player == null || hitResult == null) return;

		if (hitResult.getType() == HitResult.Type.MISS) {
			distanceText = "[∞]";
		} else if (hitResult.getType() == HitResult.Type.BLOCK) {
			Vec3d lerpedPos = client.getCameraEntity().getLerpedPos(0);
			float eyeHeight = client.getCameraEntity().getEyeHeight(client.player.getPose());
			Vec3d eyePos = new Vec3d(lerpedPos.getX(), lerpedPos.getY() + eyeHeight, lerpedPos.getZ());

			double distance = hitResult.getPos().distanceTo(eyePos);

			// format distance to string with selected amount of digits
			String format = "[%." + digits.getValue() + "f]";
			distanceText = String.format(format, distance);
		}
	}
}