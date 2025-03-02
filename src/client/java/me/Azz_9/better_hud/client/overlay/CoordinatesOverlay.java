package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.DisplayMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CoordinatesOverlay extends HudElement {
    public boolean showY = true;
    public int numberOfDigits = 0;
    public boolean showBiome = true;
    public boolean showDirection = true;
    public boolean directionAbreviation = true;
    public DisplayMode displayMode = DisplayMode.Vertical;

    public CoordinatesOverlay(double defaultX, double defaultY) {
        super(defaultX, defaultY);
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);

        final MinecraftClient CLIENT = MinecraftClient.getInstance();

        if(!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden || CLIENT.player == null) {
            return;
        }

        PlayerEntity player = CLIENT.player;

        // Get the truncated coordinates with the correct amount of digits
        String xCoords = "X: " + BigDecimal.valueOf(player.getX()).setScale(this.numberOfDigits, RoundingMode.DOWN);
        String yCoords = "Y: " + BigDecimal.valueOf(player.getY()).setScale(this.numberOfDigits, RoundingMode.DOWN);
        String zCoords = "Z: " + BigDecimal.valueOf(player.getZ()).setScale(this.numberOfDigits, RoundingMode.DOWN);

        if (this.displayMode == DisplayMode.Vertical) {

            int hudX = (int) this.x;
            int hudY = (int) this.y;

            drawContext.drawText(CLIENT.textRenderer, xCoords, hudX, hudY, this.color, this.shadow);
            updateWidth(xCoords);
            if (this.showY) {
                hudY += 10;
                drawContext.drawText(CLIENT.textRenderer, yCoords, hudX, hudY, this.color, this.shadow);
                updateWidth(yCoords);
            }
            hudY += 10;
            drawContext.drawText(CLIENT.textRenderer, zCoords, hudX, hudY, this.color, this.shadow);
            updateWidth(zCoords);

            if (this.showBiome) {
                hudY += 10;
                renderBiome(drawContext, hudX, hudY);
            }
            this.height = hudY - (int) this.y + 10;

            if (this.showDirection) {
                hudX = (int) this.x;
                hudY = (int) this.y;
                String[] direction = getDirection(player);
                String facing;
                String axisX = direction[2];
                String axisZ = direction[3];

                if (this.directionAbreviation) {
                    facing = direction[1];
                } else {
                    facing = direction[0];
                }

                int longestCoords = Math.max(Math.max(xCoords.length(), yCoords.length()), zCoords.length());
                hudX = hudX + 24 + 6 * (longestCoords - 1);
                drawContext.drawText(CLIENT.textRenderer, axisX, hudX, hudY, this.color, this.shadow);
                updateWidth(axisX, hudX - (int) this.x);
                if (this.showY) {
                    hudY += 10;
                    drawContext.drawText(CLIENT.textRenderer, facing, hudX, hudY, this.color, this.shadow);
                    updateWidth(facing, hudX - (int) this.x);
                } else {
                    drawContext.drawText(CLIENT.textRenderer, facing, hudX + 8, hudY + 5, this.color, this.shadow);
                    updateWidth(facing, hudX + 8 - (int) this.x);
                }
                hudY += 10;
                drawContext.drawText(CLIENT.textRenderer, axisZ, hudX, hudY, this.color, this.shadow);
                updateWidth(axisZ, hudX - (int) this.x);
            }

        } else {
            StringBuilder text = new StringBuilder();
            text.append(xCoords);
            if (this.showY) {
                text.append("; ").append(yCoords);
            }
            text.append("; ").append(zCoords);
            text.insert(0, "(");
            text.append(")");
            if (this.showDirection) {
                text.append(" ");
                if (this.directionAbreviation) {
                    text.append(getDirection(player)[1]);
                } else {
                    text.append(getDirection(player)[0]);
                }
            }
            drawContext.drawText(CLIENT.textRenderer, text.toString(), (int) this.x, (int) this.y, this.color, this.shadow);
            updateWidth(text.toString());
            this.height = 10;
            if (this.showBiome) {
                renderBiome(drawContext, (int) this.x, (int) this.y + 10);
                this.height += 10;
            }

        }

    }

    private String[] getDirection(PlayerEntity p) {
        float yaw = (p.getYaw() % 360 + 360) % 360;

        if (337.5 < yaw || yaw < 22.5) {
            return new String[]{"South", "S", "", "+"};
        } else if (22.5 <= yaw && yaw < 67.5) {
            return new String[]{"South-West", "SW", "-", "+"};
        } else if (67.5 <= yaw && yaw < 112.5) {
            return new String[]{"West", "W", "-", ""};
        } else if (112.5 <= yaw && yaw < 157.5) {
            return new String[]{"North-West", "NW", "-", "-"};
        } else if (157.5 <= yaw && yaw < 202.5) {
            return new String[]{"North", "N", "", "-"};
        } else if (202.5 <= yaw && yaw < 247.5) {
            return new String[]{"North-East", "NE", "+", "-"};
        } else if (247.5 <= yaw && yaw < 292.5) {
            return new String[]{"East", "E", "+", ""};
        } else  {
            return new String[]{"South-East", "SE", "+", "+"};
        }

    }

    private void renderBiome(DrawContext drawContext, int hudX, int hudY) {
        MinecraftClient CLIENT = MinecraftClient.getInstance();
        PlayerEntity p = CLIENT.player;

        RegistryKey<Biome> biomeKey = CLIENT.world.getBiome(p.getBlockPos()).getKey().orElse(null);
        String biomeName = CLIENT.world.getBiome(p.getBlockPos()).getIdAsString().replace("minecraft:", "");
        drawContext.drawText(CLIENT.textRenderer, "Biome: ", hudX, hudY, this.color, this.shadow);
        hudX += CLIENT.textRenderer.getWidth("Biome: ");
        drawContext.drawText(CLIENT.textRenderer, biomeName, hudX, hudY, Better_hudClient.BIOME_COLORS.getOrDefault(biomeKey, 0xFFFFFF), this.shadow);
        updateWidth("Biome: " + biomeName);
    }
}