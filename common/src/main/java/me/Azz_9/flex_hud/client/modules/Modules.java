package me.Azz_9.flex_hud.client.modules;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.client.modules.hud.AbstractMovableModule.AnchorPosition.*;

import net.minecraft.client.Camera;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.config.ConfigLoader;
import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.Configurable;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.config.option.ConfigInteger;
import me.Azz_9.flex_hud.client.modules.customModules.CustomModule;
import me.Azz_9.flex_hud.client.modules.hud.AbstractMovableModule;
import me.Azz_9.flex_hud.client.modules.hud.HudElement;
import me.Azz_9.flex_hud.client.modules.hud.custom.*;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.BossBar;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.Crosshair;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.Scoreboard;
import me.Azz_9.flex_hud.client.modules.hud.vanilla.Titles;
import me.Azz_9.flex_hud.client.modules.notHud.DurabilityPing;
import me.Azz_9.flex_hud.client.modules.notHud.TimeChanger;
import me.Azz_9.flex_hud.client.modules.notHud.TntCountdown;
import me.Azz_9.flex_hud.client.modules.notHud.WeatherChanger;

public class Modules {
	public ConfigBoolean isEnabled = new ConfigBoolean(true);
	//hud
	public ArmorStatus armorStatus = new ArmorStatus(2, -30, START, CENTER);
	public Cps cps = new Cps(-80, 2, END, START);
	public Clock clock = new Clock(-204, 2, END, START);
	public Fps fps = new Fps(2, 2, START, START);
	public Coordinates coordinates = new Coordinates(2, 15, START, START);
	public BiomeDisplay biomeDisplay = new BiomeDisplay(2, 45, START, START);
	public NetherCoordinates netherCoordinates = new NetherCoordinates(2, 60, START, START);
	public Compass compass = new Compass(0, 0, CENTER, START);
	public PitchDisplay pitchDisplay = new PitchDisplay(0, 0, END, CENTER);
	public DayCounter dayCounter = new DayCounter(148, 2, CENTER, START);
	public Ping ping = new Ping(-129, 2, END, START);
	public ServerAddress serverAddress = new ServerAddress(200, 2, START, START);
	public MemoryUsage memoryUsage = new MemoryUsage(75, 2, START, START);
	public Speedometer speedometer = new Speedometer(2, 70, START, START);
	public Reach reach = new Reach(2, 120, START, START);
	public Playtime playtime = new Playtime(2, 100, START, START);
	public ResourcePack resourcePack = new ResourcePack(0, 100, END, START);
	public PotionEffect potionEffect = new PotionEffect(0, 20, END, START);
	public Crosshair crosshair = new Crosshair();
	public BossBar bossBar = new BossBar(0, 35, CENTER, START);
	public WeatherDisplay weatherDisplay = new WeatherDisplay(-4, -4, END, END);
	public KeyStrokes keyStrokes = new KeyStrokes(-5, 68, END, START);
	public SignReader signReader = new SignReader(2, 60, START, CENTER);
	public FullInventoryIndicator fullInventoryIndicator = new FullInventoryIndicator(2, 96, START, CENTER);
	public LightLevel lightLevel = new LightLevel(2, 112, START, START);
	public InGameTime inGameTime = new InGameTime(-5, 2, END, START);
	public Distance distance = new Distance(0, 50, CENTER, START);
	public HeldItem heldItem = new HeldItem(0, -80, CENTER, END);
	public EntityCount entityCount = new EntityCount(0, -65, CENTER, END);
	public ToggleSprint toggleSprint = new ToggleSprint(-60, 12, END, START);
	public ToggleSneak toggleSneak = new ToggleSneak(-60, 22, END, START);
	public Scoreboard scoreboard = new Scoreboard(0, 0, END, CENTER);
	public InventoryDisplay inventoryDisplay = new InventoryDisplay(0, -50, CENTER, END);
	public Titles titles = new Titles(0, -22.5, CENTER, CENTER);
	public Tps tps = new Tps(200, 12, START, START);
	//others
	public WeatherChanger weatherChanger = new WeatherChanger();
	public TimeChanger timeChanger = new TimeChanger();
	public DurabilityPing durabilityPing = new DurabilityPing();
	public TntCountdown tntCountdown = new TntCountdown();

	//number of columns
	public ConfigInteger numberOfColumns = new ConfigInteger(2);

	static Modules INSTANCE;

	private List<AbstractModule> modules;
	private List<HudElement> hudElements;
	private List<AbstractMovableModule> movableModules;
	private List<Configurable> configurables;
	private List<TickableModule> tickableModules;
	private List<CustomModule> customModules;

	public Modules() {
		ConfigRegistry.register("global", "enabled", isEnabled);
		ConfigRegistry.register("global", "numberOfColumns", numberOfColumns);
	}

	private void init() {
		hudElements = new ArrayList<>();
		movableModules = new ArrayList<>();
		configurables = new ArrayList<>();
		tickableModules = new ArrayList<>();
		customModules = new ArrayList<>();

		modules = new ArrayList<>(List.of(
				getInstance().armorStatus,
				getInstance().cps,
				getInstance().clock,
				getInstance().fps,
				getInstance().coordinates,
				getInstance().biomeDisplay,
				getInstance().netherCoordinates,
				getInstance().compass,
				getInstance().pitchDisplay,
				getInstance().dayCounter,
				getInstance().ping,
				getInstance().serverAddress,
				getInstance().memoryUsage,
				getInstance().speedometer,
				getInstance().reach,
				getInstance().playtime,
				getInstance().resourcePack,
				getInstance().potionEffect,
				getInstance().weatherDisplay,
				getInstance().keyStrokes,
				getInstance().bossBar,
				getInstance().signReader,
				getInstance().fullInventoryIndicator,
				getInstance().lightLevel,
				getInstance().inGameTime,
				getInstance().distance,
				getInstance().heldItem,
				getInstance().entityCount,
				getInstance().toggleSprint,
				getInstance().toggleSneak,
				getInstance().scoreboard,
				getInstance().inventoryDisplay,
				getInstance().titles,
				getInstance().tps,
				getInstance().weatherChanger,
				getInstance().timeChanger,
				getInstance().crosshair,
				getInstance().durabilityPing,
				getInstance().tntCountdown
		));

		for (AbstractModule module : modules) {
			if (module instanceof HudElement hudElement) hudElements.add(hudElement);
			if (module instanceof AbstractMovableModule movableModule) movableModules.add(movableModule);
			if (module instanceof Configurable configurable) configurables.add(configurable);
			if (module instanceof TickableModule tickableModule) tickableModules.add(tickableModule);
		}
	}

	public static void addCustomModule(CustomModule module) {
		getInstance().customModules.add(module);
		getInstance().modules.add(module);
		getInstance().hudElements.add(module);
		getInstance().movableModules.add(module);
		getInstance().configurables.add(module);
	}

	public static void removeCustomModule(CustomModule module) {
		getInstance().customModules.remove(module);
		getInstance().modules.remove(module);
		getInstance().hudElements.remove(module);
		getInstance().movableModules.remove(module);
		getInstance().configurables.remove(module);
	}

	public static void recompileCustomModules() {
		if (INSTANCE == null || INSTANCE.customModules == null) {
			return;
		}

		for (CustomModule module : INSTANCE.customModules) {
			module.recompile();
		}
	}

	// Méthode pour obtenir l'instance de la configuration
	public static Modules getInstance() {
		return getInstance(true);
	}

	public static Modules getInstance(boolean loadConfig) {
		if (INSTANCE == null) {
			INSTANCE = new Modules();
			if (loadConfig) {
				ConfigLoader.loadConfig();
			}
			INSTANCE.init();
		}
		return INSTANCE;
	}

	public static List<AbstractModule> getModules() {
		return getInstance().modules;
	}

	public static List<HudElement> getHudElements() {
		return getInstance().hudElements;
	}

	public static List<AbstractMovableModule> getMovableModules() {
		return getInstance().movableModules;
	}

	public static List<Configurable> getConfigurableModules() {
		return getInstance().configurables;
	}

	public static List<TickableModule> getTickables() {
		return getInstance().tickableModules;
	}

	public static List<CustomModule> getCustomModules() {
		return getInstance().customModules;
	}

	public static void onTimeOrWeatherUpdate() {
		if (MINECRAFT.level != null) {
			MINECRAFT.level.environmentAttributes().invalidateTickCache();

			Camera camera = MINECRAFT.gameRenderer.mainCamera();
			// 2 ticks are needed to make the sun/moon move to the right place and
			// the sky have the right color based on the weather for whatever reason
			camera.attributeProbe().tick(MINECRAFT.level, camera.position());
			camera.attributeProbe().tick(MINECRAFT.level, camera.position());
		}
	}
}
