package me.Azz_9.flex_hud.client.modules.customModules;

import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.Supplier;

public final class Variable<T> {
	private final Component name;
	private final Component description;
	private final String key;
	private final Supplier<T> supplier;
	private T value;
	private long version;
	private int usageCount;

	public Variable(Component name, Component description, String key, Supplier<T> supplier) {
		this.name = Objects.requireNonNull(name, "name");
		this.description = Objects.requireNonNull(description, "description");
		this.key = Objects.requireNonNull(key, "key");
		this.supplier = Objects.requireNonNull(supplier, "supplier");
	}

	public Component getName() {
		return name;
	}

	public Component getDescription() {
		return description;
	}

	public String getKey() {
		return key;
	}

	public void updateValue() {
		T newValue = supplier.get();
		if (!Objects.equals(value, newValue)) {
			version++;
		}

		value = newValue;
	}

	public void acquireUsage() {
		if (usageCount++ == 0) {
			updateValue();
		}
	}

	public void releaseUsage() {
		if (usageCount <= 0) {
			throw new IllegalStateException("Variable usage released too many times: " + key);
		}
		usageCount--;
	}

	public boolean isUsed() {
		return usageCount > 0;
	}

	public T getValue() {
		return value;
	}

	public long getVersion() {
		return version;
	}
}
