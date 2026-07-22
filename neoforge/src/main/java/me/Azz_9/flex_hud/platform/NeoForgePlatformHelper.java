package me.Azz_9.flex_hud.platform;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.resources.ResourceLocation;
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
			@NotNull ResourceLocation beforeThis,
			@NotNull ResourceLocation location,
			@NotNull BiConsumer<GuiGraphics, DeltaTracker> hudElement
	) {
		eventBus.addListener((RegisterGuiLayersEvent event) -> event.registerBelow(beforeThis, location, hudElement::accept));
	}

	@Override
	public void registerReloadListener(@NotNull ResourceLocation location, @NotNull PreparableReloadListener listener) {
		eventBus.addListener((AddClientReloadListenersEvent event) -> event.addListener(location, listener));
	}

	@Override
	public @NotNull ResourceLocation getChatLocation() {
		return VanillaGuiLayers.CHAT;
	}

	@Override
	public @NotNull ResourceLocation getBossBarLocation() {
		return VanillaGuiLayers.BOSS_OVERLAY;
	}

	@Override
	public @NotNull ResourceLocation getCrosshairLocation() {
		return VanillaGuiLayers.CROSSHAIR;
	}

	@Override
	public @NotNull ResourceLocation getScoreboardLocation() {
		return VanillaGuiLayers.SCOREBOARD_SIDEBAR;
	}

	@Override
	public @NotNull KeyMapping registerKeyMapping(@NotNull KeyMapping keyMapping) {
		eventBus.addListener((RegisterKeyMappingsEvent event) -> event.register(keyMapping));
		return keyMapping;
	}

	@Override
	public ScreenRectangle scissorStackPeek(@NotNull GuiGraphics graphics) {
		return graphics.peekScissorStack();
	}
}