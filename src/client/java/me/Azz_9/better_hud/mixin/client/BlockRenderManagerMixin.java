package me.Azz_9.better_hud.mixin.client;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderManager.class)
public class BlockRenderManagerMixin {

	@Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
	public void renderBlock(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, net.minecraft.util.math.random.Random random, CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.player == null) {
			return;
		}
		client.player.sendMessage(Text.of("Bonsoir"), true);
		// Vérifiez si ce bloc est sélectionné
		if (isTargetBlock(pos)) {
			//MinecraftClient client = MinecraftClient.getInstance();

			// Récupérer le modèle du bloc
			BlockRenderManager renderManager = client.getBlockRenderManager();
			BakedModel bakedModel = renderManager.getModel(state);

			// Créer un VertexConsumer pour la texture de surlignage
			VertexConsumer overlayConsumer = client.getBufferBuilders().getEntityVertexConsumers()
					.getBuffer(RenderLayer.getCutout());

			// Rendre le bloc avec la texture surlignée
			renderManager.getModelRenderer().render(
					matrices.peek(),
					overlayConsumer,
					state,
					bakedModel,
					1.0f, 0.0f, 0.0f, // Couleur rouge
					0xF000F0, // Lumière
					OverlayTexture.DEFAULT_UV // Overlay
			);

			// Annule le rendu normal
			ci.cancel();
		}
	}

	@Unique
	private boolean isTargetBlock(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.world == null || client.player == null) {
			return false;
		}
		BlockState state = client.world.getBlockState(pos);

		// Récupérer l'identifiant du bloc
		Identifier blockId = Registries.BLOCK.getId(state.getBlock());

		// Vérifier si l'identifiant est dans la liste
		return ModConfig.getInstance().selectedBlocks.contains(blockId.toString());
	}
}
