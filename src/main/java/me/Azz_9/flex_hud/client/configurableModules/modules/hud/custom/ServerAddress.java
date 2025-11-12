package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextElement;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.utils.FaviconUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class ServerAddress extends AbstractTextElement {
	private ConfigBoolean hideWhenOffline = new ConfigBoolean(true, "flex_hud.server_address.config.hide_when_offline");
	private ConfigBoolean showServerIcon = new ConfigBoolean(true, "flex_hud.server_address.config.show_server_icon");

	public ServerAddress(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.server_address.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);

		ConfigRegistry.register(getID(), "hideWhenOffline", hideWhenOffline);
		ConfigRegistry.register(getID(), "showServerIcon", showServerIcon);
	}

	@Override
	public String getID() {
		return "server_address";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.server_address");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender()) {
			return;
		}

		String text = "";

		if (Flex_hudClient.isInMoveElementScreen) {

			text = "play.hypixel.net";

		} else {
			if (client.getCurrentServerEntry() != null) {

				text = client.getCurrentServerEntry().address;

			} else if (!this.hideWhenOffline.getValue()) {

				text = Text.translatable("flex_hud.server_address.hud.offline").getString();

			}
		}

		if (!text.isEmpty()) {

			this.width = 0;
			this.height = 0;

			int textX = 0;
			int textY = 0;
			int faviconSize = 14;
			Identifier icon = null;
			if (showServerIcon.getValue() && (client.getCurrentServerEntry() != null || Flex_hudClient.isInMoveElementScreen)) {
				if (Flex_hudClient.isInMoveElementScreen) {
					icon = Identifier.of(MOD_ID, "misc/hypixel-logo.png");
				} else {
					icon = FaviconUtils.getCurrentServerFavicon();
					if (icon == null) {
						icon = Identifier.of("minecraft", "textures/misc/unknown_server.png");
					}
				}

				textX = faviconSize + 2;
				textY = (faviconSize - client.textRenderer.fontHeight) / 2;
				this.height = faviconSize;
				setWidth(text);
				this.width += textX;
			}

			MatrixStack matrices = context.getMatrices();
			matrices.push();
			matrices.translate(getRoundedX(), getRoundedY(), 0);
			matrices.scale(getScale(), getScale(), 1.0f);

			drawBackground(context);

			if (icon != null) {
				context.drawTexture(RenderLayer::getGuiTextured, icon, 0, 0, 0, 0, faviconSize, faviconSize, faviconSize, faviconSize);
			}
			context.drawText(client.textRenderer, text, textX, textY, getColor(), this.shadow.getValue());

			matrices.pop();
		}
	}

	@Override
	protected boolean shouldNotRender() {
		return super.shouldNotRender() || (this.hideWhenOffline.getValue() && MinecraftClient.getInstance().getCurrentServerEntry() == null && !Flex_hudClient.isInMoveElementScreen);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 225;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColor)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(color)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackground)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColor)
								.setDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideWhenOffline)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(showServerIcon)
								.build()
				);
			}
		};
	}
}
