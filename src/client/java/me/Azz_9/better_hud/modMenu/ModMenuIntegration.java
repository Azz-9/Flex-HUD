package me.Azz_9.better_hud.modMenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.Azz_9.better_hud.screens.modsConfigScreen.DisplayMode;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.ArmorStatus;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.DurabilityPing;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.Speedometer;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.WeatherChanger;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Better HUD config"));


            ConfigCategory generalCategory = builder.getOrCreateCategory(Text.of("General"));

            //enable mod
            generalCategory.addEntry(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Enable mod"), ModConfig.getInstance().isEnabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().isEnabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Coordinates category
            SubCategoryBuilder coordinatesSubCategory = builder.entryBuilder().startSubCategory(Text.of("Coordinates"));
            //toggle
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show Coordinates"), ModConfig.getInstance().coordinates.enabled)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.enabled = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text color
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().coordinates.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().coordinates.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //toggle Y value
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Y coordinate"), ModConfig.getInstance().coordinates.showY)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.showY = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //number of digits
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("Number of digits"), ModConfig.getInstance().coordinates.numberOfDigits)
                    .setDefaultValue(0) // Used when user click "Reset"
                    .setMax(14)
                    .setMin(0)
                    .setTooltip(Text.of("Min: 0\nMax: 14"))
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.numberOfDigits = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //toggle biome
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Biome"), ModConfig.getInstance().coordinates.showBiome)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.showBiome = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //toggle direction
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Direction"), ModConfig.getInstance().coordinates.showDirection)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.showDirection = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //toggle direction abreviation
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Direction abreviation"), ModConfig.getInstance().coordinates.directionAbreviation)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.directionAbreviation = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display mode
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Orientation"), DisplayMode.class, ModConfig.getInstance().coordinates.displayMode)
                    .setDefaultValue(DisplayMode.Vertical) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.displayMode = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().coordinates.x)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().coordinates.y)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().coordinates.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinates.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //FPS category
            SubCategoryBuilder FPSSubCategory = builder.entryBuilder().startSubCategory(Text.of("FPS"));
            //toggle
            FPSSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show FPS"), ModConfig.getInstance().fps.enabled)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().fps.enabled = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text color
            FPSSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().fps.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().fps.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            FPSSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().fps.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().fps.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            FPSSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().fps.x)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().fps.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            FPSSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().fps.y)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().fps.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().fps.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().fps.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Clock category
            SubCategoryBuilder clockSubCategory = builder.entryBuilder().startSubCategory(Text.of("Clock"));
            //toggle
            clockSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show clock"), ModConfig.getInstance().clock.enabled)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clock.enabled = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text color
            clockSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().clock.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clock.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            clockSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().clock.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clock.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text format
            clockSubCategory.add(builder.entryBuilder()
                    .startStrField(Text.of("Text format"), ModConfig.getInstance().clock.textFormat)
                    .setDefaultValue("hh:mm:ss") // Used when user click "Reset"
                    .setTooltip(Text.of("hh: hours\nmm: minutes\nss: secondes"))
                    .setErrorSupplier(newValue -> { // Check if format entered by the player is allowed
                        if (isValidClockFormat(newValue)) {
                            return Optional.empty();
                        } else {
                            return Optional.of(Text.of("Invalid format. Please enter a correct format."));
                        }
                    })
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clock.textFormat = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //24-hour format
            clockSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("24-hour format"), ModConfig.getInstance().clock.isTwentyFourHourFormat)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clock.isTwentyFourHourFormat = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            clockSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().clock.x)
                    .setDefaultValue(650)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clock.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            clockSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().clock.y)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clock.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().clock.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clock.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Armor status category
            SubCategoryBuilder armorStatusSubCategory = builder.entryBuilder().startSubCategory(Text.of("Armor status"));
            //toggle
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show armor status"), ModConfig.getInstance().armorStatus.enabled)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.enabled = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text color
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().armorStatus.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().armorStatus.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show helmet
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show helmet"), ModConfig.getInstance().armorStatus.showHelmet)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.showHelmet = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show chestplate
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show chestplate"), ModConfig.getInstance().armorStatus.showChestplate)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.showChestplate = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show leggings
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show leggings"), ModConfig.getInstance().armorStatus.showLeggings)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.showLeggings = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show boots
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show boots"), ModConfig.getInstance().armorStatus.showBoots)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.showBoots = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show held item
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show held item"), ModConfig.getInstance().armorStatus.showHeldItem)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.showHeldItem = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show number of arrows when held item is a bow
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show arrows when held item is a bow"), ModConfig.getInstance().armorStatus.showArrowsWhenBowInHand)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.showArrowsWhenBowInHand = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show durability
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Show durability"), ArmorStatus.DurabilityType.class, ModConfig.getInstance().armorStatus.showDurability)
                    .setDefaultValue(ArmorStatus.DurabilityType.Percentage) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.showDurability = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display mode
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Orientation"), DisplayMode.class, ModConfig.getInstance().armorStatus.displayMode)
                    .setDefaultValue(DisplayMode.Vertical) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.displayMode = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().armorStatus.x)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().armorStatus.y)
                    .setDefaultValue(200)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().armorStatus.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatus.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Direction category
            SubCategoryBuilder directionSubCategory = builder.entryBuilder().startSubCategory(Text.of("Direction"));
            //toggle
            directionSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show direction"), ModConfig.getInstance().direction.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().direction.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            directionSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().direction.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().direction.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            directionSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().direction.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().direction.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show marker
            directionSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show marker"), ModConfig.getInstance().direction.showMarker)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().direction.showMarker = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show intermediate points
            directionSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show intermediate points"), ModConfig.getInstance().direction.showIntermediatePoint)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().direction.showIntermediatePoint = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show Xaero's map waypoints
            directionSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show Xaero's map waypoints"), ModConfig.getInstance().direction.showXaerosMapWaypoints)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().direction.showXaerosMapWaypoints = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            directionSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().direction.x)
                    .setDefaultValue(200)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().direction.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            directionSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().direction.y)
                    .setDefaultValue(0)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().direction.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().direction.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().direction.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Day counter category
            SubCategoryBuilder dayCounterSubCategory = builder.entryBuilder().startSubCategory(Text.of("Day counter"));
            //toggle
            dayCounterSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show day counter"), ModConfig.getInstance().dayCounter.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("This mod does not work with time changer").styled(style -> style.withColor(Formatting.RED)))
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().dayCounter.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            dayCounterSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().dayCounter.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().dayCounter.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            dayCounterSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().dayCounter.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().dayCounter.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            dayCounterSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().dayCounter.x)
                    .setDefaultValue(575)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().dayCounter.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            dayCounterSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().dayCounter.y)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().dayCounter.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().dayCounter.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().dayCounter.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Ping category
            SubCategoryBuilder pingSubCategory = builder.entryBuilder().startSubCategory(Text.of("Ping"));
            //toggle
            pingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show ping"), ModConfig.getInstance().ping.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().ping.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            pingSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().ping.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().ping.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            pingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().ping.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().ping.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //hide when offline
            pingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Hide when offline"), ModConfig.getInstance().ping.hideWhenOffline)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().ping.hideWhenOffline = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            pingSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().ping.x)
                    .setDefaultValue(725)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().ping.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            pingSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().ping.y)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().ping.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().ping.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().ping.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Server address category
            SubCategoryBuilder serverAddressSubCategory = builder.entryBuilder().startSubCategory(Text.of("Server Address"));
            //toggle
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show ping"), ModConfig.getInstance().serverAddress.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().serverAddress.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().serverAddress.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().serverAddress.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().serverAddress.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().serverAddress.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //hide when offline
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Hide when offline"), ModConfig.getInstance().serverAddress.hideWhenOffline)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().serverAddress.hideWhenOffline = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().serverAddress.x)
                    .setDefaultValue(200)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().serverAddress.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().serverAddress.y)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().serverAddress.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().serverAddress.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().serverAddress.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Weather changer category
            SubCategoryBuilder weatherChangerSubCategory = builder.entryBuilder().startSubCategory(Text.of("Weather Changer"));
            //toggle
            weatherChangerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Enable weather changer"), ModConfig.getInstance().weatherChanger.enabled)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().weatherChanger.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //select weather
            weatherChangerSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Select weather"), WeatherChanger.Weather.class, ModConfig.getInstance().weatherChanger.selectedWeather)
                    .setDefaultValue(WeatherChanger.Weather.Clear)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().weatherChanger.selectedWeather = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Memory usage category
            SubCategoryBuilder memoryUsageSubCategory = builder.entryBuilder().startSubCategory(Text.of("Memory usage"));
            //toggle
            memoryUsageSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show memory usage"), ModConfig.getInstance().memoryUsage.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().memoryUsage.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            memoryUsageSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().memoryUsage.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().memoryUsage.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            memoryUsageSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().memoryUsage.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().memoryUsage.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            memoryUsageSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().memoryUsage.x)
                    .setDefaultValue(75)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().memoryUsage.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            memoryUsageSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().memoryUsage.y)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().memoryUsage.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().memoryUsage.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().memoryUsage.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Cps category
            SubCategoryBuilder cpsSubCategory = builder.entryBuilder().startSubCategory(Text.of("cps"));
            //toggle
            cpsSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show cps"), ModConfig.getInstance().cps.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cps.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            cpsSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().cps.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cps.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            cpsSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().cps.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cps.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show left click cps
            cpsSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show left click cps"), ModConfig.getInstance().cps.showLeftClick)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cps.showLeftClick = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show right click cps
            cpsSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show right click cps"), ModConfig.getInstance().cps.showRightClick)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cps.showRightClick = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            cpsSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().cps.x)
                    .setDefaultValue(815)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cps.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            cpsSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().cps.y)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cps.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().cps.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cps.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Time changer category
            SubCategoryBuilder timeChangerSubCategory = builder.entryBuilder().startSubCategory(Text.of("Time changer"));
            //toggle
            timeChangerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Enable time changer"), ModConfig.getInstance().timeChanger.enabled)
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("This mod does not work with day counter").styled(style -> style.withColor(Formatting.RED)))
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().timeChanger.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //select time
            timeChangerSubCategory.add(builder.entryBuilder()
                    .startIntSlider(Text.of("Select time"), ModConfig.getInstance().timeChanger.selectedTime, 0, 24000)
                    .setDefaultValue(6000)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().timeChanger.selectedTime = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //use real current time
            timeChangerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Use real current time"), ModConfig.getInstance().timeChanger.useRealTime)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().timeChanger.useRealTime = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Durability ping category
            SubCategoryBuilder durabilityPingSubCategory = builder.entryBuilder().startSubCategory(Text.of("Durability ping"));
            //toggle
            durabilityPingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Enable durability ping"), ModConfig.getInstance().durabilityPing.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().durabilityPing.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //durability ping threshold
            durabilityPingSubCategory.add(builder.entryBuilder()
                    .startIntSlider(Text.of("Durability ping threshold"), ModConfig.getInstance().durabilityPing.threshold, 0, 100)
                    .setDefaultValue(10)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().durabilityPing.threshold = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //durability ping type
            durabilityPingSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Durability ping type"), DurabilityPing.DurabilityPingType.class, ModConfig.getInstance().durabilityPing.pingType)
                    .setDefaultValue(DurabilityPing.DurabilityPingType.Both)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().durabilityPing.pingType = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //check armor pieces
            durabilityPingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Check armor pieces durability"), ModConfig.getInstance().durabilityPing.checkArmorPieces)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().durabilityPing.checkArmorPieces = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //check elytra
            durabilityPingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.literal("Check elytra durability ").append(Text.literal("only").formatted(Formatting.UNDERLINE, Formatting.BOLD, Formatting.ITALIC)), ModConfig.getInstance().durabilityPing.checkElytraOnly)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().durabilityPing.checkElytraOnly = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());

            //speedometer category
            SubCategoryBuilder speedometerSubCategory = builder.entryBuilder().startSubCategory(Text.of("Speedometer"));
            //toggle
            speedometerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show speedometer"), ModConfig.getInstance().speedometer.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometer.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            speedometerSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().speedometer.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometer.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            speedometerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().speedometer.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometer.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //number of digits
            speedometerSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("Number of digits"), ModConfig.getInstance().speedometer.digits)
                    .setDefaultValue(1)
                    .setMin(0)
                    .setMax(16)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometer.digits = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //speed unit
            speedometerSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Speed unit"), Speedometer.SpeedometerUnits.class, ModConfig.getInstance().speedometer.units)
                    .setDefaultValue(Speedometer.SpeedometerUnits.MPS)
                    .setTooltipSupplier(value ->
                        switch (value) {
                            case MPS -> Optional.of(new Text[]{Text.literal("Meters per second (m/s):")});
                            case KPH -> Optional.of(new Text[]{Text.literal("Kilometers per hour (km/h):")});
                            case MPH -> Optional.of(new Text[]{Text.literal("Miles per hour (mph):")});
                            default -> Optional.empty();
                    })
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometer.units = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //always use knot when player is in a boat
            speedometerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Always use knot when player is in a boat"), ModConfig.getInstance().speedometer.useKnotInBoat)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometer.useKnotInBoat = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display X
            speedometerSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().speedometer.x)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometer.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display Y
            speedometerSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().speedometer.y)
                    .setDefaultValue(70)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometer.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().speedometer.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometer.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //reach category
            SubCategoryBuilder reachSubCategory = builder.entryBuilder().startSubCategory(Text.of("Reach"));
            //toggle
            reachSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show reach"), ModConfig.getInstance().reach.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reach.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            reachSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().reach.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reach.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            reachSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().reach.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reach.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //number of digits
            reachSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("Number of digits"), ModConfig.getInstance().reach.digits)
                    .setDefaultValue(2)
                    .setMin(0)
                    .setMax(16)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reach.digits = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display X
            reachSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().reach.x)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reach.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display Y
            reachSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().reach.y)
                    .setDefaultValue(100)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reach.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().reach.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reach.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //combo counter category
            SubCategoryBuilder comboCounterSubCategory = builder.entryBuilder().startSubCategory(Text.of("Combo Counter"));
            //toggle
            comboCounterSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show combo counter"), ModConfig.getInstance().comboCounter.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().comboCounter.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            comboCounterSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().comboCounter.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().comboCounter.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            comboCounterSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().comboCounter.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().comboCounter.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display X
            comboCounterSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().comboCounter.x)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().comboCounter.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display Y
            comboCounterSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().comboCounter.y)
                    .setDefaultValue(120)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().comboCounter.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().comboCounter.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().comboCounter.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //playtime category
            SubCategoryBuilder playtimeSubCategory = builder.entryBuilder().startSubCategory(Text.of("Playtime"));
            //toggle
            playtimeSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show playtime"), ModConfig.getInstance().playtime.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().playtime.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            playtimeSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().playtime.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().playtime.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            playtimeSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().playtime.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().playtime.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show prefix
            playtimeSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show prefix"), ModConfig.getInstance().playtime.showPrefix)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().playtime.showPrefix = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display X
            playtimeSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().playtime.x)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().playtime.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display Y
            playtimeSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().playtime.y)
                    .setDefaultValue(140)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().playtime.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().playtime.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().playtime.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //shrieker warning level category
            SubCategoryBuilder shriekerWarningLevelSubCategory = builder.entryBuilder().startSubCategory(Text.of("Shrieker Warning Level"));
            //toggle
            shriekerWarningLevelSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show shrieker warning level"), ModConfig.getInstance().shriekerWarningLevel.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().shriekerWarningLevel.enabled = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            shriekerWarningLevelSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().shriekerWarningLevel.color)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().shriekerWarningLevel.color = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            shriekerWarningLevelSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().shriekerWarningLevel.shadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().shriekerWarningLevel.shadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show when player is in deep dark biome
            shriekerWarningLevelSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Only show when you are in deep dark biome"), ModConfig.getInstance().shriekerWarningLevel.showWhenInDeepDark)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().shriekerWarningLevel.showWhenInDeepDark = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //show when player is in nether
            //display X
            shriekerWarningLevelSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display X"), ModConfig.getInstance().shriekerWarningLevel.x)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().shriekerWarningLevel.x = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display Y
            shriekerWarningLevelSubCategory.add(builder.entryBuilder()
                    .startDoubleField(Text.of("display Y"), ModConfig.getInstance().shriekerWarningLevel.y)
                    .setDefaultValue(160)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().shriekerWarningLevel.y = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //scale
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startFloatField(Text.of("Scale"), ModConfig.getInstance().shriekerWarningLevel.scale)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().shriekerWarningLevel.scale = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            generalCategory.addEntry(coordinatesSubCategory.build());
            generalCategory.addEntry(FPSSubCategory.build());
            generalCategory.addEntry(clockSubCategory.build());
            generalCategory.addEntry(armorStatusSubCategory.build());
            generalCategory.addEntry(directionSubCategory.build());
            generalCategory.addEntry(dayCounterSubCategory.build());
            generalCategory.addEntry(pingSubCategory.build());
            generalCategory.addEntry(serverAddressSubCategory.build());
            generalCategory.addEntry(weatherChangerSubCategory.build());
            generalCategory.addEntry(memoryUsageSubCategory.build());
            generalCategory.addEntry(cpsSubCategory.build());
            generalCategory.addEntry(timeChangerSubCategory.build());
            generalCategory.addEntry(durabilityPingSubCategory.build());
            generalCategory.addEntry(speedometerSubCategory.build());
            generalCategory.addEntry(reachSubCategory.build());
            generalCategory.addEntry(comboCounterSubCategory.build());
            generalCategory.addEntry(playtimeSubCategory.build());
            generalCategory.addEntry(shriekerWarningLevelSubCategory.build());

            return builder.build();
        };
    }

    private boolean isValidClockFormat(String textFormat) {
        try {
            textFormat = textFormat.toLowerCase();
            if (ModConfig.getInstance().clock.isTwentyFourHourFormat) {
                textFormat = textFormat.replace("hh", "HH");
            } else {
                textFormat += " a";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(textFormat);
            LocalTime.now().format(formatter);
            return true;

        } catch (IllegalArgumentException | DateTimeException e) {
            return false;
        }
    }

    private boolean isValidBlocks(List<String> selectedBlocks) {
        for (String block : selectedBlocks) {
            String[] parts = block.split(":", 2);
            if (parts.length == 2 && !Registries.BLOCK.containsId(Identifier.of(parts[0], parts[1]))) {
                return false;
            } else if (!Registries.BLOCK.containsId(Identifier.of(parts[0]))){
                return false;
            }
        }
        return true;
    }

}
