package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

public class ConfigInteger extends AbstractConfigObject<Integer> {
	private final int MIN;
	private final int MAX;

	public ConfigInteger(Integer defaultValue, String configTextTranslationKey, Integer min, Integer max) {
		super(defaultValue, configTextTranslationKey);
		this.MIN = (min == null ? 0 : min);
		this.MAX = (max == null ? Integer.MAX_VALUE : max);
	}

	public ConfigInteger(Integer defaultValue, String configTextTranslationKey) {
		this(defaultValue, configTextTranslationKey, 0, Integer.MAX_VALUE);
	}

	public int getMin() {
		return MIN;
	}

	public int getMax() {
		return MAX;
	}
}
