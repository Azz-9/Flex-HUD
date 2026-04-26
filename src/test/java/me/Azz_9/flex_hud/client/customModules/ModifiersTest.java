package me.Azz_9.flex_hud.client.customModules;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;

public class ModifiersTest {

	@Test
	void numericModifiersSupportBigDecimalCoercion() {
		Modifiers.init();

		Modifiers.ResolvedModifier<?, ?> roundModifier = Modifiers.get("round.2");
		assertNotNull(roundModifier);

		Modifiers.CompiledFormatter formatter = Modifiers.compileFormatter(Double.class, List.of(roundModifier));
		assertNotNull(formatter);

		assertEquals("12.35", formatter.format(12.345));
	}

	@Test
	void paddingSupportsSpaceCharacters() {
		Modifiers.init();

		Modifiers.ResolvedModifier<?, ?> padLeftModifier = Modifiers.get("pad_left.5. ");
		assertNotNull(padLeftModifier);

		Modifiers.CompiledFormatter formatter = Modifiers.compileFormatter(String.class, List.of(padLeftModifier));
		assertNotNull(formatter);

		assertEquals("   hi", formatter.format("hi"));
	}

	@Test
	void replaceSupportsEscapedSpecialCharacters() {
		Modifiers.init();

		Modifiers.ResolvedModifier<?, ?> replaceModifier = Modifiers.get("replace.\\.._");
		assertNotNull(replaceModifier);

		Modifiers.CompiledFormatter formatter = Modifiers.compileFormatter(String.class, List.of(replaceModifier));
		assertNotNull(formatter);

		assertEquals("1_2_3", formatter.format("1.2.3"));
	}

	@Test
	void conditionalModifierSupportsEmptyDefaultBranchText() {
		Modifiers.init();

		Modifiers.ResolvedModifier<?, ?> conditionalModifier = Modifiers.get("if_gt.0.");
		assertNotNull(conditionalModifier);

		Modifiers.CompiledFormatter formatter = Modifiers.compileFormatter(Double.class, List.of(conditionalModifier));
		assertNotNull(formatter);

		assertEquals("", formatter.format(10.0));
		assertEquals("-2.0", formatter.format(-2.0));
	}

	@Test
	void numericModifiersAreRejectedForStringInputs() {
		Modifiers.init();

		Modifiers.ResolvedModifier<?, ?> roundModifier = Modifiers.get("round.2");
		assertNotNull(roundModifier);

		assertNull(Modifiers.compileFormatter(String.class, List.of(roundModifier)));
	}

	@Test
	void numericModifiersAreRejectedForBooleanInputs() {
		Modifiers.init();

		Modifiers.ResolvedModifier<?, ?> absoluteValueModifier = Modifiers.get("abs");
		assertNotNull(absoluteValueModifier);

		assertNull(Modifiers.compileFormatter(Boolean.class, List.of(absoluteValueModifier)));
	}
}
