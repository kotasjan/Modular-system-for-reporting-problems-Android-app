package cz.jankotas.bakalarka.databases.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.*

/**
 * Třída je nutná k převodu formátu String na typ Date a obráceně. To je nutné kvůli ukládání dat do databáze
 */
class DateTypeConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun dateFromString(value: String?): Date? {
            return if (value == null) null else Gson().fromJson<Date>(value, Date::class.java)
        }

        @TypeConverter
        @JvmStatic
        fun userToString(date: Date?): String? {
            return Gson().toJson(date)
        }
    }
}