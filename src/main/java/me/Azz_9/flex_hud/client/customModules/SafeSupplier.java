package me.Azz_9.flex_hud.client.customModules;

import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public class SafeSupplier<T> implements Supplier<T> {

	private final @NonNull Supplier<T> supplier;
	private final @NonNull T fallbackValue;

	private SafeSupplier(@NonNull Supplier<T> supplier, @NonNull T fallbackValue) {
		this.supplier = supplier;
		this.fallbackValue = fallbackValue;
	}

	public static <T> SafeSupplier<T> create(Supplier<T> supplier, T fallbackValue) {
		return new SafeSupplier<>(supplier, fallbackValue);
	}

	@Override
	public T get() {
		try {
			return supplier.get();
		} catch (Exception e) {
			return fallbackValue;
		}
	}
}
