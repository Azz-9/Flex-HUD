package me.Azz_9.flex_hud.client.modules.customModules;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import me.Azz_9.flex_hud.client.gui.screens.CreateModuleScreen;

public class SafeSupplier<T> implements Supplier<T> {

	private final @NotNull Supplier<T> supplier;
	private final @NotNull T fallbackValue;
	private final @NotNull T placeholderValue;

	private SafeSupplier(@NotNull Supplier<T> supplier, @NotNull T fallbackValue, @NotNull T placeholderValue) {
		this.supplier = supplier;
		this.fallbackValue = fallbackValue;
		this.placeholderValue = placeholderValue;
	}

	public static <T> @NotNull SafeSupplier<T> create(@NotNull Supplier<T> supplier, @NotNull T fallbackValue, @NotNull T placeholderValue) {
		return new SafeSupplier<>(supplier, fallbackValue, placeholderValue);
	}

	public static <T> @NotNull SafeSupplier<T> create(@NotNull Supplier<T> supplier, @NotNull T fallbackValue) {
		return new SafeSupplier<>(supplier, fallbackValue, fallbackValue);
	}

	@Override
	public T get() {
		try {
			return supplier.get();
		} catch (Exception e) {
			if (MINECRAFT.screen instanceof CreateModuleScreen) {
				return placeholderValue;
			}
			return fallbackValue;
		}
	}
}
