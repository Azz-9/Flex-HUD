package me.Azz_9.flex_hud.client.customModules;

import java.util.ArrayList;
import java.util.List;

public final class CustomModuleSyntax {

	private CustomModuleSyntax() {
	}

	public static List<String> splitUnescaped(String input, char delimiter) {
		List<String> parts = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		boolean escaped = false;

		for (int i = 0; i < input.length(); i++) {
			char character = input.charAt(i);
			if (escaped) {
				current.append('\\').append(character);
				escaped = false;
				continue;
			}

			if (character == '\\') {
				escaped = true;
				continue;
			}

			if (character == delimiter) {
				parts.add(current.toString());
				current.setLength(0);
				continue;
			}

			current.append(character);
		}

		if (escaped) {
			current.append('\\');
		}

		parts.add(current.toString());
		return parts;
	}

	public static int findMatchingDelimiter(String source, int start, char open, char close) {
		int depth = 0;
		boolean escaped = false;

		for (int cursor = start; cursor < source.length(); cursor++) {
			char current = source.charAt(cursor);
			if (escaped) {
				escaped = false;
				continue;
			}

			if (current == '\\') {
				escaped = true;
				continue;
			}

			if (current == open) {
				depth++;
			} else if (current == close) {
				if (depth == 0) {
					return cursor;
				}
				depth--;
			}
		}

		return -1;
	}

	public static int findNextUnescaped(String input, int start, char target) {
		boolean escaped = false;
		for (int i = start; i < input.length(); i++) {
			char character = input.charAt(i);
			if (escaped) {
				escaped = false;
				continue;
			}

			if (character == '\\') {
				escaped = true;
				continue;
			}

			if (character == target) {
				return i;
			}
		}

		return -1;
	}

	public static String escape(String value, String escapedCharacters) {
		StringBuilder escaped = new StringBuilder(value.length());
		for (int i = 0; i < value.length(); i++) {
			char character = value.charAt(i);
			if (character == '\\' || escapedCharacters.indexOf(character) >= 0) {
				escaped.append('\\');
			}
			escaped.append(character);
		}
		return escaped.toString();
	}

	public static String unescape(String input) {
		StringBuilder unescaped = new StringBuilder(input.length());
		boolean escaped = false;

		for (int i = 0; i < input.length(); i++) {
			char character = input.charAt(i);
			if (escaped) {
				unescaped.append(character);
				escaped = false;
				continue;
			}

			if (character == '\\') {
				escaped = true;
				continue;
			}

			unescaped.append(character);
		}

		if (escaped) {
			unescaped.append('\\');
		}

		return unescaped.toString();
	}
}
