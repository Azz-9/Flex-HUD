package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.Enum.DisplayModeEnum;
import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.client.Better_hudClient;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class CoordinatesOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        ModConfig modConfigInstance = ModConfig.getInstance();

        if(modConfigInstance.isEnabled && modConfigInstance.showCoordinates) {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && !client.options.hudHidden) {

                PlayerEntity p = client.player;

                if (p != null) {

                    String format = "%." + modConfigInstance.coordinatesDigits + "f";
                    String xCoords = String.format("X: " + format, p.getX());
                    String yCoords = String.format("Y: " + format, p.getY());
                    String zCoords = String.format("Z: " + format, p.getZ());

                    if (modConfigInstance.displayModeCoordinates == DisplayModeEnum.Vertical) {

                        int hudX = modConfigInstance.coordinatesHudX;
                        int hudY = modConfigInstance.coordinatesHudY;

                        drawContext.drawText(client.textRenderer, xCoords, hudX, hudY, modConfigInstance.coordinatesColor, modConfigInstance.coordinatesShadow);
                        if (modConfigInstance.showYCoordinates) {
                            hudY += 10;
                            drawContext.drawText(client.textRenderer, yCoords, hudX, hudY, modConfigInstance.coordinatesColor, modConfigInstance.coordinatesShadow);
                        }
                        hudY += 10;
                        drawContext.drawText(client.textRenderer, zCoords, hudX, hudY, modConfigInstance.coordinatesColor, modConfigInstance.coordinatesShadow);

                        if (modConfigInstance.showBiome) {
                            hudY += 10;
                            renderBiome(drawContext, hudX, hudY);
                        }
                        if (modConfigInstance.showCoordinatesDirection) {
                            hudX = modConfigInstance.coordinatesHudX;
                            hudY = modConfigInstance.coordinatesHudY;
                            List<String> direction = getDirection(p);
                            String facing;
                            String axisX = direction.get(2);
                            String axisZ = direction.get(3);

                            if (modConfigInstance.coordinatesDirectionAbreviation) {
                                facing = direction.get(1);
                            } else {
                                facing = direction.getFirst();
                            }


                            int longestCoords = Math.max(Math.max(xCoords.length(), yCoords.length()), zCoords.length());
                            hudX = hudX + 24 + 6 * (longestCoords - 1);
                            drawContext.drawText(client.textRenderer, axisX, hudX, hudY, modConfigInstance.coordinatesColor, modConfigInstance.coordinatesShadow);
                            if (modConfigInstance.showYCoordinates) {
                                hudY += 10;
                                drawContext.drawText(client.textRenderer, facing, hudX, hudY, modConfigInstance.coordinatesColor, modConfigInstance.coordinatesShadow);
                            } else {
                                drawContext.drawText(client.textRenderer, facing, hudX + 8, hudY + 5, modConfigInstance.coordinatesColor, modConfigInstance.coordinatesShadow);
                            }
                            hudY += 10;
                            drawContext.drawText(client.textRenderer, axisZ, hudX, hudY, modConfigInstance.coordinatesColor, modConfigInstance.coordinatesShadow);
                        }

                    } else {
                        StringBuilder text = new StringBuilder();
                        text.append(xCoords);
                        if (modConfigInstance.showYCoordinates) {
                            text.append("; ").append(yCoords);
                        }
                        text.append("; ").append(zCoords);
                        text.insert(0, "(");
                        text.append(") ");
                        if (modConfigInstance.showCoordinatesDirection) {
                            if (modConfigInstance.coordinatesDirectionAbreviation) {
                                text.append(getDirection(p).get(1));
                            } else {
                                text.append(getDirection(p).getFirst());
                            }
                        }
                        drawContext.drawText(client.textRenderer, text.toString(), modConfigInstance.coordinatesHudX, modConfigInstance.coordinatesHudY, modConfigInstance.coordinatesColor, modConfigInstance.coordinatesShadow);
                        if (modConfigInstance.showBiome) {
                            renderBiome(drawContext, modConfigInstance.coordinatesHudX, modConfigInstance.coordinatesHudY + 10);
                        }

                    }

                }

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
        ModConfig modConfigInstance = ModConfig.getInstance();
        PlayerEntity p = client.player;

        RegistryKey<Biome> biomeKey = client.world.getBiome(p.getBlockPos()).getKey().orElse(null);
        String biomeName = client.world.getBiome(p.getBlockPos()).getIdAsString().replace("minecraft:", "");
        drawContext.drawText(client.textRenderer, "Biome: ", hudX, hudY, modConfigInstance.coordinatesColor, modConfigInstance.coordinatesShadow);
        hudX += client.textRenderer.getWidth("Biome: ");
        drawContext.drawText(client.textRenderer, biomeName, hudX, hudY, Better_hudClient.biomeColors.getOrDefault(biomeKey, 0xFFFFFF), modConfigInstance.coordinatesShadow);
    }

}