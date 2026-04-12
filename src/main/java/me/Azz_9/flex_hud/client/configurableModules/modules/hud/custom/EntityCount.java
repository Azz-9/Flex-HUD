package me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractTextModule;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ColorButtonEntry;
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
		setHeight(CLIENT.textRenderer.fontHeight);
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.entity_count");
	}

	@Override
	public String getID() {
		return "entity_count";
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldNotRender()) {
			return;
		}

		String text = getText();

		setWidth(text);

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(getRoundedX(), getRoundedY());
		matrices.scale(getScale());

		drawBackground(context);

		context.drawText(CLIENT.textRenderer, text, 0, 0, getColor(), this.shadow.getValue());

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

		return Text.translatable(translationKey, entityCount, range.getValue(), yRange.getValue()).getString();
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
		PlayerEntity player = CLIENT.player;
		if (player != null) {
			int radius = ModulesHelper.getInstance().entityCount.range.getValue();
			int yRadius = ModulesHelper.getInstance().entityCount.yRange.getValue();
			return entity.getEntityPos().isWithinRangeOf(player.getEntityPos(), radius, yRadius);
		}
		return false;
	}

	@Override
	public void tick() {
		if (CLIENT.world == null || CLIENT.player == null) {
			return;
		}

		EntityCount.entityCount = 0;
		for (Entity entity : CLIENT.world.getEntities()) {
			if (entity != CLIENT.player && isInRange(entity)) {

				if (onlyMobs.getValue()) {
					if (!(entity instanceof MobEntity)) continue;
				} else if (onlyItems.getValue()) {
					if (!(entity instanceof ItemEntity)) continue;
				}

				EntityCount.entityCount++;
			}
		}
	}
}
