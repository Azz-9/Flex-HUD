package me.Azz_9.flex_hud.client.utils.clock;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ClockUtils {
	private static String formattedTime;

	public static boolean is24HourFormat(Locale locale) {
		// Obtenir le format d'heure court pour la locale
		DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);

		if (timeFormat instanceof SimpleDateFormat simpleDateFormat) {
			String pattern = simpleDateFormat.toPattern().toLowerCase();

			// Si le pattern contient 'a', il utilise AM/PM → format 12h
			// Sinon, c'est probablement un format 24h
			return !pattern.contains("a");
		}

		// Par défaut, on suppose 24h si on ne peut pas analyser le pattern
		return true;
	}

	public static void updateTime() {
		String textFormat = ModulesHelper.getInstance().clock.textFormat.getValue().toLowerCase();
		if (ModulesHelper.getInstance().clock.isTwentyFourHourFormat.getValue()) {
			textFormat = textFormat.replace("hh", "HH").replace("h", "HH");
		} else {
			textFormat += " a";
		}
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(textFormat);
			formattedTime = LocalTime.now().format(formatter);
		} catch (Exception e) {
			// if the text format is not valid, reset to default
			ModulesHelper.getInstance().clock.textFormat.setToDefault();
		}
	}

	public static String getFormattedTime() {
		return formattedTime;
	}
}
