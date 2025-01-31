package me.Azz_9.better_hud.Screens.ModsList;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods.*;
import me.Azz_9.better_hud.Screens.widgets.fields.PlaceholderTextFieldWidget;
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

@Environment(EnvType.CLIENT)
public class ConfigurationScreen extends Screen {
	private final Screen parent;

	private PlaceholderTextFieldWidget searchBar;
	private ScrollableFeatureList featureList;

	private final List<String> modsList = new ArrayList<>() {{
		add("Coordinates");
		add("FPS");
		add("Clock");
		add("Armor status");
		add("Direction");
		add("Day Counter");
		add("Ping");
		add("Server address");
		add("Weather changer");
		add("Memory usage");
		add("CPS");
		add("Time changer");
		add("Durability ping");
		add("Speedometer");
		add("Reach");
		add("Combo (HS)");
		add("playtime");
		add("stopwatch (HS)");
		add("shrieker warning level (HS)");
	}};

	public ConfigurationScreen(Screen parent) {
		super(Text.literal("Mods Configuration"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		final int buttonWidth = 160;
		final int buttonHeight = 20;
		final int iconWidthHeight = 64;
		final int padding = 10;
		int columns = ModConfig.getInstance().numberOfColumns;
		if (columns > 4 || columns < 1) {
			columns = 2;
		}

		// Initialisation de la barre de recherche
		this.searchBar = new PlaceholderTextFieldWidget(this.textRenderer, this.width / 2 - 100, 20, 200, 20, Text.empty());
		this.searchBar.setChangedListener(this::onSearchUpdate); // Met à jour la liste lorsque le texte change
		this.searchBar.setPlaceholder(Text.of("Search..."));
		this.addDrawableChild(this.searchBar);

		// Initialisation du choix du nombre de colonnes
		CyclingButtonWidget<Integer> columnsButton = CyclingButtonWidget.<Integer>builder(value -> Text.literal(value.toString()))
				.values(1, 2, 3, 4)
				.initially(columns)
				.build(this.width / 2 + 150, 20, 100, 20, Text.literal("Columns "), this::onColumnsUpdate);

		// Initialisation de la liste défilante
		this.featureList = new ScrollableFeatureList(this.client, this.width, this.height - 84, 50, buttonHeight + iconWidthHeight + padding, buttonWidth, buttonHeight, iconWidthHeight, padding, columns);

		//Initialisation du bouton done
		ButtonWidget doneButton = ButtonWidget.builder(Text.of("Done"), (btn) -> close())
				.dimensions(this.width / 2 - 80, this.height - 27, 160, 20)
				.build();


		this.addDrawableChild(columnsButton);
		this.addSelectableChild(this.featureList);
		this.addDrawableChild(doneButton);


		//Ajout des mods
		addMods(buttonWidth, buttonHeight, columns);
	}

	public ScrollableFeatureList getFeatureList() {
		return this.featureList;
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		this.renderBackground(drawContext, mouseX, mouseY, delta);

		drawContext.drawCenteredTextWithShadow(textRenderer, Text.literal("Mods Configuration"), this.width / 2, 7, 0xffffff);

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
		for (int i = 0; i < modsList.size(); i++) {
			String modName = modsList.get(i);
			String modId = getModId(modName);

			features.add(new Feature(modName, modId, getScreen(modId), buttonWidth, buttonHeight));

			if ((i+1) % columns == 0) {
				this.featureList.addFeature(new ArrayList<>(features)); // copie de la liste
				features.clear();
			}
		}
		if (!features.isEmpty()) {
			this.featureList.addFeature(new ArrayList<>(features));
		}
	}

	private String getModId(String modName) {
		modName = modName.replace(" (HS)", "");
		modName = modName.replace(" ", "_");
		modName = modName.toLowerCase();
		return modName;
	}

	private Runnable getScreen(String modId) {
		switch (modId) {
			case "coordinates":
				return () -> MinecraftClient.getInstance().setScreen(new Coordinates(this, featureList.getScrollY()));
            case "fps":
                return () -> MinecraftClient.getInstance().setScreen(new FPS(this, featureList.getScrollY()));
            case "clock":
                return () -> MinecraftClient.getInstance().setScreen(new Clock(this, featureList.getScrollY()));
            case "armor_status":
                return () -> MinecraftClient.getInstance().setScreen(new ArmorStatus(this, featureList.getScrollY()));
            case "direction":
                return () -> MinecraftClient.getInstance().setScreen(new Direction(this, featureList.getScrollY()));
            case "day_counter":
                return () -> MinecraftClient.getInstance().setScreen(new DayCounter(this, featureList.getScrollY()));
            case "ping":
                return () -> MinecraftClient.getInstance().setScreen(new Ping(this, featureList.getScrollY()));
            case "server_address":
                return () -> MinecraftClient.getInstance().setScreen(new ServerAddress(this, featureList.getScrollY()));
            case "weather_changer":
                return () -> MinecraftClient.getInstance().setScreen(new WeatherChanger(this, featureList.getScrollY()));
			case "memory_usage":
				return () -> MinecraftClient.getInstance().setScreen(new MemoryUsage(this, featureList.getScrollY()));
            case "cps":
				return () -> MinecraftClient.getInstance().setScreen(new CPS(this, featureList.getScrollY()));
			case "time_changer":
				return () -> MinecraftClient.getInstance().setScreen(new TimeChanger(this, featureList.getScrollY()));
			case "durability_ping":
				return () -> MinecraftClient.getInstance().setScreen(new DurabilityPing(this, featureList.getScrollY()));
			case "speedometer":
				return () -> MinecraftClient.getInstance().setScreen(new Speedometer(this, featureList.getScrollY()));
			case "reach":
				return () -> MinecraftClient.getInstance().setScreen(new Reach(this, featureList.getScrollY()));
			case "combo":
				return () -> System.out.println("Mod Combo");
			case "playtime":
				return () -> MinecraftClient.getInstance().setScreen(new Playtime(this, featureList.getScrollY()));
			case "stopwatch":
				return () -> System.out.println("Mod Stopwatch");
			case "shrieker_warning_level":
				return () -> System.out.println("Mod Shrieker Warning Level");

		}
		return null;
	}
}