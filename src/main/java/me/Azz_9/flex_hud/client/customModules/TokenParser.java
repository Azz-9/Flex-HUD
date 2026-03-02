package me.Azz_9.flex_hud.client.customModules;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenParser {

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_.]+)}");

	static List<Token> parseText(@NonNull String text) {
		List<Token> tokens = new ArrayList<>();

		Matcher matcher = VARIABLE_PATTERN.matcher(text);

		int lastEnd = 0;

		while (matcher.find()) {

			if (matcher.start() > lastEnd) {
				String before = text.substring(lastEnd, matcher.start());
				if (!before.isEmpty()) {
					tokens.add(new TextToken(before));
				}
			}

			String key = matcher.group(1);
			Variable<?> variable = Variables.get(key);

			if (variable != null) {
				tokens.add(new VariableToken(variable));
			} else {
				tokens.add(new TextToken(matcher.group()));
			}

			lastEnd = matcher.end();
		}

		if (lastEnd < text.length()) {
			String remaining = text.substring(lastEnd);
			if (!remaining.isEmpty()) {
				tokens.add(new TextToken(remaining));
			}
		}

		return tokens;
	}
}
