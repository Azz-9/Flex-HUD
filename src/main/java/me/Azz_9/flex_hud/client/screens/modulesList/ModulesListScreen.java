package me.Azz_9.flex_hud.client.screens.modulesList;

import me.Azz_9.flex_hud.client.configurableModules.ConfigLoader;
import me.Azz_9.flex_hud.client.configurableModules.Configurable;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.screens.AbstractBackNavigableScreen;
import me.Azz_9.flex_hud.client.screens.widgets.textFieldWidget.PlaceholderTextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ModulesListScreen extends AbstractBackNavigableScreen {

	private PlaceholderTextFieldWidget searchBar;
	private ScrollableModulesList modulesListWidget;

	private final List<Configurable> MODULES_LIST = ModulesHelper.getConfigurableModules();

	public ModulesListScreen(Screen parent) {
		super(Text.translatable("flex_hud.configuration_screen"), parent);
	}

	@Override
	protected void init() {
		final int BUTTON_WIDTH = 140;
		final int BUTTON_HEIGHT = 20;
		final int ICON_WIDTH_HEIGHT = 64;
		final int PADDING = 10;
		final int MAX_COLUMNS = Math.min((this.width - 30) / (BUTTON_WIDTH + PADDING), MODULES_LIST.size());
		int columns = Math.clamp(ModulesHelper.getInstance().numberOfColumns.getValue(), 1, MAX_COLUMNS);

		// Initialisation de la barre de recherche
		this.searchBar = new PlaceholderTextFieldWidget(this.textRenderer, this.width / 2 - 100, 20, 200, 20, Text.empty());
		this.searchBar.setChangedListener(this::onSearchUpdate); // Met à jour la liste lorsque le texte change
		this.searchBar.setPlaceholder(Text.translatable("flex_hud.configuration_screen.searchbar_placeholder"));

		// Initialisation du choix du nombre de colonnes
		CyclingButtonWidget<Integer> columnsButton = CyclingButtonWidget.<Integer>builder(value -> Text.literal(value.toString()))
				.values(IntStream.rangeClosed(1, MAX_COLUMNS).boxed().toList())
				.initially(columns)
				.build(Math.clamp(this.width / 2 + 105 + (int) (this.width / 100.0F * 5), this.width / 2 + 105, Math.max(this.width - 105, this.width / 2 + 105)), 20, 100, 20, Text.translatable("flex_hud.configuration_screen.columns"), this::onColumnsUpdate);

		// Initialisation de la liste défilante
		this.modulesListWidget = new ScrollableModulesList(this.client, this.width, this.height - 84, 50, BUTTON_HEIGHT + ICON_WIDTH_HEIGHT + PADDING, BUTTON_WIDTH, BUTTON_HEIGHT, ICON_WIDTH_HEIGHT, PADDING, columns);

		//Initialisation du bouton done
		ButtonWidget doneButton = ButtonWidget.builder(Text.translatable("flex_hud.configuration_screen.done"), (btn) -> close())
				.dimensions(this.width / 2 - 80, this.height - 27, 160, 20)
				.build();

		this.addDrawableChild(this.searchBar);
		this.addDrawableChild(columnsButton);
		this.addSelectableChild(this.modulesListWidget);
		this.addDrawableChild(doneButton);


		//Ajout des modules
		addMods(BUTTON_WIDTH, BUTTON_HEIGHT, columns);
	}

	public ScrollableModulesList getModulesListWidget() {
		return this.modulesListWidget;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		context.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 7, 0xffffffff);

		this.modulesListWidget.render(context, mouseX, mouseY, delta);
	}

	@Override
	public void close() {
		ConfigLoader.saveConfig();
		super.close();
	}

	private void onColumnsUpdate(CyclingButtonWidget<Integer> integerCyclingButtonWidget, Integer columns) {
		this.modulesListWidget.setColumns(columns);

		this.modulesListWidget.clearEntries();

		addMods(this.modulesListWidget.getButtonWidth(), this.modulesListWidget.getButtonHeight(), columns);

		onSearchUpdate(this.searchBar.getText());
	}

	private void onSearchUpdate(String text) {
		this.modulesListWidget.filterModules(text);
	}

	private void addMods(int buttonWidth, int buttonHeight, int columns) {
		List<Module> modules = new ArrayList<>();
		for (int i = 0; i < MODULES_LIST.size(); i++) {
			String moduleId = MODULES_LIST.get(i).getID();

			Supplier<Tooltip> getTooltip = switch (moduleId) {
				case "in_game_time", "day_counter" ->
						() -> ModulesHelper.getInstance().timeChanger.enabled.getValue() ? Tooltip.of(Text.literal("⚠ ").append(Text.translatable("flex_hud.configuration_screen.module_compatibility_warning")).append(Text.translatable("flex_hud.time_changer")).formatted(Formatting.RED)) : null;
				case "weather_display" ->
						() -> ModulesHelper.getInstance().weatherChanger.enabled.getValue() ? Tooltip.of(Text.literal("⚠ ").append(Text.translatable("flex_hud.configuration_screen.module_compatibility_warning")).append(Text.translatable("flex_hud.weather_changer")).formatted(Formatting.RED)) : null;
				default -> null;
			};
			modules.add(new Module(
							MODULES_LIST.get(i).getName().getString(),
							moduleId,
							MODULES_LIST.get(i).getConfigScreen(this),
							buttonWidth,
							buttonHeight,
							this,
							getTooltip
					)
			);

			if ((i + 1) % columns == 0) {
				this.modulesListWidget.addModule(modules); // copie de la liste
				modules = new ArrayList<>();
			}
		}
		if (!modules.isEmpty()) {
			this.modulesListWidget.addModule(new ArrayList<>(modules));
		}
	}
}
