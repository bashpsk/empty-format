package io.bashpsk.emptyformat

import android.content.Context
import android.text.format.Formatter
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import io.bashpsk.emptyformat.EmptyFormat.time
import io.bashpsk.emptyformat.EmptyFormat.toRoundedDecimal
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toDuration

/**
 * `EmptyFormat` is a utility object that provides a comprehensive suite of functions for
 * formatting, parsing, and converting various data types. It focuses on date/time manipulation,
 * numerical formatting, file size representation, color conversions, and resolution calculations.
 *
 * This object aims to simplify common formatting tasks by offering a consistent and easy-to-use
 * API. It leverages the `kotlinx-datetime` library for robust date and time operations and
 * provides custom solutions for other formatting needs.
 *
 * Key features include:
 * - **Date and Time Formatting:**
 *     - Format `LocalDateTime` and epoch milliseconds into various string representations using
 *       predefined patterns.
 *     - Parse formatted date/time strings back into epoch milliseconds.
 *     - Convert time values (hours, minutes, seconds) into milliseconds and vice-versa.
 *     - Retrieve start and end of day timestamps.
 * - **Numerical Formatting:**
 *     - Round `Double` and `Float` values to a specified number of decimal places.
 *     - Format large numbers into human-readable strings with scaling suffixes (K, M, B, etc.).
 *     - Calculate percentages.
 * - **File Size Formatting:**
 *     - Convert byte counts into human-readable file size strings (e.g., "1.5 MB").
 * - **Color Formatting:**
 *     - Convert `Color` objects to hexadecimal string representations.
 *     - Convert hexadecimal color strings to `Color` objects and Android color integers.
 * - **Resolution Formatting:**
 *     - Generate human-readable labels for common screen resolutions (e.g., "1080p HD", "4K UHD").
 *     - Calculate simplified aspect ratios (e.g., "16:9").
 *
 * The `EmptyFormat` object is designed to be a versatile tool for developers needing to handle
 * diverse formatting requirements within their applications.
 */
@OptIn(ExperimentalTime::class)
object EmptyFormat {

    private const val LOG_TAG = "EmptyFormat"

    /**
     * Enum class defining the various formatting patterns supported by `EmptyFormat`.
     */
    enum class DateTimePattern {

        /**
         * Time in 12-hour format.
         * Example: 07:30.
         */
        TIME_HH_MM,

        /**
         * Time in 24-hour format.
         * Example: 19:30.
         */
        TIME_HH_MM_24,

        /**
         * Time in 12-hour format, including seconds.
         * Example: 07:30:33.
         */
        TIME_HH_MM_SS,

        /**
         * Time in 24-hour format, including seconds.
         * Example: 19:30:33.
         */
        TIME_HH_MM_SS_24,

        /**
         * Time in 12-hour format with AM/PM indicator.
         * Example: 07:30:33 PM.
         */
        TIME_12,

        /**
         * Time in 24-hour format.
         * Example: 19:30:33.
         */
        TIME_24,

        /**
         * Short date format.
         * Example: 09:12:2000.
         */
        SHORT_DATE,

        /**
         * Human-readable full date format.
         * Example: Dec 09, 2000.
         */
        LONG_DATE,

        /**
         * Short date and time in 12-hour format.
         * Example: 09:12:2000 07:30 PM.
         */
        SHORT_DATE_TIME,

        /**
         * Short date and time in 24-hour format.
         * Example: 09:12:2000 19:30.
         */
        SHORT_DATE_TIME_24,

        /**
         * Full date and time in 12-hour format.
         * Example: Sun, Dec 09, 2000 07:30 PM.
         */
        LONG_DATE_TIME,

        /**
         * Full date and time in 24-hour format.
         * Example: Sun, Dec 09, 2000 19:30.
         */
        LONG_DATE_TIME_24,

        /**
         * Extended full date-time format with milliseconds.
         * Example: Sun, Dec 09, 2000 07:30:33.333 PM.
         */
        LONG_DATE_TIME_MILLIS,

        /**
         * Extended full date-time format with milliseconds in 24-hour format.
         * Example: Sun, Dec 09, 2000 19:30:33.333.
         */
        LONG_DATE_TIME_MILLIS_24,

        /**
         * Date and time format suitable for file names, in 24-hour format.
         * Example: 19-30-33 09-12-2000.
         */
        FILE_NAME,

        /**
         * Day of the week only.
         * Example: Mon.
         */
        DAY_ONLY,

        /**
         * Month of the year only.
         * Example: Dec.
         */
        MONTH_ONLY,

        /**
         * Year only.
         * Example: 2000.
         */
        YEAR_ONLY,

        /**
         * Month and Year only.
         * Example: Dec 09.
         */
        MONTH_DAY,

        /**
         * Short year and month format.
         * Example: 12/00.
         */
        SHORT_MONTH_YEAR,

        /**
         * Month and Year only.
         * Example: Dec 2000.
         */
        MONTH_YEAR,

        /**
         * Day-of-year format.
         * Example: 343 (343rd day of the year).
         */
        DAY_OF_YEAR,

        /**
         * Day-of-month format.
         * Example: 09 (09th day of the month).
         */
        DAY_OF_MONTH,

        /**
         * Month of the year format.
         * Example: 12 (12th month of the year).
         */
        MONTH_OF_YEAR,

        /**
         * Compact timestamp format.
         * Example: 20001209193033 (YYYYMMDDHHMMSS).
         */
        TIMESTAMP_COMPACT,
    }

    /**
     * Enum class defining patterns for formatting durations.
     *
     * This enum provides various predefined formats for displaying time durations.
     * Each pattern specifies how hours, minutes, seconds, and milliseconds
     * should be represented in the output string.
     */
    enum class DurationPattern {

        /**
         * Duration format : Seconds.Milliseconds. Example: "05.333".
         *
         * Displays seconds (zero-padded to 2 digits) and milliseconds (zero-padded to 3 digits).
         */
        SS_MS,

        /**
         * Duration format : Minutes:Seconds. Example: "40:33".
         *
         * Displays minutes (zero-padded to 2 digits) and seconds (zero-padded to 2 digits).
         */
        MM_SS,

        /**
         * Duration format : Minutes:Seconds.Milliseconds. Example: "40:30.333".
         *
         * Displays minutes (zero-padded to 2 digits), seconds (zero-padded to 2 digits),
         * and milliseconds (zero-padded to 3 digits).
         */
        MM_SS_MS,

        /**
         * Duration format : Hours:Minutes:Seconds. Example: "03:40:33".
         *
         * Displays hours (zero-padded to 2 digits), minutes (zero-padded to 2 digits),
         * and seconds (zero-padded to 2 digits).
         */
        HH_MM_SS,

        /**
         * Duration format : Hours:Minutes:Seconds.Milliseconds. Example: "03:40:43.333".
         *
         * Displays hours (zero-padded to 2 digits), minutes (zero-padded to 2 digits),
         * seconds (zero-padded to 2 digits), and milliseconds (zero-padded to 3 digits).
         */
        HH_MM_SS_MS,

        /**
         * Automatically determines the format based on the duration value:
         *
         * - If the duration is exactly 0, returns "00".
         * - If the duration is less than 1 hour, returns in "MM:SS" format (Minutes:Seconds).
         * - Otherwise (if the duration is 1 hour or more), returns in "HH:MM:SS" format
         * (Hours:Minutes:Seconds).
         */
        AUTO;
    }

    private val dayOfWeekNames = DayOfWeekNames(
        monday = "Mon",
        tuesday = "Tue",
        wednesday = "Wed",
        thursday = "Thu",
        friday = "Fri",
        saturday = "Sat",
        sunday = "Sun"
    )

    private val monthNames = MonthNames(
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

    /**
     * Formats a date and time represented by milliseconds since the epoch into a `String` based on
     * the specified pattern.
     *
     * @param millis The date and time in milliseconds since the epoch.
     * @param pattern The formatting pattern to apply.
     * @return The formatted date and time `String`.
     */
    @JvmStatic
    fun dateTime(millis: Long, pattern: DateTimePattern): String {

        val instant = Instant.fromEpochMilliseconds(epochMilliseconds = millis)
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
    fun dateTime(localDateTime: LocalDateTime, pattern: DateTimePattern): String {

        return localDateTime.format(format = findDateTimeFormat(pattern = pattern))
    }

    /**
     * Parses a formatted date and time `String` into milliseconds since the epoch based on the
     * specified pattern.
     *
     * @param dateTime The formatted date and time `String`.
     * @param pattern
     * @return The date and time in milliseconds since the epoch.
     */
    @JvmStatic
    fun dateTimeToMilliseconds(dateTime: String, pattern: DateTimePattern): Long? {

        return try {

            LocalDateTime.parse(
                input = dateTime,
                format = findDateTimeFormat(pattern = pattern)
            ).toInstant(timeZone = TimeZone.currentSystemDefault()).toEpochMilliseconds()
        } catch (exception: Exception) {

            Log.e(LOG_TAG, exception.message, exception)
            null
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
    fun time(time: Long, pattern: DateTimePattern): String {

        val duration = time.toDuration(unit = DurationUnit.MILLISECONDS)
        val hours = duration.inWholeHours
        val minutes = duration.inWholeMinutes % 60
        val seconds = duration.inWholeSeconds % 60
        val nanoseconds = duration.inWholeNanoseconds % 1_000_000_000

        val localTime = LocalTime(
            hour = hours.toInt().coerceIn(0, 23),
            minute = minutes.toInt().coerceIn(0, 59),
            second = seconds.toInt().coerceIn(0, 59),
            nanosecond = nanoseconds.toInt().coerceIn(0, 999_999_999)
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
    fun time(localTime: LocalTime, pattern: DateTimePattern): String {

        return localTime.format(format = findTimeFormat(pattern = pattern))
    }

    /**
     * Retrieves the appropriate `DateTimeFormat` for a given time pattern.
     *
     * This function maps various `Pattern` enums to their respective
     * `LocalTime.Format` definitions using kotlinx-datetime formatting.
     *
     * @param pattern The `Pattern` enum specifying the desired time format.
     * @return The corresponding `DateTimeFormat<LocalTime>`.
     *
     * Supported patterns:
     * - `Pattern.TIME_HH_MM` → Time in 12-hour format. Example: 07:30.
     * - `Pattern.TIME_HH_MM_24` → Time in 24-hour format. Example: 19:30.
     * - `Pattern.TIME_HH_MM_SS` → Time in 12-hour format, including seconds. Example: 07:30:33.
     * - `Pattern.TIME_HH_MM_SS_24` → Time in 24-hour format, including seconds. Example: 19:30:33.
     * - `Pattern.TIME_12` → Time in 12-hour format with AM/PM indicator. Example: 07:30:33 PM.
     * - `Pattern.TIME_24` → Time in 24-hour format. Example: 19:30:33.
     * - `else` → `Pattern.TIME_12`.
     */
    @JvmStatic
    fun findTimeFormat(pattern: DateTimePattern): DateTimeFormat<LocalTime> {

        return when (pattern) {

            DateTimePattern.TIME_HH_MM -> LocalTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_24 -> LocalTime.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS -> LocalTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS_24 -> LocalTime.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_12 -> LocalTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
                char(' ')
                amPmMarker(am = "AM", pm = "PM")
            }

            DateTimePattern.TIME_24 -> LocalTime.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            else -> LocalTime.Format {

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

    /**
     * Retrieves the appropriate `DateTimeFormat` for a given pattern.
     *
     * This function maps various `Pattern` enums to their respective
     * `LocalDateTime.Format` definitions using kotlinx-datetime formatting.
     * @param pattern The `Pattern` enum specifying the desired date-time format.
     * @return The corresponding `DateTimeFormat<LocalDateTime>`.
     */
    @JvmStatic
    fun findDateTimeFormat(pattern: DateTimePattern): DateTimeFormat<LocalDateTime> {

        return when (pattern) {

            DateTimePattern.TIME_HH_MM -> LocalDateTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_24 -> LocalDateTime.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS -> LocalDateTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS_24 -> LocalDateTime.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_12 -> LocalDateTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
                char(' ')
                amPmMarker(am = "AM", pm = "PM")
            }

            DateTimePattern.TIME_24 -> LocalDateTime.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.SHORT_DATE -> LocalDateTime.Format {

                dayOfMonth(padding = Padding.ZERO)
                char(value = ':')
                monthNumber(padding = Padding.ZERO)
                char(value = ':')
                year(padding = Padding.ZERO)
            }

            DateTimePattern.LONG_DATE -> LocalDateTime.Format {

                monthName(names = monthNames)
                char(value = ' ')
                dayOfMonth(padding = Padding.ZERO)
                char(value = ',')
                char(value = ' ')
                year(padding = Padding.ZERO)
            }

            DateTimePattern.SHORT_DATE_TIME -> LocalDateTime.Format {

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

            DateTimePattern.SHORT_DATE_TIME_24 -> LocalDateTime.Format {

                dayOfMonth(padding = Padding.ZERO)
                char(value = ':')
                monthNumber(padding = Padding.ZERO)
                char(value = ':')
                year(padding = Padding.ZERO)
                char(value = ' ')
                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.LONG_DATE_TIME -> LocalDateTime.Format {

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
                char(value = ' ')
                amPmMarker(am = "AM", pm = "PM")
            }

            DateTimePattern.LONG_DATE_TIME_24 -> LocalDateTime.Format {

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
                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.LONG_DATE_TIME_MILLIS -> LocalDateTime.Format {

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
                char(value = '.')
                secondFraction(fixedLength = 3)
                char(value = ' ')
                amPmMarker(am = "AM", pm = "PM")
            }

            DateTimePattern.LONG_DATE_TIME_MILLIS_24 -> LocalDateTime.Format {

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
                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
                char(value = '.')
                secondFraction(fixedLength = 3)
            }

            DateTimePattern.FILE_NAME -> LocalDateTime.Format {

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

            DateTimePattern.DAY_ONLY -> LocalDateTime.Format {

                dayOfWeek(names = dayOfWeekNames)
            }

            DateTimePattern.MONTH_ONLY -> LocalDateTime.Format {

                monthName(names = monthNames)
            }

            DateTimePattern.YEAR_ONLY -> LocalDateTime.Format {

                year(padding = Padding.ZERO)
            }

            DateTimePattern.MONTH_DAY -> LocalDateTime.Format {

                monthName(names = monthNames)
                char(value = ' ')
                dayOfMonth(padding = Padding.ZERO)
            }

            DateTimePattern.SHORT_MONTH_YEAR -> LocalDateTime.Format {

                monthName(names = monthNames)
                char(value = ' ')
                yearTwoDigits(baseYear = 1960)
            }

            DateTimePattern.MONTH_YEAR -> LocalDateTime.Format {

                monthName(names = monthNames)
                char(value = ' ')
                year(padding = Padding.ZERO)
            }

            DateTimePattern.DAY_OF_YEAR -> LocalDateTime.Format {

                dayOfYear(padding = Padding.ZERO)
            }

            DateTimePattern.DAY_OF_MONTH -> LocalDateTime.Format {

                dayOfMonth(padding = Padding.ZERO)
            }

            DateTimePattern.MONTH_OF_YEAR -> LocalDateTime.Format {

                monthNumber(padding = Padding.ZERO)
            }

            DateTimePattern.TIMESTAMP_COMPACT -> LocalDateTime.Format {

                year(padding = Padding.ZERO)
                monthNumber(padding = Padding.ZERO)
                dayOfMonth(padding = Padding.ZERO)
                hour(padding = Padding.ZERO)
                minute(padding = Padding.ZERO)
                second(padding = Padding.ZERO)
            }
        }
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
    fun timeToMilliseconds(hours: Int, minutes: Int, seconds: Int): Long? {

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

            Log.e(LOG_TAG, exception.message, exception)
            null
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
     */
    @JvmStatic
    fun toRoundTime(time: Int): String {

        return try {

            String.format(locale = Locale.getDefault(), format = "%02d", time)
        } catch (exception: Exception) {

            Log.e(LOG_TAG, exception.message, exception)
            "00"
        }
    }

    /**
     * Formats a duration value into a string representation based on the specified pattern.
     *
     * This function takes a `durationValue` (a [Long] representing the duration magnitude),
     * its `unit` (a [DurationUnit]), and a `pattern` (a [DurationPattern]) to determine
     * the output string format.
     *
     * It first converts the input `durationValue` and `unit` into a `kotlin.time.Duration` object.
     * Then, it extracts the whole hours, minutes (modulo 60), seconds (modulo 60), and
     * milliseconds (modulo 1000) from this duration.
     *
     * Based on the provided `pattern`, it formats these components into a string:
     * - [DurationPattern.SS_MS]: Formats as "SS.mmm" (e.g., "05.333").
     * - [DurationPattern.MM_SS]: Formats as "MM:SS" (e.g., "40:33").
     * - [DurationPattern.MM_SS_MS]: Formats as "MM:SS.mmm" (e.g., "40:30.333").
     * - [DurationPattern.HH_MM_SS]: Formats as "HH:MM:SS" (e.g., "03:40:33").
     * - [DurationPattern.HH_MM_SS_MS]: Formats as "HH:MM:SS.mmm" (e.g., "03:40:43.333").
     * - [DurationPattern.AUTO]: Automatically selects the format:
     *     - If `durationValue` is 0, returns "00".
     *     - If `durationValue` is less than 1 hour (3,600,000 milliseconds), formats as "MM:SS".
     *     - Otherwise, formats as "HH:MM:SS".
     *
     * All numerical components in the formatted string are zero-padded to ensure consistent length
     * (e.g., seconds are "05", not "5").
     *
     * @param durationValue The magnitude of the duration.
     * @param unit The unit of the `durationValue` (e.g., [DurationUnit.MILLISECONDS],
     * [DurationUnit.SECONDS]).
     * @param pattern The [DurationPattern] to use for formatting.
     * @return A string representation of the formatted duration.
     */
    @JvmStatic
    fun duration(durationValue: Long, unit: DurationUnit, pattern: DurationPattern): String {

        val duration = durationValue.toDuration(unit)
        val hours = duration.inWholeHours
        val minutes = duration.inWholeMinutes % 60
        val seconds = duration.inWholeSeconds % 60
        val millis = duration.inWholeMilliseconds % 1000

        return when (pattern) {

            DurationPattern.SS_MS -> "%02d.%03d".format(seconds, millis)
            DurationPattern.MM_SS -> "%02d:%02d".format(minutes, seconds)
            DurationPattern.MM_SS_MS -> "%02d:%02d.%03d".format(minutes, seconds, millis)
            DurationPattern.HH_MM_SS -> "%02d:%02d:%02d".format(hours, minutes, seconds)
            DurationPattern.HH_MM_SS_MS -> "%02d:%02d:%02d.%03d".format(
                hours,
                minutes,
                seconds,
                millis
            )

            DurationPattern.AUTO -> when {

                durationValue == 0L -> "00"
                duration.inWholeMilliseconds < 3_600_000L -> "%02d:%02d".format(minutes, seconds)
                else -> "%02d:%02d:%02d".format(hours, minutes, seconds)
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
     * will log the error using `Log.e()` and return 0.0.
     *
     * @param decimal The `Double` value to be rounded.
     * @param fraction The number of decimal places to round to. Defaults to 0.0.
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

            Log.e(LOG_TAG, exception.message, exception)
            0.0
        }
    }

    /**
     * Rounds a `Float` to a specified number of decimal places.
     *
     * Similar to `toRoundedDecimal(Double, Int)`, this function rounds a `Float`
     * value to the nearest decimal place specified by the `fraction` parameter.
     * It also uses the default locale for formatting. If an error occurs during
     * rounding, it will log the error using `Log.e()` and return 0F.
     *
     * @param decimal The `Float` value to be rounded.
     * @param fraction The number of decimal places to round to. Defaults to 0F.
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

            Log.e(LOG_TAG, exception.message, exception)
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

        return Formatter.formatFileSize(context, size).uppercase()
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

        val units = persistentListOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")

        var length = size.toDouble()
        var order = 0

        while (length >= 1024 && order < units.size - 1) {

            order++
            length /= 1024
        }

        return String.format(
            locale = Locale.getDefault(),
            format = "%.2f %s",
            length,
            units.getOrElse(index = order) { 0 }
        ).uppercase()
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
    fun shortenedNumericalNotation(value: Long): String {

        return when {

            value >= 1_000_000_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fQ",
                value / 1_000_000_000_000_000.0
            )

            value >= 1_000_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fT",
                value / 1_000_000_000_000.0
            )

            value >= 1_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fB",
                value / 1_000_000_000.0
            )

            value >= 1_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fM",
                value / 1_000_000.0
            )

            value >= 1_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fK",
                value / 1_000.0
            )

            else -> value.toString()
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
    fun shortenedNumericalNotation(value: Double): String {

        return when {

            value >= 1_000_000_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fQ",
                value / 1_000_000_000_000_000.0
            )

            value >= 1_000_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fT",
                value / 1_000_000_000_000.0
            )

            value >= 1_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fB",
                value / 1_000_000_000.0
            )

            value >= 1_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fM",
                value / 1_000_000.0
            )

            value >= 1_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fK",
                value / 1_000.0
            )

            else -> String.format(
                locale = Locale.getDefault(),
                format = "%.1f",
                value
            )
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
    fun shortenedNumericalNotation(value: Int): String {

        return shortenedNumericalNotation(value = value.toLong())
    }

    /**
     * Converts a [Color] object to its hexadecimal string representation, including alpha.
     *
     * The resulting string will be in the format "AARRGGBB", where AA is the alpha component,
     * RR is red, GG is green, and BB is blue, all in hexadecimal.
     *
     * @param color The [Color] object to convert.
     * @return A [String] representing the color in "AARRGGBB" hexadecimal format.
     * For example, `Color.Red` would be returned as "FFFF0000".
     */
    @JvmStatic
    fun toColorHex(color: Color): String {

        return String.format(locale = Locale.getDefault(), format = "%08X", color.toArgb())
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

        val cleanHex = hex.removePrefix("#")

        return try {

            val colorLong = when (cleanHex.length) {

                8 -> cleanHex.toULong(radix = 16).toLong()
                6 -> ("FF$cleanHex").toULong(radix = 16).toLong()
                else -> throw IllegalArgumentException("Invalid hex color string length : $hex")
            }

            Color(color = colorLong)
        } catch (exception: Exception) {

            Log.e(LOG_TAG, "Failed to parse hex to Color : $hex", exception)
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

        val cleanHex = hex.removePrefix("#")

        return try {

            val colorLong = when (cleanHex.length) {

                8 -> cleanHex.toULong(radix = 16).toLong()
                6 -> ("FF$cleanHex").toULong(radix = 16).toLong()
                else -> throw IllegalArgumentException("Invalid hex color string length : $hex")
            }

            colorLong.toInt()
        } catch (exception: Exception) {

            Log.e(LOG_TAG, "Failed to parse hex to Android Color Int : $hex", exception)
            Color.Unspecified.toArgb()
        }
    }

    /**
     * Calculates the percentage of obtained value relative to the total.
     * @param total The total possible value.
     * @param obtained The obtained value.
     * @return The percentage value as an integer, or 0 if total is zero.
     */
    @JvmStatic
    fun findPercentage(total: Long, obtained: Long): Int {

        return when (total) {

            0L -> 0
            else -> ((obtained.toDouble() / total) * 100).toInt()
        }
    }

    /**
     * Calculates the percentage of obtained value relative to the total.
     * @param total The total possible value.
     * @param obtained The obtained value.
     * @return The percentage value as an integer, or 0 if total is zero.
     */
    @JvmStatic
    fun findPercentage(total: Int, obtained: Int): Int {

        return when (total) {

            0 -> 0
            else -> ((obtained.toDouble() / total) * 100).toInt()
        }
    }

    /**
     * Calculates the percentage of obtained value relative to the total.
     * @param total The total possible value.
     * @param obtained The obtained value.
     * @return The percentage value rounded to one decimal place, or 0.0 if total is zero.
     * @see toRoundedDecimal for rounding the result.
     */
    @JvmStatic
    fun findPercentage(total: Double, obtained: Double): Double {

        return when (total) {

            0.0 -> 0.0
            else -> toRoundedDecimal(decimal = (obtained / total) * 100, fraction = 1)
        }
    }

    /**
     * Calculates the percentage of obtained value relative to the total.
     * @param total The total possible value.
     * @param obtained The obtained value.
     * @return The percentage value rounded to one decimal place, or 0.0F if total is zero.
     * @see toRoundedDecimal for rounding the result.
     */
    @JvmStatic
    fun findPercentage(total: Float, obtained: Float): Float {

        return when (total) {

            0.0F -> 0.0F
            else -> toRoundedDecimal(decimal = (obtained / total) * 100, fraction = 1)
        }
    }

    /**
     * Returns a human-readable resolution label (e.g., "1080p", "4K UHD", "16K") based on screen
     * width and height.
     *
     * This function identifies common video and display resolutions including SD, HD, Full HD,
     * Ultra HD,
     * cinema-grade resolutions, and even ultra-high formats like 12K and 16K. If the resolution
     * doesn't match
     * a known label, it falls back to raw pixel dimensions (e.g., "1234x567").
     *
     * @param width  The width of the resolution in pixels.
     * @param height The height of the resolution in pixels.
     * @return A string describing the resolution in standard format.
     *
     * Example:
     * ```
     * formatResolutionLabel(1920, 1080) // returns "1080p HD"
     * formatResolutionLabel(15360, 8640) // returns "16K UHD"
     * ```
     */
    @JvmStatic
    fun findResolutionLabel(width: Int, height: Int): String {

        val resolution = maxOf(width, height)

        return when {

            resolution >= 15360 -> "16K UHD"
            resolution >= 11520 -> "12K UHD"
            resolution >= 8192 -> "8K UHD"
            resolution >= 7680 -> "8K"
            resolution >= 5120 -> "5K"
            resolution >= 4096 -> "4K DCI"
            resolution >= 3840 -> "4K UHD"
            resolution >= 3200 -> "3K"
            resolution >= 2880 -> "WQHD+"
            resolution >= 2560 -> "2.5K"
            resolution >= 2048 -> "2K DCI"
            resolution >= 1920 -> "1080p HD"
            resolution >= 1600 -> "UXGA"
            resolution >= 1440 -> "HD+"
            resolution >= 1366 -> "HD"
            resolution >= 1280 -> "720p"
            resolution >= 1024 -> "XGA"
            resolution >= 960 -> "FWVGA"
            resolution >= 854 -> "480p"
            resolution >= 640 -> "360p"
            resolution >= 426 -> "240p"
            resolution >= 256 -> "144p"
            else -> "${width}x$height"
        }
    }

    /**
     * Calculates the simplified aspect ratio of a given width and height.
     *
     * This function uses the greatest common divisor (GCD) to reduce the given dimensions
     * to their simplest integer ratio form, commonly used in video and display resolutions.
     *
     * @param width The horizontal resolution in pixels.
     * @param height The vertical resolution in pixels.
     * @return A string representing the simplified aspect ratio (e.g., "16:9").
     *
     * Example:
     * ```
     * findAspectRatio(1920, 1080) // returns "16:9"
     * findAspectRatio(1080, 1920) // returns "9:16"
     * ```
     */
    @JvmStatic
    fun findAspectRatio(width: Int, height: Int): String {

        fun gcd(a: Int, b: Int): Int {

            return if (b == 0) a else gcd(b, a % b)
        }

        val divisor = gcd(width, height)
        val ratioWidth = width / divisor
        val ratioHeight = height / divisor

        return "$ratioWidth:$ratioHeight"
    }
}