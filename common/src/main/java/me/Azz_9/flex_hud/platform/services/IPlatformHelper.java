package me.Azz_9.flex_hud.platform.services;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.BiConsumer;

public interface IPlatformHelper {

	/**
	 * Gets the name of the current platform
	 *
	 * @return The name of the current platform.
	 */
	@NotNull String getPlatformName();

	/**
	 * Checks if a mod with the given id is loaded.
	 *
	 * @param modId The mod to check if it is loaded.
	 * @return True if the mod is loaded, false otherwise.
	 */
	boolean isModLoaded(@NotNull String modId);

	/**
	 * Check if the game is currently in a development environment.
	 *
	 * @return True if in a development environment, false otherwise.
	 */
	boolean isDevelopmentEnvironment();

	/**
	 * Gets the name of the environment type as a string.
	 *
	 * @return The name of the environment type.
	 */
	default @NotNull String getEnvironmentName() {
		return isDevelopmentEnvironment() ? "development" : "production";
	}

	@NotNull Path getConfigDir();

	void registerClientStartEvent(@NotNull Runnable runnable);

	void registerEndClientTickEvent(@NotNull Runnable runnable);

	void registerJoinEvent(@NotNull Runnable runnable);

	void registerDisconnectEvent(@NotNull Runnable runnable);

	void registerHudElement(@NotNull Identifier beforeThis, @NotNull Identifier identifier, @NotNull BiConsumer<GuiGraphicsExtractor, DeltaTracker> hudElement);

	void registerReloadListener(@NotNull Identifier id, @NotNull PreparableReloadListener listener);

	@NotNull Identifier getChatIdentifier();

	@NotNull Identifier getBossBarIdentifier();

	@NotNull Identifier getCrosshairIdentifier();

	@NotNull Identifier getScoreboardIdentifier();

	@NotNull KeyMapping registerKeyMapping(@NotNull KeyMapping keyMapping);

	ScreenRectangle scissorStackPeek(@NotNull GuiGraphicsExtractor graphics);
}