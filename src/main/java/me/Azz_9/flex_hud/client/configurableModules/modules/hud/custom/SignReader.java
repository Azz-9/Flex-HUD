package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.tickables.RaycastTickable;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SignReader extends AbstractMovableModule implements TickableModule {

	@NotNull
	private RenderData renderData = new RenderData();

	public SignReader(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
		this.enabled.setConfigTextTranslationKey("flex_hud.sign_reader.config.enable");
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.sign_reader");
	}

	@Override
	public String getID() {
		return "sign_reader";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender()) {
			return;
		}

		if (Flex_hudClient.isInMoveElementScreen) {
			renderSign(context, getPlaceholderRenderData());
		} else if (this.renderData.texture != null) {
			renderSign(context, this.renderData);
		}
	}

	private void renderSign(@NotNull DrawContext context, @NotNull RenderData data) {
		if (data.texture == null) return;

		float textureScale; // used to make the texture bigger by default
		if (data.isHangingSign) {
			textureScale = 4.5f;
			setWidth(Math.round(14 * textureScale));
			setHeight(Math.round(10 * textureScale));
		} else {
			textureScale = 4;
			setWidth(Math.round(24 * textureScale));
			setHeight(Math.round(12 * textureScale));
		}

		int textureWidth = Math.round(64 * textureScale);
		int textureHeight = Math.round(32 * textureScale);

		float offsetX = 2 * textureScale;
		if (!data.playerFacingFront) {
			offsetX += getWidth() + 2 * textureScale;
		}
		float offsetY = data.isHangingSign ? 14 * textureScale : 2 * textureScale;

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1.0f);

		// only draw the side of the sign texture
		context.drawTexture(RenderLayer::getGuiTextured, data.texture, 0, 0,
				offsetX, offsetY,
				getWidth(), getHeight(),
				textureWidth, textureHeight,
				0xffffffff);

		renderSignText(context, data);

		matrices.pop();
	}

	private void renderSignText(@NotNull DrawContext context, @NotNull RenderData data) {
		if (data.texture == null) return;

		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

		for (int i = 0; i < 4; i++) {
			if (i >= data.content.length) continue;
			Text line = data.content[i];
			int x = (getWidth() - textRenderer.getWidth(line)) / 2;
			int y = data.isHangingSign ? 5 + 9 * i : 4 + 10 * i;

			// render glow
			if (data.isGlowing) {
				MutableText glowLine = deepCopyText(line);

				glowLine.setStyle(Style.EMPTY.withItalic(glowLine.getStyle().isItalic()).withBold(glowLine.getStyle().isBold()));

				for (Text sibling : glowLine.getSiblings()) {
					boolean isItalic = sibling.getStyle().isItalic();
					boolean isBold = sibling.getStyle().isBold();
					((MutableText) sibling).setStyle(Style.EMPTY.withItalic(isItalic).withBold(isBold));
				}

				for (int dx = -1; dx <= 1; dx++) {
					for (int dy = -1; dy <= 1; dy++) {
						if (dx == 0 && dy == 0) continue;
						context.drawText(textRenderer, glowLine, x + dx, y + dy, data.glowColor, false);
					}
				}
			}

			context.drawText(textRenderer, line, x, y, data.textColor, false);
		}
	}

	private static @NotNull MutableText deepCopyText(@NotNull Text original) {
		MutableText copy = original.copyContentOnly();
		copy.setStyle(original.getStyle());
		for (Text sibling : original.getSiblings()) {
			copy.append(deepCopyText(sibling));
		}
		return copy;
	}

	@Override
	public boolean shouldNotRender() {
		return super.shouldNotRender() ||
				!Flex_hudClient.isInMoveElementScreen && (
						MinecraftClient.getInstance().getCameraEntity() == null ||
								MinecraftClient.getInstance().world == null ||
								MinecraftClient.getInstance().player == null
				);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 190;
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
								.setVariable(hideInF3)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}

	@Override
	public void tick() {
		renderData = getSignRenderData();

		if (renderData.texture != null) {
			renderData.texture = renderData.texture.withPrefixedPath("textures/").withSuffixedPath(".png");
		}
	}

	private @NotNull RenderData getPlaceholderRenderData() {
		RenderData data = new RenderData();
		data.texture = TexturedRenderLayers.getSignTextureId(WoodType.OAK).getTextureId().withPrefixedPath("textures/").withSuffixedPath(".png");
		data.content = new Text[]{
				Text.of(""),
				Text.translatable("flex_hud.sign_reader.placeholder_content"),
				Text.of(""),
				Text.of("")
		};
		data.textColor = DyeColor.BLACK.getSignColor();
		data.isHangingSign = false;
		return data;
	}

	private @NotNull RenderData getSignRenderData() {
		RenderData data = new RenderData();

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.world == null || client.getCameraEntity() == null) {
			return data;
		}

		PlayerEntity player = client.player;
		World world = client.world;

		HitResult hitResult = RaycastTickable.getHitResult();

		if (!(hitResult instanceof BlockHitResult blockHitResult)) return data;

		BlockPos pos = blockHitResult.getBlockPos();
		BlockEntity blockEntity = world.getBlockEntity(pos);
		Block block = world.getBlockState(pos).getBlock();

		SignBlockEntity signEntity = null;
		WoodType woodType = null;


		if (block instanceof SignBlock signBlock && blockEntity instanceof SignBlockEntity signBlockEntity) {
			signEntity = signBlockEntity;
			woodType = signBlock.getWoodType();
			data.isHangingSign = false;

		} else if (block instanceof WallSignBlock signBlock && blockEntity instanceof SignBlockEntity signBlockEntity) {
			signEntity = signBlockEntity;
			woodType = signBlock.getWoodType();
			data.isHangingSign = false;

		} else if (block instanceof HangingSignBlock hangingSignBlock && blockEntity instanceof HangingSignBlockEntity hangingSignBlockEntity) {
			signEntity = hangingSignBlockEntity;
			woodType = hangingSignBlock.getWoodType();
			data.isHangingSign = true;

		} else if (block instanceof WallHangingSignBlock hangingSignBlock && blockEntity instanceof HangingSignBlockEntity hangingSignBlockEntity) {
			signEntity = hangingSignBlockEntity;
			woodType = hangingSignBlock.getWoodType();
			data.isHangingSign = true;

		}

		if (signEntity == null || woodType == null) return data;

		data.playerFacingFront = signEntity.isPlayerFacingFront(player);
		SignText signText = signEntity.getText(data.playerFacingFront);

		data.content = signText.getMessages(false);
		data.textColor = signText.getColor().getSignColor();
		data.glowColor = AbstractSignBlockEntityRenderer.getTextColor(signText);
		data.isGlowing = signText.isGlowing();
		data.texture = (data.isHangingSign
				? TexturedRenderLayers.getHangingSignTextureId(woodType)
				: TexturedRenderLayers.getSignTextureId(woodType)).getTextureId();

		return data;
	}

	private static class RenderData {
		@Nullable
		Identifier texture = null;
		boolean playerFacingFront;
		@NotNull
		Text[] content = new Text[0];
		int textColor = DyeColor.BLACK.getSignColor();
		boolean isGlowing;
		int glowColor = AbstractSignBlockEntityRenderer.getTextColor(
				new SignText(null, null, DyeColor.BLACK, true)
		);
		boolean isHangingSign;
	}
}
