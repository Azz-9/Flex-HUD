package me.Azz_9.flex_hud.client.gui;

import static org.lwjgl.sdl.SDLMouse.*;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;

public class Cursors extends CursorTypes {
	public static final CursorType DEFAULT = CursorType.DEFAULT;
	public static final CursorType RESIZE_NESW = CursorType.createStandardCursor(SDL_SYSTEM_CURSOR_NESW_RESIZE, "resize_nesw", CursorType.DEFAULT);
	public static final CursorType RESIZE_NWSE = CursorType.createStandardCursor(SDL_SYSTEM_CURSOR_NWSE_RESIZE, "resize_nwse", CursorType.DEFAULT);
	public static final CursorType CROSSHAIR = CursorType.createStandardCursor(SDL_SYSTEM_CURSOR_CROSSHAIR, "crosshair", CursorType.DEFAULT);
}
