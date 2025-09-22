package me.Azz_9.flex_hud.client.utils;

import net.minecraft.client.gui.cursor.Cursor;
import org.lwjgl.glfw.GLFW;

public class Cursors extends net.minecraft.client.gui.cursor.StandardCursors {
	public static final Cursor RESIZE_NESW = Cursor.createStandard(GLFW.GLFW_RESIZE_NESW_CURSOR, "resize_nesw", Cursor.DEFAULT);
	public static final Cursor RESIZE_NWSE = Cursor.createStandard(GLFW.GLFW_RESIZE_NWSE_CURSOR, "resize_nwse", Cursor.DEFAULT);
}
