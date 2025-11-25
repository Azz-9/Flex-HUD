package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import java.util.Arrays;

public class ConfigIntGrid extends AbstractConfigObject<int[][]> {

	public ConfigIntGrid(int[][] defaultValue, String configTextTranslationKey) {
		super(defaultValue, configTextTranslationKey);
	}

	public ConfigIntGrid(int[][] defaultValue) {
		super(defaultValue);
	}

	@Override
	public int[][] getValue() {
		// Créer une copie profonde du tableau
		int[][] copy = new int[super.getValue().length][];
		for (int i = 0; i < super.getValue().length; i++) {
			copy[i] = Arrays.copyOf(super.getValue()[i], super.getValue()[i].length);
		}
		return copy;
	}

	@Override
	public void setValue(int[][] newValue) {
		for (int i = 0; i < newValue.length; i++) {
			super.getValue()[i] = Arrays.copyOf(newValue[i], newValue[i].length);
		}
	}

	@Override
	public int[][] getDefaultValue() {
		// Créer une copie profonde du tableau par défaut
		int[][] copy = new int[super.getDefaultValue().length][];
		for (int i = 0; i < super.getDefaultValue().length; i++) {
			copy[i] = Arrays.copyOf(super.getDefaultValue()[i], super.getDefaultValue()[i].length);
		}
		return copy;
	}

	@Override
	public void setDefaultValue(int[][] newValue) {
		for (int i = 0; i < newValue.length; i++) {
			super.getDefaultValue()[i] = Arrays.copyOf(newValue[i], newValue[i].length);
		}
	}

	public int getLength() {
		return super.getValue().length;
	}

	public int getRowLength(int index) {
		return super.getValue()[index].length;
	}

	public int getIntValue(int x, int y) {
		return super.getValue()[y][x];
	}

	public void setIntValue(int x, int y, int value) {
		super.getValue()[y][x] = value;
	}

	public int getIntDefaultValue(int x, int y) {
		return super.getDefaultValue()[y][x];
	}

	@Override
	public void setToDefault() {
		this.setDefaultValue(super.getDefaultValue());
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

	@Override
	public JsonElement toJsonValue() {
		int[][] value = getValue();
		if (value == null) return JsonNull.INSTANCE;

		JsonArray outerArray = new JsonArray();
		for (int[] row : value) {
			JsonArray innerArray = new JsonArray();
			for (int cell : row) {
				innerArray.add(cell);
			}
			outerArray.add(innerArray);
		}

		return outerArray;
	}
}
