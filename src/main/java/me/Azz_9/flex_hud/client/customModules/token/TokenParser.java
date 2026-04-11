package me.Azz_9.flex_hud.client.customModules.token;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.Variables;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;

public class TokenParser {

	public static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{((?:\\\\.|[^{}])+)}");
	public static final String DELIMITER = ":";

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

			List<String> parts = Modifiers.splitUnescaped(matcher.group(1), DELIMITER.charAt(0));
			String variableKey = parts.getFirst().trim();
			Variable<?> variable = Variables.get(variableKey);

			if (variable != null) {
				List<Modifiers.ResolvedModifier<?, ?>> modifiers = new ArrayList<>(Math.max(0, parts.size() - 1));
				boolean invalidModifier = false;
				for (int i = 1; i < parts.size(); i++) {
					Modifiers.ResolvedModifier<?, ?> resolvedModifier = Modifiers.get(parts.get(i));
					if (resolvedModifier == null) {
						invalidModifier = true;
						break;
					}
					modifiers.add(resolvedModifier);
				}

				tokens.add(invalidModifier ? new TextToken(matcher.group()) : new VariableToken<>(variable, modifiers));
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
