package me.Azz_9.flex_hud.platform;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.function.BiConsumer;

import me.Azz_9.flex_hud.platform.services.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {

	public static IEventBus eventBus;

	@Override
	public @NotNull String getPlatformName() {
		return "NeoForge";
	}

	@Override
	public boolean isModLoaded(@NotNull String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.getCurrent().isProduction();
	}

	@Override
	public @NotNull Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Override
	public void registerClientStartEvent(@NotNull Runnable runnable) {
		NeoForge.EVENT_BUS.addListener((ClientStartedEvent event) -> runnable.run());
	}

	@Override
	public void registerEndClientTickEvent(@NotNull Runnable runnable) {
		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> runnable.run());
	}

	@Override
	public void registerJoinEvent(@NotNull Runnable runnable) {
		NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> runnable.run());
	}

	@Override
	public void registerDisconnectEvent(@NotNull Runnable runnable) {
		NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> runnable.run());
	}

	@Override
	public void registerHudElement(
			@NotNull Identifier beforeThis,
			@NotNull Identifier identifier,
			@NotNull BiConsumer<GuiGraphicsExtractor, DeltaTracker> hudElement
	) {
		eventBus.addListener((RegisterGuiLayersEvent event) -> event.registerBelow(beforeThis, identifier, hudElement::accept));
	}

	@Override
	public void registerReloadListener(@NotNull Identifier id, @NotNull PreparableReloadListener listener) {
		eventBus.addListener((AddClientReloadListenersEvent event) -> event.addListener(id, listener));
	}

	@Override
	public @NonNull Identifier getChatIdentifier() {
		return VanillaGuiLayers.CHAT;
	}

	@Override
	public @NotNull KeyMapping registerKeyMapping(@NotNull KeyMapping keyMapping) {
		eventBus.addListener((RegisterKeyMappingsEvent event) -> event.register(keyMapping));
		return keyMapping;
	}
}