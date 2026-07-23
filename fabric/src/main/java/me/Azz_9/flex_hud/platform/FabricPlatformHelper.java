package me.Azz_9.flex_hud.platform;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.BiConsumer;

import me.Azz_9.flex_hud.platform.services.IPlatformHelper;

public class FabricPlatformHelper implements IPlatformHelper {

	@Override
	public @NotNull String getPlatformName() {
		return "Fabric";
	}

	@Override
	public boolean isModLoaded(@NotNull String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public @NotNull Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	public void registerClientStartEvent(@NotNull Runnable runnable) {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> runnable.run());
	}

	@Override
	public void registerEndClientTickEvent(@NotNull Runnable runnable) {
		ClientTickEvents.END_CLIENT_TICK.register(client -> runnable.run());
	}

	@Override
	public void registerJoinEvent(@NotNull Runnable runnable) {
		ClientPlayConnectionEvents.JOIN.register((listener, sender, minecraft) -> runnable.run());
	}

	@Override
	public void registerDisconnectEvent(@NotNull Runnable runnable) {
		ClientPlayConnectionEvents.DISCONNECT.register((listener, minecraft) -> runnable.run());
	}

	@Override
	public void registerHudElement(@NotNull ResourceLocation beforeThis, @NotNull ResourceLocation location, @NotNull BiConsumer<GuiGraphics, DeltaTracker> hudElement) {
		HudElementRegistry.attachElementBefore(
				beforeThis,
				location,
				hudElement::accept
		);
	}

	@Override
	public void registerReloadListener(@NotNull ResourceLocation location, @NotNull PreparableReloadListener listener) {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(location, listener);
	}

	@Override
	public @NotNull ResourceLocation getChatLocation() {
		return VanillaHudElements.CHAT;
	}

	@Override
	public @NotNull KeyMapping registerKeyMapping(@NotNull KeyMapping keyMapping) {
		return KeyBindingHelper.registerKeyBinding(keyMapping);
	}

	@Override
	public ScreenRectangle scissorStackPeek(@NotNull GuiGraphics graphics) {
		return graphics.scissorStack.peek();
	}
}
