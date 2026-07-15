package me.Azz_9.flex_hud.client.modules.notHud;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.item.PrimedTnt;

import java.util.List;

import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.AbstractModule;
import me.Azz_9.flex_hud.client.modules.TickableModule;

public class TntCountdown extends AbstractModule implements TickableModule {
	public TntCountdown() {
		super("tnt_countdown");
		this.enabled.setConfigTextTranslationKey("flex_hud.tnt_countdown.config.enable");
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.tnt_countdown");
	}

	@Override
	public void tick() {
		LocalPlayer player = MINECRAFT.player;

		if (player == null) {
			return;
		}

		List<PrimedTnt> tntEntities = player.level().getEntitiesOfClass(PrimedTnt.class, player.getBoundingBox().inflate(20), (entity) -> true);

		for (PrimedTnt tntEntity : tntEntities) {
			int seconds = tntEntity.getFuse() / 20;
			int hundredth = (tntEntity.getFuse() % 20) * 5;

			MutableComponent text = Component.literal(seconds + String.format(".%02d", hundredth));
			switch (seconds) {
				case 2 -> text.withStyle(ChatFormatting.YELLOW);
				case 1 -> text.withStyle(ChatFormatting.GOLD);
				case 0 -> text.withStyle(ChatFormatting.RED);
				default -> text.withStyle(ChatFormatting.WHITE);
			}
			tntEntity.setCustomName(text);
			if (!tntEntity.isCustomNameVisible()) tntEntity.setCustomNameVisible(true);
		}
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 200;
				}

				super.initContent();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build()
				);
			}
		};
	}
}
