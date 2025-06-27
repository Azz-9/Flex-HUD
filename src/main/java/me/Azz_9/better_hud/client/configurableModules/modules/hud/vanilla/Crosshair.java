package me.Azz_9.better_hud.client.configurableModules.modules.hud.vanilla;

import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

public class Crosshair extends AbstractModule {
	public int size = 15;
	public float scale = 1.0f;

	private final int[][] pixels;

	public Crosshair() {
		this.enabled = false;

		pixels = new int[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if ((x == size / 2 || y == size / 2) && x >= 3 && x < size - 3 && y >= 3 && y < size - 3) {
					pixels[x][y] = 0xffffffff;
				} else {
					pixels[x][y] = 0;
				}
			}
		}
	}

	@Override
	public String getID() {
		return "crosshair";
	}

	@Override
	public Text getName() {
		return Text.translatable("better_hud.crosshair");
	}

	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (!JsonConfigHelper.getInstance().isEnabled || !this.enabled || client == null || client.player == null) {
			return;
		}

		int screenWidth = client.getWindow().getScaledWidth();
		int screenHeight = client.getWindow().getScaledHeight();
		double startX = screenWidth / 2.0 - (size / 2.0) * scale;
		double startY = screenHeight / 2.0 - (size / 2.0) * scale;

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate((float) startX, (float) startY);
		matrices.scale(scale, scale);

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (pixels[x][y] >> 24 != 0) {
					context.fill(x, y, x + 1, y + 1, pixels[x][y]);
				}
			}
		}

		matrices.popMatrix();


		/*final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_full");
		final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_background");
		final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_progress");

		GameOptions gameOptions = client.options;
		if (gameOptions.getPerspective().isFirstPerson() && client.interactionManager != null) {
			if (client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR || this.shouldRenderSpectatorCrosshair(client.crosshairTarget)) {
				if (!this.shouldRenderCrosshair()) {
					context.createNewRootLayer();
					context.fill(RenderPipelines.CROSSHAIR, TextureSetup.empty(), );
					context.drawGuiTexture(RenderPipelines.CROSSHAIR, CROSSHAIR_TEXTURE, (context.getScaledWindowWidth() - 15) / 2, (context.getScaledWindowHeight() - 15) / 2, 15, 15);
					if (client.options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR) {
						float attackCooldownProgress = client.player.getAttackCooldownProgress(0.0F);
						boolean renderFullAttackIndeicator = false;
						if (client.targetedEntity instanceof LivingEntity && attackCooldownProgress >= 1.0F) {
							renderFullAttackIndeicator = client.player.getAttackCooldownProgressPerTick() > 5.0F;
							renderFullAttackIndeicator &= client.targetedEntity.isAlive();
						}

						int y = context.getScaledWindowHeight() / 2 - 7 + 16;
						int x = context.getScaledWindowWidth() / 2 - 8;
						if (renderFullAttackIndeicator) {
							context.drawGuiTexture(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, x, y, 16, 16);
						} else if (attackCooldownProgress < 1.0F) {
							int l = (int) (attackCooldownProgress * 17.0F);
							context.drawGuiTexture(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, x, y, 16, 4);
							context.drawGuiTexture(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, x, y, l, 4);
						}
					}
				}
			}
		}
	}

	public boolean shouldRenderCrosshair() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.getDebugHud().shouldShowDebugHud() && client.options.getPerspective() == Perspective.FIRST_PERSON && client.player != null && !client.player.hasReducedDebugInfo() && !(Boolean) client.options.getReducedDebugInfo().getValue();
	}

	private boolean shouldRenderSpectatorCrosshair(@Nullable HitResult hitResult) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (hitResult == null || client.world == null) {
			return false;
		} else if (hitResult.getType() == HitResult.Type.ENTITY) {
			return ((EntityHitResult) hitResult).getEntity() instanceof NamedScreenHandlerFactory;
		} else if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
			World world = client.world;
			return world.getBlockState(blockPos).createScreenHandlerFactory(world, blockPos) != null;
		} else {
			return false;
		}*/
	}

	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(getName(), parent, parentScrollAmount) {
			@Override
			protected void init() {
				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(enabled)
								.setDefaultValue(false)
								.setOnToggle(toggled -> enabled = toggled)
								.setText(Text.translatable("better_hud.crosshair.config.enable"))
								.build()
				);
			}
		};
	}
}
