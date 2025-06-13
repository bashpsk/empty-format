package io.bashpsk.emptyformat

import android.content.Context
import android.text.format.Formatter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import io.bashpsk.emptyformat.EmptyFormat.time
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * `EmptyFormat` is a utility object designed to format and parse date and time values according to
 * various predefined patterns. It provides methods to convert between `Long` (milliseconds),
 * `LocalDateTime`, `LocalTime` and formatted `String` representations. It also allows parsing
 * formatted `String` back to `Long` representation.
 *
 * The class offers a range of formatting patterns to handle different date and time display
 * requirements, such as:
 * - 24-hour and 12-hour time formats
 * - Short and full date formats
 * - Formats suitable for file names
 *
 * It utilizes the `kotlinx-datetime` library for date and time handling and allows custom month and
 * day-of-week names.
 */
@OptIn(ExperimentalTime::class)
object EmptyFormat {

    /**
     * Enum class defining the various formatting patterns supported by `EmptyFormat`.
     */
    enum class Pattern {

        /**
         * Time in 24-hour format.
         * Example: 07:30.
         */
        TIME_HH_MM,

        /**
         * Time in 24-hour format, including seconds.
         * Example: 07:30:45.
         */
        TIME_HH_MM_SS,

        /**
         * Time in 12-hour format with AM/PM indicator.
         * Example: 07:30:45 PM.
         */
        TIME_12,

        /**
         * Short date format.
         * Example: 09:12:2000.
         */
        SHORT_DATE,

        /**
         * Short date and time in 12-hour format.
         * Example: 09:12:2000 07:30 PM.
         */
        SHORT_DATE_TIME,

        /**
         * Full date and time in 12-hour format.
         * Example: Sunday, December 09, 2000 07:30 PM.
         */
        FULL_DATE_TIME,

        /**
         * Date and time format suitable for file names, in 24-hour format.
         * Example: 19-30-45 09-12-2000.
         */
        FILE_NAME
    }

    /**
     * Formats a date and time represented by milliseconds since the epoch into a `String` based on
     * the specified pattern.
     *
     * @param dateTimeMillis The date and time in milliseconds since the epoch.
     * @param pattern The formatting pattern to apply.
     * @return The formatted date and time `String`.
     */
    @JvmStatic
    fun dateTime(dateTimeMillis: Long, pattern: Pattern): String {

        val instant = Instant.fromEpochMilliseconds(epochMilliseconds = dateTimeMillis)
        val localDateTime = instant.toLocalDateTime(timeZone = TimeZone.currentSystemDefault())

        return dateTime(localDateTime = localDateTime, pattern = pattern)
    }

    /**
     * Formats a `LocalDateTime` object into a `String` based on the specified pattern.
     *
     * @param localDateTime The `LocalDateTime` object to format.
     * @param pattern The formatting pattern to apply.
     * @return The formatted date and time `String`.
     */
    @JvmStatic
    fun dateTime(localDateTime: LocalDateTime, pattern: Pattern): String {

        val format = when (pattern) {

            Pattern.TIME_HH_MM -> {

                LocalDateTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                }
            }

            Pattern.TIME_HH_MM_SS -> {

                LocalDateTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                }
            }

            Pattern.TIME_12 -> {

                LocalDateTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(' ')
                    amPmMarker(am = "AM", pm = "PM")
                }
            }

            Pattern.SHORT_DATE -> {

                LocalDateTime.Format {

                    dayOfMonth(padding = Padding.ZERO)
                    char(value = ':')
                    monthNumber(padding = Padding.ZERO)
                    char(value = ':')
                    year(padding = Padding.ZERO)
                }
            }

            Pattern.SHORT_DATE_TIME -> {

                LocalDateTime.Format {

                    dayOfMonth(padding = Padding.ZERO)
                    char(value = ':')
                    monthNumber(padding = Padding.ZERO)
                    char(value = ':')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmMarker(am = "AM", pm = "PM")
                }
            }

            Pattern.FULL_DATE_TIME -> {

                val dayOfWeekNames = DayOfWeekNames(
                    monday = "Monday",
                    tuesday = "Tuesday",
                    wednesday = "Wednesday",
                    thursday = "Thursday",
                    friday = "Friday",
                    saturday = "Saturday",
                    sunday = "Sunday"
                )

                /*val monthNames = MonthNames(
                    january = "January",
                    february = "February",
                    march = "March",
                    april = "April",
                    may = "May",
                    june = "June",
                    july = "July",
                    august = "August",
                    september = "September",
                    october = "October",
                    november = "November",
                    december = "December"
                )*/

                val monthNames = MonthNames(
                    january = "Jan",
                    february = "Feb",
                    march = "Mar",
                    april = "Apr",
                    may = "May",
                    june = "Jun",
                    july = "Jul",
                    august = "Aug",
                    september = "Sep",
                    october = "Oct",
                    november = "Nov",
                    december = "Dec"
                )

                LocalDateTime.Format {

                    dayOfWeek(names = dayOfWeekNames)
                    char(value = ',')
                    char(value = ' ')
                    monthName(names = monthNames)
                    char(value = ' ')
                    dayOfMonth(padding = Padding.ZERO)
                    char(value = ',')
                    char(value = ' ')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmMarker(am = "AM", pm = "PM")
                }
            }

            Pattern.FILE_NAME -> {

                LocalDateTime.Format {

                    dayOfMonth(padding = Padding.ZERO)
                    char(value = '-')
                    monthNumber(padding = Padding.ZERO)
                    char(value = '-')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    hour(padding = Padding.ZERO)
                    char(value = '-')
                    minute(padding = Padding.ZERO)
                    char(value = '-')
                    second(padding = Padding.ZERO)
                }
            }

            else -> {

                LocalDateTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(' ')
                    amPmMarker(am = "AM", pm = "PM")
                }
            }
        }

        return localDateTime.format(format = format)
    }

    /**
     * Parses a formatted date and time `String` into milliseconds since the epoch based on the
     * specified pattern.
     *
     * @param dateTime The formatted date and time `String`.
     * @param pattern
     * @return The date and time in milliseconds since the epoch.
     * @throws IllegalArgumentException if the input `String` does not match the specified pattern.
     */
    @JvmStatic
    fun dateTimeToMilliseconds(dateTime: String, pattern: Pattern): Long {

        val format = when (pattern) {

            Pattern.TIME_HH_MM -> {

                LocalDateTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                }
            }

            Pattern.TIME_HH_MM_SS -> {

                LocalDateTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                }
            }

            Pattern.TIME_12 -> {

                LocalDateTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(' ')
                    amPmMarker(am = "AM", pm = "PM")
                }
            }

            Pattern.SHORT_DATE -> {

                LocalDateTime.Format {

                    dayOfMonth(padding = Padding.ZERO)
                    char(value = ':')
                    monthNumber(padding = Padding.ZERO)
                    char(value = ':')
                    year(padding = Padding.ZERO)
                }
            }

            Pattern.SHORT_DATE_TIME -> {

                LocalDateTime.Format {

                    dayOfMonth(padding = Padding.ZERO)
                    char(value = ':')
                    monthNumber(padding = Padding.ZERO)
                    char(value = ':')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmMarker(am = "AM", pm = "PM")
                }
            }

            Pattern.FULL_DATE_TIME -> {

                val dayOfWeekNames = DayOfWeekNames(
                    monday = "Monday",
                    tuesday = "Tuesday",
                    wednesday = "Wednesday",
                    thursday = "Thursday",
                    friday = "Friday",
                    saturday = "Saturday",
                    sunday = "Sunday"
                )

                /*val monthNames = MonthNames(
                    january = "January",
                    february = "February",
                    march = "March",
                    april = "April",
                    may = "May",
                    june = "June",
                    july = "July",
                    august = "August",
                    september = "September",
                    october = "October",
                    november = "November",
                    december = "December"
                )*/

                val monthNames = MonthNames(
                    january = "Jan",
                    february = "Feb",
                    march = "Mar",
                    april = "Apr",
                    may = "May",
                    june = "Jun",
                    july = "Jul",
                    august = "Aug",
                    september = "Sep",
                    october = "Oct",
                    november = "Nov",
                    december = "Dec"
                )

                LocalDateTime.Format {

                    dayOfWeek(names = dayOfWeekNames)
                    char(value = ',')
                    char(value = ' ')
                    monthName(names = monthNames)
                    char(value = ' ')
                    dayOfMonth(padding = Padding.ZERO)
                    char(value = ',')
                    char(value = ' ')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmMarker(am = "AM", pm = "PM")
                }
            }

            Pattern.FILE_NAME -> {

                LocalDateTime.Format {

                    dayOfMonth(padding = Padding.ZERO)
                    char(value = '-')
                    monthNumber(padding = Padding.ZERO)
                    char(value = '-')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    hour(padding = Padding.ZERO)
                    char(value = '-')
                    minute(padding = Padding.ZERO)
                    char(value = '-')
                    second(padding = Padding.ZERO)
                }
            }

            else -> {

                LocalDateTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(' ')
                    amPmMarker(am = "AM", pm = "PM")
                }
            }
        }

        return try {

            LocalDateTime.parse(
                input = dateTime,
                format = format
            ).toInstant(timeZone = TimeZone.currentSystemDefault()).toEpochMilliseconds()
        } catch (exception: Exception) {

            SetLog.setError(message = exception.message, throwable = exception)

            0L
        }
    }

    /**
     * Formats a time represented by milliseconds into a `String` based on the specified pattern.
     *
     * @param time The time in milliseconds.
     * @param pattern The formatting pattern to apply.
     * @return The formatted time `String`.
     */
    @JvmStatic
    fun time(time: Long, pattern: Pattern): String {

        val hours = time.toDuration(unit = DurationUnit.MILLISECONDS).inWholeHours
        val minutes = time.toDuration(unit = DurationUnit.MILLISECONDS).inWholeMinutes % 60
        val seconds = time.toDuration(unit = DurationUnit.MILLISECONDS).inWholeSeconds % 60

        val nanoseconds = time.toDuration(
            unit = DurationUnit.MILLISECONDS
        ).inWholeNanoseconds % 1_000_000_000

        val localTime = LocalTime(
            hour = hours.toInt(),
            minute = minutes.toInt(),
            second = seconds.toInt(),
            nanosecond = nanoseconds.toInt()
        )

        return time(localTime = localTime, pattern = pattern)
    }

    /**
     * Formats a time represented by milliseconds into a `String` based on the specified pattern.
     *
     * @param localTime The `LocalTime` object to format.
     * @param pattern The formatting pattern to apply.
     * @return The formatted time `String`.
     */
    @JvmStatic
    fun time(localTime: LocalTime, pattern: Pattern): String {

        val format = when (pattern) {

            Pattern.TIME_HH_MM -> {

                LocalTime.Format {

                    hour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                }
            }

            Pattern.TIME_HH_MM_SS -> {

                LocalTime.Format {

                    hour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                }
            }

            Pattern.TIME_12 -> {

                LocalTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(' ')
                    amPmMarker(am = "AM", pm = "PM")
                }
            }

            else -> {

                LocalTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(' ')
                    amPmMarker(am = "AM", pm = "PM")
                }
            }
        }

        return localTime.format(format = format)
    }

    /**
     * Converts a time span, provided in hours, minutes, and seconds, into its total
     * equivalent in milliseconds.
     *
     * @param hours The number of hours in the time span.
     * @param minutes The number of minutes in the time span.
     * @param seconds The number of seconds in the time span.
     * @return The total number of milliseconds equivalent to the input time span or 0L if an
     * exception occurs.
     */
    @JvmStatic
    fun timeToMilliseconds(hours: Int, minutes: Int, seconds: Int): Long {

        return try {

            val hourMillis = Duration.convert(
                value = hours.toDouble(),
                sourceUnit = DurationUnit.HOURS,
                targetUnit = DurationUnit.MILLISECONDS
            ).toLong()

            val minuteMillis = Duration.convert(
                value = minutes.toDouble(),
                sourceUnit = DurationUnit.MINUTES,
                targetUnit = DurationUnit.MILLISECONDS
            ).toLong()

            val secondMillis = Duration.convert(
                value = seconds.toDouble(),
                sourceUnit = DurationUnit.SECONDS,
                targetUnit = DurationUnit.MILLISECONDS
            ).toLong()

            hourMillis + minuteMillis + secondMillis
        } catch (exception: Exception) {

            SetLog.setError(message = exception.message, throwable = exception)

            0L
        }
    }

    /**
     * Converts an integer representing a time value into a two-digit string.
     *
     * This function uses the default locale to format the time value as a string
     * with leading zeros if the value is less than 10, ensuring a consistent
     * two-digit representation (e.g., "01", "09", "10", "25").
     *
     * If any exception occurs during the formatting process, it logs the error
     * and returns "00" as a default fallback value.
     *
     * @param time The integer representing the time value to format.
     * @return A string representing the formatted time, always two digits long.
     *         Returns "00" in case of an exception.
     *
     * @throws Exception If any error occurred during the formatting process.
     */
    @JvmStatic
    fun toRoundTime(time: Int): String {

        return try {

            String.format(locale = Locale.getDefault(), format = "%02d", time)
        } catch (exception: Exception) {

            SetLog.setError(message = exception.message, throwable = exception)

            "00"
        }
    }

    /**
     * Converts a duration in milliseconds into a formatted string representation.
     *
     * This function takes a long representing milliseconds and formats it into a string
     * in one of the following formats:
     * - "0:0" if the input is 0 milliseconds.
     * - "MM:SS" if the duration is less than one hour, where MM is the minutes and SS is the
     * seconds.
     * - "HH:MM:SS" if the duration is one hour or more, where HH is the hours, MM is the minutes,
     *   and SS is the seconds.
     *
     * @param millis The duration in milliseconds to format.
     * @return A formatted string representation of the duration.
     */
    @JvmStatic
    fun millisecondsToDuration(millis: Long): String {

        return when {

            millis == 0L -> {

                "0:0"
            }

            millis < 3600000L -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%02d:%02d",
                    millis.toDuration(unit = DurationUnit.MILLISECONDS).inWholeMinutes % 60,
                    millis.toDuration(unit = DurationUnit.MILLISECONDS).inWholeSeconds % 60
                )
            }

            else -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%02d:%02d:%02d",
                    millis.toDuration(unit = DurationUnit.MILLISECONDS).inWholeHours,
                    millis.toDuration(unit = DurationUnit.MILLISECONDS).inWholeMinutes % 60,
                    millis.toDuration(unit = DurationUnit.MILLISECONDS).inWholeSeconds % 60
                )
            }
        }
    }

    /**
     * Retrieves the start of the current day in milliseconds since the epoch.
     *
     * This function utilizes the `kotlinx-datetime` library to determine the current system's
     * default timezone, obtain the current date and time, and calculate the start of the current
     * day.
     *
     * The returned value represents the number of milliseconds elapsed between the Unix epoch
     * (January 1, 1970, 00:00:00 UTC) and the start of the current day in the system's default
     * timezone.
     *
     * @return A [Long] representing the start of the current day in milliseconds.
     *
     * @throws IllegalStateException if there is an error in obtaining the current timezone or
     * system time.
     */
    @JvmStatic
    fun getTodayStartMillis(): Long {

        val timeZone = TimeZone.currentSystemDefault()
        val localDateTime = Clock.System.now().toLocalDateTime(timeZone = timeZone)
        val startOfDay = localDateTime.date.atStartOfDayIn(timeZone = timeZone)

        return startOfDay.toEpochMilliseconds()
    }

    /**
     * Calculates the end-of-day timestamp in milliseconds for the current day.
     *
     * This function utilizes the `kotlinx-datetime` library to determine the current system's
     * default timezone, obtain the current date and time, and calculate the end of the current
     * day.
     *
     * @return [Long] The timestamp representing the last millisecond of the current day.
     */
    @JvmStatic
    fun getTodayEndMillis(): Long {

        val timeZone = TimeZone.currentSystemDefault()
        val localDateTime = Clock.System.now().toLocalDateTime(timeZone = timeZone)

        val endOfDay = localDateTime.date.plus(period = DatePeriod(days = 1)).atStartOfDayIn(
            timeZone = timeZone
        ).minus(value = 1, unit = DateTimeUnit.NANOSECOND)

        return endOfDay.toEpochMilliseconds()
    }

    /**
     * Calculates the start of the day in milliseconds for a given [LocalDateTime].
     *
     * This function takes a [LocalDateTime] object and returns the corresponding
     * time in milliseconds that represents the start of that day in the current system's
     * default timezone.
     *
     * @param localDateTime The [LocalDateTime] for which to calculate the start of the day.
     * @return A [Long] representing the start of the day in milliseconds since the epoch.
     */
    @JvmStatic
    fun getDayStartMillis(localDateTime: LocalDateTime): Long {

        val timeZone = TimeZone.currentSystemDefault()
        val startOfDay = localDateTime.date.atStartOfDayIn(timeZone = timeZone)

        return startOfDay.toEpochMilliseconds()
    }

    /**
     * Calculates the end of the day in epoch milliseconds for a given [LocalDateTime].
     *
     * This function takes a [LocalDateTime] object and returns the corresponding
     * time in milliseconds that represents the end of that day in the current system's
     * default timezone.
     *
     * @param localDateTime The [LocalDateTime] for which to find the end of the day.
     * @return A [Long] representing the end of the day in milliseconds since the epoch.
     */
    @JvmStatic
    fun getDayEndMillis(localDateTime: LocalDateTime): Long {

        val timeZone = TimeZone.currentSystemDefault()

        val endOfDay = localDateTime.date.plus(period = DatePeriod(days = 1)).atStartOfDayIn(
            timeZone = timeZone
        ).minus(value = 1, unit = DateTimeUnit.NANOSECOND)

        return endOfDay.toEpochMilliseconds()
    }

    /**
     * Rounds a `Double` to a specified number of decimal places.
     *
     * This function takes a `Double` value and rounds it to the nearest decimal
     * place specified by the `fraction` parameter. It uses the default locale
     * for formatting. If an exception occurs during the rounding process, it
     * will log the error using `SetLog.setError()` and return 0.0.
     *
     * @param decimal The `Double` value to be rounded.
     * @param fraction The number of decimal places to round to. Defaults to 0.
     * @return The rounded `Double` value, or 0.0 if an error occurs.
     */
    @JvmStatic
    fun toRoundedDecimal(decimal: Double, fraction: Int = 0): Double {

        return try {

            String.format(
                locale = Locale.getDefault(),
                format = "%.${fraction}f",
                decimal
            ).toDouble()
        } catch (exception: Exception) {

            SetLog.setError(message = exception.message, throwable = exception)

            0.0
        }
    }

    /**
     * Rounds a `Float` to a specified number of decimal places.
     *
     * Similar to `toRoundedDecimal(Double, Int)`, this function rounds a `Float`
     * value to the nearest decimal place specified by the `fraction` parameter.
     * It also uses the default locale for formatting. If an error occurs during
     * rounding, it will log the error using `SetLog.setError()` and return 0F.
     *
     * @param decimal The `Float` value to be rounded.
     * @param fraction The number of decimal places to round to. Defaults to 0.
     * @return The rounded `Float` value, or 0F if an error occurs.
     */
    @JvmStatic
    fun toRoundedDecimal(decimal: Float, fraction: Int = 0): Float {

        return try {

            String.format(
                locale = Locale.getDefault(),
                format = "%.${fraction}f",
                decimal
            ).toFloat()
        } catch (exception: Exception) {

            SetLog.setError(message = exception.message, throwable = exception)

            0F
        }
    }

    /**
     * Formats a file size represented as a Long into a human-readable string using the
     * system's default formatting.
     *
     * This method utilizes the Android framework's `Formatter` class to format the provided
     * `size` into a file size string appropriate for the user's locale.
     *
     * @param context The application's context, used by the underlying `Formatter`.
     * @param size The file size in bytes.
     * @return A string representing the formatted file size (e.g., "1.5 MB", "1024 KB").
     *
     * @see Formatter.formatFileSize
     */
    @JvmStatic
    fun toFileSize(context: Context, size: Long): String {

        return Formatter.formatFileSize(context, size)
    }

    /**
     * Formats a file size represented as a Long into a human-readable string.
     *
     * This method converts the provided `size` (in bytes) into a string with the appropriate
     * magnitude suffix (B, KB, MB, GB, TB, PB, EB, ZB, YB). The output string is formatted
     * to two decimal places and uses the current locale's formatting.
     *
     * @param size The file size in bytes.
     * @return A string representing the formatted file size (e.g., "1.50 MB", "1024.00 KB").
     */
    @JvmStatic
    fun toFileSize(size: Long): String {

        val units = listOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")

        var length = size.toDouble()
        var order = 0

        while (length >= 1024 && order < units.size - 1) {

            order++
            length /= 1024
        }

        return String.format(locale = Locale.getDefault(), format = "%.2f %s", length, units[order])
    }

    /**
     * Formats a `Long` value into a human-readable string with scaling suffixes.
     *
     * @param value The `Long` value to format.
     * @return A formatted `String` representation of the input value.
     * Example:
     * - 1234 -> "1.2K"
     * - 1234567 -> "1.2M"
     * - 123 -> "123"
     */
    @JvmStatic
    fun formatNumber(value: Long): String {

        return when {

            value >= 1_000_000_000_000_000 -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%.1fQ",
                    value / 1_000_000_000_000_000.0
                )
            }

            value >= 1_000_000_000_000 -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%.1fT",
                    value / 1_000_000_000_000.0
                )
            }

            value >= 1_000_000_000 -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%.1fB",
                    value / 1_000_000_000.0
                )
            }

            value >= 1_000_000 -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%.1fM",
                    value / 1_000_000.0
                )
            }

            value >= 1_000 -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%.1fK",
                    value / 1_000.0
                )
            }

            else -> {

                value.toString()
            }
        }
    }

    /**
     * Formats a `Double` value into a human-readable string with scaling suffixes.
     *
     * @param value The `Double` value to format.
     * @return A formatted `String` representation of the input value.
     * Example:
     * - 1234 -> "1.2K"
     * - 1234567 -> "1.2M"
     * - 123 -> "123"
     */
    @JvmStatic
    fun formatNumber(value: Double): String {

        return when {

            value >= 1_000_000_000_000_000 -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%.1fQ",
                    value / 1_000_000_000_000_000.0
                )
            }

            value >= 1_000_000_000_000 -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%.1fT",
                    value / 1_000_000_000_000.0
                )
            }

            value >= 1_000_000_000 -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%.1fB",
                    value / 1_000_000_000.0
                )
            }

            value >= 1_000_000 -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%.1fM",
                    value / 1_000_000.0
                )
            }

            value >= 1_000 -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%.1fK",
                    value / 1_000.0
                )
            }

            else -> {

                String.format(
                    locale = Locale.getDefault(),
                    format = "%.1f",
                    value
                )
            }
        }
    }

    /**
     * Formats an `Int` value into a human-readable string with scaling suffixes.
     *
     * @param value The `Int` value to format.
     * @return A formatted `String` representation of the input value.
     * Example:
     * - 1234 -> "1.2K"
     * - 1234567 -> "1.2M"
     * - 123 -> "123"
     */
    @JvmStatic
    fun formatNumber(value: Int): String {

        return formatNumber(value = value.toLong())
    }

    /**
     * Converts a Color object to its hexadecimal string representation.
     *
     * This function takes a Color object and returns a string representing the color
     * in hexadecimal format (e.g., "#RRGGBB" or "#AARRGGBB"). The output format
     * depends on whether the color has an alpha (transparency) component.
     *
     * @param color The Color object to convert.
     * @return A string representing the hexadecimal value of the color, including
     * the alpha component if it is not fully opaque. Returns "#000000" for null input.
     * @sample
     *     val opaqueRed = Color.rgb(255, 0, 0)
     *     val hexRed = toColorHex(opaqueRed) // Returns "#FF0000"
     *
     *     val transparentBlue = Color.argb(128, 0, 0, 255)
     *     val hexTransparentBlue = toColorHex(transparentBlue) // Returns "#800000FF"
     *
     *     val nullColor: Color? = null
     *     val hexNull = toColorHex(nullColor) // Returns "#000000"
     */
    @JvmStatic
    fun toColorHex(color: Color): String {

        val alpha = (color.alpha * 255).toInt()
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()

        return String.format(
            locale = Locale.getDefault(),
            format = "%02X%02X%02X%02X",
            alpha,
            red,
            green,
            blue
        )
    }

    /**
     * Converts a hexadecimal color string to an Android [Color] object.
     *
     * This function supports hexadecimal color strings in the following formats:
     * - `#RRGGBB` (e.g., "#FF0000" for red)
     * - `#AARRGGBB` (e.g., "#80FF0000" for semi-transparent red)
     * - `#RGB` (e.g., "#F00" for red, shorthand notation)
     * - `#ARGB` (e.g., "#8F00" for semi-transparent red, shorthand notation)
     *
     * The hexadecimal values are case-insensitive.
     *
     * @param hex The hexadecimal color string to convert. Must start with '#'.
     * @return The corresponding [Color] object.
     */
    @JvmStatic
    fun hexToColor(hex: String): Color {

        return try {

            Color(color = hex.toLong(radix = 16))
        } catch (exception: Exception) {

            SetLog.setError(message = exception.message, throwable = exception)

            Color.Unspecified
        }
    }

    /**
     * Converts a hexadecimal color string to an Android color integer.
     *
     * This function takes a hexadecimal color string as input and returns the corresponding
     * Android color integer. The input string can be in the following formats:
     * - "#RRGGBBAA" (e.g., "#FF0000FF" for red with full opacity)
     *
     * If the input string is not in a valid hexadecimal format, or if it does not represent
     * a valid color, the function will return an [Color.Unspecified].
     *
     * @param hex The hexadecimal color string.
     * @return The Android color integer.
     */
    @JvmStatic
    fun toAndroidColor(hex: String): Int {

        return try {

            hex.toLong(radix = 16).toInt()
        } catch (exception: Exception) {

            SetLog.setError(message = exception.message, throwable = exception)

            Color.Unspecified.toArgb()
        }
    }
}