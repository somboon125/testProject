package th.co.cdg.iconsume.database

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import th.co.cdg.iconsume.model.ProductOutput
import th.co.cdg.iconsume.model.SearchData

@SuppressLint("CheckResult")
class DatabaseManager(context: Context) {
    private val database = Room.databaseBuilder(context, AppDatabase::class.java, "iConsume.db").build()

    companion object {
        @Volatile
        private var INSTANCE: DatabaseManager? = null

        fun getInstance(context: Context): DatabaseManager {
            return INSTANCE ?: DatabaseManager(context)
        }
    }

    fun getHistories(): Single<List<SearchData>> {
        return database.historyDao().getHistories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { database.close() }
    }

    fun insertHistories(vararg data: SearchData): Completable {
        return database.historyDao().insert(*data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { database.close() }
    }

    fun deleteHistories(vararg data: SearchData): Completable {
        return database.historyDao().delete(*data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { database.close() }
    }


}