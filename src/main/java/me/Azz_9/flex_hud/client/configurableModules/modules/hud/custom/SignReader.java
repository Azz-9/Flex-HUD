package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
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

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.tickables.RaycastTickable;

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
	public Component getName() {
		return Component.translatable("flex_hud.sign_reader");
	}

	@Override
	public String getID() {
		return "sign_reader";
	}

	@Override
	public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender()) {
			return;
		}

		if (Flex_hudClient.isInMoveElementScreen) {
			renderSign(graphics, getPlaceholderRenderData());
		} else if (this.renderData.texture != null) {
			renderSign(graphics, this.renderData);
		}
	}

	private void renderSign(@NotNull GuiGraphicsExtractor graphics, @NotNull RenderData data) {
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

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale(), getScale());

		// only draw the side of the sign texture
		graphics.blit(RenderPipelines.GUI_TEXTURED, data.texture, 0, 0,
				offsetX, offsetY,
				getWidth(), getHeight(),
				textureWidth, textureHeight,
				0xffffffff);

		renderSignText(graphics, data);

		matrices.popMatrix();
	}

	private void renderSignText(@NotNull GuiGraphicsExtractor graphics, @NotNull RenderData data) {
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
						graphics.text(font, glowLine, x + dx, y + dy, data.glowColor, false);
					}
				}
			}

			graphics.text(font, line, x, y, data.textColor, false);
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
				!Flex_hudClient.isInMoveElementScreen && (
						MINECRAFT.getCameraEntity() == null ||
								MINECRAFT.level == null ||
								MINECRAFT.player == null
				);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
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
			renderData.texture = renderData.texture.withPrefix("textures/").withSuffix(".png");
		}
	}

	private @NotNull RenderData getPlaceholderRenderData() {
		RenderData data = new RenderData();
		data.texture = Sheets.getSignSprite(WoodType.OAK).texture().withPrefix("textures/").withSuffix(".png");
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
		data.texture = (data.isHangingSign
				? Sheets.getHangingSignSprite(woodType)
				: Sheets.getSignSprite(woodType)).texture();

		return data;
	}

	private static class RenderData {
		@Nullable
		Identifier texture = null;
		boolean playerFacingFront;
		@NotNull
		Component[] content = new Component[0];
		int textColor = DyeColor.BLACK.getTextColor();
		boolean isGlowing;
		int glowColor = AbstractSignRenderer.getDarkColor(
				new SignText(null, null, DyeColor.BLACK, true)
		);
		boolean isHangingSign;
	}
}
