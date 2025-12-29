package me.Azz_9.flex_hud.client.configurableModules.modules.notHud;

import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.AbstractCrosshairConfigScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.item.PrimedTnt;

import java.util.List;

public class TntCountdown extends AbstractModule implements TickableModule {
	public TntCountdown() {
		this.enabled.setConfigTextTranslationKey("flex_hud.tnt_countdown.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.tnt_countdown");
	}

	@Override
	public String getID() {
		return "tnt_countdown";
	}

	@Override
	public void tick() {
		LocalPlayer player = Minecraft.getInstance().player;

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
		return new AbstractCrosshairConfigScreen(getName(), parent) {
			@Override
			protected void init() {
				if (Minecraft.getInstance().getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 200;
				}

				super.init();

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
