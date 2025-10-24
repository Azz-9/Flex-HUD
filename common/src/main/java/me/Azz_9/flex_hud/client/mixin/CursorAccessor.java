package me.Azz_9.flex_hud.client.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.Cursor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DrawContext.class)
public interface CursorAccessor {

	@Accessor("cursor")
	Cursor getCursor();
}
