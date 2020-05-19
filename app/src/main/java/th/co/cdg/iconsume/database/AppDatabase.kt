package th.co.cdg.iconsume.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import th.co.cdg.iconsume.model.SearchData

@Database(
    entities = [
        SearchData::class
    ], version = 1, exportSchema = false
)
@TypeConverters(MapTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}