package me.Azz_9.flex_hud.utils;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;

import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.Scoreboard;

public final class ScoreboardMixinHelper {

	private static final int SCOREBOARD_PADDING = 2;

	private ScoreboardMixinHelper() {
	}

	public static int beforeRender(
			int headerY,
			GuiGraphics graphics,
			int biggestWidth,
			int height,
			LocalIntRef bottom,
			LocalIntRef left,
			LocalIntRef right,
			LocalIntRef backgroundColor,
			LocalIntRef headerBackgroundColor
	) {
		Scoreboard scoreboard = Modules.getInstance().scoreboard;

		if (!Modules.getInstance().isEnabled.getValue() || !scoreboard.enabled.getValue()) {
			return headerY;
		}

		scoreboard.setWidth(biggestWidth + SCOREBOARD_PADDING * 2);
		scoreboard.setHeight(MINECRAFT.font.lineHeight + 1 + height);

		bottom.set(scoreboard.getHeight());
		left.set(SCOREBOARD_PADDING);
		right.set(scoreboard.getWidth());
		backgroundColor.set(ARGB.color(0.3f, scoreboard.backgroundColor.getValue()));
		headerBackgroundColor.set(ARGB.color(0.4f, scoreboard.backgroundColor.getValue()));

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(scoreboard.getRoundedX(), scoreboard.getRoundedY());
		matrices.scale(scoreboard.getScale());

		return scoreboard.getHeight() - height;
	}

	public static MutableComponent modifyScoreString(MutableComponent original) {
		if (Modules.getInstance().isEnabled.getValue()
				&& Modules.getInstance().scoreboard.enabled.getValue()
				&& !Modules.getInstance().scoreboard.showScore.getValue()) {
			return Component.empty();
		}
		return original;
	}

	public static int modifyScoreWidth(int original) {
		if (Modules.getInstance().isEnabled.getValue()
				&& Modules.getInstance().scoreboard.enabled.getValue()
				&& !Modules.getInstance().scoreboard.showScore.getValue()) {
			return 0;
		}
		return original;
	}
}
