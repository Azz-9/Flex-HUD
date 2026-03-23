package me.Azz_9.flex_hud.client.customModules.token;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.Variables;

public class TokenParser {

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_.]+)}");
	private static final String DELIMITER = ":";

	public static List<Token> parseText(@NonNull String text) {
		List<Token> tokens = new ArrayList<>();

		Matcher matcher = VARIABLE_PATTERN.matcher(text);

		int lastEnd = 0;

		while (matcher.find()) {

			// text before the variable
			if (matcher.start() > lastEnd) {
				String before = text.substring(lastEnd, matcher.start());
				if (!before.isEmpty()) {
					tokens.add(new TextToken(before));
				}
			}

			String[] parts = matcher.group(1).split(DELIMITER);
			String variableKey = parts[0].trim();
			Variable<?> variable = Variables.get(variableKey);

			if (variable != null) {
				List<String> modifiers = Arrays.stream(parts)
						.skip(1)
						.map(String::trim)
						.toList();

				tokens.add(new VariableToken<>(variable, modifiers));
			} else {
				tokens.add(new TextToken(matcher.group()));
			}

			lastEnd = matcher.end();
		}

		// remaining text after the last variable
		if (lastEnd < text.length()) {
			String remaining = text.substring(lastEnd);
			if (!remaining.isEmpty()) {
				tokens.add(new TextToken(remaining));
			}
		}

		return tokens;
	}
}
