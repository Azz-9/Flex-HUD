package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

public class Ping extends AbstractTextModule {
	private final ConfigBoolean hideWhenOffline = new ConfigBoolean(true, "flex_hud.ping.config.hide_when_offline");

	public Ping(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.ping.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);

		ConfigRegistry.register(getID(), "hideWhenOffline", hideWhenOffline);
	}

	@Override
	public void init() {
		setHeight(Minecraft.getInstance().font.lineHeight);
	}

	@Override
	public String getID() {
		return "ping";
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.ping");
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();

		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && minecraft.player == null) {
			return;
		}

		String text = "";

		if (Flex_hudClient.isInMoveElementScreen) {

			text = "20 ms";

		} else {
			if (minecraft.getCurrentServer() != null) {

				if (minecraft.getConnection() != null) {
					PlayerInfo entry = minecraft.getConnection().getPlayerInfo(minecraft.player.getUUID());

					if (entry != null) {
						int latency = entry.getLatency();

						text = latency + " ms";
					}
				}

			} else if (!this.hideWhenOffline.getValue()) {

				text = Component.translatable("flex_hud.ping.hud.offline").getString();

			}
		}

		if (!text.isEmpty()) {

			setWidth(text);

			Matrix3x2fStack matrices = graphics.pose();
			matrices.pushMatrix();
			matrices.translate(getRoundedX(), getRoundedY());
			matrices.scale(getScale());

			drawBackground(graphics);

			graphics.drawString(minecraft.font, text, 0, 0, getColor(), this.shadow.getValue());

			matrices.popMatrix();
		}
	}

	@Override
	public boolean shouldNotRender() {
		return super.shouldNotRender() || (this.hideWhenOffline.getValue() && Minecraft.getInstance().getCurrentServer() == null && !Flex_hudClient.isInMoveElementScreen);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (Minecraft.getInstance().getLanguageManager().getSelected().equals("fr_fr")) {
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
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideWhenOffline)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
