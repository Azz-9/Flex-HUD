package me.Azz_9.better_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.better_hud.client.utils.FaviconUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import java.util.LinkedList;
import java.util.List;

public class ServerAddress extends AbstractHudElement {
	private ConfigBoolean hideWhenOffline = new ConfigBoolean(true, "better_hud.server_address.config.hide_when_offline");
	private ConfigBoolean showServerIcon = new ConfigBoolean(true, "better_hud.server_address.config.show_server_icon");
	public static List<Long> times = new LinkedList<>();

	public ServerAddress(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public void init() {
		this.enabled.setConfigTextTranslationKey("better_hud.server_address.config.enable");
	}

	@Override
	public String getID() {
		return "server_address";
	}

	@Override
	public Text getName() {
		return Text.translatable("better_hud.server_address");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		long a = System.nanoTime();

		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender()) {
			return;
		}

		String text = "";

		if (client.getCurrentServerEntry() != null) {

			text = client.getCurrentServerEntry().address;

		} else if (!this.hideWhenOffline.getValue()) {

			text = Text.translatable("better_hud.server_address.hud.offline").getString();

		} else if (Better_hudClient.isInMoveElementScreen) {

			text = "play.hypixel.net";

		}

		if (!text.isEmpty()) {

			this.width = 0;
			this.height = 0;

			int textX = 0;
			int textY = 0;
			int faviconSize = 14;
			Identifier icon = null;
			if (showServerIcon.getValue() && client.getCurrentServerEntry() != null) {
				icon = FaviconUtils.getCurrentServerFavicon();
				if (icon == null) {
					icon = Identifier.of("minecraft", "textures/misc/unknown_server.png");
				}

				textX = faviconSize + 2;
				textY = (faviconSize - client.textRenderer.fontHeight) / 2;
				this.height = faviconSize;
				setWidth(text);
				this.width += textX;
			}

			Matrix3x2fStack matrices = context.getMatrices();
			matrices.pushMatrix();
			matrices.translate(getRoundedX(), getRoundedY());
			matrices.scale(this.scale, this.scale);

			drawBackground(context);

			context.drawTexture(RenderPipelines.GUI_TEXTURED, icon, 0, 0, 0, 0, faviconSize, faviconSize, faviconSize, faviconSize);
			context.drawText(client.textRenderer, text, textX, textY, getColor(), this.shadow.getValue());

			matrices.popMatrix();
		}

		long b = System.nanoTime();
		times.add(b - a);
		if (times.size() > 1000) {
			times.removeFirst();
		}
	}

	@Override
	protected boolean shouldNotRender() {
		return super.shouldNotRender() || (this.hideWhenOffline.getValue() && MinecraftClient.getInstance().getCurrentServerEntry() == null);
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
