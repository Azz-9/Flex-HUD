package me.Azz_9.flex_hud.client.mixin;

import com.mojang.blaze3d.platform.cursor.CursorType;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiGraphics.class)
public interface CursorAccessor {

	@Accessor("pendingCursor")
	CursorType getCursor();
}
