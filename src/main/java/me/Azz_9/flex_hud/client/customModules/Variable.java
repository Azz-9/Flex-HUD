package me.Azz_9.flex_hud.client.customModules;

import net.minecraft.text.Text;

import java.util.function.Function;
import java.util.function.Supplier;

public class Variable<T> {
	private final Text name;
	private final Text description;
	private final String key;
	private final Supplier<T> supplier;
	private final Function<T, String> formatter;
	private T value;

	public Variable(Text name, Text description, String key, Supplier<T> supplier, Function<T, String> formatter) {
		this.name = name;
		this.description = description;
		this.key = key;
		this.supplier = supplier;
		this.formatter = formatter;
	}

	public Variable(Text name, Text description, String key, Supplier<T> supplier) {
		this.name = name;
		this.description = description;
		this.key = key;
		this.supplier = supplier;
		this.formatter = Object::toString;
	}

	public String resolve() {
		if (value == null) updateValue();
		return formatter.apply(value);
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
}
