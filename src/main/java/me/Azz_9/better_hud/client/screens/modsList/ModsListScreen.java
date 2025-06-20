package me.Azz_9.better_hud.client.screens.modsList;

import me.Azz_9.better_hud.client.configurableMods.JsonConfigHelper;
import me.Azz_9.better_hud.client.screens.AbstractBackNavigableScreen;
import me.Azz_9.better_hud.client.screens.widgets.textFieldWidget.PlaceholderTextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ModsListScreen extends AbstractBackNavigableScreen {

	private PlaceholderTextFieldWidget searchBar;
	private ScrollableModsList modsList;

	private final String[] MODS_LIST = new String[]{
			Text.translatable("better_hud.armor_status").getString(),
			Text.translatable("better_hud.cps").getString(),
			Text.translatable("better_hud.clock").getString(),
			Text.translatable("better_hud.durability_ping").getString(),
			Text.translatable("better_hud.weather_changer").getString(),
			Text.translatable("better_hud.time_changer").getString()
	};

	public ModsListScreen(Screen parent) {
		super(Text.translatable("better_hud.configuration_screen"), parent);
	}

	@Override
	protected void init() {
		final int BUTTON_WIDTH = 160;
		final int BUTTON_HEIGHT = 20;
		final int ICON_WIDTH_HEIGHT = 64;
		final int PADDING = 10;
		final int MAX_COLUMNS = Math.min(Math.min((this.width - 30) / (BUTTON_WIDTH + PADDING), 4), MODS_LIST.length);
		int columns = Math.clamp(JsonConfigHelper.getInstance().numberOfColumns, 1, MAX_COLUMNS);

		// Initialisation de la barre de recherche
		this.searchBar = new PlaceholderTextFieldWidget(this.textRenderer, this.width / 2 - 100, 20, 200, 20, Text.empty());
		this.searchBar.setChangedListener(this::onSearchUpdate); // Met à jour la liste lorsque le texte change
		this.searchBar.setPlaceholder(Text.translatable("better_hud.configuration_screen.searchbar_placeholder"));

		// Initialisation du choix du nombre de colonnes
		CyclingButtonWidget<Integer> columnsButton = CyclingButtonWidget.<Integer>builder(value -> Text.literal(value.toString()))
				.values(IntStream.rangeClosed(1, MAX_COLUMNS).boxed().toList())
				.initially(columns)
				.build(Math.clamp(this.width / 2 + 105 + (int) (this.width / 100.0F * 5), this.width / 2 + 105, Math.max(this.width - 105, this.width / 2 + 105)), 20, 100, 20, Text.translatable("better_hud.configuration_screen.columns"), this::onColumnsUpdate);

		// Initialisation de la liste défilante
		this.modsList = new ScrollableModsList(this.client, this.width, this.height - 84, 50, BUTTON_HEIGHT + ICON_WIDTH_HEIGHT + PADDING, BUTTON_WIDTH, BUTTON_HEIGHT, ICON_WIDTH_HEIGHT, PADDING, columns);

		//Initialisation du bouton done
		ButtonWidget doneButton = ButtonWidget.builder(Text.translatable("better_hud.configuration_screen.done"), (btn) -> close())
				.dimensions(this.width / 2 - 80, this.height - 27, 160, 20)
				.build();

		this.addDrawableChild(this.searchBar);
		this.addDrawableChild(columnsButton);
		this.addSelectableChild(this.modsList);
		this.addDrawableChild(doneButton);


		//Ajout des mods
		addMods(BUTTON_WIDTH, BUTTON_HEIGHT, columns);
	}

	public ScrollableModsList getModsList() {
		return this.modsList;
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		super.render(drawContext, mouseX, mouseY, delta);

		drawContext.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 7, 0xffffff);

		this.modsList.render(drawContext, mouseX, mouseY, delta);
	}

	@Override
	public void close() {
		JsonConfigHelper.saveConfig();
		super.close();
	}

	private void onColumnsUpdate(CyclingButtonWidget<Integer> integerCyclingButtonWidget, Integer columns) {
		this.modsList.setColumns(columns);

		this.modsList.children().clear();
		this.modsList.getAllEntries().clear();

		addMods(this.modsList.getButtonWidth(), this.modsList.getButtonHeight(), columns);

		onSearchUpdate(this.searchBar.getText());
	}

	private void onSearchUpdate(String text) {
		this.modsList.filterFeatures(text);
	}

	private void addMods(int buttonWidth, int buttonHeight, int columns) {
		List<Mod> mods = new ArrayList<>();
		for (int i = 0; i < MODS_LIST.length; i++) {
			String modName = MODS_LIST[i];
			String modId = getModId(i);

			mods.add(new Mod(modName, modId, getSetScreenRunnable(modId), buttonWidth, buttonHeight));

			if ((i + 1) % columns == 0) {
				this.modsList.addFeature(new ArrayList<>(mods)); // copie de la liste
				mods.clear();
			}
		}
		if (!mods.isEmpty()) {
			this.modsList.addFeature(new ArrayList<>(mods));
		}
	}

	private String getModId(int idx) {
		return new String[]{
				"armor_status",
				"cps",
				"clock",
				"durability_ping",
				"weather_changer",
				"time_changer",
				"coordinates",
				"fps",
				"direction",
				"day_counter",
				"ping",
				"server_address",
				"memory_usage",
				"speedometer",
				"reach",
				"combo",
				"playtime",
				"stopwatch",
				"shrieker_warning_level"
		}[idx];
	}

	private Runnable getSetScreenRunnable(String modId) {
		return switch (modId) {
			/*case "coordinates" ->
					() -> MinecraftClient.getInstance().setScreen(new Coordinates(this, modsList.getScrollY()));
			case "fps" -> () -> MinecraftClient.getInstance().setScreen(new FPS(this, modsList.getScrollY()));*/
			case "clock" -> () -> MinecraftClient.getInstance().setScreen(
					JsonConfigHelper.getInstance().clock.getConfigScreen(this, modsList.getScrollY())
			);
			case "armor_status" -> () -> MinecraftClient.getInstance().setScreen(
					JsonConfigHelper.getInstance().armorStatus.getConfigScreen(this, modsList.getScrollY())
			);
			/*case "direction" ->
					() -> MinecraftClient.getInstance().setScreen(new Direction(this, modsList.getScrollY()));
			case "day_counter" ->
					() -> MinecraftClient.getInstance().setScreen(new DayCounter(this, modsList.getScrollY()));
			case "ping" -> () -> MinecraftClient.getInstance().setScreen(new Ping(this, modsList.getScrollY()));
			case "server_address" ->
					() -> MinecraftClient.getInstance().setScreen(new ServerAddress(this, modsList.getScrollY()));
			case "memory_usage" ->
					() -> MinecraftClient.getInstance().setScreen(new MemoryUsage(this, modsList.getScrollY()));*/
			case "cps" -> () -> MinecraftClient.getInstance().setScreen(
					JsonConfigHelper.getInstance().cps.getConfigScreen(this, modsList.getScrollY())
			);
			case "time_changer" -> () -> MinecraftClient.getInstance().setScreen(
					JsonConfigHelper.getInstance().timeChanger.getConfigScreen(this, modsList.getScrollY())
			);
			case "weather_changer" -> () -> MinecraftClient.getInstance().setScreen(
					JsonConfigHelper.getInstance().weatherChanger.getConfigScreen(this, modsList.getScrollY())
			);
			case "durability_ping" -> () -> MinecraftClient.getInstance().setScreen(
					JsonConfigHelper.getInstance().durabilityPing.getConfigScreen(this, modsList.getScrollY())
			);
			/*case "speedometer" ->
					() -> MinecraftClient.getInstance().setScreen(new Speedometer(this, modsList.getScrollY()));
			case "reach" -> () -> MinecraftClient.getInstance().setScreen(new Reach(this, modsList.getScrollY()));
			case "combo" -> () -> System.out.println("Mod Combo");
			case "playtime" -> () -> MinecraftClient.getInstance().setScreen(new Playtime(this, modsList.getScrollY()));
			case "stopwatch" -> () -> System.out.println("Mod Stopwatch");
			case "shrieker_warning_level" ->
					() -> MinecraftClient.getInstance().setScreen(new ShriekerWarningLevel(this, modsList.getScrollY()));*/
			default -> () -> System.out.println(modId);
		};
	}
}
