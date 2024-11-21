package me.Azz_9.better_hud.ModMenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.Azz_9.better_hud.ModMenu.Enum.*;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("bonsoir config"));


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
                    .startBooleanToggle(Text.of("Show Coordinates"), ModConfig.getInstance().showCoordinates)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showCoordinates = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text color
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().coordinatesColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinatesColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().coordinatesShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinatesShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //toggle Y value
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Y coordinate"), ModConfig.getInstance().showYCoordinates)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showYCoordinates = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //number of digits
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("Number of digits"), ModConfig.getInstance().coordinatesDigits)
                    .setDefaultValue(0) // Used when user click "Reset"
                    .setMax(14)
                    .setMin(0)
                    .setTooltip(Text.of("Min: 0\nMax: 14"))
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinatesDigits = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //toggle biome
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Biome"), ModConfig.getInstance().showBiome)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showBiome = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //toggle direction
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Direction"), ModConfig.getInstance().showCoordinatesDirection)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showCoordinatesDirection = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //toggle direction abreviation
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Direction abreviation"), ModConfig.getInstance().coordinatesDirectionAbreviation)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinatesDirectionAbreviation = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display mode
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Orientation"), DisplayModeEnum.class, ModConfig.getInstance().displayModeCoordinates)
                    .setDefaultValue(DisplayModeEnum.Vertical) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().displayModeCoordinates = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().coordinatesHudX)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinatesHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            coordinatesSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().coordinatesHudY)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().coordinatesHudY = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //FPS category
            SubCategoryBuilder FPSSubCategory = builder.entryBuilder().startSubCategory(Text.of("FPS"));
            //toggle
            FPSSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show FPS"), ModConfig.getInstance().showFPS)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showFPS = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text color
            FPSSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().FPSColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().FPSColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            FPSSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().FPSShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().FPSShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            FPSSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().FPSHudX)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().FPSHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            FPSSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().FPSHudY)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().FPSHudY = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Clock category
            SubCategoryBuilder clockSubCategory = builder.entryBuilder().startSubCategory(Text.of("Clock"));
            //toggle
            clockSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show clock"), ModConfig.getInstance().showClock)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showClock = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text color
            clockSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().clockColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clockColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            clockSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().clockShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clockShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text format
            clockSubCategory.add(builder.entryBuilder()
                    .startStrField(Text.of("Text format"), ModConfig.getInstance().clockTextFormat)
                    .setDefaultValue("hh:mm:ss") // Used when user click "Reset"
                    .setTooltip(Text.of("hh: hours\nmm: minutes\nss: secondes"))
                    .setErrorSupplier(newValue -> { // Check if format entered by the player is allowed
                        if (isValidClockFormat(newValue)) {
                            return Optional.empty();
                        } else {
                            return Optional.of(Text.of("Invalid format. Please enter a correct fomrat."));
                        }
                    })
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clockTextFormat = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //24-hour format
            clockSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("24-hour format"), ModConfig.getInstance().clock24hourformat)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clock24hourformat = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            clockSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().clockHudX)
                    .setDefaultValue(650)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clockHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            clockSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().clockHudY)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().clockHudY = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Armor status category
            SubCategoryBuilder armorStatusSubCategory = builder.entryBuilder().startSubCategory(Text.of("Armor status"));
            //toggle
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show armor status"), ModConfig.getInstance().showArmorStatus)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showArmorStatus = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text color
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().armorStatusTextColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatusTextColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().armorStatusTextShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatusTextShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show helmet
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show helmet"), ModConfig.getInstance().showHelmet)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showHelmet = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show chestplate
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show chestplate"), ModConfig.getInstance().showChestplate)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showChestplate = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show leggings
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show leggings"), ModConfig.getInstance().showLeggings)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showLeggings = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show boots
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show boots"), ModConfig.getInstance().showBoots)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showBoots = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show held item
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show held item"), ModConfig.getInstance().showHeldItem)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showHeldItem = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show number of arrows when held item is a bow
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show arrows when held item is a bow"), ModConfig.getInstance().showArrowsWhenBowInHand)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showArrowsWhenBowInHand = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show durability
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Show durability"), DurabilityTypeEnum.class, ModConfig.getInstance().showDurability)
                    .setDefaultValue(DurabilityTypeEnum.Percentage) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showDurability = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display mode
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Orientation"), DisplayModeEnum.class, ModConfig.getInstance().displayModeArmorStatus)
                    .setDefaultValue(DisplayModeEnum.Vertical) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().displayModeArmorStatus = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().armorStatusHudX)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatusHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            armorStatusSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().armorStatusHudY)
                    .setDefaultValue(200)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().armorStatusHudY = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Direction category
            SubCategoryBuilder directionSubCategory = builder.entryBuilder().startSubCategory(Text.of("Direction"));
            //toggle
            directionSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show direction"), ModConfig.getInstance().showDirection)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showDirection = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            directionSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().directionColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().directionColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            directionSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().directionShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().directionShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show marker
            directionSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show marker"), ModConfig.getInstance().showDirectionMarker)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showDirectionMarker = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show intermediate points
            directionSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show intermediate points"), ModConfig.getInstance().showIntermediateDirectionPoint)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showIntermediateDirectionPoint = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());


            //Day counter category
            SubCategoryBuilder dayCounterSubCategory = builder.entryBuilder().startSubCategory(Text.of("Day counter"));
            //toggle
            dayCounterSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show day counter"), ModConfig.getInstance().showDayCounter)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showDayCounter = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            dayCounterSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().dayCounterColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().dayCounterColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            dayCounterSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().dayCounterShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().dayCounterShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            dayCounterSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().dayCounterHudX)
                    .setDefaultValue(575)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().dayCounterHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            dayCounterSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().dayCounterHudY)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().dayCounterHudY = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Ping category
            SubCategoryBuilder pingSubCategory = builder.entryBuilder().startSubCategory(Text.of("Ping"));
            //toggle
            pingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show ping"), ModConfig.getInstance().showPing)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showPing = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            pingSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().pingColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().pingColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            pingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().pingShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().pingShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //hide when offline
            pingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Hide when offline"), ModConfig.getInstance().hidePingWhenOffline)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().hidePingWhenOffline = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            pingSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().pingHudX)
                    .setDefaultValue(725)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().pingHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            pingSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().pingHudY)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().pingHudY = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Server address category
            SubCategoryBuilder serverAddressSubCategory = builder.entryBuilder().startSubCategory(Text.of("Server Address"));
            //toggle
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show ping"), ModConfig.getInstance().showServerAddress)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showServerAddress = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().serverAddressColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().serverAddressColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().serverAddressShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().serverAddressShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //hide when offline
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Hide when offline"), ModConfig.getInstance().hideServerAddressWhenOffline)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().hideServerAddressWhenOffline = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().serverAddressHudX)
                    .setDefaultValue(150)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().serverAddressHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            serverAddressSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().serverAddressHudY)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().serverAddressHudY = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Weather changer category
            SubCategoryBuilder weatherChangerSubCategory = builder.entryBuilder().startSubCategory(Text.of("Server Address"));
            //toggle
            weatherChangerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Enable weather changer"), ModConfig.getInstance().enableWeatherChanger)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().enableWeatherChanger = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //select weather
            weatherChangerSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Select weather"), WeatherEnum.class, ModConfig.getInstance().selectedWeather)
                    .setDefaultValue(WeatherEnum.Clear)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().selectedWeather = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Memory usage category
            SubCategoryBuilder memoryUsageSubCategory = builder.entryBuilder().startSubCategory(Text.of("Memory usage"));
            //toggle
            memoryUsageSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show memory usage"), ModConfig.getInstance().showMemoryUsage)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showMemoryUsage = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            memoryUsageSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().memoryUsageColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().memoryUsageColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            memoryUsageSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().memoryUsageShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().memoryUsageShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            memoryUsageSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().memoryUsageHudX)
                    .setDefaultValue(75)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().memoryUsageHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            memoryUsageSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().memoryUsageHudY)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().memoryUsageHudY = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Cps category
            SubCategoryBuilder cpsSubCategory = builder.entryBuilder().startSubCategory(Text.of("CPS"));
            //toggle
            cpsSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show cps"), ModConfig.getInstance().showCps)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showCps = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            cpsSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().cpsColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cpsColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            cpsSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().cpsShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cpsShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show left click cps
            cpsSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show left click cps"), ModConfig.getInstance().showLeftClickCPS)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showLeftClickCPS = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //show right click cps
            cpsSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show right click cps"), ModConfig.getInstance().showRightClickCPS)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showRightClickCPS = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display x
            cpsSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().cpsHudX)
                    .setDefaultValue(75)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cpsHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display y
            cpsSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().cpsHudY)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().cpsHudY = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Time changer category
            SubCategoryBuilder timeChangerSubCategory = builder.entryBuilder().startSubCategory(Text.of("Time changer"));
            //toggle
            timeChangerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Enable time changer"), ModConfig.getInstance().enableTimeChanger)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().enableTimeChanger = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //select time
            timeChangerSubCategory.add(builder.entryBuilder()
                    .startIntSlider(Text.of("Select time"), ModConfig.getInstance().selectedTime, 0, 24000)
                    .setDefaultValue(6000)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().selectedTime = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //use real current time
            timeChangerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Use real current time"), ModConfig.getInstance().useRealTime)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().useRealTime = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //Durability ping category
            SubCategoryBuilder durabilityPingSubCategory = builder.entryBuilder().startSubCategory(Text.of("Durability ping"));
            //toggle
            durabilityPingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Enable durability ping"), ModConfig.getInstance().enableDurabilityPing)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().enableDurabilityPing = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //durability ping threshold
            durabilityPingSubCategory.add(builder.entryBuilder()
                    .startIntSlider(Text.of("Durability ping threshold"), ModConfig.getInstance().durabilityPingThreshold, 0, 100)
                    .setDefaultValue(10)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().durabilityPingThreshold = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //durability ping type
            durabilityPingSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Durability ping type"), DurabilityPingTypeEnum.class, ModConfig.getInstance().durabilityPingType)
                    .setDefaultValue(DurabilityPingTypeEnum.Both)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().durabilityPingType = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //check armor pieces
            durabilityPingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Check armor pieces durability"), ModConfig.getInstance().checkArmorPieces)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().checkArmorPieces = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //check elytra
            durabilityPingSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.literal("Check elytra durability ").append(Text.literal("only").formatted(Formatting.UNDERLINE, Formatting.BOLD, Formatting.ITALIC)), ModConfig.getInstance().checkElytraOnly)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().checkElytraOnly = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());

            //speedometer category
            SubCategoryBuilder speedometerSubCategory = builder.entryBuilder().startSubCategory(Text.of("Speedometer"));
            //toggle
            speedometerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show speedometer"), ModConfig.getInstance().showSpeedometer)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showSpeedometer = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            speedometerSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().speedometerColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometerColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            speedometerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().speedometerShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometerShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //number of digits
            speedometerSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("Number of digits"), ModConfig.getInstance().speedometerDigits)
                    .setDefaultValue(1)
                    .setMin(0)
                    .setMax(16)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometerDigits = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //speed unit
            speedometerSubCategory.add(builder.entryBuilder()
                    .startEnumSelector(Text.of("Speed unit"), SpeedometerUnitsEnum.class, ModConfig.getInstance().speedometerUnits)
                    .setDefaultValue(SpeedometerUnitsEnum.MPS)
                    .setTooltipSupplier(value ->
                        switch (value) {
                            case MPS -> Optional.of(new Text[]{Text.literal("Meters per second (m/s):")});
                            case KPH -> Optional.of(new Text[]{Text.literal("Kilometers per hour (km/h):")});
                            case MPH -> Optional.of(new Text[]{Text.literal("Miles per hour (mph):")});
                            default -> Optional.empty();
                    })
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometerUnits = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //always use knot when player is in a boat
            speedometerSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Always use knot when player is in a boat"), ModConfig.getInstance().useKnotInBoat)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().useKnotInBoat = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display X
            speedometerSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().speedometerHudX)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometerHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display Y
            speedometerSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().speedometerHudY)
                    .setDefaultValue(70)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().speedometerHudY = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //reach category
            SubCategoryBuilder reachSubCategory = builder.entryBuilder().startSubCategory(Text.of("Reach"));
            //toggle
            reachSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show reach"), ModConfig.getInstance().showReach)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showReach = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            reachSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().reachColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reachColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            reachSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().reachShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reachShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //number of digits
            reachSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("Number of digits"), ModConfig.getInstance().reachDigits)
                    .setDefaultValue(2)
                    .setMin(0)
                    .setMax(16)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reachDigits = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display X
            reachSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().reachHudX)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reachHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display Y
            reachSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().reachHudY)
                    .setDefaultValue(100)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().reachHudY = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());


            //combo counter category
            SubCategoryBuilder comboCounterSubCategory = builder.entryBuilder().startSubCategory(Text.of("Combo Counter"));
            //toggle
            comboCounterSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Show combo counter"), ModConfig.getInstance().showComboCounter)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().showComboCounter = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //text color
            comboCounterSubCategory.add(builder.entryBuilder()
                    .startColorField(Text.of("Text color"), ModConfig.getInstance().comboCounterColor)
                    .setDefaultValue(0xFFFFFF) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().comboCounterColor = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //text shadow
            comboCounterSubCategory.add(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Text shadow"), ModConfig.getInstance().comboCounterShadow)
                    .setDefaultValue(true) // Used when user click "Reset"
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().comboCounterShadow = newValue;
                        ModConfig.saveConfig();
                    }) // Called when user save the config
                    .build());
            //display X
            comboCounterSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display X"), ModConfig.getInstance().comboCounterHudX)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().comboCounterHudX = newValue;
                        ModConfig.saveConfig();
                    })
                    .build());
            //display Y
            comboCounterSubCategory.add(builder.entryBuilder()
                    .startIntField(Text.of("display Y"), ModConfig.getInstance().comboCounterHudY)
                    .setDefaultValue(120)
                    .setSaveConsumer(newValue -> {
                        ModConfig.getInstance().comboCounterHudY = newValue;
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
            generalCategory.addEntry(memoryUsageSubCategory.build());
            generalCategory.addEntry(cpsSubCategory.build());
            generalCategory.addEntry(timeChangerSubCategory.build());
            generalCategory.addEntry(durabilityPingSubCategory.build());
            generalCategory.addEntry(speedometerSubCategory.build());
            generalCategory.addEntry(reachSubCategory.build());
            generalCategory.addEntry(comboCounterSubCategory.build());

            return builder.build();
        };
    }

    private boolean isValidClockFormat(String textFormat) {
        try {
            textFormat = textFormat.toLowerCase();
            if (ModConfig.getInstance().clock24hourformat) {
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
}
