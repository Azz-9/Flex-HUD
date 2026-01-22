package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

public class EntityCount extends AbstractTextModule implements TickableModule {

	private final ConfigBoolean onlyMobs = new ConfigBoolean(false, "flex_hud.entity_count.config.only_mbos");
	private final ConfigBoolean onlyItems = new ConfigBoolean(false, "flex_hud.entity_count.config.only_items");
	private final ConfigInteger range = new ConfigInteger(16, "flex_hud.entity_count.config.range", 0, 512);
	public static int entityCount = 0;

	public EntityCount(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setValue(false);
		this.enabled.setDefaultValue(false);
		this.enabled.setConfigTextTranslationKey("flex_hud.entity_count.config.enable");
	}

	@Override
	public void init() {
		setHeight(Minecraft.getInstance().font.lineHeight);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.entity_count");
	}

	@Override
	public String getID() {
		return "entity_count";
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();

		if (shouldNotRender()) {
			return;
		}

		String text = getText();

		setWidth(text);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(graphics);

		graphics.drawString(minecraft.font, text, 0, 0, getColor(), this.shadow.getValue());

		matrices.popMatrix();
	}

	private String getText() {
		int entityCount = Flex_hudClient.isInMoveElementScreen ? 10 : EntityCount.entityCount;

		String text;
		if (onlyMobs.getValue()) {
			text = Component.translatable("flex_hud.entity_count.text.mobs", entityCount, range.getValue()).getString();
		} else if (onlyItems.getValue()) {
			text = Component.translatable("flex_hud.entity_count.text.items", entityCount, range.getValue()).getString();
		} else {
			text = Component.translatable("flex_hud.entity_count.text.entities", entityCount, range.getValue()).getString();
		}
		return text;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
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
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(onlyMobs)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(onlyItems)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build(),
						new IntFieldEntry.Builder()
								.setIntFieldWidth(30)
								.setVariable(range)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}

	public static boolean isInRange(Entity entity) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			int radius = ModulesHelper.getInstance().entityCount.range.getValue();
			return entity.position().closerThan(player.position(), radius);
		}
		return false;
	}

	@Override
	public void tick() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null || minecraft.player == null) {
			return;
		}

		EntityCount.entityCount = 0;
		for (Entity entity : minecraft.level.entitiesForRendering()) {
			if (entity != minecraft.player && isInRange(entity)) {

				if (onlyMobs.getValue()) {
					if (!(entity instanceof Mob)) continue;
				} else if (onlyItems.getValue()) {
					if (!(entity instanceof ItemEntity)) continue;
				}

				EntityCount.entityCount++;
			}
		}
	}
}
