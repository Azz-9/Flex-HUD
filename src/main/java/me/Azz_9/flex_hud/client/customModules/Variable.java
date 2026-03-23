package me.Azz_9.flex_hud.client.customModules;

import net.minecraft.text.Text;

import java.util.function.Supplier;

public class Variable<T> {
	private final Text name;
	private final Text description;
	private final String key;
	private final Supplier<T> supplier;
	private T value;

	public Variable(Text name, Text description, String key, Supplier<T> supplier) {
		this.name = name;
		this.description = description;
		this.key = key;
		this.supplier = supplier;

	}

	public Text getName() {
		return name;
	}

	public Text getDescription() {
		return description;
	}

	public String getKey() {
		return key;
	}

	public void updateValue() {
		value = supplier.get();
	}

	public T getValue() {
		return value;
	}
}
