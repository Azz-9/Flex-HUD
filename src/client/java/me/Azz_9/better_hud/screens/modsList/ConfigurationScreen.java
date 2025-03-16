package me.Azz_9.better_hud.screens.modsList;

import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.*;
import me.Azz_9.better_hud.screens.widgets.fields.PlaceholderTextFieldWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class ConfigurationScreen extends Screen {
	private final Screen parent;

	private PlaceholderTextFieldWidget searchBar;
	private ScrollableFeatureList featureList;

	private final String[] MODS_LIST = {
			Text.translatable("better_hud.coordinates").getString(),
			Text.translatable("better_hud.fps").getString(),
			Text.translatable("better_hud.clock").getString(),
			Text.translatable("better_hud.armor_status").getString(),
			Text.translatable("better_hud.direction").getString(),
			Text.translatable("better_hud.day_counter").getString(),
			Text.translatable("better_hud.ping").getString(),
			Text.translatable("better_hud.server_address").getString(),
			Text.translatable("better_hud.weather_changer").getString(),
			Text.translatable("better_hud.memory_usage").getString(),
			Text.translatable("better_hud.cps").getString(),
			Text.translatable("better_hud.time_changer").getString(),
			Text.translatable("better_hud.durability_ping").getString(),
			Text.translatable("better_hud.speedometer").getString(),
			Text.translatable("better_hud.reach").getString(),
			"Combo (HS)",
			Text.translatable("better_hud.playtime").getString(),
			"stopwatch (HS)",
			"shrieker warning level (HS)"
	};

	public ConfigurationScreen(Screen parent) {
		super(Text.translatable("better_hud.configuration_screen"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		final int BUTTON_WIDTH = 160;
		final int BUTTON_HEIGHT = 20;
		final int ICON_WIDTH_HEIGHT = 64;
		final int PADDING = 10;
		final int MAX_COLUMNS = Math.min((this.width - 30) / (BUTTON_WIDTH + PADDING), 4);
		int columns = ModConfig.getInstance().numberOfColumns;
		columns = Math.clamp(columns, 1, MAX_COLUMNS);

		// Initialisation de la barre de recherche
		this.searchBar = new PlaceholderTextFieldWidget(this.textRenderer, this.width / 2 - 100, 20, 200, 20, Text.empty());
		this.searchBar.setChangedListener(this::onSearchUpdate); // Met à jour la liste lorsque le texte change
		this.searchBar.setPlaceholder(Text.translatable("better_hud.configuration_screen.searchbar_placeholder"));
		this.addDrawableChild(this.searchBar);

		// Initialisation du choix du nombre de colonnes
		CyclingButtonWidget<Integer> columnsButton = CyclingButtonWidget.<Integer>builder(value -> Text.literal(value.toString()))
				.values(IntStream.rangeClosed(1, MAX_COLUMNS).boxed().toList())
				.initially(columns)
				.build(Math.clamp(this.width / 2 + 105 + (int) (this.width / 100.0F * 5), this.width / 2 + 105, Math.max(this.width - 105, this.width / 2 + 105)), 20, 100, 20, Text.translatable("better_hud.configuration_screen.columns"), this::onColumnsUpdate);

		// Initialisation de la liste défilante
		this.featureList = new ScrollableFeatureList(this.client, this.width, this.height - 84, 50, BUTTON_HEIGHT + ICON_WIDTH_HEIGHT + PADDING, BUTTON_WIDTH, BUTTON_HEIGHT, ICON_WIDTH_HEIGHT, PADDING, columns);

		//Initialisation du bouton done
		ButtonWidget doneButton = ButtonWidget.builder(Text.translatable("better_hud.configuration_screen.done"), (btn) -> close())
				.dimensions(this.width / 2 - 80, this.height - 27, 160, 20)
				.build();


		this.addDrawableChild(columnsButton);
		this.addSelectableChild(this.featureList);
		this.addDrawableChild(doneButton);


		//Ajout des mods
		addMods(BUTTON_WIDTH, BUTTON_HEIGHT, columns);
	}

	public ScrollableFeatureList getFeatureList() {
		return this.featureList;
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		this.renderBackground(drawContext, mouseX, mouseY, delta);

		drawContext.drawCenteredTextWithShadow(textRenderer, Text.translatable("better_hud.configuration_screen"), this.width / 2, 7, 0xffffff);

		super.render(drawContext, mouseX, mouseY, delta);
		this.featureList.render(drawContext, mouseX, mouseY, delta);
	}

	@Override
	public void close() {
		ModConfig.saveConfig();
		client.setScreen(parent);
	}

	private void onColumnsUpdate(CyclingButtonWidget<Integer> integerCyclingButtonWidget, Integer columns) {
		this.featureList.setColumns(columns);

		this.featureList.children().clear();
		this.featureList.getAllEntries().clear();

		addMods(this.featureList.getButtonWidth(), this.featureList.getButtonHeight(), columns);

		onSearchUpdate(this.searchBar.getText());
	}

	private void onSearchUpdate(String text) {
		this.featureList.filterFeatures(text);
	}

	private void addMods(int buttonWidth, int buttonHeight, int columns) {
		List<Feature> features = new ArrayList<>();
		for (int i = 0; i < MODS_LIST.length; i++) {
			String modName = MODS_LIST[i];
			String modId = getModId(i);

			features.add(new Feature(modName, modId, getScreen(modId), buttonWidth, buttonHeight));

			if ((i + 1) % columns == 0) {
				this.featureList.addFeature(new ArrayList<>(features)); // copie de la liste
				features.clear();
			}
		}
		if (!features.isEmpty()) {
			this.featureList.addFeature(new ArrayList<>(features));
		}
	}

	private String getModId(int idx) {
		return new String[]{
				"coordinates",
				"fps",
				"clock",
				"armor_status",
				"direction",
				"day_counter",
				"ping",
				"server_address",
				"weather_changer",
				"memory_usage",
				"cps",
				"time_changer",
				"durability_ping",
				"speedometer",
				"reach",
				"combo",
				"playtime",
				"stopwatch",
				"shrieker_warning_level"
		}[idx];
	}

	private Runnable getScreen(String modId) {
		return switch (modId) {
			case "coordinates" ->
					() -> MinecraftClient.getInstance().setScreen(new Coordinates(this, featureList.getScrollY()));
			case "fps" -> () -> MinecraftClient.getInstance().setScreen(new FPS(this, featureList.getScrollY()));
			case "clock" -> () -> MinecraftClient.getInstance().setScreen(new Clock(this, featureList.getScrollY()));
			case "armor_status" ->
					() -> MinecraftClient.getInstance().setScreen(new ArmorStatus(this, featureList.getScrollY()));
			case "direction" ->
					() -> MinecraftClient.getInstance().setScreen(new Direction(this, featureList.getScrollY()));
			case "day_counter" ->
					() -> MinecraftClient.getInstance().setScreen(new DayCounter(this, featureList.getScrollY()));
			case "ping" -> () -> MinecraftClient.getInstance().setScreen(new Ping(this, featureList.getScrollY()));
			case "server_address" ->
					() -> MinecraftClient.getInstance().setScreen(new ServerAddress(this, featureList.getScrollY()));
			case "weather_changer" ->
					() -> MinecraftClient.getInstance().setScreen(new WeatherChanger(this, featureList.getScrollY()));
			case "memory_usage" ->
					() -> MinecraftClient.getInstance().setScreen(new MemoryUsage(this, featureList.getScrollY()));
			case "cps" -> () -> MinecraftClient.getInstance().setScreen(new CPS(this, featureList.getScrollY()));
			case "time_changer" ->
					() -> MinecraftClient.getInstance().setScreen(new TimeChanger(this, featureList.getScrollY()));
			case "durability_ping" ->
					() -> MinecraftClient.getInstance().setScreen(new DurabilityPing(this, featureList.getScrollY()));
			case "speedometer" ->
					() -> MinecraftClient.getInstance().setScreen(new Speedometer(this, featureList.getScrollY()));
			case "reach" -> () -> MinecraftClient.getInstance().setScreen(new Reach(this, featureList.getScrollY()));
			case "combo" -> () -> System.out.println("Mod Combo");
			case "playtime" ->
					() -> MinecraftClient.getInstance().setScreen(new Playtime(this, featureList.getScrollY()));
			case "stopwatch" -> () -> System.out.println("Mod Stopwatch");
			case "shrieker_warning_level" -> () -> System.out.println("Mod Shrieker Warning Level");
			default -> null;
		};
	}
}