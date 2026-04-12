package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.*;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;

public class Ping extends AbstractTextModule {
	private final ConfigBoolean hideWhenOffline = new ConfigBoolean(true, "flex_hud.ping.config.hide_when_offline");

	private final static @NotNull ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
	private final static int PERIOD = 1000; // ms
	private static @Nullable ScheduledFuture<?> pingFuture;
	public static @Nullable PacketSender packetSender;
	private final static Deque<Long> pings = new ArrayDeque<>();
	private final static int maxSize = 20;
	private static long sum = 0;

	public Ping(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.ping.config.enable");

		ConfigRegistry.register(getID(), "hideWhenOffline", hideWhenOffline);
	}

	public static void startPinging() {
		pingFuture = SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
			if (ModulesHelper.getInstance().ping.isEnabled() && packetSender != null) {
				packetSender.sendPacket(new QueryPingC2SPacket(Util.getMeasuringTimeMs()));
			}
		}, 0, PERIOD, TimeUnit.MILLISECONDS);
	}

	public static void stopPinging() {
		if (pingFuture != null && pingFuture.state().equals(Future.State.RUNNING)) {
			pingFuture.cancel(true);
		}
		packetSender = null;
		pings.clear();
		sum = 0;
	}

	public static void addPingValue(long ping) {
		pings.addLast(ping);
		sum += ping;

		if (pings.size() > maxSize) {
			sum -= pings.removeFirst();
		}
	}

	@Override
	public void init() {
		setHeight(CLIENT.textRenderer.fontHeight);
	}

	@Override
	public String getID() {
		return "ping";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.ping");
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {

		if (shouldNotRender() || !Flex_hudClient.isInMoveElementScreen && CLIENT.player == null) {
			return;
		}

		String text = "";

		if (Flex_hudClient.isInMoveElementScreen) {

			text = "20 ms";

		} else {
			if (CLIENT.getCurrentServerEntry() != null) {

				long ping = pings.isEmpty() ? 0 : sum / pings.size();
				text = ping + " ms";

			} else if (!this.hideWhenOffline.getValue()) {

				text = Text.translatable("flex_hud.ping.hud.offline").getString();

			}
		}

		if (!text.isEmpty()) {

			setWidth(text);

			MatrixStack matrices = context.getMatrices();
			matrices.push();
			matrices.translate(getRoundedX(), getRoundedY(), 0);
			matrices.scale(getScale(), getScale(), 1.0f);

			drawBackground(context);

			context.drawText(CLIENT.textRenderer, text, 0, 0, getColor(), this.shadow.getValue());

			matrices.pop();
		}
	}

	@Override
	public boolean shouldNotRender() {
		return super.shouldNotRender() || (this.hideWhenOffline.getValue() && CLIENT.getCurrentServerEntry() == null && !Flex_hudClient.isInMoveElementScreen);
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (CLIENT.getLanguageManager().getLanguage().equals("fr_fr")) {
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
