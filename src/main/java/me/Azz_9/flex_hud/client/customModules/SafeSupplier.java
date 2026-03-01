package me.Azz_9.flex_hud.client.customModules;

import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public class SafeSupplier<T> implements Supplier<T> {

	private final @NonNull Supplier<T> supplier;
	private final @NonNull T defaultValue;

	private SafeSupplier(@NonNull Supplier<T> supplier, @NonNull T defaultValue) {
		this.supplier = supplier;
		this.defaultValue = defaultValue;
	}

	public static <T> SafeSupplier<T> create(Supplier<T> supplier, T defaultValue) {
		return new SafeSupplier<>(supplier, defaultValue);
	}

	@Override
	public T get() {
		try {
			return supplier.get();
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
