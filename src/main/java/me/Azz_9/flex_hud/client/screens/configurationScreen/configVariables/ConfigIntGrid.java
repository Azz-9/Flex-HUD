package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Arrays;

public class ConfigIntGrid extends AbstractConfigObject<int[][]> {

	public ConfigIntGrid(int[][] defaultValue, String configTextTranslationKey) {
		super(defaultValue, configTextTranslationKey);
	}

	public ConfigIntGrid(int[][] defaultValue) {
		super(defaultValue);
	}

	@Override
	protected int[][] parseValue(JsonElement element) {
		if (element == null || element.isJsonNull() || !element.isJsonArray()) return getDefaultValue();

		JsonArray outerArray = element.getAsJsonArray();
		int[][] result = new int[outerArray.size()][];

		for (int i = 0; i < outerArray.size(); i++) {
			JsonElement innerElement = outerArray.get(i);
			if (!innerElement.isJsonArray()) {
				// Si la ligne est invalide, on prend la ligne par défaut (si elle existe)
				result[i] = (i < getDefaultValue().length)
						? Arrays.copyOf(getDefaultValue()[i], getDefaultValue()[i].length)
						: new int[0];
				continue;
			}

			JsonArray innerArray = innerElement.getAsJsonArray();
			int[] innerValues = new int[innerArray.size()];

			for (int j = 0; j < innerArray.size(); j++) {
				try {
					innerValues[j] = innerArray.get(j).getAsInt();
				} catch (Exception e) {
					// Si un élément est invalide, on prend la valeur par défaut correspondante si dispo
					if (i < getDefaultValue().length && j < getDefaultValue()[i].length)
						innerValues[j] = getDefaultValue()[i][j];
					else
						innerValues[j] = 0; // fallback
				}
			}

			result[i] = innerValues;
		}

		return result;
	}
}
