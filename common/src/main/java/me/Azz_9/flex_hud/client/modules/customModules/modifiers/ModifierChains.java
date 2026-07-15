package me.Azz_9.flex_hud.client.modules.customModules.modifiers;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class ModifierChains {

	private static final int IMPOSSIBLE_COST = Integer.MAX_VALUE / 4;

	private ModifierChains() {
	}

	static Modifiers.@Nullable CompiledFormatter compileFormatter(@Nullable Class<?> inputType, List<Modifiers.ResolvedModifier<?, ?>> modifiers) {
		if (modifiers.isEmpty()) {
			return String::valueOf;
		}

		if (inputType == null) {
			return null;
		}

		List<Modifiers.ResolvedModifier<?, ?>> orderedModifiers = orderForInputType(inputType, List.copyOf(modifiers));
		if (orderedModifiers == null) {
			return null;
		}

		return value -> {
			Object current = value;
			if (current == null) {
				return "null";
			}

			for (Modifiers.ResolvedModifier<?, ?> resolvedModifier : orderedModifiers) {
				current = applyResolvedModifier(current, resolvedModifier);
			}

			return (String) ValueCoercions.coerce(current, String.class);
		};
	}

	static @Nullable Object applyValueModifiers(@Nullable Object value, List<Modifiers.ResolvedModifier<?, ?>> modifiers) {
		if (modifiers.isEmpty()) {
			return value;
		}

		if (value == null) {
			return null;
		}

		List<Modifiers.ResolvedModifier<?, ?>> orderedModifiers = orderForInputType(value.getClass(), List.copyOf(modifiers));
		if (orderedModifiers == null) {
			return null;
		}

		Object current = value;
		for (Modifiers.ResolvedModifier<?, ?> resolvedModifier : orderedModifiers) {
			current = applyResolvedModifier(current, resolvedModifier);
		}
		return current;
	}

	static @Nullable Class<?> resolveOutputType(@Nullable Class<?> inputType, List<Modifiers.ResolvedModifier<?, ?>> modifiers) {
		if (inputType == null) {
			return null;
		}

		if (modifiers.isEmpty()) {
			return inputType;
		}

		List<Modifiers.ResolvedModifier<?, ?>> orderedModifiers = orderForInputType(inputType, List.copyOf(modifiers));
		if (orderedModifiers == null) {
			return null;
		}

		Class<?> outputType = inputType;
		for (Modifiers.ResolvedModifier<?, ?> resolvedModifier : orderedModifiers) {
			outputType = resolvedModifier.modifier().outputType();
		}
		return outputType;
	}

	private static @Nullable List<Modifiers.ResolvedModifier<?, ?>> orderForInputType(Class<?> inputType, List<Modifiers.ResolvedModifier<?, ?>> modifiers) {
		BestOrder bestOrder = new BestOrder();
		searchBestOrder(inputType, new ArrayList<>(modifiers), new ArrayList<>(modifiers.size()), 0, bestOrder);

		if (bestOrder.cost == IMPOSSIBLE_COST) {
			return null;
		}

		return bestOrder.modifiers;
	}

	private static void searchBestOrder(Class<?> currentType,
	                                    List<Modifiers.ResolvedModifier<?, ?>> remaining,
	                                    List<Modifiers.ResolvedModifier<?, ?>> path,
	                                    int currentCost,
	                                    BestOrder bestOrder) {
		if (currentCost >= bestOrder.cost) {
			return;
		}

		if (remaining.isEmpty()) {
			int totalCost = currentCost + transitionCost(currentType, String.class);
			if (totalCost < bestOrder.cost) {
				bestOrder.cost = totalCost;
				bestOrder.modifiers = List.copyOf(path);
			}
			return;
		}

		for (int i = 0; i < remaining.size(); i++) {
			Modifiers.ResolvedModifier<?, ?> candidate = remaining.remove(i);
			Class<?> candidateInputType = candidate.modifier().inputType();
			int transitionCost = transitionCost(currentType, candidateInputType);

			if (transitionCost < IMPOSSIBLE_COST) {
				path.add(candidate);
				searchBestOrder(candidate.modifier().outputType(), remaining, path, currentCost + transitionCost, bestOrder);
				path.removeLast();
			}

			remaining.add(i, candidate);
		}
	}

	private static int transitionCost(Class<?> fromType, Class<?> toType) {
		if (toType.isAssignableFrom(fromType)) {
			return 0;
		}

		if (ValueCoercions.canCoerce(fromType, toType)) {
			return 1;
		}

		return IMPOSSIBLE_COST;
	}

	private static Object applyResolvedModifier(Object input, Modifiers.ResolvedModifier<?, ?> resolvedModifier) {
		Modifier<?, ?> modifier = resolvedModifier.modifier();
		Object adaptedInput = ValueCoercions.coerce(input, modifier.inputType());
		return modifier.applyUnchecked(adaptedInput, resolvedModifier.arguments());
	}

	private static final class BestOrder {
		private int cost = IMPOSSIBLE_COST;
		private List<Modifiers.ResolvedModifier<?, ?>> modifiers = List.of();
	}
}
