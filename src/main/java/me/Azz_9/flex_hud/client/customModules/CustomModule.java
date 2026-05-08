package me.Azz_9.flex_hud.client.customModules;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.customModules.template.CompiledCustomText;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;

public class CustomModule extends AbstractTextModule {


	private CompiledCustomText compiledText = CompiledCustomText.compile("");
	private @NonNull String text;

	private @NonNull String name;

	private CustomModule(@NonNull String name, @NonNull String text) {
		super(CustomModuleRegistry.nameToId(name), 0, 0, AnchorPosition.START, AnchorPosition.START);
		this.name = name;
		this.text = text;
	}

	public static CustomModule fromText(@NonNull String id, @NonNull String text) {
		CustomModule module = new CustomModule(id, text);

		module.compiledText = CompiledCustomText.compile(text);

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

		CompiledCustomText.RenderData renderData = compiledText.getRenderData();
		setWidth(renderData.width());

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(context);

		context.drawText(
				CLIENT.textRenderer,
				renderData.text(),
				0, 0,
				renderData.hasOwnColors() ? 0xffffffff : getColor(),
				shadow.getValue()
		);

		matrices.popMatrix();
	}

	@Override
	public Text getName() {
		return Text.of(name);
	}

	public void update(@NonNull String name, @NonNull String text) {
		String newId = CustomModuleRegistry.nameToId(name);
		if (!getID().equals(newId)) {
			ConfigRegistry.renameModule(getID(), newId);
			setId(newId);
		}

		this.name = name;
		this.text = text;
		this.compiledText = CompiledCustomText.compile(text);
		init();
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
