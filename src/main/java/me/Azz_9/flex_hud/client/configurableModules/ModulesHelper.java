package me.Azz_9.flex_hud.client.configurableModules;

import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.TickableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.AbstractHudElement;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.MovableModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.*;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.vanilla.BossBar;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.vanilla.Crosshair;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.TimeChanger;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.TntCountdown;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.WeatherChanger;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.durabilityPing.DurabilityPing;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;

import java.util.ArrayList;
import java.util.List;

public class ModulesHelper {
	public ConfigBoolean isEnabled = new ConfigBoolean(true);
	//hud
	public ArmorStatus armorStatus = new ArmorStatus(2, -30, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.CENTER);
	public Cps cps = new Cps(-80, 2, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public Clock clock = new Clock(-204, 2, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public Fps fps = new Fps(2, 2, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public Coordinates coordinates = new Coordinates(2, 15, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public NetherCoordinates netherCoordinates = new NetherCoordinates(2, 60, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public Compass compass = new Compass(0, 0, AbstractHudElement.AnchorPosition.CENTER, AbstractHudElement.AnchorPosition.START);
	public DayCounter dayCounter = new DayCounter(148, 2, AbstractHudElement.AnchorPosition.CENTER, AbstractHudElement.AnchorPosition.START);
	public Ping ping = new Ping(-129, 2, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public ServerAddress serverAddress = new ServerAddress(200, 2, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public MemoryUsage memoryUsage = new MemoryUsage(75, 2, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public Speedometer speedometer = new Speedometer(2, 70, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public Reach reach = new Reach(2, 120, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public Playtime playtime = new Playtime(2, 100, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	//public ResourcePack resourcePack = new ResourcePack(0, 100, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public PotionEffect potionEffect = new PotionEffect(0, 20, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public Crosshair crosshair = new Crosshair();
	public BossBar bossBar = new BossBar(0, 35, AbstractHudElement.AnchorPosition.CENTER, AbstractHudElement.AnchorPosition.START);
	public WeatherDisplay weatherDisplay = new WeatherDisplay(-4, -4, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.END);
	public KeyStrokes keyStrokes = new KeyStrokes(-5, 68, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public SignReader signReader = new SignReader(2, 60, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.CENTER);
	public FullInventoryIndicator fullInventoryIndicator = new FullInventoryIndicator(2, 96, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.CENTER);
	public LightLevel lightLevel = new LightLevel(2, 112, AbstractHudElement.AnchorPosition.START, AbstractHudElement.AnchorPosition.START);
	public InGameTime inGameTime = new InGameTime(-5, 2, AbstractHudElement.AnchorPosition.END, AbstractHudElement.AnchorPosition.START);
	public Distance distance = new Distance(0, 50, AbstractHudElement.AnchorPosition.CENTER, AbstractHudElement.AnchorPosition.START);
	public HeldItem heldItem = new HeldItem(0, -60, AbstractHudElement.AnchorPosition.CENTER, AbstractHudElement.AnchorPosition.END);
	//others
	public WeatherChanger weatherChanger = new WeatherChanger();
	public TimeChanger timeChanger = new TimeChanger();
	public DurabilityPing durabilityPing = new DurabilityPing();
	public TntCountdown tntCountdown = new TntCountdown();

	//number of columns
	public ConfigInteger numberOfColumns = new ConfigInteger(2);

	static ModulesHelper INSTANCE;

	private List<AbstractModule> modules;
	private List<AbstractHudElement> hudElements;
	private List<MovableModule> movableModules;
	private List<Configurable> configurables;
	private List<TickableModule> tickableModules;

	public ModulesHelper() {
		ConfigRegistry.register("global", "enabled", isEnabled);
		ConfigRegistry.register("global", "numberOfColumns", numberOfColumns);
	}

	private void init() {
		hudElements = new ArrayList<>();
		movableModules = new ArrayList<>();
		configurables = new ArrayList<>();
		tickableModules = new ArrayList<>();

		modules = List.of(
				getInstance().armorStatus,
				getInstance().cps,
				getInstance().clock,
				getInstance().fps,
				getInstance().coordinates,
				getInstance().netherCoordinates,
				getInstance().compass,
				getInstance().dayCounter,
				getInstance().ping,
				getInstance().serverAddress,
				getInstance().memoryUsage,
				getInstance().speedometer,
				getInstance().reach,
				getInstance().playtime,
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
				getInstance().weatherChanger,
				getInstance().timeChanger,
				getInstance().crosshair,
				getInstance().tntCountdown
		);

		for (AbstractModule module : modules) {
			if (module instanceof AbstractHudElement hudElement) hudElements.add(hudElement);
			if (module instanceof MovableModule movableModule) movableModules.add(movableModule);
			if (module instanceof Configurable configurable) configurables.add(configurable);
			if (module instanceof TickableModule tickableModule) tickableModules.add(tickableModule);
		}
	}

	// Méthode pour obtenir l'instance de la configuration
	public static ModulesHelper getInstance() {
		if (INSTANCE == null) {
			ConfigLoader.loadConfig();
			INSTANCE.init();
		}
		return INSTANCE;
	}

	public static List<AbstractModule> getModules() {
		return getInstance().modules;
	}

	public static List<AbstractHudElement> getHudElements() {
		return getInstance().hudElements;
	}

	public static List<MovableModule> getMovableModules() {
		return getInstance().movableModules;
	}

	public static List<Configurable> getConfigurableModules() {
		return getInstance().configurables;
	}

	public static List<TickableModule> getTickables() {
		return getInstance().tickableModules;
	}
}
