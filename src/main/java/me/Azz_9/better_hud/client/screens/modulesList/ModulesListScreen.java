package me.Azz_9.better_hud.client.screens.modulesList;

import me.Azz_9.better_hud.client.configurableModules.Configurable;
import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.better_hud.client.screens.AbstractBackNavigableScreen;
import me.Azz_9.better_hud.client.screens.widgets.textFieldWidget.PlaceholderTextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ModulesListScreen extends AbstractBackNavigableScreen {

	private PlaceholderTextFieldWidget searchBar;
	private ScrollableModulesList modulesList;

	private final List<Configurable> MODULES_LIST = JsonConfigHelper.getConfigurableModules();

	public ModulesListScreen(Screen parent) {
		super(Text.translatable("better_hud.configuration_screen"), parent);
	}

	@Override
	protected void init() {
		final int BUTTON_WIDTH = 140;
		final int BUTTON_HEIGHT = 20;
		final int ICON_WIDTH_HEIGHT = 64;
		final int PADDING = 10;
		final int MAX_COLUMNS = Math.min((this.width - 30) / (BUTTON_WIDTH + PADDING), MODULES_LIST.size());
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
		this.modulesList = new ScrollableModulesList(this.client, this.width, this.height - 84, 50, BUTTON_HEIGHT + ICON_WIDTH_HEIGHT + PADDING, BUTTON_WIDTH, BUTTON_HEIGHT, ICON_WIDTH_HEIGHT, PADDING, columns);

		//Initialisation du bouton done
		ButtonWidget doneButton = ButtonWidget.builder(Text.translatable("better_hud.configuration_screen.done"), (btn) -> close())
				.dimensions(this.width / 2 - 80, this.height - 27, 160, 20)
				.build();

		this.addDrawableChild(this.searchBar);
		this.addDrawableChild(columnsButton);
		this.addSelectableChild(this.modulesList);
		this.addDrawableChild(doneButton);


		//Ajout des modules
		addMods(BUTTON_WIDTH, BUTTON_HEIGHT, columns);
	}

	public ScrollableModulesList getModulesList() {
		return this.modulesList;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		context.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 7, 0xffffffff);

		this.modulesList.render(context, mouseX, mouseY, delta);
	}

	@Override
	public void close() {
		JsonConfigHelper.saveConfig();
		super.close();
	}

	private void onColumnsUpdate(CyclingButtonWidget<Integer> integerCyclingButtonWidget, Integer columns) {
		this.modulesList.setColumns(columns);

		this.modulesList.children().clear();
		this.modulesList.getAllEntries().clear();

		addMods(this.modulesList.getButtonWidth(), this.modulesList.getButtonHeight(), columns);

		onSearchUpdate(this.searchBar.getText());
	}

	private void onSearchUpdate(String text) {
		this.modulesList.filterModules(text);
	}

	private void addMods(int buttonWidth, int buttonHeight, int columns) {
		List<Module> modules = new ArrayList<>();
		for (int i = 0; i < MODULES_LIST.size(); i++) {
			Text moduleName = MODULES_LIST.get(i).getName();
			String moduleId = MODULES_LIST.get(i).getID();


			modules.add(new Module(
					moduleName.getString(),
					moduleId,
					MODULES_LIST.get(i).getConfigScreen(this),
					buttonWidth,
					buttonHeight,
					this)
			);

			if ((i + 1) % columns == 0) {
				this.modulesList.addModule(new ArrayList<>(modules)); // copie de la liste
				modules.clear();
			}
		}
		if (!modules.isEmpty()) {
			this.modulesList.addModule(new ArrayList<>(modules));
		}
	}
}
