package me.Azz_9.flex_hud.client.customModules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
