package me.Azz_9.flex_hud.client.customModules.template;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;
import me.Azz_9.flex_hud.client.customModules.text.CustomCondition;

public class CompiledCustomTextTest {

	@Test
	void appliesModifiersAndInlineStyles() {
		Modifiers.init();

		AtomicReference<Double> value = new AtomicReference<>(12.345);
		Variable<Double> variable = createVariable("player.x", value::get);

		CompiledCustomText template = CompiledCustomText.compile(
				"X: &bold{player.x:round.1}&bold!",
				key -> "player.x".equals(key) ? variable : null
		);

		CompiledCustomText.RenderData renderData = template.getRenderDataForTests();
		List<Text> siblings = renderData.text().getSiblings();

		assertEquals("X: 12.3!", renderData.text().getString());
		assertEquals(3, siblings.size());
		assertFalse(renderData.hasOwnColors());
		assertFalse(siblings.getFirst().getStyle().isBold());
		assertTrue(siblings.get(1).getStyle().isBold());
		assertFalse(siblings.getLast().getStyle().isBold());
	}

	@Test
	void keepsUnknownModifiersAndStylesLiteral() {
		Modifiers.init();

		Variable<Integer> variable = createVariable("player.x", () -> 12);
		CompiledCustomText template = CompiledCustomText.compile(
				"{player.x:oops} &oops text",
				key -> "player.x".equals(key) ? variable : null
		);

		assertEquals("{player.x:oops} &oops text", template.getRenderDataForTests().text().getString());
	}

	@Test
	void nestedGradientsOverrideOuterGradientColors() {
		CompiledCustomText template = CompiledCustomText.compile(
				"[#ff0000:#00ff00,ab[#0000ff:#00ffff,cd]ef]",
				key -> null
		);

		CompiledCustomText.RenderData renderData = template.getRenderDataForTests();
		List<Text> siblings = renderData.text().getSiblings();

		assertEquals("abcdef", renderData.text().getString());
		assertTrue(renderData.hasOwnColors());
		assertEquals(6, siblings.size());

		TextColor bColor = colorOf(siblings.get(1).getStyle());
		TextColor cColor = colorOf(siblings.get(2).getStyle());
		TextColor dColor = colorOf(siblings.get(3).getStyle());
		TextColor eColor = colorOf(siblings.get(4).getStyle());

		assertNotEquals(bColor.getRgb(), cColor.getRgb());
		assertNotEquals(cColor.getRgb(), eColor.getRgb());
		assertNotEquals(dColor.getRgb(), eColor.getRgb());
	}

	@Test
	void sameColorDirectiveTogglesBackToDefaultStyledColor() {
		CompiledCustomText template = CompiledCustomText.compile("&greenAB&greenCD", key -> null);

		CompiledCustomText.RenderData renderData = template.getRenderDataForTests();
		List<Text> siblings = renderData.text().getSiblings();

		assertEquals("ABCD", renderData.text().getString());
		assertTrue(renderData.hasOwnColors());
		assertEquals(2, siblings.size());
		assertEquals(0x55ff55, colorOf(siblings.getFirst().getStyle()).getRgb());
		assertEquals(0xffffff, colorOf(siblings.getLast().getStyle()).getRgb());
	}

	@Test
	void chainedConditionalBranchesUseTheOriginalNumericValue() {
		Modifiers.init();

		AtomicReference<Integer> value = new AtomicReference<>(5);
		Variable<Integer> variable = createVariable("player.x", value::get);
		CompiledCustomText template = CompiledCustomText.compile(
				"{player.x:if_lt.10.<10.0.if_gt.100.>100}",
				key -> "player.x".equals(key) ? variable : null
		);

		assertEquals("<10.0", template.getRenderDataForTests().text().getString());

		value.set(150);
		variable.updateValue();
		assertEquals(">100", template.getRenderDataForTests().text().getString());

		value.set(50);
		variable.updateValue();
		assertEquals("50", template.getRenderDataForTests().text().getString());
	}

	@Test
	void generalConditionsCanRenderMixedTextAndVariables() {
		Modifiers.init();

		AtomicReference<Integer> health = new AtomicReference<>(10);
		AtomicReference<Integer> food = new AtomicReference<>(5);
		AtomicReference<Integer> armor = new AtomicReference<>(20);
		Variable<Integer> healthVariable = createVariable("player.health", health::get);
		Variable<Integer> foodVariable = createVariable("player.food", food::get);
		Variable<Integer> armorVariable = createVariable("player.armor", armor::get);

		CompiledCustomText template = CompiledCustomText.compile(
				"{if:player.health>0&&player.food>0|Armor:{player.armor}}",
				key -> switch (key) {
					case "player.health" -> healthVariable;
					case "player.food" -> foodVariable;
					case "player.armor" -> armorVariable;
					default -> null;
				}
		);

		assertEquals("Armor:20", template.getRenderDataForTests().text().getString());

		food.set(0);
		foodVariable.updateValue();
		assertEquals("", template.getRenderDataForTests().text().getString());
	}

	@Test
	void generalConditionsCanUseOrConnectors() {
		Modifiers.init();

		AtomicReference<Integer> health = new AtomicReference<>(0);
		AtomicReference<Integer> food = new AtomicReference<>(5);
		AtomicReference<Integer> armor = new AtomicReference<>(20);
		Variable<Integer> healthVariable = createVariable("player.health", health::get);
		Variable<Integer> foodVariable = createVariable("player.food", food::get);
		Variable<Integer> armorVariable = createVariable("player.armor", armor::get);

		CompiledCustomText template = CompiledCustomText.compile(
				"{if:player.health>0||player.food>0|Ready:{player.armor}}",
				key -> switch (key) {
					case "player.health" -> healthVariable;
					case "player.food" -> foodVariable;
					case "player.armor" -> armorVariable;
					default -> null;
				}
		);

		assertEquals("Ready:20", template.getRenderDataForTests().text().getString());

		food.set(0);
		foodVariable.updateValue();
		assertEquals("", template.getRenderDataForTests().text().getString());

		health.set(1);
		healthVariable.updateValue();
		assertEquals("Ready:20", template.getRenderDataForTests().text().getString());
	}

	@Test
	void conditionDisplayUsesReadableConnectorNames() {
		Modifiers.init();

		Variable<Integer> healthVariable = createVariable("player.health", () -> 1);
		Variable<Integer> foodVariable = createVariable("player.food", () -> 1);

		CustomCondition.Condition condition = CustomCondition.parse(
				"player.health>0 OR player.food>0",
				key -> switch (key) {
					case "player.health" -> healthVariable;
					case "player.food" -> foodVariable;
					default -> null;
				}
		);

		assertNotNull(condition);
		assertEquals("player.health > 0 OR player.food > 0", condition.displayText());
		assertEquals("player.health>0||player.food>0", condition.format());
	}

	private static <T> Variable<T> createVariable(String key, java.util.function.Supplier<T> supplier) {
		Variable<T> variable = new Variable<>(Text.of(key), Text.of(key), key, supplier);
		variable.updateValue();
		return variable;
	}

	private static TextColor colorOf(Style style) {
		TextColor color = style.getColor();
		if (color == null) {
			throw new AssertionError("Expected a text color");
		}

		return color;
	}
}
