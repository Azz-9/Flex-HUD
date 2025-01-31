package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.DisplayMode;
import me.Azz_9.better_hud.client.Better_hudClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CoordinatesOverlay extends HudElement {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);

        ModConfig INSTANCE = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if(!INSTANCE.isEnabled || !INSTANCE.showCoordinates || client == null || client.options.hudHidden || client.player == null) {
            return;
        }

        PlayerEntity p = client.player;

        // Get the truncated coordinates with the correct amount of digits
        String xCoords = BigDecimal.valueOf(p.getX()).setScale(INSTANCE.coordinatesDigits, RoundingMode.DOWN).toString();
        String yCoords = BigDecimal.valueOf(p.getY()).setScale(INSTANCE.coordinatesDigits, RoundingMode.DOWN).toString();
        String zCoords = BigDecimal.valueOf(p.getZ()).setScale(INSTANCE.coordinatesDigits, RoundingMode.DOWN).toString();

        xCoords = "X: " + xCoords;
        yCoords = "Y: " + yCoords;
        zCoords = "Z: " + zCoords;

        this.x = INSTANCE.coordinatesHudX;
        this.y = INSTANCE.coordinatesHudY;

        if (INSTANCE.displayModeCoordinates == DisplayMode.Vertical) {

            int hudX = this.x;
            int hudY = this.y;

            drawContext.drawText(client.textRenderer, xCoords, hudX, hudY, INSTANCE.coordinatesColor, INSTANCE.coordinatesShadow);
            updateWidth(xCoords);
            if (INSTANCE.showYCoordinates) {
                hudY += 10;
                drawContext.drawText(client.textRenderer, yCoords, hudX, hudY, INSTANCE.coordinatesColor, INSTANCE.coordinatesShadow);
                updateWidth(yCoords);
            }
            hudY += 10;
            drawContext.drawText(client.textRenderer, zCoords, hudX, hudY, INSTANCE.coordinatesColor, INSTANCE.coordinatesShadow);
            updateWidth(zCoords);

            if (INSTANCE.showBiome) {
                hudY += 10;
                renderBiome(drawContext, hudX, hudY);
            }
            this.height = hudY - this.y + 10;
            if (INSTANCE.showCoordinatesDirection) {
                hudX = this.x;
                hudY = this.y;
                List<String> direction = getDirection(p);
                String facing;
                String axisX = direction.get(2);
                String axisZ = direction.get(3);

                if (INSTANCE.coordinatesDirectionAbreviation) {
                    facing = direction.get(1);
                } else {
                    facing = direction.getFirst();
                }

                int longestCoords = Math.max(Math.max(xCoords.length(), yCoords.length()), zCoords.length());
                hudX = hudX + 24 + 6 * (longestCoords - 1);
                drawContext.drawText(client.textRenderer, axisX, hudX, hudY, INSTANCE.coordinatesColor, INSTANCE.coordinatesShadow);
                updateWidth(axisX, hudX - this.x);
                if (INSTANCE.showYCoordinates) {
                    hudY += 10;
                    drawContext.drawText(client.textRenderer, facing, hudX, hudY, INSTANCE.coordinatesColor, INSTANCE.coordinatesShadow);
                    updateWidth(facing, hudX - this.x);
                } else {
                    drawContext.drawText(client.textRenderer, facing, hudX + 8, hudY + 5, INSTANCE.coordinatesColor, INSTANCE.coordinatesShadow);
                    updateWidth(facing, hudX + 8 - this.x);
                }
                hudY += 10;
                drawContext.drawText(client.textRenderer, axisZ, hudX, hudY, INSTANCE.coordinatesColor, INSTANCE.coordinatesShadow);
                updateWidth(axisZ, hudX - this.x);
            }

        } else {
            StringBuilder text = new StringBuilder();
            text.append(xCoords);
            if (INSTANCE.showYCoordinates) {
                text.append("; ").append(yCoords);
            }
            text.append("; ").append(zCoords);
            text.insert(0, "(");
            text.append(")");
            if (INSTANCE.showCoordinatesDirection) {
                text.append(" ");
                if (INSTANCE.coordinatesDirectionAbreviation) {
                    text.append(getDirection(p).get(1));
                } else {
                    text.append(getDirection(p).getFirst());
                }
            }
            drawContext.drawText(client.textRenderer, text.toString(), this.x, this.y, INSTANCE.coordinatesColor, INSTANCE.coordinatesShadow);
            updateWidth(text.toString());
            this.height = 10;
            if (INSTANCE.showBiome) {
                renderBiome(drawContext, this.x, this.y + 10);
                this.height += 10;
            }

        }

    }

    private List<String> getDirection(PlayerEntity p) {
        float yaw = (p.getYaw() % 360 + 360) % 360;

        if (337.5 < yaw || yaw < 22.5) {
            return List.of("South", "S", "", "+");
        } else if (22.5 <= yaw && yaw < 67.5) {
            return List.of("South-West", "SW", "-", "+");
        } else if (67.5 <= yaw && yaw < 112.5) {
            return List.of("West", "W", "-", "");
        } else if (112.5 <= yaw && yaw < 157.5) {
            return List.of("North-West", "NW", "-", "-");
        } else if (157.5 <= yaw && yaw < 202.5) {
            return List.of("North", "N", "", "-");
        } else if (202.5 <= yaw && yaw < 247.5) {
            return List.of("North-East", "NE", "+", "-");
        } else if (247.5 <= yaw && yaw < 292.5) {
            return List.of("East", "E", "+", "");
        } else  {
            return List.of("South-East", "SE", "+", "+");
        }

    }

    private void renderBiome(DrawContext drawContext, int hudX, int hudY) {
        MinecraftClient client = MinecraftClient.getInstance();
        ModConfig INSTANCE = ModConfig.getInstance();
        PlayerEntity p = client.player;

        RegistryKey<Biome> biomeKey = client.world.getBiome(p.getBlockPos()).getKey().orElse(null);
        String biomeName = client.world.getBiome(p.getBlockPos()).getIdAsString().replace("minecraft:", "");
        drawContext.drawText(client.textRenderer, "Biome: ", hudX, hudY, INSTANCE.coordinatesColor, INSTANCE.coordinatesShadow);
        hudX += client.textRenderer.getWidth("Biome: ");
        drawContext.drawText(client.textRenderer, biomeName, hudX, hudY, Better_hudClient.biomeColors.getOrDefault(biomeKey, 0xFFFFFF), INSTANCE.coordinatesShadow);
        updateWidth("Biome: " + biomeName);
    }

    @Override
    public void setPos(int x, int y) {
        ModConfig INSTANCE = ModConfig.getInstance();
        INSTANCE.coordinatesHudX = x;
        INSTANCE.coordinatesHudY = y;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.getInstance().showCoordinates;
    }
}