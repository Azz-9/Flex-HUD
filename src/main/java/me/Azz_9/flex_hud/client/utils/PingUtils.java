package me.Azz_9.flex_hud.client.utils;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.util.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.*;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;

public class PingUtils {
	private static final @NotNull ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
	private static final int PERIOD = 1000; // ms
	private static @Nullable ScheduledFuture<?> pingFuture;
	public static @Nullable PacketSender packetSender;
	private static final Deque<Long> pings = new ArrayDeque<>();
	private static final int maxSize = 20;
	private static long sum = 0;

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

	public static long getPing() {
		if (pings.isEmpty()) {
			return 0;
		}
		return sum / pings.size();
	}
}
