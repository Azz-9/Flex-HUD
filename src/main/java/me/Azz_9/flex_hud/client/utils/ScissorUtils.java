package me.Azz_9.flex_hud.client.utils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;

import java.lang.reflect.Field;

import me.Azz_9.flex_hud.client.mixin.drawContext.ScissorStackAccessor;

public final class ScissorUtils {

	private static Field scissorStackField;

	private ScissorUtils() {
	}

	public static ScreenRect peekLast(DrawContext context) {
		return getScissorStack(context).flex_hud$getStack().peekLast();
	}

	private static ScissorStackAccessor getScissorStack(DrawContext context) {
		try {
			if (scissorStackField != null) {
				return (ScissorStackAccessor) scissorStackField.get(context);
			}

			for (Field field : DrawContext.class.getDeclaredFields()) {
				field.setAccessible(true);
				Object value = field.get(context);
				if (value instanceof ScissorStackAccessor scissorStack) {
					scissorStackField = field;
					return scissorStack;
				}
			}
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Unable to read DrawContext scissor stack", e);
		}

		throw new IllegalStateException("Unable to find DrawContext scissor stack");
	}
}
