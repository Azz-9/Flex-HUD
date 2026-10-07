package me.Azz_9.flex_hud.utils;

import static me.Azz_9.flex_hud.Constants.MOD_ID;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.util.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import me.Azz_9.flex_hud.client.modules.Modules;

public class PingUtils {
	private static final @NotNull ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(
			runnable -> {
				Thread thread = new Thread(runnable, MOD_ID + "-Ping");
				thread.setDaemon(true);
				return thread;
			});
	private static final int PERIOD_MS = 1000;
	private static @Nullable ScheduledFuture<?> pingFuture;
	public static @Nullable ClientPacketListener connection;
	private static final Deque<Long> pings = new ArrayDeque<>();
	private static final int maxSize = 20;
	private static long sum = 0;

	public synchronized static void startPinging() {
		pingFuture = SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
			if (Modules.getInstance().ping.isEnabled() && connection != null) {
				connection.send(new ServerboundPingRequestPacket(Util.getMillis()));
			}
		}, 0, PERIOD_MS, TimeUnit.MILLISECONDS);
	}

	public synchronized static void stopPinging() {
		if (pingFuture != null) {
			pingFuture.cancel(false);
		}
		connection = null;
		pings.clear();
		sum = 0;
	}

	public synchronized static void addPingValue(long ping) {
		pings.addLast(ping);
		sum += ping;

		if (pings.size() > maxSize) {
			sum -= pings.removeFirst();
		}
	}

	public synchronized static long getPing() {
		if (pings.isEmpty()) {
			return 0;
		}
		return sum / pings.size();
	}

	public static void shutdown() {
		SCHEDULED_EXECUTOR_SERVICE.shutdown();
	}
}
