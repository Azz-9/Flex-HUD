package me.Azz_9.flex_hud.client.configurableModules.modules.notHud;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.AbstractCrosshairConfigScreen;

public class TntCountdown extends AbstractModule implements TickableModule {
	public TntCountdown() {
		super("tnt_countdown");
		this.enabled.setConfigTextTranslationKey("flex_hud.tnt_countdown.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.tnt_countdown");
	}

	@Override
	public void tick() {
		PlayerEntity player = CLIENT.player;

		if (player == null) {
			return;
		}

		List<TntEntity> tntEntities = player.getEntityWorld().getEntitiesByClass(TntEntity.class, player.getBoundingBox().expand(20), (entity) -> true);

		for (TntEntity tntEntity : tntEntities) {
			int seconds = tntEntity.getFuse() / 20;
			int hundredth = (tntEntity.getFuse() % 20) * 5;

			MutableText text = Text.literal(seconds + String.format(".%02d", hundredth));
			switch (seconds) {
				case 2 -> text.formatted(Formatting.YELLOW);
				case 1 -> text.formatted(Formatting.GOLD);
				case 0 -> text.formatted(Formatting.RED);
				default -> text.formatted(Formatting.WHITE);
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
				if (CLIENT.getLanguageManager().getLanguage().equals("fr_fr")) {
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
