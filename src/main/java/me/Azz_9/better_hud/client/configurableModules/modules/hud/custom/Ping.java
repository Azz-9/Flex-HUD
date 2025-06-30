package me.Azz_9.better_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

import java.util.LinkedList;
import java.util.List;

public class Ping extends AbstractHudElement {
	private ConfigBoolean hideWhenOffline = new ConfigBoolean(true, "better_hud.ping.config.hide_when_offline");
	public static List<Long> times = new LinkedList<>();

	public Ping(double defaultOffsetX, double defaultOffsetY, AnchorPosition defaultAnchorX, AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public void init() {
		this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
		this.enabled.setConfigTextTranslationKey("better_hud.ping.config.enable");
	}

	@Override
	public String getID() {
		return "ping";
	}

	@Override
	public Text getName() {
		return Text.translatable("better_hud.ping");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		long a = System.nanoTime();

		MinecraftClient client = MinecraftClient.getInstance();

		if (shouldNotRender() || client.player == null) {
			return;
		}

		String text = "";

		if (client.getCurrentServerEntry() != null) {

			if (client.getNetworkHandler() != null) {
				PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());

				if (entry != null) {
					int latency = entry.getLatency();

					text = latency + " ms";
				}
			}

		} else if (!this.hideWhenOffline.getValue()) {

			text = Text.translatable("better_hud.ping.hud.offline").getString();

		} else if (Better_hudClient.isInMoveElementScreen) {

			text = "20 ms";

		}

		if (!text.isEmpty()) {

			Matrix3x2fStack matrices = context.getMatrices();
			matrices.pushMatrix();
			matrices.translate(Math.round(getX()), Math.round(getY()));
			matrices.scale(this.scale, this.scale);

			drawBackground(context);

			context.drawText(client.textRenderer, text, 0, 0, getColor(), this.shadow.getValue());

			setWidth(text);

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
								.build()
				);
			}
		};
	}
}
