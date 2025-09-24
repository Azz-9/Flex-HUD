package me.Azz_9.flex_hud.client.utils;

import net.minecraft.client.gui.cursor.Cursor;
import org.lwjgl.glfw.GLFW;

public class Cursors extends net.minecraft.client.gui.cursor.StandardCursors {
	public static final Cursor RESIZE_NESW = Cursor.createStandard(GLFW.GLFW_RESIZE_NESW_CURSOR, "resize_nesw", Cursor.DEFAULT);
	public static final Cursor RESIZE_NWSE = Cursor.createStandard(GLFW.GLFW_RESIZE_NWSE_CURSOR, "resize_nwse", Cursor.DEFAULT);
	public static final Cursor HIDDEN = Cursor.createStandard(GLFW.GLFW_CURSOR_UNAVAILABLE, "hidden", Cursor.DEFAULT);
	public static final Cursor CROSSHAIR = Cursor.createStandard(GLFW.GLFW_CROSSHAIR_CURSOR, "crosshair", Cursor.DEFAULT);
}
