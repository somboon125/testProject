package th.co.cdg.iconsume.database

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single
import th.co.cdg.iconsume.model.SearchData

@Dao
interface HistoryDao {
    @Query("SELECT * FROM History ORDER BY id DESC")
    fun getHistories(): Single<List<SearchData>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg searchData: SearchData): Completable

    @Delete
    fun delete(vararg searchData: SearchData): Completable
}