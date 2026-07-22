package me.Azz_9.flex_hud.client.modules.notHud;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.Util;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.net.URI;

import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.AbstractModule;

public class DurabilityPing extends AbstractModule {

	private static final String MODRINTH_MOD_PAGE = "https://modrinth.com/mod/durability-alert-azz_9";
	private static final String CURSEFORGE_MOD_PAGE = "https://www.curseforge.com/minecraft/mc-mods/durabilityalert";
	private static final Component MODRINTH_COMPONENT = Component.literal("Modrinth")
			.withStyle(style -> style
					.withUnderlined(true)
					.withClickEvent(new ClickEvent.OpenUrl(URI.create(MODRINTH_MOD_PAGE)))
			);
	private static final Component CURSEFORGE_COMPONENT = Component.literal("CurseForge")
			.withStyle(style -> style
					.withUnderlined(true)
					.withClickEvent(new ClickEvent.OpenUrl(URI.create(CURSEFORGE_MOD_PAGE)))
			);
	private static final Component MESSAGE = Component.translatable(
			"flex_hud.durability_ping.config_screen_message",
			MODRINTH_COMPONENT,
			CURSEFORGE_COMPONENT
	);

	public DurabilityPing() {
		super("durability_ping");

		ConfigRegistry.unregister(getID(), "enabled");
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.durability_ping");
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {

			private static final float MESSAGE_WIDTH_FRACTION = 0.4f;

			@Override
			protected void initContent() {
				int width = (int) (MINECRAFT.getWindow().getGuiScaledWidth() * MESSAGE_WIDTH_FRACTION);

				MultiLineTextWidget text = new FocusableTextWidget(
						width,
						MESSAGE,
						font,
						false,
						false,
						FocusableTextWidget.DEFAULT_PADDING
				)
						.setCentered(true)
						.configureStyleHandling(true, style -> {
							if (style.getClickEvent() instanceof ClickEvent.OpenUrl(URI uri)) {
								Util.getPlatform().openUri(uri);
							}
						});
				text.setPosition(
						(MINECRAFT.getWindow().getGuiScaledWidth() - text.getWidth()) / 2,
						(MINECRAFT.getWindow().getGuiScaledHeight() - text.getHeight()) / 2
				);

				addRenderableWidget(text);
			}
		};
	}
}
