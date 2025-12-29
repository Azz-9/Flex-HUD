package me.Azz_9.flex_hud.client.utils;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import org.lwjgl.glfw.GLFW;

public class Cursors extends CursorTypes {
	public static final CursorType DEFAULT = CursorType.DEFAULT;
	public static final CursorType RESIZE_NESW = CursorType.createStandardCursor(GLFW.GLFW_RESIZE_NESW_CURSOR, "resize_nesw", CursorType.DEFAULT);
	public static final CursorType RESIZE_NWSE = CursorType.createStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR, "resize_nwse", CursorType.DEFAULT);
	public static final CursorType CROSSHAIR = CursorType.createStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR, "crosshair", CursorType.DEFAULT);
}
