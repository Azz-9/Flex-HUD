package me.Azz_9.flex_hud.client.gui.screens;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import me.Azz_9.flex_hud.client.config.ConfigLoader;
import me.Azz_9.flex_hud.client.config.Configurable;
import me.Azz_9.flex_hud.client.gui.components.CustomEditBox;
import me.Azz_9.flex_hud.client.gui.components.config.modulesList.CustomModuleElement;
import me.Azz_9.flex_hud.client.gui.components.config.modulesList.ModuleElement;
import me.Azz_9.flex_hud.client.gui.components.config.modulesList.ScrollableModulesList;
import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.customModules.CustomModule;
import me.Azz_9.flex_hud.client.modules.customModules.CustomModuleRegistry;
import me.Azz_9.flex_hud.client.modules.customModules.CustomModulesPersistence;

public class ModulesListScreen extends AbstractBackNavigableScreen {

	private CustomEditBox searchBar;
	private ScrollableModulesList modulesListWidget;
	private CycleButton<Integer> columnsButton;

	private final List<Configurable> MODULES_LIST = Modules.getConfigurableModules();

	public ModulesListScreen(Screen parent) {
		super(Component.translatable("flex_hud.configuration_screen"), parent);
	}

	@Override
	protected void init() {
		final int BUTTON_WIDTH = 140;
		final int BUTTON_HEIGHT = 20;
		final int ICON_WIDTH_HEIGHT = 64;
		final int PADDING = 10;
		final int MAX_COLUMNS = Math.min((this.width - 30) / (BUTTON_WIDTH + PADDING), MODULES_LIST.size());
		int columns = Math.clamp(Modules.getInstance().numberOfColumns.getValue(), 1, MAX_COLUMNS);

		// Initialisation de la barre de recherche
		this.searchBar = new CustomEditBox(this.font, this.width / 2 - 100, 20, 200, 20, Component.empty());
		this.searchBar.setResponder(this::onSearchUpdate); // Met à jour la liste lorsque le texte change
		this.searchBar.setPlaceholder(Component.translatable("flex_hud.configuration_screen.searchbar_placeholder"));

		// Initialisation du bouton pour créer un module
		Button createModuleButton = Button.builder(Component.translatable("flex_hud.configuration_screen.create_module"), (button) -> {
					CreateModuleScreen createModuleScreen = new CreateModuleScreen(this);
					createModuleScreen.setParentScrollAmount(getModulesListWidget().scrollAmount());
					MINECRAFT.setScreen(createModuleScreen);
				}).bounds(Math.clamp(
						this.width / 2 - 105 - (int) (this.width / 100.0F * 5),
						Math.min(105, this.width / 2 - 105),
						this.width / 2 - 105
				) - 100, 20, 100, 20)
				.build();

		// Initialisation du choix du nombre de colonnes
		columnsButton = CycleButton.<Integer>builder(value -> Component.literal(value.toString()))
				.withInitialValue(columns)
				.withValues(IntStream.rangeClosed(1, MAX_COLUMNS).boxed().toList())
				.create(Math.clamp(
						this.width / 2 + 105 + (int) (this.width / 100.0F * 5),
						this.width / 2 + 105,
						Math.max(this.width - 105, this.width / 2 + 105)
				), 20, 100, 20, Component.translatable("flex_hud.configuration_screen.columns"), this::onColumnsUpdate);

		// Initialisation de la liste défilante
		this.modulesListWidget = new ScrollableModulesList(this.minecraft, this.width, this.height - 84, 50, BUTTON_HEIGHT + ICON_WIDTH_HEIGHT + PADDING, BUTTON_WIDTH, BUTTON_HEIGHT, ICON_WIDTH_HEIGHT, PADDING, columns);

		//Initialisation du bouton done
		Button doneButton = Button.builder(CommonComponents.GUI_DONE, (btn) -> onClose())
				.bounds(this.width / 2 - 80, this.height - 27, 160, 20)
				.build();

		this.addRenderableWidget(createModuleButton);
		this.addRenderableWidget(this.searchBar);
		this.addRenderableWidget(columnsButton);
		this.addWidget(this.modulesListWidget);
		this.addRenderableWidget(doneButton);


		//Ajout des modules
		addMods(BUTTON_WIDTH, BUTTON_HEIGHT, columns);
	}

	public ScrollableModulesList getModulesListWidget() {
		return this.modulesListWidget;
	}

	@Override
	public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.render(graphics, mouseX, mouseY, delta);

		graphics.drawCenteredString(font, title, this.width / 2, 7, 0xffffffff);

		this.modulesListWidget.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		CustomModulesPersistence.saveConfig();
		ConfigLoader.saveConfig();
		super.onClose();
	}

	private void onColumnsUpdate(CycleButton<Integer> integerCyclingButtonWidget, Integer columns) {
		this.modulesListWidget.setColumns(columns);

		this.modulesListWidget.clearEntries();

		addMods(this.modulesListWidget.getButtonWidth(), this.modulesListWidget.getButtonHeight(), columns);

		onSearchUpdate(this.searchBar.getValue());
	}

	private void onSearchUpdate(String text) {
		this.modulesListWidget.filterModules(text);
	}

	public void refreshModulesList() {
		modulesListWidget.clearEntries();
		addMods(this.modulesListWidget.getButtonWidth(), this.modulesListWidget.getButtonHeight(), columnsButton.getValue());
		onSearchUpdate(this.searchBar.getValue());
	}

	private void addMods(int buttonWidth, int buttonHeight, int columns) {
		List<ModuleElement> modules = new ArrayList<>();
		for (int i = 0; i < MODULES_LIST.size(); i++) {
			Configurable module = MODULES_LIST.get(i);

			if (module instanceof CustomModule customModule) {
				modules.add(new CustomModuleElement(
						customModule,
						buttonWidth,
						buttonHeight,
						this,
						module::getTooltip,
						ImmutableList.copyOf(module.getKeywords()),
						() -> {
							CustomModuleRegistry.unregister(customModule);
							refreshModulesList();
						}
				));
			} else {
				modules.add(new ModuleElement(
						module,
						buttonWidth,
						buttonHeight,
						this,
						module::getTooltip,
						ImmutableList.copyOf(module.getKeywords())
				));
			}

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
