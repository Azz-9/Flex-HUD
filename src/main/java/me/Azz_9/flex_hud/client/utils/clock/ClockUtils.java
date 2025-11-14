package me.Azz_9.flex_hud.client.utils.clock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ClockUtils {

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
}
