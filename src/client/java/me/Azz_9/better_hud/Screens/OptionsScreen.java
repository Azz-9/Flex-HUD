package me.Azz_9.better_hud.Screens;

import com.mojang.blaze3d.systems.RenderSystem;
import me.Azz_9.better_hud.Screens.ModsList.ConfigurationScreen;
import me.Azz_9.better_hud.Screens.MoveElementsScreen.MoveElementsScreen;
import me.Azz_9.better_hud.client.Better_hudClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

@Environment(EnvType.CLIENT)
public class OptionsScreen extends Screen {
    public OptionsScreen(Text title) {
        super(title);
    }

    private float alpha = 0.0f;
    private int yAnimation = 0; // y value used to make the animation on the icon

    @Override
    protected void init() {
        ButtonWidget modsButton = ButtonWidget.builder(Text.of("Mods"), (btn) -> {
            MinecraftClient.getInstance().setScreen(new ConfigurationScreen(this));
        }).dimensions(width/2 - 60, height/2 - 10, 120, 20).build();

        ButtonWidget moveButton = ButtonWidget.builder(Text.of("←↑→↓"), (btn) -> {
            MinecraftClient.getInstance().setScreen(new MoveElementsScreen(this));
            Better_hudClient.isEditing = true;
        }).dimensions(width/2 - 10 + 80, height/2 - 10, 20, 20).build();


        this.addDrawableChild(modsButton);
        this.addDrawableChild(moveButton);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        final Identifier modIcon = Identifier.of(MOD_ID, "icon.png");

        // set the icon width and height
        int iconWidth = 64;
        int iconHeight = 64;

        // increment icon alpha value from 0 to 1 to make a fade in effect
        if (alpha < 0.98f) { // check alpha < 0.985 to prevent alpha to be superior to 1 because of the imprecision of the floats
            alpha += 0.02f;
            float easedAlpha = 1 - (1 - alpha) * (1 - alpha); // Ease-out quadratique
            yAnimation = (int) (16 * easedAlpha); // yAnimation never reach 16 because easedAlpha is at maximum 0.99
        }

        // set x and y value for the icon
        int x = width/2 - iconWidth/2;
        // subtract 35 to make it a bit higher
        // subtract yAnimation to make the icon moves smoothly
        int y = height/2 - iconHeight/2 - 35 - yAnimation;


        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc(); // Définit une fonction de mélange par défaut
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha); // Définit une opacité à 50% (alpha = 0.5)

        // Draw the icon
        context.drawTexture(RenderLayer::getGuiTexturedOverlay, modIcon, x, y, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
        context.drawText(textRenderer, ".", -9999, 0, 0xffffff, false); // i don't know why but i need to put this in order to the texture to be rendered with the correct opacity

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Opacité à 100%
        RenderSystem.disableBlend(); // Désactive le mélange pour éviter des effets indésirables
    }

}