package me.Azz_9.flex_hud.client.modules.customModules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.List;

public class CustomModuleSyntaxTest {

	@Test
	void splitUnescapedKeepsEscapedDelimitersInsideParts() {
		assertEquals(
				List.of("player.name", "replace.\\:._", "upper"),
				CustomModuleSyntax.splitUnescaped("player.name:replace.\\:._:upper", ':')
		);
	}

	@Test
	void matchingDelimiterIgnoresEscapedAndNestedDelimiters() {
		String source = "{outer\\}{inner}end}";
		assertEquals(source.length() - 1, CustomModuleSyntax.findMatchingDelimiter(source, 1, '{', '}'));
	}

	@Test
	void escapeAndUnescapeRoundTripSpecialCharacters() {
		String raw = "a.b\\c[d]{e}";
		String escaped = CustomModuleSyntax.escape(raw, ".[]{}");

		assertEquals("a\\.b\\\\c\\[d\\]\\{e\\}", escaped);
		assertEquals(raw, CustomModuleSyntax.unescape(escaped));
	}
}
