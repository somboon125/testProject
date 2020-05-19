package th.co.cdg.iconsume.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import th.co.cdg.iconsume.model.ProductOutput

object MapTypeConverters {
    @TypeConverter
    @JvmStatic
    fun fromString(value: String): ProductOutput? {
        if (value.isEmpty()) return null
        return Gson().fromJson(value, ProductOutput::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun toString(data: ProductOutput?): String {
        if (data == null) return ""
        return Gson().toJson(data)
    }
}