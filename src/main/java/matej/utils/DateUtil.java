package matej.utils;

import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
* This utility class contains static methods used for date calculation
*
* @author  MatejDanic
* @version 1.0
* @since   2020-08-13
*/
public final class DateUtil {

	/**
   * Determines if two date objects of type LocalDateTime are in the same week of the year.
   * 
   * @param date1 the first date object of type LocalDateTime
   * @param date2  the second date object of type LocalDateTime
   * @return {@code true} if both date object are in the same week of the year
   * @exception IllegalArgumentException if either of the date objects equal null
   */
	public static boolean isSameWeek(LocalDateTime date1, LocalDateTime date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("Datumi nisu valjani");
		}
		TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
		return (date1.getYear() == date2.getYear() &&
				date1.get(woy) == date2.get(woy));
	}
}