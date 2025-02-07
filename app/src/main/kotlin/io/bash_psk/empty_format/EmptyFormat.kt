package io.bash_psk.empty_format

import android.content.Context
import android.text.format.Formatter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@OptIn(ExperimentalTime::class)
object EmptyFormat {

    enum class Pattern {

        /**
         * Time in 24 hour format.
         * Ex. 07:30.
         * */
        TIME_HH_MM,

        /**
         * Time in 24 hour format.
         * Ex. 07:30:45.
         * */
        TIME_HH_MM_SS,

        /**
         * Time in 12 hour format. Indicate AM/PM.
         * Ex. 07:30:45 PM.
         * */
        TIME_12,

        /**
         * Simple Date.
         * Ex. 09:12:2000.
         * */
        SHORT_DATE,

        /**
         * Simple Date; Time in 12 hour format.
         * Ex. 09:12:2000 07:30 PM.
         * */
        SHORT_DATE_TIME,

        /**
         * Long Date; Time in 12 hour format.
         * Ex. Sunday, December 09, 2000 07:30 PM.
         * */
        FULL_DATE_TIME,

        /**
         * Long Date; Time in 24 hour format.
         * Compatible for file name.
         * Ex. 19-30-45 09-12-2000.
         * */
        FILE_NAME
    }

    fun dateTime(dateTimeMillis: Long, pattern: Pattern): String {

        val instant = Instant.fromEpochMilliseconds(epochMilliseconds = dateTimeMillis)
        val localDateTime = instant.toLocalDateTime(timeZone = TimeZone.currentSystemDefault())

        return dateTime(localDateTime = localDateTime, pattern = pattern)
    }

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

    fun toRoundTime(time: Int): String {

        return try {

            String.format(locale = Locale.getDefault(), format = "%02d", time)
        } catch (exception: Exception) {

            SetLog.setError(message = exception.message, throwable = exception)

            "00"
        }
    }

    fun toRoundTime(time: Long): String {

        return try {

            String.format(locale = Locale.getDefault(), format = "%02d", time)
        } catch (exception: Exception) {

            SetLog.setError(message = exception.message, throwable = exception)

            "00"
        }
    }

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

    fun toFileSize(context: Context, size: Long): String {

        return Formatter.formatFileSize(context, size)
    }

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
}