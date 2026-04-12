package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;
import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.utils.FaviconUtils;

public class ServerAddress extends AbstractTextModule {
	private final ConfigBoolean hideWhenOffline = new ConfigBoolean(true, "flex_hud.server_address.config.hide_when_offline");
	private final ConfigBoolean showServerIcon = new ConfigBoolean(true, "flex_hud.server_address.config.show_server_icon");

	public ServerAddress(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.server_address.config.enable");

		ConfigRegistry.register(getID(), "hideWhenOffline", hideWhenOffline);
		ConfigRegistry.register(getID(), "showServerIcon", showServerIcon);
	}

	@Override
	public String getID() {
		return "server_address";
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.server_address");
	}

	@Override
	public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		if (shouldNotRender()) {
			return;
		}

		String text = "";

		if (Flex_hudClient.isInMoveElementScreen) {

			text = "play.hypixel.net";

		} else {
			if (MINECRAFT.getCurrentServer() != null) {

				text = MINECRAFT.getCurrentServer().ip;

			} else if (!this.hideWhenOffline.getValue()) {

				text = Component.translatable("flex_hud.server_address.hud.offline").getString();

			}
		}

		if (!text.isEmpty()) {

			setWidth(0);
			setHeight(0);

			int textX = 0;
			int textY = 0;
			int faviconSize = 14;
			Identifier icon = null;
			if (showServerIcon.getValue() && (MINECRAFT.getCurrentServer() != null || Flex_hudClient.isInMoveElementScreen)) {
				if (Flex_hudClient.isInMoveElementScreen) {
					icon = Identifier.fromNamespaceAndPath(MOD_ID, "misc/hypixel-logo.png");
				} else {
					icon = FaviconUtils.getCurrentServerFavicon();
					if (icon == null) {
						icon = Identifier.fromNamespaceAndPath("minecraft", "textures/misc/unknown_server.png");
					}
				}

				textX = faviconSize + 2;
				textY = (faviconSize - MINECRAFT.font.lineHeight) / 2;
				setHeight(faviconSize);
				setWidth(text);
				setWidth(getWidth() + textX);
			}

			Matrix3x2fStack matrices = graphics.pose();
			matrices.pushMatrix();
			matrices.translate(getRoundedX(), getRoundedY());
			matrices.scale(getScale());

			drawBackground(graphics);

			if (icon != null) {
				graphics.blit(RenderPipelines.GUI_TEXTURED, icon, 0, 0, 0, 0, faviconSize, faviconSize, faviconSize, faviconSize);
			}
			graphics.text(MINECRAFT.font, text, textX, textY, getColor(), this.shadow.getValue());

			matrices.popMatrix();
		}
	}

	@Override
	public boolean shouldNotRender() {
		return super.shouldNotRender() || (this.hideWhenOffline.getValue() && MINECRAFT.getCurrentServer() == null && !Flex_hudClient.isInMoveElementScreen);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 225;
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
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideWhenOffline)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showServerIcon)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
