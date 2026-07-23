package me.Azz_9.flex_hud.client.modules.customModules;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.customModules.template.CompiledCustomText;
import me.Azz_9.flex_hud.client.modules.hud.AbstractTextModule;

public class CustomModule extends AbstractTextModule {

	private CompiledCustomText compiledText = CompiledCustomText.compile("");
	private @NotNull String text;

	private @NotNull String name;

	private CustomModule(@NotNull String name, @NotNull String text) {
		super(CustomModuleRegistry.nameToId(name), 0, 0, AnchorPosition.START, AnchorPosition.START);
		// custom modules are enabled by default when created
		this.enabled.setDefaultValue(true);
		this.enabled.setValue(true);

		this.name = name;
		this.text = text;
	}

	public static CustomModule fromText(@NotNull String id, @NotNull String text) {
		CustomModule module = new CustomModule(id, text);

		module.replaceCompiledText(CompiledCustomText.compile(text));

		module.init();
		return module;
	}

	@Override
	public void init() {
		setHeight(MINECRAFT.font.lineHeight);
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker tickCounter) {
		if (shouldNotRender()) {
			return;
		}

		CompiledCustomText.RenderData renderData = compiledText.getRenderData();
		setWidth(renderData.width());

		PoseStack matrices = graphics.pose();
		matrices.pushPose();
		matrices.translate(getRoundedX(), getRoundedY(), 0);
		matrices.scale(getScale(), getScale(), 1);

		drawBackground(graphics);

		graphics.drawString(
				MINECRAFT.font,
				renderData.text(),
				0, 0,
				renderData.hasOwnColors() ? 0xffffffff : getColor(),
				shadow.getValue()
		);

		matrices.popPose();
	}

	@Override
	public Component getName() {
		return Component.literal(name);
	}

	public void update(@NotNull String name, @NotNull String text) {
		String newId = CustomModuleRegistry.nameToId(name);
		if (!getID().equals(newId)) {
			ConfigRegistry.renameModule(getID(), newId);
			setId(newId);
		}

		this.name = name;
		this.text = text;
		replaceCompiledText(CompiledCustomText.compile(text));
		init();
	}

	public @NotNull String getText() {
		return text;
	}

	public void recompile() {
		replaceCompiledText(CompiledCustomText.compile(text));
	}

	public void unload() {
		compiledText.close();
	}

	private void replaceCompiledText(CompiledCustomText newCompiledText) {
		CompiledCustomText oldCompiledText = compiledText;
		compiledText = newCompiledText;
		oldCompiledText.close();
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
				if (MINECRAFT.getLanguageManager().getSelected().equals("fr_fr")) {
					buttonWidth = 160;
				}

				super.initContent();

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
								.build(),
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeX)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeX(anchorModeX.getValue()))
								.build(),
						new CyclingButtonEntry.Builder<AnchorMode>()
								.setCyclingButtonWidth(80)
								.setVariable(anchorModeY)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.addObserver((getter) -> setAnchorModeY(anchorModeY.getValue()))
								.build()
				);
			}
		};
	}
}
