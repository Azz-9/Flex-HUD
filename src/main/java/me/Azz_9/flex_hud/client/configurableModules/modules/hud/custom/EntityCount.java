package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntFieldEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;

public class EntityCount extends AbstractTextModule implements TickableModule {

	private final ConfigBoolean onlyMobs = new ConfigBoolean(false, "flex_hud.entity_count.config.only_mbos");
	private final ConfigBoolean onlyItems = new ConfigBoolean(false, "flex_hud.entity_count.config.only_items");
	private final ConfigInteger range = new ConfigInteger(16, "flex_hud.entity_count.config.range", 0, 512);
	private final ConfigInteger yRange = new ConfigInteger(16, "flex_hud.entity_count.config.y_range", 0, 512);
	public static int entityCount = 0;

	public EntityCount(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
		this.enabled.setConfigTextTranslationKey("flex_hud.entity_count.config.enable");

		ConfigRegistry.register(getID(), "onlyMobs", onlyMobs);
		ConfigRegistry.register(getID(), "onlyItems", onlyItems);
		ConfigRegistry.register(getID(), "range", range);
		ConfigRegistry.register(getID(), "yRange", yRange);
	}

	@Override
	public void init() {
		setHeight(MINECRAFT.font.lineHeight);
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
	public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
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

		graphics.text(MINECRAFT.font, text, 0, 0, getColor(), this.shadow.getValue());

		matrices.popMatrix();
	}

	private String getText() {
		int entityCount = Flex_hudClient.isInMoveElementScreen ? 10 : EntityCount.entityCount;

		String translationKey;
		if (onlyMobs.getValue()) {
			translationKey = "flex_hud.entity_count.text.mobs";
		} else if (onlyItems.getValue()) {
			translationKey = "flex_hud.entity_count.text.items";
		} else {
			translationKey = "flex_hud.entity_count.text.entities";
		}

		if (!range.getValue().equals(yRange.getValue())) {
			translationKey += ".different_y";
		}

		return Component.translatable(translationKey, entityCount, range.getValue(), yRange.getValue()).getString();
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
								.build(),
						new IntFieldEntry.Builder()
								.setIntFieldWidth(30)
								.setVariable(yRange)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}

	public static boolean isInRange(Entity entity) {
		LocalPlayer player = MINECRAFT.player;
		if (player != null) {
			int radius = ModulesHelper.getInstance().entityCount.range.getValue();
			int yRadius = ModulesHelper.getInstance().entityCount.yRange.getValue();
			return entity.position().closerThan(player.position(), radius, yRadius);
		}
		return false;
	}

	@Override
	public void tick() {
		if (MINECRAFT.level == null || MINECRAFT.player == null) {
			return;
		}

		EntityCount.entityCount = 0;
		for (Entity entity : MINECRAFT.level.entitiesForRendering()) {
			if (entity != MINECRAFT.player && isInRange(entity)) {

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
