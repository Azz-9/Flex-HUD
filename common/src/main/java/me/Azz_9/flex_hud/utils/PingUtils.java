package me.Azz_9.flex_hud.utils;

import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.*;

import me.Azz_9.flex_hud.client.modules.Modules;

public class PingUtils {
	private static final @NotNull ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
	private static final int PERIOD = 1000; // ms
	private static @Nullable ScheduledFuture<?> pingFuture;
	public static @Nullable ClientPacketListener connection;
	private static final Deque<Long> pings = new ArrayDeque<>();
	private static final int maxSize = 20;
	private static long sum = 0;

	public static void startPinging() {
		pingFuture = SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
			if (Modules.getInstance().ping.isEnabled() && connection != null) {
				connection.send(new ServerboundPingRequestPacket(Util.getMillis()));
			}
		}, 0, PERIOD, TimeUnit.MILLISECONDS);
	}

	public static void stopPinging() {
		if (pingFuture != null && pingFuture.state().equals(Future.State.RUNNING)) {
			pingFuture.cancel(true);
		}
		connection = null;
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
