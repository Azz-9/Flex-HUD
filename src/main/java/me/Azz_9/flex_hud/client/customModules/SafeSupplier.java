package me.Azz_9.flex_hud.client.customModules;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

import me.Azz_9.flex_hud.client.screens.createModuleScreen.CreateModuleScreen;

public class SafeSupplier<T> implements Supplier<T> {

	private final @NonNull Supplier<T> supplier;
	private final @NonNull T fallbackValue;
	private final @NonNull T placeholderValue;

	private SafeSupplier(@NonNull Supplier<T> supplier, @NonNull T fallbackValue, @NonNull T placeholderValue) {
		this.supplier = supplier;
		this.fallbackValue = fallbackValue;
		this.placeholderValue = placeholderValue;
	}

	public static <T> @NonNull SafeSupplier<T> create(@NonNull Supplier<T> supplier, @NonNull T fallbackValue, @NonNull T placeholderValue) {
		return new SafeSupplier<>(supplier, fallbackValue, placeholderValue);
	}

	public static <T> @NonNull SafeSupplier<T> create(@NonNull Supplier<T> supplier, @NonNull T fallbackValue) {
		return new SafeSupplier<>(supplier, fallbackValue, fallbackValue);
	}

	@Override
	public T get() {
		try {
			return supplier.get();
		} catch (Exception e) {
			if (CLIENT.currentScreen instanceof CreateModuleScreen) {
				return placeholderValue;
			}
			return fallbackValue;
		}
	}
}
