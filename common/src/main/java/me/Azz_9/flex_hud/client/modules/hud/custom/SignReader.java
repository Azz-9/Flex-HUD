package me.Azz_9.flex_hud.client.modules.hud.custom;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.TickableModule;
import me.Azz_9.flex_hud.client.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.client.tickables.RaycastTickable;

public class SignReader extends AbstractMovableModule implements TickableModule {

	@NotNull
	private RenderData renderData = new RenderData();

	public SignReader(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super("sign_reader", defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.sign_reader.config.enable");
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.sign_reader");
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender()) {
			return;
		}

		if (CommonClass.isEditingLayout) {
			renderSign(graphics, getPlaceholderRenderData());
		} else if (this.renderData.texture != null) {
			renderSign(graphics, this.renderData);
		}
	}

	private void renderSign(@NotNull GuiGraphics graphics, @NotNull RenderData data) {
		if (data.texture == null) return;

		float textureScale; // used to make the texture bigger by default
		int textureWidth, textureHeight;
		if (data.isHangingSign) {
			textureScale = 4.5f;
			setWidth(Math.round(14 * textureScale));
			setHeight(Math.round(10 * textureScale));
		} else {
			textureScale = 4;
			setWidth(Math.round(24 * textureScale));
			setHeight(Math.round(12 * textureScale));
		}

		textureWidth = Math.round(64 * textureScale);
		textureHeight = Math.round(32 * textureScale);

		float offsetX = 2 * textureScale;
		if (!data.playerFacingFront) {
			offsetX += getWidth() + 2 * textureScale;
		}
		float offsetY = data.isHangingSign ? 14 * textureScale : 2 * textureScale;

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale(), getScale());

		// only draw the side of the sign texture
		graphics.blit(RenderPipelines.GUI_TEXTURED, data.texture, 0, 0,
				offsetX, offsetY,
				getWidth(), getHeight(),
				textureWidth, textureHeight,
				Colors.WHITE);

		renderSignText(graphics, data);

		matrices.popMatrix();
	}

	private void renderSignText(@NotNull GuiGraphics graphics, @NotNull RenderData data) {
		if (data.texture == null) return;

		Font font = MINECRAFT.font;

		for (int i = 0; i < 4; i++) {
			if (i >= data.content.length) continue;
			Component line = data.content[i];
			int x = (getWidth() - font.width(line)) / 2;
			int y = data.isHangingSign ? 5 + 9 * i : 4 + 10 * i;

			// render glow
			if (data.isGlowing) {
				MutableComponent glowLine = deepCopyText(line);

				glowLine.setStyle(Style.EMPTY.withItalic(glowLine.getStyle().isItalic()).withBold(glowLine.getStyle().isBold()));

				for (Component sibling : glowLine.getSiblings()) {
					boolean isItalic = sibling.getStyle().isItalic();
					boolean isBold = sibling.getStyle().isBold();
					((MutableComponent) sibling).setStyle(Style.EMPTY.withItalic(isItalic).withBold(isBold));
				}

				for (int dx = -1; dx <= 1; dx++) {
					for (int dy = -1; dy <= 1; dy++) {
						if (dx == 0 && dy == 0) continue;
						graphics.drawString(font, glowLine, x + dx, y + dy, data.glowColor, false);
					}
				}
			}

			graphics.drawString(font, line, x, y, data.textColor, false);
		}
	}

	private static @NotNull MutableComponent deepCopyText(@NotNull Component original) {
		MutableComponent copy = original.copy();
		copy.setStyle(original.getStyle());
		for (Component sibling : original.getSiblings()) {
			copy.append(deepCopyText(sibling));
		}
		return copy;
	}

	@Override
	public boolean shouldNotRender() {
		return super.shouldNotRender() ||
				!CommonClass.isEditingLayout && (
						MINECRAFT.getCameraEntity() == null ||
								MINECRAFT.level == null ||
								MINECRAFT.player == null
				);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 190;
				}

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
								.build()
				);
			}
		};
	}

	@Override
	public void tick() {
		renderData = getSignRenderData();
	}

	private @NotNull RenderData getPlaceholderRenderData() {
		RenderData data = new RenderData();

		data.texture = getSignTexture(WoodType.OAK);
		data.content = new Component[]{
				Component.literal(""),
				Component.translatable("flex_hud.sign_reader.placeholder_content"),
				Component.literal(""),
				Component.literal("")
		};
		data.textColor = DyeColor.BLACK.getTextColor();
		data.isHangingSign = false;
		return data;
	}

	private @NotNull RenderData getSignRenderData() {
		RenderData data = new RenderData();

		if (MINECRAFT.player == null || MINECRAFT.level == null || MINECRAFT.getCameraEntity() == null) {
			return data;
		}

		LocalPlayer player = MINECRAFT.player;
		Level world = MINECRAFT.level;

		HitResult hitResult = RaycastTickable.getHitResult();

		if (!(hitResult instanceof BlockHitResult blockHitResult)) return data;

		BlockPos pos = blockHitResult.getBlockPos();
		BlockEntity blockEntity = world.getBlockEntity(pos);
		Block block = world.getBlockState(pos).getBlock();

		SignBlockEntity signEntity = null;
		WoodType woodType = null;


		switch (block) {
			case StandingSignBlock signBlock when blockEntity instanceof SignBlockEntity signBlockEntity -> {
				signEntity = signBlockEntity;
				woodType = signBlock.type();
				data.isHangingSign = false;
			}
			case WallSignBlock signBlock when blockEntity instanceof SignBlockEntity signBlockEntity -> {
				signEntity = signBlockEntity;
				woodType = signBlock.type();
				data.isHangingSign = false;
			}
			case CeilingHangingSignBlock hangingSignBlock when blockEntity instanceof HangingSignBlockEntity hangingSignBlockEntity -> {
				signEntity = hangingSignBlockEntity;
				woodType = hangingSignBlock.type();
				data.isHangingSign = true;
			}
			case WallHangingSignBlock hangingSignBlock when blockEntity instanceof HangingSignBlockEntity hangingSignBlockEntity -> {
				signEntity = hangingSignBlockEntity;
				woodType = hangingSignBlock.type();
				data.isHangingSign = true;
			}
			default -> {
			}
		}

		if (signEntity == null) return data;

		data.playerFacingFront = signEntity.isFacingFrontText(player);
		SignText signText = signEntity.getText(data.playerFacingFront);

		data.content = signText.getMessages(false);
		data.textColor = signText.getColor().getTextColor();
		data.glowColor = AbstractSignRenderer.getDarkColor(signText);
		data.isGlowing = signText.hasGlowingText();
		data.texture = data.isHangingSign
				? getHangingSignTexture(woodType)
				: getSignTexture(woodType);

		return data;
	}

	public static ResourceLocation getSignTexture(@NotNull WoodType woodType) {
		return addPrefixAndSuffix(Sheets.getSignMaterial(woodType).texture());
	}

	public static ResourceLocation getHangingSignTexture(@NotNull WoodType woodType) {
		return addPrefixAndSuffix(Sheets.getHangingSignMaterial(woodType).texture());
	}

	private static ResourceLocation addPrefixAndSuffix(ResourceLocation location) {
		return location.withPrefix("textures/").withSuffix(".png");
	}

	private static class RenderData {
		@Nullable
		ResourceLocation texture = null;
		boolean playerFacingFront;
		@NotNull
		Component[] content = new Component[0];
		int textColor = DyeColor.BLACK.getTextColor();
		boolean isGlowing;
		int glowColor = AbstractSignRenderer.getDarkColor(
				new SignText(new Component[]{}, new Component[]{}, DyeColor.BLACK, true)
		);
		boolean isHangingSign;
	}
}
