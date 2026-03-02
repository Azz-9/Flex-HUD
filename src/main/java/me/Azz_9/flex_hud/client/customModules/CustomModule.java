package me.Azz_9.flex_hud.client.customModules;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;

public class CustomModule extends AbstractTextModule {


	private List<Token> tokens = new ArrayList<>();
	private final @NonNull String text;

	private @NonNull String name;

	private CustomModule(@NonNull String name, @NonNull String text) {
		super(CustomModuleRegistry.nameToId(name), 0, 0, AnchorPosition.START, AnchorPosition.START);
		this.name = name;
		this.text = text;

		//TODO on peut pas créer un module custom avec un nom qu'un module existant utilise déjà
		// TODO peut être faire en sorte que si un utilisateur créé un module avec un nom et que une nouversion du mod ajoute un module avec le même nom, alors ça renomme le module custom avec (1) ou quelque chose comme ça
	}

	public static CustomModule fromText(@NonNull String id, @NonNull String text) {
		CustomModule module = new CustomModule(id, text);

		module.tokens = TokenParser.parseText(text);

		module.init();
		return module;
	}

	@Override
	public void init() {
		setHeight(CLIENT.textRenderer.fontHeight);
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender()) {
			return;
		}

		setWidth(0);

		for (Token token : tokens) {
			setWidth(token.getString(), getWidth());
		}

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(context);

		int hudX = 0;
		for (Token token : tokens) {
			context.drawText(
					CLIENT.textRenderer,
					token.getString(),
					hudX, 0,
					getColor(),
					shadow.getValue()
			);

			hudX += CLIENT.textRenderer.getWidth(token.getString());
		}

		matrices.popMatrix();
	}

	@Override
	public Text getName() {
		return Text.of(name);
	}

	public void setName(@NonNull String name) {
		setId("custom_module-" + name.toLowerCase().replace(' ', '_'));
		this.name = name;
	}

	public @NonNull String getText() {
		return text;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (CLIENT.getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 160;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(shadow)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(chromaColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(color)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), true)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(drawBackground)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
				this.addAllEntries(
						new ColorButtonEntry.Builder()
								.setColorButtonWidth(buttonWidth)
								.setVariable(backgroundColor)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addDependency(this.getConfigList().getLastEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(hideInF3)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}
}
